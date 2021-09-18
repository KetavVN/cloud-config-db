package com.db.property;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

//@Slf4j
@SpringBootApplication
public class Application extends ApplicationContextReloader {

	//Option A: PropertySource -- does not work
	/*public static void main(String[] args) {
		context = SpringApplication.run(Application.class, args);
		context.getEnvironment().getPropertySources().addLast(new DBPropertySource2("database-property-source", context.getEnvironment()));
		context.refresh();
	}*/

	//Option B: BeanPostProcessor - works!!
	/*public static void main(String[] args) {
		new Application().startApplication(Application.class, args);	
	}*/

	//Option C: ApplicationContextInitializer - works!! 
	public static void main(String[] args) {
		new Application().startApplicationWithInitializer(Application.class, args);	
	}

	@Override
	public int contextMonitorSleepTimeInSeconds() {
		return 20;
	}

	@Override
	public Comparable<Object> reloadPropertyValue(
			ConfigurableApplicationContext context) {
		return context.getBean(PropertiesJPARepository.class)
				.findByPropertyKey("reload.flag");
	}

	@Override
	public String getDatabaseUsername(ConfigurableEnvironment env) {
		return env.getProperty("database.username");
	}

	@Override
	public String getDatabasePassword(ConfigurableEnvironment env) {
		return env.getProperty("database.password");
	}

	@Override
	public String getDatabaseURI(ConfigurableEnvironment env) {
		return env.getProperty("database.url");
	}

	@Override
	public String getPropertiesSelectQuery(ConfigurableEnvironment env) {
		return env.getProperty("database.config.select.query");
	}

	@Override
	public boolean isConfigEnabled(ConfigurableEnvironment env) {
		Boolean dbConfigEnabled = 
				env.getProperty("database.config.enabled", Boolean.class);
		return dbConfigEnabled!=null && dbConfigEnabled;
	}

}
