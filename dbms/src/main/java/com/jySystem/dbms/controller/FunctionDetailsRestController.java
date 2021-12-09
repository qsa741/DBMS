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
public class FunctionDetailsRestController {

	@Autowired
	private DbmsServiceImpl dbmsService;
	
	@RequestMapping("/functionDetailsCode")
	public List<Map<String, Object>> functionDetailsCode(String schema, String functionName, DbDTO dto) throws ClassNotFoundException, SQLException {
		return dbmsService.detailsCode(schema, functionName, dto, "FUNCTION");
	}
	
}
