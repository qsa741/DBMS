package com.jySystem.dbms.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jySystem.dbms.service.DbmsServiceImpl;

@RestController
@RequestMapping("/dbmsTool")
public class PackageDetailsRestController {

	@Autowired
	private DbmsServiceImpl dbmsService;

	// PACKAGE 디테일 CODE 조회
	@RequestMapping("/packageDetailsCode")
	public List<Map<String, Object>> packageDetailsCode(String schema, String packageName, String userId)
			throws Exception {
		return dbmsService.detailsCode(schema, packageName, "PACKAGE", userId);
	}

}
