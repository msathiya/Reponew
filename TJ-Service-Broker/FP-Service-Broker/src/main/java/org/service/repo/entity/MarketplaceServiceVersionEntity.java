package org.service.repo.entity;

import java.sql.Timestamp;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.CreatedDate;

/**
 * The <code>MarketplaceServiceVersionEntity</code> class represents POJO for
 * the columns of MARKETPLACE_SERVICE_VERSION table.
 * 
 * @author 439120
 *
 */
@Entity
@Table(name = "MARKETPLACE_SERVICE_VERSION")
public class MarketplaceServiceVersionEntity {

	@EmbeddedId
	private MarketplaceServiceVersionEntityPk marketplaceServiceVersionEntityPk;

	@NotNull
	@Column(name = "SERVICE_RELEASE_NOTES")
	private String serviceReleaseNotes;

	@CreatedDate
	@Column(name = "CREATED_DATE")
	private Timestamp createdDate;

	@NotNull
	@Column(name = "SERVICE_GIT_URL")
	private String serviceGitUrl;

	@NotNull
	@Column(name = "SERVICE_STATUS")
	private String serviceStatus;

	@Column(name = "CF_USER_ID")
	private String cfUserId;

	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JoinColumn(name = "SERVICE_ID", updatable = false, insertable = false)
	private MarketplaceServiceEntity marketplaceServiceEntity;

	public MarketplaceServiceVersionEntity() {

	}

	public MarketplaceServiceEntity getMarketplaceServiceEntity() {
		return marketplaceServiceEntity;
	}

	public void setMarketplaceServiceEntity(MarketplaceServiceEntity marketplaceServiceEntity) {
		this.marketplaceServiceEntity = marketplaceServiceEntity;
	}

	public MarketplaceServiceVersionEntity(String serviceId, String serviceVersion) {
		super();
		this.marketplaceServiceVersionEntityPk = new MarketplaceServiceVersionEntityPk(serviceId, serviceVersion);
	}

	public MarketplaceServiceVersionEntityPk getMarketplaceServiceVersionEntityPk() {
		return marketplaceServiceVersionEntityPk;
	}

	public void setMarketplaceServiceVersionEntityPk(
			MarketplaceServiceVersionEntityPk marketplaceServiceVersionEntityPk) {
		this.marketplaceServiceVersionEntityPk = marketplaceServiceVersionEntityPk;
	}

	public String getServiceReleaseNotes() {
		return serviceReleaseNotes;
	}

	public Timestamp getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Timestamp createdDate) {
		this.createdDate = createdDate;
	}

	public void setServiceReleaseNotes(String serviceReleaseNotes) {
		this.serviceReleaseNotes = serviceReleaseNotes;
	}

	public String getServiceGitUrl() {
		return serviceGitUrl;
	}

	public void setServiceGitUrl(String serviceGitUrl) {
		this.serviceGitUrl = serviceGitUrl;
	}

	public String getServiceStatus() {
		return serviceStatus;
	}

	public void setServiceStatus(String serviceStatus) {
		this.serviceStatus = serviceStatus;
	}

	public String getCfUserId() {
		return cfUserId;
	}

	public void setCfUserId(String cfUserId) {
		this.cfUserId = cfUserId;
	}
}
