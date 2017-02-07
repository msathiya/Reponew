package org.service.repo;

import javax.persistence.LockModeType;

import org.service.repo.entity.CloudProviderVm;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRED)
public interface CloudProviderVmRepo extends CrudRepository<CloudProviderVm, String> {

	
	@Lock(LockModeType.NONE)
	@Query("FROM CloudProviderVm AS cpvm WHERE cpvm.isDefault = :isDefault")
	CloudProviderVm findDefaultVMType(@Param("isDefault") boolean isDefault);

	@Lock(LockModeType.NONE)
	@Query("FROM CloudProviderVm AS cpvm WHERE cpvm.vmType = :vmType")
	CloudProviderVm findVMType(@Param("vmType") String vmType);

}
