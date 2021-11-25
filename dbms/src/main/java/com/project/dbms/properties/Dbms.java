package com.project.dbms.properties;

import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("dbms")
public class Dbms {
	List<Map<String, String>> properties;
	
	public Dbms(List<Map<String, String>> properties) {
		this.properties = properties;
	}
	
	public List<Map<String, String>> getProperties() {
		return properties;
	}
}
