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
public class SchemaDetailRestController {

	@Autowired
	private DbmsServiceImpl dbmsService;
	
	// 스키마 디테일 Info 정보 불러오기
	@RequestMapping("/schemaDetailsInfo") 
	public Map<String, Object> schemaDetailInfo(String schema, DbDTO dto) throws ClassNotFoundException, SQLException {
		return dbmsService.schemaDetailsInfo(schema, dto);
	}
	
	// 스키마 디테일 Role Grants 정보 불러오기
	@RequestMapping("/schemaDetailsRoleGrants")
	public List<Map<String, Object>> schemaDetailsRoleGrants(String schema, DbDTO dto) throws ClassNotFoundException, SQLException {
		return dbmsService.schemaDetailsRoleGrants(schema, dto);
	}
	
	// 스키마 디테일 System Privileges 정보 불러오기
	@RequestMapping("/schemaDetailsSystemPrivileges")
	public List<Map<String, Object>> schemaDetailsSystemPrivileges(String schema, DbDTO dto) throws ClassNotFoundException, SQLException {
		return dbmsService.schemaDetailsSystemPrivileges(schema, dto);
	}

	// 스키마 디테일 Extends 정보 불러오기
	@RequestMapping("/schemaDetailsExtents")
	public List<Map<String, Object>> schemaDetailsExtends(String schema, DbDTO dto) throws ClassNotFoundException, SQLException {
		return dbmsService.schemaDetailsExtents(schema, dto);
	}
	
}
