package org.service.repo.entity;

import java.sql.Timestamp;
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
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

/**
 * The <code>TenantEntity</code> class represents POJO for the
 * columns of TENANT table.
 * 
 * @author 439120
 *
 */
@Entity
@Table(name = "TENANT")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "tenantId")
public class TenantEntity {

	@Id
	@Column(name = "TENANT_ID")
	private String tenantId;

	@NotNull
	@Column(name = "TENANT_NAME")
	private String tenantName;

	
	@NotNull
	@Column(name = "EXPIRY_DATE")
	private Timestamp expiryDate;	
	
	
	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATE_TS")
	private Calendar createDate;
	
	@OneToMany(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JoinColumn(name = "TENANT_ID", updatable = false, insertable = false)
	private List<BoshInstanceEntity> boshInstances;
	
	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getTenantName() {
		return tenantName;
	}

	public void setTenantName(String tenantName) {
		this.tenantName = tenantName;
	}

	public Timestamp getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Timestamp expiryDate) {
		this.expiryDate = expiryDate;
	}

	public List<BoshInstanceEntity> getBoshInstances() {
		return boshInstances;
	}

	public void setBoshInstances(List<BoshInstanceEntity> boshInstances) {
		this.boshInstances = boshInstances;
	}

	public Calendar getCreateDate() {
		return createDate;
	}
  
}
