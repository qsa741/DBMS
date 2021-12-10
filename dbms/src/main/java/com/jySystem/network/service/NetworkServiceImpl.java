package com.jySystem.network.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;

import com.jySystem.dbms.sql.DbmsSQL;
import com.jySystem.kafka.KafkaProducer;

@Service
public class NetworkServiceImpl implements NetworkService{

	@Value("${save.agent.id}")
	private String saveAgentId;
	
	@Value("${action.agent.id}")
	private String actionAgentId;
	
	@Value("${action.agent.network}")
	private String actionAgentNetwork;
	
	@Value("${dbms.properties.network}")
	private String dbmsPropertiesNetwork;
	
	private final KafkaProducer producer;

	@Autowired
	NetworkServiceImpl(KafkaProducer producer) {
		this.producer = producer;
	}
	
	@Autowired
	private DbmsSQL dbmsSQL;
	
	// Agent와 같은 망인지 확인
	@Override
	public boolean networkCheck(String agentNetwork) {
		boolean result = false;
		if(dbmsPropertiesNetwork.equals(agentNetwork)) {
			result = true;
		}
		
		return result;
	}
	
	// type이 DB일 경우 tiberoDB의 userScheduler에 저장
	// type이 kafka일 경우 saveUser에 명령어 저장
	@Override
	public void saveUser(JSONObject data) throws Exception {
		if(data.get("type").equals("DB")) {
			dbmsSQL.userSchedulerSave((String) data.get("data"));
		} else if(data.get("type").equals("KAFKA")) {
			JSONObject json = new JSONObject();
			json.put("id", saveAgentId + "-02");
			json.put("data", (String) data.get("data"));
			json.put("time", new Date().toString());
			
			this.producer.sendUserSave(json.toString());
		}
	}
	
	// SSO에게 userAction 정리 실행 메세지 전송
	@Override
	public void settingUserAction() throws Exception {
		JSONObject json = new JSONObject();
		json.put("id", actionAgentId);
		if(networkCheck(actionAgentNetwork)) {
			json.put("type", "DB");
		} else {
			json.put("type", "KAFKA");
		}
		json.put("time", new Date().toString());
		
		this.producer.sendUserAction(json.toString());
	}
}
