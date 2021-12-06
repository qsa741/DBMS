package com.jySystem.common.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

@RequestMapping("/session")
@RestController
public class SessionController {

	@RequestMapping("/sessionSave")
	public void sessionSave(String id, String session) {
		RequestContextHolder.getRequestAttributes().setAttribute("JYSESSION", id,
				RequestAttributes.SCOPE_SESSION);
	}
	
}
