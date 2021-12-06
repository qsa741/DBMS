package com.jySystem.kafka.service;

import org.springframework.boot.configurationprocessor.json.JSONObject;

public interface KafkaService {

	public void saveActionData(JSONObject data) throws Exception;
	
}
