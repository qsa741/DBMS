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
public class ViewDetailsRestController {

	@Autowired
	private DbmsServiceImpl dbmsService;
	
	@RequestMapping("/viewDetailsColumns")
	public List<Map<String, Object>> viewDetailsColumns(String schema, String viewName, DbDTO dto) throws ClassNotFoundException, SQLException {
		return dbmsService.viewDetailsColumns(schema, viewName, dto);
	}
	
	@RequestMapping("/viewDetailsScript")
	public List<Map<String, Object>> viewDetailsScript(String schema, String viewName, DbDTO dto) throws ClassNotFoundException, SQLException {
		return dbmsService.viewDetailsScript(schema, viewName, dto);
	}
}
