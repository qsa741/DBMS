package com.jySystem.common.scheduler;

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
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class SchedulerSQL {

	// Tibero driver, url, username, password
	@Value("${spring.datasource.driver-class-name}")
	private String driver;

	@Value("${spring.datasource.url}")
	private String url;

	@Value("${spring.datasource.username}")
	private String username;

	@Value("${spring.datasource.password}")
	private String password;

	// Action 데이터 저장
	@SuppressWarnings("resource")
	public void saveActionData(String action, JSONObject json) throws ClassNotFoundException, SQLException, JSONException {
		String select = "select * from ACTIONDATA where year = ? and month = ? and day = ? and action = ?";
		String insert = "insert into ACTIONDATA values(?,?,?,?,?, sysdate)";
		String update = "update ACTIONDATA set count = ? where year = ? and month = ? and day = ? and action = ?";
		Connection conn = null;
		PreparedStatement pre = null;
		ResultSet rs = null;

		Class.forName(driver);
		conn = DriverManager.getConnection(url, username, password);

		// 해당 일에 데이터가 있는지 확인
		pre = conn.prepareStatement(select);
		pre.setString(1, json.getString("year"));
		pre.setString(2, json.getString("month"));
		pre.setString(3, json.getString("day"));
		pre.setString(4, action);

		rs = pre.executeQuery();

		// 데이터가 있으면 Update, 없으면 Insert
		if (rs.next()) {
			pre = conn.prepareStatement(update);
			pre.setInt(1, json.getInt("count"));
			pre.setString(2, json.getString("year"));
			pre.setString(3, json.getString("month"));
			pre.setString(4, json.getString("day"));
			pre.setString(5, action);
		} else {
			pre = conn.prepareStatement(insert);
			pre.setString(1, json.getString("year"));
			pre.setString(2, json.getString("month"));
			pre.setString(3, json.getString("day"));
			pre.setString(4, action);
			pre.setInt(5, json.getInt("count"));
		}

		pre.executeUpdate();

		rs.close();
		pre.close();
		conn.close();
	}

	// ActionScheduler 테이블에 읽지 않은 데이터 처리 후 읽음으로 표시
	public List<Map<String, Object>> actionScheduler() throws ClassNotFoundException, SQLException {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		// 스케줄러가 읽지 않은 데이터 찾기
		String sql = "select * from actionScheduler where readCheck = 'N'";
		// 스케줄러가 읽은 데이터 readCheck = 'Y'로 업데이트
		String sql2 = "update actionScheduler set executeTime = sysdate, readCheck = 'Y' where scheduleNum = ?";

		Connection conn = null;
		PreparedStatement pre = null;
		ResultSet result = null;

		Class.forName(driver);
		conn = DriverManager.getConnection(url, username, password);
		pre = conn.prepareStatement(sql);
		result = pre.executeQuery();

		ResultSetMetaData metaData = result.getMetaData();

		Map<String, Object> map;
		String col;

		while (result.next()) {
			map = new LinkedHashMap<>();
			for (int i = 0; i < 4; i++) {
				col = metaData.getColumnName(i + 1);
				map.put(col, result.getString(col));
			}
			list.add(map);
			pre = conn.prepareStatement(sql2);
			pre.setString(1, (String) map.get("SCHEDULENUM"));
			pre.executeUpdate();
		}
		
		result.close();
		pre.close();
		conn.close();

		return list;
	}

}
