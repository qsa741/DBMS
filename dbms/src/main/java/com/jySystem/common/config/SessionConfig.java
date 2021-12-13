package com.jySystem.common.config;

import java.util.Map;
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

	public DbDTO getSessionID(String id) {
		DbDTO dto = new DbDTO();
		for (String key : sessions.keySet()) {
			HttpSession s = sessions.get(key);
			if (s != null && s.getAttribute("JYSESSION") != null && s.getAttribute("JYSESSION").equals(id)) {
				dto.setDbId((String) s.getAttribute("JYDBID"));
				dto.setDbPw((String) s.getAttribute("JYDBPW"));
				break;
			}
		}
		return dto;
	}

}
