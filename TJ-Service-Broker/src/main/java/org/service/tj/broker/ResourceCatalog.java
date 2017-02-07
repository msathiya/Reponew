package org.service.tj.broker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.service.tj.data.ResourceEnvironment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.servicebroker.model.Catalog;
import org.springframework.cloud.servicebroker.model.Plan;
import org.springframework.cloud.servicebroker.model.ServiceDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
 * CF-BOT catalog with service and plans to be displayed in marketplace
 */
@Configuration
public class ResourceCatalog {

	@Autowired
	private ResourceEnvironment rEnvironment;

	@Bean
	public Catalog catalog() {
		String brokerServiceName = rEnvironment.getServiceName();
		List<String> tags = new ArrayList<String>();
		tags.add("svc_offr_bots");
		return new Catalog(Collections.singletonList(new ServiceDefinition(rEnvironment.getServiceGUID(),
				brokerServiceName, "Highly available TJ services for your app", true, false,
				Arrays.asList(
						new Plan(rEnvironment.getPlanSandboxGUID(),
						 "Default",
						 "Great for getting started and developing your apps",
						 null, true)
//					,
//					new Plan(rEnvironment.getPlanDedicatedSingleGUID(), "Dedicated Single-Node",
//							"Dedicated Database and Best for light production", null, true)
//	//			 ,
				// new Plan(rEnvironment.getPlanSharedClusterGUID(), "Shared
				// Cluster",
				// "Shared database cluster for getting started", null, false),
				// new Plan(rEnvironment.getPlanDedicatedClusterGUID(),
				// "Dedicated Cluster",
				// "Dedicated Database cluster for production", null, false)
				), tags, null, null, null)));
	}
}
