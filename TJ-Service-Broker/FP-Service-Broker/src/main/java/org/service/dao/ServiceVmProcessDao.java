package org.service.dao;

import java.util.List;

import org.service.repo.entity.ServiceVmProcess;

/**
 * The <code>ServiceVmProcessDao</code> interface represents interface for
 * finding service VM process.
 * 
 * @author Sandeep (sandeep.visvanathan@cognizant.com)
 * @author Deepthi (deepthi.g2@cognizant.com)
 * @author Sundar (sundarajan.srinivasan@cognizant.com)
 * @author Ramkumar(ramkumar.kandhakumar@cognizant.com)
 *
 */
public interface ServiceVmProcessDao {

	/**
	 * Method declaration to save service deployment.
	 * 
	 * @param serviceVmProcess
	 *            holds <code>ServiceVmProcess</code> reference.
	 * @return ServiceVmProcess returns service VM process reference.
	 * @see org.service.repo.entity.ServiceVmProcess
	 */
	ServiceVmProcess saveServiceVmProcess(ServiceVmProcess serviceVmProcess);

	/**
	 * Method declaration to find process with service UUID.
	 * 
	 * @param serviceUUID holds service UUID.
	 * @return List<ServiceVmProcess> returns list of
	 *         <code>ServiceVmProcess</code> references.
	 */
	List<ServiceVmProcess> findByServiceUUID(String serviceUUID);

	/**
	 * Method declaration to find process with service UUID and process name.
	 * 
	 * @param serviceUUID holds service UUID.
	 * @param processName holds process name.
	 * @return ServiceVmProcess returns of
	 *         <code>ServiceVmProcess</code> references.
	 */
	ServiceVmProcess findByProcessName(String serviceUUID, String processName);

	/**
	 * Method declaration to delete process with service UUID.
	 * 
	 * @param serviceUUID holds service UUID.
	 * @param processName holds process name.
	 * @return int returns list of
	 *         <code>ServiceVmProcess</code> references.
	 */
	int deleteByProcessName(String serviceUUID, String processName);

	/**
	 * Method declaration to delete process with service UUID and process name.
	 * 
	 * @param serviceUUID holds service UUID.
	 * @return int returns list of
	 *         <code>ServiceVmProcess</code> references.
	 */
	int deleteByServiceUUID(String serviceUUID);


}