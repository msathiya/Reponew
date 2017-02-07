package org.service.resource;

import static org.service.data.ServiceConstant.CONTEXTPATHPREFIX;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.service.dao.ServiceDao;
import org.service.dao.ServiceVmProcessDao;
import org.service.data.ServiceEnvironment;
import org.service.repo.entity.ServiceVmProcess;
import org.service.repo.entity.ServiceVmProcessPk;
import org.service.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The <code>ProcessResource</code> class exposes process information.
 * 
 * @author Anupriya (anupriya.gangadharan@cognizant.com)
 */
@RestController
public class ProcessResource {

	/**
	 * Holds LOGGER reference.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ProcessResource.class);

	/**
	 * Holds DBEnvironment reference.
	 */
	@Autowired
	private ServiceEnvironment environment;

	@Autowired
	private ServiceDao serviceDao;

	@Autowired
	private ServiceVmProcessDao serviceVmProcessDao;


	
	/**
	 * This method allows to save vm process details db.
	 * 
	 * 
	 * @param serviceUUID
	 *            holds service UUID.
	 * @param processName
	 *            holds processName reference.
	 * @return ResponseEntity<String> returns HTTP response of application/json
	 *         content type.
	 */
	@RequestMapping(value = CONTEXTPATHPREFIX
			+ "/process/{serviceUUID}/{processName}", method = POST, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<String> saveVmProcess(@PathVariable String serviceUUID, @PathVariable String processName,
			@RequestBody String processJSON) {

		ResponseEntity<String> response = null;

		try {

			LOGGER.info("Scheduler process JSON - {}" + processJSON);
			Map<String, Object> jsonMap = JsonUtil.getJsonMap(processJSON);
			ServiceVmProcess serviceVmProcess = new ServiceVmProcess();
			serviceVmProcess.setServiceVmProcessPk(new ServiceVmProcessPk(jsonMap.get("SERVICE_UUID").toString(),
					jsonMap.get("PROCESS_NAME").toString()));
			serviceVmProcess.setCallbackUrl(jsonMap.get("CALLBACK_URL").toString());
			serviceVmProcess.setStatus("running");
			serviceVmProcess.setCronExpression(jsonMap.get("CRON_EXPRESSION").toString());

			serviceVmProcessDao.saveServiceVmProcess(serviceVmProcess);
			jsonMap.remove("SERVICE_UUID");
			response = new ResponseEntity<String>(JsonUtil.getJsonString(jsonMap), OK);

		} catch (Exception exception) {
			LOGGER.error("Exception in saveVmProcess", exception);
			Map<String, Object> serviceVmProcessMap = new HashMap<String, Object>();
			serviceVmProcessMap.put("PROCESS_NAME", processName);
			serviceVmProcessMap.put("STATUS", "failed");
			response = new ResponseEntity<String>(JsonUtil.getJsonString(serviceVmProcessMap), INTERNAL_SERVER_ERROR);
		}

		return response;

	}

	/**
	 * This method allows to get particular process details from DB.
	 * 
	 * @param serviceUUID
	 *            holds service UUID.
	 * @param processName
	 *            holds processName reference.
	 * @return ResponseEntity<String> returns HTTP response of application/json
	 *         content type.
	 */
	@RequestMapping(value = CONTEXTPATHPREFIX
			+ "/process/{serviceUUID}/{processName}", method = GET, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getVmProcess(@PathVariable String serviceUUID, @PathVariable String processName) {

		ResponseEntity<String> response = null;
		try {
			LOGGER.info("Scheduler process name, uuid - {}" + processName, serviceUUID);
			ServiceVmProcess serviceVmProcess = serviceVmProcessDao.findByProcessName(serviceUUID, processName);
			Map<String, Object> serviceVmProcessMap = new HashMap<String, Object>();
			serviceVmProcessMap.put("PROCESS_NAME", serviceVmProcess.getServiceVmProcessPk().getProcessName());
			serviceVmProcessMap.put("CALLBACK_URL", serviceVmProcess.getCallbackUrl());
			serviceVmProcessMap.put("CRON_EXPRESSION", serviceVmProcess.getCronExpression());
			serviceVmProcessMap.put("STATUS", serviceVmProcess.getStatus());
			response = new ResponseEntity<String>(JsonUtil.getJsonString(serviceVmProcessMap), OK);

		} catch (Exception exception) {
			LOGGER.error("Exception in getVmProcess", exception);
			Map<String, Object> serviceVmProcessMap = new HashMap<String, Object>();
			serviceVmProcessMap.put("PROCESS_NAME", processName);
			serviceVmProcessMap.put("STATUS", "failed");
			response = new ResponseEntity<String>(JsonUtil.getJsonString(serviceVmProcessMap), INTERNAL_SERVER_ERROR);
		}
		return response;

	}

	/**
	 * This method allows to get all running process in vm from db.
	 * 
	 * @param serviceUUID
	 *            holds service UUID.
	 * @return ResponseEntity<String> returns HTTP response of application/json
	 *         content type.
	 */
	@RequestMapping(value = CONTEXTPATHPREFIX
			+ "/process/{serviceUUID}", method = GET, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getAllVmProcess(@PathVariable String serviceUUID) {

		ResponseEntity<String> response = null;
		try {

			LOGGER.info("Scheduler service uuid - {}" + serviceUUID);
			List<ServiceVmProcess> serviceVmProcessList = serviceVmProcessDao.findByServiceUUID(serviceUUID);

			List<Map<String, Object>> serviceVmProcesMapList = new ArrayList<Map<String, Object>>();
			for (ServiceVmProcess serviceVmProcess : serviceVmProcessList) {
				Map<String, Object> serviceVmProcessMap = new HashMap<String, Object>();
				serviceVmProcessMap.put("PROCESS_NAME", serviceVmProcess.getServiceVmProcessPk().getProcessName());
				serviceVmProcessMap.put("CALLBACK_URL", serviceVmProcess.getCallbackUrl());
				serviceVmProcessMap.put("CRON_EXPRESSION", serviceVmProcess.getCronExpression());
				serviceVmProcessMap.put("STATUS", serviceVmProcess.getStatus());
				serviceVmProcesMapList.add(serviceVmProcessMap);
			}

			response = new ResponseEntity<String>(JsonUtil.getJsonString(serviceVmProcesMapList), OK);

		} catch (Exception exception) {
			LOGGER.error("Exception in getAllVmProcess", exception);
			Map<String, Object> serviceVmProcessMap = new HashMap<String, Object>();
			serviceVmProcessMap.put("STATUS", "failed");
			response = new ResponseEntity<String>(JsonUtil.getJsonString(serviceVmProcessMap), INTERNAL_SERVER_ERROR);
		}
		return response;

	}

	/**
	 * This method allows to get all running process in vm from db.
	 * 
	 * @param serviceUUID
	 *            holds service UUID.
	 * @return ResponseEntity<String> returns HTTP response of application/json
	 *         content type.
	 */
	@RequestMapping(value = CONTEXTPATHPREFIX
			+ "/process/{serviceUUID}/{processName}", method = PUT, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<String> updateVmProcess(@PathVariable String serviceUUID, @PathVariable String processName,
			@RequestBody String processJSON) {

		ResponseEntity<String> response = null;
		try {

			LOGGER.info("Scheduler service uuid - {}" + serviceUUID);
			Map<String, Object> jsonMap = JsonUtil.getJsonMap(processJSON);

			ServiceVmProcess serviceVmProcess = serviceVmProcessDao.findByProcessName(serviceUUID, processName);
			serviceVmProcess.setServiceVmProcessPk(new ServiceVmProcessPk(jsonMap.get("SERVICE_UUID").toString(),
					jsonMap.get("PROCESS_NAME").toString()));
			serviceVmProcess.setCallbackUrl(jsonMap.get("CALLBACK_URL").toString());
			serviceVmProcess.setStatus("running");
			serviceVmProcess.setCronExpression(jsonMap.get("CRON_EXPRESSION").toString());
			serviceVmProcessDao.saveServiceVmProcess(serviceVmProcess);

			jsonMap.remove("SERVICE_UUID");
			response = new ResponseEntity<String>(JsonUtil.getJsonString(jsonMap), OK);

		} catch (Exception exception) {
			LOGGER.error("Exception in verticalScale", exception);
			Map<String, Object> serviceVmProcessMap = new HashMap<String, Object>();
			serviceVmProcessMap.put("PROCESS_NAME", processName);
			serviceVmProcessMap.put("STATUS", "failed");
			response = new ResponseEntity<String>(JsonUtil.getJsonString(serviceVmProcessMap), INTERNAL_SERVER_ERROR);
		}
		return response;

	}

	/**
	 * This method allows to get all running process in vm from db.
	 * 
	 * @param serviceUUID
	 *            holds service UUID.
	 * @return ResponseEntity<String> returns HTTP response of application/json
	 *         content type.
	 */
	@RequestMapping(value = CONTEXTPATHPREFIX
			+ "/process/{serviceUUID}/{processName}", method = DELETE, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<String> deleteVmProcess(@PathVariable String serviceUUID, @PathVariable String processName) {

		ResponseEntity<String> response = null;
		try {

			LOGGER.info("Scheduler service uuid - {}" + serviceUUID);

			serviceVmProcessDao.deleteByProcessName(serviceUUID, processName);

			Map<String, Object> serviceVmProcessMap = new HashMap<String, Object>();
			serviceVmProcessMap.put("PROCESS_NAME", processName);
			serviceVmProcessMap.put("STATUS", "success");
			response = new ResponseEntity<String>(JsonUtil.getJsonString(serviceVmProcessMap), OK);

		} catch (Exception exception) {
			LOGGER.error("Exception in verticalScale", exception);
			Map<String, Object> serviceVmProcessMap = new HashMap<String, Object>();
			serviceVmProcessMap.put("PROCESS_NAME", processName);
			serviceVmProcessMap.put("STATUS", "failed");
			response = new ResponseEntity<String>(JsonUtil.getJsonString(serviceVmProcessMap), INTERNAL_SERVER_ERROR);
		}
		return response;

	}

}