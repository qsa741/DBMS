package com.jySystem.common.intercept;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

@SuppressWarnings("deprecation")
public class DbmsInterceptor extends HandlerInterceptorAdapter {

	private String url;
	
	public DbmsInterceptor(String url) {
		this.url = url;
	}
	
	// 일부 페이지 비로그인으로 접근시 로그인 페이지로 이동
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		final HttpSession session = request.getSession();
		String path = request.getRequestURI();

		if (path.contains("/resources/*")) {
			return true;
		}

		if (path.contains("/dbmsTool/*")) {
			if (session.getAttribute("JYSESSION") == null) {
				if(path.contains("/dbmsTool/connectionTest")) {
					return true;
				}
				response.sendRedirect(url + "/users/signIn");
				return false;
			}
		}

		return true;
	}
}
