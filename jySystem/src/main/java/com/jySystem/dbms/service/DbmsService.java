package com.jySystem.dbms.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.boot.configurationprocessor.json.JSONObject;

import com.jySystem.dbms.dto.ChartDataSetDTO;
import com.jySystem.dbms.dto.DbDTO;
import com.jySystem.dbms.dto.LoadObjectDTO;
import com.jySystem.dbms.dto.ObjectDTO;
import com.jySystem.dbms.dto.TreeDTO;

public interface DbmsService {

	// DB 커넥션 테스트
	public boolean connectionTest(DbDTO dto) throws Exception;

	// 모든 스키마 가져오기
	public List<TreeDTO> getAllSchemas() throws ClassNotFoundException, SQLException;

	// 스키마 정보 가져오기
	public List<TreeDTO> schemaInfo(String schema) throws ClassNotFoundException, SQLException;

	// 오브젝트 정보 가져오기
	public List<TreeDTO> objectInfo(ObjectDTO object) throws ClassNotFoundException, SQLException;

	// 테이블 불러오기
	public Map<String, Object> loadObject(LoadObjectDTO dto) throws ClassNotFoundException, SQLException;

	// 테이블 자식 정보 불러오기
	public Map<String, Object> getTableChildren(LoadObjectDTO dto) throws ClassNotFoundException, SQLException;

	// 스키마 디테일 정보 검색
	public Map<String, Object> schemaDetailsInfo(String schema) throws ClassNotFoundException, SQLException;

	// 스키마 디테일 정보 검색
	public List<Map<String, Object>> schemaDetailsRoleGrants(String schema) throws ClassNotFoundException, SQLException;

	// 스키마 디테일 정보 검색
	public List<Map<String, Object>> schemaDetailsSystemPrivileges(String schema)
			throws ClassNotFoundException, SQLException;

	// 스키마 디테일 정보 검색
	public List<Map<String, Object>> schemaDetailsExtents(String schema) throws ClassNotFoundException, SQLException;

	// 테이블 디테일 정보 검색
	public Map<String, Object> tableDetailsTable(String table, String schema)
			throws ClassNotFoundException, SQLException;

	// SQL 한줄 실행
	public Map<String, Object> runCurrentSQL(String sql, int cursor) throws ClassNotFoundException, SQLException;

	// SQL 전체 실행
	public List<Map<String, Object>> runAllSQL(String sql) throws ClassNotFoundException, SQLException;

	// 차트 연도 가져오기
	public List<String> getChartYears() throws ClassNotFoundException, SQLException;

	// 차트 월 가져오기
	public List<String> getChartMonth(String year) throws ClassNotFoundException, SQLException;

	// mChart 차트 정보 가져오기
	public Map<String, Object> mChartInfo(String year) throws Exception;

	// mChart 차트 dataset 세팅
	public Map<String, Object> mChartDataSet(List<JSONObject> data, ChartDataSetDTO dto) throws Exception;

	// dChart 차트 정보 가져오기
	public Map<String, Object> dChartInfo(String year, String month) throws Exception;

	// dChart 차트 dataset 세팅
	public Map<String, Object> dChartDataSet(List<JSONObject> data, int day, ChartDataSetDTO dto) throws Exception;

}
