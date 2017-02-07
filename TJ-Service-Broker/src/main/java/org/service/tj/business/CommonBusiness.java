package org.service.tj.business;

import java.util.Map;

import org.service.broker.exception.ServiceException;
import org.service.model.OperationRequest;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.DeleteServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.DeleteServiceInstanceRequest;

public interface CommonBusiness {

	void createServiceInstance(OperationRequest operationRequest, CreateServiceInstanceRequest request)
			throws ServiceException;
		
	public void deleteServiceInstance(OperationRequest operationRequest,DeleteServiceInstanceRequest request) throws ServiceException;
	
	public Map<String, Object> createServiceInstanceBinding(OperationRequest operationRequest,CreateServiceInstanceBindingRequest request) throws ServiceException;
	
	public void deleteServiceInstanceBinding(OperationRequest operationRequest,DeleteServiceInstanceBindingRequest request) throws ServiceException;
}
