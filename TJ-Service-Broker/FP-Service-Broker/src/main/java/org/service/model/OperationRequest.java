package org.service.model;

public class OperationRequest {
	private String deploymentName;
	private String instanceCount;
	private String sqlQuery;
	
	private String hadoopServiceName ;
	private String hadoopPlanName;
	

	private String mountPathServiceName;
	private String mongoRole;
	private String serviceName;
	
	private String vaultRootToken;
	
	public String getSqlQuery() {
		return sqlQuery;
	}

	public void setSqlQuery(String sqlQuery) {
		this.sqlQuery = sqlQuery;
	}

	
	public String getVaultRootToken() {
		return vaultRootToken;
	}

	public void setVaultRootToken(String vaultRootToken) {
		this.vaultRootToken = vaultRootToken;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getMongoRole() {
		return mongoRole;
	}

	public void setMongoRole(String mongoRole) {
		this.mongoRole = mongoRole;
	}

	public String getMountPathServiceName() {
		return mountPathServiceName;
	}

	public void setMountPathServiceName(String mountPathServiceName) {
		this.mountPathServiceName = mountPathServiceName;
	}

	public String getDeploymentName() {
		return deploymentName;
	}

	public void setDeploymentName(String deploymentName) {
		this.deploymentName = deploymentName;
	}

	public String getInstanceCount() {
		return instanceCount;
	}

	public void setInstanceCount(String instanceCount) {
		this.instanceCount = instanceCount;
	}
	
	public String getHadoopServiceName() {
		return hadoopServiceName;
	}

	public void setHadoopServiceName(String hadoopServiceName) {
		this.hadoopServiceName = hadoopServiceName;
	}

	public String getHadoopPlanName() {
		return hadoopPlanName;
	}

	public void setHadoopPlanName(String hadoopPlanName) {
		this.hadoopPlanName = hadoopPlanName;
	}
}
