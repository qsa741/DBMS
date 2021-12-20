package com.jySystem.dbms.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jySystem.dbms.dto.DbObjectDTO;
import com.jySystem.dbms.service.DbmsDetailServiceImpl;

@RestController
@RequestMapping("/dbmsTool")
public class TriggerDetailsRestController {

	@Autowired
	private DbmsDetailServiceImpl dbmsDetailService;

	// TRIGGER 디테일 SOURCE 조회
	@RequestMapping("/triggerDetailsSource")
	public List<Map<String, Object>> triggerDetailsSource(DbObjectDTO dto, String userId) throws Exception {
		return dbmsDetailService.getDetailsCode(dto, userId);
	}
}
