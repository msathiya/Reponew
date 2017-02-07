package org.service.repo.entity;

import java.io.Serializable;
import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "SERVICE_VM_PROCESS")
@Embeddable
public class ServiceVmProcess implements Serializable {

	private static final long serialVersionUID = -4033605052577082744L;

	@EmbeddedId
	private ServiceVmProcessPk serviceVmProcessPk;

	@NotNull
	@Column(name = "STATUS")
	private String status;

	@Column(name = "CALL_BACK_URL")
	private String callbackUrl;

	@Column(name = "CRON_EXPRESSION")
	private String cronExpression;

	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED_TS", updatable = false)
	private Calendar createTimestamp;

	public ServiceVmProcess() {
	}

	public ServiceVmProcessPk getServiceVmProcessPk() {
		return serviceVmProcessPk;
	}

	public void setServiceVmProcessPk(ServiceVmProcessPk serviceVmProcessPk) {
		this.serviceVmProcessPk = serviceVmProcessPk;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCallbackUrl() {
		return callbackUrl;
	}

	public void setCallbackUrl(String callbackUrl) {
		this.callbackUrl = callbackUrl;
	}

	public String getCronExpression() {
		return cronExpression;
	}

	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}

	public Calendar getCreateTimestamp() {
		return createTimestamp;
	}

}
