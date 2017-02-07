package org.service.tj.helper;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.service.tj.data.ResourceEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;




public class ResourceDBOperations {
	/**
	 * LOGGER variable
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ResourceDBOperations.class);

	
	public static String mongoDatabase(String serverIp, String SeviceInstanceId, String type,
			ResourceEnvironment environment) {

		String message=null;
		return message;


	}



	


	public static void mongoDatabaseUnBind(String serverIp, String SeviceInstanceId, String BindingServiceId,
			ResourceEnvironment environment) {

		String username = null;
		String stringToEncode = BindingServiceId;
		String encode = Base64.encodeBase64URLSafeString(stringToEncode.getBytes());
		username = encode;
		//Delete user 

	}


		
						
	
	/*public static String mongoDatabase(String serverIp, String SeviceInstanceId, String type,
			ResourceEnvironment environment) {
		LOGGER.info("Email Server - {}.", serverIp);
		String message = null;
		MongoClient mongoClient = null;
		try {
			mongoClient = openConnection(serverIp, environment);
			mongoClient.getDatabase("admin");
			LOGGER.info("Database {} created successfully.", type);
		} catch (RuntimeException exception) {
			LOGGER.error("Exception in mongoDatabase", exception);
		} finally {
			closeConnection(mongoClient);
		}
		return message;
	}*/

	
	
	
	public static String mongoDatabaseDrop(String serverIp, String SeviceInstanceId, String type,
			ResourceEnvironment environment) {
		LOGGER.info("Email Server - {}.", serverIp);
		String message = null;
//		MongoClient mongoClient = null;
//		try {
//			mongoClient = openConnection(serverIp, environment);
//			String serviceName = StringUtils.join("DB_", StringUtils.replace(SeviceInstanceId, "-", "_"));
//			mongoClient.dropDatabase(serviceName);
//			LOGGER.info("Database {} droped successfully.", type);
//		} catch (RuntimeException exception) {
//			LOGGER.error("Exception in mongoDatabaseDrop", exception);
//		} finally {
//			closeConnection(mongoClient);
//		}
		return message;
	}

	/*public static Map<String, Object> mongoDatabaseBind(String serverIp, String serviceName,
			String BindingServiceId, String mongoRole, ResourceEnvironment environment) {

		LOGGER.info("Mongo Database - {}.", serverIp);
		MongoDatabase db = null;
		MongoClient mongoClient = null;
		String username = null;
		String password = null;
		try {
			mongoClient = openConnection(serverIp, environment);
			//String serviceName = StringUtils.join("DB_", StringUtils.replace(SeviceInstanceId, "-", "_"));
			db = mongoClient.getDatabase(serviceName);
			String stringToEncode = BindingServiceId;
			String encode = Base64.encodeBase64URLSafeString(stringToEncode.getBytes());
			username = encode;
			password = (RandomStringUtils.randomAlphanumeric(10)).toLowerCase();
			Map<String, Object> commandArguments = new BasicDBObject();
			commandArguments.put("createUser", username);
			commandArguments.put("pwd", password);
			String[] roles = { mongoRole };
			commandArguments.put("roles", roles);
			BasicDBObject command = new BasicDBObject(commandArguments);
			db.runCommand(command);
			LOGGER.info("User added successfully.");
		} catch (RuntimeException exception) {
			LOGGER.error("Exception in mongoDatabaseBind", exception);
		} finally {
			closeConnection(mongoClient);
		}

		Map<String, Object> messageMap = new HashMap<String, Object>();
		messageMap.put("username", username);
		messageMap.put("password", password);
		messageMap.put("IP", serverIp);
		return messageMap;
	}*/

	
	
	/*public static void mongoDatabaseUnBind(String serverIp, String SeviceInstanceId, String BindingServiceId,
			ResourceEnvironment environment) {
		LOGGER.info("Mongo Database:  - {}.", serverIp);
		MongoDatabase db = null;
		MongoClient mongoClient = null;
		String username = null;
		try {
			mongoClient = openConnection(serverIp, environment);
			String serviceName = StringUtils.join("DB_", StringUtils.replace(SeviceInstanceId, "-", "_"));
			db = mongoClient.getDatabase(serviceName);
			String stringToEncode = BindingServiceId;
			String encode = Base64.encodeBase64URLSafeString(stringToEncode.getBytes());
			username = encode;
			Map<String, Object> commandArguments = new BasicDBObject();
			commandArguments.put("dropUser", username);
			BasicDBObject command = new BasicDBObject(commandArguments);
			db.runCommand(command);
			LOGGER.info("User added successfully.");
		} catch (RuntimeException exception) {
			LOGGER.error("Exception in mongoDatabaseUnBind", exception);
		} finally {
			closeConnection(mongoClient);
		}
	}*/

	/*public static int getCountOfDB(String serverIp, ResourceEnvironment environment) {
		MongoClient mongoClient = null;
		int i = 0;
		try {
			mongoClient = openConnection(serverIp, environment);
			MongoIterable<String> dbNames = mongoClient.listDatabaseNames();
			for (String dbname : dbNames) {
				if (!StringUtils.equalsIgnoreCase("admin", dbname) && !StringUtils.equalsIgnoreCase("local", dbname)
						&& !StringUtils.equalsIgnoreCase("test", dbname)&& !StringUtils.equalsIgnoreCase("config", dbname)) {
					i = i + 1;
				}
			}
		} catch (RuntimeException exception) {
			LOGGER.error("Exception in getCountOfDB", exception);
		} finally {
			closeConnection(mongoClient);
		}
		return i;
	}*/

	/*private static MongoClient openConnection(String serverIp, ResourceEnvironment environment) {
		String mongoUri = StringUtils.join("mongodb://", environment.getServerDefaultUsername(), ServiceConstant.COLON,
				environment.getServerDefaultPassword(), ServiceConstant.AT, serverIp, ServiceConstant.COLON,
				environment.getServerDefaultPort(), "/?authSource=admin");
		MongoClientURI uri = new MongoClientURI(mongoUri);
		MongoClient mongoClient = new MongoClient(uri);

		return mongoClient;
	}*/

	/*private static void closeConnection(MongoClient mongoClient) {
		if (mongoClient != null) {
			mongoClient.close();
		}
	}*/

}
