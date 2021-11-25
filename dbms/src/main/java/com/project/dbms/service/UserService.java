package com.project.dbms.service;

import com.project.dbms.dto.DbDTO;

public interface UserService {
	
	// DB 커넥션 테스트
	public boolean connectionTest(DbDTO dto) throws Exception;
	
	// 세션에 저장된 아이디 가져오기
	public String getSessionId();
	
	// 세션에 저장된 DB 아이디 가져오기
	public String getSessionDbId();
	
	// 세션에 저장된 DB 비밀번호 가져오기
	public String getSessionDbPw();
	
}
