/**
 * 
 */
/**
 * @author rajnishpandey
 *
 */
package org.service.bosh.aws;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.service.data.ServiceEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.Address;
import com.amazonaws.services.ec2.model.AllocateAddressRequest;
import com.amazonaws.services.ec2.model.AssociateAddressRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancingClient;
import com.amazonaws.services.elasticloadbalancing.model.CreateLoadBalancerRequest;
import com.amazonaws.services.elasticloadbalancing.model.CreateLoadBalancerResult;
import com.amazonaws.services.elasticloadbalancing.model.DeleteLoadBalancerRequest;
import com.amazonaws.services.elasticloadbalancing.model.DescribeLoadBalancersResult;
import com.amazonaws.services.elasticloadbalancing.model.Listener;
import com.amazonaws.services.elasticloadbalancing.model.LoadBalancerDescription;
import com.amazonaws.services.elasticloadbalancing.model.RegisterInstancesWithLoadBalancerRequest;
import com.amazonaws.services.elasticloadbalancing.model.RegisterInstancesWithLoadBalancerResult;
import com.amazonaws.services.route53.AmazonRoute53;
import com.amazonaws.services.route53.AmazonRoute53Client;
import com.amazonaws.services.route53.model.AliasTarget;
import com.amazonaws.services.route53.model.Change;
import com.amazonaws.services.route53.model.ChangeAction;
import com.amazonaws.services.route53.model.ChangeBatch;
import com.amazonaws.services.route53.model.ChangeResourceRecordSetsRequest;
import com.amazonaws.services.route53.model.ChangeResourceRecordSetsResult;
import com.amazonaws.services.route53.model.HostedZone;
import com.amazonaws.services.route53.model.ListHostedZonesResult;
import com.amazonaws.services.route53.model.RRType;
import com.amazonaws.services.route53.model.ResourceRecord;
import com.amazonaws.services.route53.model.ResourceRecordSet;

public final class AwsUtil {

	/**
	 * Holds LOGGER reference.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(AwsUtil.class);

	public static void mapPublicIp(ServiceEnvironment environment, String staticip) {
		AWSCredentials credentials = new BasicAWSCredentials(environment.getAwsAccessKey(),
				environment.getAwsSecretKey());
		Region se = Region.getRegion(Regions.AP_SOUTHEAST_1);
		AmazonEC2Client ec2Client = new AmazonEC2Client(credentials);

		ec2Client.setRegion(se);
		DescribeInstancesResult describeInstancesRequest = ec2Client.describeInstances();
		List<Reservation> reservations = describeInstancesRequest.getReservations();
		List<Instance> instances = new ArrayList<Instance>();
		for (Reservation reservation : reservations) {

			for (Instance instance : reservation.getInstances()) {
				if (null != instance.getPrivateIpAddress() && instance.getPrivateIpAddress().equals(staticip)) {
					LOGGER.debug("mapELB  found instances -" + staticip);
					instances.add(instance);
					break;
				}
			}
		}

		List<Address> lstAddress = ec2Client.describeAddresses().getAddresses();

		System.out.println("lstAddress... " + lstAddress);
		for (Address address : lstAddress) {
			if (null == address.getInstanceId() || address.getInstanceId().isEmpty()) {
				LOGGER.debug("mapELB  found public IP  -" + staticip);
				if (instances.size() > 0) {
					address.setInstanceId(instances.get(0).getInstanceId());
					AllocateAddressRequest aar = new AllocateAddressRequest();
					LOGGER.debug("mapELB  mapping  public IP to instances  -" + instances.get(0).getInstanceId() + " - "
							+ address.getPublicIp());
					ec2Client.associateAddress(
							new AssociateAddressRequest(instances.get(0).getInstanceId(), address.getPublicIp()));

					break;
				}
			}
		}

	}

	public static boolean mapELB(ServiceEnvironment environment, String staticip, String serviceId) {
		LOGGER.debug("mapELB Starting -" + staticip);
		String loadBalancer = serviceId.split("-")[0];
		String subdomain = serviceId.replaceAll("-", "");
		String aliasHostedZoneId = null;
		String aliasDNSName = null;
		String hostedZoneId = null;
		LOGGER.debug("serviceId : " + serviceId);
		LOGGER.debug("loadBalancer : " + loadBalancer);
		LOGGER.debug("subdomain : " + subdomain);
		AWSCredentials credentials = new BasicAWSCredentials(environment.getAwsAccessKey(),
				environment.getAwsSecretKey());
		AmazonElasticLoadBalancingClient elb = new AmazonElasticLoadBalancingClient(credentials);
		Region se = Region.getRegion(Regions.AP_SOUTHEAST_1);
		elb.setRegion(se);

		AmazonEC2Client ec2Client = new AmazonEC2Client(credentials);
		ec2Client.setRegion(se);
		AmazonRoute53 route53 = new AmazonRoute53Client(credentials);
		AmazonRoute53Client routeclient53 = new AmazonRoute53Client(credentials);
		routeclient53.setRegion(se);
		route53.setRegion(se);

		/** To get the hosted zone ID **/
		ListHostedZonesResult listHostedZonesResult = route53.listHostedZones();
		List<HostedZone> lsthostedZone = listHostedZonesResult.getHostedZones();
		for (HostedZone hostedZone : lsthostedZone) {
			if (hostedZone.getName().contains(environment.getHostName())) {
				LOGGER.debug("hostedZone : " + hostedZone.getName());
				hostedZoneId = hostedZone.getId();
				break;
			}
		}

