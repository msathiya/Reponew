package org.service.repo.entity;

import java.util.Base64;
import java.util.Calendar;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

/**
 * The <code>MarketplaceServiceEntity</code> class represents POJO for the
 * columns of MARKETPLACE_SERVICE table.
 * 
 * @author 439120
 *
 */
@Entity
@Table(name = "MARKETPLACE_SERVICE")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "serviceId")
public class MarketplaceServiceEntity {

	@Id
	@Column(name = "SERVICE_ID")
	private String serviceId;

	@NotNull
	@Column(name = "SERVICE_NAME")
	private String serviceName;

	@Column(name = "SERVICE_DESCRIPTION")
	private String serviceDesc;

	@Column(name = "SERVICE_ICON")
	private byte[] serviceIcon;

	@Column(name = "CF_USER_ID")
	private String cfUserId;

	@Column(name = "SYSLOG_IP")
	private String syslogIp;

	@Column(name = "SYSLOG_PORT")
	private String syslogPort;

	@Column(name = "HORIZONTAL_SCALING")
	private boolean isHorizontalScaling;

	@Column(name = "VERTICAL_SCALING")
	private boolean isVerticalScaling;

	@Column(name = "IS_UPGRADE_AVAILABLE")
	private boolean isUpgradeAvailable;

	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED_TS", updatable = false)
	private Calendar createdDate;

	@ManyToOne(cascade = CascadeType.REFRESH)//, fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "REGION_ID", updatable = false, insertable = false),
			@JoinColumn(name = "SUBNET_ID", updatable = false, insertable = false),
			@JoinColumn(name = "VPC_ID", updatable = false, insertable = false) })
	private RegionSubnet regionSubnet;

	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JoinColumn(name = "STEMCELL_CID", updatable = false, insertable = false)
	private Stemcell stemcell;

	@OneToMany(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JoinColumn(name = "SERVICE_ID", updatable = false, insertable = false)
	private List<MarketplaceServiceVersionEntity> marketplaceServiceVersionEntity;
	
	@OneToMany(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JoinColumn(name = "SERVICE_ID", updatable = false, insertable = false)
	private List<ServiceGroup> serviceGroup;

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getServiceDesc() {
		return serviceDesc;
	}

	public void setServiceDesc(String serviceDesc) {
		this.serviceDesc = serviceDesc;
	}

	public String getServiceIcon() {
		if (serviceIcon != null) {
			return Base64.getEncoder().encodeToString(serviceIcon);
		} else {
			return null;
		}
	}

	public void setServiceIcon(byte[] serviceIcon) {
		this.serviceIcon = serviceIcon;
	}

	public String getCfUserId() {
		return cfUserId;
	}

	public void setCfUserId(String cfUserId) {
		this.cfUserId = cfUserId;
	}

	public String getSyslogIp() {
		return syslogIp;
	}

	public void setSyslogIp(String syslogIp) {
		this.syslogIp = syslogIp;
	}

	public String getSyslogPort() {
		return syslogPort;
	}

	public void setSyslogPort(String syslogPort) {
		this.syslogPort = syslogPort;
	}

	public boolean isHorizontalScaling() {
		return isHorizontalScaling;
	}

	public void setHorizontalScaling(boolean isHorizontalScaling) {
		this.isHorizontalScaling = isHorizontalScaling;
	}

	public boolean isVerticalScaling() {
		return isVerticalScaling;
	}

	public void setVerticalScaling(boolean isVerticalScaling) {
		this.isVerticalScaling = isVerticalScaling;
	}

	public boolean isUpgradeAvailable() {
		return isUpgradeAvailable;
	}

	public void setUpgradeAvailable(boolean isUpgradeAvailable) {
		this.isUpgradeAvailable = isUpgradeAvailable;
	}

	public Calendar getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Calendar createdDate) {
		this.createdDate = createdDate;
	}

	public Stemcell getStemcell() {
		return stemcell;
	}

	public void setStemcell(Stemcell stemcell) {
		this.stemcell = stemcell;
	}

	public List<MarketplaceServiceVersionEntity> getMarketplaceServiceVersionEntity() {
		return marketplaceServiceVersionEntity;
	}

	public void setMarketplaceServiceVersionEntity(
			List<MarketplaceServiceVersionEntity> marketplaceServiceVersionEntity) {
		this.marketplaceServiceVersionEntity = marketplaceServiceVersionEntity;
	}

	public RegionSubnet getRegionSubnet() {
		return regionSubnet;
	}

	public void setRegionSubnet(RegionSubnet regionSubnet) {
		this.regionSubnet = regionSubnet;
	}

	public List<ServiceGroup> getServiceGroup() {
		return serviceGroup;
	}

	public void setServiceGroup(List<ServiceGroup> serviceGroup) {
		this.serviceGroup = serviceGroup;
	}

}
