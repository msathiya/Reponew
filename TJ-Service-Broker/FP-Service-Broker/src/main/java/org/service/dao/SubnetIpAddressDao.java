package org.service.dao;

import java.util.List;
import java.util.Map;

import org.service.repo.entity.SubnetIpAddress;

/**
 * The <code>SubnetIpAddressDao</code> interface represents interface for
 * finding available sub-net IP address.
 * 
 * @author Sandeep (sandeep.visvanathan@cognizant.com)
 * @author Deepthi (deepthi.g2@cognizant.com)
 * @author Sundar (sundarajan.srinivasan@cognizant.com)
 * @author Ramkumar(ramkumar.kandhakumar@cognizant.com)
 *
 */
public interface SubnetIpAddressDao {

	/**
	 * Method declaration to find all sub-net IP address.
	 * 
	 * @return List<SubnetIpAddress> returns list of
	 *         <code>SubnetIpAddress</code> references.
	 */
	List<SubnetIpAddress> findAll();

	/**
	 * Method declaration to reset IP address.
	 * 
	 * @param ipAddress
	 *            holds IP address.
	 * @return SubnetIpAddress returns SubnetIpAddress reference.
	 */
	SubnetIpAddress resetIpAddress(String ipAddress);

	/**
	 * Method declaration to reset IP address list.
	 * 
	 * @param ipAddressList
	 *            holds IP address list.
	 * @return List<SubnetIpAddress> returns list of SubnetIpAddress reference.
	 */
	List<SubnetIpAddress> resetIpAddressList(List<String> ipAddressList);

	/**
	 * Method declaration to find available sub-net.
	 * 
	 * @param count
	 *            holds number of IP needed.
	 * @return String returns available sub-net.
	 */
	String findAvailableSubnet(long count);

	/**
	 * Method declaration to find available IPs.
	 * 
	 * @param subnetId
	 *            holds sub-net ID.
	 * @param count
	 *            holds number of IP needed.
	 * @return List<String> returns list of IPs reference.
	 */
	List<String> findAvailableIp(String subnetId, int count,String deploymentName);
	
	void updateJobGroupName(List<Map<String,Object>> subnetJobGroupDetails);
	
	Map<String,Object> getDefaultVMType(boolean isDefault);
	
	Map<String,Object> getVMType(String vmType);
	
}
