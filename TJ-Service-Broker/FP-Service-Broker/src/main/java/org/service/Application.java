package org.service;

import java.util.Set;

import org.service.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

/**
 * The <code>Application</code> class represents startup application.
 
 * @author Rajnish(rajnishkumar.pandey@cognizant.com)
 */
@SpringBootApplication
@ComponentScan
@EnableAutoConfiguration(exclude = { SecurityAutoConfiguration.class })
public class Application {

	/**
	 * Holds LOGGER reference.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

	/**
	 * This method is invoked while application start up.
	 * 
	 * @param args
	 *            holds string array.
	 */
	public static void main(String[] args) {

		Set<String> envKeys = System.getenv().keySet();
		for (String envKey : envKeys) {
			LOGGER.info("Environment - {} : {}", envKey, System.getenv(envKey));
		}

		ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
		Resource resource = context.getBean(Resource.class);

		LOGGER.info("{} Service-Broker  - Started", resource.getName());
	}
}