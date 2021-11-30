package com.project.dbms.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;

import com.project.dbms.kafka.KafkaProducer;
import com.project.dbms.properties.Action;
import com.project.dbms.properties.Dbms;
import com.project.dbms.properties.Save;
import com.project.dbms.sql.DbmsSQL;

@Service
public class NetworkServiceImpl implements NetworkService{

	@Autowired
	private Save save;

	@Autowired
	private Action action;
	
	@Autowired
	private Dbms dbms;
	
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
		Map<String, String> network = dbms.getProperties().get(0);
		if(network.get("network").equals(agentNetwork)) {
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
			List<Map<String, String>> agent = save.getAgent();
			JSONObject json = new JSONObject();
			json.put("id", agent.get(0).get("id"));
			json.put("data", (String) data.get("data"));
			json.put("time", new Date().toString());
			
			this.producer.sendSSO(json.toString());
		}
	}
	
	// SSO에게 userAction 정리 실행 메세지 전송
	@Override
	public void settingUserAction() throws Exception {
		JSONObject json = new JSONObject();
		List<Map<String, String>> agent = action.getAgent();
		json.put("id", agent.get(0).get("id"));
		if(networkCheck(agent.get(1).get("network"))) {
			json.put("type", "DB");
		} else {
			json.put("type", "KAFKA");
		}
		json.put("reg", new Date().toString());
		
		this.producer.userAction(json.toString());
	}
}
