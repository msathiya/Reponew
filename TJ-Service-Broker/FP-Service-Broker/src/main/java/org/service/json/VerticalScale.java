package org.service.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The <code>Scale</code> class scale JSON object.
 *
 * @author Cognizant
 */
@JsonInclude(Include.NON_NULL)
public class VerticalScale {

	@JsonProperty(required = true)
	private String jobName;
	@JsonProperty(required = true)
	private String oldInstanceType;
	@JsonProperty(required = true)
	private String newInstanceType;

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getOldInstanceType() {
		return oldInstanceType;
	}

	public void setOldInstanceType(String oldInstanceType) {
		this.oldInstanceType = oldInstanceType;
	}

	public String getNewInstanceType() {
		return newInstanceType;
	}

	public void setNewInstanceType(String newInstanceType) {
		this.newInstanceType = newInstanceType;
	}

}
