package com.jySystem.common.service;

public interface SessionService {
	
	// 세션에 저장된 아이디 가져오기
	public String getSessionId();
	
	// 세션에 저장된 DB 아이디 가져오기
	public String getSessionDbId();
	
	// 세션에 저장된 DB 비밀번호 가져오기
	public String getSessionDbPw();
	
}
