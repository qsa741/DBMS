package com.jySystem.dbms.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jySystem.dbms.dto.DbObjectDTO;
import com.jySystem.dbms.service.DbmsServiceImpl;

@RestController
@RequestMapping("/dbmsTool")
public class ViewDetailsRestController {

	@Autowired
	private DbmsServiceImpl dbmsService;

	// VIEW 디테일 COLUMNS 조회
	@RequestMapping("/viewDetailsColumns")
	public List<Map<String, Object>> viewDetailsColumns(DbObjectDTO dto, String userId)
			throws Exception {
		return dbmsService.viewDetailsColumns(dto, userId);
	}

	// VIEW 디테일 SCRIPT 조회
	@RequestMapping("/viewDetailsScript")
	public List<Map<String, Object>> viewDetailsScript(DbObjectDTO dto, String userId)
			throws Exception {
		return dbmsService.viewDetailsScript(dto, userId);
	}
}
