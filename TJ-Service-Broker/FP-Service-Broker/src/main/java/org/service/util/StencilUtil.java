package org.service.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.MustacheException;
import com.samskivert.mustache.Template;

/**
 * The <code>StencilUtil</code> class is template engine which used 'MUSTACHE'
 * implementation.
 * 
 * @author Sandeep (sandeep.visvanathan@cognizant.com)
 * @author Deepthi (deepthi.g2@cognizant.com)
 * @author Sundar (sundarajan.srinivasan@cognizant.com)
 * @author Ramkumar(ramkumar.kandhakumar@cognizant.com)
 */
public final class StencilUtil {

	/**
	 * LOGGER variable
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(StencilUtil.class);

	/**
	 * Private constructor
	 */
	private StencilUtil() {
		// Utility class.
	}

	/**
	 * Method to get evaluated template.
	 * 
	 * @param templateString
	 *            - holds stencil string.
	 * @param attributes
	 *            - holds map of <String, Object> reference.
	 * @return String - return replaced string.
	 */
	public static String execute(String templateString, Map<String, Object> attributes) {

		if (StringUtils.isEmpty(templateString)) {
			throw new IllegalArgumentException();
		}
		if (MapUtils.isEmpty(attributes)) {
			throw new IllegalArgumentException();
		}

		String result = StringUtils.EMPTY;
		try {
			// Replace the template with values
			Template template = Mustache.compiler().compile(templateString);
			result = StringUtils.trim(template.execute(attributes));

		} catch (MustacheException mustacheException) {
			throw new RuntimeException("MustacheException", mustacheException);
		}

		LOGGER.debug("Stencil template '{}' framed successfully.", result);
		return result;
	}

	/**
	 * Method to get MONGO docker template.
	 * 
	 * @param attributes
	 *            - holds map of <String, Object> reference.
	 * @return String - return replaced string of MONGO docker.
	 */
	public static String getTemplate(File templateLocation, Map<String, Object> attributes) {
		Reader reader;
		try {
			reader = new FileReader(templateLocation);
		} catch (FileNotFoundException fileNotFoundException) {
			throw new RuntimeException("FileNotFoundException", fileNotFoundException);
		}
		return getTemplate(reader, attributes);
	}

	/**
	 * Method to get MONGO docker template.
	 * 
	 * @param attributes
	 *            - holds map of <String, Object> reference.
	 * @return String - return replaced string of MONGO docker.
	 */
	public static String getTemplate(InputStream inputStream, Map<String, Object> attributes) {

		return getTemplate(new InputStreamReader(inputStream), attributes);
	}

	/**
	 * Method to get MONGO docker template.
	 * 
	 * @param attributes
	 *            - holds map of <String, Object> reference.
	 * @return String - return replaced string of MONGO docker.
	 */
	public static String getTemplate(Reader reader, Map<String, Object> attributes) {

		if (MapUtils.isEmpty(attributes)) {
			throw new IllegalArgumentException();
		}

		String result = StringUtils.EMPTY;
		try {
			// Replace the template with values
			Template template = Mustache.compiler().compile(reader);
			result = template.execute(attributes);
		} catch (MustacheException mustacheException) {
			throw new RuntimeException("MustacheException", mustacheException);
		}

		LOGGER.debug("Mongo docker stencil framed successfully.");

		return result;
	}

	/**
	 * Method is used to frame the inputs like reserved IPs and static IP used
	 * for deployment.
	 * 
	 * @param list
	 *            holds assigned IP list.
	 * @param key
	 *            holds attribute name.
	 * @return Lit<String> returns the static IP list.
	 */
	public static <T> List<Map<String, T>> getMapList(List<T> list, String key) {

		List<Map<String, T>> mapList = new ArrayList<Map<String, T>>();

		for (T value : list) {
			Map<String, T> map = new HashMap<String, T>();
			map.put(key, value);
			mapList.add(map);
		}

		return mapList;
	}

	/**
	 * Method is used to frame the inputs like reserved IPs and static IP used
	 * for deployment.
	 * 
	 * @param list
	 *            holds assigned IP list.
	 * @param prefixKey
	 *            holds attribute name.
	 * @return Lit<String> returns the static IP list.
	 */
	public static <T> Map<String, T> getMap(List<T> list, String prefixKey) {

		Map<String, T> map = new HashMap<String, T>();

		int position = 0;
		for (T value : list) {
			map.put(StringUtils.join(prefixKey, String.valueOf(position)), value);
			position++;
		}

		return map;
	}

}
