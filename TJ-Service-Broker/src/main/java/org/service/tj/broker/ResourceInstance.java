package org.service.tj.broker;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.service.Instance;
import org.service.bosh.util.BoshRestUtil;
import org.service.dao.ServiceDao;
import org.service.data.ServiceEnvironment;
import org.service.repo.entity.Service;
import org.service.tj.plan.ResourcePlanLocator;
import org.service.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerException;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceResponse;
import org.springframework.cloud.servicebroker.model.DeleteServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.DeleteServiceInstanceResponse;
import org.springframework.cloud.servicebroker.model.GetLastServiceOperationRequest;
import org.springframework.cloud.servicebroker.model.GetLastServiceOperationResponse;
import org.springframework.cloud.servicebroker.model.OperationState;
import org.springframework.cloud.servicebroker.model.UpdateServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.UpdateServiceInstanceResponse;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ResourceInstance implements Instance {

	/**
	 * LOGGER variable
	 */
	private static final Logger LOGGER = Logger.getLogger(ResourceInstance.class);

	@Autowired
	private ServiceEnvironment environment;

	@Autowired
	private ResourcePlanLocator planLocator;

	@Autowired
	private ServiceDao serviceDao;

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public CreateServiceInstanceResponse createServiceInstance(CreateServiceInstanceRequest request) {

		LOGGER.info("createServiceInstance");
		CreateServiceInstanceResponse response = new CreateServiceInstanceResponse();
			response.withAsync(true);
			planLocator.lookup(request.getPlanId()).createServiceInstance(request);
		LOGGER.info("createServiceInstance - Initiated");
		return response;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public GetLastServiceOperationResponse getLastOperation(GetLastServiceOperationRequest request) {
		LOGGER.info("getLastOperation");

		GetLastServiceOperationResponse response = new GetLastServiceOperationResponse();
		Service service = serviceDao.findByServiceUUID(request.getServiceInstanceId());
		try {

			String value = service.getStatus();
			String taskUrl = service.getServiceDeployment().getTaskUrl();

			if (StringUtils.equalsIgnoreCase(OperationState.SUCCEEDED.getValue(), value)) {
				service.setStatus(OperationState.SUCCEEDED.getValue());
				response.withOperationState(OperationState.SUCCEEDED);
				response.withDescription("Success");
			} else if (StringUtils.equalsIgnoreCase(OperationState.IN_PROGRESS.getValue(), value)) {

				service.setStatus(OperationState.IN_PROGRESS.getValue());
				response.withOperationState(OperationState.IN_PROGRESS);
				response.withDescription("In Progress");
				if (StringUtils.isNotEmpty(taskUrl)) {
					String host = environment.getBoshIpAddress() + ":" + environment.getBoshPort();
					Map<String, String> getscalingInput = new HashMap<String, String>();
					getscalingInput.put("boshProtocol", environment.getBoshProtocol());
					getscalingInput.put("boshUrl", host);
					getscalingInput.put("Authorization", environment.getBoshAuthorization());

					String getStatusResponse = BoshRestUtil.getTaskStatus(getscalingInput, taskUrl);
					Map<String, Object> responseJSON = JsonUtil.getJsonMap(getStatusResponse);
					String status = (String) responseJSON.get("state");
					if (StringUtils.equalsIgnoreCase(status, "done")) {
						service.setStatus(OperationState.SUCCEEDED.getValue());
						response.withOperationState(OperationState.SUCCEEDED);
						response.withDescription("Success");
					} else if (StringUtils.equalsIgnoreCase(status, "failed")) {
						service.setStatus(OperationState.FAILED.getValue());
						response.withOperationState(OperationState.FAILED);
						response.withDescription("Failed");
					}
				}
			} else {
				throw new ServiceBrokerException("Exception during task validation.");
			}

		} catch (Exception e) {
			service.setStatus(OperationState.FAILED.getValue());
			throw new ServiceBrokerException("Exception during task validation.");
		}

		LOGGER.info(String.format("Service status %s in response.", service.getStatus()));
		serviceDao.updateServiceStatus(service.getServiceUUID(),service.getStatus());
		LOGGER.debug(String.format("Response - %s ", response.toString()));
		return response;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public DeleteServiceInstanceResponse deleteServiceInstance(DeleteServiceInstanceRequest request) {
		LOGGER.info("deleteServiceInstance");
		planLocator.lookup(request.getPlanId()).deleteServiceInstance(request);
		return new DeleteServiceInstanceResponse();
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public UpdateServiceInstanceResponse updateServiceInstance(UpdateServiceInstanceRequest request) {

		LOGGER.info("updateServiceInstance-Started");

		LOGGER.info("updateServiceInstance-Completed");
		return new UpdateServiceInstanceResponse();
	}

}
