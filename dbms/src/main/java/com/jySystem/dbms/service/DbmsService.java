package com.jySystem.dbms.service;

import java.util.List;
import java.util.Map;

import org.springframework.boot.configurationprocessor.json.JSONObject;

import com.jySystem.dbms.dto.ChartDataSetDTO;
import com.jySystem.dbms.dto.DbDTO;
import com.jySystem.dbms.dto.LoadObjectDTO;
import com.jySystem.dbms.dto.ObjectDTO;
import com.jySystem.dbms.dto.TreeDTO;
import com.jySystem.exception.JYException;

public interface DbmsService {

	// DB 커넥션 테스트
	public boolean connectionTest(DbDTO dto) throws JYException;

	// 모든 스키마 가져오기
	public List<TreeDTO> getAllSchemas(String userId) throws JYException;

	// 스키마 정보 가져오기
	public List<TreeDTO> schemaInfo(String schema, String userId) throws JYException;

	// 오브젝트 정보 가져오기
	public List<TreeDTO> objectInfo(ObjectDTO object, String userId) throws JYException;

	// 테이블 불러오기
	public Map<String, Object> loadObject(LoadObjectDTO dto, String userId) throws JYException;

	// 테이블 자식 정보 불러오기
	public Map<String, Object> getTableChildren(LoadObjectDTO dto, String userId)
			throws JYException;

	// 스키마 디테일 정보 검색
	public Map<String, Object> schemaDetailsInfo(String schema, String userId)
			throws JYException;

	// 스키마 디테일 정보 검색
	public List<Map<String, Object>> schemaDetailsRoleGrants(String schema, String userId)
			throws JYException;

	// 스키마 디테일 정보 검색
	public List<Map<String, Object>> schemaDetailsSystemPrivileges(String schema, String userId)
			throws JYException;

	// 스키마 디테일 정보 검색
	public List<Map<String, Object>> schemaDetailsExtents(String schema, String userId)
			throws JYException;

	// 테이블 디테일 정보 검색
	public Map<String, Object> tableDetailsTable(String table, String schema, String userId)
			throws JYException;

	// 테이블 디테일 정보 검색
	public List<Map<String, Object>> tableDetailsColumns(String table, String schema, String userId)
			throws JYException;

	// 테이블 디테일 정보 검색
	public List<Map<String, Object>> tableDetailsIndexesTop(String table, String schema, String userId)
			throws JYException;

	// 테이블 디테일 정보 검색
	public Map<String, Object> tableDetailsIndexesBottom(String indexName, String userId)
			throws JYException;

	// 테이블 디테일 정보 검색
	public List<Map<String, Object>> tableDetailsConstraints(String table, String schema, String userId)
			throws JYException;

	// 인덱스 디테일 정보 검색
	public List<Map<String, Object>> indexDetailsColumns(String indexName, String userId)
			throws JYException;

	// 스키마 디테일 정보 검색
	public Map<String, Object> sequenceDetailsInfo(String sequenceName, String userId)
			throws JYException;

	// 뷰 디테일 정보 검색
	public List<Map<String, Object>> viewDetailsColumns(String schema, String viewName, String userId)
			throws JYException;

	// 뷰 디테일 정보 검색
	public List<Map<String, Object>> viewDetailsScript(String schema, String viewName, String userId)
			throws JYException;

	// 펑션 디테일 정보 검색
	public List<Map<String, Object>> detailsCode(String schema, String name, String type, String userId)
			throws JYException;

	// SQL 한줄 실행
	public Map<String, Object> runCurrentSQL(String sql, int cursor, String userId)
			throws JYException;

	// SQL 전체 실행
	public List<Map<String, Object>> runAllSQL(String sql, String userId) throws JYException;

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
