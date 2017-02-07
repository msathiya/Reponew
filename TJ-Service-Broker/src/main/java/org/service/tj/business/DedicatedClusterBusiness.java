package org.service.tj.business;

import java.util.Map;

import org.service.broker.exception.ServiceException;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.DeleteServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.DeleteServiceInstanceRequest;

public interface DedicatedClusterBusiness {

	void createServiceInstance(CreateServiceInstanceRequest request) throws ServiceException;

	void deleteServiceInstance(DeleteServiceInstanceRequest request) throws ServiceException;

	Map<String, Object> createServiceInstanceBinding(CreateServiceInstanceBindingRequest request) throws ServiceException;

	void deleteServiceInstanceBinding(DeleteServiceInstanceBindingRequest request) throws ServiceException;

}
