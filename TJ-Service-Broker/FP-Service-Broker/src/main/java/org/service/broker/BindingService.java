package org.service.broker;

import org.service.Binding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceBindingResponse;
import org.springframework.cloud.servicebroker.model.DeleteServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.service.ServiceInstanceBindingService;
import org.springframework.stereotype.Service;

/**
 * The <code>BindingService</code> class represents interface for binding
 * storage service created via broker with APP.
 * 
 * @author Sandeep (sandeep.visvanathan@cognizant.com)
 * @author Deepthi (deepthi.g2@cognizant.com)
 * @author Sundar (sundarajan.srinivasan@cognizant.com)
 * @author Ramkumar(ramkumar.kandhakumar@cognizant.com)
 *
 */
@Service
public class BindingService implements ServiceInstanceBindingService {
	/**
	 * Holds LOGGER reference.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(BindingService.class);

	/**
	 * Holds binding reference.
	 */
	@Autowired
	private Binding binding;

	/**
	 * Method to implement create service instance binding request.
	 * 
	 * @param request
	 *            holds <code>CreateServiceInstanceBindingRequest</code>
	 *            reference.
	 * @return CreateServiceInstanceBindingResponse returns create service
	 *         instance binding response.
	 */
	@Override
	public CreateServiceInstanceBindingResponse createServiceInstanceBinding(
			CreateServiceInstanceBindingRequest request) {
		LOGGER.info("Create binding request for service ({}) resouces ({}).", request.getServiceInstanceId(),
				request.getBindResource());

		return binding.createServiceInstanceBinding(request);
	}

	/**
	 * Method to implement delete service instance binding request.
	 * 
	 * @param request
	 *            holds <code>DeleteServiceInstanceBindingRequest</code>
	 *            reference.
	 */
	@Override
	public void deleteServiceInstanceBinding(DeleteServiceInstanceBindingRequest request) {
		LOGGER.info("Delete binding request for service ({}); binding ({}).", request.getServiceInstanceId(),
				request.getBindingId());

		binding.deleteServiceInstanceBinding(request);
	}

}