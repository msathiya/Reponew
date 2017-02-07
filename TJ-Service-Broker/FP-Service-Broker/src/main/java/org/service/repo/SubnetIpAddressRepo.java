package org.service.repo;

import java.util.List;

import javax.persistence.LockModeType;

import org.service.repo.entity.SubnetIpAddress;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRED)
public interface SubnetIpAddressRepo extends CrudRepository<SubnetIpAddress, String> {

	@Lock(LockModeType.NONE)
	@Query("FROM SubnetIpAddress sipa")
	List<SubnetIpAddress> findAll();
	
	@Lock(LockModeType.NONE)
	@Query("FROM SubnetIpAddress AS sipa WHERE sipa.ipAddress = :ipAddress")
	SubnetIpAddress resetIpAddress(@Param("ipAddress") String ipAddress);
	
	@Lock(LockModeType.NONE)
	@Query("FROM SubnetIpAddress AS sipa WHERE sipa.ipAddress IN (:ipAddressList)")
	List<SubnetIpAddress> resetIpAddressList(@Param("ipAddressList") List<String> ipAddressList);

	@Lock(LockModeType.NONE)
	@Query("SELECT sipa.regionSubnet.regionSubnetPk.subnetId, COUNT(sipa.ipAddress) FROM SubnetIpAddress AS sipa WHERE sipa.isReserved='false' GROUP BY sipa.regionSubnet.regionSubnetPk.subnetId HAVING COUNT(sipa.ipAddress) >= :ipCount")
	List<Object[]> getAvailableSubnet(@Param("ipCount") long ipCount);
	
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("FROM SubnetIpAddress sipa WHERE sipa.regionSubnet.regionSubnetPk.subnetId = :subnetId AND sipa.isReserved='false'")
	Page<SubnetIpAddress> findAvailableIp(@Param("subnetId") String subnetId, Pageable pageable);

}
