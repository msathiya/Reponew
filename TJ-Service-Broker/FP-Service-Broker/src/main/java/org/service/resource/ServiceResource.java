package org.service.resource;

import static org.apache.http.HttpHeaders.AUTHORIZATION;
import static org.apache.http.HttpHeaders.CONTENT_TYPE;
import static org.service.data.ServiceConstant.COLON;
import static org.service.data.ServiceConstant.CONTEXTPATHPREFIX;
import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.service.Scaling;
import org.service.bosh.util.BoshRestUtil;
import org.service.bosh.util.BoshUtil;
import org.service.dao.ServiceDao;
import org.service.dao.SubnetIpAddressDao;
import org.service.data.ServiceEnvironment;
import org.service.json.HorizontalScale;
import org.service.json.VerticalScale;
import org.service.repo.entity.Service;
import org.service.util.JsonUtil;
import org.service.util.RestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The <code>DBServiceResources</code> class exposes other dependent services
 * like scaling.
 * 
 * @author Sandeep (sandeep.visvanathan@cognizant.com)
 * @author Deepthi (deepthi.g2@cognizant.com)
 * @author Sundar (sundarajan.srinivasan@cognizant.com)
 * @author Ramkumar(ramkumar.kandhakumar@cognizant.com)
 */
@RestController
public class ServiceResource {

	/**
	 * Holds LOGGER reference.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ServiceResource.class);

	/**
	 * Holds DBEnvironment reference.
	 */
	@Autowired
	private ServiceEnvironment environment;

	@Autowired
	private ServiceDao serviceDao;

	/**
	 * Holds Scaling reference.
	 */
	@Autowired
	private Scaling scaling;

	@Autowired
	private SubnetIpAddressDao subnetIpAddressDao;

