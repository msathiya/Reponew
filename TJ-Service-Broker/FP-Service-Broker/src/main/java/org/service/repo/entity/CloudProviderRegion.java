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
@Table(name = "CLOUD_PROVIDER_REGION")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "regionId")
public class CloudProviderRegion {
	
	@Id
	@Column(name = "REGION_ID")
	private String regionId;
	
	@Column(name = "PROVIDER_NAME")
	private String providerName;
	
	@Column(name = "REGION_NAME")
	private String regionName;
	
	@Column(name = "REGION_URL")
	private String regionUrl;

	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED_TS")
	private Calendar createdDate;
	
	@OneToMany(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)		
	@JoinColumn(name = "REGION_ID", updatable = false, insertable = false)		
	private List<RegionSubnet> regionSubnet;
	
	public String getRegionId() {
		return regionId;
	}

	public void setRegionId(String regionId) {
		this.regionId = regionId;
	}
	
	public String getProviderName() {
		return providerName;
	}

	public void setProviderName(String providerName) {
		this.providerName = providerName;
	}
	
	public String getRegionName() {
		return regionName;
	}

	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}

	public String getRegionUrl() {
		return regionUrl;
	}

	public void setRegionUrl(String regionUrl) {
		this.regionUrl = regionUrl;
	}
	
	public Calendar getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Calendar createdDate) {
		this.createdDate = createdDate;
	}

	public List<RegionSubnet> getRegionSubnet() {
		return regionSubnet;
	}

	public void setRegionSubnet(List<RegionSubnet> regionSubnet) {
		this.regionSubnet = regionSubnet;
	}
}
