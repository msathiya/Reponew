package org.service.repo.entity;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Entity
@Table(name = "STEMCELL")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "stemcellCid")
public class Stemcell {

	@Id
	@Column(name = "STEMCELL_CID")
	private String stemcellCid;

	@Column(name = "STEMCELL_NAME")
	private String stemcellName;

	@Column(name = "STEMCELL_OS")
	private String stemcellOs;
	
	@Column(name = "STEMCELL_VERSION")
	private String stemcellVersion;
	
	@Column(name = "STEMCELL_URL")
	private String stemcellUrl;

	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED_TS")
	private Calendar createdDate;

	public Stemcell() {

	}

	public String getStemcellCid() {
		return stemcellCid;
	}

	public void setStemcellCid(String stemcellCid) {
		this.stemcellCid = stemcellCid;
	}

	public String getStemcellName() {
		return stemcellName;
	}

	public void setStemcellName(String stemcellName) {
		this.stemcellName = stemcellName;
	}

	public String getStemcellOs() {
		return stemcellOs;
	}

	public void setStemcellOs(String stemcellOs) {
		this.stemcellOs = stemcellOs;
	}

	public String getStemcellVersion() {
		return stemcellVersion;
	}

	public void setStemcellVersion(String stemcellVersion) {
		this.stemcellVersion = stemcellVersion;
	}

	public String getStemcellUrl() {
		return stemcellUrl;
	}

	public void setStemcellUrl(String stemcellUrl) {
		this.stemcellUrl = stemcellUrl;
	}

	public Calendar getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Calendar createdDate) {
		this.createdDate = createdDate;
	}

}
