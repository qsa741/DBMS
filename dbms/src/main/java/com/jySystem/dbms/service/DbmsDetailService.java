package com.jySystem.dbms.service;

import java.util.List;
import java.util.Map;

import com.jySystem.dbms.dto.DbObjectDTO;
import com.jySystem.exception.JYException;

public interface DbmsDetailService {
	
	// 스키마 디테일 정보 검색
	public Map<String, Object> schemaDetailsInfo(DbObjectDTO dto, String userId) throws JYException;

	// 스키마 디테일 정보 검색
	public List<Map<String, Object>> schemaDetailsRoleGrants(DbObjectDTO dto, String userId) throws JYException;

	// 스키마 디테일 정보 검색
	public List<Map<String, Object>> schemaDetailsSystemPrivileges(DbObjectDTO dto, String userId) throws JYException;

	// 스키마 디테일 정보 검색
	public List<Map<String, Object>> schemaDetailsExtents(DbObjectDTO dto, String userId) throws JYException;

	// 테이블 디테일 정보 검색
	public Map<String, Object> tableDetailsTable(DbObjectDTO dto, String userId) throws JYException;

	// 테이블 디테일 정보 검색
	public List<Map<String, Object>> tableDetailsColumns(DbObjectDTO dto, String userId) throws JYException;

	// 테이블 디테일 정보 검색
	public List<Map<String, Object>> tableDetailsIndexesTop(DbObjectDTO dto, String userId) throws JYException;

	// 테이블 디테일 정보 검색
	public Map<String, Object> tableDetailsIndexesBottom(DbObjectDTO dto, String userId) throws JYException;

	// 테이블 디테일 정보 검색
	public List<Map<String, Object>> tableDetailsConstraints(DbObjectDTO dto, String userId) throws JYException;

	// 테이블 디테일 정보 검색
	public String tableDetailsScript(DbObjectDTO dto, String userId) throws JYException;

	// 인덱스 디테일 정보 검색
	public List<Map<String, Object>> indexDetailsColumns(DbObjectDTO dto, String userId) throws JYException;

	// 스키마 디테일 정보 검색
	public Map<String, Object> sequenceDetailsInfo(DbObjectDTO dto, String userId) throws JYException;

	// 뷰 디테일 정보 검색
	public List<Map<String, Object>> viewDetailsColumns(DbObjectDTO dto, String userId) throws JYException;

	// 뷰 디테일 정보 검색
	public List<Map<String, Object>> viewDetailsScript(DbObjectDTO dto, String userId) throws JYException;

	// 펑션 디테일 정보 검색
	public List<Map<String, Object>> detailsCode(DbObjectDTO dto, String userId) throws JYException;

}
