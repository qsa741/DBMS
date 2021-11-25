package com.project.dbms.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.project.dbms.dto.LoadObjectDTO;

@RequestMapping("/dbms")
@Controller
public class DbmsController {
	
	// SSO URL
	@Value("${sso.url}")
	private String url;
	
	@RequestMapping("/main")
	public String main() {
		return "main";
	}
	
	// DBMS 페이지
	@RequestMapping("/dbms")
	public String dbms() {
		return "dbms";
	}
	
	// jsp include 용 페이지
	@RequestMapping("/schemaDetails")
	public String schemaDetails() {
		return "schemaDetails";
	}
	
	// 추가 table tab 페이지 
	@RequestMapping("/loadTable")
	public String loadTable(LoadObjectDTO dto, Model model) {
		model.addAttribute("dto", dto);

		return "table";
	}

	// SSO의 signOut으로 이동
	@RequestMapping("/signOut")
	public String signOut(HttpServletRequest request, HttpServletResponse response) {
		return "redirect:" + url + "/users/signOut";
	}
	
	// SSO의 modifyUser으로 이동
	@RequestMapping("/modifyUser")
	public String modifyUser() {
		return "redirect:" + url + "/users/modifyUser";
	}
	
	// SSO의 deleteUser으로 이동
	@RequestMapping("/deleteUser")
	public String deleteUser() {
		return "redirect:" + url + "/users/deleteUser";
	}
	
	// SSO의 signIn으로 이동
	@RequestMapping("/signIn")
	public String signIn() {
		return "redirect:" + url + "/users/signIn";
	}
	
	// SSO의 signUp으로 이동
	@RequestMapping("/signUp")
	public String signUp() {
		return "redirect:" + url + "/users/signUp";
	}
	
}
