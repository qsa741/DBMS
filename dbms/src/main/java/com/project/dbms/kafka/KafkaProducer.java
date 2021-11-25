package com.project.dbms.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {
	
	private static final String TOPIC = "saveUser";
	private static final String TOPIC2 = "dbmsTopic";
	
	private final KafkaTemplate<String, String> kafkaTemplate;
	
	@Autowired
	public KafkaProducer(KafkaTemplate kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}
	
	public void sendSSO(String msg) {
		this.kafkaTemplate.send(TOPIC, msg);
	}
	
	public void sendDBMS(String msg) {
		this.kafkaTemplate.send(TOPIC2, msg);
	}
}
