package com.db.property;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DBPropertySource2 extends PropertySource<DBProperties> {

	public static final String driverCls = "database.driver.class";
	public static final String databaseURI = "database.url";
	public static final String databaseUsername = "database.username";
	public static final String databasePassword = "database.password";

	public DBPropertySource2(String name, Environment env) {
		super(name, new DBProperties());
		log.info("Constructor called.");
		try {
			Class.forName(env.getProperty(driverCls));
			Connection con = DriverManager.getConnection(databaseURI, databaseUsername, databasePassword);
			Statement stmt = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = stmt.executeQuery("select property_key, property_value from properties");
			while(rs.next()) {
				source.propertyMap.put(rs.getString(0), rs.getString(1));
			}
			rs.close();
			stmt.close();
			log.info(String.format("loaded properties : %s", source.propertyMap));
		} catch (ClassNotFoundException | SQLException e) {
			log.warn("could not load class");
		}
	}

	@Override
	public Object getProperty(String name) {
		return source.propertyMap.get(name);
	}

}
