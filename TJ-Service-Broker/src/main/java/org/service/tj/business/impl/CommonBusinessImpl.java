package org.service.tj.business.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.json.simple.JSONObject;
import org.service.bosh.util.BoshRestUtil;
import org.service.bosh.util.BoshUtil;
import org.service.broker.exception.ServiceException;
import org.service.cf.util.CFUtil;
import org.service.dao.MarketplaceServiceDao;
import org.service.dao.ServiceDao;
import org.service.dao.SubnetIpAddressDao;
import org.service.data.ServiceEnvironment;
import org.service.model.OperationRequest;
import org.service.repo.entity.Service;
import org.service.repo.entity.ServiceDeployment;
import org.service.tj.business.CommonBusiness;
import org.service.tj.data.ResourceEnvironment;
import org.service.util.YamlUtil;
import org.service.util.ZipUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.DeleteServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.DeleteServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.OperationState;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class CommonBusinessImpl implements CommonBusiness {

	/**
	 * LOGGER variable
	 */
	private static final Logger LOGGER = Logger.getLogger(CommonBusinessImpl.class);

	@Autowired
	private SubnetIpAddressDao subnetIpAddressDao;

	@Autowired
	private ServiceDao serviceDao;
	
	@Autowired
	private MarketplaceServiceDao marketplaceServiceDao;

	@Autowired
	private ServiceEnvironment sEnvironment;

	@Autowired
	private ResourceEnvironment rEnvironment;

	@Override
	public void createServiceInstance(OperationRequest operationRequest, CreateServiceInstanceRequest request)
			throws ServiceException {

		Service service = new Service();
		service.setServiceUUID(request.getServiceInstanceId());
		ServiceDeployment serviceDeployment = new ServiceDeployment();
		serviceDeployment.setServiceDeploymentUUID(request.getServiceInstanceId());
		serviceDeployment.setPlanUUID(request.getPlanId());
		serviceDeployment.setDeploymentName(operationRequest.getDeploymentName());
		serviceDeployment.setCfServiceName(rEnvironment.getServiceName());
		serviceDao.saveServiceDeployment(serviceDeployment);
		service.setStatus(OperationState.IN_PROGRESS.getValue());
		service.setServiceDeployment(serviceDeployment);
//		serviceDao.saveService(service);
		
		Resource resource = new ClassPathResource("/TemplateApp/template.zip");
		String cups_name="javamongosingleone";
		String filetoupload = null;
		try {
			filetoupload = resource.getFile().getPath();
			CFUtil.TJdeployApp(cups_name, sEnvironment, filetoupload, request.getParameters(),service);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
				
	}

	@Override
	public void deleteServiceInstance(OperationRequest operationRequest, DeleteServiceInstanceRequest request)
			throws ServiceException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<String, Object> createServiceInstanceBinding(OperationRequest operationRequest,
			CreateServiceInstanceBindingRequest request) throws ServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteServiceInstanceBinding(OperationRequest operationRequest,
			DeleteServiceInstanceBindingRequest request) throws ServiceException {
		// TODO Auto-generated method stub
		
	}

}
