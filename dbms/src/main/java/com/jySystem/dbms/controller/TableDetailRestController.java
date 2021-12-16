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
public class TableDetailRestController {

	@Autowired
	private DbmsServiceImpl dbmsService;

	// TABLE 디테일 TABLE 조회
	@RequestMapping("/tableDetailsTable")
	public Map<String, Object> tableDetailsTable(DbObjectDTO dto, String userId)
			throws Exception {
		return dbmsService.tableDetailsTable(dto, userId);
	}

	// TABLE 디테일 COLUMNS 조회
	@RequestMapping("/tableDetailsColumns")
	public List<Map<String, Object>> tableDetailsColumns(DbObjectDTO dto, String userId)
			throws Exception {
		return dbmsService.tableDetailsColumns(dto, userId);
	}

	// TABLE 디테일 INDEXES TOP 조회
	@RequestMapping("/tableDetailsIndexesTop")
	public List<Map<String, Object>> tableDetailsIndexesTop(DbObjectDTO dto, String userId)
			throws Exception {
		return dbmsService.tableDetailsIndexesTop(dto, userId);
	}

	// TABLE 디테일 INDEXES BOTTOM 조회
	@RequestMapping("/tableDetailsIndexesBottom")
	public Map<String, Object> tableDetailsIndexesBottom(DbObjectDTO dto, String userId)
			throws Exception {
		return dbmsService.tableDetailsIndexesBottom(dto, userId);
	}

	// TABLE 디테일 CONSTRAINTS 조회
	@RequestMapping("/tableDetailsConstraints")
	public List<Map<String, Object>> tableDetailsConstraints(DbObjectDTO dto, String userId)
			throws Exception {
		return dbmsService.tableDetailsConstraints(dto, userId);
	}
	
	// TABLE 디테일 SCRIPT 조회
	@RequestMapping("/tableDetailsScript")
	public String tableDetailsScript(DbObjectDTO dto, String userId) throws Exception {
		return dbmsService.tableDetailsScript(dto, userId);
	}
	

}
