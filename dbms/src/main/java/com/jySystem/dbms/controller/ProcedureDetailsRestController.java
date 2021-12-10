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
public class ProcedureDetailsRestController {

	@Autowired
	private DbmsServiceImpl dbmsService;
	
	// PROCEDURE 디테일 CODE 조회
	@RequestMapping("/procedureDetailsCode")
	public List<Map<String, Object>> procedureDetailsCode(String schema, String procedureName, DbDTO dto) throws ClassNotFoundException, SQLException {
		return dbmsService.detailsCode(schema, procedureName, dto, "PROCEDURE");
	}
}
