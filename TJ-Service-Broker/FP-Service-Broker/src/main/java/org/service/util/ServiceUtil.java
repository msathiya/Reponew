package org.service.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The <code>ServiceUtil</code> class is generac utility for service.
 * 
 * @author Sandeep (sandeep.visvanathan@cognizant.com)
 * @author Deepthi (deepthi.g2@cognizant.com)
 * @author Sundar (sundarajan.srinivasan@cognizant.com)
 * @author Ramkumar(ramkumar.kandhakumar@cognizant.com)
 */
public class ServiceUtil {

	/**
	 * LOGGER variable
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ServiceUtil.class);

	/**
	 * Private constructor
	 */
	private ServiceUtil() {
		// Utility class.
	}

	/**
	 * Method to get sublist for given list with end index.
	 * 
	 * @param list holds list reference.
	 * @param endIndex holds end index.
	 * @return List<T> returns list of sub list.
	 */
	public static <T> List<T> getSubList(List<T> list, Integer endIndex) {
		return getSubList(list, NumberUtils.INTEGER_ZERO, endIndex);
	}

	/**
	 * Method to get sublist for given list with start index and end index.
	 * 
	 * @param list holds list reference.
	 * @param startIndex holds start index.
	 * @param endIndex holds end index.
	 * @return List<T> returns list of sub list.
	 */
	public static <T> List<T> getSubList(List<T> list, Integer startIndex, Integer endIndex) {
		List<T> subList = new ArrayList<T>();
		if (CollectionUtils.isNotEmpty(list)) {
			subList = list.subList(startIndex, endIndex);
		}
		LOGGER.debug("Sublist value - {}",subList);
		return subList;
	}
}