package org.service.json;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The <code>Scale</code> class scale JSON object.
 *
 * @author Cognizant
 */
@JsonInclude(Include.NON_NULL)
public class HorizontalScale {
	public static final String SCALE_IN = "SCALE_IN";
	public static final String SCALE_OUT = "SCALE_OUT";

	@JsonProperty(required = false)
	private String jobName;
	
	@JsonProperty(required = false)
	private String groupName;
	
	@JsonProperty(required = false)
	private Integer noOfInstances;
	
	@JsonProperty(required = false)
	private String type;

	@JsonProperty(required = false)
	private List<String> ipAddress;

	@JsonProperty(required = false)
	private String yaml;

	public String getGroupName() {
		return groupName;
	}

	public void setYaml(String yaml) {
		this.yaml = yaml;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setIpAddress(List<String> ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getYaml() {
		return yaml;
	}

	public String getType() {
		return type;
	}

	public List<String> getIpAddress() {
		return ipAddress;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public Integer getNoOfInstances() {
		return noOfInstances;
	}

	public void setNoOfInstances(Integer noOfInstances) {
		this.noOfInstances = noOfInstances;
	}

}
