package org.service.dao;

import java.util.List;

/**
 * The <code>ServiceGroupDao</code> interface represents interface for
 * creating,updating and finding service group.
 * 
 * @author Sandeep (sandeep.visvanathan@cognizant.com)
 * @author Deepthi (deepthi.g2@cognizant.com)
 * @author Sundar (sundarajan.srinivasan@cognizant.com)
 * @author Ramkumar(ramkumar.kandhakumar@cognizant.com)
 *
 */
public interface ServiceGroupDao {

	List<String> getServiceGroupNames(Integer serviceId);
}