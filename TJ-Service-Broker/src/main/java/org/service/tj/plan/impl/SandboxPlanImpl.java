package org.service.tj.plan.impl;

import static org.service.tj.data.ResourceConstant.SANDBOX_INSTANCE;

import java.util.Map;

import org.apache.log4j.Logger;
import org.service.broker.exception.ServiceException;
import org.service.tj.business.SandboxBusiness;
import org.service.tj.plan.ResourcePlan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.DeleteServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.DeleteServiceInstanceRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Qualifier(SANDBOX_INSTANCE)
public class SandboxPlanImpl implements ResourcePlan {
	/**
	 * LOGGER variable
	 */
	private static final Logger LOGGER = Logger.getLogger(SandboxPlanImpl.class);

	@Autowired
	private SandboxBusiness sanboxBusiness;

	@Override
	@Async
	public void createServiceInstance(CreateServiceInstanceRequest request) {
		LOGGER.info("Maria sandbox execute");
		try {
			sanboxBusiness.createServiceInstance(request);
			
		} catch (ServiceException exception) {
			LOGGER.error("Exception", exception);
		} catch (RuntimeException exception) {

			LOGGER.error("Exception", exception);
		}
	}

	@Override
	@Async
	public void deleteServiceInstance(DeleteServiceInstanceRequest request) {
		LOGGER.info("Maria sandbox dropexecute");
		try {
			sanboxBusiness.deleteServiceInstance(request);
		} catch (ServiceException exception) {
			LOGGER.error("Exception", exception);
		} catch (RuntimeException exception) {

			LOGGER.error("Exception", exception);
		}
	}

	@Override
	public Map<String, Object> createServiceInstanceBinding(CreateServiceInstanceBindingRequest request) {
		LOGGER.info("Maria sandbox bind execute");
		Map<String, Object> bindingVcap = null;
		try {
			bindingVcap = sanboxBusiness.createServiceInstanceBinding(request);
		} catch (ServiceException exception) {
			LOGGER.error("Exception", exception);
		} catch (RuntimeException exception) {

			LOGGER.error("Exception", exception);
		}
		return bindingVcap;
	}

	@Override
	public void deleteServiceInstanceBinding(DeleteServiceInstanceBindingRequest request) {
		LOGGER.info("Maria sandbox unbind execute");

	}

}