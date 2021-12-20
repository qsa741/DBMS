package com.jySystem.dbms.service;

import java.util.List;
import java.util.Map;

import org.springframework.boot.configurationprocessor.json.JSONObject;

import com.jySystem.dbms.dto.ChartDataSetDTO;
import com.jySystem.exception.JYException;

public interface DbmsChartService {

	// 차트 연도 가져오기
	public List<String> getChartYears() throws JYException;

	// 차트 월 가져오기
	public List<String> getChartMonth(String year) throws JYException;

	// mChart 차트 정보 가져오기
	public Map<String, Object> mChartInfo(String year) throws JYException;

	// mChart 차트 dataset 세팅
	public Map<String, Object> mChartDataSet(List<JSONObject> data, ChartDataSetDTO dto) throws JYException;

	// dChart 차트 정보 가져오기
	public Map<String, Object> dChartInfo(String year, String month) throws JYException;

	// dChart 차트 dataset 세팅
	public Map<String, Object> dChartDataSet(List<JSONObject> data, int day, ChartDataSetDTO dto) throws JYException;

	
}
