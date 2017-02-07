package org.service.tj.data;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * The <code>ServiceEnvironment</code> class is to hold environment variables.
 * 
 * @author Rajnish (418618)
 */
@Configuration
public class ResourceEnvironment {
	@Value("${org.db.service.name}")
	private String serviceName;
	@Value("${org.service.guid}")
	private String serviceGUID;

	@Value("${org.service.plan.sandbox.guid}")
	private String planSandboxGUID;
	@Value("${org.service.plan.sandbox.instance}")
	private String planSandboxInstance;

	@Value("${org.service.plan.shared.cluster.guid}")
	private String planSharedClusterGUID;
	@Value("${org.service.plan.shared.cluster.instance}")
	private String planSharedClusterInstance;

	@Value("${org.service.plan.dedicated.single.guid}")
	private String planDedicatedSingleGUID;
	@Value("${org.service.plan.dedicated.single.instance}")
	private String planDedicatedSingleInstance;

	@Value("${org.service.plan.dedicated.cluster.guid}")
	private String planDedicatedClusterGUID;
	@Value("${org.service.plan.dedicated.cluster.instance}")
	private String planDedicatedClusterInstance;

	@Value("${org.service.default.port}")
	private String serverDefaultPort;
	@Value("${org.service.default.username}")
	private String serverDefaultUsername;
	@Value("${org.service.default.password}")
	private String serverDefaultPassword;

	public String getServiceName() {
		return serviceName;
	}

	public String getServiceGUID() {
		return serviceGUID;
	}

	public String getPlanSandboxGUID() {
		return planSandboxGUID;
	}

	public String getPlanSandboxInstance() {
		return planSandboxInstance;
	}

	public String getPlanSharedClusterGUID() {
		return planSharedClusterGUID;
	}

	public String getPlanSharedClusterInstance() {
		return planSharedClusterInstance;
	}

	public String getPlanDedicatedSingleGUID() {
		return planDedicatedSingleGUID;
	}

	public String getPlanDedicatedSingleInstance() {
		return planDedicatedSingleInstance;
	}

	public String getPlanDedicatedClusterGUID() {
		return planDedicatedClusterGUID;
	}

	public String getPlanDedicatedClusterInstance() {
		return planDedicatedClusterInstance;
	}

	public String getServerDefaultPort() {
		return serverDefaultPort;
	}

	public String getServerDefaultUsername() {
		return serverDefaultUsername;
	}

	public String getServerDefaultPassword() {
		return serverDefaultPassword;
	}

}
