package org.service.repo.entity;

import java.nio.charset.StandardCharsets;
import java.util.Calendar;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "DEPLOYMENT_MANIFEST")
public class DeploymentManifest {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "DEPLOYMENT_MANIFEST_ID")
	private Integer deployment_manifest_id;

	@NotNull
	@Lob
	@Column(name = "DEPLOYMENT_YAML")
	private byte[] deploymentYaml;

	@NotNull
	@Column(name = "IS_ACTIVE")
	private Boolean isActive;

	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED_TS", updatable = false)
	private Calendar createTimestamp;

	@NotNull
	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.REFRESH }, fetch = FetchType.LAZY)
	@JoinColumn(name = "SERVICE_DEPLOYMENT_UUID", updatable = false)
	private ServiceDeployment serviceDeployment;

	public String getDeploymentYaml() {

		return StringUtils.toEncodedString(deploymentYaml, StandardCharsets.UTF_8);
	}

	public void setDeploymentYaml(byte[] deploymentYaml) {
		this.deploymentYaml = deploymentYaml;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public Calendar getCreateTimestamp() {
		return createTimestamp;
	}

	public ServiceDeployment getServiceDeployment() {
		return serviceDeployment;
	}

	public void setServiceDeployment(ServiceDeployment serviceDeployment) {
		this.serviceDeployment = serviceDeployment;
	}
}