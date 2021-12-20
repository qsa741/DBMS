package com.jySystem.dbms.service;

import java.util.List;
import java.util.Map;

import com.jySystem.dbms.dto.DbObjectDTO;
import com.jySystem.exception.JYException;

public interface DbmsDetailService {
	
	// 스키마 디테일 정보 검색
	public Map<String, Object> getSchemaDetailsInfo(DbObjectDTO dto, String userId) throws JYException;

	// 스키마 디테일 정보 검색
	public List<Map<String, Object>> getSchemaDetailsRoleGrants(DbObjectDTO dto, String userId) throws JYException;

	// 스키마 디테일 정보 검색
	public List<Map<String, Object>> getSchemaDetailsSystemPrivileges(DbObjectDTO dto, String userId) throws JYException;

	// 스키마 디테일 정보 검색
	public List<Map<String, Object>> getSchemaDetailsExtents(DbObjectDTO dto, String userId) throws JYException;

	// 테이블 디테일 정보 검색
	public Map<String, Object> getTableDetailsTable(DbObjectDTO dto, String userId) throws JYException;

	// 테이블 디테일 정보 검색
	public List<Map<String, Object>> getTableDetailsColumns(DbObjectDTO dto, String userId) throws JYException;

	// 테이블 디테일 정보 검색
	public List<Map<String, Object>> getTableDetailsIndexesTop(DbObjectDTO dto, String userId) throws JYException;

	// 테이블 디테일 정보 검색
	public Map<String, Object> getTableDetailsIndexesBottom(DbObjectDTO dto, String userId) throws JYException;

	// 테이블 디테일 정보 검색
	public List<Map<String, Object>> getTableDetailsConstraints(DbObjectDTO dto, String userId) throws JYException;

	// 테이블 디테일 정보 검색
	public String getTableDetailsScript(DbObjectDTO dto, String userId) throws JYException;

	// 인덱스 디테일 정보 검색
	public List<Map<String, Object>> getIndexDetailsColumns(DbObjectDTO dto, String userId) throws JYException;

	// 스키마 디테일 정보 검색
	public Map<String, Object> getSequenceDetailsInfo(DbObjectDTO dto, String userId) throws JYException;

	// 뷰 디테일 정보 검색
	public List<Map<String, Object>> getViewDetailsColumns(DbObjectDTO dto, String userId) throws JYException;

	// 뷰 디테일 정보 검색
	public List<Map<String, Object>> getViewDetailsScript(DbObjectDTO dto, String userId) throws JYException;

	// 펑션 디테일 정보 검색
	public List<Map<String, Object>> getDetailsCode(DbObjectDTO dto, String userId) throws JYException;

}
