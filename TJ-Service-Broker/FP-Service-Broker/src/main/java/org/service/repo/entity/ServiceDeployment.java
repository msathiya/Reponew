package org.service.repo.entity;

import java.util.Calendar;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

/***
 * 
 * @author 379587
 *
 */

@Entity
@Table(name = "SERVICE_DEPLOYMENT")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "serviceDeploymentUUID")
public class ServiceDeployment {

	@Id
	@Column(name = "SERVICE_DEPLOYMENT_UUID")
	private String serviceDeploymentUUID;

	@NotNull
	@Column(name = "PLAN_UUID")
	private String planUUID;

	@Column(name = "TASK_URL")
	private String taskUrl;

	@NotNull
	@Column(name = "DEPLOYMENT_NAME")
	private String deploymentName;

	@Column(name = "CF_SERVICE_NAME")
	private String cfServiceName;

	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED_TS", updatable = false)
	private Calendar createTimestamp;

	@UpdateTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "UPDATED_TS", insertable = false)
	private Calendar updateTimestamp;

	@OneToMany(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JoinColumn(name = "SERVICE_DEPLOYMENT_UUID", updatable = false)
	private List<Service> services;

	@OneToMany(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JoinColumn(name = "SERVICE_DEPLOYMENT_UUID", updatable = false)
	private List<DeploymentManifest> deploymentManifest;

	@JsonIgnore
	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JoinColumn(name = "SERVICE_ID", updatable = false, insertable = false)
	private MarketplaceServiceEntity marketplaceServiceEntity;

	public String getServiceDeploymentUUID() {
		return serviceDeploymentUUID;
	}

	public void setServiceDeploymentUUID(String serviceDeploymentUUID) {
		this.serviceDeploymentUUID = serviceDeploymentUUID;
	}

	public String getPlanUUID() {
		return planUUID;
	}

	public void setPlanUUID(String planUUID) {
		this.planUUID = planUUID;
	}

	public String getTaskUrl() {
		return taskUrl;
	}

	public void setTaskUrl(String taskUrl) {
		this.taskUrl = taskUrl;
	}

	public String getDeploymentName() {
		return deploymentName;
	}

	public void setDeploymentName(String deploymentName) {
		this.deploymentName = deploymentName;
	}

	public Calendar getCreateTimestamp() {
		return createTimestamp;
	}

	public Calendar getUpdateTimestamp() {
		return updateTimestamp;
	}

	public List<Service> getServices() {
		return services;
	}

	public void setServices(List<Service> services) {
		this.services = services;
	}

	public void setDeploymentManifests(List<DeploymentManifest> deploymentManifests) {
		this.deploymentManifest = deploymentManifests;
	}

	public List<DeploymentManifest> getDeploymentManifests() {
		return deploymentManifest;
	}

	public String getCfServiceName() {
		return cfServiceName;
	}

	public void setCfServiceName(String cfServiceName) {
		this.cfServiceName = cfServiceName;
	}

	public void setCreateTimestamp(Calendar createTimestamp) {
		this.createTimestamp = createTimestamp;
	}

	public void setUpdateTimestamp(Calendar updateTimestamp) {
		this.updateTimestamp = updateTimestamp;
	}

	public MarketplaceServiceEntity getMarketplaceServiceEntity() {
		return marketplaceServiceEntity;
	}

	public void setMarketplaceServiceEntity(MarketplaceServiceEntity marketplaceServiceEntity) {
		this.marketplaceServiceEntity = marketplaceServiceEntity;
	}

}
