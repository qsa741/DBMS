package com.project.dbms.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.project.dbms.service.KafkaService;
import com.project.dbms.service.KafkaServiceImpl;
import com.project.dbms.service.NetworkServiceImpl;

@Service
public class KafkaConsumer {

	@Autowired
	private NetworkServiceImpl networkService;
	
	@Autowired
	private KafkaServiceImpl kafkaService;
	
	@KafkaListener(topics = "userTopic", groupId = "dbms")
	public void userTopic(String data) throws Exception {
		JSONObject json = new JSONObject(data);
		
		if(json.get("id").equals("JY-SAVE")) {
			networkService.saveUser(json);
		}
	}
	
	@KafkaListener(topics = "actionTopic", groupId = "dbms")
	public void userAction(String data) throws Exception {
		JSONObject json = new JSONObject(data);
		
		String id = json.getString("id");
		if(id.equals("JY-ACTION-02")) {
			kafkaService.saveActionData(json);
		}
	}
	
}
