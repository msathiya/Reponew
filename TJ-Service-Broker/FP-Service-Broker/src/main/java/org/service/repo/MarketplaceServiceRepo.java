package org.service.repo;

import org.service.repo.entity.MarketplaceServiceEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * The <code>MarketplaceServiceRepo</code> interface represents interface for
 * CRUD operations on marketplace service.
 * 
 * @author 439120
 *
 */
@Repository
@Transactional(propagation = Propagation.REQUIRED)
public interface MarketplaceServiceRepo extends CrudRepository<MarketplaceServiceEntity, String> {
	
	/**
	 * To get Service by given service ID
	 * @param serviceId holds service Id.
	 * @return MarketplaceServiceEntity returns <code>MarketplaceServiceEntity</code> entity reference.
	 */
	@Query("FROM MarketplaceServiceEntity WHERE serviceId=:serviceId")
	public MarketplaceServiceEntity findByServiceId(@Param("serviceId") String serviceId);
	
	/**
	 * To get Service by given service Name
	 * @param serviceName holds service Name.
	 * @return MarketplaceServiceEntity returns <code>MarketplaceServiceEntity</code> entity reference.
	 */
	@Query("FROM MarketplaceServiceEntity WHERE serviceName=:serviceName")
	public MarketplaceServiceEntity findByServiceName(@Param("serviceName") String serviceName);
	
	/**
	 * To get Service by given status
	 * @param status AVAILABLE,IN_PROGRESS,INSTALLED.
	 * @return
	 */
	@Query("FROM MarketplaceServiceEntity WHERE serviceStatus=:status")
	public Iterable<MarketplaceServiceEntity> findByStatus(@Param("status") String status);
}