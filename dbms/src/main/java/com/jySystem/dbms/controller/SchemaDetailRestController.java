package com.jySystem.dbms.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jySystem.dbms.dto.DbObjectDTO;
import com.jySystem.dbms.service.DbmsServiceImpl;

@RequestMapping("/dbmsTool")
@RestController
public class SchemaDetailRestController {

	@Autowired
	private DbmsServiceImpl dbmsService;

	// 스키마 디테일 Info 정보 불러오기
	@RequestMapping("/schemaDetailsInfo")
	public Map<String, Object> schemaDetailInfo(DbObjectDTO dto, String userId)
			throws Exception {
		return dbmsService.schemaDetailsInfo(dto, userId);
	}

	// 스키마 디테일 Role Grants 정보 불러오기
	@RequestMapping("/schemaDetailsRoleGrants")
	public List<Map<String, Object>> schemaDetailsRoleGrants(DbObjectDTO dto, String userId)
			throws Exception {
		return dbmsService.schemaDetailsRoleGrants(dto, userId);
	}

	// 스키마 디테일 System Privileges 정보 불러오기
	@RequestMapping("/schemaDetailsSystemPrivileges")
	public List<Map<String, Object>> schemaDetailsSystemPrivileges(DbObjectDTO dto, String userId)
			throws Exception {
		return dbmsService.schemaDetailsSystemPrivileges(dto, userId);
	}

	// 스키마 디테일 Extends 정보 불러오기
	@RequestMapping("/schemaDetailsExtents")
	public List<Map<String, Object>> schemaDetailsExtends(DbObjectDTO dto, String userId)
			throws Exception {
		return dbmsService.schemaDetailsExtents(dto, userId);
	}

}
