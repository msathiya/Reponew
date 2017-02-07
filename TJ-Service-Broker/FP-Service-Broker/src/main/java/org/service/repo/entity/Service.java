package org.service.repo.entity;

import java.nio.charset.StandardCharsets;
import java.util.Calendar;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
/***
 * 
 * @author 379587
 *
 */


@Entity
@Table(name = "SERVICE")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "serviceUUID")
public class Service {
	
	@Id
	@Column(name = "SERVICE_UUID")
	private String serviceUUID;

	@Column(name = "STATUS")
	private String status;

	@Lob
	@Column(name = "PROPERTIES_JSON")
	private byte[] propertiesJson;

	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED_TS", updatable = false)
	private Calendar createTimestamp;

	@UpdateTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "UPDATED_TS", insertable = false)
	private Calendar updateTimestamp;

	@ManyToOne(cascade = {CascadeType.REFRESH }, fetch = FetchType.LAZY)
	@JoinColumn(name = "SERVICE_DEPLOYMENT_UUID", updatable = false)
	private ServiceDeployment serviceDeployment;

	public String getServiceUUID() {
		return serviceUUID;
	}

	public void setServiceUUID(String serviceUUID) {
		this.serviceUUID = serviceUUID;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPropertiesJson() {
		return new String(propertiesJson, StandardCharsets.UTF_8);
	}

	public void setPropertiesJson(byte[] propertiesJson) {
		this.propertiesJson = propertiesJson;
	}

	public Calendar getCreateTimestamp() {
		return createTimestamp;
	}

	public void setCreateTimestamp(Calendar createTimestamp) {
		this.createTimestamp = createTimestamp;
	}

	public void setUpdateTimestamp(Calendar updateTimestamp) {
		this.updateTimestamp = updateTimestamp;
	}

	public Calendar getUpdateTimestamp() {
		return updateTimestamp;
	}
	
	public ServiceDeployment getServiceDeployment() {
		return serviceDeployment;
	}

	public void setServiceDeployment(ServiceDeployment serviceDeployment) {
		this.serviceDeployment = serviceDeployment;
	}

}
