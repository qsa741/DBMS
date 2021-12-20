package com.jySystem.dbms.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jySystem.dbms.dto.DbObjectDTO;
import com.jySystem.dbms.service.DbmsDetailServiceImpl;

@RestController
@RequestMapping("/dbmsTool")
public class SequenceDetailsRestController {

	@Autowired
	private DbmsDetailServiceImpl dbmsDetailService;

	// SEQUENCE 디테일 INFO 조회
	@RequestMapping("/sequenceDetailsInfo")
	public Map<String, Object> sequenceDetailsInfo(DbObjectDTO dto, String userId) throws Exception {
		return dbmsDetailService.sequenceDetailsInfo(dto, userId);
	}

}
