package org.service.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The <code>JsonUtil</code> class is convert JSON string to BEAN and BEAN to
 * JSON string.
 * 
 * @author Sandeep (sandeep.visvanathan@cognizant.com)
 * @author Deepthi (deepthi.g2@cognizant.com)
 * @author Sundar (sundarajan.srinivasan@cognizant.com)
 * @author Ramkumar(ramkumar.kandhakumar@cognizant.com)
 */
public class JsonUtil {

	/**
	 * LOGGER variable
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(JsonUtil.class);

	/**
	 * Private constructor
	 */
	private JsonUtil() {
		// Utility class.
	}

	/**
	 * Method to convert given string to given class.
	 * 
	 * @param jsonString
	 *            - holds JSON string.
	 * @param clazz
	 *            - holds return reference type class.
	 * @return T - return given class reference.
	 */
	public static <T> T getJsonBean(String jsonString, Class<T> clazz) {

		if (jsonString == null || clazz == null) {
			throw new IllegalArgumentException();
		}

		T reference = null;
		try {

			ObjectMapper mapper = new ObjectMapper();
			reference = mapper.readValue(jsonString, clazz);
		} catch (JsonParseException exception) {
			throw new RuntimeException("JsonMappingException", exception);
		} catch (JsonMappingException exception) {
			throw new RuntimeException("JsonMappingException", exception);
		} catch (IOException exception) {
			throw new RuntimeException("IOException", exception);
		}
		LOGGER.debug("Reference '{}' created successfully.", clazz.getName());

		return reference;

	}

	/**
	 * Method to convert given string to linked map reference.
	 * 
	 * @param jsonString
	 *            - holds JSON string.
	 * @return Map<String, Object> - return list of map reference.
	 */
	public static Map<String, Object> getJsonMap(String jsonString) {

		if (jsonString == null) {
			throw new IllegalArgumentException();
		}

		Map<String, Object> reference = null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			TypeReference<LinkedHashMap<String, Object>> typeRef = new TypeReference<LinkedHashMap<String, Object>>() {
			};
			reference = mapper.readValue(jsonString, typeRef);
		} catch (JsonParseException exception) {
			throw new RuntimeException("JsonMappingException", exception);
		} catch (JsonMappingException exception) {
			throw new RuntimeException("JsonMappingException", exception);
		} catch (IOException exception) {
			throw new RuntimeException("IOException", exception);
		}

		LOGGER.debug("Map<String, Object> created successfully.");
		return reference;

	}

	/**
	 * Method to convert given string to list of linked map reference.
	 * 
	 * @param jsonString
	 *            - holds JSON string.
	 * @return List<Map<String, Object>> - return list of map reference.
	 */
	public static List<Map<String, Object>> getJsonListMap(String jsonString) {

		if (jsonString == null) {
			throw new IllegalArgumentException();
		}

		List<Map<String, Object>> reference = null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			TypeReference<ArrayList<LinkedHashMap<String, Object>>> typeRef = new TypeReference<ArrayList<LinkedHashMap<String, Object>>>() {
			};
			reference = mapper.readValue(jsonString, typeRef);
		} catch (JsonParseException exception) {
			throw new RuntimeException("JsonMappingException", exception);
		} catch (JsonMappingException exception) {
			throw new RuntimeException("JsonMappingException", exception);
		} catch (IOException exception) {
			throw new RuntimeException("IOException", exception);
		}

		LOGGER.debug("List<Map<String, Object>> created successfully.");

		return reference;

	}

	/**
	 * Method to get JSON string for given reference.
	 * 
	 * @param reference
	 *            - holds JSON bean reference.
	 * @return String - return JSON string.
	 */
	public static <T> String getJsonString(T reference) {

		if (reference == null) {
			throw new IllegalArgumentException();
		}

		String jsonString = null;
		try {

			ObjectMapper mapper = new ObjectMapper();
			jsonString = mapper.writeValueAsString(reference);
		} catch (JsonParseException exception) {
			throw new RuntimeException("JsonMappingException", exception);
		} catch (JsonMappingException exception) {
			throw new RuntimeException("JsonMappingException", exception);
		} catch (IOException exception) {
			throw new RuntimeException("IOException", exception);
		}

		LOGGER.debug("JSON string '{}' for bean '{}' created successfully", jsonString, reference);

		return jsonString;

	}

	/**
	 * Method to read JSON from a file and convert to JSON bean.
	 * 
	 * @param file
	 *            - holds source file reference.
	 * @param clazz
	 *            - holds class type reference.
	 * @return T - return given class reference.
	 */
	public static <T> T readJsonFile(File file, Class<T> clazz) {

		if (file == null || clazz == null) {
			throw new IllegalArgumentException();
		}

		T reference = null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			reference = mapper.readValue(file, clazz);
		} catch (JsonParseException exception) {
			throw new RuntimeException("JsonMappingException", exception);
		} catch (JsonMappingException exception) {
			throw new RuntimeException("JsonMappingException", exception);
		} catch (IOException exception) {
			throw new RuntimeException("IOException", exception);
		}

		LOGGER.debug("Bean '{}' created successfully", clazz.getName());

		return reference;

	}

	/**
	 * Method to write JSON to a file from given JSON bean.
	 * 
	 * @param file
	 *            - holds destination file reference.
	 * @param reference
	 *            - holds JSON bean reference.
	 * @return boolean - return success or failure.
	 */
	public static <T> boolean writeJsonFile(File file, T reference) {
		boolean flag = false;
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.writeValue(file, reference);
			flag = true;
		} catch (JsonParseException exception) {
			throw new RuntimeException("JsonMappingException", exception);
		} catch (JsonMappingException exception) {
			throw new RuntimeException("JsonMappingException", exception);
		} catch (IOException exception) {
			throw new RuntimeException("IOException", exception);
		}

		LOGGER.debug("File '{}' wrote successfully", file.getAbsolutePath());

		return flag;

	}
}