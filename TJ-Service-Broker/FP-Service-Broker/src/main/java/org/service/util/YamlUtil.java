package org.service.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.deser.std.StringDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

/**
 * The <code>YamlUtil</code> class is convert YAML string to BEAN and BEAN to
 * JSON string.
 * 
 * @author Sandeep (sandeep.visvanathan@cognizant.com)
 * @author Deepthi (deepthi.g2@cognizant.com)
 * @author Sundar (sundarajan.srinivasan@cognizant.com)
 * @author Ramkumar(ramkumar.kandhakumar@cognizant.com)
 */
public class YamlUtil {

	/**
	 * LOGGER variable
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(YamlUtil.class);

	/**
	 * Private constructor
	 */
	private YamlUtil() {
		// Utility class.
	}

	/**
	 * Method to convert given string to given class.
	 * 
	 * @param yamlString
	 *            holds YAML string.
	 * @param clazz
	 *            holds return reference type class.
	 * @return T returns given class reference.
	 */
	public static <T> T getYamlBean(String yamlString, Class<T> clazz) {

		if (yamlString == null || clazz == null) {
			throw new IllegalArgumentException();
		}

		T reference = null;
		try {

			ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
			reference = mapper.readValue(yamlString, clazz);
		} catch (JsonParseException exception) {
			throw new RuntimeException("YamlParseException", exception);
		} catch (JsonMappingException exception) {
			throw new RuntimeException("YamlMappingException", exception);
		} catch (IOException exception) {
			throw new RuntimeException("IOException", exception);
		}
		LOGGER.debug("Reference '{}' created successfully.", clazz.getName());

		return reference;

	}

	/**
	 * Method to convert given string to linked map reference.
	 * 
	 * @param yamlString
	 *            holds YAML string.
	 * @return Map<String, Object> return list of map reference.
	 */
	public static Map<String, Object> getYamlMap(String yamlString) {

		if (yamlString == null) {
			throw new IllegalArgumentException();
		}

		Map<String, Object> reference = null;
		try {

			ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
			TypeReference<LinkedHashMap<String, Object>> typeRef = new TypeReference<LinkedHashMap<String, Object>>() {
			};

			SimpleModule module = new SimpleModule();
			module.addDeserializer(String.class, new CustomDeserializer());
			mapper.registerModules(module);

			reference = mapper.readValue(yamlString, typeRef);
		} catch (JsonParseException exception) {
			throw new RuntimeException("YamlParseException", exception);
		} catch (JsonMappingException exception) {
			throw new RuntimeException("YamlMappingException", exception);
		} catch (IOException exception) {
			throw new RuntimeException("IOException", exception);
		}

		LOGGER.debug("Map<String, Object> created successfully.");
		return reference;

	}

	/**
	 * Method to convert given string to list of linked map reference.
	 * 
	 * @param yamlString
	 *            holds YAML string.
	 * @return List<Map<String, Object>> return list of map reference.
	 */
	public static List<Map<String, Object>> getYamlListMap(String yamlString) {

		if (yamlString == null) {
			throw new IllegalArgumentException();
		}

		List<Map<String, Object>> reference = null;
		try {
			ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
			TypeReference<ArrayList<LinkedHashMap<String, Object>>> typeRef = new TypeReference<ArrayList<LinkedHashMap<String, Object>>>() {
			};
			reference = mapper.readValue(yamlString, typeRef);
		} catch (JsonParseException exception) {
			throw new RuntimeException("YamlParseException", exception);
		} catch (JsonMappingException exception) {
			throw new RuntimeException("YamlMappingException", exception);
		} catch (IOException exception) {
			throw new RuntimeException("IOException", exception);
		}
		LOGGER.debug("List<Map<String, Object>> created successfully.");

		return reference;

	}

	/**
	 * Method to get YAML string for given reference.
	 * 
	 * @param reference
	 *            holds YAML bean reference.
	 * @return String return YAML string.
	 */
	public static <T> String getYamlString(T reference) {

		if (reference == null) {
			throw new IllegalArgumentException();
		}

		String yamlString = null;
		try {

			ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
			mapper.enable(SerializationFeature.WRITE_NULL_MAP_VALUES);
			yamlString = mapper.writeValueAsString(reference);
		} catch (JsonParseException exception) {
			throw new RuntimeException("YamlParseException", exception);
		} catch (JsonMappingException exception) {
			throw new RuntimeException("YamlMappingException", exception);
		} catch (IOException exception) {
			throw new RuntimeException("IOException", exception);
		}

		LOGGER.debug("YAML string for bean '{}' created successfully", reference);

		return yamlString;

	}

