package com.jySystem.kafka;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.jySystem.kafka.service.KafkaServiceImpl;
import com.jySystem.network.service.NetworkServiceImpl;

@Service
public class KafkaConsumer {

	@Value("${save.agent.id}")
	private String saveAgentId;

	@Value("${action.agent.id}")
	private String actionAgentId;

	@Autowired
	private NetworkServiceImpl networkService;

	@Autowired
	private KafkaServiceImpl kafkaService;

	// 유저 정보 토픽(생성 / 수정)
	@KafkaListener(topics = "userTopic", groupId = "dbms-userTopic")
	public void userTopic(String data) throws Exception {
		JSONObject json = new JSONObject(data);
		if (Objects.equals(json.get("id"), saveAgentId)) {
			networkService.saveUser(json);
		}
	}

	// 유저 활동 토픽 (C R U D)
	@KafkaListener(topics = "actionTopic", groupId = "dbms-actionTopic")
	public void userAction(String data) throws Exception {
		JSONObject json = new JSONObject(data);

		String id = json.getString("id");
		if (Objects.equals(id, actionAgentId + "-02")) {
			kafkaService.saveActionData(json);
		}
	}
}
