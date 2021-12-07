package com.jySystem.dbms.controller;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jySystem.dbms.dto.DbDTO;
import com.jySystem.dbms.service.DbmsServiceImpl;

@RequestMapping("/dbmsTool")
@RestController
public class TableDetailRestController {

	@Autowired
	private DbmsServiceImpl dbmsService;

	@RequestMapping("/tableDetailsTable")
	public Map<String, Object> tableDetailsTable(String table, String schema, DbDTO dto)
			throws ClassNotFoundException, SQLException {
		return dbmsService.tableDetailsTable(table, schema, dto);
	}
	
	@RequestMapping("/tableDetailsColumns")
	public List<Map<String, Object>> tableDetailsColumns(String table, String schema, DbDTO dto)
			throws ClassNotFoundException, SQLException {
		return dbmsService.tableDetailsColumns(table, schema, dto);
	}
	
	@RequestMapping("/tableDetailsIndexesTop")
	public List<Map<String, Object>> tableDetailsIndexesTop(String table, String schema, DbDTO dto)
			throws ClassNotFoundException, SQLException {
		return dbmsService.tableDetailsIndexesTop(table, schema, dto);
	}
	
	@RequestMapping("/tableDetailsIndexesBottom")
	public Map<String, Object> tableDetailsIndexesBottom(String indexName, DbDTO dto)
			throws ClassNotFoundException, SQLException {
		return dbmsService.tableDetailsIndexesBottom(indexName, dto);
	}
	
	@RequestMapping("/tableDetailsConstraints")
	public List<Map<String, Object>> tableDetailsConstraints(String table, String schema, DbDTO dto)
			throws ClassNotFoundException, SQLException {
		return dbmsService.tableDetailsConstraints(table, schema, dto);
	}

}