		/** To Create load balancer **/
		CreateLoadBalancerRequest lbRequest = new CreateLoadBalancerRequest();
		lbRequest.setLoadBalancerName(loadBalancer);
		List<Listener> listeners = new ArrayList<Listener>(1);
		listeners.add(new Listener("TCP", 25, 22));
		// lbRequest.withAvailabilityZones(environment.getAvailabilityZone());
		lbRequest.withSubnets(environment.getSubnet());
		lbRequest.withSecurityGroups(environment.getSecuritygroup());
		lbRequest.setListeners(listeners);
		CreateLoadBalancerResult lbResult = elb.createLoadBalancer(lbRequest);

		/** To map the public IP address to this instances **/
		DescribeInstancesResult describeInstancesRequest = ec2Client.describeInstances();
		List<Reservation> reservations = describeInstancesRequest.getReservations();
		List<Instance> instances = new ArrayList<Instance>();
		for (Reservation reservation : reservations) {

			for (Instance instance : reservation.getInstances()) {
				if (null != instance.getPrivateIpAddress() && instance.getPrivateIpAddress().equals(staticip)) {
					LOGGER.debug("mapELB  found instances -" + staticip);
					instances.add(instance);
					break;
				}
			}
		}

		List<Address> lstAddress = ec2Client.describeAddresses().getAddresses();
		for (Address address : lstAddress) {
			if (null == address.getInstanceId() || address.getInstanceId().isEmpty()) {
				LOGGER.debug("mapELB  found public IP  -" + staticip);
				if (instances.size() > 0) {
					address.setInstanceId(instances.get(0).getInstanceId());
					AllocateAddressRequest aar = new AllocateAddressRequest();
					LOGGER.debug("mapELB  mapping  public IP to instances  -" + instances.get(0).getInstanceId() + " - "
							+ address.getPublicIp());
					ec2Client.associateAddress(
							new AssociateAddressRequest(instances.get(0).getInstanceId(), address.getPublicIp()));
					break;
				}
			}
		}

		/** Mapping the instances to elastic load balancer **/
		LOGGER.debug("mapELB starting  mapping  ELB  to instances  -");
		RegisterInstancesWithLoadBalancerRequest register = new RegisterInstancesWithLoadBalancerRequest();
		register.setLoadBalancerName(loadBalancer);
		String id;
		List instanceId = new ArrayList();
		List instanceIdString = new ArrayList();
		if (instances.size() > 0) {
			Iterator<Instance> iterator = instances.iterator();
			while (iterator.hasNext()) {
				id = iterator.next().getInstanceId();
				instanceId.add(new com.amazonaws.services.elasticloadbalancing.model.Instance(id));
				instanceIdString.add(id);
			}
			LOGGER.debug("mapELB  mapping  ELB  to instances  -");
			register.setInstances((Collection) instanceId);
			RegisterInstancesWithLoadBalancerResult registerWithLoadBalancerResult = elb
					.registerInstancesWithLoadBalancer(register);
		}

		DescribeLoadBalancersResult lbs = elb.describeLoadBalancers();
		List<LoadBalancerDescription> descriptions = lbs.getLoadBalancerDescriptions();
		System.out.println("descriptions : " + descriptions.size());
		for (LoadBalancerDescription loadBalancerDescription : descriptions) {
			if (loadBalancer.equals(loadBalancerDescription.getLoadBalancerName())) {
				LOGGER.debug("Name: " + loadBalancerDescription.getLoadBalancerName());
				LOGGER.debug("DNS Name: " + loadBalancerDescription.getDNSName());
				aliasHostedZoneId = loadBalancerDescription.getCanonicalHostedZoneNameID();
				aliasDNSName = loadBalancerDescription.getDNSName();
				break;
			}
		}

		LOGGER.debug("aliasHostedZoneId :" + aliasHostedZoneId);
		LOGGER.debug("aliasDNSName :" + aliasDNSName);

