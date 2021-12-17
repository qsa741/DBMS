package com.jySystem.exception.controller;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import com.jySystem.exception.JYException;

@ControllerAdvice
public class JYExceptionController {

	@Value("${sso.url}")
	private String url;

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	// 에러 발생시 에러 메세지와 함께 에러페이지 보여주기
	@ExceptionHandler(JYException.class)
	public ModelAndView handleError(JYException e) {
		ModelAndView mView = new ModelAndView();

		mView.addObject("exception", e);
		mView.setViewName("redirect:" + url + "/error");

		logger.error(getPrintStackTrace(e));

		return mView;
	}

	public String getPrintStackTrace(Throwable e) {

		StringWriter errors = new StringWriter();
		e.printStackTrace(new PrintWriter(errors));

		return errors.toString();
	}
}
