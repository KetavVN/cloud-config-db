package com.db.property;

import java.util.concurrent.TimeUnit;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class ApplicationContextReloader implements Runnable {

	private Class<?> cls;
	private String [] args;
	
	protected ConfigurableApplicationContext context = null;
	protected volatile boolean keepRunning = true;

	public abstract String reloadFlagPropertyName();
	public abstract int contextMonitorSleepTimeInSeconds();
	public abstract ApplicationContextInitializer<ConfigurableApplicationContext> contextInitializer();

	public void startApplication(Class<?> cls, String[] args) {
		context = SpringApplication.run(cls, args);
		Thread t = new Thread(this);
		t.start();
	}

	public void startApplicationWithInitializer(Class<?> cls, String[] args) {
		this.cls = cls;
		this.args = args;
		context = createAndRunContext();
		Thread t = new Thread(this);
		t.start();
	}

	private ConfigurableApplicationContext createAndRunContext() {
		SpringApplication app = new SpringApplication(cls);
		app.addInitializers(contextInitializer());
		return app.run(args);
	}

	@Override
	public void run() {
		PropertiesJPARepository repo = context.getBean(PropertiesJPARepository.class);
		Properties initialValue = repo.findByPropertyKey("reload.flag");
		while(keepRunning) {
			try {
				TimeUnit.SECONDS.sleep(contextMonitorSleepTimeInSeconds());
			} catch (InterruptedException e) {
				log.error("Thread sleep interrupted");
			}
			Properties currentValue = repo.findByPropertyKey(reloadFlagPropertyName());
			if(!currentValue.getPropertyValue().equals(initialValue.getPropertyValue())) {
				initialValue = currentValue;
				context.close();
				context = createAndRunContext();
				repo = context.getBean(PropertiesJPARepository.class);
			}
		}
	}
}
