package com.jySystem.dbms.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;

import com.jySystem.dbms.dto.DbDTO;
import com.jySystem.dbms.dto.DbObjectDTO;
import com.jySystem.dbms.dto.TreeDTO;
import com.jySystem.exception.JYException;

@Service
public class DbmsSQL {

	// Tibero driver, url, username, password
	@Value("${spring.datasource.driver-class-name}")
	private String driver;

	@Value("${spring.datasource.url}")
	private String url;

	@Value("${spring.datasource.username}")
	private String username;

	@Value("${spring.datasource.password}")
	private String password;

	// DB 커넥션 테스트
	@SuppressWarnings({ "finally", "unused" })
	public boolean connectionTest(DbDTO dto) throws JYException {
		boolean result = false;
		Connection conn = null;
		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, dto.getDbId(), dto.getDbPw());
			result = true;
		} catch (ClassNotFoundException cnfe) {
			throw new JYException("Class Not Found Exception", cnfe);
		} catch (SQLException se) {
			result = false;
			if (conn != null)
				conn.close();
			throw new JYException("SQL Exception", se);
		} finally {
			return result;
		}
	}

	// 전체 스키마 리스트 조회
	public List<TreeDTO> allSchemas(DbDTO db) throws JYException {
		// ID는 SCHEMA로 고정
		String sql = "SELECT USERNAME AS TEXT FROM ALL_USERS ORDER BY USERNAME";

		List<TreeDTO> list = new ArrayList<TreeDTO>();

		Connection conn = null;
		PreparedStatement pre = null;
		ResultSet result = null;

		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, db.getDbId(), db.getDbPw());
			pre = conn.prepareStatement(sql);
			result = pre.executeQuery();
			TreeDTO tree;

			while (result.next()) {
				tree = new TreeDTO();
				tree.setId("SCHEMA");
				tree.setText(result.getString(1));
				tree.setIconCls("tree-schema");
				tree.setState("closed");
				tree.setChildren(null);
				list.add(tree);
			}

			result.close();
			pre.close();
			conn.close();

		} catch (ClassNotFoundException cnfe) {
			throw new JYException("Class Not Found Exception", cnfe);
		} catch (SQLException se) {
			throw new JYException("SQL Exception", se);
		}

		return list;
	}

	// 스키마 항목 카운트 조회
	public TreeDTO getSchemaInfo(String owner, String objectType, DbDTO db) throws JYException {
		String sql = "SELECT COUNT(*) FROM all_objects WHERE OWNER = ? AND OBJECT_TYPE = ?";

		TreeDTO tree = new TreeDTO();
		tree.setIconCls("tree-schemaInfo");

		try {
			Connection conn = null;
			PreparedStatement pre = null;
			ResultSet result = null;

			if (Objects.equals(objectType,"PVM")) {
				tree.setId("PVM");
				tree.setText("Pvm (4)");
				tree.setState("closed");

				return tree;
			}

			Class.forName(driver);
			conn = DriverManager.getConnection(url, db.getDbId(), db.getDbPw());
			pre = conn.prepareStatement(sql);
			pre.setString(1, owner);
			pre.setString(2, objectType);
			result = pre.executeQuery();

			while (result.next()) {
				tree.setId(objectType + "Folder");
				// 첫글자 대문자로 바꾸기
				String substr1 = objectType.substring(0, 1);
				String substr2 = objectType.substring(1);
				tree.setText(substr1 + substr2.toLowerCase() + " (" + result.getInt(1) + ")");
				if (result.getInt(1) != 0) {
					tree.setState("closed");
					tree.setChildren(null);
				}
			}

			result.close();
			pre.close();
			conn.close();
		} catch (ClassNotFoundException cnfe) {
			throw new JYException("Class Not Found Exception", cnfe);
		} catch (SQLException se) {
			throw new JYException("SQL Exception", se);
		}

		return tree;
	}

	// 오브젝트 항목 조회
	public List<TreeDTO> getObjectInfo(DbObjectDTO dto, DbDTO db) throws JYException {
		String sql = "SELECT OBJECT_TYPE, OBJECT_NAME FROM all_objects WHERE OWNER = ? AND OBJECT_TYPE = ?";
		String iconCls = "tree-" + dto.getObjectType().toLowerCase();

		List<TreeDTO> list = new ArrayList<TreeDTO>();
		TreeDTO tree = null;

		Connection conn = null;
		PreparedStatement pre = null;
		ResultSet result = null;

		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, db.getDbId(), db.getDbPw());
			pre = conn.prepareStatement(sql);
			pre.setString(1, dto.getSchemaName());
			pre.setString(2, dto.getObjectType());
			result = pre.executeQuery();

			while (result.next()) {

				tree = new TreeDTO();
				tree.setId(result.getString(1));
				tree.setText(result.getString(2));
				tree.setIconCls(iconCls);
				if (result.getString(1).equals("TABLE")) {
					tree.setState("closed");
				}
				list.add(tree);
			}

			result.close();
			pre.close();
			conn.close();
		} catch (ClassNotFoundException cnfe) {
			throw new JYException("Class Not Found Exception", cnfe);
		} catch (SQLException se) {
			throw new JYException("SQL Exception", se);
		}

		return list;
	}

	// Table 불러오기
	public List<Map<String, Object>> loadObjectTable(DbObjectDTO dto, DbDTO db) throws JYException {
		String sql = "SELECT * FROM " + dto.getSchemaName() + "." + dto.getTableName();
		List<Map<String, Object>> list = new ArrayList<>();

		Connection conn = null;
		PreparedStatement pre = null;
		ResultSet result = null;

		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, db.getDbId(), db.getDbPw());
			pre = conn.prepareStatement(sql);
			result = pre.executeQuery();

			ResultSetMetaData metaData = result.getMetaData();
			int size = metaData.getColumnCount();
			Map<String, Object> map;
			String col;

			while (result.next()) {
				map = new HashMap<>();
				for (int i = 0; i < size; i++) {
					col = metaData.getColumnName(i + 1);
					map.put(col, result.getString(col));
				}
				list.add(map);
			}

			result.close();
			pre.close();
			conn.close();
		} catch (ClassNotFoundException cnfe) {
			throw new JYException("Class Not Found Exception", cnfe);
		} catch (SQLException se) {
			throw new JYException("SQL Exception", se);
		}

		return list;
	}

	// Table 하위 목록 조회 (column, constraint, index)
	public List<Map<String, Object>> selectTableChild(String type, DbObjectDTO dto, DbDTO db) throws JYException {
		String sql = "";
		if (Objects.equals(type, "column")) {
			sql = "SELECT OWNER, TABLE_NAME, COLUMN_NAME, DATA_TYPE, DATA_LENGTH "
					+ "FROM all_tab_columns WHERE OWNER = ? AND TABLE_NAME = ?";
		} else if (Objects.equals(type, "index")) {
			sql = "SELECT INDEX_NAME FROM (SELECT * FROM all_ind_columns WHERE INDEX_OWNER = ? AND TABLE_NAME = ?) GROUP BY INDEX_NAME";
		} else {
			sql = "SELECT CONSTRAINT_NAME FROM (SELECT * FROM all_cons_columns WHERE OWNER = ? AND TABLE_NAME = ?) GROUP BY CONSTRAINT_NAME";
		}

		List<Map<String, Object>> list = new ArrayList<>();

		Connection conn = null;
		PreparedStatement pre = null;
		ResultSet result = null;

		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, db.getDbId(), db.getDbPw());
			pre = conn.prepareStatement(sql);
			pre.setString(1, dto.getSchemaName());
			pre.setString(2, dto.getTableName());
			result = pre.executeQuery();

			ResultSetMetaData metaData = result.getMetaData();
			int size = metaData.getColumnCount();
			Map<String, Object> map;
			String col;

			while (result.next()) {
				map = new HashMap<>();
				for (int i = 0; i < size; i++) {
					col = metaData.getColumnName(i + 1);
					map.put(col, result.getString(col));
				}
				list.add(map);
			}

			result.close();
			pre.close();
			conn.close();
		} catch (ClassNotFoundException cnfe) {
			throw new JYException("Class Not Found Exception", cnfe);
		} catch (SQLException se) {
			throw new JYException("SQL Exception", se);
		}

		return list;
	}

	// SQL 한줄 실행
	@SuppressWarnings("finally")
	public Map<String, Object> runCurrentSQL(String sql, String type, int index, DbDTO db) throws JYException {

		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		Map<String, Object> row;

		// div id로 설정하기 위해 특수문자 지우기
		String key = sql.replace("\'", "").replace(" ", "").replace("=", "").replace("<", "").replace(">", "")
				.replace(",", "").replace("*", "");

		map.put("type", type);
		map.put("sql", sql);
		map.put("key", key);

		Connection conn = null;
		PreparedStatement pre = null;
		ResultSet result = null;

		int size = 0;
		long startTime = System.nanoTime();
		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, db.getDbId(), db.getDbPw());
			pre = conn.prepareStatement(sql);

			// SELECT 일 경우
			if (Objects.equals(type, "SELECT")) {
				result = pre.executeQuery();
				ResultSetMetaData metaData = result.getMetaData();
				size = metaData.getColumnCount();
				String col;
				String[] cols = new String[size];

				for (int i = 0; i < size; i++) {
					col = metaData.getColumnName(i + 1);
					cols[i] = col;
				}
				while (result.next()) {
					row = new LinkedHashMap<>();
					for (int i = 0; i < size; i++) {
						row.put(cols[i], result.getString(cols[i]));
					}
					data.add(row);
				}

				map.put("cols", cols);

				// 그 외 명령어일 경우
			} else {
				int count = pre.executeUpdate();
				long stopTime = System.nanoTime();
				double time = (double) (stopTime - startTime) / 1000000;
				row = new LinkedHashMap<>();

				row.put("Row", index);
				row.put("ExecutionTime", time);

				// 결과에 따라 row 등록 DDL DML DCL
				if (Objects.equals(type, "CREATE") || Objects.equals(type, "DROP") || Objects.equals(type,"ALTER") || Objects.equals(type,"TRUNCATE")) {
					row.put("DbmsOutput", type.toLowerCase() + " " + sql.split(" ")[1].toLowerCase() + ".");
				} else if (Objects.equals(type, "INSERT") || Objects.equals(type, "UPDATE") || Objects.equals(type,"DELETE")) {
					row.put("DbmsOutput", count + " rows " + type.toLowerCase() + ".");
				} else if (Objects.equals(type,"GRANT") || Objects.equals(type,"REVOKE")) {
					row.put("DbmsOutput", "commends complated successfully.");
				}
				data.add(row);
			}

			result.close();
			pre.close();
			conn.close();

			// 에러 발생시 에러메세지 추가
		} catch (SQLException e) {
			long stopTime = System.nanoTime();
			double time = (double) (stopTime - startTime) / 1000000;
			row = new LinkedHashMap<>();
			row.put("Row", index);
			row.put("DbmsOutput", e.getMessage());
			row.put("ExecutionTime", time);

			data.add(row);
			throw new JYException("SQL Exception", e);

			// 에러 메세지를 보내기위해 finally에서 return
		} catch (ClassNotFoundException cnfe) {
			throw new JYException("Class Not Found Exception", cnfe);
		} finally {
			map.put("size", size);
			map.put("data", data);

			return map;
		}
	}

	// 카프카로 받은 데이터 스케줄러 테이블에 저장
	public void userSchedulerSave(String data) throws JYException {
		String sql = "insert into userScheduler values(USERS_SEQ.NEXTVAL, \'" + data + "\', sysdate, 'N')";

		Connection conn = null;
		PreparedStatement pre = null;

		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, username, password);
			pre = conn.prepareStatement(sql);
			pre.executeUpdate();
			pre.close();
			conn.close();
		} catch (ClassNotFoundException cnfe) {
			throw new JYException("Class Not Found Exception", cnfe);
		} catch (SQLException se) {
			throw new JYException("SQL Exception", se);
		}

	}

	// ActionData에 존재하는 연도 가져오기
	public List<String> getChartYears() throws JYException {
		List<String> list = new ArrayList<String>();

		String sql = "select year from ACTIONDATA group by year order by year desc";

		Connection conn = null;
		PreparedStatement pre = null;
		ResultSet result = null;

		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, username, password);
			pre = conn.prepareStatement(sql);
			result = pre.executeQuery();

			while (result.next()) {
				list.add(result.getString(1));
			}

			result.close();
			pre.close();
			conn.close();
		} catch (ClassNotFoundException cnfe) {
			throw new JYException("Class Not Found Exception", cnfe);
		} catch (SQLException se) {
			throw new JYException("SQL Exception", se);
		}

		return list;
	}

	// 연도에 해당하는 ActionData에 존재하는 월 가져오기
	public List<String> getChartMonth(String year) throws JYException {
		List<String> list = new ArrayList<String>();

		String sql = "select month from (select * from ACTIONDATA where year = ?) group by month order by month desc";

		Connection conn = null;
		PreparedStatement pre = null;
		ResultSet result = null;

		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, username, password);
			pre = conn.prepareStatement(sql);
			pre.setString(1, year);
			result = pre.executeQuery();

			while (result.next()) {
				list.add(result.getString(1));
			}

			result.close();
			pre.close();
			conn.close();
		} catch (ClassNotFoundException cnfe) {
			throw new JYException("Class Not Found Exception", cnfe);
		} catch (SQLException se) {
			throw new JYException("SQL Exception", se);
		}

		return list;
	}

	// year와 action에 해당하는 ACTIONDATA 데이터 Json 리스트로 반환
	public List<JSONObject> getActionData(String year, String action) throws JYException {
		List<JSONObject> list = new ArrayList<JSONObject>();

		String sql = "select * from ACTIONDATA where action = ? and year = ? order by 1 desc, 2, 3, 4";

		Connection conn = null;
		PreparedStatement pre = null;
		ResultSet result = null;

		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, username, password);
			pre = conn.prepareStatement(sql);
			pre.setString(1, action);
			pre.setString(2, year);
			result = pre.executeQuery();

			ResultSetMetaData metaData = result.getMetaData();
			int size = metaData.getColumnCount();
			String col;

			JSONObject json;
			while (result.next()) {
				json = new JSONObject();
				for (int i = 0; i < size; i++) {
					col = metaData.getColumnName(i + 1);
					json.put(col, result.getString(col));
				}
				list.add(json);
			}

			result.close();
			pre.close();
			conn.close();
		} catch (ClassNotFoundException cnfe) {
			throw new JYException("Class Not Found Exception", cnfe);
		} catch (SQLException se) {
			throw new JYException("SQL Exception", se);
		} catch (JSONException je) {
			throw new JYException("JSON Exception", je);
		}

		return list;
	}

	// year, month, action에 해당하는 데이터 Json 리스트로 반환
	public List<JSONObject> getActionData(String year, String month, String action) throws JYException {
		List<JSONObject> list = new ArrayList<JSONObject>();

		String sql = "select * from ACTIONDATA where action = ? and year = ? and month = ? order by 1 desc, 2, 3, 4";

		Connection conn = null;
		PreparedStatement pre = null;
		ResultSet result = null;

		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, username, password);
			pre = conn.prepareStatement(sql);
			pre.setString(1, action);
			pre.setString(2, year);
			pre.setString(3, month);
			result = pre.executeQuery();

			ResultSetMetaData metaData = result.getMetaData();
			int size = metaData.getColumnCount();
			String col;

			JSONObject json;
			while (result.next()) {
				json = new JSONObject();
				for (int i = 0; i < size; i++) {
					col = metaData.getColumnName(i + 1);
					json.put(col, result.getString(col));
				}
				list.add(json);
			}

			result.close();
			pre.close();
			conn.close();
		} catch (ClassNotFoundException cnfe) {
			throw new JYException("Class Not Found Exception", cnfe);
		} catch (SQLException se) {
			throw new JYException("SQL Exception", se);
		} catch (JSONException je) {
			throw new JYException("JSON Exception", je);
		}

		return list;
	}

}
