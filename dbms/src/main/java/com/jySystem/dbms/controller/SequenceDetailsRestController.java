package com.jySystem.dbms.controller;

import java.sql.SQLException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jySystem.dbms.service.DbmsServiceImpl;

@RestController
@RequestMapping("/dbmsTool")
public class SequenceDetailsRestController {

	@Autowired
	private DbmsServiceImpl dbmsService;

	// SEQUENCE 디테일 INFO 조회
	@RequestMapping("/sequenceDetailsInfo")
	public Map<String, Object> sequenceDetailsInfo(String sequenceName, String userId)
			throws ClassNotFoundException, SQLException {
		return dbmsService.sequenceDetailsInfo(sequenceName, userId);
	}

}
