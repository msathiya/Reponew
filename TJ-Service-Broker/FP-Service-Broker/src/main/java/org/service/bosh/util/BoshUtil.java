package org.service.bosh.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.service.data.ServiceEnvironment;
import org.service.model.OperationRequest;
import org.service.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The <code>BoshUtil</code> class is a helper class for BOSH activities.
 * 
 * @author Sandeep (sandeep.visvanathan@cognizant.com)
 * @author Deepthi (deepthi.g2@cognizant.com)
 * @author Sundar (sundarajan.srinivasan@cognizant.com)
 * @author Ramkumar(ramkumar.kandhakumar@cognizant.com)
 */

public final class BoshUtil {

	/**
	 * Holds LOGGER reference.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(BoshUtil.class);

	/**
	 * Private constructor.
	 */
	private BoshUtil() {
		// Helper class constructor.
	}

	/**
	 * Method is used to check if the deployment exists.
	 * 
	 * @param deployments
	 *            holds deployment information.
	 * @param boshRequest
	 *            holds BOSH request.
	 * @return Boolean returns true (if deployment exists) and false (if
	 *         deployment does not exists).
	 */
	public static Boolean checkDeploymentAvailability(List<Map<String, Object>> deployments,
			Map<String, String> boshRequest) {

		Boolean availability = false;
		String deploymentName = boshRequest.get("name");

		for (Map<String, Object> deployment : deployments) {
			LOGGER.info("Deployment - {}.", deployment.get("name"));
			if (deploymentName.equals(deployment.get("name"))) {
				availability = true;
				break;
			}
		}

		return availability;
	}

	/**
	 * Method gets the IP values for the deployment.
	 * 
	 * @param deployments
	 *            holds deployment details.
	 * @param boshRequest
	 *            holds BOSH request.
	 * @return List<String> returns list of IPs used for deployment.
	 * @throws Exception
	 *             throws on exception.
	 */
	public static List<String> getInternetProtocolValues(List<Map<String, Object>> deployments,
			Map<String, String> boshRequest) throws Exception {

		List<String> ipList = new ArrayList<String>();
		for (Map<String, Object> deployment : deployments) {

			// Gets the VMs details
			String responseData = BoshRestUtil.getVmsDetails(boshRequest, deployment.get("name").toString());

			Map<String, Object> vmDetails = JsonUtil.getJsonMap(responseData);
			for (String vmdetail : vmDetails.keySet()) {
				if (vmdetail == "id") {
					String taskDetails = BoshRestUtil.getTaskDetail(boshRequest, vmDetails.get("id").toString());
					LOGGER.debug("TaskDetails - {}.", taskDetails);
					ipList = BoshDataHandler.obtainIpList(taskDetails);
				}
			}
		}

		return ipList;
	}

	/**
	 * Method is used to frame the inputs required for BOSH.
	 * 
	 * @param operationRequest
	 *            holds operation request reference.
	 * @param environment
	 *            holds DBEnvironment reference.
	 * @return Map<String,String> returns the inputs required for accessing
	 *         BOSH.
	 */
	public static Map<String, String> getReqestHeader(OperationRequest operationRequest, ServiceEnvironment environment) {

		Map<String, String> provisioningInput = new HashMap<String, String>();

		String host = environment.getBoshIpAddress() + ":" + environment.getBoshPort();
		provisioningInput.put("name", operationRequest.getDeploymentName());
		provisioningInput.put("serviceInstance", operationRequest.getInstanceCount());
		provisioningInput.put("boshProtocol", environment.getBoshProtocol());
		provisioningInput.put("boshUrl", host);
		provisioningInput.put("boshDeploymentPath", "/deployments");
		provisioningInput.put("Authorization", environment.getBoshAuthorization());

		return provisioningInput;
	}

}
