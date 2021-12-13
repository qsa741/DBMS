package com.jySystem.dbms.controller;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jySystem.dbms.service.DbmsServiceImpl;

@RestController
@RequestMapping("/dbmsTool")
public class ViewDetailsRestController {

	@Autowired
	private DbmsServiceImpl dbmsService;

	// VIEW 디테일 COLUMNS 조회
	@RequestMapping("/viewDetailsColumns")
	public List<Map<String, Object>> viewDetailsColumns(String schema, String viewName, String userId)
			throws ClassNotFoundException, SQLException {
		return dbmsService.viewDetailsColumns(schema, viewName, userId);
	}

	// VIEW 디테일 SCRIPT 조회
	@RequestMapping("/viewDetailsScript")
	public List<Map<String, Object>> viewDetailsScript(String schema, String viewName, String userId)
			throws ClassNotFoundException, SQLException {
		return dbmsService.viewDetailsScript(schema, viewName, userId);
	}
}
