package org.service.bosh.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * The <code>BoshTaskDataHandler</code> is a util to handle BOSH tasks.
 * 
 * @author .
 */
public final class BoshDataHandler {
	/**
	 * Private constructor.
	 */
	private BoshDataHandler() {
		// Helper class constructor.
	}
	
	/**
	 * This method is used to get the IPs that are available in particular
	 * sub-net.
	 * 
	 * @param taskDetails
	 * @return returns <code>List<string></code> list of IPs in subnet.
	 */
	public static List<String> obtainIpList(String taskDetails) {
		List<String> ipList = new ArrayList<String>();
		if (StringUtils.isNotBlank(taskDetails)) {
			String[] tasks = StringUtils.split(taskDetails, ",");

			for (String task : tasks) {
				// task matches with IP address and sub-net range
				if (task.contains("ips") && task.contains("10.0.10")) {
					String ips = task;
					String[] splitIps = StringUtils.split(ips, ":");
					ipList.add(StringUtils.substringBetween(splitIps[1], "[\"", "\"]"));
				}
			}
		}
		return ipList;
	}
}
