package com.project.dbms.service;

import org.springframework.boot.configurationprocessor.json.JSONObject;

public interface NetworkService {

	public void saveUser(JSONObject data) throws Exception;
	
}
