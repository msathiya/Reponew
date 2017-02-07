package org.service.repo.entity;

import java.util.Calendar;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Entity
@Table(name = "SUBNET_IP_ADDRESS")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "ipAddress")
public class SubnetIpAddress {

	@Id
	@Column(name = "IP_ADDRESS")
	private String ipAddress;

	@Column(name = "IS_RESERVED")
	private Boolean isReserved;

	@Column(name = "DEPLOYMENT_NAME")
	private String deploymentName;

	@Column(name = "JOB_NAME")
	private String jobName;

	@Column(name = "GROUP_NAME")
	private String groupName;

	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED_TS")
	private Calendar createdDate;

	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "REGION_ID", updatable = false, insertable = false),
			@JoinColumn(name = "SUBNET_ID", updatable = false, insertable = false),
			@JoinColumn(name = "VPC_ID", updatable = false, insertable = false) })
	private RegionSubnet regionSubnet;

	@Column(name = "VM_TYPE_ID")
	private Integer vmTypeId;

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public Boolean getIsReserved() {
		return isReserved;
	}

	public void setIsReserved(Boolean isReserved) {
		this.isReserved = isReserved;
	}

	public String getDeploymentName() {
		return deploymentName;
	}

	public void setDeploymentName(String deploymentName) {
		this.deploymentName = deploymentName;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public Calendar getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Calendar createdDate) {
		this.createdDate = createdDate;
	}

	public RegionSubnet getRegionSubnet() {
		return regionSubnet;
	}

	public void setRegionSubnet(RegionSubnet regionSubnet) {
		this.regionSubnet = regionSubnet;
	}

	public Integer getVmTypeId() {
		return vmTypeId;
	}

	public void setVmTypeId(Integer vmTypeId) {
		this.vmTypeId = vmTypeId;
	}

}
