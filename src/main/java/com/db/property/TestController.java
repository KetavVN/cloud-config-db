package com.db.property;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class TestController {

	@Autowired
	private Environment env;

	@GetMapping("/v1/value")
	public String getValue() {
		String prop1 = env.getProperty("prop1");
		log.info("Controller called. value = {}", prop1);
		return prop1;
	}

}
