package org.service.yaml.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.service.util.FileUtil;
import org.service.util.StencilUtil;

public final class HaProxyHelper {

	/**
	 * Private constructor.
	 */
	private HaProxyHelper() {
		// Helper class constructor.
	}

	public static Map<String, Object> generateHaProxyYaml(Map<String, String> provisioningInput,
			List<Map<String, String>> staticIpList, String subnetId, List<Map<String, String>> proxyIpList,
			String proxyPort) {

		Map<String, Object> haproxyRequestParam = new HashMap<String, Object>();
		haproxyRequestParam.put("noOfProxyInstance", 1);
		haproxyRequestParam.put("staticIpList", staticIpList);
		haproxyRequestParam.put("proxyIpList", proxyIpList);
		haproxyRequestParam.put("subnetId", subnetId);
		haproxyRequestParam.put("proxyPort", proxyPort);
		haproxyRequestParam.put("deploymentName", provisioningInput.get("name"));
		haproxyRequestParam.put("directoruuid",provisioningInput.get("directoruuid"));
		haproxyRequestParam.put("gatewayip", provisioningInput.get("gatewayip"));
		haproxyRequestParam.put("rangeip", provisioningInput.get("rangeip"));
		String haproxyJob = StencilUtil.getTemplate(FileUtil.getResourceStream("yaml/haproxy/haproxy_job.yml"),
				haproxyRequestParam);
		String haproxyProperties = StencilUtil
				.getTemplate(FileUtil.getResourceStream("yaml/haproxy/haproxy_properties.yml"), haproxyRequestParam);
		String haproxyRelease = StencilUtil.getTemplate(FileUtil.getResourceStream("yaml/haproxy/haproxy_releases.yml"),
				haproxyRequestParam);
		String haproxyResourcePool = StencilUtil
				.getTemplate(FileUtil.getResourceStream("yaml/haproxy/haproxy_resourcepool.yml"), haproxyRequestParam);
		String haproxyNetwork = StencilUtil.getTemplate(FileUtil.getResourceStream("yaml/haproxy/haproxy_network.yml"),
				haproxyRequestParam);

		Map<String, Object> haproxyResponseParam = new HashMap<String, Object>();
		haproxyResponseParam.put("haproxyJob", haproxyJob);
		haproxyResponseParam.put("haproxyProperties", haproxyProperties);
		haproxyResponseParam.put("haproxyRelease", haproxyRelease);
		haproxyResponseParam.put("haproxyResourcePool", haproxyResourcePool);
		haproxyResponseParam.put("haproxyNetwork", haproxyNetwork);
		return haproxyResponseParam;
	}

}
