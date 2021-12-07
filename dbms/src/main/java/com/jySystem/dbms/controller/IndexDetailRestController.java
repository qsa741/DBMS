package com.jySystem.dbms.controller;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jySystem.dbms.dto.DbDTO;
import com.jySystem.dbms.service.DbmsServiceImpl;

@RestController
@RequestMapping("/dbmsTool")
public class IndexDetailRestController {

	@Autowired
	private DbmsServiceImpl dbmsService;
	
	@RequestMapping("/indexDetailsIndex")
	public Map<String, Object> indexDetailsIndex(String indexName, DbDTO dto) throws ClassNotFoundException, SQLException {
		return dbmsService.tableDetailsIndexesBottom(indexName, dto);
	}
	
	@RequestMapping("/indexDetailsColumns")
	public List<Map<String, Object>> indexDetailsColumns(String indexName, DbDTO dto) throws ClassNotFoundException, SQLException {
		return dbmsService.indexDetailsColumns(indexName, dto);
	}
	
}
