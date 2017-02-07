package org.service.repo.entity;

import java.sql.Timestamp;
import java.util.Calendar;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

/**
 * The <code>BoshInstanceEntity</code> class represents POJO for the
 * columns of BOSH_INSTANCE table.
 * 
 * @author 439120
 *
 */
@Entity
@Table(name = "BOSH_INSTANCE")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "instanceUUID")
public class BoshInstanceEntity {

	@Id
	@NotNull
	@Column(name = "INSTANCE_UUID")
	private String instanceUUID;
	
	@NotNull
	@Column(name = "INSTANCE_NAME")
	private String instanceName;
	
	@NotNull
	@Column(name = "INSTANCE_IP")
	private String instanceIp;
	
	@NotNull
	@Column(name = "INSTANCE_PORT")
	private String instancePort;
	
	@NotNull
	@Column(name = "CF_API")
	private String cfApi;	

	
	@NotNull
	@Column(name = "EXPIRY_DATE")
	private Timestamp expiryDate;	
	
	
	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATE_TS")
	private Calendar createDate;
	
	@ManyToOne(cascade = CascadeType.REFRESH , fetch = FetchType.LAZY)
	@JoinColumn(name = "TENANT_ID", insertable = false ,updatable = false)
	private TenantEntity tenant;


	public String getInstanceUUID() {
		return instanceUUID;
	}


	public void setInstanceUUID(String instanceUUID) {
		this.instanceUUID = instanceUUID;
	}


	public String getInstanceName() {
		return instanceName;
	}


	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
	}


	public String getInstanceIp() {
		return instanceIp;
	}


	public void setInstanceIp(String instanceIp) {
		this.instanceIp = instanceIp;
	}


	public String getInstancePort() {
		return instancePort;
	}


	public void setInstancePort(String instancePort) {
		this.instancePort = instancePort;
	}


	public String getCfApi() {
		return cfApi;
	}


	public void setCfApi(String cfApi) {
		this.cfApi = cfApi;
	}


	public Timestamp getExpiryDate() {
		return expiryDate;
	}


	public void setExpiryDate(Timestamp expiryDate) {
		this.expiryDate = expiryDate;
	}


	public TenantEntity getTenant() {
		return tenant;
	}


	public void setTenant(TenantEntity tenant) {
		this.tenant = tenant;
	}


	public Calendar getCreateDate() {
		return createDate;
	}
	
	
  
}
