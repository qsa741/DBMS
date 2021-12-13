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
public class TypeDetailsRestController {

	@Autowired
	private DbmsServiceImpl dbmsService;

	// TYPE 디테일 CODE 조회
	@RequestMapping("/typeDetailsCode")
	public List<Map<String, Object>> typeDetailsCode(String schema, String typeName, String userId)
			throws ClassNotFoundException, SQLException {
		return dbmsService.detailsCode(schema, typeName, "TYPE", userId);
	}

}
