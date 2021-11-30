package com.project.dbms.properties;

import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("action")
public class Action {

	private final List<Map<String, String>> agent;
	
	public Action(List<Map<String, String>> agent) {
		this.agent = agent;
	}
	
	public List<Map<String, String>> getAgent() {
		return agent;
	}
}
