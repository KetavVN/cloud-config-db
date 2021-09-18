package com.db.property;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Loads application properties from database before application context 
 * is created/refreshed. Properties are loaded as last propety source and 
 * therefore database properties overwrites any file based property value 
 * in case of conflict.
 * 
 * @author ketav
 */
@Slf4j
public abstract class DatabasePropertiesBasedContextInitializer
		implements ApplicationContextInitializer<ConfigurableApplicationContext> {

	//abstract public String getDatabaseDriverClass(ConfigurableEnvironment env);
	abstract public String getDatabaseUsername(ConfigurableEnvironment env);
	abstract public String getDatabasePassword(ConfigurableEnvironment env);
	abstract public String getDatabaseURI(ConfigurableEnvironment env);
	abstract public String getPropertiesSelectQuery(ConfigurableEnvironment env);
	abstract public boolean isConfigEnabled(ConfigurableEnvironment env);

	/**
	 * Loads application properties from database before application context 
	 * is created/refreshed. Properties are loaded as last propety source and 
	 * therefore database properties overwrites any file based property value 
	 * in case of conflict.
	 */
	@Override
	public void initialize(ConfigurableApplicationContext appContext) {
		ConfigurableEnvironment env = appContext.getEnvironment();
		if(!isConfigEnabled(env)) {
			log.info("Database config is not enabled. Skipping DatabasePropertiesBasedContextInitializer execution.");
			return;
		}
		log.info("Executing DatabasePropertiesBasedContextInitializer to load properties from database.");
		//String driverCls = getDatabaseDriverClass(env);
		String uri = getDatabaseURI(env);
		String username = getDatabaseUsername(env);
		String password = getDatabasePassword(env);
		String query = getPropertiesSelectQuery(env);
		validateDetails(uri, username, password, query);
		try (Connection con = DriverManager.getConnection(uri, username, password);
				Statement stmt = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
				ResultSet rs = stmt.executeQuery(query);) {
			//Class.forName(env.getProperty(driverCls));
			Map<String, Object> source = new HashMap<>();
			while(rs.next()) {
				source.put(rs.getString(1), rs.getString(2));
			}
			log.info("{} properties loaded from database", source.size());
			log.debug("Loaded properties from database : {}", source);
			env.getPropertySources().addLast(new MapPropertySource("DatabasePropertySource", source));
		} catch (SQLException e) {
			log.error("Exception occured while loading properties from database : {}", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Validate Required parameters to connect to db and load properties from table.
	 * 
	 * @param uri
	 * @param username
	 * @param password
	 * @param query
	 */
	private void validateDetails(String uri, String username, String password, String query) {
		Assert.hasText(uri, "Database connection uri must be non empty.");
		Assert.hasText(username, "Database connection username must be non empty.");
		Assert.hasText(password, "Database connection password must be non empty.");
		Assert.hasText(query, "Select query to load properties from database must be non empty.");
	}

}
