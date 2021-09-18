package com.db.property;

import java.util.concurrent.TimeUnit;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import lombok.extern.slf4j.Slf4j;

/**
 * Monitors database continuously for any change.
 * On flag change/toggle, application context is reloaded.
 * 
 * @author ketav
 */
@Slf4j
public abstract class ApplicationContextReloader 
	extends DatabasePropertiesBasedContextInitializer
	implements Runnable {

	private Class<?> mainClass;
	private String [] args;
	protected ConfigurableApplicationContext context = null;
	protected volatile boolean keepRunning = true;

	public abstract Comparable<Object> reloadPropertyValue(ConfigurableApplicationContext context);

	public int contextMonitorSleepTimeInSeconds() {
		return 15;
	}

	public void startApplication(Class<?> cls, String[] args) {
		context = SpringApplication.run(cls, args);
		Thread t = new Thread(this);
		t.start();
	}

	public void startApplicationWithInitializer(Class<?> cls, String[] args) {
		this.mainClass = cls;
		this.args = args;
		context = createAndRunContext();
		Thread t = new Thread(this);
		t.start();
	}

	private ConfigurableApplicationContext createAndRunContext() {
		SpringApplication app = new SpringApplication(mainClass);
		app.addInitializers(this);
		return app.run(args);
	}

	@Override
	public void run() {
		Comparable<Object> initialValue = reloadPropertyValue(context);
		while(keepRunning) {
			try {
				TimeUnit.SECONDS.sleep(contextMonitorSleepTimeInSeconds());
			} catch (InterruptedException e) {
				log.error("Thread sleep interrupted");
			}
			Comparable<Object> currentValue = reloadPropertyValue(context);
			if(currentValue.compareTo(initialValue)!=0) {
				initialValue = currentValue;
				context.close();
				context = createAndRunContext();
			}
		}
	}
}
