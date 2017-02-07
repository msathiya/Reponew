package org.service.dao.impl;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.service.dao.ServiceDao;
import org.service.repo.DeploymentManifestRepo;
import org.service.repo.ServiceDeploymentRepo;
import org.service.repo.ServiceRepo;
import org.service.repo.entity.DeploymentManifest;
import org.service.repo.entity.Service;
import org.service.repo.entity.ServiceDeployment;
import org.service.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ServiceDaoImpl implements ServiceDao {

	@Autowired
	private ServiceRepo serviceRepo;

	@Autowired
	private ServiceDeploymentRepo serviceDeploymentRepo;

	@Autowired
	private DeploymentManifestRepo deploymentManifestRepo;

	/**
	 * Method to save service deployment.
	 * 
	 * @param serviceDeployment
	 *            holds <code>ServiceDeployment</code> reference.
	 * @return ServiceDeployment returns service deployment entity reference.
	 * @see org.service.repo.entity.ServiceDeployment
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public ServiceDeployment saveServiceDeployment(ServiceDeployment serviceDeployment) {
		return serviceDeploymentRepo.save(serviceDeployment);
	}

	/**
	 * Method to save service.
	 * 
	 * @param service
	 *            holds <code>Service</code> reference.
	 * @return Service returns service entity reference.
	 * @see org.service.repo.entity.Service
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public Service saveService(Service service) {
		return serviceRepo.save(service);
	}

	/**
	 * Method to save deployment manifest.
	 * 
	 * @param deploymentManifest
	 *            holds <code>DeploymentManifest</code> reference.
	 * @return DeploymentManifest returns deployment manifest entity reference.
	 * @see org.service.repo.entity.DeploymentManifest
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public DeploymentManifest saveDeploymentManifest(DeploymentManifest deploymentManifest) {
		deploymentManifestRepo
				.softDeleteDeploymentManifest(deploymentManifest.getServiceDeployment().getServiceDeploymentUUID());
		return deploymentManifestRepo.save(deploymentManifest);
	}

	/**
	 * Method to save deployment manifest.
	 * 
	 * @param serviceDeploymentUUID
	 *            holds service deployment UUID.
	 * @param manifestYaml
	 *            holds deployment manifest YAML string.
	 * @return DeploymentManifest returns deployment manifest entity reference.
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public DeploymentManifest saveDeploymentManifest(String serviceDeploymentUUID, String manifestYaml) {
		DeploymentManifest deploymentManifest = null;
		if (StringUtils.isNotBlank(manifestYaml)) {
			ServiceDeployment serviceDeployment = serviceDeploymentRepo
					.findServiceDeploymentByUUID(serviceDeploymentUUID);
			if (serviceDeployment != null) {
				// Soft delete existing deployment.
				deploymentManifestRepo.softDeleteDeploymentManifest(serviceDeploymentUUID);
				deploymentManifest = new DeploymentManifest();
				deploymentManifest.setDeploymentYaml(manifestYaml.getBytes(StandardCharsets.UTF_8));
				deploymentManifest.setIsActive(Boolean.TRUE);
				deploymentManifest.setServiceDeployment(serviceDeployment);
				deploymentManifest = deploymentManifestRepo.save(deploymentManifest);
			} else {
				throw new IllegalArgumentException(String.format(
						"Service deployment is not available for service (%s) to save deployment manifest.",
						serviceDeploymentUUID));
			}
		}
		return deploymentManifest;
	}

	/**
	 * Method to update task URL.
	 * 
	 * @param uuid
	 *            holds service UUID.
	 * @param url
	 *            holds task URL.
	 * @return ServiceDeployment returns service deployment entity reference.
	 * @see org.service.repo.entity.ServiceDeployment
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public ServiceDeployment updateTaskUrl(String uuid, String url) {
		ServiceDeployment serviceDeployment = serviceDeploymentRepo.findServiceDeploymentByUUID(uuid);
		if (StringUtils.isNotBlank(url)) {
			serviceDeployment.setTaskUrl(url);
			serviceDeploymentRepo.save(serviceDeployment);
		}
		return serviceDeployment;
	}

	/**
	 * Method to update service status.
	 * 
	 * @param uuid
	 *            holds service UUID.
	 * @param status
	 *            holds service status.
	 * @return Service returns service entity reference.
	 * @see org.service.repo.entity.Service
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public Service updateServiceStatus(String uuid, String status) {
		Service service = serviceRepo.findServiceByUUID(uuid);
		if (StringUtils.isNotBlank(status)) {
			service.setStatus(status);
			serviceRepo.save(service);
		}
		return service;
	}

	/**
	 * Method to find a service properties.
	 * 
	 * @param uuid
	 *            holds service UUID.
	 * @param key
	 *            holds property key
	 * @return T returns value.
	 */
	@Override
	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED)
	public <T> T findServiceProperties(String uuid, String key) {
		Service service = serviceRepo.findServiceByUUID(uuid);
		String propertiesJson = service.getPropertiesJson();
		Map<String, Object> jsonMap = JsonUtil.getJsonMap(propertiesJson);
		T resultReference = null;
		if (jsonMap.containsKey(key)) {
			resultReference = (T) jsonMap.get(key);
		}
		return resultReference;
	}

	/**
	 * Method to update service properties.
	 * 
	 * @param uuid
	 *            holds service UUID.
	 * @param properties
	 *            holds JSON as Map<String,Object> status.
	 * @return Service returns service entity reference.
	 * @see org.service.repo.entity.Service
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public Service updateServiceProperties(String uuid, Map<String, Object> properties) {
		Service service = serviceRepo.findServiceByUUID(uuid);
		if (MapUtils.isNotEmpty(properties)) {
			service.setPropertiesJson(JsonUtil.getJsonString(properties).getBytes(StandardCharsets.UTF_8));
			serviceRepo.save(service);
		}
		return service;
	}

	/**
	 * Method to find service by service UUID.
	 * 
	 * @param uuid
	 *            holds service UUID.
	 * @return Service returns service entity reference.
	 * @see org.service.repo.entity.Service
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public Service findByServiceUUID(String uuid) {

		return serviceRepo.findServiceByUUID(uuid);
	}

	/**
	 * Method to find deployment manifest by service UUID.
	 * 
	 * @param uuid
	 *            holds service UUID.
	 * @return DeploymentManifest returns deployment manifest.
	 * @see org.service.repo.entity.DeploymentManifest
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public ServiceDeployment findByServiceDeploymentUUID(String uuid) {

		return serviceDeploymentRepo.findServiceDeploymentByUUID(uuid);
	}

	/**
	 * Method to find service deployment by service UUID.
	 * 
	 * @param uuid
	 *            holds service UUID.
	 * @return ServiceDeployment returns service deployment entity reference.
	 * @see org.service.repo.entity.ServiceDeployment
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public ServiceDeployment findByServiceDeploymentName(String name) {

		return serviceDeploymentRepo.findServiceDeploymentByName(name);
	}

	/**
	 * Method to find service deployment by deployment name.
	 * 
	 * @param name
	 *            holds deployment name.
	 * @return ServiceDeployment returns service deployment entity reference.
	 * @see org.service.repo.entity.ServiceDeployment
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public DeploymentManifest findByDeploymentManifestUUID(String uuid) {

		return deploymentManifestRepo.findDeploymentManifestByUUID(uuid);
	}
	

	/**
	 * Method to delete service deployment.
	 * 
	 * @param serviceDeploymentUUID
	 *            holds serviceDeployment UUID.
	 * @return ServiceDeployment returns service deployment entity reference.
	 * @see org.service.repo.entity.ServiceDeployment
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public int deleteByServiceDeploymentUUID(String serviceDeploymentUUID) {
		deploymentManifestRepo.deleteDeploymentManifest(serviceDeploymentUUID);
		return serviceDeploymentRepo.deleteServiceDeployment(serviceDeploymentUUID);
	}

	/**
	 * Method to delete service.
	 * 
	 * @param serviceUUID
	 *            holds service UUID.
	 * @return Service returns service entity reference.
	 * @see org.service.repo.entity.Service
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public int deleteByServiceUUID(String serviceUUID) {
		return serviceRepo.deleteService(serviceUUID);
	}

}
