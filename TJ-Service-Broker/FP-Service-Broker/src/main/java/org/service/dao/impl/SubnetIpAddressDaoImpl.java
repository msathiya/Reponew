package org.service.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.service.dao.SubnetIpAddressDao;
import org.service.repo.CloudProviderVmRepo;
import org.service.repo.SubnetIpAddressRepo;
import org.service.repo.entity.CloudProviderVm;
import org.service.repo.entity.SubnetIpAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class SubnetIpAddressDaoImpl implements SubnetIpAddressDao {

	/**
	 * LOGGER variable
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(SubnetIpAddressDaoImpl.class);

	@Autowired
	private SubnetIpAddressRepo subnetIpAddressRepo;

	@Autowired
	private CloudProviderVmRepo cloudProviderVmRepo;
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<SubnetIpAddress> findAll() {
		List<SubnetIpAddress> subnetIpAddressList = subnetIpAddressRepo.findAll();

		return subnetIpAddressList;
	}


	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public SubnetIpAddress resetIpAddress(String ipAddress) {
		SubnetIpAddress subnetIpAddress = subnetIpAddressRepo.resetIpAddress(ipAddress);
		subnetIpAddress.setIsReserved(false);
		subnetIpAddressRepo.save(subnetIpAddress);
		return subnetIpAddress;
	}


	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<SubnetIpAddress> resetIpAddressList(List<String> ipAddressList) {
		List<SubnetIpAddress> subnetIpAddressList = subnetIpAddressRepo.resetIpAddressList(ipAddressList);
		for (SubnetIpAddress subnetIpAddress : subnetIpAddressList) {
			subnetIpAddress.setIsReserved(false);
		}
		subnetIpAddressRepo.save(subnetIpAddressList);
		return subnetIpAddressList;
	}


	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public String findAvailableSubnet(long count) {
		List<Object[]> list = subnetIpAddressRepo.getAvailableSubnet(count);
		String subnetName = null;
		if (CollectionUtils.isEmpty(list)) {
			throw new RuntimeException("No available subnet.");
		}
		for (Object[] object : list) {
			subnetName = (String) object[0];
			break;
		}
		return subnetName;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public List<String> findAvailableIp(String subnetId, int count,String deploymentName) {
		Page<SubnetIpAddress> subnetIpAddressPage = subnetIpAddressRepo.findAvailableIp(subnetId,
				new PageRequest(0, count));
		List<SubnetIpAddress> subnetIpAddressList = subnetIpAddressPage.getContent();

		for (SubnetIpAddress subnetIpAddress : subnetIpAddressList) {
			subnetIpAddress.setIsReserved(true);
			subnetIpAddress.setDeploymentName(deploymentName);
			LOGGER.info(subnetIpAddress.getIpAddress());
		}
		subnetIpAddressRepo.save(subnetIpAddressList);
		
		List<String> assignedIpList = new ArrayList<String>();
		for (SubnetIpAddress ipAddress : subnetIpAddressList) {
			assignedIpList.add(ipAddress.getIpAddress());
		}
		return assignedIpList;
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void updateJobGroupName(List<Map<String,Object>> subnetJobGroupDetails) {
		for (Map<String,Object> eachDetails : subnetJobGroupDetails) {
			SubnetIpAddress subnetIpAddress = subnetIpAddressRepo.resetIpAddress(eachDetails.get("ip_adddress").toString());
			subnetIpAddress.setJobName(eachDetails.get("job_name").toString());
			subnetIpAddress.setVmTypeId((Integer) eachDetails.get("vm_type_id"));
			subnetIpAddress.setGroupName(eachDetails.get("group_name").toString());
			subnetIpAddressRepo.save(subnetIpAddress);
		}
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Map<String,Object> getDefaultVMType(boolean isDefault) {	
		Map<String,Object> vmDetails = new HashMap<String,Object>();
		CloudProviderVm cloudProviderVm = cloudProviderVmRepo.findDefaultVMType(isDefault);
		vmDetails.put("vm_type",cloudProviderVm.getVmType());
		vmDetails.put("vm_type_id", cloudProviderVm.getVmTypeId());
		return vmDetails;
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Map<String,Object> getVMType(String vmType) {
		Map<String,Object> vmDetails = new HashMap<String,Object>();
		CloudProviderVm cloudProviderVm = cloudProviderVmRepo.findVMType(vmType);
		vmDetails.put("vm_type",cloudProviderVm.getVmType());
		vmDetails.put("vm_type_id", cloudProviderVm.getVmTypeId());
		return vmDetails;
	}
}
