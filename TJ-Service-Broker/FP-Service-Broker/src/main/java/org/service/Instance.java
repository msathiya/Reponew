package org.service;

import org.springframework.cloud.servicebroker.model.CreateServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.CreateServiceInstanceResponse;
import org.springframework.cloud.servicebroker.model.DeleteServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.DeleteServiceInstanceResponse;
import org.springframework.cloud.servicebroker.model.GetLastServiceOperationRequest;
import org.springframework.cloud.servicebroker.model.GetLastServiceOperationResponse;
import org.springframework.cloud.servicebroker.model.UpdateServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.UpdateServiceInstanceResponse;

/**
 * The <code>Instance</code> interface represents interface for
 * creating,updating and deleting the service and also ASYNC flow.
 * 
 * @author Sandeep (sandeep.visvanathan@cognizant.com)
 * @author Deepthi (deepthi.g2@cognizant.com)
 * @author Sundar (sundarajan.srinivasan@cognizant.com)
 * @author Ramkumar(ramkumar.kandhakumar@cognizant.com)
 *
 */
public interface Instance {

	/**
	 * This interface method is used to create a service instance.
	 * 
	 * @param request
	 *            holds <code>CreateServiceInstanceRequest</code> reference.
	 * @return CreateServiceInstanceResponse returns create service instance
	 *         response.
	 */
	CreateServiceInstanceResponse createServiceInstance(CreateServiceInstanceRequest request);

	/**
	 * This interface method is for ASYNC call (polling till the service
	 * creation is done).
	 * 
	 * @param request
	 *            holds <code>GetLastServiceOperationRequest</code> reference.
	 * @return GetLastServiceOperationResponse returns get last service
	 *         operation response.
	 */
	GetLastServiceOperationResponse getLastOperation(GetLastServiceOperationRequest request);

	/**
	 * This interface method is used for deleting the service.
	 * 
	 * @param request
	 *            holds <code>DeleteServiceInstanceRequest</code> reference.
	 * @return DeleteServiceInstanceResponse returns delete service instance
	 *         response.
	 */
	DeleteServiceInstanceResponse deleteServiceInstance(DeleteServiceInstanceRequest request);

	/**
	 * This interface method is used for updating the service.
	 * 
	 * @param request
	 *            holds <code>UpdateServiceInstanceRequest</code> reference.
	 * @return UpdateServiceInstanceResponse returns update service instance
	 *         response.
	 */
	UpdateServiceInstanceResponse updateServiceInstance(UpdateServiceInstanceRequest request);
}
