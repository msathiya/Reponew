package org.service.repo.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

/**
 * The <code>MarketplaceServiceVersionEntityPk</code> class represents POJO for
 * the columns of MARKETPLACE_SERVICE_VERSION table for primary key columns.
 * 
 * @author 439120
 *
 */
@Embeddable
public class MarketplaceServiceVersionEntityPk implements Serializable {

	private static final long serialVersionUID = -4033605052577082744L;

	@NotNull
	@Column(name = "SERVICE_ID")
	private String serviceId;

	@NotNull
	@Column(name = "SERVICE_VERSION")
	private String serviceVersion;

	public MarketplaceServiceVersionEntityPk() {

	}

	public MarketplaceServiceVersionEntityPk(String serviceId, String serviceVersion) {
		super();
		this.serviceId = serviceId;
		this.serviceVersion = serviceVersion;
	}

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public String getServiceVersion() {
		return serviceVersion;
	}

	public void setServiceVersion(String serviceVersion) {
		this.serviceVersion = serviceVersion;
	}
}
