package com.jySystem.dbms.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jySystem.dbms.service.DbmsServiceImpl;

@RequestMapping("/dbmsTool")
@RestController
public class TableDetailRestController {

	@Autowired
	private DbmsServiceImpl dbmsService;

	// TABLE 디테일 TABLE 조회
	@RequestMapping("/tableDetailsTable")
	public Map<String, Object> tableDetailsTable(String table, String schema, String userId)
			throws Exception {
		return dbmsService.tableDetailsTable(table, schema, userId);
	}

	// TABLE 디테일 COLUMNS 조회
	@RequestMapping("/tableDetailsColumns")
	public List<Map<String, Object>> tableDetailsColumns(String table, String schema, String userId)
			throws Exception {
		return dbmsService.tableDetailsColumns(table, schema, userId);
	}

	// TABLE 디테일 INDEXES TOP 조회
	@RequestMapping("/tableDetailsIndexesTop")
	public List<Map<String, Object>> tableDetailsIndexesTop(String table, String schema, String userId)
			throws Exception {
		return dbmsService.tableDetailsIndexesTop(table, schema, userId);
	}

	// TABLE 디테일 INDEXES BOTTOM 조회
	@RequestMapping("/tableDetailsIndexesBottom")
	public Map<String, Object> tableDetailsIndexesBottom(String indexName, String userId)
			throws Exception {
		return dbmsService.tableDetailsIndexesBottom(indexName, userId);
	}

	// TABLE 디테일 CONSTRAINTS 조회
	@RequestMapping("/tableDetailsConstraints")
	public List<Map<String, Object>> tableDetailsConstraints(String table, String schema, String userId)
			throws Exception {
		return dbmsService.tableDetailsConstraints(table, schema, userId);
	}
	
	// TABLE 디테일 SCRIPT 조회
	@RequestMapping("/tableDetailsScript")
	public String tableDetailsScript(String table, String schema, String userId) throws Exception {
		return dbmsService.tableDetailsScript(table, schema, userId);
	}
	

}
