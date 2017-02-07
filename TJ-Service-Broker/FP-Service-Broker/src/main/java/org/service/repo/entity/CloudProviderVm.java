package org.service.repo.entity;

import java.util.Calendar;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Entity
@Table(name = "CLOUD_PROVIDER_VM")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "vmTypeId")
public class CloudProviderVm {
	
	@Id
	@Column(name = "VM_TYPE_ID")
	private Integer vmTypeId;
	
	@Column(name = "VM_TYPE")
	private String vmType;

	@Column(name = "IS_DEFAULT")
	private Boolean isDefault;
	
	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED_TS")
	private Calendar createdDate;
	
	@OneToMany(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)		
	@JoinColumn(name = "VM_TYPE_ID", updatable = false, insertable = false)		
	private List<SubnetIpAddress> subnetIpAddress;

	public Integer getVmTypeId() {
		return vmTypeId;
	}

	public void setVmTypeId(Integer vmTypeId) {
		this.vmTypeId = vmTypeId;
	}

	public String getVmType() {
		return vmType;
	}

	public void setVmType(String vmType) {
		this.vmType = vmType;
	}

	public List<SubnetIpAddress> getSubnetIpAddress() {
		return subnetIpAddress;
	}

	public void setSubnetIpAddress(List<SubnetIpAddress> subnetIpAddress) {
		this.subnetIpAddress = subnetIpAddress;
	}
	
	
	public Calendar getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Calendar createdDate) {
		this.createdDate = createdDate;
	}

	public boolean isDefault() {
		return isDefault;
	}

}
