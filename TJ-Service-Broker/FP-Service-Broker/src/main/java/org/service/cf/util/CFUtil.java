package org.service.cf.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.service.broker.exception.ServiceException;
import org.service.data.ServiceEnvironment;
import org.service.repo.entity.Service;
import org.service.resource.ServiceResource;
import org.service.util.FileUtil;
import org.service.util.JsonUtil;
import org.service.util.RestUtil;
import org.service.util.XPathUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.servicebroker.model.OperationState;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;

public final class CFUtil {

	/**
	 * Holds LOGGER reference.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ServiceResource.class);
	private static String ip;
	private static String port;
	private static String username;
	private static String password;

	/**
	 * Holds DBEnvironment reference.
	 */
	private CFUtil() {
		// Helper class constructor.
	}

	public static String login(ServiceEnvironment environment) throws ServiceException {
		LOGGER.debug("Calling CF login API");
		String login_response = null;
		try {
			Map<String, String> httpDetail = new HashMap<String, String>();
			httpDetail.put(HttpHeaders.AUTHORIZATION, "Basic Y2Y6");
			httpDetail.put(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
			httpDetail.put(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_UTF8_VALUE);
			LOGGER.info(String.format("Login URL - %s", environment.getCfLoginUrl()));
			LOGGER.info(String.format("Login https url - %s", environment.getCfHttpsUrl()));
			login_response = RestUtil.posthttps(environment.getCfLoginUrl(), httpDetail, StringUtils.EMPTY);
			LOGGER.info(String.format("Login response - %s", login_response));
		} catch (Exception exception) {
			throw new ServiceException("Exception in CFLogin.", exception);
		}
		return JsonUtil.getJsonMap(login_response).get("access_token").toString();
	}

	public static String login(String loginUrl, ServiceEnvironment environment) throws ServiceException {
		LOGGER.debug("Calling CF login API");
		String login_response = null;
		try {
			Map<String, String> httpDetail = new HashMap<String, String>();
			httpDetail.put(HttpHeaders.AUTHORIZATION, "Basic Y2Y6");
			httpDetail.put(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
			httpDetail.put(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_UTF8_VALUE);
			LOGGER.info(String.format("Login URL - %s", loginUrl));
			login_response = RestUtil.posthttps(loginUrl, httpDetail, StringUtils.EMPTY);
			LOGGER.info(String.format("Login response - %s", login_response));
		} catch (Exception exception) {
			throw new ServiceException("Exception in CFLogin.", exception);
		}
		return JsonUtil.getJsonMap(login_response).get("access_token").toString();
	}

	public static void deployApp(String app_name, ServiceEnvironment environment, File filetoupload, boolean isStart,
			Map<String, Object> map, String environment_json) throws ServiceException {
		try {

			String authToken = login(environment);
			// String app_name = "cfbot";
			String space_guid = space(authToken, environment);
			LOGGER.info(String.format("space_guid - %s.", space_guid));
			// Create Cups
			String upsguid = createCUPS(app_name, space_guid, map, environment, authToken);
			LOGGER.info(String.format("upsguid - %s.", upsguid));
			// create apps
			String appsummary = createApp(authToken, space_guid, app_name, environment);
			LOGGER.info(String.format("appsummary - %s.", appsummary));
			JSONObject appdetail = jsonparser(appsummary);
			String app_guid = (String) appdetail.get("app_guid");
			String upload = uploadApplication(authToken, app_guid, filetoupload.getAbsolutePath().toString(),
					environment);
			LOGGER.info(String.format("uploadApplication - %s.", upload));
			if (isStart) {
				// String startApp = startApplication(authToken, app_guid,
				// environment, true);
				String bindService = bindServicetoApp(authToken, space_guid, app_guid, upsguid, environment);
				LOGGER.info(String.format("BindApplication - %s.", bindService));
				String startApp = startApplicationWithEnv(authToken, app_guid, environment_json, environment);
				LOGGER.info(String.format("startApplication - %s.", startApp));
				appStatus(authToken, app_guid, environment);
			}

		} catch (Exception exception) {
			throw new ServiceException("Exception in Deploy App.", exception);
		}
	}

	@SuppressWarnings("unchecked")
	public static void redeployApp(String app_name, ServiceEnvironment environment, File filetoupload,
			Map<String, Object> map, String appbindguid, boolean issandbox, String serviceBindingGUID)
			throws ServiceException {
		try {
			String authToken = login(environment);
			String space_guid = space(authToken, environment);
			LOGGER.info(String.format("space_guid - %s.", space_guid));
			String serviceGuid = getUPSGUID(authToken, app_name, environment);
			// Create Cups
			String appsummary = getAppsGUID(authToken, app_name, environment);
			String appbindname = getAppsSummary(authToken, appbindguid, environment);
			LOGGER.info(String.format("appbindname - %s.", appbindname));
			LOGGER.info(String.format("appsummary - %s.", appsummary));
			if (appsummary != null) {
				JSONObject appsObj = new JSONObject();
				JSONObject appdetail = jsonparser(appsummary);
				String appsguid = (String) appdetail.get("guid");
				JSONObject environment_json = (JSONObject) appdetail.get("environment_json");
				if (environment_json != null) {
					LOGGER.info(String.format("environment_json - %s.", environment_json.toString()));
					if (issandbox) {
						if (environment_json.get("APPS") == null) {
							appsObj.put("APPS", appbindname);
						} else {
							appsObj.put("APPS", environment_json.get("APPS") + " " + appbindname);
						}
					} else {
						appsObj.put("APPS", appbindname);
					}
				}
				deleteService(appsummary, serviceGuid, serviceBindingGUID, environment, authToken);
				deployApp(app_name, environment, filetoupload, true, map, appsObj.toString());
			} else {
				LOGGER.info(String.format("Cannot able to find apps Guid "));
			}
		} catch (Exception exception) {
			throw new ServiceException("Exception in Deploy App.", exception);
		}
	}
	//

	public static void deleteServiceBinding(String authToken, String app_name, ServiceEnvironment environment)
			throws ServiceException {

		String appsummary = getAppsGUID(authToken, app_name, environment);

		if (appsummary != null) {
			// JSONObject appsObj = new JSONObject();
			JSONObject appdetail = jsonparser(appsummary);
			String appsguid = (String) appdetail.get("guid");
			JSONObject environment_json = (JSONObject) appdetail.get("environment_json");
			LOGGER.info(String.format("environment_json - %s.", environment_json.toString()));

			if (environment_json.get("APPS").equals(app_name)) {

				startApplication(authToken, appsguid, environment, false);
			}
		}
	}

	public static String space(String authToken, ServiceEnvironment environment) throws ServiceException {
		LOGGER.debug("Calling CF Space API");
		String space_response = null;

		String space = environment.getCfSpaceName();// "platform-prod";
		// String space = "DEV";
		JSONObject parsedvalue = null;
		String space_guid = null;
		try {

			HashMap<String, String> httpDetail = form_headers(authToken);
			space_response = RestUtil.gethttps(environment.getCfHttpsUrl() + "/v2/spaces", httpDetail);
			parsedvalue = jsonparser(space_response);
			int resources = ((JSONArray) parsedvalue.get("resources")).size();
			LOGGER.info(String.format("resources length - %s", resources));
			LOGGER.info(String.format("space name - %s", space));
			for (int i = 0; i < resources; i++) {
				JSONObject item = (JSONObject) ((JSONArray) parsedvalue.get("resources")).get(i);
				JSONObject entity = (JSONObject) item.get("entity");
				String compare = (String) entity.get("name");
				LOGGER.info(String.format("Space_NAME : - %s.", compare));
				if (compare.equalsIgnoreCase(space)) {
					space_guid = (String) ((JSONObject) item.get("metadata")).get("guid");
					LOGGER.info(String.format("Space GUID of SPACE : - %s  GUID : - %s", space, space_guid));
				}
			}
		} catch (Exception e) {
			String error_code=(String) parsedvalue.get("code");
			String description=(String) parsedvalue.get("description");
			if(error_code.equalsIgnoreCase("1000")&& description.equalsIgnoreCase("Invalid Auth Token"))
					{
				String refresh_authtoken=login(environment);
				space(refresh_authtoken,environment);
					}
			throw new ServiceException("Exception in cf space api.", e);
		}
		return space_guid;
	}

	public static String space(String authToken, ServiceEnvironment environment, String space) throws ServiceException {
		LOGGER.debug("Calling CF Space API");
		String space_response = null;

		// String space = environment.getCfSpaceName();// "platform-prod";
		JSONObject parsedvalue = null;
		String space_guid = null;
		try {

			HashMap<String, String> httpDetail = form_headers(authToken);
			space_response = RestUtil.gethttps(environment.getCfHttpsUrl() + "/v2/spaces", httpDetail);
			parsedvalue = jsonparser(space_response);
			int resources = ((JSONArray) parsedvalue.get("resources")).size();
			LOGGER.info(String.format("resources length - %s", resources));
			LOGGER.info(String.format("space name - %s", space));
			for (int i = 0; i < resources; i++) {
				JSONObject item = (JSONObject) ((JSONArray) parsedvalue.get("resources")).get(i);
				JSONObject entity = (JSONObject) item.get("entity");
				String compare = (String) entity.get("name");
				LOGGER.info(String.format("Space_NAME : - %s.", compare));
				if (compare.equalsIgnoreCase(space)) {
					space_guid = (String) ((JSONObject) item.get("metadata")).get("guid");
					LOGGER.info(String.format("Space GUID of SPACE : - %s  GUID : - %s", space, space_guid));
				}
			}
		} catch (Exception e) {
			throw new ServiceException("Exception in cf space api.", e);
		}
		return space_guid;
	}

	@SuppressWarnings("unchecked")
	public static String createApp(String authToken, String space_guid, String app_name, ServiceEnvironment environment)
			throws ServiceException {

		String createApp_response = null;
		JSONObject parsedvalue = null;
		JSONObject app_summary = new JSONObject();
		JSONObject postJSON = new JSONObject();
		postJSON.put("name", app_name);
		postJSON.put("space_guid", space_guid);
		postJSON.put("memory", 512);
		try {
			HashMap<String, String> httpDetail = form_headers(authToken);
			createApp_response = RestUtil.posthttps(environment.getCfHttpsUrl() + "/v2/apps", httpDetail,
					postJSON.toString());
			parsedvalue = jsonparser(createApp_response);

			LOGGER.info(String.format("Create App Response : - %s", parsedvalue));
			JSONObject temp = (JSONObject) parsedvalue.get("metadata");
			LOGGER.info(String.format("temp - %s", temp.get("guid")));
			Object app_guid = temp.get("guid");
			app_summary.put("app_guid", app_guid);
			app_summary.put("access_token", authToken);
			app_summary.put("app_name", app_name);
			app_summary.put("space_guid", space_guid);
			app_summary.put("bind_services", (String) parsedvalue.get("bind_services"));
			LOGGER.info(String.format("createApp_response - %s", createApp_response));
			LOGGER.info(String.format("app_summary - %s", app_summary));
		} catch (Exception e) {
			
			String error_code=(String) parsedvalue.get("code");
			String description=(String) parsedvalue.get("description");
			if(error_code.equalsIgnoreCase("1000")&& description.equalsIgnoreCase("Invalid Auth Token"))
					{
				String refersh_authtoken=login(environment);
				String appsummary = createApp(refersh_authtoken, space_guid, app_name, environment);
				LOGGER.info(String.format("appsummary - %s.", appsummary));
		}
		}
		return app_summary.toString();
	}

	public static String restageApp(String authToken, String app_guid, ServiceEnvironment environment)
			throws ServiceException {

		String createApp_response = null;
		JSONObject parsedvalue = null;
		try {
			HashMap<String, String> httpDetail = form_headers(authToken);
			createApp_response = RestUtil.posthttps(environment.getCfHttpsUrl() + "/v2/apps/" + app_guid + "/restage",
					httpDetail, StringUtils.EMPTY);
			parsedvalue = jsonparser(createApp_response);
			LOGGER.info(String.format("restageApp Response : - %s", parsedvalue));
		} catch (Exception e) {
			String error_code=(String) parsedvalue.get("code");
			String description=(String) parsedvalue.get("description");
			if(error_code.equalsIgnoreCase("1000")&& description.equalsIgnoreCase("Invalid Auth Token"))
					{
				String refersh_authtoken=login(environment);
				String restage = restageApp(refersh_authtoken, app_guid, environment);
				LOGGER.info(String.format("restage - %s.", restage));
		}
		}

		return parsedvalue.toString();

	}

	@SuppressWarnings("unchecked")
	public static String bindServicetoApp(String authToken, String space_guid, String app_guid,
			String service_instance_guid, ServiceEnvironment environment) throws ServiceException {

		String createApp_response = null;
		JSONObject parsedvalue = null;

		JSONObject postJSON = new JSONObject();
		postJSON.put("service_instance_guid", service_instance_guid);
		postJSON.put("app_guid", app_guid);

		try {
			HashMap<String, String> httpDetail = form_headers(authToken);
			createApp_response = RestUtil.posthttps(environment.getCfHttpsUrl() + "/v2/service_bindings", httpDetail,
					postJSON.toString());
			parsedvalue = jsonparser(createApp_response);
			LOGGER.info(String.format("service Binding Response : - %s", parsedvalue));
		} catch (Exception e) {
			String error_code=(String) parsedvalue.get("code");
			String description=(String) parsedvalue.get("description");
			if(error_code.equalsIgnoreCase("1000")&& description.equalsIgnoreCase("Invalid Auth Token"))
					{
				String refersh_authtoken=login(environment);
				String bindService = bindServicetoApp(refersh_authtoken, space_guid, app_guid, service_instance_guid, environment);
				LOGGER.info(String.format("BindApplication - %s.", bindService));
			
		}
		}
		return parsedvalue.toString();

	}

	public static String getDomains(String authToken, ServiceEnvironment environment) throws ServiceException {

		LOGGER.debug("Calling CF getDomains API");
		String domain_response = null;
		JSONObject parsedvalue = null;
		String domain_guid = null;

		try {
			HashMap<String, String> httpDetail = form_headers(authToken);
			domain_response = RestUtil.gethttps(environment.getCfHttpsUrl() + "/v2/domains", httpDetail);
			parsedvalue = jsonparser(domain_response);
			LOGGER.info(String.format("Final response of Get Domains API - %s.", parsedvalue));
			JSONArray resources = (JSONArray) parsedvalue.get("resources");
			JSONObject temp = (JSONObject) resources.get(0);
			temp = (JSONObject) temp.get("metadata");
			domain_guid = (String) temp.get("guid");
		} catch (Exception e) {
			String error_code=(String) parsedvalue.get("code");
			String description=(String) parsedvalue.get("description");
			if(error_code.equalsIgnoreCase("1000")&& description.equalsIgnoreCase("Invalid Auth Token"))
					{
				String refresh_authtoken=login(environment);
				domain_guid = getDomains(refresh_authtoken, environment);
				LOGGER.info(String.format("getDomains - %s.", domain_guid));
		}
		}
		return domain_guid;
	}

	public static String uploadApplication(String authToken, String app_guid, String filetoupload,
			ServiceEnvironment environment) throws ServiceException {

		LOGGER.debug("Calling CF uploadApplication API");
		String uploadApplication_response = null;
		JSONObject parsedvalue=null;
		try {
			File uploadFile = new File(filetoupload);
			String requestURL = environment.getCfHttpsUrl() + "/v2/apps/" + app_guid + "/bits?async=false";
			String boundary = "---------------------------" + System.currentTimeMillis();
			Map<String, String> httpDetail = new HashMap<String, String>();
			httpDetail.put(HttpHeaders.AUTHORIZATION, "bearer " + authToken);
			httpDetail.put(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE + ";boundary=" + boundary);
			uploadApplication_response = RestUtil.Multipartput(requestURL, httpDetail, uploadFile, boundary);
			parsedvalue=jsonparser(uploadApplication_response);
			LOGGER.info("Upload success");
		} catch (Exception e) {
			//throw new ServiceException("Exception in uploadApplication api.", e);
			String error_code=(String) parsedvalue.get("code");
			String description=(String) parsedvalue.get("description");
			if(error_code.equalsIgnoreCase("1000")&& description.equalsIgnoreCase("Invalid Auth Token"))
					{
				String refresh_authtoken=login(environment);
				String upload = uploadApplication(refresh_authtoken, app_guid, filetoupload, environment);
				LOGGER.info(String.format("uploadApplication - %s.", upload));		
		}
		}

		LOGGER.info(String.format("Upload Response of uploadApplication API : %s", uploadApplication_response));
		return uploadApplication_response;

	}

	public static String startApplication(String authToken, String app_guid, ServiceEnvironment environment,
			boolean isStart) throws ServiceException {
		LOGGER.debug("Calling CF startApplication API");
		JSONObject parsedvalue = null;
		String startApp_response = null;
		String space_guid = null;
		try {
			HashMap<String, String> httpDetail = form_headers(authToken);

			String postBody = null;
			if (isStart) {
				postBody = "{\"console\":true,\"state\":\"STARTED\"}";
			} else {
				postBody = "{\"console\":true,\"state\":\"STOPPED\"}";
			}

			startApp_response = RestUtil.puthttps(environment.getCfHttpsUrl() + "/v2/apps/" + app_guid, httpDetail,
					postBody);
			parsedvalue = jsonparser(startApp_response);
			space_guid = (String) ((JSONObject) parsedvalue.get("entity")).get("space_guid");

		} catch (Exception e) {
//			throw new ServiceException("Exception in startApplication api.", e);
			String error_code=(String) parsedvalue.get("code");
			String description=(String) parsedvalue.get("description");
			if(error_code.equalsIgnoreCase("1000")&& description.equalsIgnoreCase("Invalid Auth Token"))
					{
				String refresh_authtoken=login(environment);
				String startApp = startApplication(refresh_authtoken, app_guid, environment, true);
				LOGGER.info(String.format("startApplication - %s.", startApp));
		}
		}

		return parsedvalue.toString();

	}

	public static String startApplicationWithEnv(String authToken, String app_guid, String environment_json,
			ServiceEnvironment environment) throws ServiceException {
		LOGGER.debug("Calling CF startApplicationWithEnv API");
		JSONObject parsedvalue = null;
		String startApp_response = null;
		String space_guid = null;
		try {
			HashMap<String, String> httpDetail = form_headers(authToken);
			String postBody = "{\"console\":true,\"state\":\"STARTED\",\"environment_json\":" + environment_json
					+ "\"}";
			startApp_response = RestUtil.puthttps(environment.getCfHttpsUrl() + "/v2/apps/" + app_guid, httpDetail,
					postBody);
			parsedvalue = jsonparser(startApp_response);
			LOGGER.info(String.format("Final Response of startApplication API: %s.", parsedvalue));
			space_guid = (String) ((JSONObject) parsedvalue.get("entity")).get("space_guid");

		} catch (Exception e) {
			throw new ServiceException("Exception in startApplicationWithEnv api.", e);
		}
		return space_guid;
	}

	public static String associateRoute(String authToken, String route_guid, String appsummary,
			ServiceEnvironment environment) throws ServiceException {

		LOGGER.debug("Calling CF associateRoute API");
		JSONObject appdetail = jsonparser(appsummary);
		String app_guid = (String) appdetail.get("app_guid");
		JSONObject parsedvalue = null;
		String associateRoute_response = null;
		try {
			HashMap<String, String> httpDetail = form_headers(authToken);
			String postBody = "{}";
			associateRoute_response = RestUtil.puthttps(
					environment.getCfHttpsUrl() + "/v2/apps/" + app_guid + "/routes/" + route_guid, httpDetail,
					postBody);
			parsedvalue = jsonparser(associateRoute_response);
			LOGGER.info(String.format("Final Response of associateRoute API : %s.", parsedvalue));
		} catch (Exception e) {
//			throw new ServiceException("Exception in cf associate route api.", e);
			String error_code=(String) parsedvalue.get("code");
			String description=(String) parsedvalue.get("description");
			if(error_code.equalsIgnoreCase("1000")&& description.equalsIgnoreCase("Invalid Auth Token"))
					{
				String refresh_authtoken=login(environment);
				app_guid = associateRoute(refresh_authtoken, route_guid, appsummary, environment);
				LOGGER.info(String.format("associateRoute - %s.", app_guid));

				
		}
		}
		return app_guid;

	}

	public static String createRoute(String authToken, String space_guid, String app_name, String domain_guid,
			ServiceEnvironment environment) throws ServiceException {

		LOGGER.debug("Calling CF createRoute API");

		String route_guid = null;
		JSONObject parsedvalue = null;
		String createRoute_response = null;

		try {
			HashMap<String, String> httpDetail = form_headers(authToken);
			String postbody = "{\"space_guid\":\"" + space_guid + "\",\"domain_guid\":\"" + domain_guid
					+ "\",\"host\":\"" + app_name + "\"}";
			createRoute_response = RestUtil.posthttps(environment.getCfHttpsUrl() + "/v2/routes", httpDetail, postbody);
			parsedvalue = jsonparser(createRoute_response);
			LOGGER.info(String.format("Final response Create Route API - %s.", parsedvalue));

			JSONObject temp = (JSONObject) parsedvalue.get("metadata");
			route_guid = (String) temp.get("guid");
		} catch (Exception e) {
//			throw new ServiceException("Exception in cf createRoute api.", e);
			String error_code=(String) parsedvalue.get("code");
			String description=(String) parsedvalue.get("description");
			if(error_code.equalsIgnoreCase("1000")&& description.equalsIgnoreCase("Invalid Auth Token"))
					{
				String refresh_authtoken=login(environment);
				 route_guid = createRoute(refresh_authtoken, space_guid, app_name, domain_guid, environment);
				LOGGER.info(String.format("createRoute - %s.", route_guid));
		}
		}
		return route_guid;

	}

	public static void appStatus(String authToken, String app_guid, ServiceEnvironment environment)throws ServiceException {
		String State = null;

		Integer i = 1;
		while (i == 1) {
			JSONObject parsedvalue=null;
			try {
				Thread.sleep(20000);
				LOGGER.debug("appstatus auth token" + authToken);
				HashMap<String, String> httpDetail = form_headers(authToken);

				String appsummary_response = RestUtil
						.gethttps(environment.getCfHttpsUrl() + "/v2/apps/" + app_guid + "/summary", httpDetail);
				parsedvalue = jsonparser(appsummary_response);
				LOGGER.info(String.format("Current appsummary status check - %s.", parsedvalue));
				State = parsedvalue.get("state").toString();
				LOGGER.debug("State .. " + State);
				if (State.equalsIgnoreCase("STARTED")) {
					i++;
				} else {
					LOGGER.debug("appsummary_response " + appsummary_response);
				}
			} catch (Exception e) {
				String error_code=(String) parsedvalue.get("code");
				String description=(String) parsedvalue.get("description");
				if(error_code.equalsIgnoreCase("1000")&& description.equalsIgnoreCase("Invalid Auth Token"))
						{
					String refresh_authtoken=login(environment);
					appStatus(authToken, refresh_authtoken, environment);
			}
				e.printStackTrace();
			}
		}
	}
// /v2/service_instances/
	public static void serviceInstanceStatus(String authToken, String service_guid, ServiceEnvironment environment) {
		String State = null;

		Integer i = 1;
		while (i == 1) {
			try {
				Thread.sleep(20000);
				LOGGER.debug("appstatus auth token" + authToken);
				HashMap<String, String> httpDetail = form_headers(authToken);

				String serviceinstance_response = RestUtil
						.gethttps(environment.getCfHttpsUrl() + "/v2/service_instances/" + service_guid, httpDetail);
				JSONObject parsedvalue = jsonparser(serviceinstance_response);
				LOGGER.info(String.format("Current serviceInstanceStatus status check - %s.", parsedvalue));
				JSONObject entity = (JSONObject) parsedvalue.get("entity");
				JSONObject last_operation = (JSONObject) entity.get("last_operation");
				State = last_operation.get("state").toString();
				LOGGER.debug("State .. " + State);
				if (State.equalsIgnoreCase("succeeded")) {
					i++;
				} else {
					LOGGER.debug("appsummary_response " + serviceinstance_response);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static JSONObject jsonparser(String inputobject) throws ServiceException {
		JSONParser parser = new JSONParser();
		JSONObject responseObj = null;
		try {
			responseObj = (JSONObject) parser.parse(inputobject);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// LOGGER.info(String.format("jsonparser - %s", responseObj));
		return responseObj;
	}

	public static HashMap<String, String> form_headers(String authToken) throws Exception {
		Map<String, String> httpDetail = new HashMap<String, String>();
		httpDetail.put(HttpHeaders.AUTHORIZATION, "bearer " + authToken);
		httpDetail.put(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
		httpDetail.put(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_UTF8_VALUE);
		return (HashMap<String, String>) httpDetail;
	}

	@SuppressWarnings("unchecked")
	public static String createCUPS(String name, String space_guid, Map<String, Object> credentialMap,
			ServiceEnvironment environment, String accessToken) throws ServiceException {

		String summaryResponse = null;
		String ups_guid = null;
		JSONObject parsedvalue = null;

		String url = environment.getCfHttpsUrl() + "/v2/user_provided_service_instances";

		HashMap<String, String> httpDetail = null;
		try {
			httpDetail = form_headers(accessToken);
		} catch (Exception e) {
			e.printStackTrace();
		}

		JSONObject cupsObject = new JSONObject();
		cupsObject.put("name", name);
		cupsObject.put("space_guid", space_guid);
		LOGGER.debug("space_guid  " + space_guid);
		if (credentialMap != null && credentialMap.size() > 0) {
			JSONObject credentialJsonObject = new JSONObject();
			for (String tempStr : credentialMap.keySet()) {

				credentialJsonObject.put(tempStr, credentialMap.get(tempStr));

			}
			credentialJsonObject.put("cf_api", environment.getCfHttpsUrl());
			cupsObject.put("credentials", credentialJsonObject);
		}

		try {
			summaryResponse = RestUtil.posthttps(url, httpDetail, cupsObject.toString());
			parsedvalue = jsonparser(summaryResponse);
			LOGGER.debug("cups Response " + parsedvalue);
			JSONObject temp = (JSONObject) parsedvalue.get("metadata");
			ups_guid = (String) temp.get("guid");
			JSONObject entity = (JSONObject) parsedvalue.get("entity");
			JSONObject credentials = (JSONObject) entity.get("credentials");
			ip=(String) credentials.get("host");
			port=(String) credentials.get("port");
			username=(String) credentials.get("username"); 
			password=(String) credentials.get("password");
		} catch (Exception e) {
			e.printStackTrace();
			String error_code=(String) parsedvalue.get("code");
			String description=(String) parsedvalue.get("description");
			if(error_code.equalsIgnoreCase("1000")&& description.equalsIgnoreCase("Invalid Auth Token"))
					{
				String refresh_authtoken=login(environment);
				String cupsguid = createCUPS(name, space_guid, credentialMap, environment, refresh_authtoken);
				LOGGER.info(String.format("cupsguid - %s.", cupsguid));
					}
		}

		LOGGER.debug(summaryResponse);
		return ups_guid;
	}

	public static String getUPSGUID(String authToken, String upsName, ServiceEnvironment environment)
			throws ServiceException {

		LOGGER.debug("Calling CF get UPS GUID API");
		String getserviceplan_response = null;
		JSONObject parsedvalue = null;
		String guid = null;
		try {
			HashMap<String, String> httpDetail = form_headers(authToken);
			getserviceplan_response = RestUtil
					.gethttps(environment.getCfHttpsUrl() + "/v2/user_provided_service_instances", httpDetail);
			parsedvalue = jsonparser(getserviceplan_response);
			int resources = ((JSONArray) parsedvalue.get("resources")).size();
			LOGGER.info(String.format("resources length array of serviceplan API - %s", resources));
			for (int i = 0; i < resources; i++) {
				JSONObject item = (JSONObject) ((JSONArray) parsedvalue.get("resources")).get(i);
				JSONObject entity = (JSONObject) item.get("entity");
				String upsname = (String) entity.get("name");
				LOGGER.info(String.format("serviceplan unique_id - %s", upsname));
				if (upsname.equals(upsName)) {

					guid = (String) ((JSONObject) item.get("metadata")).get("guid");
					LOGGER.info(String.format("serviceplan GUID- %s", guid));
					// plan_guid.add(guid);
				}
			}
			// LOGGER.info(String.format("serviceplan Final Response - %s",
			// parsedvalue));
		} catch (Exception e) {
			LOGGER.error("Exception in ups guid api.", e);
			throw new ServiceException("Exception in service plan api.", e);
		}

		return guid;

	}

	/**
	 * To return the app guid and app enviroment json for specified apps.
	 * 
	 * @param authToken
	 * @param appsName
	 * @param environment
	 * @return
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public static String getAppsGUID(String authToken, String appsName, ServiceEnvironment environment)
			throws ServiceException {

		LOGGER.debug("Calling CF getAppsGUID API");
		String getserviceplan_response = null;
		JSONObject parsedvalue = null;
		JSONObject app_summary = new JSONObject();
		String guid = null, next_url = null, url = "/v2/apps";
		JSONObject environment_json = null;
		try {
			do {
				HashMap<String, String> httpDetail = form_headers(authToken);
				getserviceplan_response = RestUtil.gethttps(environment.getCfHttpsUrl() + url, httpDetail);
				parsedvalue = jsonparser(getserviceplan_response);
				int resources = ((JSONArray) parsedvalue.get("resources")).size();
				next_url = (String) parsedvalue.get("next_url");
				LOGGER.info(String.format("resources length array of serviceplan API - %s", resources));
				for (int i = 0; i < resources; i++) {
					JSONObject item = (JSONObject) ((JSONArray) parsedvalue.get("resources")).get(i);
					JSONObject entity = (JSONObject) item.get("entity");
					String upsname = (String) entity.get("name");
					LOGGER.info(String.format("serviceplan unique_id - %s and %s", upsname, appsName));
					if (upsname.equalsIgnoreCase(appsName)) {
						guid = (String) ((JSONObject) item.get("metadata")).get("guid");
						environment_json = ((JSONObject) ((JSONObject) item.get("entity")).get("environment_json"));
						app_summary.put("guid", guid);
						app_summary.put("environment_json", environment_json);
						LOGGER.info(String.format("getAppsGUID environment_json- %s", environment_json));
						// LOGGER.info(String.format("getAppsGUID GUID- %s",
						// guid));
					}
				}
				url = next_url;
			} while (next_url != null);
		} catch (Exception e) {
			
			LOGGER.error("Exception in apps guid api.", e);
			String error_code=(String) parsedvalue.get("code");
			String description=(String) parsedvalue.get("description");
			if(error_code.equalsIgnoreCase("1000")&& description.equalsIgnoreCase("Invalid Auth Token"))
					{
				String refersh_authtoken=login(environment);
				String app_guid = getAppsGUID(refersh_authtoken, appsName, environment);
				LOGGER.info(String.format(" TJ app_guid - %s.", app_guid));
			
		}
		}
			

		return guid;
		
	}

	public static String getAppsSummary(String authToken, String appsguid, ServiceEnvironment environment)
			throws ServiceException {

		LOGGER.debug("Calling CF getAppsGUID API");
		String getserviceplan_response = null;
		JSONObject parsedvalue = null;
		String appName = null;
		// ArrayList<String> plan_guid = new ArrayList<String>();
		try {
			HashMap<String, String> httpDetail = form_headers(authToken);
			getserviceplan_response = RestUtil
					.gethttps(environment.getCfHttpsUrl() + "/v2/apps/" + appsguid + "/summary", httpDetail);
			parsedvalue = jsonparser(getserviceplan_response);

			appName = (String) parsedvalue.get("name");

			LOGGER.info(String.format("serviceplan Final Response - %s", parsedvalue));
		} catch (Exception e) {
			LOGGER.error("Exception in service plan api.", e);
			throw new ServiceException("Exception in service plan api.", e);
		}

		return appName;

	}

	@SuppressWarnings("unchecked")
	public static void updateUPS(String authToken, String upsName, ServiceEnvironment environment,
			Map<String, Object> credentialMap, String upsguid) throws ServiceException {

		LOGGER.debug("Calling CF Update UPS API");
		String getserviceplan_response = null;
		JSONObject parsedvalue = null;

		JSONObject cupsObject = new JSONObject();

		cupsObject.put("name", upsName);

		if (credentialMap != null && credentialMap.size() > 0) {
			JSONObject credentialJsonObject = new JSONObject();
			for (String tempStr : credentialMap.keySet()) {

				credentialJsonObject.put(tempStr, credentialMap.get(tempStr));

			}
			credentialJsonObject.put("cf_api", environment.getCfHttpsUrl());
			cupsObject.put("credentials", credentialJsonObject);

		}

		String Body = cupsObject.toJSONString();

		// ArrayList<String> plan_guid = new ArrayList<String>();
		try {
			HashMap<String, String> httpDetail = form_headers(authToken);
			getserviceplan_response = RestUtil.puthttps(
					environment.getCfHttpsUrl() + "/v2/user_provided_service_instances/" + upsguid, httpDetail, Body);
			parsedvalue = jsonparser(getserviceplan_response);
			LOGGER.info(String.format("Update UPS response - %s", parsedvalue));
		} catch (Exception e) {
			LOGGER.error("Exception in service plan api.", e);
			throw new ServiceException("Exception in service plan api.", e);
		}
	}

	public static Map<String, Object> getSpaceSummary(String spaceGUID, String accessToken,
			ServiceEnvironment environment) throws ServiceException {
		LOGGER.debug("Calling CF spaceSummary API");
		String summaryResponse = null;
		Map<String, Object> serviceInstancesJson = null;
		try {
			Map<String, String> httpDetail = new HashMap<String, String>();
			httpDetail.put(HttpHeaders.AUTHORIZATION, accessToken);
			String url = environment.getCfHttpsUrl() + "/v2/spaces/" + spaceGUID + "/summary";
			summaryResponse = RestUtil.gethttps(url, httpDetail);
			LOGGER.info("summaryResponse response - {}", summaryResponse);
			serviceInstancesJson = JsonUtil.getJsonMap(summaryResponse);

		} catch (Exception exception) {
			throw new ServiceException("Exception in CFLogin.", exception);
		}
		return serviceInstancesJson;
	}

	public static Map<String, Object> getSpaceDetails(String accessToken, ServiceEnvironment environment)
			throws ServiceException {
		String spaceResponse = null;
		Map<String, Object> spaceDetailsJson = null;
		try {
			Map<String, String> httpDetail = new HashMap<String, String>();
			httpDetail.put(HttpHeaders.AUTHORIZATION, accessToken);
			String url = environment.getCfHttpsUrl() + "/v2/spaces";
			spaceResponse = RestUtil.gethttps(url, httpDetail);
			spaceDetailsJson = JsonUtil.getJsonMap(spaceResponse);

		} catch (Exception exception) {
			throw new ServiceException("Exception in CFLogin.", exception);
		}
		return spaceDetailsJson;
	}

	public static String getSpaceID(ServiceEnvironment environment, String accessToken) throws ServiceException {
		String spaceGUID = null;
		try {
			Map<String, Object> ref = getSpaceDetails(accessToken, environment);
			Map<String, Object> attributes = new HashMap<String, Object>();
			attributes.put("name", environment.getCfSpaceName());
			Map<String, Object> result1 = XPathUtil.getValue(ref, "resources[entity/name=$name]", attributes);
			spaceGUID = XPathUtil.getValue(result1, "metadata/guid");

		} catch (Exception exception) {
			throw new ServiceException("Exception in CFLogin.", exception);
		}
		return spaceGUID;
	}

	public static Map<String, Object> createService(String accessToken, ServiceEnvironment environment, String bodyData)
			throws ServiceException {
		LOGGER.debug("Calling CF create service API");
		String summaryResponse = null;
		Map<String, Object> serviceInstancesJson = null;
		try {
			Map<String, String> httpDetail = new HashMap<String, String>();
			httpDetail.put(HttpHeaders.AUTHORIZATION, accessToken);
			String url = environment.getCfHttpsUrl() + "/v2/service_instances?accepts_incomplete=true";
			summaryResponse = RestUtil.posthttps(url, httpDetail, bodyData);
			serviceInstancesJson = JsonUtil.getJsonMap(summaryResponse);

		} catch (Exception exception) {
			throw new ServiceException("Exception in CFLogin.", exception);
		}
		return serviceInstancesJson;
	}

	public static String getServicePlanId(ServiceEnvironment environment, String accessToken, String serviceId,
			String planName) throws ServiceException {
		String servicePlanID = null;
		String servicePlanDetails = null;
		Map<String, Object> servicePlanJson = null;
		try {
			Map<String, String> httpDetail = new HashMap<String, String>();
			httpDetail.put(HttpHeaders.AUTHORIZATION, accessToken);
			String url = environment.getCfHttpsUrl() + "/v2/services/" + serviceId + "/service_plans";
			servicePlanDetails = RestUtil.gethttps(url, httpDetail);
			servicePlanJson = JsonUtil.getJsonMap(servicePlanDetails);
			Map<String, Object> variableMap = new HashMap<String, Object>();
			variableMap.put("name", planName);
			Map<String, Object> jobObj = XPathUtil.getValue(servicePlanJson, "resources[entity/name=$name]",
					variableMap);
			servicePlanID = XPathUtil.getValue(jobObj, "metadata/guid");
			;
		} catch (Exception exception) {
			throw new ServiceException("Exception in CFLogin.", exception);
		}
		return servicePlanID;
	}

	public static Map<String, Object> getAllServiceDetails(ServiceEnvironment environment, String spaceID,
			String accessToken) throws ServiceException {
		String summaryResponse = null;
		Map<String, Object> serviceJson = null;
		try {
			Map<String, String> httpDetail = new HashMap<String, String>();
			httpDetail.put(HttpHeaders.AUTHORIZATION, accessToken);
			String url = environment.getCfHttpsUrl() + "/v2/spaces/" + spaceID + "/services";
			summaryResponse = RestUtil.gethttps(url, httpDetail);
			serviceJson = JsonUtil.getJsonMap(summaryResponse);
		} catch (Exception exception) {
			throw new ServiceException("Exception in CFLogin.", exception);
		}
		return serviceJson;
	}

	public static String getServiceId(String accessToken, ServiceEnvironment environment, String spaceID,
			String serviceName) throws ServiceException {
		String serviceID = null;
		try {
			Map<String, Object> serviceJson = getAllServiceDetails(environment, spaceID, accessToken);
			Map<String, Object> attributes = new HashMap<String, Object>();
			attributes.put("label", serviceName);
			Map<String, Object> result1 = XPathUtil.getValue(serviceJson, "resources[entity/label=$label]", attributes);
			serviceID = XPathUtil.getValue(result1, "metadata/guid");
		} catch (Exception exception) {
			throw new ServiceException("Exception in CFLogin.", exception);
		}
		return serviceID;
	}
	// Added By Rajnish for Delete Service Binding

	public static void deleteServiceBinding(String appGuid, String serviceBindingGuid, ServiceEnvironment environment,
			String authToken) throws ServiceException {

		try {
			HashMap<String, String> httpDetail = form_headers(authToken);

			String deleteServiceBinding = RestUtil.deletehttps(
					environment.getCfHttpsUrl() + "/v2/apps/" + appGuid + "service_bindings" + serviceBindingGuid,
					httpDetail);

			LOGGER.debug(deleteServiceBinding);
		}

		catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static boolean deleteService(String appGuid, String serviceGuid, String serviceBindingGuid,
			ServiceEnvironment environment, String authToken) throws ServiceException {
		boolean isdeleted = false;
		try {
			HashMap<String, String> httpDetail = form_headers(authToken);

			// JSONObject appsObj = new JSONObject();
			JSONObject appdetail = jsonparser(appGuid);
			String appsguid = (String) appdetail.get("guid");

			String upsroute = RestUtil
					.gethttps(environment.getCfHttpsUrl() + "/v2/apps/" + appsguid + "/service_bindings", httpDetail);
			JSONObject parsedvalue = jsonparser(upsroute);
			LOGGER.info(String.format("Final response of upsroute API - %s.", parsedvalue));
			String deleteServicebindapp;
			try {
				JSONArray resources = (JSONArray) parsedvalue.get("resources");
				if (resources.size() > 0) {
					JSONObject temp = (JSONObject) resources.get(0);
					temp = (JSONObject) temp.get("metadata");
					String servicebindguid = (String) temp.get("guid");
					deleteServicebindapp = RestUtil.deletehttps(environment.getCfHttpsUrl() + "/v2/apps/" + appsguid
							+ "/service_bindings/" + servicebindguid, httpDetail);
					LOGGER.debug("deleteServicebindapp : " + deleteServicebindapp);
					isdeleted = true;
				}
			} catch (Exception e) {
				e.printStackTrace();
				// throw new ServiceException("Exception in Deploy App.", e);
			}

			String deleteapp;
			try {
				deleteapp = RestUtil.deletehttps(environment.getCfHttpsUrl() + "/v2/apps/" + appsguid, httpDetail);
				LOGGER.debug("deleteapp : " + deleteapp);
				isdeleted = true;
			} catch (Exception e) {
				e.printStackTrace();
				throw new ServiceException("Exception in Deploy App.", e);
			}
			try {
				String deleteService = RestUtil.deletehttps(
						environment.getCfHttpsUrl() + "/v2/user_provided_service_instances/" + serviceGuid, httpDetail);
				LOGGER.debug("deleteService : " + deleteService);
				isdeleted = true;
			} catch (Exception e) {
				e.printStackTrace();
				throw new ServiceException("Exception in Deploy App.", e);

			}

		}

		catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Exception in Deploy App.", e);

		}
		return isdeleted;
	}

	public static void routeCreation(ServiceEnvironment environment, String authToken) throws ServiceException {

		try {
			HashMap<String, String> httpDetail = form_headers(authToken);

			String deleteService = RestUtil
					.gethttps(environment.getCfHttpsUrl() + "/v2/config/feature_flags/route_creation", httpDetail);

			LOGGER.debug(deleteService);
		}

		catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static String getServiceInstanceIDFromPlan(String accessToken, ServiceEnvironment environment,
			String serviceName, String planId) throws ServiceException {
		String serviceInstanceID = null;
		Map<String, Object> serviceInstanceJson = null;
		try {
			Map<String, String> httpDetail = new HashMap<String, String>();
			httpDetail.put(HttpHeaders.AUTHORIZATION, accessToken);
			String url = environment.getCfHttpsUrl() + "/v2/service_plans/" + planId + "/service_instances";
			String serviceInstanceResponse = RestUtil.gethttps(url, httpDetail);
			serviceInstanceJson = JsonUtil.getJsonMap(serviceInstanceResponse);
			Map<String, Object> attributes = new HashMap<String, Object>();
			attributes.put("name", serviceName);
			Map<String, Object> result1 = XPathUtil.getValue(serviceInstanceJson, "resources[entity/name=$name]",
					attributes);
			serviceInstanceID = XPathUtil.getValue(result1, "metadata/guid");
		} catch (Exception exception) {
			throw new ServiceException("Exception in CFLogin.", exception);
		}
		return serviceInstanceID;
	}

	public static String getServiceStatus(String accessToken, ServiceEnvironment environment, String spaceID,
			String serviceGuid) throws ServiceException {
		String status = null;
		try {
			Map<String, String> httpDetail = new HashMap<String, String>();
			httpDetail.put(HttpHeaders.AUTHORIZATION, accessToken);
			String url = environment.getCfHttpsUrl() + "/v2/service_instances/" + serviceGuid;
			String serviceDetails = RestUtil.gethttps(url, httpDetail);
			Map<String, Object> serviceJson = JsonUtil.getJsonMap(serviceDetails);
			status = XPathUtil.getValue(serviceJson, "entity/last_operation/state");
		} catch (Exception exception) {
			throw new ServiceException("Exception in CFLogin.", exception);
		}
		return status;
	}

	public static void deleteServiceInstance(String accessToken, String serviceId, ServiceEnvironment environment)
			throws ServiceException {
		try {
			Map<String, String> httpDetail = new HashMap<String, String>();
			httpDetail.put(HttpHeaders.AUTHORIZATION, accessToken);
			String url = environment.getCfHttpsUrl() + "/v2/service_instances/" + serviceId
					+ "?accepts_incomplete=true";
			RestUtil.deletehttps(url, httpDetail);
		} catch (Exception exception) {
			throw new ServiceException("Exception in deleteServiceInstance.", exception);
		}
	}

	/***
	 * To create the service
	 * 
	 * @param environment
	 * @param serviceName
	 *            - The service name which we need to create (E.g :
	 *            Redis-Service)
	 * @param planName
	 *            - Plan name to create the service like "SandBox","Dedicated
	 *            Single-Node".
	 * @param serviceInstance
	 *            - Service Instance Name given by user.
	 * @author Navaneethan Thambu (navaneethan.t@cognizant.com)
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public static String createService(String loginUrl, String space, ServiceEnvironment environment,
			String serviceName, String planName, String serviceInstance) throws ServiceException {
		String serviceId = null;
		try {

			String accessToken = login(loginUrl, environment);

			String spaceID = space(accessToken, environment, space);
			accessToken = "bearer " + accessToken;
			String serviceID = getServiceId(accessToken, environment, spaceID, serviceName);
			LOGGER.info("createService serviceID" + serviceID);
			String servicePlanID = getServicePlanId(environment, accessToken, serviceID, planName);
			LOGGER.info("createService servicePlanID" + servicePlanID);

			// accessToken = "bearer " +login();

			JSONObject bodyData = new JSONObject();
			bodyData.put("space_guid", spaceID);
			bodyData.put("service_plan_guid", servicePlanID);
			bodyData.put("name", serviceInstance);

			String serviceResponse = null;
			Map<String, Object> serviceInstancesJson = null;
			try {
				Map<String, String> httpDetail = new HashMap<String, String>();
				httpDetail.put(HttpHeaders.AUTHORIZATION, accessToken);
				String url = environment.getCfHttpsUrl() + "/v2/service_instances?accepts_incomplete=true";
				serviceResponse = RestUtil.posthttps(url, httpDetail, bodyData.toString());
				serviceInstancesJson = JsonUtil.getJsonMap(serviceResponse);
				serviceId = XPathUtil.getValue(serviceInstancesJson, "metadata/guid");
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		} catch (ServiceException e) {
			e.printStackTrace();
			throw new ServiceException("Exception in Create Service.", e);
		}
		return serviceId;
	}

	/***
	 * To get the App enviroment variables
	 * 
	 * @param environment
	 *            ServiceEnviroment
	 * @param appID
	 *            Apps GUID to get Enviroment variables
	 * @param accessToken
	 *            Access Token for login
	 * @return
	 * @throws ServiceException
	 * @author Navaneethan Thambu (navaneethan.t@cognizant.com)
	 */
	public static Map<String, Object> getAppsEnv(ServiceEnvironment environment, String appID, String accessToken)
			throws ServiceException {
		String summaryResponse = null;
		Map<String, Object> serviceJson = null;
		try {
			Map<String, String> httpDetail = new HashMap<String, String>();
			httpDetail.put(HttpHeaders.AUTHORIZATION, accessToken);
			String url = environment.getCfHttpsUrl() + "/v2/apps/" + appID + "/env";
			summaryResponse = RestUtil.gethttps(url, httpDetail);
			serviceJson = JsonUtil.getJsonMap(summaryResponse);

		} catch (Exception exception) {
			exception.printStackTrace();
			throw new ServiceException("Exception in get Apps Env.", exception);
		}
		return serviceJson;
	}

	/***
	 * To create Spring Data Flow App and bind the redis Service and RabbitMQ
	 * service and to import the all the apps od SDF.
	 * 
	 * @param environment
	 *            ServiceEnvirment to get app URL
	 * @param parameters
	 *            Parameters to get app name, Redis Name, Rabbit MQ Name, CF
	 *            details
	 * @param downloadDirectory
	 *            Directory to download the Spring cloud data flow JAR
	 * @throws ServiceException
	 * @author Navaneethan Thambu (navaneethan.t@cognizant.com)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static String createSDF(ServiceEnvironment environment, Map<String, Object> parameters,
			String downloadDirectory, String servicename) throws ServiceException {
		String app_guid = null;
		try {
			String app_name;
			String cf_url = environment.getCfHttpsUrl();
			String redis_name, rabbitmq_name, cf_org, cf_space, cf_domain, cf_username, cf_password, redis_plan,
					rabbitmq_plan;
			boolean is_new_redis, is_new_rabbitmq;
			// app_name = XPathUtil.getValue(parameters, "app_name");
			app_name = servicename;
			cf_org = XPathUtil.getValue(parameters, "EnvironmentVariables/CloudFoundry/organization");
			cf_space = XPathUtil.getValue(parameters, "EnvironmentVariables/CloudFoundry/space");
			cf_domain = XPathUtil.getValue(parameters, "EnvironmentVariables/CloudFoundry/domain");
			cf_username = XPathUtil.getValue(parameters, "EnvironmentVariables/CloudFoundry/username");
			cf_password = XPathUtil.getValue(parameters, "EnvironmentVariables/CloudFoundry/password");

			redis_name = XPathUtil.getValue(parameters, "Services/Redis/instance_name");
			redis_plan = XPathUtil.getValue(parameters, "Services/Redis/service_plan");

			String loginUrl = "https://login." + cf_domain + "/oauth/token?grant_type=password&username=" + cf_username
					+ "&password=" + cf_password;
			if (redis_plan == null || redis_plan.equals("null")) {
				redis_plan = "Dedicated Single-Node";
			}
			String new_redis = XPathUtil.getValue(parameters, "Services/Redis/create_new_instance");
			if (new_redis == null || new_redis.equals("null")) {
				is_new_redis = true;
			} else {
				is_new_redis = Boolean.valueOf(new_redis);
			}
			rabbitmq_name = XPathUtil.getValue(parameters, "Services/RabbitMQ/instance_name");
			rabbitmq_plan = XPathUtil.getValue(parameters, "Services/RabbitMQ/service_plan");
			if (rabbitmq_plan == null || rabbitmq_plan.equals("null")) {
				rabbitmq_plan = "Dedicated Single-Node";
			}
			String new_rabbitmq = XPathUtil.getValue(parameters, "Services/RabbitMQ/create_new_instance");
			if (new_rabbitmq == null || new_rabbitmq.equals("null")) {
				is_new_rabbitmq = true;
			} else {
				is_new_rabbitmq = Boolean.valueOf(new_rabbitmq);
			}

			try {
				String authToken = login(loginUrl, environment);
				String redisServiceId = null;
				if (is_new_redis) {
					redisServiceId = createService(loginUrl, cf_space, environment, "Redis-Service", redis_plan,
							redis_name);
				} else {
					redisServiceId = getServiceInstanceID(authToken, environment, redis_name);
				}
				String rabbitServiceId = null;
				if (is_new_rabbitmq) {
					rabbitServiceId = createService(loginUrl, cf_space, environment, "RabbitMQ-Service", rabbitmq_plan,
							rabbitmq_name);
				} else {
					rabbitServiceId = getServiceInstanceID(authToken, environment, rabbitmq_name);
				}
				JSONObject appsObj = new JSONObject();
				appsObj.put("SPRING_CLOUD_DEPLOYER_CLOUDFOUNDRY_URL", cf_url);
				appsObj.put("SPRING_CLOUD_DEPLOYER_CLOUDFOUNDRY_ORG", cf_org);
				appsObj.put("SPRING_CLOUD_DEPLOYER_CLOUDFOUNDRY_SPACE", cf_space);
				appsObj.put("SPRING_CLOUD_DEPLOYER_CLOUDFOUNDRY_DOMAIN", cf_domain);
				appsObj.put("SPRING_CLOUD_DEPLOYER_CLOUDFOUNDRY_STREAM_SERVICES", rabbitmq_name);
				appsObj.put("SPRING_CLOUD_DEPLOYER_CLOUDFOUNDRY_USERNAME", cf_username);
				appsObj.put("SPRING_CLOUD_DEPLOYER_CLOUDFOUNDRY_PASSWORD", cf_password);
				appsObj.put("SPRING_CLOUD_DEPLOYER_CLOUDFOUNDRY_SKIP_SSL_VALIDATION", false);
				// String downloadDirectory =
				// request.getServiceInstanceId().split("-")[0];
				URL link = new URL(environment.getCfsdfURL());
				String fileName = downloadDirectory + FilenameUtils.getName(link.getPath());

				File filetoupload = new File(fileName);
				FileUtil.copyUrlToFile(link, filetoupload);
				authToken = login(loginUrl, environment);
				LOGGER.info(String.format("Redis Service instance ID - %s.", redisServiceId));
				serviceInstanceStatus(authToken, redisServiceId, environment);
				LOGGER.info(String.format("RabbitMQ Service instance ID - %s.", rabbitServiceId));
				serviceInstanceStatus(authToken, rabbitServiceId, environment);
				String space_guid = space(authToken, environment, cf_space);

				app_name = getServiceInstanceName(authToken, environment, servicename);
				LOGGER.info(String.format("servicename - %s.", servicename));
				LOGGER.info(String.format("app_name - %s.", app_name));
				String appsummary = createApp(authToken, space_guid,app_name, environment);
				JSONObject appdetail = jsonparser(appsummary);
				// String app_guid = (String) appdetail.get("app_guid");

				LOGGER.info(String.format("appsummary - %s.", appsummary));
				String domain_guid = getDomains(authToken, environment);
				LOGGER.info(String.format("getDomains - %s.", domain_guid));
				String route_guid = createRoute(authToken, space_guid, app_name, domain_guid, environment);
				LOGGER.info(String.format("createRoute - %s.", route_guid));
				app_guid = associateRoute(authToken, route_guid, appsummary, environment);
				LOGGER.info(String.format("associateRoute - %s.", app_guid));
				String upload = uploadApplication(authToken, app_guid, filetoupload.getAbsolutePath().toString(),
						environment);
				LOGGER.info(String.format("uploadApplication - %s.", upload));

				String bindRedisService = bindServicetoApp(authToken, space_guid, app_guid, redisServiceId,
						environment);
				LOGGER.info(String.format("BindApplication - %s.", bindRedisService));
				// String bindService = bindServicetoApp(authToken, space_guid,
				// app_guid, rabbitServiceId, environment);
				// LOGGER.info(String.format("BindApplication - %s.",
				// bindService));
				String startApp = startApplicationWithEnv(authToken, app_guid, appsObj.toString(), environment);
				LOGGER.info(String.format("startApplication - %s.", startApp));
				appStatus(authToken, app_guid, environment);

				authToken = "bearer " + authToken;
				Map<String, Object> serviceJson = getAppsEnv(environment, app_guid, authToken);
				ArrayList serviceId = XPathUtil.getValue(serviceJson,
						"application_env_json/VCAP_APPLICATION/application_uris");
				LOGGER.info("application_url " + serviceId.get(0));
				String sdf_appurl = "http://" + (String) serviceId.get(0);

				Path filePath = Paths.get("springdataflow.txt");
				List<String> fileContent;
				int j = 1;
				fileContent = new ArrayList<>(Files.readAllLines(filePath, StandardCharsets.UTF_8));
				while (j == 1) {
					LOGGER.info("sdf_appurl " + sdf_appurl);
					Thread.sleep(20000);
					for (int i = 0; i < fileContent.size(); i++) {
						String type = fileContent.get(i).trim().split("=")[0].split("\\.")[0];
						String name = fileContent.get(i).trim().split("=")[0].split("\\.")[1];
						String uri = fileContent.get(i).trim().split("=")[1];
						boolean appImportStatus = createappImport(sdf_appurl, type, name, uri);
						if (!appImportStatus) {
							j = 1;
							LOGGER.info("appImportStatus " + appImportStatus);
							break;

						} else {
							LOGGER.info("appImportStatus " + appImportStatus);
							j++;
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				sdferror(environment, parameters, downloadDirectory, servicename);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Exception in Create SDF.", e);
		}
		return app_guid;
	}

	public static void sdferror(ServiceEnvironment environment, Map<String, Object> parameters,
			String downloadDirectory, String servicename) throws Exception {
		String redis_name, rabbitmq_name, redis_plan, rabbitmq_plan;
		boolean is_new_redis, is_new_rabbitmq;
		redis_name = XPathUtil.getValue(parameters, "Services/Redis/instance_name");
		redis_plan = XPathUtil.getValue(parameters, "Services/Redis/service_plan");
		if (redis_plan == null || redis_plan.equals("null")) {
			redis_plan = "Dedicated Single-Node";
		}
		String new_redis = XPathUtil.getValue(parameters, "Services/Redis/create_new_instance");
		if (new_redis == null || new_redis.equals("null")) {
			is_new_redis = true;
		} else {
			is_new_redis = Boolean.valueOf(new_redis);
		}
		rabbitmq_name = XPathUtil.getValue(parameters, "Services/RabbitMQ/instance_name");
		rabbitmq_plan = XPathUtil.getValue(parameters, "Services/RabbitMQ/service_plan");
		if (rabbitmq_plan == null || rabbitmq_plan.equals("null")) {
			rabbitmq_plan = "Dedicated Single-Node";
		}
		String new_rabbitmq = XPathUtil.getValue(parameters, "Services/RabbitMQ/create_new_instance");
		if (new_rabbitmq == null || new_rabbitmq.equals("null")) {
			is_new_rabbitmq = true;
		} else {
			is_new_rabbitmq = Boolean.valueOf(new_rabbitmq);
		}
		boolean isdeleted;
		String authToken = login(environment);
		String redisServiceId, rabbitServiceId;
		HashMap<String, String> httpDetail = form_headers(authToken);
		String appsummary = getAppsGUID(authToken, servicename, environment);
		if (appsummary != null && !appsummary.isEmpty()) {
			JSONObject appdetail = jsonparser(appsummary);
			String appsguid = (String) appdetail.get("guid");

			String upsroute = RestUtil
					.gethttps(environment.getCfHttpsUrl() + "/v2/apps/" + appsguid + "/service_bindings", httpDetail);
			JSONObject parsedvalue = jsonparser(upsroute);
			LOGGER.info(String.format("Final response of upsroute API - %s.", parsedvalue));
			String deleteServicebindapp;
			try {
				JSONArray resources = (JSONArray) parsedvalue.get("resources");
				if (resources.size() > 0) {
					JSONObject temp = (JSONObject) resources.get(0);
					temp = (JSONObject) temp.get("metadata");
					String servicebindguid = (String) temp.get("guid");
					deleteServicebindapp = RestUtil.deletehttps(environment.getCfHttpsUrl() + "/v2/apps/" + appsguid
							+ "/service_bindings/" + servicebindguid, httpDetail);
					LOGGER.debug("deleteServicebindapp : " + deleteServicebindapp);
					isdeleted = true;
				}
			} catch (Exception e) {
				e.printStackTrace();
				// throw new ServiceException("Exception in Deploy App.", e);
			}

			String deleteapp;
			try {
				deleteapp = RestUtil.deletehttps(environment.getCfHttpsUrl() + "/v2/apps/" + appsguid, httpDetail);
				LOGGER.debug("deleteapp : " + deleteapp);
				isdeleted = true;
			} catch (Exception e) {
				e.printStackTrace();
				throw new ServiceException("Exception in Deploy App.", e);
			}

			if (is_new_redis) {
				redisServiceId = getServiceInstanceID(authToken, environment, redis_name);
				try {
					String deleteService = RestUtil.deletehttps(environment.getCfHttpsUrl() + "/v2/service_instances/"
							+ redisServiceId + "?accepts_incomplete=true", httpDetail);
					LOGGER.debug("deleteService : " + deleteService);
					isdeleted = true;
				} catch (Exception e) {
					e.printStackTrace();
					throw new ServiceException("Exception in Deploy App.", e);

				}
			}

			if (is_new_rabbitmq) {
				rabbitServiceId = getServiceInstanceID(authToken, environment, rabbitmq_name);
				try {
					String deleteService = RestUtil.deletehttps(environment.getCfHttpsUrl() + "/v2/service_instances/"
							+ rabbitServiceId + "?accepts_incomplete=true", httpDetail);
					LOGGER.debug("deleteService : " + deleteService);
					isdeleted = true;
				} catch (Exception e) {
					e.printStackTrace();
					throw new ServiceException("Exception in Deploy App.", e);

				}
			}
		}

	}

	/***
	 * Method to register the apps for SDF
	 * 
	 * @param baseurl
	 *            SDF base url
	 * @param type
	 *            SDF app type(source,processor,sink)
	 * @param name
	 *            Name of the app.
	 * @param URL
	 *            URL to import the app
	 * @return
	 * @throws ServiceException
	 * @author Navaneethan Thambu (navaneethan.t@cognizant.com)
	 */
	public static boolean createappImport(String baseurl, String type, String name, String URL)
			throws ServiceException {
		LOGGER.info("Calling createappImport API");
		String summaryResponse = null;
		Map<String, Object> serviceInstancesJson = null;
		try {
			Map<String, String> httpDetail = new HashMap<String, String>();
			String url = baseurl + "/apps/" + type + "/" + name + "?force=false&uri=" + URLEncoder.encode(URL, "UTF-8");
			LOGGER.info("Calling createappImport API url" + url);
			summaryResponse = RestUtil.posthttps(url, httpDetail);
			LOGGER.info("Calling createappImport API summaryResponse " + summaryResponse);
			if (summaryResponse.contains("404")) {
				LOGGER.info("Calling createappImport 404");
				return false;
			} else {
				LOGGER.info("Calling createappImport 404 not");
				return true;
			}

		} catch (Exception exception) {
			exception.printStackTrace();
			throw new ServiceException("Exception in create app import for SDF.", exception);
		}

	}

	/***
	 * To get the service instance guid for the given service instance name
	 * 
	 * @param accessToken
	 * @param environment
	 * @param serviceName
	 * @return
	 * @throws ServiceException
	 * @author Navaneethan Thambu (navaneethan.t@cognizant.com)
	 */
	public static String getServiceInstanceID(String accessToken, ServiceEnvironment environment, String serviceName)
			throws ServiceException {
		String serviceInstanceID = null;
		Map<String, Object> serviceInstanceJson = null;
		try {
			accessToken = "bearer " + accessToken;
			Map<String, String> httpDetail = new HashMap<String, String>();
			httpDetail.put(HttpHeaders.AUTHORIZATION, accessToken);
			String url = environment.getCfHttpsUrl() + "/v2/service_instances";
			String serviceInstanceResponse = RestUtil.gethttps(url, httpDetail);
			serviceInstanceJson = JsonUtil.getJsonMap(serviceInstanceResponse);
			Map<String, Object> attributes = new HashMap<String, Object>();
			LOGGER.debug("getServiceInstanceID serviceName : " + serviceName);
			LOGGER.debug("getServiceInstanceID serviceInstanceResponse : " + serviceInstanceResponse);
			attributes.put("name", serviceName);
			Map<String, Object> result1 = XPathUtil.getValue(serviceInstanceJson, "resources[entity/name=$name]",
					attributes);
			serviceInstanceID = XPathUtil.getValue(result1, "metadata/guid");
		} catch (Exception exception) {
			throw new ServiceException("Exception in CFLogin.", exception);
		}
		return serviceInstanceID;
	}

	public static String getServiceInstanceName(String accessToken, ServiceEnvironment environment,
			String serviceInstanceId) throws ServiceException {
		String serviceInstanceID = null;
		Map<String, Object> serviceInstanceJson = null;
		try {
			accessToken = "bearer " + accessToken;
			Map<String, String> httpDetail = new HashMap<String, String>();
			httpDetail.put(HttpHeaders.AUTHORIZATION, accessToken);
			String url = environment.getCfHttpsUrl() + "/v2/service_instances/" + serviceInstanceId;
			String serviceInstanceResponse = RestUtil.gethttps(url, httpDetail);
			serviceInstanceJson = JsonUtil.getJsonMap(serviceInstanceResponse);
			Map<String, Object> attributes = new HashMap<String, Object>();
			LOGGER.debug("getServiceInstanceID serviceName : " + serviceInstanceId);
			LOGGER.debug("getServiceInstanceID serviceInstanceResponse : " + serviceInstanceResponse);
			attributes.put("guid", serviceInstanceId);
			// Map<String, Object> result1 =
			// XPathUtil.getValue(serviceInstanceJson,
			// "resources[metadata/guid=$name]",
			// attributes);
			serviceInstanceID = XPathUtil.getValue(serviceInstanceJson, "entity/name");
		} catch (Exception exception) {
			throw new ServiceException("Exception in CFLogin.", exception);
		}
		return serviceInstanceID;
	}

	public static String updateAppName(String authToken, String app_guid, ServiceEnvironment environment,
			String app_name) throws ServiceException {
		LOGGER.debug("Calling CF startApplication API");
		JSONObject parsedvalue = null;
		String startApp_response = null;
		String space_guid = null;
		try {
			HashMap<String, String> httpDetail = form_headers(authToken);

			String postBody = null;

			postBody = "{\"name\":\"" + app_name + "\"}";

			startApp_response = RestUtil.puthttps(environment.getCfHttpsUrl() + "/v2/apps/" + app_guid, httpDetail,
					postBody);
			parsedvalue = jsonparser(startApp_response);
			space_guid = (String) ((JSONObject) parsedvalue.get("entity")).get("space_guid");

			// LOGGER.info(String.format("Final Response of startApplication API
			// : %s.", parsedvalue));
		} catch (Exception e) {
			throw new ServiceException("Exception in startApplication api.", e);
		}

		return space_guid;

	}

	public static void TJdeployApp(String cups_name, ServiceEnvironment environment, String filetoupload,
			Map<String, Object> map,Service service) throws ServiceException {
		try {
			// login
			String authToken = login(environment);
			LOGGER.info(String.format("authToken - %s.", authToken));

			// get space id
			String space_guid = space(authToken, environment);
			LOGGER.info(String.format("space_guid - %s.", space_guid));

			// Create Cups
			String cupsguid = createCUPS(cups_name, space_guid, map, environment, authToken);
			LOGGER.info(String.format("cupsguid - %s.", cupsguid));

			//replace credentials in config file using cups request
			replaceMongoCredentials();
			 
			//run python scripts
			trujunctionPythonScripts();

			// get app guid
			String deployed_app_name = "Trujunction";
			String app_guid = getAppsGUID(authToken, deployed_app_name, environment);
			LOGGER.info(String.format(" TJ app_guid - %s.", app_guid));

			// service binding
			String bindService = bindServicetoApp(authToken, space_guid, app_guid, cupsguid, environment);
			LOGGER.info(String.format("BindApplication - %s.", bindService));

			// restage
			String restage = restageApp(authToken, app_guid, environment);
			LOGGER.info(String.format("restage - %s.", restage));

			// create apps
			String template_app_name = "TJ-Template-App";
			String appsummary = createApp(authToken, space_guid, template_app_name, environment);
			LOGGER.info(String.format("appsummary - %s.", appsummary));
			JSONObject appdetail = jsonparser(appsummary);
			String template_app_guid = (String) appdetail.get("app_guid");
			LOGGER.info(String.format("template_app_guid - %s.", template_app_guid));

			// get domains
			String domain_guid = getDomains(authToken, environment);
			LOGGER.info(String.format("getDomains - %s.", domain_guid));

			// create route
			template_app_name = template_app_name + "-Test";
			String route_guid = createRoute(authToken, space_guid, template_app_name, domain_guid, environment);
			LOGGER.info(String.format("createRoute - %s.", route_guid));

			// associate route
			app_guid = associateRoute(authToken, route_guid, appsummary, environment);
			LOGGER.info(String.format("associateRoute - %s.", app_guid));

			// upload app
			String upload = uploadApplication(authToken, template_app_guid, filetoupload, environment);
			LOGGER.info(String.format("uploadApplication - %s.", upload));

			// start application
			String startApp = startApplication(authToken, template_app_guid, environment, true);
			LOGGER.info(String.format("startApplication - %s.", startApp));
			appStatus(authToken, template_app_guid, environment);
			
		} catch (Exception exception) {
			throw new ServiceException("Exception in TJDeploy App.", exception);
		}
	}
//last_operation
	public static void trujunctionPythonScripts() throws ServiceException {

		try {
			File masterfile = new ClassPathResource("TJpythonScripts/insert_master_data.py").getFile();
			System.out.println(masterfile);

			Process p1 = Runtime.getRuntime().exec("python " + masterfile + " MASTER_DB");
			BufferedReader input1 = new BufferedReader(new InputStreamReader(p1.getInputStream()));
			String result1 = input1.readLine();
			System.out.println("value is : " + result1);

			Process p2 = Runtime.getRuntime().exec("python " + masterfile);
			BufferedReader input2 = new BufferedReader(new InputStreamReader(p2.getInputStream()));
			String result2 = input2.readLine();
			System.out.println("value is : " + result2);

			File jsonfile = new ClassPathResource("TJpythonScripts/insert_json_schema.py").getFile();
			System.out.println(jsonfile);

			Process p3 = Runtime.getRuntime().exec("python " + jsonfile + " MASTER_DB");
			BufferedReader input3 = new BufferedReader(new InputStreamReader(p3.getInputStream()));
			String result3 = input3.readLine();
			System.out.println("value is : " + result3);

			Process p4 = Runtime.getRuntime().exec("python " + jsonfile);
			BufferedReader input4 = new BufferedReader(new InputStreamReader(p4.getInputStream()));
			String result4 = input4.readLine();
			System.out.println("value is : " + result4);

			File dbsetupfile = new ClassPathResource("TJpythonScripts/dbSetup.py").getFile();
			System.out.println(dbsetupfile);

			Process p5 = Runtime.getRuntime().exec("python " + dbsetupfile);
			BufferedReader input5 = new BufferedReader(new InputStreamReader(p5.getInputStream()));
			String result5 = input5.readLine();
			System.out.println("value is : " + result5);

		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Exception in running python scripts.", e);
		}
	}
	
	
	private static void replaceMongoCredentials() throws IOException {
		// TODO Auto-generated method stub
		File configfile = new ClassPathResource("TJpythonScripts/configurationfile.json").getFile();
		String[] search = {"#IP_ADDRESS#","#PORT#","#USER_NAME#","#PASSWORD#"};
		String[] replace={ip,port,username,password};
		for(int i=0;i<search.length;i++)
		{
		try{
		    FileReader fr = new FileReader(configfile);
		    String s;
		    String totalStr = "";
		    try (BufferedReader br = new BufferedReader(fr)) {

		        while ((s = br.readLine()) != null) {
		            totalStr += s;
		        }
		        totalStr = totalStr.replaceAll(search[i], replace[i]);
		        FileWriter fw = new FileWriter(configfile);
		    fw.write(totalStr);
		    fw.close();
		    }
		}catch(Exception e){
		    e.printStackTrace();
		}
		}
		 
		}			

}