		/**
		 * To create host name(subdomain) and map the elastic load balancer to
		 * that host
		 **/
		if (hostedZoneId != null && !hostedZoneId.isEmpty()) {
			ChangeBatch changeBatch = new ChangeBatch();
			Collection<Change> changes = new ArrayList<Change>();
			Change change = new Change();
			change.setAction(ChangeAction.CREATE);
			changes.add(change);
			changeBatch.setChanges(changes);

			ResourceRecordSet resourceRecordSet = new ResourceRecordSet();
			AliasTarget alias = new AliasTarget();

			alias.setHostedZoneId(aliasHostedZoneId);

			alias.setDNSName(aliasDNSName);
			alias.setEvaluateTargetHealth(false);
			resourceRecordSet.setAliasTarget(alias);

			resourceRecordSet.setName(loadBalancer + "." + environment.getHostName());
			resourceRecordSet.setType(RRType.A);

			change.setResourceRecordSet(resourceRecordSet);

			ChangeResourceRecordSetsRequest changeResourceRecordSetsRequest = new ChangeResourceRecordSetsRequest();
			changeResourceRecordSetsRequest.setHostedZoneId(hostedZoneId);
			changeResourceRecordSetsRequest.setChangeBatch(changeBatch);
			ChangeResourceRecordSetsResult result = routeclient53
					.changeResourceRecordSets(changeResourceRecordSetsRequest);
			LOGGER.debug("HostName created as :" + loadBalancer + "." + environment.getHostName());
		}
		return true;

	}

	public static void deleteELB(ServiceEnvironment environment, String staticip, String serviceId) {

		String hostedZoneId = null;
		// String publicIPNew = null;
		String aliasHostedZoneId = null;
		String aliasDNSName = null;
		String loadBalancer = serviceId.split("-")[0];

		AWSCredentials credentials = new BasicAWSCredentials(environment.getAwsAccessKey(),
				environment.getAwsSecretKey());
		AmazonElasticLoadBalancingClient elb = new AmazonElasticLoadBalancingClient(credentials);
		Region se = Region.getRegion(Regions.AP_SOUTHEAST_1);
		elb.setRegion(se);
		AmazonRoute53Client routeclient53 = new AmazonRoute53Client(credentials);
		routeclient53.setRegion(se);

		AmazonRoute53 route53 = new AmazonRoute53Client(credentials);
		route53.setRegion(se);

		/** To get the hosted zone ID **/
		ListHostedZonesResult listHostedZonesResult = route53.listHostedZones();
		List<HostedZone> lsthostedZone = listHostedZonesResult.getHostedZones();
		for (HostedZone hostedZone : lsthostedZone) {
			if (hostedZone.getName().contains(environment.getHostName())) {
				LOGGER.debug("hostedZone : " + hostedZone.getName());
				hostedZoneId = hostedZone.getId();
				break;
			}
		}

		DescribeLoadBalancersResult lbs = elb.describeLoadBalancers();
		List<LoadBalancerDescription> descriptions = lbs.getLoadBalancerDescriptions();
		System.out.println("descriptions : " + descriptions.size());
		for (LoadBalancerDescription loadBalancerDescription : descriptions) {
			if (loadBalancer.equals(loadBalancerDescription.getLoadBalancerName())) {
				LOGGER.debug("Name: " + loadBalancerDescription.getLoadBalancerName());
				LOGGER.debug("DNS Name: " + loadBalancerDescription.getDNSName());
				aliasHostedZoneId = loadBalancerDescription.getCanonicalHostedZoneNameID();
				aliasDNSName = loadBalancerDescription.getDNSName();
				break;
			}
		}

		ChangeBatch changeBatch = new ChangeBatch();
		Collection<Change> changes = new ArrayList<Change>();
		Change change = new Change();
		change.setAction(ChangeAction.DELETE);
		changes.add(change);
		changeBatch.setChanges(changes);

		ResourceRecordSet resourceRecordSet = new ResourceRecordSet();
		AliasTarget alias = new AliasTarget();

		alias.setHostedZoneId(aliasHostedZoneId);

		alias.setDNSName(aliasDNSName);
		alias.setEvaluateTargetHealth(false);
		resourceRecordSet.setAliasTarget(alias);

		resourceRecordSet.setName(loadBalancer + "." + environment.getHostName());
		resourceRecordSet.setType(RRType.A);

		change.setResourceRecordSet(resourceRecordSet);

		ChangeResourceRecordSetsRequest changeResourceRecordSetsRequest = new ChangeResourceRecordSetsRequest();
		changeResourceRecordSetsRequest.setHostedZoneId(hostedZoneId);
		changeResourceRecordSetsRequest.setChangeBatch(changeBatch);
		ChangeResourceRecordSetsResult result = routeclient53.changeResourceRecordSets(changeResourceRecordSetsRequest);

		LOGGER.debug("HostName Deleted :" + loadBalancer + "." + environment.getHostName());

		DeleteLoadBalancerRequest elbDelete = new DeleteLoadBalancerRequest();
		elbDelete.setLoadBalancerName(loadBalancer);
		elb.deleteLoadBalancer(elbDelete);
		LOGGER.debug("Deleted ELB : " + loadBalancer);
	}

}
