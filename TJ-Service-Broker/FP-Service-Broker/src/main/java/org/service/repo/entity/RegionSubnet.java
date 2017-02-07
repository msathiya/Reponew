package org.service.repo.entity;

import java.util.Calendar;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "REGION_SUBNET")
public class RegionSubnet {

	@EmbeddedId
	private RegionSubnetPk regionSubnetPk;

	@Column(name = "SUBNET_NAME")
	private String subnetName;

	@Column(name = "SUBNET_DESC")
	private String subnetDesc;

	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED_TS")
	private Calendar createdDate;

	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JoinColumn(name = "REGION_ID", updatable = false, insertable = false)
	private CloudProviderRegion cloudProviderRegion;

	@OneToMany(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "REGION_ID", updatable = false, insertable = false),
			@JoinColumn(name = "SUBNET_ID", updatable = false, insertable = false),
			@JoinColumn(name = "VPC_ID", updatable = false, insertable = false) })
	private List<SubnetIpAddress> subnetIpAddress;

	public RegionSubnet() {

	}

	public RegionSubnet(RegionSubnetPk regionSubnetPk) {
		this.regionSubnetPk = regionSubnetPk;
	}

	public RegionSubnetPk getRegionSubnetPk() {
		return regionSubnetPk;
	}

	public String getSubnetName() {
		return subnetName;
	}

	public void setSubnetName(String subnetName) {
		this.subnetName = subnetName;
	}

	public String getSubnetDesc() {
		return subnetDesc;
	}

	public void setSubnetDesc(String subnetDesc) {
		this.subnetDesc = subnetDesc;
	}

	public Calendar getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Calendar createdDate) {
		this.createdDate = createdDate;
	}

	public CloudProviderRegion getCloudProviderRegion() {
		return cloudProviderRegion;
	}

	public void setCloudProviderRegion(CloudProviderRegion cloudProviderRegion) {
		this.cloudProviderRegion = cloudProviderRegion;
	}

}
