package com.project.dbms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;

import com.project.dbms.sql.SchedulerSQL;

@Service
public class KafkaServiceImpl implements KafkaService {

	@Autowired
	private SchedulerSQL schedulerSQL;
	
	// ActionData 테이블에 일간 사용량 저장
	@Override
	public void saveActionData(JSONObject data) throws Exception {
		JSONObject json = new JSONObject(data.getString("data"));
		
		JSONObject create = new JSONObject(json.getString("create"));
		JSONObject read = new JSONObject(json.getString("read"));
		JSONObject update = new JSONObject(json.getString("update"));
		JSONObject delete = new JSONObject(json.getString("delete"));
		
		schedulerSQL.saveActionData("C",create);
		schedulerSQL.saveActionData("R",read);
		schedulerSQL.saveActionData("U",update);
		schedulerSQL.saveActionData("D",delete);
		
	}
	
}
