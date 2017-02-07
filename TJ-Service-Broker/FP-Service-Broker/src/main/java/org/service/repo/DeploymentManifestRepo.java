package org.service.repo;

import javax.persistence.LockModeType;

import org.service.repo.entity.DeploymentManifest;
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
public interface DeploymentManifestRepo extends CrudRepository<DeploymentManifest, String> {

	
	@Lock(LockModeType.NONE)
	@Query("FROM DeploymentManifest dm WHERE dm.serviceDeployment.serviceDeploymentUUID = :serviceDeploymentUUID AND dm.isActive='true'")
	DeploymentManifest findDeploymentManifestByUUID(@Param("serviceDeploymentUUID") String serviceDeploymentUUID);
	
	@Lock(LockModeType.NONE)
	@Modifying
	@Query("DELETE DeploymentManifest dm WHERE dm.serviceDeployment.serviceDeploymentUUID = :serviceDeploymentUUID ")
	int deleteDeploymentManifest(@Param("serviceDeploymentUUID") String serviceDeploymentUUID);

	@Lock(LockModeType.NONE)
	@Modifying
	@Query("UPDATE DeploymentManifest dm SET dm.isActive='false' WHERE dm.serviceDeployment.serviceDeploymentUUID = :serviceDeploymentUUID AND dm.isActive='true'")
	int softDeleteDeploymentManifest(@Param("serviceDeploymentUUID") String serviceDeploymentUUID);
}