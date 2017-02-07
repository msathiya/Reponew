package org.service.tj.plan;

import java.util.Map;

import org.springframework.cloud.servicebroker.model.CreateServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.DeleteServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.DeleteServiceInstanceRequest;

public interface ResourcePlan {

	void createServiceInstance( CreateServiceInstanceRequest request);

	void deleteServiceInstance(DeleteServiceInstanceRequest request);

	Map<String, Object> createServiceInstanceBinding(CreateServiceInstanceBindingRequest request);

	void deleteServiceInstanceBinding(DeleteServiceInstanceBindingRequest request);

}
