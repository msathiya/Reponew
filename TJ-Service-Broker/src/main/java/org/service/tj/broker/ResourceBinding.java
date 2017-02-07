package org.service.tj.broker;

import java.util.Map;

import org.apache.log4j.Logger;
import org.service.Binding;
import org.service.tj.plan.ResourcePlanLocator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceAppBindingResponse;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceBindingResponse;
import org.springframework.cloud.servicebroker.model.DeleteServiceInstanceBindingRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ResourceBinding implements Binding{
	
	/**
	 * LOGGER variable
	 */
	private static final Logger LOGGER = Logger.getLogger(ResourceBinding.class);

	 @Autowired
	 private ResourcePlanLocator planLocator;

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public CreateServiceInstanceBindingResponse createServiceInstanceBinding(
			CreateServiceInstanceBindingRequest request) {
		LOGGER.info("CreateServiceInstanceBindingResponse");
		Map<String,Object> responseMessage = planLocator.lookup(request.getPlanId()).createServiceInstanceBinding(request);
		return new CreateServiceInstanceAppBindingResponse().withCredentials(responseMessage);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void deleteServiceInstanceBinding(DeleteServiceInstanceBindingRequest request) {
		
		LOGGER.info("deleteServiceInstanceBinding");
		planLocator.lookup(request.getPlanId()).deleteServiceInstanceBinding(request);
	}

}
