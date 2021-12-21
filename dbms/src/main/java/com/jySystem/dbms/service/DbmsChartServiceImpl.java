package com.jySystem.dbms.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;

import com.jySystem.dbms.dto.ChartDataSetDTO;
import com.jySystem.dbms.sql.DbmsSQL;
import com.jySystem.exception.JYException;
import com.jySystem.kafka.config.Action;

@Service
public class DbmsChartServiceImpl implements DbmsChartService {

	@Autowired
	private DbmsSQL dbmsSQL;
	
	// 차트에 들어가는 연도 구하기
	@Override
	public List<String> getChartYears() throws JYException {
		return dbmsSQL.getChartYears();
	}

	// 차트에 해당 연도 데이터가 있는 월 구하기
	@Override
	public List<String> getChartMonth(String year) throws JYException {
		return dbmsSQL.getChartMonth(year);
	}

	// mChart 정보 세팅 : 선택된 연도 데이터 정리
	@Override
	public Map<String, Object> mChartInfo(String year) throws JYException {
		Map<String, Object> result = new HashMap<String, Object>();

		// action별로 데이터 생성
		List<JSONObject> create = dbmsSQL.getActionData(year, Action.CREATE.name());
		List<JSONObject> read = dbmsSQL.getActionData(year, Action.READ.name());
		List<JSONObject> update = dbmsSQL.getActionData(year, Action.UPDATE.name());
		List<JSONObject> delete = dbmsSQL.getActionData(year, Action.DELETE.name());

		String[] monthArray = { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12" };

		// 디자인 관련 초기값 생성자로 세팅
		ChartDataSetDTO createDataSet = new ChartDataSetDTO("가입", "white", "red", 2);
		ChartDataSetDTO readDataSet = new ChartDataSetDTO("로그인", "white", "orange", 2);
		ChartDataSetDTO updateDataSet = new ChartDataSetDTO("수정", "white", "blue", 2);
		ChartDataSetDTO deleteDataSet = new ChartDataSetDTO("탈퇴", "white", "black", 2);

		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		data.add(mChartDataSet(create, createDataSet));
		data.add(mChartDataSet(read, readDataSet));
		data.add(mChartDataSet(update, updateDataSet));
		data.add(mChartDataSet(delete, deleteDataSet));

		result.put("labels", monthArray);
		result.put("datasets", data);

		return result;
	}

	// mChart에 들어갈 DataSet 세팅
	@Override
	public Map<String, Object> mChartDataSet(List<JSONObject> list, ChartDataSetDTO dto) throws JYException {
		// 1월 ~ 12월
		int[] monthCount = new int[12];

		try {
			for (JSONObject json : list) {
				int month = json.getInt("MONTH") - 1;
				monthCount[month] += json.getInt("COUNT");
			}
		} catch (JSONException je) {
			throw new JYException("JSON Exception", je);
		}

		List<Integer> data = new ArrayList<Integer>();

		for (int num : monthCount) {
			data.add(num);
		}

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("label", dto.getLabel());
		map.put("backgroundColor", dto.getBackgroundColor());
		map.put("borderColor", dto.getBorderColor());
		map.put("borderWidth", dto.getBorderWidth());
		map.put("data", data);

		return map;
	}

	// dChart 차트 정보 세팅
	@Override
	public Map<String, Object> dChartInfo(String year, String month) throws JYException {
		Map<String, Object> result = new HashMap<String, Object>();

		// action별로 데이터 세팅
		List<JSONObject> create = dbmsSQL.getActionData(year, month, Action.CREATE.name());
		List<JSONObject> read = dbmsSQL.getActionData(year, month, Action.READ.name());
		List<JSONObject> update = dbmsSQL.getActionData(year, month, Action.UPDATE.name());
		List<JSONObject> delete = dbmsSQL.getActionData(year, month, Action.DELETE.name());

		List<String> labels = new ArrayList<String>();
		// 31일까지 있는 달
		String[] monthArray = { "01", "03", "05", "07", "08", "10", "12" };
		int day = 0;

		if (Arrays.stream(monthArray).anyMatch(month::equals)) {
			day = 31;
			// 2월 (28일까지)
		} else if (month.equals("02")) {
			day = 28;
			// 윤년
			if (Integer.parseInt(year) / 4 == 0) {
				day += 1;
			}
			// 나머지 월
		} else {
			day = 30;
		}

		// 1자리수의 숫자 앞에 "0" 추가
		for (int i = 1; i <= day; i++) {
			if (i / 10.0 < 1) {
				labels.add("0" + i);
			} else {
				labels.add("" + i);
			}
		}

		// 디자인 관련 초기값 생성자로 세팅
		ChartDataSetDTO createDataSet = new ChartDataSetDTO("가입", "white", "red", 2);
		ChartDataSetDTO readDataSet = new ChartDataSetDTO("로그인", "white", "orange", 2);
		ChartDataSetDTO updateDataSet = new ChartDataSetDTO("수정", "white", "blue", 2);
		ChartDataSetDTO deleteDataSet = new ChartDataSetDTO("탈퇴", "white", "black", 2);

		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		data.add(dChartDataSet(create, day, createDataSet));
		data.add(dChartDataSet(read, day, readDataSet));
		data.add(dChartDataSet(update, day, updateDataSet));
		data.add(dChartDataSet(delete, day, deleteDataSet));

		result.put("labels", labels);
		result.put("datasets", data);

		return result;
	}

	// dChart에 들어갈 데이터 세팅
	@Override
	public Map<String, Object> dChartDataSet(List<JSONObject> list, int days, ChartDataSetDTO dto) throws JYException {
		int[] dayCount = new int[days];

		try {
			for (JSONObject json : list) {
				int day = json.getInt("DAY") - 1;
				dayCount[day] += json.getInt("COUNT");
			}
		} catch (JSONException je) {
			throw new JYException("JSON Exception", je);
		}

		List<Integer> data = new ArrayList<Integer>();

		for (int num : dayCount) {
			data.add(num);
		}

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("label", dto.getLabel());
		map.put("backgroundColor", dto.getBackgroundColor());
		map.put("borderColor", dto.getBorderColor());
		map.put("borderWidth", dto.getBorderWidth());
		map.put("data", data);

		return map;
	}

	
}
