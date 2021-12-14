package com.jySystem.exception.controller;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class WebErrorController implements ErrorController {

	@Value("${sso.url}")
	private String url;
	
	@Override
	public String getErrorPath() {
		return null;
	}

	// 에러 발생시 에러 코드와 함께 에러페이지 보여주기
	@RequestMapping("/error")
	public String handleError() {
		return "redirect:" + url + "/error";
	}
}
