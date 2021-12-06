package com.jySystem.common.service;

import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

@Service
public class SessionServiceImpl implements SessionService {
	
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
	
	public void test() {
		
		System.out.println(RequestContextHolder.getRequestAttributes());
		
	}
}
