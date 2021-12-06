package com.jySystem.dbms.controller;

import java.sql.SQLException;
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

}
