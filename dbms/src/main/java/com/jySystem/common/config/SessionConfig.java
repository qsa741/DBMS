package com.jySystem.common.config;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import com.jySystem.dbms.dto.DbDTO;

@WebListener
public class SessionConfig implements HttpSessionListener {

	private static final Map<String, HttpSession> sessions = new ConcurrentHashMap<>();

	// 세션 로그인 시 sessions에 풋
	@Override
	public void sessionCreated(HttpSessionEvent session) {
		sessions.put(session.getSession().getId(), session.getSession());
	}

	// 세션값 지우기
	@Override
	public void sessionDestroyed(HttpSessionEvent session) {
		if (sessions.get(session.getSession().getId()) != null) {
			sessions.remove(session.getSession().getId());
		}
	}

	// 사용자 아이디로 DB ID/PW 찾기
	public DbDTO getSessionID(String sessionID, String sessionDBID, String sessionDBPW, String id) {
		DbDTO dto = new DbDTO();
		for (String key : sessions.keySet()) {
			HttpSession s = sessions.get(key);
			if (s != null && s.getAttribute(sessionID) != null && Objects.equals(s.getAttribute(sessionID),id)) {
				dto.setDbId((String) s.getAttribute(sessionDBID));
				dto.setDbPw((String) s.getAttribute(sessionDBPW));
				break;
			}
		}
		return dto;
	}

}
