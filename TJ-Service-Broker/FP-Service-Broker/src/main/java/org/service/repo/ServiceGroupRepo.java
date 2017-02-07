package org.service.repo;

import java.util.List;

import javax.persistence.LockModeType;

import org.service.repo.entity.ServiceGroup;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
@Repository
@Transactional(propagation = Propagation.REQUIRED)
public interface ServiceGroupRepo extends CrudRepository<ServiceGroup, String> {

	@Lock(LockModeType.NONE)
	@Query("FROM ServiceGroup sg WHERE sg.serviceId = :serviceId")
	List<ServiceGroup> findByServiceId(@Param("serviceId") Integer serviceId);
}