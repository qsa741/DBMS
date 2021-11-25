package com.project.dbms.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.dbms.dto.DbDTO;
import com.project.dbms.dto.LoadObjectDTO;
import com.project.dbms.dto.ObjectDTO;
import com.project.dbms.dto.TreeDTO;
import com.project.dbms.service.DbmsServiceImpl;
import com.project.dbms.service.UserServiceImpl;

@RequestMapping("/dbms")
@RestController
public class DbmsRestController {

	@Autowired
	private DbmsServiceImpl dbmsService;
	
	@Autowired
	private UserServiceImpl userService;
	
	// 모든 스키마 불러오기
	@RequestMapping("/allSchemas")
	public List<TreeDTO> allSchemas(){
		return dbmsService.getAllSchemas();
	}
	
	// 스키마 정보 불러오기
	@RequestMapping("/schemaInfo")
	public List<TreeDTO> schemaInfo(String schema) {
		return dbmsService.schemaInfo(schema);
	}
	
	// 오브젝트 불러오기
	@RequestMapping("/objectInfo")
	public List<TreeDTO> objectInfo(ObjectDTO object) {
		return dbmsService.objectInfo(object);
	}
	
	// 테이블 정보 가져오기
	@RequestMapping("/loadObject")
	public Map<String, Object> loadObject(LoadObjectDTO dto) {
		return dbmsService.loadObject(dto);
	}

	// 테이블 정보 가져오기
	@RequestMapping("/getTableChildren")
	public Map<String, Object> getTableChildren(LoadObjectDTO dto) {
		return dbmsService.getTableChildren(dto);
	}
	
	// 스키마 디테일 Info 정보 불러오기
	@RequestMapping("/schemaDetailsInfo") 
	public Map<String, Object> schemaDetailInfo(String schema) {
		return dbmsService.schemaDetailsInfo(schema);
	}
	
	// 스키마 디테일 Role Grants 정보 불러오기
	@RequestMapping("/schemaDetailsRoleGrants")
	public List<Map<String, Object>> schemaDetailsRoleGrants(String schema) {
		return dbmsService.schemaDetailsRoleGrants(schema);
	}
	
	// 스키마 디테일 System Privileges 정보 불러오기
	@RequestMapping("/schemaDetailsSystemPrivileges")
	public List<Map<String, Object>> schemaDetailsSystemPrivileges(String schema) {
		return dbmsService.schemaDetailsSystemPrivileges(schema);
	}
	
	// DB 커넥션 테스트
	@RequestMapping("/connectionTest")
	public boolean connecntionTest(DbDTO dto) throws Exception {
		return userService.connectionTest(dto);
	}
	
	// SQL 한줄 실행
	@RequestMapping("/runCurrentSQL")
	public Map<String, Object> runCurrentSQL(String sql, int cursor) throws Exception {
		return dbmsService.runCurrentSQL(sql, cursor);
	}
	
	// SQL 전체 실행
	@RequestMapping("/runAllSQL")
	public List<Map<String, Object>> runAllSQL(String sqls) throws Exception {
		return dbmsService.runAllSQL(sqls);
	}
	
}
