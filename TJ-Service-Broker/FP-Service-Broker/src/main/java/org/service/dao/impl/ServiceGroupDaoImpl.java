package org.service.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.service.dao.ServiceGroupDao;
import org.service.repo.ServiceGroupRepo;
import org.service.repo.entity.ServiceGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ServiceGroupDaoImpl implements ServiceGroupDao {

	@Autowired
	private ServiceGroupRepo serviceGroupRepo;

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public List<String> getServiceGroupNames(Integer serviceId) {
		List<ServiceGroup> serviceGroups = serviceGroupRepo.findByServiceId(serviceId);
		List<String> groupNames = new ArrayList<String>();
		for (ServiceGroup serviceGroup : serviceGroups) {
			groupNames.add(serviceGroup.getGroupName());
		}
		return groupNames;
	}

}
