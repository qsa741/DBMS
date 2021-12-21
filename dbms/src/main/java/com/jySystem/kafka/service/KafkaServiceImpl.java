package com.jySystem.kafka.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;

import com.jySystem.dbms.sql.SchedulerSQL;
import com.jySystem.kafka.config.Action;

@Service
public class KafkaServiceImpl implements KafkaService {

	@Autowired
	private SchedulerSQL schedulerSQL;

	// ActionData 테이블에 일간 사용량 저장
	@Override
	public void saveActionData(JSONObject data) throws Exception {
		JSONObject json = new JSONObject(data.getString("data"));

		JSONObject create = new JSONObject(json.getString(Action.CREATE.name()));
		JSONObject read = new JSONObject(json.getString(Action.READ.name()));
		JSONObject update = new JSONObject(json.getString(Action.UPDATE.name()));
		JSONObject delete = new JSONObject(json.getString(Action.DELETE.name()));

		schedulerSQL.saveActionData(Action.CREATE.name(), create);
		schedulerSQL.saveActionData(Action.READ.name(), read);
		schedulerSQL.saveActionData(Action.UPDATE.name(), update);
		schedulerSQL.saveActionData(Action.DELETE.name(), delete);

	}

}