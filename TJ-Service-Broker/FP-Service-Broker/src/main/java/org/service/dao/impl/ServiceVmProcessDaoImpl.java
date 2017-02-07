package org.service.dao.impl;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.service.dao.ServiceVmProcessDao;
import org.service.repo.ServiceVmProcessRepo;
import org.service.repo.entity.ServiceVmProcess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ServiceVmProcessDaoImpl implements ServiceVmProcessDao {

	@Autowired
	private ServiceVmProcessRepo serviceVmProcessRepo;
	/**
	 * Method declaration to save service deployment.
	 * 
	 * @param serviceVmProcess
	 *            holds <code>ServiceVmProcess</code> reference.
	 * @return ServiceVmProcess returns service VM process reference.
	 * @see org.service.repo.entity.ServiceVmProcess
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public ServiceVmProcess saveServiceVmProcess(ServiceVmProcess serviceVmProcess) {
		serviceVmProcess = serviceVmProcessRepo.save(serviceVmProcess);
		return serviceVmProcess;
	}

	/**
	 * Method declaration to find process with service UUID.
	 * 
	 * @param serviceUUID
	 *            holds service UUID.
	 * @return List<ServiceVmProcess> returns list of
	 *         <code>ServiceVmProcess</code> references.
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<ServiceVmProcess> findByServiceUUID(String serviceUUID) {
		List<ServiceVmProcess> serviceVmProcessList = serviceVmProcessRepo.findByServiceUUID(serviceUUID);
		if (CollectionUtils.isEmpty(serviceVmProcessList)) {
			throw new RuntimeException(String.format("No process for service UUID (%s)", serviceUUID));
		}
		return serviceVmProcessList;
	}

	/**
	 * Method declaration to find process with service UUID and process name.
	 * 
	 * @return ServiceVmProcess returns of <code>ServiceVmProcess</code>
	 *         references.
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public ServiceVmProcess findByProcessName(String serviceUUID, String processName) {
		ServiceVmProcess serviceVmProcess = serviceVmProcessRepo.findByProcessName(serviceUUID, processName);
		if (serviceVmProcess == null) {
			throw new RuntimeException(
					String.format("No process for serviceUUID (%s) with processName (%s)", serviceUUID, processName));
		}
		return serviceVmProcess;
	}

	/**
	 * Method declaration to delete process with service UUID and process name.
	 * 
	 * @param serviceUUID
	 *            holds service UUID.
	 * @param processName
	 *            holds process name.
	 * @return int returns list of <code>ServiceVmProcess</code> references.
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public int deleteByProcessName(String serviceUUID, String processName) {
		int deleteCount = serviceVmProcessRepo.deleteByProcessName(serviceUUID, processName);
		return deleteCount;
	}

	/**
	 * Method declaration to delete process with service UUID and process name.
	 * 
	 * @param serviceUUID
	 *            holds service UUID.
	 * @return int returns list of <code>ServiceVmProcess</code> references.
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public int deleteByServiceUUID(String serviceUUID) {
		int deleteCount = serviceVmProcessRepo.deleteByServiceUUID(serviceUUID);
		return deleteCount;
	}
}
