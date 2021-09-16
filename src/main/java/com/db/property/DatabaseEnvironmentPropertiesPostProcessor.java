package com.db.property;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DatabaseEnvironmentPropertiesPostProcessor implements EnvironmentPostProcessor {

	public static final String driverCls = "database.driver.class";
	public static final String databaseURI = "database.url";
	public static final String databaseUsername = "database.username";
	public static final String databasePassword = "database.password";

	@Override
	public void postProcessEnvironment(ConfigurableEnvironment env, SpringApplication application) {
		Map<String, Object> source = new HashMap<>();
		log.info("Constructor called.");
		try {
			Class.forName(env.getProperty(driverCls));
			Connection con = DriverManager.getConnection(
					env.getProperty(databaseURI), 
					env.getProperty(databaseUsername), 
					env.getProperty(databasePassword));
			Statement stmt = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = stmt.executeQuery("select property_key, property_value from properties");
			while(rs.next()) {
				source.put(rs.getString("property_key"), rs.getString("property_value"));
			}
			rs.close();
			stmt.close();
			log.info("loaded properties : {}", source);
			env.getPropertySources().addFirst(new MapPropertySource("DatabasePropertySource", source));
		} catch (SQLException | ClassNotFoundException e) {
			log.error("Exception occured while loading properties from database : {}", e);
			throw new RuntimeException(e);
		}
		
	}

}