	/**
	 * Method to read YAML from a file and convert to YAML bean.
	 * 
	 * @param file
	 *            holds source file reference.
	 * @param clazz
	 *            holds class type reference.
	 * @return T return given class reference.
	 */
	public static <T> T readYamlFile(File file, Class<T> clazz) {

		if (file == null || clazz == null) {
			throw new IllegalArgumentException();
		}

		T reference = null;
		try {
			ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
			reference = mapper.readValue(file, clazz);
		} catch (JsonParseException exception) {
			throw new RuntimeException("YamlParseException", exception);
		} catch (JsonMappingException exception) {
			throw new RuntimeException("YamlMappingException", exception);
		} catch (IOException exception) {
			throw new RuntimeException("IOException", exception);
		}

		LOGGER.debug("Bean '{}' created successfully", clazz.getName());

		return reference;

	}

	/**
	 * Method to write YAML to a file from given YAML bean.
	 * 
	 * @param file
	 *            holds destination file reference.
	 * @param reference
	 *            holds YAML bean reference.
	 * @return boolean return success or failure.
	 */
	public static <T> boolean writeYamlFile(File file, T reference) {
		boolean flag = false;
		try {
			ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
			mapper.writeValue(file, reference);
			flag = true;
		} catch (JsonParseException exception) {
			throw new RuntimeException("YamlParseException", exception);
		} catch (JsonMappingException exception) {
			throw new RuntimeException("YamlMappingException", exception);
		} catch (IOException exception) {
			throw new RuntimeException("IOException", exception);
		}
		LOGGER.debug("File '{}' wrote successfully", file.getAbsolutePath());

		return flag;

	}

	/**
	 * The <code>CustomDeserializer</code> class is to consider 'EMPTY' string
	 * as 'null' reference which deserialize YAML string.
	 * 
	 * @author Sandeep (sandeep.visvanathan@cognizant.com)
	 * @author Deepthi (deepthi.g2@cognizant.com)
	 * @author Sundar (sundarajan.srinivasan@cognizant.com)
	 * @author Ramkumar(ramkumar.kandhakumar@cognizant.com)
	 */
	private static class CustomDeserializer extends JsonDeserializer<String> {

		@Override
		public String deserialize(JsonParser jsonParser, DeserializationContext context)
				throws IOException, JsonProcessingException {
			String value = StringDeserializer.instance.deserialize(jsonParser, context);
			if (StringUtils.isEmpty(value)) {
				return null;
			}
			return value;
		}

	}

	/***
	 * To modify the manifest.yml file for cfbot
	 * @param fileName
	 * @param appName
	 */
	// yaml customization
	public static void manifestYamlWriter(String fileName, String appName, String serviceName) {
		Yaml yaml = new Yaml();
		try {
			InputStream ios = new FileInputStream(new File(fileName));
			// Parse the YAML file and return the output as a series of Maps and
			// Lists
			Map<String, Object> result = (Map<String, Object>) yaml.load(ios);
			 LOGGER.debug(result.toString());
			for (Object name : result.keySet()) {
				ArrayList<Object> valueapp = (ArrayList<Object>) result.get(name);
				Map<String, Object> resultnew = (Map<String, Object>) valueapp.get(0);
				boolean hasENV = false;
				for (Object namenew : resultnew.keySet()) {
					if(namenew.toString().equals("services") && serviceName != null){
						resultnew.put(namenew.toString(), serviceName);
					}
					if (namenew.toString().equals("env")) {
						LOGGER.debug("Has ENV");
						hasENV = true;
						Map<String, Object> resultenv = (Map<String, Object>) resultnew.get(namenew);
						for (Object nameenv : resultenv.keySet()) {
							if (nameenv.toString().equals("APPS")) {
								resultenv.put(nameenv.toString(), resultenv.get(nameenv).toString() + " " + appName);
							}
						}
					}
				}
				if (!hasENV) {
					LOGGER.debug("No ENV");
					JSONObject appsObj = new JSONObject();
					appsObj.put("APPS", appName);
					resultnew.put("env", appsObj);
				}
			}
			FileWriter writer = null;
			try {
				writer = new FileWriter(fileName);
			} catch (IOException e) {
				e.printStackTrace();
			}
			yaml.dump(result, writer);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try{
			InputStream iosnew = new FileInputStream(new File(fileName));
                        // Parse the YAML file and return the output as a series of Maps and
                        // Lists
                        Map<String, Object> resultnew = (Map<String, Object>) yaml.load(iosnew);
                         LOGGER.debug(resultnew.toString());
		} catch (Exception e) {
                        e.printStackTrace();
                }

	}
}
