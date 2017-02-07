package org.service.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.service.broker.exception.ServiceException;
import org.service.data.ServiceEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

/**
 * 
 * @author Sandeep (sandeep.visvanathan@cognizant.com)
 * @author Deepthi (deepthi.g2@cognizant.com)
 * @author Sundar (sundarajan.srinivasan@cognizant.com)
 * @author Ramkumar(ramkumar.kandhakumar@cognizant.com)
 * @author Navaneethan(Navaneethan.T@cognizant.com)
 *
 */
public class DBUtil {

	/**
	 * LOGGER variable
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(FileUtil.class);
	
	/**
	 * The <code>getConnection</code> method is used to create connection to Database.
	 * @param sEnvironment holds the reference to service environment.
	 * @param databaseName is database to which connection has to be created.
	 * @return conn is connection to the DB.
	 */
	private static Connection getConnection(ServiceEnvironment sEnvironment, String databaseName) {
		Connection conn = null;
		try {
			Class.forName("org.postgresql.Driver").newInstance();
			conn = DriverManager.getConnection(StringUtils.join(sEnvironment.getUrlNoDbName(), databaseName),
					sEnvironment.getUserName(), sEnvironment.getPassword());
		} catch (Exception e) {
			LOGGER.error("Exception in get Connection - {} ", e);
		}
		return conn;
	}
	
	/**
	 * The <code>createDatabase</code> is used to create Database.
	 * @param sEnvironment holds the reference to service environment.
	 * @param databaseName is database to which connection has to be created.
	 */
	public static void createDatabase(ServiceEnvironment sEnvironment, String dbName) {
		Assert.notNull(dbName);

		Connection conn = null;
		Statement stmt = null;
		try {
			conn = getConnection(sEnvironment, sEnvironment.getDefaultDb());
			stmt = conn.createStatement();

			LOGGER.info("connection: {}", conn);
			Map<String, Object> stencilInput = new HashMap<String, Object>();
			stencilInput.put("databaseName", dbName);

			String sql = StencilUtil.execute("CREATE DATABASE {{databaseName}};", stencilInput);
			stmt.executeUpdate(sql);
		} catch (Exception e) {
			LOGGER.error("Exception in create database.", e);
		} finally {
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException e) {
				LOGGER.warn("Error in closing statement - {}", e);	
			}
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				LOGGER.warn("Error in closing connection - {}", e);	
			}
		}

	}
	
	/**
	 * The <code>createTable</code> is used to create table in specifed Database.
	 * @param sEnvironment holds the reference to service environment.
	 * @param databaseName is database to which connection has to be created.
	 * @param tableNames holds the names of tables to be created.
	 */
	public static void createTable(ServiceEnvironment sEnvironment, String dbName, List<String> tableNames) {
		Assert.notNull(dbName);
		Assert.notEmpty(tableNames);

		Connection conn = null;
		Statement stmt = null;
		try {
			conn = getConnection(sEnvironment, dbName);
			stmt = conn.createStatement();
			String createTable = "CREATE TABLE {{tableName}}(IP_ADDRESS  VARCHAR(100)  NOT NULL ,METRIC_NAME   VARCHAR(100) ,METRIC_VALUE VARCHAR(100),CREATE_TS timestamp not null default CURRENT_TIMESTAMP);";
			LOGGER.info("connection: {}", conn);

			StringBuffer sb = new StringBuffer();
			for (String tableName : tableNames) {
				Map<String, Object> stencilInput = new HashMap<String, Object>();
				stencilInput.put("databaseName", dbName);
				stencilInput.put("tableName", tableName);
				sb.append(StencilUtil.execute(createTable, stencilInput));
			}
			stmt.executeUpdate(sb.toString());

		} catch (Exception e) {
			LOGGER.error("Exception in create table.", e);
		} finally {
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException e) {
				LOGGER.warn("Error in closing statement - {}", e);	
			}
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				LOGGER.warn("Error in closing connection - {}", e);	
			}
		}

	}

}
