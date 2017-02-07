package org.service.bosh.util;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.service.data.ServiceConstant;
import org.service.data.ServiceEnvironment;
import org.service.model.OperationRequest;
import org.service.util.JsonUtil;
import org.service.util.RestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The <code>BoshRestApi</code> class is a helper class for BOSH rest request.
 * 
 * @author Sandeep (sandeep.visvanathan@cognizant.com)
 * @author Deepthi (deepthi.g2@cognizant.com)
 * @author Sundar (sundarajan.srinivasan@cognizant.com)
 * @author Ramkumar(ramkumar.kandhakumar@cognizant.com)
 */
public final class BoshRestUtil {

	/**
	 * Holds LOGGER reference.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(BoshRestUtil.class);

	/**
	 * Private constructor.
	 */
	private BoshRestUtil() {
		// Helper class constructor.
	}

	/**
	 * Method gets the deployment detail when a deployment name is specified.
	 * 
	 * @param boshRequest
	 *            holds BOSH request.
	 * @return String returns detail JSON of particular deployment.
	 * @throws Exception
	 *             throws on exception.
	 */
	public static String getDeploymentDetail(Map<String, String> boshRequest) throws Exception {

		String deploymentDetails = null;
		String uri = StringUtils.join(boshRequest.get("boshProtocol"), boshRequest.get("boshUrl"),
				boshRequest.get("boshDeploymentPath"));
		Map<String, String> httpDetail = new HashMap<String, String>();

		httpDetail.put(HttpHeaders.AUTHORIZATION, boshRequest.get("Authorization"));

		deploymentDetails = RestUtil.gethttps(uri, httpDetail);

		return deploymentDetails;

	}

	/**
	 * Method gets all the deployment and its details based on the BOSH URL.
	 * 
	 * @param boshRequest
	 *            holds BOSH request.
	 * @return String returns detail JSON of all deployments. specified BOSH.
	 * @throws Exception
	 *             throws on exception
	 */
	public static String getDeploymentDetails(Map<String, String> boshRequest) throws Exception {

		String deploymentDetails = null;
		String uri = StringUtils.join(boshRequest.get("boshProtocol"), boshRequest.get("boshUrl"),
				boshRequest.get("boshDeploymentPath"));
		Map<String, String> httpDetail = new HashMap<String, String>();

		httpDetail.put(HttpHeaders.AUTHORIZATION, boshRequest.get("Authorization"));

		deploymentDetails = RestUtil.gethttps(uri, httpDetail);

		return deploymentDetails;

	}

	/**
	 * Method gets the details of all VMs in specified BOSH.
	 * 
	 * @param boshRequest
	 *            holds BOSH request.
	 * @param deploymentName
	 *            holds deployment name.
	 * @return String returns VM details
	 * @throws Exception
	 *             throws on exception.
	 */
	public static String getVmsDetails(Map<String, String> boshRequest, String deploymentName) throws Exception {

		String vmDetails = null;
		String uri = StringUtils.join(boshRequest.get("boshProtocol"), boshRequest.get("boshUrl"),
				boshRequest.get("boshDeploymentPath"), "/", deploymentName, "/vms?format=full");

		Map<String, String> httpDetail = new HashMap<String, String>();

		httpDetail.put(HttpHeaders.AUTHORIZATION, boshRequest.get("Authorization"));
		vmDetails = RestUtil.gethttps(uri, httpDetail);

		return vmDetails;
	}

	/**
	 * Method gets the details of the task for a specific task ID.
	 * 
	 * @param boshRequest
	 *            holds BOSH request.
	 * @param taskId
	 *            holds task ID.
	 * @return String returns the details of a specific task.
	 * @throws Exception
	 *             throws on exception.
	 */
	public static String getTaskDetail(Map<String, String> boshRequest, String taskId) throws Exception {

		String path = StringUtils.join("/tasks/", taskId, "/output?type=result");
		String uri = StringUtils.join(boshRequest.get("boshProtocol"), boshRequest.get("boshUrl"), path);
		Map<String, String> httpDetail = new HashMap<String, String>();
		httpDetail.put(HttpHeaders.AUTHORIZATION, boshRequest.get("Authorization"));
		Thread.sleep(8000);
		String taskDetail = RestUtil.gethttps(uri, httpDetail);
		return taskDetail;
	}

