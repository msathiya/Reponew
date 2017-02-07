package org.service.dao;

import java.util.Map;

import org.service.repo.entity.DeploymentManifest;
import org.service.repo.entity.Service;
import org.service.repo.entity.ServiceDeployment;

/**
 * The <code>ServiceStatusDao</code> interface represents interface for
 * creating,updating and finding service status.
 * 
 * @author Sandeep (sandeep.visvanathan@cognizant.com)
 * @author Deepthi (deepthi.g2@cognizant.com)
 * @author Sundar (sundarajan.srinivasan@cognizant.com)
 * @author Ramkumar(ramkumar.kandhakumar@cognizant.com)
 *
 */
public interface ServiceDao {

	/**
	 * Method declaration to save service deployment.
	 * 
	 * @param serviceDeployment
	 *            holds <code>ServiceDeployment</code> reference.
	 * @return ServiceDeployment returns service deployment entity reference.
	 * @see org.service.repo.entity.ServiceDeployment
	 */
	ServiceDeployment saveServiceDeployment(ServiceDeployment serviceDeployment);

	/**
	 * Method to save deployment manifest.
	 * 
	 * @param serviceDeploymentUUID
	 *            holds service deployment UUID.
	 * @param manifestYaml
	 *            holds deployment manifest YAML string.
	 * @return DeploymentManifest returns deployment manifest entity reference.
	 */
	DeploymentManifest saveDeploymentManifest(String serviceDeploymentUUID, String manifestYaml);

	/**
	 * Method declaration to save service.
	 * 
	 * @param service
	 *            holds <code>Service</code> reference.
	 * @return Service returns service entity reference.
	 * @see org.service.repo.entity.Service
	 */
	Service saveService(Service service);

	/**
	 * Method declaration to save deployment manifest.
	 * 
	 * @param deploymentManifest
	 *            holds <code>DeploymentManifest</code> reference.
	 * @return DeploymentManifest returns deployment manifest entity reference.
	 * @see org.service.repo.entity.DeploymentManifest
	 */
	DeploymentManifest saveDeploymentManifest(DeploymentManifest deploymentManifest);

	/**
	 * Method declaration to update task URL.
	 * 
	 * @param uuid
	 *            holds service UUID.
	 * @param url
	 *            holds task URL.
	 * @return ServiceDeployment returns service deployment entity reference.
	 * @see org.service.repo.entity.ServiceDeployment
	 */
	ServiceDeployment updateTaskUrl(String uuid, String url);

	/**
	 * Method declaration to update service status.
	 * 
	 * @param uuid
	 *            holds service UUID.
	 * @param status
	 *            holds service status.
	 * @return Service returns service entity reference.
	 * @see org.service.repo.entity.Service
	 */
	Service updateServiceStatus(String uuid, String status);

	/**
	 * Method declaration to update service properties.
	 * 
	 * @param uuid
	 *            holds service UUID.
	 * @param properties
	 *            holds JSON as Map<String,Object> status.
	 * @return Service returns service entity reference.
	 * @see org.service.repo.entity.Service
	 */
	Service updateServiceProperties(String uuid, Map<String, Object> properties);

	/**
	 * Method declaration to find a service properties.
	 * 
	 * @param uuid
	 *            holds service UUID.
	 * @param key
	 *            holds property key
	 * @return T returns value.
	 */
	<T> T findServiceProperties(String uuid, String key);

	/**
	 * Method declaration to find service by service UUID.
	 * 
	 * @param uuid
	 *            holds service UUID.
	 * @return Service returns service entity reference.
	 * @see org.service.repo.entity.Service
	 */
	Service findByServiceUUID(String uuid);

	/**
	 * Method declaration to find deployment manifest by service UUID.
	 * 
	 * @param uuid
	 *            holds service UUID.
	 * @return DeploymentManifest returns deployment manifest.
	 * @see org.service.repo.entity.DeploymentManifest
	 */
	DeploymentManifest findByDeploymentManifestUUID(String uuid);

	/**
	 * Method declaration to find service deployment by service UUID.
	 * 
	 * @param uuid
	 *            holds service UUID.
	 * @return ServiceDeployment returns service deployment entity reference.
	 * @see org.service.repo.entity.ServiceDeployment
	 */
	ServiceDeployment findByServiceDeploymentUUID(String uuid);

	/**
	 * Method declaration to find service deployment by deployment name.
	 * 
	 * @param name
	 *            holds deployment name.
	 * @return ServiceDeployment returns service deployment entity reference.
	 * @see org.service.repo.entity.ServiceDeployment
	 */
	ServiceDeployment findByServiceDeploymentName(String name);
	

	/**
	 * Method to delete service deployment.
	 * 
	 * @param serviceDeploymentUUID
	 *            holds serviceDeployment UUID.
	 * @return ServiceDeployment returns service deployment entity reference.
	 * @see org.service.repo.entity.ServiceDeployment
	 */
	int deleteByServiceDeploymentUUID(String serviceDeploymentUUID);

	/**
	 * Method to delete service.
	 * 
	 * @param serviceUUID
	 *            holds service UUID.
	 * @return Service returns service entity reference.
	 * @see org.service.repo.entity.Service
	 * */
	int deleteByServiceUUID(String serviceUUID);
}