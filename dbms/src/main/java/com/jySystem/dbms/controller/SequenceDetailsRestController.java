package com.jySystem.dbms.controller;

import java.sql.SQLException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jySystem.dbms.dto.DbDTO;
import com.jySystem.dbms.service.DbmsServiceImpl;

@RestController
@RequestMapping("/dbmsTool")
public class SequenceDetailsRestController {

	@Autowired
	private DbmsServiceImpl dbmsService;
	
	@RequestMapping("/sequenceDetailsInfo")
	public Map<String, Object> sequenceDetailsInfo(String sequenceName, DbDTO dto) throws ClassNotFoundException, SQLException {
		return dbmsService.sequenceDetailsInfo(sequenceName, dto);
	}
	
}