	/**
	 * Method starts the BOSH deployment using BOSH details and YML file.
	 * 
	 * @param boshRequest
	 *            holds BOSH request.
	 * @param yamlFile
	 *            holds YAML file location as string.
	 * @return String returns the task URL.
	 * @throws Exception
	 *             throws on exception.
	 */
	public static String postBoshDeployment(Map<String, String> boshRequest, String yamlFile) throws Exception {

		String taskUrl = null;

		String uri = boshRequest.get("boshProtocol") + boshRequest.get("boshUrl")
				+ boshRequest.get("boshDeploymentPath");

		Map<String, String> httpDetail = new HashMap<String, String>();

		httpDetail.put(HttpHeaders.AUTHORIZATION, boshRequest.get("Authorization"));
		httpDetail.put(HttpHeaders.CONTENT_TYPE, ServiceConstant.TEXT_YAML);

		taskUrl = RestUtil.posthttps(uri, httpDetail, yamlFile);
		return taskUrl;
	}

	/**
	 * Method gets the status of the BOSH task.
	 * 
	 * @param httpDetail
	 * @param taskUrl
	 * @return Returns <code>String</code> the status of the task.
	 * @throws Exception
	 */
	public static String getStatus(Map<String, String> httpDetail, String taskUrl) throws Exception {
		String response = getTaskStatus(httpDetail, taskUrl);

		org.json.JSONObject taskDetail = new org.json.JSONObject(response);
		String status = taskDetail.getString("state");
		LOGGER.debug("Task status - {}", status);
		return status;
	}

	/**
	 * Method gets the details and status of the task based on task URL.
	 * 
	 * @param boshRequest
	 *            holds BOSH request.
	 * @param taskurl
	 *            holds task URL.
	 * @return String returns the details of the task.
	 * @throws Exception
	 *             throws on exception.
	 */
	public static String getTaskStatus(Map<String, String> boshRequest, String taskurl) throws Exception {

		String taskDetail = null;
		LOGGER.debug("Task url - {}.", taskurl);

		Map<String, String> httpDetail = new HashMap<String, String>();
		httpDetail.put(HttpHeaders.AUTHORIZATION, boshRequest.get("Authorization"));
		taskDetail = RestUtil.gethttps(taskurl, httpDetail);

		return taskDetail;
	}

	/**
	 * Method deletes the deployment from the BOSH.
	 * 
	 * @param operationRequest
	 *            holds operation request.
	 * @param environment
	 *            holds environment reference.
	 * @return String returns the details of the deployment that is deleted.
	 * @throws Exception
	 *             throws on exception.
	 */
	public static String deleteDeployment(OperationRequest operationRequest, ServiceEnvironment environment)
			throws Exception {

		Map<String, String> boshRequest = BoshUtil.getReqestHeader(operationRequest, environment);

		String deploymentDetails = null;
		String uri = boshRequest.get("boshProtocol") + boshRequest.get("boshUrl")
				+ boshRequest.get("boshDeploymentPath") + "/" + boshRequest.get("name");
		Map<String, String> httpDetail = new HashMap<String, String>();
		httpDetail.put(HttpHeaders.AUTHORIZATION, boshRequest.get("Authorization"));
		deploymentDetails = RestUtil.deletehttps(uri, httpDetail);

		return deploymentDetails;

	}

	/**
	 * Method to get expose IP list from BOSH deployment using deployment name.
	 * 
	 * @param provisioningInput
	 *            holds provision input.
	 * @return List<String> returns IP list.
	 * @throws Exception
	 *             throws on exception.
	 */
	public static List<String> getExposeIpList(Map<String, String> provisioningInput) throws Exception {
		return getIpList(provisioningInput, "haproxy");
	}

