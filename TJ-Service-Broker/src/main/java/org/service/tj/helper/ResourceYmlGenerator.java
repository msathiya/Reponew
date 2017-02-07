package org.service.tj.helper;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.service.util.FileUtil;
import org.service.util.StencilUtil;

public class ResourceYmlGenerator {

	/**
	 * LOGGER variable
	 */
	private static final Logger LOGGER = Logger.getLogger(ResourceYmlGenerator.class);

	
	public static String yamlGenerator(Map<String, String> provisioningInput, String subnetId, List<String> ipList)
			throws FileNotFoundException {
		Map<String, Object> stencilInput = new HashMap<String, Object>();
		return generator(provisioningInput, subnetId, ipList, stencilInput, "yaml/tj.yml");
	}
	
	
	/*public static String yamlGenerator(Map<String, String> provisioningInput, String subnetId, List<String> ipList,
			Map<String, Object> stencilInput) throws FileNotFoundException {
		
		List<Map<String, String>> ipListMap = StencilUtil.getMapList(ipList, "ipList");
		stencilInput.put("ipListMap",ipListMap);

		stencilInput.put("deploymentName", provisioningInput.get("name"));
		stencilInput.put("mongodb_secondary_rs0_0",ipList.get(0));
		stencilInput.put("mongodb_secondary_rs1_0",ipList.get(1));
		stencilInput.put("no_of_secondary_rs0",1);
		stencilInput.put("no_of_secondary_rs1",1);
		List<String> secondary_rs0_ipList = new ArrayList<String>();
		secondary_rs0_ipList.add(ipList.get(0));
		List<Map<String, String>> secondary_rs0_ipListMap = StencilUtil.getMapList(secondary_rs0_ipList, "secondary_rs0_ip");
		stencilInput.put("secondary_rs0_ipListMap",secondary_rs0_ipListMap);
		List<String> secondary_rs1_ipList = new ArrayList<String>();
		secondary_rs1_ipList.add(ipList.get(1));
		List<Map<String, String>> secondary_rs1_ipListMap = StencilUtil.getMapList(secondary_rs1_ipList, "secondary_rs1_ip");
		stencilInput.put("secondary_rs1_ipListMap",secondary_rs1_ipListMap);
		
		stencilInput.put("mongodb_arbiter_rs0_0",ipList.get(2));
		stencilInput.put("mongodb_arbiter_rs1_0",ipList.get(3));
		stencilInput.put("no_of_arbiter_rs0",1);
		stencilInput.put("no_of_arbiter_rs1",1);
		List<String> arbiter_rs0_ipList = new ArrayList<String>();
		arbiter_rs0_ipList.add(ipList.get(2));
		List<Map<String, String>> arbiter_rs0_ipListMap = StencilUtil.getMapList(arbiter_rs0_ipList, "arbiter_rs0_ip");
		stencilInput.put("arbiter_rs0_ipListMap",arbiter_rs0_ipListMap);
		List<String> arbiter_rs1_ipList = new ArrayList<String>();
		arbiter_rs1_ipList.add(ipList.get(3));
		List<Map<String, String>> arbiter_rs1_ipListMap = StencilUtil.getMapList(arbiter_rs1_ipList, "arbiter_rs1_ip");
		stencilInput.put("arbiter_rs1_ipListMap",arbiter_rs1_ipListMap);
		stencilInput.put("mongodb_primary_rs0",ipList.get(4));
		stencilInput.put("mongodb_primary_rs1",ipList.get(5));
		stencilInput.put("mongodb_sconfig_rsConfig_0",ipList.get(6));
		stencilInput.put("mongodb_sconfig_rsConfig_1",ipList.get(7));
		stencilInput.put("mongodb_pconfig_rsConfig_0",ipList.get(8));
		stencilInput.put("no_of_mongodb_sconfig_rsConfig",2);
		List<String> mongodb_sconfig_rsConfig_ipList = new ArrayList<String>();
		mongodb_sconfig_rsConfig_ipList.add(ipList.get(6));
		mongodb_sconfig_rsConfig_ipList.add(ipList.get(7));
		List<Map<String, String>> mongodb_sconfig_rsConfig_ipListMap = StencilUtil.getMapList(mongodb_sconfig_rsConfig_ipList, "mongodb_sconfig_rsConfig_ip");
		stencilInput.put("mongodb_sconfig_rsConfig_ipListMap",mongodb_sconfig_rsConfig_ipListMap);
		stencilInput.put("mongodb_router_0",ipList.get(9));
		stencilInput.put("mongodb_router_1",ipList.get(10));
		List<String> mongodb_router_ipList = new ArrayList<String>();
		mongodb_router_ipList.add(ipList.get(9));
		mongodb_router_ipList.add(ipList.get(10));
		List<Map<String, String>> mongodb_router_ipListMap = StencilUtil.getMapList(mongodb_router_ipList, "staticIp");
		stencilInput.put("staticIpList",mongodb_router_ipListMap);
		
		return generator(provisioningInput, subnetId, ipList, stencilInput, "yaml/mongo_haproxy_broker.yml");
	}*/

	private static String generator(Map<String, String> provisioningInput, String subnetId, List<String> ipList,
			Map<String, Object> stencilInput, String template) throws FileNotFoundException {
		String yamlFile = null;
		LOGGER.info(String.format("Assigned Static Ip - %s",
				CollectionUtils.isNotEmpty(ipList) ? ipList.toString() : StringUtils.EMPTY));

//		List<Map<String, String>> staticList = StencilUtil.getMapList(ipList, "staticIp");
//
		stencilInput.put("subnetId", subnetId);
//		stencilInput.put("staticIpList", staticList);
		stencilInput.put("noofinstance", provisioningInput.get("serviceInstance"));
		stencilInput.put("deploymentName", provisioningInput.get("name"));
		
		
		yamlFile = StencilUtil.getTemplate(FileUtil.getResourceStream(template), stencilInput);

		return yamlFile;
	}
}
