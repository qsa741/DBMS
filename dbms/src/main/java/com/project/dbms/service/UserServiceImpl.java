package com.project.dbms.service;

import java.sql.Connection;
import java.sql.DriverManager;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import com.project.dbms.dto.DbDTO;

@Service
public class UserServiceImpl implements UserService {
	
	// Tibero driver
	@Value("${spring.datasource.driver-class-name}")
	private String driver;
	
	// 125 Tibero 서버
	@Value("${spring.datasource.url}")
	private String url;
	
	
	// DB 커넥션 테스트
	@Override
	public boolean connectionTest(DbDTO dto) throws Exception {
		boolean result = false;
		Connection conn = null;
		Class.forName(driver);
		try {
			conn = DriverManager.getConnection(url, dto.getDbId(), dto.getDbPw());
			result = true;
		} catch (Exception e) {
			result = false;
		} finally {
			conn.close();
		}
		return result;
	}
	
	// 세션값 가져오기
	@Override
	public String getSessionId() {
		return (String) RequestContextHolder.getRequestAttributes().getAttribute("JYSESSION",
				RequestAttributes.SCOPE_SESSION);
	}
	
	// DB ID 세션값 가져오기
	@Override
	public String getSessionDbId() {
		return (String) RequestContextHolder.getRequestAttributes().getAttribute("JYDBID",
				RequestAttributes.SCOPE_SESSION);
	}
	
	// DB PW 세션값 가져오기
	@Override
	public String getSessionDbPw() {
		return (String) RequestContextHolder.getRequestAttributes().getAttribute("JYDBPW",
				RequestAttributes.SCOPE_SESSION);
	}
}
