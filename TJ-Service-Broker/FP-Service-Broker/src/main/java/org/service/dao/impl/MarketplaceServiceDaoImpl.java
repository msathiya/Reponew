package org.service.dao.impl;

import org.service.dao.MarketplaceServiceDao;
import org.service.repo.MarketplaceServiceRepo;
import org.service.repo.entity.MarketplaceServiceEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MarketplaceServiceDaoImpl implements MarketplaceServiceDao {

	
	@Autowired
	private MarketplaceServiceRepo marketplaceServiceRepo;
	
	@Override
	public MarketplaceServiceEntity findByServiceName(String serviceName) {
		return marketplaceServiceRepo.findByServiceName(serviceName);
	}
	
	@Override
	public String getSubNetId(String serviceName) {
		String subnetId = null;
		MarketplaceServiceEntity marketplaceServiceEntity =marketplaceServiceRepo.findByServiceName(serviceName);
		if(marketplaceServiceEntity != null){
			subnetId = marketplaceServiceEntity.getRegionSubnet().getRegionSubnetPk().getSubnetId();
		}
		return subnetId;
	}
	
}