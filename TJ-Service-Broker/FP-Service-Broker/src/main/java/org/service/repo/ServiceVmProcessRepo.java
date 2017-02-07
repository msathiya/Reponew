package org.service.repo;

import java.util.List;

import javax.persistence.LockModeType;

import org.service.repo.entity.ServiceVmProcess;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
@Repository
@Transactional(propagation = Propagation.REQUIRED)
public interface ServiceVmProcessRepo extends CrudRepository<ServiceVmProcess, String> {

	@Lock(LockModeType.NONE)
	@Query("FROM ServiceVmProcess svp WHERE svp.serviceVmProcessPk.serviceUUID = :serviceUUID")
	List<ServiceVmProcess> findByServiceUUID(@Param("serviceUUID") String serviceUUID);

	@Lock(LockModeType.NONE)
	@Query("FROM ServiceVmProcess svp WHERE svp.serviceVmProcessPk.serviceUUID = :serviceUUID AND svp.serviceVmProcessPk.processName = :processName")
	ServiceVmProcess findByProcessName(@Param("serviceUUID") String serviceUUID, @Param("processName") String processName);

	@Lock(LockModeType.NONE)
	@Modifying
	@Query("DELETE ServiceVmProcess svp WHERE svp.serviceVmProcessPk.serviceUUID = :serviceUUID")
	int deleteByServiceUUID(@Param("serviceUUID") String serviceUUID);
	
	@Lock(LockModeType.NONE)
	@Modifying
	@Query("DELETE ServiceVmProcess svp WHERE svp.serviceVmProcessPk.serviceUUID = :serviceUUID AND svp.serviceVmProcessPk.processName = :processName")
	int deleteByProcessName(@Param("serviceUUID") String serviceUUID, @Param("processName") String processName);
	

}