package org.service.data;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * The <code>DBEnvironment</code> class is to hold environment variables.
 * 
 * @author Sandeep (439001)
 * @author Deepthi (439120)
 * @author Sundar (320923)
 * @author RamKumar (205248)
 */
@Configuration
public class ServiceEnvironment {

	@Value("${org.service.bosh.ip}")
	private String boshIpAddress;
	@Value("${org.service.bosh.port}")
	private int boshPort;
	@Value("${org.service.bosh.protocol}")
	private String boshProtocol;
	@Value("${org.service.bosh.authorization}")
	private String boshAuthorization;
	@Value("${org.service.bosh.sleep}")
	private long boshSleep;

	@Value("${org.service.bosh.etcd.ip}")
	private String boshEtcdIp;
	@Value("${org.service.bosh.directoruuid}")
	private String boshDirectorUUID;

	@Value("${spring.datasource.url}")
	private String url;
	
	@Value("${spring.datasource.url.nodbname}")
	private String urlNoDbName;

	@Value("${spring.datasource.url.defaultdb}")
	private String defaultDb;

	@Value("${spring.datasource.username}")
	private String userName;	
	@Value("${spring.datasource.password}")
	private String password;

	@Value("${cf.api.url.https}")
	private String cfHttpsUrl;
	@Value("${cf.api.host.name}")
	private String apiHostName;
	
	@Value("${cf.service.broker.process.url}")
	private String serviceBrokerProcessUrl;
	

	@Value("${org.service.bosh.cfbot.password}")
	private String cfbotPassword;	
	@Value("${org.service.bosh.aws.secretKey}")
	private String awsSecretKey;
	@Value("${org.service.bosh.aws.hostName}")
	private String hostName;
	@Value("${org.service.bosh.aws.availabilityzone}")
	private String availabilityZone;

	@Value("${cf.login.url}")
	private String cfLoginUrl;

	@Value("${org.service.bosh.aws.subnet}")
	private String subnet;
	@Value("${org.service.bosh.aws.securitygroup}")
	private String securitygroup;

	@Value("${cf.space.name}")
	private String cfSpaceName;
	
	@Value("${yaml.director.uuid}")
	private String yamlDirectorUUID;
	@Value("${yaml.gateway.ip}")
	private String yamlGatewayIp;
	@Value("${yaml.range.ip}")
	private String yamlRangeIp;
	
	@Value("${org.service.bosh.aws.accessKey}")
	private String awsAccessKey;
	
	@Value("${org.service.bosh.cfbot.slacktoken}")
	private String cfbotSlackToken;
	
	@Value("${org.service.cfbot.replace}")
	private String cfbotreplace;
	@Value("${org.service.cfbot.replacewith}")
	private String cfbotreplacewith;
	@Value("${org.service.cfbot.replacerequired}")
	private String cfbotreplacerequired;
	@Value("${org.service.cfbot.replacewithrequired}")
	private String cfbotreplacewithrequired;
	@Value("${org.service.cfbot.replaceappname}")
	private String cfbotreplaceappname;
	@Value("${org.service.cfbot.replacewithappname}")
	private String cfbotreplacewithappname;
	@Value("${org.service.bosh.cfbot.username}")
	private String cfbotUserName;
	@Value("${org.service.bosh.cfbot.api}")
	private String cfbotApi;
	@Value("${org.service.cfbot.git.url}")
	public String gitUrl;
	@Value("${org.service.cfbot.git.username}")
	public String gitUserName;
	@Value("${org.service.cfbot.git.password}")
	public String gitPassword;
	@Value("${org.service.cfbot.git.download.path}")
	public String gitDownloadPath;

	@Value("${cf.service.sdf.url}")
	private String cfsdfURL;
	
	public String getBoshDirectorUUID() {
		return boshDirectorUUID;
	}
	
	public String getBoshEtcdIp() {
		return boshEtcdIp;
	}
	
	public String getApiHostName() {
		return apiHostName;
	}

	public String getServiceBrokerProcessUrl() {
		return serviceBrokerProcessUrl;
	}

	public String getCfSpaceName() {
		return cfSpaceName;
	}

	public String getSubnet() {
		return subnet;
	}

	public String getSecuritygroup() {
		return securitygroup;
	}

	public String getCfbotSlackToken() {
		return cfbotSlackToken;
	}
	/**
	 * @return the cfsdfURL
	 */
	public String getCfsdfURL() {
		return cfsdfURL;
	}

	public String getCfbotreplacerequired() {
		return cfbotreplacerequired;
	}

	public String getCfbotreplacewithrequired() {
		return cfbotreplacewithrequired;
	}

	public String getCfbotreplaceappname() {
		return cfbotreplaceappname;
	}

	public String getCfbotreplacewithappname() {
		return cfbotreplacewithappname;
	}

	public String getCfbotreplace() {
		return cfbotreplace;
	}

	public String getCfbotreplacewith() {
		return cfbotreplacewith;
	}

	public String getCfbotApi() {
		return cfbotApi;
	}

	public String getGitUrl() {
		return gitUrl;
	}

	public String getGitUserName() {
		return gitUserName;
	}

	public String getGitPassword() {
		return gitPassword;
	}

	public String getGitDownloadPath() {
		return gitDownloadPath;
	}

	public String getCfbotUserName() {
		return cfbotUserName;
	}
	
	public String getUserName() {
		return userName;
	}

	public String getPassword() {
		return password;
	}
	
	public String getCfbotPassword() {
		return cfbotPassword;
	}

	public String getAwsAccessKey() {
		return awsAccessKey;
	}

	public String getAwsSecretKey() {
		return awsSecretKey;
	}

	public String getHostName() {
		return hostName;
	}

	public String getAvailabilityZone() {
		return availabilityZone;
	}

	public String getCfHttpsUrl() {
		return cfHttpsUrl;
	}

	public String getCfLoginUrl() {
		return cfLoginUrl;
	}

	public String getUrl() {
		return url;
	}
	
	
	public String getUrlNoDbName() {
		return urlNoDbName;
	}
	
	public String getDefaultDb() {
		return defaultDb;
	}
	
	public long getBoshSleep() {
		return boshSleep;
	}

	public String getBoshIpAddress() {
		return boshIpAddress;
	}

	public int getBoshPort() {
		return boshPort;
	}

	public String getBoshProtocol() {
		return boshProtocol;
	}

	public String getBoshAuthorization() {
		return boshAuthorization;
	}

	public String getYamlDirectorUUID() {
		return yamlDirectorUUID;
	}

	public String getYamlGatewayIp() {
		return yamlGatewayIp;
	}

	public String getYamlRangeIp() {
		return yamlRangeIp;
	}

}
