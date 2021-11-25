package com.project.dbms.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.project.dbms.service.NetworkServiceImpl;

@Service
public class KafkaConsumer {

	@Autowired
	private NetworkServiceImpl networkService;
	
	@KafkaListener(topics = "dbmsTopic", groupId = "dbms")
	public void consume(String data) throws Exception {
		JSONObject json = new JSONObject(data);
		
		if(json.get("id").equals("JY-SAVE")) {
			networkService.saveUser(json);
		}
	}
	
	
}
