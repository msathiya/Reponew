package org.service.tj.business.impl;

import static org.service.data.ServiceConstant.HYPEN;
import static org.service.data.ServiceConstant.UNDERSCORE;

import java.io.File;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.service.broker.exception.ServiceException;
import org.service.cf.util.CFUtil;
import org.service.data.ServiceEnvironment;
import org.service.model.OperationRequest;
import org.service.tj.business.CommonBusiness;
import org.service.tj.business.DedicatedSingleBusiness;
import org.service.tj.data.ResourceConstant;
import org.service.tj.data.ResourceEnvironment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.DeleteServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.DeleteServiceInstanceRequest;
import org.springframework.stereotype.Component;

@Component
public class DedicatedSingleBusinessImpl implements DedicatedSingleBusiness {

	/**
	 * LOGGER variable
	 */
	private static final Logger LOGGER = Logger.getLogger(CommonBusinessImpl.class);
	@Autowired
	private CommonBusiness commonBusiness;

	@Autowired
	private ServiceEnvironment sEnvironment;

	@Autowired
	private ResourceEnvironment rEnvironment;

	@Override
	public void createServiceInstance(CreateServiceInstanceRequest request) throws ServiceException {
		try {
			String deploymentName = StringUtils.join(ResourceConstant.DEDICATED_SINGLE_SERVICE_PREFIX,
					StringUtils.replace(request.getServiceInstanceId(), HYPEN, UNDERSCORE));

			OperationRequest operationRequest = new OperationRequest();
			operationRequest.setDeploymentName(deploymentName);
			operationRequest.setInstanceCount(rEnvironment.getPlanDedicatedSingleInstance());

			commonBusiness.createServiceInstance(operationRequest, request);

		} catch (RuntimeException exception) {
			throw new ServiceException(exception);
		}
	}

	@Override
	public void deleteServiceInstance(DeleteServiceInstanceRequest request) throws ServiceException {
		try {

			String deploymentName = StringUtils.join(ResourceConstant.DEDICATED_SINGLE_SERVICE_PREFIX,
					StringUtils.replace(request.getServiceInstanceId(), HYPEN, UNDERSCORE));

			OperationRequest operationRequest = new OperationRequest();
			operationRequest.setDeploymentName(deploymentName);
			operationRequest.setInstanceCount(rEnvironment.getPlanDedicatedSingleInstance());

//			String cfappName ="cfbot"+request.getServiceInstanceId().split("-")[0];
//			LOGGER.debug("appName : "+cfappName);
			
//			String authToken=CFUtil.login(sEnvironment);
//			String appGuid=CFUtil.getAppsGUID(authToken, cfappName, sEnvironment);
//			String serviceGuid=CFUtil.getUPSGUID(authToken, cfappName, sEnvironment);
//			String serviceBindingGUID=request.getServiceInstanceId();
//			CFUtil.deleteService(appGuid,serviceGuid, serviceBindingGUID, sEnvironment, authToken);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public Map<String, Object> createServiceInstanceBinding(CreateServiceInstanceBindingRequest request)
			throws ServiceException {
		Map<String, Object> bindingVcap = null;
		String serviceName = "admin";
		try {
			String deploymentName = StringUtils.join(ResourceConstant.DEDICATED_SINGLE_SERVICE_PREFIX,
					StringUtils.replace(request.getServiceInstanceId(), HYPEN, UNDERSCORE));
			String mongoRole = "root";

			OperationRequest operationRequest = new OperationRequest();
			operationRequest.setDeploymentName(deploymentName);
			operationRequest.setInstanceCount(rEnvironment.getPlanDedicatedSingleInstance());
			operationRequest.setMongoRole(mongoRole);
			operationRequest.setServiceName(serviceName);

			bindingVcap = commonBusiness.createServiceInstanceBinding(operationRequest, request);
//			String gitDownloadDirectory=request.getServiceInstanceId().split("-")[0];
//			String cfappName ="cfbot"+request.getServiceInstanceId().split("-")[0];
//			LOGGER.debug("appName : "+cfappName);
//			File zipFile = new File(gitDownloadDirectory+"/application.zip");
//			String serviceBindingGUID=request.getServiceInstanceId();
//			CFUtil.redeployApp(cfappName, sEnvironment, zipFile,request.getParameters(),request.getBoundAppGuid(),false,serviceBindingGUID);
//			bindingVcap.put("cfbotname", cfappName);
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
			String deploymentName = StringUtils.join(ResourceConstant.DEDICATED_SINGLE_SERVICE_PREFIX,
					StringUtils.replace(request.getServiceInstanceId(), HYPEN, UNDERSCORE));

			OperationRequest operationRequest = new OperationRequest();
			operationRequest.setDeploymentName(deploymentName);
			operationRequest.setInstanceCount(rEnvironment.getPlanDedicatedSingleInstance());

			commonBusiness.deleteServiceInstanceBinding(operationRequest, request);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

}
