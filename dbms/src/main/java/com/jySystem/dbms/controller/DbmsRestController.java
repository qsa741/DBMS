package com.jySystem.dbms.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jySystem.dbms.dto.DbDTO;
import com.jySystem.dbms.dto.DbObjectDTO;
import com.jySystem.dbms.dto.TreeDTO;
import com.jySystem.dbms.service.DbmsServiceImpl;

@RequestMapping("/dbmsTool")
@RestController
public class DbmsRestController {

	@Autowired
	private DbmsServiceImpl dbmsService;

	// 모든 스키마 불러오기
	@RequestMapping("/allSchemas")
	public List<TreeDTO> allSchemas(String userId) throws Exception {
		return dbmsService.getAllSchemas(userId);
	}

	// 스키마 정보 불러오기
	@RequestMapping("/schemaInfo")
	public List<TreeDTO> schemaInfo(DbObjectDTO dto, String userId) throws Exception {
		return dbmsService.schemaInfo(dto, userId);
	}

	// 오브젝트 불러오기
	@RequestMapping("/objectInfo")
	public List<TreeDTO> objectInfo(DbObjectDTO dto, String userId) throws Exception {
		return dbmsService.objectInfo(dto, userId);
	}

	// 테이블 정보 가져오기
	@RequestMapping("/loadObject")
	public Map<String, Object> loadObject(DbObjectDTO dto, String userId) throws Exception {
		return dbmsService.loadObject(dto, userId);
	}

	// 테이블 정보 가져오기
	@RequestMapping("/getTableChildren")
	public Map<String, Object> getTableChildren(DbObjectDTO dto, String userId) throws Exception {
		return dbmsService.getTableChildren(dto, userId);
	}

	// DB 커넥션 테스트
	@RequestMapping("/connectionTest")
	public boolean connecntionTest(DbDTO dto) throws Exception {
		return dbmsService.connectionTest(dto);
	}

	// SQL 한줄 실행
	@RequestMapping("/runCurrentSQL")
	public Map<String, Object> runCurrentSQL(String sql, int cursor, String userId) throws Exception {
		return dbmsService.runCurrentSQL(sql, cursor, userId);
	}

	// SQL 전체 실행
	@RequestMapping("/runAllSQL")
	public List<Map<String, Object>> runAllSQL(String sqls, String userId) throws Exception {
		return dbmsService.runAllSQL(sqls, userId);
	}

	// mChart 차트 정보 가져오기
	@RequestMapping("/setMChart")
	public Map<String, Object> setMChart(String year) throws Exception {
		return dbmsService.mChartInfo(year);
	}

	// dChart 차트 정보 가져오기
	@RequestMapping("/setDChart")
	public Map<String, Object> setDChart(String year, String month) throws Exception {
		return dbmsService.dChartInfo(year, month);
	}

	// 차트 연도 가져오기
	@RequestMapping("/getChartYears")
	public List<String> getChartYears() throws Exception {
		return dbmsService.getChartYears();
	}

	// 차트 월 가져오기
	@RequestMapping("/getChartMonth")
	public List<String> getChartMonth(String year) throws Exception {
		return dbmsService.getChartMonth(year);
	}

}
