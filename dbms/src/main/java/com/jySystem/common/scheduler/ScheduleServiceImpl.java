package com.jySystem.common.scheduler;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.jySystem.network.service.NetworkServiceImpl;

@Service
public class ScheduleServiceImpl implements ScheduleService {
	
	@Autowired
	private NetworkServiceImpl networkService;
	
	@Autowired
	private SchedulerSQL schedulerSQL;
	
	// 오전 10시마다 ActionData 테이블 정리 명령어 SSO로 전송
	@Scheduled(cron = "0 0 10 * * *")
	@Override
	public void settingUserAction() throws Exception {
		networkService.settingUserAction();
	}

	// 오전 10시 5분에 ActionScheduler에 저장된 데이터 처리
	@Scheduled(cron = "0 5 10 * * *")
	@Override
	public void saveActionData() throws Exception {
		List<Map<String, Object>> list = schedulerSQL.actionScheduler();
		Map<String, Object> map;
		
		for(int i = 0; i < list.size(); i++) {
			map = list.get(i);
			
			JSONObject json = new JSONObject((String)map.get("DATA"));
			
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
	
}
