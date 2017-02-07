package org.service.repo.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

@Embeddable
public class RegionSubnetPk implements Serializable {

	private static final long serialVersionUID = -4033605052577082744L;

	@NotNull
	@Column(name = "REGION_ID")
	private String regionId;

	@NotNull
	@Column(name = "SUBNET_ID")
	private String subnetId;

	@NotNull
	@Column(name = "VPC_ID")
	private String vpcId;

	public RegionSubnetPk() {

	}

	public String getRegionId() {
		return regionId;
	}

	public void setRegionId(String regionId) {
		this.regionId = regionId;
	}

	public String getSubnetId() {
		return subnetId;
	}

	public void setSubnetId(String subnetId) {
		this.subnetId = subnetId;
	}

	public String getVpcId() {
		return vpcId;
	}

	public void setVpcId(String vpcId) {
		this.vpcId = vpcId;
	}

}
