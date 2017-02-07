package org.service.repo;

import javax.persistence.LockModeType;

import org.service.repo.entity.ServiceDeployment;
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
public interface ServiceDeploymentRepo extends CrudRepository<ServiceDeployment, String> {

	@Lock(LockModeType.NONE)
	@Query("FROM ServiceDeployment sd WHERE sd.serviceDeploymentUUID = :serviceDeploymentUUID")
	ServiceDeployment findServiceDeploymentByUUID(@Param("serviceDeploymentUUID") String serviceDeploymentUUID);
	
	@Lock(LockModeType.NONE)
	@Query("FROM ServiceDeployment sd WHERE sd.deploymentName = :deploymentName")
	ServiceDeployment findServiceDeploymentByName(@Param("deploymentName") String serviceUUID);
	
	@Lock(LockModeType.NONE)
	@Modifying
	@Query("DELETE ServiceDeployment sd WHERE sd.serviceDeploymentUUID = :serviceDeploymentUUID ")
	int deleteServiceDeployment(@Param("serviceDeploymentUUID") String serviceDeploymentUUID);

}