package com.jySystem.dbms.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jySystem.dbms.dto.DbObjectDTO;
import com.jySystem.dbms.service.DbmsDetailServiceImpl;

@RequestMapping("/dbmsTool")
@RestController
public class TableDetailRestController {

	@Autowired
	private DbmsDetailServiceImpl dbmsDetailService;

	// TABLE 디테일 TABLE 조회
	@RequestMapping("/tableDetailsTable")
	public Map<String, Object> tableDetailsTable(DbObjectDTO dto, String userId) throws Exception {
		return dbmsDetailService.tableDetailsTable(dto, userId);
	}

	// TABLE 디테일 COLUMNS 조회
	@RequestMapping("/tableDetailsColumns")
	public List<Map<String, Object>> tableDetailsColumns(DbObjectDTO dto, String userId) throws Exception {
		return dbmsDetailService.tableDetailsColumns(dto, userId);
	}

	// TABLE 디테일 INDEXES TOP 조회
	@RequestMapping("/tableDetailsIndexesTop")
	public List<Map<String, Object>> tableDetailsIndexesTop(DbObjectDTO dto, String userId) throws Exception {
		return dbmsDetailService.tableDetailsIndexesTop(dto, userId);
	}

	// TABLE 디테일 INDEXES BOTTOM 조회
	@RequestMapping("/tableDetailsIndexesBottom")
	public Map<String, Object> tableDetailsIndexesBottom(DbObjectDTO dto, String userId) throws Exception {
		return dbmsDetailService.tableDetailsIndexesBottom(dto, userId);
	}

	// TABLE 디테일 CONSTRAINTS 조회
	@RequestMapping("/tableDetailsConstraints")
	public List<Map<String, Object>> tableDetailsConstraints(DbObjectDTO dto, String userId) throws Exception {
		return dbmsDetailService.tableDetailsConstraints(dto, userId);
	}

	// TABLE 디테일 SCRIPT 조회
	@RequestMapping("/tableDetailsScript")
	public String tableDetailsScript(DbObjectDTO dto, String userId) throws Exception {
		return dbmsDetailService.tableDetailsScript(dto, userId);
	}

}
