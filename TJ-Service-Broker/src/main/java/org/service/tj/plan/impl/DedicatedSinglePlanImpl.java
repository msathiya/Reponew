package org.service.tj.plan.impl;

import static org.service.tj.data.ResourceConstant.DEDICATED_SINGLE_INSTANCE;

import java.util.Map;

import org.apache.log4j.Logger;
import org.service.broker.exception.ServiceException;
import org.service.tj.business.DedicatedSingleBusiness;
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
@Qualifier(DEDICATED_SINGLE_INSTANCE)
public class DedicatedSinglePlanImpl implements ResourcePlan {
	/**
	 * LOGGER variable
	 */
	private static final Logger LOGGER = Logger.getLogger(DedicatedSinglePlanImpl.class);

	@Autowired
	private DedicatedSingleBusiness dedicatedSingleBusiness;

	@Override
	@Async
	public void createServiceInstance(CreateServiceInstanceRequest request) {
		LOGGER.info("Maria dedicated single node execute");
		try {
			dedicatedSingleBusiness.createServiceInstance(request);
		} catch (ServiceException exception) {
			LOGGER.error("Exception", exception);
		} catch (RuntimeException exception) {

			LOGGER.error("Exception", exception);
		}
	}

	@Override
	@Async
	public void deleteServiceInstance(DeleteServiceInstanceRequest request) {
		LOGGER.info("Maria dedicated single node dropexecute");
		try {
			dedicatedSingleBusiness.deleteServiceInstance(request);
		} catch (ServiceException exception) {
			LOGGER.error(exception);
		} catch (RuntimeException exception) {

			LOGGER.error(exception);
		}
	}

	@Override
	public Map<String, Object> createServiceInstanceBinding(CreateServiceInstanceBindingRequest request) {
		LOGGER.info("Maria dedicated single node bind execute");
		Map<String, Object> bindingVcap = null;
		try {
			bindingVcap = dedicatedSingleBusiness.createServiceInstanceBinding(request);
		} catch (ServiceException exception) {
			LOGGER.error("Exception", exception);
		} catch (RuntimeException exception) {

			LOGGER.error("Exception", exception);
		}
		return bindingVcap;
	}

	@Override
	public void deleteServiceInstanceBinding(DeleteServiceInstanceBindingRequest request) {
		LOGGER.info("Maria dedicated single node unbind execute");

	}

}
