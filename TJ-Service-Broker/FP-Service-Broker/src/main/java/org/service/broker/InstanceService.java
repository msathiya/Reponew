package org.service.broker;

import org.service.Instance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceResponse;
import org.springframework.cloud.servicebroker.model.DeleteServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.DeleteServiceInstanceResponse;
import org.springframework.cloud.servicebroker.model.GetLastServiceOperationRequest;
import org.springframework.cloud.servicebroker.model.GetLastServiceOperationResponse;
import org.springframework.cloud.servicebroker.model.UpdateServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.UpdateServiceInstanceResponse;
import org.springframework.cloud.servicebroker.service.ServiceInstanceService;
import org.springframework.stereotype.Service;

/**
 * The <code>InstanceService</code> class represents interface for
 * creating,updating and deleting the service and also async flow.
 * 
 * @author Sandeep (sandeep.visvanathan@cognizant.com)
 * @author Deepthi (deepthi.g2@cognizant.com)
 * @author Sundar (sundarajan.srinivasan@cognizant.com)
 * @author Ramkumar(ramkumar.kandhakumar@cognizant.com)
 *
 */
@Service
public class InstanceService implements ServiceInstanceService {

	/**
	 * Holds LOGGER reference.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(InstanceService.class);

	/**
	 * Holds instance reference.
	 */
	@Autowired
	private Instance instance;

	/**
	 * This method is used for creating service instance.
	 */
	@Override
	public CreateServiceInstanceResponse createServiceInstance(CreateServiceInstanceRequest request) {
		LOGGER.info("Create service instance request for service({}) with plan({}).", request.getServiceInstanceId(),
				request.getPlanId());
		return instance.createServiceInstance(request);
	}

	/**
	 * This method is for ASYNC call - polling till the service creation is
	 * completed.
	 */
	@Override
	public GetLastServiceOperationResponse getLastOperation(GetLastServiceOperationRequest request) {

		LOGGER.info("Get last operation for service({}).", request.getServiceInstanceId());
		return instance.getLastOperation(request);
	}

	/**
	 * This method deletes the service instance.
	 */
	@Override
	public DeleteServiceInstanceResponse deleteServiceInstance(DeleteServiceInstanceRequest request) {

		LOGGER.info("Delete service instance for service({}).", request.getServiceInstanceId());
		return instance.deleteServiceInstance(request);
	}

	/**
	 * This method updates the service instance.
	 */
	@Override
	public UpdateServiceInstanceResponse updateServiceInstance(UpdateServiceInstanceRequest request) {

		LOGGER.info("Update service instance for service({}).", request.getServiceInstanceId());
		return instance.updateServiceInstance(request);
	}

}
