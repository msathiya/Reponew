package org.service.tj.business.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.service.bosh.util.BoshRestUtil;
import org.service.bosh.util.BoshUtil;
import org.service.broker.exception.ServiceException;
import org.service.data.ServiceEnvironment;
import org.service.model.OperationRequest;
import org.service.tj.business.CommonBusiness;
import org.service.tj.business.SharedClusterBusiness;
import org.service.tj.data.ResourceConstant;
import org.service.tj.data.ResourceEnvironment;
import org.service.tj.helper.ResourceDBOperations;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.DeleteServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.DeleteServiceInstanceRequest;
import org.springframework.stereotype.Component;

@Component
public class SharedClusterBusinessImpl implements SharedClusterBusiness {

	/**
	 * LOGGER variable
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(SharedClusterBusinessImpl.class);

	@Autowired
	private CommonBusiness commonBusiness;

	@Autowired
	private ServiceEnvironment sEnvironment;

	@Autowired
	private ResourceEnvironment rEnvironment;

	@Override
	public void createServiceInstance(CreateServiceInstanceRequest request) throws ServiceException {
		try {
			String deploymentName = ResourceConstant.SHARED_CLUSTER_SERVICE_PREFIX;
			OperationRequest operationRequest = new OperationRequest();
			operationRequest.setDeploymentName(deploymentName);
			operationRequest.setInstanceCount(rEnvironment.getPlanSharedClusterInstance());

			commonBusiness.createServiceInstance(operationRequest, request);

		} catch (RuntimeException exception) {
			throw new ServiceException(exception);
		}
	}

	@Override
	public void deleteServiceInstance(DeleteServiceInstanceRequest request) throws ServiceException {
		try {

			String deploymentName = ResourceConstant.SHARED_CLUSTER_SERVICE_PREFIX;
			OperationRequest operationRequest = new OperationRequest();
			operationRequest.setDeploymentName(deploymentName);
			operationRequest.setInstanceCount(rEnvironment.getPlanSharedClusterInstance());
			Map<String, String> provisioningInput = BoshUtil.getReqestHeader(operationRequest, sEnvironment);
			List<String> iplist = BoshRestUtil.getExposeIpList(provisioningInput);
			ResourceDBOperations.mongoDatabaseDrop(iplist.get(0), request.getServiceInstanceId(), "DROP", rEnvironment);
//			int dbCount = ResourceDBOperations.getCountOfDB(iplist.get(0), rEnvironment);
//			LOGGER.debug("dbCount-{}", dbCount);
//			if (dbCount == 0) {
//
//				commonBusiness.deleteServiceInstance(operationRequest, request);
//			}

			
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public Map<String, Object> createServiceInstanceBinding(CreateServiceInstanceBindingRequest request)
			throws ServiceException {
		Map<String, Object> bindingVcap = null;
		String serviceName = StringUtils.join("DB_", StringUtils.replace(request.getServiceInstanceId(), "-", "_"));
		try {
			String deploymentName = ResourceConstant.SHARED_CLUSTER_SERVICE_PREFIX;
			String mongoRole = "readWrite";

			OperationRequest operationRequest = new OperationRequest();
			operationRequest.setDeploymentName(deploymentName);
			operationRequest.setInstanceCount(rEnvironment.getPlanSharedClusterInstance());
			operationRequest.setMongoRole(mongoRole);
			operationRequest.setServiceName(serviceName);

			bindingVcap = commonBusiness.createServiceInstanceBinding(operationRequest, request);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
		bindingVcap.put("DBName", serviceName);
		bindingVcap.put("port", rEnvironment.getServerDefaultPort());
		return bindingVcap;
	}

	@Override
	public void deleteServiceInstanceBinding(DeleteServiceInstanceBindingRequest request) throws ServiceException {
		try {
			String deploymentName = ResourceConstant.SHARED_CLUSTER_SERVICE_PREFIX;
			OperationRequest operationRequest = new OperationRequest();
			operationRequest.setDeploymentName(deploymentName);
			operationRequest.setInstanceCount(rEnvironment.getPlanSharedClusterInstance());

			commonBusiness.deleteServiceInstanceBinding(operationRequest, request);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

}