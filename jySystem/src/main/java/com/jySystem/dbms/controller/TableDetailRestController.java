package com.jySystem.dbms.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jySystem.dbms.service.DbmsServiceImpl;

@RequestMapping("/dbms")
@RestController
public class TableDetailRestController {

	@Autowired
	private DbmsServiceImpl dbmsService;
	
	@RequestMapping("/tableDetailsTable")
	public Map<String, Object> tableDetailsTable(String table, String schema) {
		System.out.println(table + " " + schema);
		return null;
//		return dbmsService.tableDetailsTable(table , "TESTER");
	}
	
}
