package org.service.dao;

import org.service.repo.entity.MarketplaceServiceEntity;

/***
 * 
 * @author 379587
 *
 */
public interface MarketplaceServiceDao {

	/***
	 * To get the MarketPlaceServiceEntity to the given service Name
	 * @param serviceName
	 * @return
	 */
	MarketplaceServiceEntity findByServiceName(String serviceName);
	
	String getSubNetId(String serviceName);
}