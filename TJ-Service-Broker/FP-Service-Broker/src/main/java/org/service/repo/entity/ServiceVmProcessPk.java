package org.service.repo.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

@Embeddable
public class ServiceVmProcessPk implements Serializable {

	private static final long serialVersionUID = 5367623814624806642L;

	@NotNull
	@Column(name = "SERVICE_UUID")
	private String serviceUUID;

	@NotNull
	@Column(name = "PROCESS_NAME")
	private String processName;

	public ServiceVmProcessPk() {
	}

	public ServiceVmProcessPk(String serviceUUID, String processName) {
		super();
		this.serviceUUID = serviceUUID;
		this.processName = processName;
	}

	public String getServiceUUID() {
		return serviceUUID;
	}

	public void setServiceUUID(String serviceUUID) {
		this.serviceUUID = serviceUUID;
	}

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

}