	/**
	 * Method to get expose IP list from BOSH deployment using deployment name.
	 * 
	 * @param provisioningInput
	 *            holds provision input.
	 * @return List<String> returns IP list.
	 * @throws Exception
	 *             throws on exception.
	 */
	public static List<String> getDeploymentIpList(Map<String, String> provisioningInput) throws Exception {

		return getIpList(provisioningInput, null);
	}
	
	/**
	 * Method to get namenode IP list (for hadoop) from BOSH deployment using deployment name.
	 * 
	 * @param provisioningInput
	 *            holds provision input.
	 * @return List<String> returns IP list.
	 * @throws Exception
	 *             throws on exception.
	 */
	public static List<String> getExposeIpList(Map<String, String> provisioningInput,String jobName) throws Exception {

		return getIpList(provisioningInput,jobName);
	}


	/**
	 * Method to get IP list from BOSH deployment using deployment name.
	 * 
	 * @param provisioningInput
	 *            holds provision input.
	 * @param type
	 *            holds type.
	 * @return List<String> returns IP list.
	 * @throws Exception
	 *             throws on exception.
	 */
	@SuppressWarnings("unchecked")
	private static List<String> getIpList(Map<String, String> provisioningInput,String jobName) throws Exception {

		List<String> exposeIpList = new ArrayList<String>();
		List<String> resourceIpList = new ArrayList<String>();
		String deploymentDetails = BoshRestUtil.getDeploymentDetails(provisioningInput);
		List<Map<String, Object>> deployments = JsonUtil.getJsonListMap(deploymentDetails);
		LOGGER.info("UserProvidedDeploymentName - {}.", provisioningInput.get("name"));
		String userProvidedDeploymentName = provisioningInput.get("name");
		for (Map<String, Object> deployment : deployments) {
			if (deployment.get("name").equals(userProvidedDeploymentName)) {
				String vmResponse = BoshRestUtil.getVmsDetails(provisioningInput, deployment.get("name").toString());
				Map<String, Object> vmDetails = JsonUtil.getJsonMap(vmResponse);
				Object idObject = vmDetails.get("id");
				if (idObject != null) {
					String taskDetails = BoshRestUtil.getTaskDetail(provisioningInput, vmDetails.get("id").toString());
					LOGGER.info("Task Details - {}.", taskDetails);
					List<String> lines = IOUtils.readLines(new StringReader(taskDetails));
					for (String line : lines) {
						Map<String, Object> eachtask = JsonUtil.getJsonMap(line);
						String deploymentJobName = (String) eachtask.get("job_name");
						List<String> ipList = (List<String>) eachtask.get("ips");
						resourceIpList.addAll(ipList);
						if (StringUtils.startsWith(deploymentJobName,jobName)) {
							exposeIpList.addAll(ipList);
						}
					}
				}
			}
		}

		LOGGER.debug("exposeIpList - {}", exposeIpList);
		LOGGER.debug("resourceIpList - {}", resourceIpList);
		List<String> ipList = resourceIpList;
		if (CollectionUtils.isNotEmpty(exposeIpList)) {
			ipList = exposeIpList;
		}
		LOGGER.info("IpList {} for jobName {}.", ipList, jobName);
		return ipList;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, String> getVmTypeList(Map<String, String> provisioningInput, String deploymentName)
			throws Exception {

		Map<String, String> vmTypeMap = new HashMap<String, String>();
		String vmResponse = BoshRestUtil.getVmsDetails(provisioningInput, deploymentName);
		Map<String, Object> vmDetails = JsonUtil.getJsonMap(vmResponse);
		Object idObject = vmDetails.get("id");
		if (idObject != null) {
			String taskDetails = BoshRestUtil.getTaskDetail(provisioningInput, vmDetails.get("id").toString());
			LOGGER.info("Task Details - {}.", taskDetails);
			List<String> lines = IOUtils.readLines(new StringReader(taskDetails));
			for (String line : lines) {
				Map<String, Object> eachtask = JsonUtil.getJsonMap(line);
				String jobName = (String) eachtask.get("job_name");
				String vmType = (String) eachtask.get("vm_type");
				vmTypeMap.put(jobName, vmType);
			}
		}
		LOGGER.info("vmTypeMap - {}", vmTypeMap);
		return vmTypeMap;
	}

}
