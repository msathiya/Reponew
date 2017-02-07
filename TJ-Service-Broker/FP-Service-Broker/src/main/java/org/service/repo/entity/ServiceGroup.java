package org.service.repo.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

/***
 * 
 * @author 379587
 *
 */

@Entity
@Table(name = "SERVICE_GROUP")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "groupName")
public class ServiceGroup {
	
	@Id
	@Column(name = "GROUP_NAME")
	private String groupName;

	@Column(name = "SERVICE_ID")
	private String serviceId;
	

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

}
