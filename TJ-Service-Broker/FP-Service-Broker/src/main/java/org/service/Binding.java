package org.service;

import org.springframework.cloud.servicebroker.model.CreateServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceBindingResponse;
import org.springframework.cloud.servicebroker.model.DeleteServiceInstanceBindingRequest;

/**
 * The <code>Binding</code> interface represents interface for binding storage
 * service created via broker with APP.
 * 
 * @author Sandeep (sandeep.visvanathan@cognizant.com)
 * @author Deepthi (deepthi.g2@cognizant.com)
 * @author Sundar (sundarajan.srinivasan@cognizant.com)
 * @author Ramkumar(ramkumar.kandhakumar@cognizant.com)
 */
public interface Binding {

	/**
	 * This interface method is used to bind the service with APPS.
	 * 
	 * @param request
	 *            holds <code>CreateServiceInstanceBindingRequest</code>
	 *            reference.
	 * @return returns the APP binding details to be set in APP environment.
	 */
	CreateServiceInstanceBindingResponse createServiceInstanceBinding(CreateServiceInstanceBindingRequest request);

	/**
	 * This interface method is used to un-bind the service from the app.
	 * 
	 * @param request
	 *            holds <code>DeleteServiceInstanceBindingRequest</code>
	 */
	void deleteServiceInstanceBinding(DeleteServiceInstanceBindingRequest request);

}
