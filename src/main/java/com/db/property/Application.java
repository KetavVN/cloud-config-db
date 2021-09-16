package com.db.property;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

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
	public String reloadFlagPropertyName() {
		return "reload.flag";
	}

	@Override
	public int contextMonitorSleepTimeInSeconds() {
		return 20;
	}

	@Override
	public ApplicationContextInitializer<ConfigurableApplicationContext> contextInitializer() {
		return new DatabasePropertiesBasedContextInitializer();
	}

}