	/**
	 * This method allows vertical scaling of the instance by re-deploying the
	 * YML file.
	 * 
	 * @param serviceUUID
	 *            holds service UUID.
	 * @param verticalScale
	 *            holds vertical scale reference.
	 * @return ResponseEntity<String> returns HTTP response of application/json
	 *         content type.
	 */
	@RequestMapping(value = CONTEXTPATHPREFIX
			+ "/verticalScale/{serviceUUID}", method = PUT, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<String> verticalScale(@PathVariable String serviceUUID,
			@RequestBody VerticalScale verticalScale) {

		ResponseEntity<String> response = null;
		try {

			Service service = serviceDao.findByServiceUUID(serviceUUID);

			if (service != null) {
				String serviceDeploymentUUID = service.getServiceDeployment().getServiceDeploymentUUID();

				// Prepare BOSH request
				Map<String, String> boshRequest = new HashMap<String, String>();
				boshRequest.put("name", service.getServiceDeployment().getDeploymentName());
				boshRequest.put("boshProtocol", environment.getBoshProtocol());
				boshRequest.put("boshUrl", StringUtils.join(environment.getBoshIpAddress(), COLON,
						String.valueOf(environment.getBoshPort())));
				boshRequest.put("Authorization", environment.getBoshAuthorization());

				/*
				 * Get the deployment details from the given deployment name if
				 * it exits in BOSH
				 */
				boshRequest.put("boshDeploymentPath", "/deployments");
				String deploymentDetails = BoshRestUtil.getDeploymentDetails(boshRequest);
				List<Map<String, Object>> deployments = JsonUtil.getJsonListMap(deploymentDetails);
				Boolean deploymentExist = BoshUtil.checkDeploymentAvailability(deployments, boshRequest);

				// If deployment exists,scaling is done.
				if (deploymentExist == true) {

					/* Get the manifest string from the deployment response */
					String manifestYaml = scaling.verticalScale(
							serviceDao.findByDeploymentManifestUUID(serviceDeploymentUUID).getDeploymentYaml(),
							verticalScale);

					/*
					 * Deploys the updated manifest file to change the VM type.
					 */
					serviceDao.saveDeploymentManifest(serviceDeploymentUUID, manifestYaml);
					boshRequest.put("boshDeploymentPath", "/deployments");
					String taskUrl = BoshRestUtil.postBoshDeployment(boshRequest, manifestYaml);
					serviceDao.updateTaskUrl(serviceDeploymentUUID, taskUrl);

					// Success response.
					Map<String, Object> message = new HashMap<String, Object>();
					message.put("status", "PROCESSING");
					MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
					headers.add(CONTENT_TYPE, APPLICATION_JSON_VALUE);
					response = new ResponseEntity<String>(JsonUtil.getJsonString(message), headers, ACCEPTED);

				} else {

					// Deployment does not exist response.
					Map<String, Object> message = new HashMap<String, Object>();
					message.put("status", "Deployment does not exists");
					MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
					headers.add(CONTENT_TYPE, APPLICATION_JSON_VALUE);
					response = new ResponseEntity<String>(JsonUtil.getJsonString(message), headers, OK);

				}
			} else {
				response = new ResponseEntity<String>(String.format("No service found with UUID, %s", serviceUUID),
						SERVICE_UNAVAILABLE);
			}
		} catch (Exception exception) {
			LOGGER.error("Exception in verticalScale", exception);
			response = new ResponseEntity<String>(INTERNAL_SERVER_ERROR);
		}
		return response;

	}

	/**
	 * 
	 * This method allows horizontal scaling of the instance by re-deploying the
	 * YML file.
	 * 
	 * @param serviceUUID
	 *            holds service UUID.
	 * @param horizontalScale
	 *            holds horizontal scale reference.
	 * @return ResponseEntity<String> returns HTTP response of application/json
	 *         content type.
	 */
	@RequestMapping(value = CONTEXTPATHPREFIX
			+ "/horizontalScale/{serviceUUID}", method = PUT, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<String> horizontalScale(@PathVariable String serviceUUID,
			@RequestBody HorizontalScale horizontalScale) {

		ResponseEntity<String> response = null;
		try {
			Service service = serviceDao.findByServiceUUID(serviceUUID);

			if (service != null) {
				String serviceDeploymentUUID = service.getServiceDeployment().getServiceDeploymentUUID();
				LOGGER.debug("HorizontalScale- jobName - {}", horizontalScale.getJobName());
				LOGGER.debug("HorizontalScale- noOfInstances - {}", horizontalScale.getNoOfInstances());
				// Prepare BOSH request
				Map<String, String> boshRequest = new HashMap<String, String>();
				boshRequest.put("name", service.getServiceDeployment().getDeploymentName());
				boshRequest.put("boshProtocol", environment.getBoshProtocol());
				boshRequest.put("boshUrl", StringUtils.join(environment.getBoshIpAddress(), COLON,
						String.valueOf(environment.getBoshPort())));
				boshRequest.put("Authorization", environment.getBoshAuthorization());

				/*
				 * Get the deployment details from the given deployment name if
				 * it exits in BOSH
				 */
				boshRequest.put("boshDeploymentPath", "/deployments");
				String deploymentDetails = BoshRestUtil.getDeploymentDetails(boshRequest);
				List<Map<String, Object>> deployments = JsonUtil.getJsonListMap(deploymentDetails);
				Boolean deploymentExist = BoshUtil.checkDeploymentAvailability(deployments, boshRequest);

				// If deployment exists,scaling is done.
				if (deploymentExist == true) {

					/* Get the manifest string from the deployment response */
					horizontalScale = scaling.horizontalScale(
							serviceDao.findByDeploymentManifestUUID(serviceDeploymentUUID).getDeploymentYaml(),
							horizontalScale);

					/*
					 * Deploys the updated manifest file to change the VM type.
					 */
					serviceDao.saveDeploymentManifest(serviceDeploymentUUID, horizontalScale.getYaml());
					boshRequest.put("boshDeploymentPath", "/deployments");
					String taskUrl = BoshRestUtil.postBoshDeployment(boshRequest, horizontalScale.getYaml());
					LOGGER.info("Task URL - {}" + taskUrl);
					serviceDao.updateTaskUrl(serviceDeploymentUUID, taskUrl);

					// Reset IP address from IP table.

					// Success response.
					Map<String, Object> message = new HashMap<String, Object>();
					message.put("status", "PROCESSING");

					if (horizontalScale.getType().equals(HorizontalScale.SCALE_IN)) {
						subnetIpAddressDao.resetIpAddressList(horizontalScale.getIpAddress());
					}
					MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
					headers.add(CONTENT_TYPE, APPLICATION_JSON_VALUE);
					response = new ResponseEntity<String>(JsonUtil.getJsonString(message), headers, ACCEPTED);

				} else {

					// Deployment does not exist response.
					Map<String, Object> message = new HashMap<String, Object>();
					message.put("status", "Deployment does not exists");
					MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
					headers.add(CONTENT_TYPE, APPLICATION_JSON_VALUE);
					response = new ResponseEntity<String>(JsonUtil.getJsonString(message), headers, OK);

				}
			} else {
				response = new ResponseEntity<String>(String.format("No service found with UUID, %s", serviceUUID),
						SERVICE_UNAVAILABLE);
			}
		} catch (Exception exception) {
			LOGGER.error("Exception in horizontalScale", exception);
			response = new ResponseEntity<String>(INTERNAL_SERVER_ERROR);
		}
		return response;

	}

	/**
	 * 
	 * This method gets the status of the deployment in the BOSH from task URL.
	 * 
	 * @param serviceUUID
	 *            holds service UUID.
	 * @return String returns <code>String</code> deployment status.
	 * @return ResponseEntity<String> returns HTTP response of application/json
	 *         content type.
	 */
	@RequestMapping(value = CONTEXTPATHPREFIX
			+ "/getTaskStatus/{serviceUUID}", method = GET, produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<String> taskStatus(@PathVariable String serviceUUID) {

		/*
		 * Get deployment status ASYNC call expected input.
		 */
		ResponseEntity<String> response = null;

		try {
			Service service = serviceDao.findByServiceUUID(serviceUUID);

			if (service != null) {

				// Checks if the deployment exists in BOSH.
				Map<String, String> provisioningInput = new HashMap<String, String>();
				provisioningInput.put("name", service.getServiceDeployment().getDeploymentName());
				String host = environment.getBoshIpAddress() + ":" + environment.getBoshPort();
				provisioningInput.put("boshProtocol", environment.getBoshProtocol());
				provisioningInput.put("boshUrl", host);
				provisioningInput.put("boshDeploymentPath", "/deployments");
				provisioningInput.put("Authorization", environment.getBoshAuthorization());

				String deploymentDetails = BoshRestUtil.getDeploymentDetails(provisioningInput);
				List<Map<String, Object>> deployments = JsonUtil.getJsonListMap(deploymentDetails);
				Boolean deploymentExist = BoshUtil.checkDeploymentAvailability(deployments, provisioningInput);

				// If the deployment exists, details of the deployment are
				// returned.
				if (deploymentExist == true) {

					String path = "/tasks?deployment=" + service.getServiceDeployment().getDeploymentName();
					String uri = environment.getBoshProtocol() + host + path;
					Map<String, String> httpDetail = new HashMap<String, String>();
					httpDetail.put(AUTHORIZATION, environment.getBoshAuthorization());
					String taskDetail = RestUtil.gethttps(uri, httpDetail);

					MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
					headers.add(CONTENT_TYPE, APPLICATION_JSON_VALUE);
					response = new ResponseEntity<String>(taskDetail, headers, OK);
				} else {

					// Deployment does not exist response.
					Map<String, Object> message = new HashMap<String, Object>();
					message.put("status", "Deployment does not exists");
					MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
					headers.add(CONTENT_TYPE, APPLICATION_JSON_VALUE);
					response = new ResponseEntity<String>(JsonUtil.getJsonString(message), headers, OK);
				}
			} else {
				response = new ResponseEntity<String>(String.format("No service found with UUID, %s", serviceUUID),
						SERVICE_UNAVAILABLE);
			}
		} catch (Exception exception) {
			LOGGER.error("Exception in get status task", exception);
			response = new ResponseEntity<String>(INTERNAL_SERVER_ERROR);
		}
		return response;
	}

}