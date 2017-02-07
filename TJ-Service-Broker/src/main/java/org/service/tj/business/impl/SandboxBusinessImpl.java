package org.service.tj.business.impl;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.service.bosh.util.BoshRestUtil;
import org.service.bosh.util.BoshUtil;
import org.service.broker.exception.ServiceException;
import org.service.cf.util.CFUtil;
import org.service.data.ServiceEnvironment;
import org.service.model.OperationRequest;
import org.service.tj.business.CommonBusiness;
import org.service.tj.business.SandboxBusiness;
import org.service.tj.data.ResourceConstant;
import org.service.tj.data.ResourceEnvironment;
import org.service.tj.helper.ResourceDBOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.DeleteServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.DeleteServiceInstanceRequest;
import org.springframework.stereotype.Component;

@Component
public class SandboxBusinessImpl implements SandboxBusiness {

	
	/**
	 * LOGGER variable
	 */
	private static final Logger LOGGER = Logger.getLogger(SandboxBusinessImpl.class);
	@Autowired
	private CommonBusiness commonBusiness;

	@Autowired
	private ServiceEnvironment sEnvironment;

	@Autowired
	private ResourceEnvironment rEnvironment;

	@Override
	public void createServiceInstance(CreateServiceInstanceRequest request) throws ServiceException {
		try {
			String deploymentName = ResourceConstant.DEFAULT_SERVICE_PREFIX;
			OperationRequest operationRequest = new OperationRequest();
			operationRequest.setDeploymentName(deploymentName);
			operationRequest.setInstanceCount(rEnvironment.getPlanSandboxInstance());
			commonBusiness.createServiceInstance(operationRequest, request);

		} catch (RuntimeException exception) {
			throw new ServiceException(exception);
		}
	}
// Application exception overridden by rollback exception
	@Override
	public void deleteServiceInstance(DeleteServiceInstanceRequest request) throws ServiceException {
		try {

			String deploymentName = ResourceConstant.SANDBOX_SERVICE_PREFIX;
			OperationRequest operationRequest = new OperationRequest();
			operationRequest.setDeploymentName(deploymentName);
			operationRequest.setInstanceCount(rEnvironment.getPlanSandboxInstance());
			Map<String, String> provisioningInput = BoshUtil.getReqestHeader(operationRequest, sEnvironment);
			List<String> iplist = BoshRestUtil.getExposeIpList(provisioningInput);
			ResourceDBOperations.mongoDatabaseDrop(iplist.get(0), request.getServiceInstanceId(), "DROP", rEnvironment);

			
		
		
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public Map<String, Object> createServiceInstanceBinding(CreateServiceInstanceBindingRequest request)
			throws ServiceException {
		Map<String, Object> bindingVcap = null;
//		String serviceName = StringUtils.join("DB_", StringUtils.replace(request.getServiceInstanceId(), "-", "_"));
		try {
			String deploymentName = ResourceConstant.SANDBOX_SERVICE_PREFIX;
//			String mongoRole = "readWrite";

			OperationRequest operationRequest = new OperationRequest();
			operationRequest.setDeploymentName(deploymentName);
			operationRequest.setInstanceCount(rEnvironment.getPlanSandboxInstance());


			
		} catch (Exception e) {
			throw new ServiceException(e);
		}
		return bindingVcap;
	}

	@Override
	public void deleteServiceInstanceBinding(DeleteServiceInstanceBindingRequest request) throws ServiceException {
		try {
			String deploymentName = ResourceConstant.SANDBOX_SERVICE_PREFIX;
			OperationRequest operationRequest = new OperationRequest();
			operationRequest.setDeploymentName(deploymentName);
			operationRequest.setInstanceCount(rEnvironment.getPlanSandboxInstance());

			String cfappName ="cfbot"+request.getServiceInstanceId().split("-")[0];
			LOGGER.debug("appName : "+cfappName);
			
			String authToken=CFUtil.login(sEnvironment);
			
			CFUtil.deleteServiceBinding(authToken, cfappName, sEnvironment);
			
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

}