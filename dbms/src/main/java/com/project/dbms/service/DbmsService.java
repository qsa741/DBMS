package com.project.dbms.service;

import java.util.List;
import java.util.Map;

import org.springframework.boot.configurationprocessor.json.JSONObject;

import com.project.dbms.dto.ChartDataSetDTO;
import com.project.dbms.dto.LoadObjectDTO;
import com.project.dbms.dto.ObjectDTO;
import com.project.dbms.dto.TreeDTO;

public interface DbmsService {

	// 모든 스키마 가져오기
	public List<TreeDTO> getAllSchemas();
	
	// 스키마 정보 가져오기
	public List<TreeDTO> schemaInfo(String schema);
	
	// 오브젝트 정보 가져오기
	public List<TreeDTO> objectInfo(ObjectDTO object);
	
	// 테이블 불러오기
	public Map<String, Object> loadObject(LoadObjectDTO dto);
	
	// 테이블 자식 정보 불러오기
	public Map<String, Object> getTableChildren(LoadObjectDTO dto);
	
	// 스키마 디테일 정보 검색
	public Map<String, Object> schemaDetailsInfo(String schema);

	// 스키마 디테일 정보 검색
	public List<Map<String, Object>> schemaDetailsRoleGrants(String schema);

	// 스키마 디테일 정보 검색
	public List<Map<String, Object>> schemaDetailsSystemPrivileges(String schema);

	// 스키마 디테일 정보 검색
	public List<Map<String, Object>> schemaDetailsExtents(String schema);

	// SQL 한줄 실행
	public Map<String, Object> runCurrentSQL(String sql, int cursor);
	
	// SQL 전체 실행
	public List<Map<String, Object>> runAllSQL(String sql);
	
	// 차트 연도 가져오기
	public List<String> getChartYears();
	
	// 차트 월 가져오기
	public List<String> getChartMonth(String year);

	// mChart 차트 정보 가져오기
	public Map<String, Object> mChartInfo(String year) throws Exception;
	
	// mChart 차트 dataset 세팅
	public Map<String, Object> mChartDataSet(List<JSONObject> data, ChartDataSetDTO dto) throws Exception;

	// dChart 차트 정보 가져오기
	public Map<String, Object> dChartInfo(String year, String month) throws Exception;
	
	// dChart 차트 dataset 세팅
	public Map<String, Object> dChartDataSet(List<JSONObject> data, int day, ChartDataSetDTO dto) throws Exception;
	
}
