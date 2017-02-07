package org.service.tj;

import org.service.model.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * The <code>Application</code> class represents startup application.
 * 
 * @author Rajnish(rajnishkumar.pandey@cognizant.com)
 */
@Configuration
@ImportResource("classpath:spring/TJapp-context.xml")
public class ResourceConfiguration {

	@Bean
	public Resource resouce(){
		Resource resource = new Resource();
		resource.setName("TJapp");
		return resource;
	}
}