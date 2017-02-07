package org.service.util;

import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The <code>XpathUtil</code> class is traverse java object with XPATH queries.
 * 
 * @author Sandeep (sandeep.visvanathan@cognizant.com)
 * @author Deepthi (deepthi.g2@cognizant.com)
 * @author Sundar (sundarajan.srinivasan@cognizant.com)
 * @author Ramkumar(ramkumar.kandhakumar@cognizant.com)
 */
public class XPathUtil {

	/**
	 * LOGGER variable
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(XPathUtil.class);

	/**
	 * Private constructor
	 */
	private XPathUtil() {
		// Utility class.
	}

	/**
	 * Method to get value from given map context reference for XPATH.
	 * 
	 * @param contextReference
	 *            holds Map<String,Object> context reference.
	 * @param xpath
	 *            holds XPATH query string.
	 * @return T returns reference of desired type.
	 */
	public static <T> T getValue(Map<String, Object> contextReference, String xpath) {
		return getValue(contextReference, xpath, null);
	}

	/**
	 * Method to get value from given map context reference for XPATH.
	 * 
	 * @param contextReference
	 *            holds Map<String,Object> context reference.
	 * @param xpath
	 *            holds XPATH query string.
	 * @param variable
	 *            holds placeholder for XPATH query string.
	 * @return T returns reference of desired type.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getValue(Map<String, Object> contextReference, String xpath, Map<String, Object> variables) {

		LOGGER.debug("Xpath template '{}'.", xpath);
		JXPathContext context = JXPathContext.newContext(contextReference);
		if (MapUtils.isNotEmpty(variables)) {
			for (String key : variables.keySet()) {
				context.getVariables().declareVariable(key, variables.get(key));
			}
		}
		T value = (T) context.getValue(xpath);
		LOGGER.info("value from xpath  getvalue "+value);
		return value;
	}

	/**
	 * Method to get value from given map context reference for XPATH with safe.
	 * Return null on JXPathNotFoundException.
	 * 
	 * @param contextReference
	 *            holds Map<String,Object> context reference.
	 * @param xpath
	 *            holds XPATH query string.
	 * @param variable
	 *            holds placeholder for XPATH query string.
	 * @return T returns reference of desired type.
	 */
	public static <T> T getSafeValue(Map<String, Object> contextReference, String xpath,
			Map<String, Object> variables) {
		T value = null;
		try {
			value = getValue(contextReference, xpath, variables);
		} catch (JXPathNotFoundException exception) {
			// Suppress exception purposefully.
		}
		return value;
	}

	/**
	 * Method to get value from given POJO context reference for XPATH.
	 * 
	 * @param contextReference
	 *            holds POJO context reference.
	 * @param xpath
	 *            holds XPATH query string.
	 * @return T returns reference of desired type.
	 */
	public static <T> T getValue(Object contextReference, String xpath) {
		return getValue(contextReference, xpath, null);
	}

	/**
	 * Method to get value from given POJO context reference for XPATH with
	 * safe. Return null on JXPathNotFoundException.
	 * 
	 * @param contextReference
	 *            holds POJO context reference.
	 * @param xpath
	 *            holds XPATH query string.
	 * @return T returns reference of desired type.
	 */
	public static <T> T getSafeValue(Object contextReference, String xpath) {
		T value = null;
		try {
			value = getValue(contextReference, xpath, null);
		} catch (JXPathNotFoundException exception) {
			// Suppress exception purposefully.
		}
		return value;
	}

	/**
	 * Method to get value from given POJO context reference for XPATH.
	 * 
	 * @param contextReference
	 *            holds POJO context reference.
	 * @param xpath
	 *            holds XPATH query string.
	 * @param variable
	 *            holds placeholder for XPATH query string.
	 * @return T returns reference of desired type.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getValue(Object contextReference, String xpath, Map<String, Object> variables) {
		LOGGER.debug("Xpath template '{}'.", xpath);
		JXPathContext context = JXPathContext.newContext(contextReference);
		if (MapUtils.isNotEmpty(variables)) {
			for (String key : variables.keySet()) {
				context.getVariables().declareVariable(key, variables.get(key));
			}
		}
		T value = (T) context.getValue(xpath);
		return value;
	}
	
	public static <T> T setValue(Object contextReference, String xpath,Object value) {
		
		JXPathContext context = JXPathContext.newContext(contextReference);
		context.setValue(xpath, value);
		return null;
	} 
	

}
