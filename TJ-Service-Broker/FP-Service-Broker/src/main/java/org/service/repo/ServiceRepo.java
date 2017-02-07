package org.service.repo;

import javax.persistence.LockModeType;

import org.service.repo.entity.Service;
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
public interface ServiceRepo extends CrudRepository<Service, String> {

	@Lock(LockModeType.NONE)
	@Query("FROM Service ss WHERE ss.serviceUUID = :serviceUUID")
	Service findServiceByUUID(@Param("serviceUUID") String serviceUUID);
	
	@Lock(LockModeType.NONE)
	@Modifying
	@Query("DELETE Service ss WHERE ss.serviceUUID = :serviceUUID")
	int deleteService(@Param("serviceUUID") String serviceUUID);
}