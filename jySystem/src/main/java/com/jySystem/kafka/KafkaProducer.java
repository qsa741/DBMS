package com.jySystem.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {
	
	private static final String TOPIC = "userTopic";
	
	private final KafkaTemplate<String, String> kafkaTemplate;
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Autowired
	public KafkaProducer(KafkaTemplate kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}
	
	public void sendSSO(String msg) {
		this.kafkaTemplate.send(TOPIC, msg);
	}
	
	public void userAction(String data) {
		this.kafkaTemplate.send("actionTopic", data);
	}
}
