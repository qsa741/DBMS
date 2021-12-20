package com.jySystem.dbms.service;

import java.util.List;
import java.util.Map;

import com.jySystem.dbms.dto.DbDTO;
import com.jySystem.dbms.dto.DbObjectDTO;
import com.jySystem.dbms.dto.TreeDTO;
import com.jySystem.exception.JYException;

public interface DbmsService {

	// DB 커넥션 테스트
	public boolean connectionTest(DbDTO dto) throws JYException;

	// 모든 스키마 가져오기
	public List<TreeDTO> getAllSchemas(String userId) throws JYException;

	// 스키마 정보 가져오기
	public List<TreeDTO> schemaInfo(DbObjectDTO dto, String userId) throws JYException;

	// 오브젝트 정보 가져오기
	public List<TreeDTO> objectInfo(DbObjectDTO object, String userId) throws JYException;

	// 테이블 불러오기
	public Map<String, Object> loadObject(DbObjectDTO dto, String userId) throws JYException;

	// 테이블 자식 정보 불러오기
	public Map<String, Object> getTableChildren(DbObjectDTO dto, String userId) throws JYException;

	// SQL 한줄 실행
	public Map<String, Object> runCurrentSQL(String sql, int cursor, String userId) throws JYException;

	// SQL 전체 실행
	public List<Map<String, Object>> runAllSQL(String sql, String userId) throws JYException;


}
