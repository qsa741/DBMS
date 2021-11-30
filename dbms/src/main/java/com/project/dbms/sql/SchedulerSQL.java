package com.project.dbms.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;


@Service
public class SchedulerSQL {

	// Tibero driver
	@Value("${spring.datasource.driver-class-name}")
	private String driver;
	
	// 125 Tibero 서버
	@Value("${spring.datasource.url}")
	private String url;
	
	@Value("${spring.datasource.username}")
	private String username;
	
	@Value("${spring.datasource.password}")
	private String password;
	
	// Action 데이터 저장
	public void saveActionData(String action, JSONObject json) {
		String sql = "insert into ACTIONDATA values(?,?,?,?,?)";

		Connection conn = null;
		PreparedStatement pre = null;
		
		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, username, password);
			pre = conn.prepareStatement(sql);
			pre.setString(1, json.getString("year"));
			pre.setString(2, json.getString("month"));
			pre.setString(3, json.getString("day"));
			pre.setString(4, action);
			pre.setInt(5, json.getInt("count"));
			
			pre.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (pre != null) try {pre.close();} catch (SQLException se) {}
			if (conn != null)try {conn.close();} catch (SQLException se) {}
		}
	}

	
	// ActionScheduler 테이블에 읽지 않은 데이터 처리 후 읽음으로 표시
	public List<Map<String, Object>> actionScheduler() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		// 스케줄러가 읽지 않은 데이터 찾기
		String sql = "select * from actionScheduler where readCheck = 'N'";
		// 스케줄러가 읽은 데이터 readCheck = 'Y'로 업데이트
		String sql2 = "update actionScheduler set executeTime = sysdate, readCheck = 'Y' where scheduleNum = ?";
	
		Connection conn = null;
		PreparedStatement pre = null;
		ResultSet result = null;
		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, username, password);
			pre = conn.prepareStatement(sql);
			result = pre.executeQuery();
		
			ResultSetMetaData metaData = result.getMetaData();
			
			Map<String, Object> map;
			String col;
			while(result.next()) {
				map = new LinkedHashMap<>();
				for (int i = 0; i < 4; i++) {
					col = metaData.getColumnName(i + 1);
					map.put(col, result.getString(col));
				}
				list.add(map);
				pre = conn.prepareStatement(sql2);
				pre.setString(1, (String)map.get("SCHEDULENUM"));
				pre.executeUpdate();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(result != null) try {result.close();} catch(SQLException se) {}
			if(pre != null) try {pre.close();} catch(SQLException se) {}
			if(conn != null) try {conn.close();} catch(SQLException se) {}
		}
		
		return list;
	}
	
}
