package com.db.property;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import lombok.Getter;

@Getter
public class DBProperties {

	protected ConcurrentMap<String, String> propertyMap = new ConcurrentHashMap<>();
	
}
