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
public class IndexDetailRestController {

	@Autowired
	private DbmsDetailServiceImpl dbmsDetailService;

	// INDEX 디테일 INDEX 조회
	@RequestMapping("/indexDetailsIndex")
	public Map<String, Object> indexDetailsIndex(DbObjectDTO dto, String userId) throws Exception {
		return dbmsDetailService.getTableDetailsIndexesBottom(dto, userId);
	}

	// INDEX 디테일 COLUMNS 조회
	@RequestMapping("/indexDetailsColumns")
	public List<Map<String, Object>> indexDetailsColumns(DbObjectDTO dto, String userId) throws Exception {
		return dbmsDetailService.getIndexDetailsColumns(dto, userId);
	}

}
