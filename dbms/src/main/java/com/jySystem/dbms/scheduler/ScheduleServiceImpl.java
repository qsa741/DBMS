package com.jySystem.dbms.scheduler;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.jySystem.dbms.sql.SchedulerSQL;
import com.jySystem.network.service.NetworkServiceImpl;

@Service
public class ScheduleServiceImpl implements ScheduleService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private NetworkServiceImpl networkService;

	@Autowired
	private SchedulerSQL schedulerSQL;

	// 오전 10시마다 ActionData 테이블 정리 명령어 SSO로 전송
	@Scheduled(cron = "0 0 10 * * *")
	@Override
	public void settingUserAction() throws Exception {
		logger.info(new Date().toString() + " / Setting User Action Scheduler Execute");
		networkService.settingUserAction();
	}

	// 오전 10시 5분에 ActionScheduler에 저장된 데이터 처리
	@Scheduled(cron = "0 5 10 * * *")
	@Override
	public void saveActionData() throws Exception {
		logger.info(new Date().toString() + " / Save Action Data Scheduler Execute");
		List<Map<String, Object>> list = schedulerSQL.actionScheduler();
		Map<String, Object> map;

		for (int i = 0; i < list.size(); i++) {
			map = list.get(i);

			JSONObject json = new JSONObject((String) map.get("DATA"));

			JSONObject create = new JSONObject(json.getString("create"));
			JSONObject read = new JSONObject(json.getString("read"));
			JSONObject update = new JSONObject(json.getString("update"));
			JSONObject delete = new JSONObject(json.getString("delete"));

			schedulerSQL.saveActionData("C", create);
			schedulerSQL.saveActionData("R", read);
			schedulerSQL.saveActionData("U", update);
			schedulerSQL.saveActionData("D", delete);

		}
	}

}
