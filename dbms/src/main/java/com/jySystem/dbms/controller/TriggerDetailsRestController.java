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
public class TriggerDetailsRestController {

	@Autowired
	private DbmsServiceImpl dbmsService;
	
	// TRIGGER 디테일 SOURCE 조회
	@RequestMapping("/triggerDetailsSource")
	public List<Map<String, Object>> triggerDetailsSource(String schema, String triggerName, DbDTO dto) throws ClassNotFoundException, SQLException {
		return dbmsService.detailsCode(schema, triggerName, dto, "TRIGGER");
	}
}
