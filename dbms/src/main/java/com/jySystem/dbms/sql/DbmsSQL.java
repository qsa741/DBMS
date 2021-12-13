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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;

import com.jySystem.dbms.dto.DbDTO;
import com.jySystem.dbms.dto.LoadObjectDTO;
import com.jySystem.dbms.dto.ObjectDTO;
import com.jySystem.dbms.dto.TreeDTO;

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
	@SuppressWarnings("finally")
	public boolean connectionTest(DbDTO dto) throws Exception {
		boolean result = false;
		Connection conn = null;
		Class.forName(driver);

		try {
			conn = DriverManager.getConnection(url, dto.getDbId(), dto.getDbPw());
			result = true;
		} catch (Exception e) {
			result = false;
		} finally {
			if (conn != null)
				conn.close();
			return result;
		}
	}

	// 전체 스키마 리스트 조회
	public List<TreeDTO> allSchemas(DbDTO db) throws ClassNotFoundException, SQLException {
		// ID는 SCHEMA로 고정
		String sql = "SELECT USERNAME AS TEXT FROM ALL_USERS ORDER BY USERNAME";

		List<TreeDTO> list = new ArrayList<TreeDTO>();

		Connection conn = null;
		PreparedStatement pre = null;
		ResultSet result = null;

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

		return list;
	}

	// 스키마 항목 카운트 조회
	public TreeDTO schemaInfo(String owner, String objectType, DbDTO db) throws ClassNotFoundException, SQLException {
		String sql = "SELECT COUNT(*) FROM all_objects WHERE OWNER = ? AND OBJECT_TYPE = ?";

		TreeDTO tree = new TreeDTO();
		tree.setIconCls("tree-schemaInfo");

		Connection conn = null;
		PreparedStatement pre = null;
		ResultSet result = null;

		if (objectType.equals("PVM")) {
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
			tree.setId(objectType + "S");
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

		return tree;
	}

	// 오브젝트 항목 조회
	public List<TreeDTO> objectInfo(ObjectDTO dto, DbDTO db) throws ClassNotFoundException, SQLException {
		String sql = "SELECT OBJECT_TYPE, OBJECT_NAME FROM all_objects WHERE OWNER = ? AND OBJECT_TYPE = ?";
		String iconCls = "tree-" + dto.getObject().toLowerCase();

		List<TreeDTO> list = new ArrayList<TreeDTO>();
		TreeDTO tree = null;

		Connection conn = null;
		PreparedStatement pre = null;
		ResultSet result = null;

		Class.forName(driver);
		conn = DriverManager.getConnection(url, db.getDbId(), db.getDbPw());
		pre = conn.prepareStatement(sql);
		pre.setString(1, dto.getId());
		pre.setString(2, dto.getObject());
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

		return list;
	}

	// Table 불러오기
	public List<Map<String, Object>> loadObjectTable(LoadObjectDTO dto, DbDTO db)
			throws SQLException, ClassNotFoundException {
		String sql = "SELECT * FROM " + dto.getSchema() + "." + dto.getObjectName();
		List<Map<String, Object>> list = new ArrayList<>();

		Connection conn = null;
		PreparedStatement pre = null;
		ResultSet result = null;

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

		return list;
	}

	// Table 하위 목록 조회 (column, constraint, index)
	public List<Map<String, Object>> selectTableChild(String type, LoadObjectDTO dto, DbDTO db)
			throws ClassNotFoundException, SQLException {
		String sql = "";
		if (type.equals("column")) {
			sql = "SELECT OWNER, TABLE_NAME, COLUMN_NAME, DATA_TYPE, DATA_LENGTH "
					+ "FROM all_tab_columns WHERE OWNER = ? AND TABLE_NAME = ?";
		} else if (type.equals("index")) {
			sql = "SELECT INDEX_NAME FROM (SELECT * FROM all_ind_columns WHERE INDEX_OWNER = ? AND TABLE_NAME = ?) GROUP BY INDEX_NAME";
		} else {
			sql = "SELECT CONSTRAINT_NAME FROM (SELECT * FROM all_cons_columns WHERE OWNER = ? AND TABLE_NAME = ?) GROUP BY CONSTRAINT_NAME";
		}

		List<Map<String, Object>> list = new ArrayList<>();

		Connection conn = null;
		PreparedStatement pre = null;
		ResultSet result = null;

		Class.forName(driver);
		conn = DriverManager.getConnection(url, db.getDbId(), db.getDbPw());
		pre = conn.prepareStatement(sql);
		pre.setString(1, dto.getSchema());
		pre.setString(2, dto.getObjectName());
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

		return list;
	}

	// 현재 내 권한 조회
	public String getGrant(DbDTO db) throws ClassNotFoundException, SQLException {
		String sql = "SELECT GRANTED_ROLE FROM user_role_privs";
		String grant = null;

		Connection conn = null;
		PreparedStatement pre = null;
		ResultSet result = null;

		Class.forName(driver);
		conn = DriverManager.getConnection(url, db.getDbId(), db.getDbPw());
		pre = conn.prepareStatement(sql);
		result = pre.executeQuery();

		while (result.next()) {
			grant = result.getString(1);
			// DBA 권한이 있을때 빠져나오기
			if (grant.equals("DBA")) {
				break;
			}
			grant = "CONNECT";
		}

		result.close();
		pre.close();
		conn.close();

		return grant;
	}

	// 스키마 디테일 Info 조회
	public Map<String, Object> schemaDetailsInfo(String schema, DbDTO db) throws ClassNotFoundException, SQLException {
		String sql = "SELECT * FROM all_users WHERE USERNAME = ?";
		if (getGrant(db).equals("DBA")) {
			sql = "SELECT USERNAME, USER_ID, ACCOUNT_STATUS, LOCK_DATE, EXPIRY_DATE, DEFAULT_TABLESPACE, CREATED "
					+ "FROM dba_users WHERE USERNAME = ?";
			// 권한이 DBA가 아닐때 자기 자신 조회시 추가 정보있음
		} else if (schema.equals(db.getDbId().toUpperCase())) {
			sql = "SELECT * FROM USER_USERS WHERE USERNAME = ?";
		}

		Map<String, Object> map = new LinkedHashMap<>();

		Connection conn = null;
		PreparedStatement pre = null;
		ResultSet result = null;

		Class.forName(driver);
		conn = DriverManager.getConnection(url, db.getDbId(), db.getDbPw());
		pre = conn.prepareStatement(sql);
		pre.setString(1, schema);
		result = pre.executeQuery();

		ResultSetMetaData metaData = result.getMetaData();
		int size = metaData.getColumnCount();
		String col;

		while (result.next()) {
			map = new LinkedHashMap<>();
			for (int i = 0; i < size; i++) {
				col = metaData.getColumnName(i + 1);
				// 날짜형식 YYYY-MM-DD 로 고정
				if (col.equals("CREATED")) {
					map.put(col, result.getString(col).substring(0, 10));
				} else {
					map.put(col, result.getString(col));
				}
			}
		}

		result.close();
		pre.close();
		conn.close();

		return map;
	}

	// 스키마 디테일 Role Grants 조회
	public List<Map<String, Object>> schemaDetailsRoleGrants(String schema, DbDTO db)
			throws ClassNotFoundException, SQLException {
		String sql = "SELECT * FROM user_role_privs WHERE GRANTEE = ?";
		if (getGrant(db).equals("DBA")) {
			sql = "SELECT * FROM dba_role_privs WHERE GRANTEE = ?";
		}

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		Connection conn = null;
		PreparedStatement pre = null;
		ResultSet result = null;

		Class.forName(driver);
		conn = DriverManager.getConnection(url, db.getDbId(), db.getDbPw());
		pre = conn.prepareStatement(sql);
		pre.setString(1, schema);
		result = pre.executeQuery();

		ResultSetMetaData metaData = result.getMetaData();
		int size = metaData.getColumnCount();
		String col;

		Map<String, Object> map;

		while (result.next()) {
			map = new LinkedHashMap<>();
			for (int i = 0; i < size; i++) {
				col = metaData.getColumnName(i + 1);
				map.put(col, result.getString(col));
			}
			list.add(map);
		}

		result.close();
		pre.close();
		conn.close();

		return list;
	}

	// 스키마 디테일 System Privileges 조회
	public List<Map<String, Object>> schemaDetailsSystemPrivileges(String schema, DbDTO db)
			throws ClassNotFoundException, SQLException {
		String sql = "SELECT PRIVILEGE, ADMIN_OPTION, USERNAME AS GRANTEE, 'USER' AS TYPE FROM user_sys_privs WHERE USERNAME = ?";
		if (getGrant(db).equals("DBA")) {
			sql = "SELECT PRIVILEGE, ADMIN_OPTION, GRANTEE, 'USER' AS TYPE FROM dba_sys_privs WHERE GRANTEE = ?";
		}

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		Connection conn = null;
		PreparedStatement pre = null;
		ResultSet result = null;

		Class.forName(driver);
		conn = DriverManager.getConnection(url, db.getDbId(), db.getDbPw());
		pre = conn.prepareStatement(sql);
		pre.setString(1, schema);
		result = pre.executeQuery();

		ResultSetMetaData metaData = result.getMetaData();
		int size = metaData.getColumnCount();
		String col;

		Map<String, Object> map;

		while (result.next()) {
			map = new LinkedHashMap<>();
			for (int i = 0; i < size; i++) {
				col = metaData.getColumnName(i + 1);
				// 티베로에서 조회시 빈칸으로 나옴
				if (col.equals("GRANTEE")) {
					map.put(col, "");
				} else {
					map.put(col, result.getString(col));
				}
			}
			list.add(map);
		}

		result.close();
		pre.close();
		conn.close();

		return list;
	}

	// 스키마 디테일 Extends 조회
	public List<Map<String, Object>> schemaDetailsExtents(String schema, DbDTO db)
			throws ClassNotFoundException, SQLException {
		String sql = "SELECT SEGMENT_TYPE AS TABLESPACE, '' AS SEGMENT_NAME, SEGMENT_NAME AS OBJECT_NAME, '' AS FILE_ID, '' AS BLOCK_ID, BLOCKS FROM user_extents ORDER BY SEGMENT_NAME";
		String grant = getGrant(db);
		if (grant.equals("DBA")) {
			sql = "SELECT SEGMENT_TYPE AS TABLESPACE, '' AS SEGMENT_NAME, SEGMENT_NAME AS OBJECT_NAME, FILE_ID, BLOCK_ID, BLOCKS FROM dba_extents WHERE OWNER = ? ORDER BY SEGMENT_NAME";
		}
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		Connection conn = null;
		PreparedStatement pre = null;
		ResultSet result = null;

		Class.forName(driver);
		conn = DriverManager.getConnection(url, db.getDbId(), db.getDbPw());
		pre = conn.prepareStatement(sql);
		if (grant.equals("DBA")) {
			pre.setString(1, schema);
		}

		result = pre.executeQuery();

		ResultSetMetaData metaData = result.getMetaData();
		int size = metaData.getColumnCount();
		String col;

		Map<String, Object> map;
		while (result.next()) {
			map = new LinkedHashMap<>();
			for (int i = 0; i < size; i++) {
				col = metaData.getColumnName(i + 1);
				map.put(col, result.getString(col));
			}
			list.add(map);
		}
		// 권한이 DBA가 아니고 ID와 스키마가 다를경우 빈칸으로 리턴
		if (!grant.equals("DBA") && !schema.equals(db.getDbId().toUpperCase())) {
			list = new ArrayList<Map<String, Object>>();
		}

		result.close();
		pre.close();
		conn.close();

		return list;
	}

	// 테이블 디테일 Table 조회
	public Map<String, Object> tableDetailsTable(String table, String schema, DbDTO db)
			throws ClassNotFoundException, SQLException {
		String sql = "select T.TABLE_NAME AS NAME, C.COMMENTS, T.OWNER, T.PCT_FREE, T.INI_TRANS, T.LOGGING, T.NUM_ROWS, T.BLOCKS, T.AVG_ROW_LEN, "
				+ "T.SAMPLE_SIZE, T.LAST_ANALYZED, T.DURATION, T.BUFFER_POOL, T.TABLESPACE_NAME, T.COMPRESSION, T.IOT_TYPE, T.MAX_EXTENTS "
				+ " from ALL_TABLES T, ALL_TAB_COMMENTS C where T.OWNER = ? and T.TABLE_NAME = ?";

		Map<String, Object> map = null;

		Connection conn = null;
		PreparedStatement pre = null;
		ResultSet result = null;

		Class.forName(driver);
		conn = DriverManager.getConnection(url, db.getDbId(), db.getDbPw());
		pre = conn.prepareStatement(sql);
		pre.setString(1, schema);
		pre.setString(2, table);
		result = pre.executeQuery();

		ResultSetMetaData metaData = result.getMetaData();
		int size = metaData.getColumnCount();
		String col;

		while (result.next()) {
			map = new LinkedHashMap<>();
			for (int i = 0; i < size; i++) {
				col = metaData.getColumnName(i + 1);
				map.put(col, result.getString(col));
			}
		}

		result.close();
		pre.close();
		conn.close();

		return map;
	}

	// 테이블 디테일 Columns 조회
	public List<Map<String, Object>> tableDetailsColumns(String table, String schema, DbDTO db)
			throws ClassNotFoundException, SQLException {
		String pkSQL = "select acc.column_name from all_constraints ac, all_cons_columns acc "
				+ "where ac.owner = ? and ac.table_name = ? and ac.con_type = 'PRIMARY KEY' and ac.constraint_name = acc.constraint_name";

		String sql = "select TC.COLUMN_NAME, TC.COLUMN_ID, TC.DATA_TYPE, TC.NULLABLE, TC.DATA_DEFAULT, CC.COMMENTS "
				+ "from all_tab_columns TC, all_col_comments CC "
				+ "where TC.TABLE_NAME = CC.TABLE_NAME and TC.COLUMN_NAME = CC.COLUMN_NAME and TC.owner = ? and TC.TABLE_NAME = ? "
				+ "order by 2";

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		List<String> pkList = new ArrayList<String>();

		Map<String, Object> map = new LinkedHashMap<>();

		Connection conn = null;
		PreparedStatement pre = null;
		ResultSet result = null;

		Class.forName(driver);
		conn = DriverManager.getConnection(url, db.getDbId(), db.getDbPw());

		// PK 목록 구하기
		pre = conn.prepareStatement(pkSQL);
		pre.setString(1, schema);
		pre.setString(2, table);
		result = pre.executeQuery();

		while (result.next()) {
			pkList.add(result.getString(1));
		}

		pre = conn.prepareStatement(sql);
		pre.setString(1, schema);
		pre.setString(2, table);
		result = pre.executeQuery();

		ResultSetMetaData metaData = result.getMetaData();
		int size = metaData.getColumnCount();
		String col;

		while (result.next()) {
			map = new LinkedHashMap<>();
			for (int i = 0; i < size; i++) {
				col = metaData.getColumnName(i + 1);
				map.put(col, result.getString(col));
				if (i == 0) {
					// Column이 PK일 경우 Y로 지정
					if (pkList.contains(result.getString(col))) {
						map.put("PK", "Y");
					} else {
						map.put("PK", "");
					}
				}
			}
			list.add(map);
		}

		result.close();
		pre.close();
		conn.close();

		return list;
	}

	// 테이블 디테일 Index Top 조회
	public List<Map<String, Object>> tableDetailsIndexesTop(String table, String schema, DbDTO db)
			throws ClassNotFoundException, SQLException {
		String sql = "select * from all_cons_columns ACC, all_indexes AI "
				+ "where ACC.owner = ? and ACC.TABLE_NAME = ? and ACC.constraint_name = AI.index_name";

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		Map<String, Object> map = new LinkedHashMap<>();

		Connection conn = null;
		PreparedStatement pre = null;
		ResultSet result = null;

		Class.forName(driver);
		conn = DriverManager.getConnection(url, db.getDbId(), db.getDbPw());
		pre = conn.prepareStatement(sql);
		pre.setString(1, schema);
		pre.setString(2, table);
		result = pre.executeQuery();

		ResultSetMetaData metaData = result.getMetaData();
		int size = metaData.getColumnCount();
		String col;

		while (result.next()) {
			map = new LinkedHashMap<>();
			for (int i = 0; i < size; i++) {
				col = metaData.getColumnName(i + 1);
				map.put(col, result.getString(col));
			}
			list.add(map);
		}

		result.close();
		pre.close();
		conn.close();

		return list;
	}

	// 테이블 디테일 Index Bottom 조회
	public Map<String, Object> tableDetailsIndexesBottom(String indexName, DbDTO db)
			throws ClassNotFoundException, SQLException {
		String sql = "select UNIQUENESS, INDEX_NAME, INDEX_TYPE, TABLE_OWNER, TABLE_NAME, TABLE_TYPE, TABLESPACE_NAME, INI_TRANS, PCT_FREE, INITIAL_EXTENT, NEXT_EXTENT, DISTINCT_KEYS "
				+ " from all_indexes where INDEX_NAME = ?";

		Map<String, Object> map = new LinkedHashMap<>();

		Connection conn = null;
		PreparedStatement pre = null;
		ResultSet result = null;

		Class.forName(driver);
		conn = DriverManager.getConnection(url, db.getDbId(), db.getDbPw());
		pre = conn.prepareStatement(sql);
		pre.setString(1, indexName);
		result = pre.executeQuery();

		ResultSetMetaData metaData = result.getMetaData();
		int size = metaData.getColumnCount();
		String col;

		while (result.next()) {
			for (int i = 0; i < size; i++) {
				col = metaData.getColumnName(i + 1);
				map.put(col, result.getString(col));
			}
		}

		result.close();
		pre.close();
		conn.close();

		return map;
	}

	// 테이블 디테일 Index Top 조회
	public List<Map<String, Object>> tableDetailsConstraints(String table, String schema, DbDTO db)
			throws ClassNotFoundException, SQLException {
		String sql = "select ACC.CONSTRAINT_NAME, AC.CON_TYPE, ACC.COLUMN_NAME, ACC.POSITION, AC.DELETE_RULE, AC.R_CONSTRAINT_NAME, AC.SEARCH_CONDITION, AC.R_OWNER "
				+ "from ALL_CONS_COLUMNS ACC, ALL_CONSTRAINTS AC where ACC.OWNER = ? and ACC.TABLE_NAME = ? and ACC.CONSTRAINT_NAME = AC.CONSTRAINT_NAME "
				+ "order by ACC.CONSTRAINT_NAME";

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		Map<String, Object> map = new LinkedHashMap<>();

		Connection conn = null;
		PreparedStatement pre = null;
		ResultSet result = null;

		Class.forName(driver);
		conn = DriverManager.getConnection(url, db.getDbId(), db.getDbPw());
		pre = conn.prepareStatement(sql);
		pre.setString(1, schema);
		pre.setString(2, table);
		result = pre.executeQuery();

		ResultSetMetaData metaData = result.getMetaData();
		int size = metaData.getColumnCount();
		String col;

		while (result.next()) {
			map = new LinkedHashMap<>();
			for (int i = 0; i < size; i++) {
				col = metaData.getColumnName(i + 1);
				map.put(col, result.getString(col));
			}
			list.add(map);
		}

		result.close();
		pre.close();
		conn.close();

		return list;
	}

	// 인덱스 디테일 Columns 조회
	public List<Map<String, Object>> indexDetailsColumns(String indexName, DbDTO db)
			throws ClassNotFoundException, SQLException {
		String sql = "select * from all_ind_columns WHERE index_name = ?";

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;

		Connection conn = null;
		PreparedStatement pre = null;
		ResultSet result = null;

		Class.forName(driver);
		conn = DriverManager.getConnection(url, db.getDbId(), db.getDbPw());
		pre = conn.prepareStatement(sql);
		pre.setString(1, indexName);
		result = pre.executeQuery();

		ResultSetMetaData metaData = result.getMetaData();
		int size = metaData.getColumnCount();
		String col;

		while (result.next()) {
			map = new LinkedHashMap<>();
			for (int i = 0; i < size; i++) {
				col = metaData.getColumnName(i + 1);
				map.put(col, result.getString(col));
			}
			list.add(map);
		}

		result.close();
		pre.close();
		conn.close();

		return list;
	}

	// 시퀀스 디테일 Info 조회
	public Map<String, Object> sequenceDetailsInfo(String sequenceName, DbDTO db)
			throws SQLException, ClassNotFoundException {
		String sql = "select INCREMENT_BY, MIN_VALUE, MAX_VALUE, CYCLE_FLAG, LAST_NUMBER, CACHE_SIZE, ORDER_FLAG from all_sequences WHERE sequence_name = ?";

		Map<String, Object> map = null;

		Connection conn = null;
		PreparedStatement pre = null;
		ResultSet result = null;

		Class.forName(driver);
		conn = DriverManager.getConnection(url, db.getDbId(), db.getDbPw());
		pre = conn.prepareStatement(sql);
		pre.setString(1, sequenceName);
		result = pre.executeQuery();

		ResultSetMetaData metaData = result.getMetaData();
		int size = metaData.getColumnCount();
		String col;

		while (result.next()) {
			map = new LinkedHashMap<>();
			for (int i = 0; i < size; i++) {
				col = metaData.getColumnName(i + 1);
				map.put(col, result.getString(col));
			}
		}

		result.close();
		pre.close();
		conn.close();

		return map;
	}

	// 뷰 디테일 Columns 조회
	public List<Map<String, Object>> viewDetailsColumns(String schema, String viewName, DbDTO db)
			throws ClassNotFoundException, SQLException {
		String sql = "select at.column_name, at.column_id, at.data_type, at.nullable, au.updatable, au.insertable, au.deletable, ac.comments "
				+ "from ALL_TAB_COLUMNS at, ALL_UPDATABLE_COLUMNS au , ALL_COL_COMMENTS ac where at.owner = ? and at.table_name = ? and at.column_name = au.column_name and at.table_name = au.table_name and at.column_name = ac.column_name and at.table_name = ac.table_name order by at.column_id";

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;

		Connection conn = null;
		PreparedStatement pre = null;
		ResultSet result = null;

		Class.forName(driver);
		conn = DriverManager.getConnection(url, db.getDbId(), db.getDbPw());
		pre = conn.prepareStatement(sql);
		pre.setString(1, schema);
		pre.setString(2, viewName);
		result = pre.executeQuery();

		ResultSetMetaData metaData = result.getMetaData();
		int size = metaData.getColumnCount();
		String col;

		while (result.next()) {
			map = new LinkedHashMap<>();
			for (int i = 0; i < size; i++) {
				col = metaData.getColumnName(i + 1);
				map.put(col, result.getString(col));
			}
			list.add(map);
		}

		result.close();
		pre.close();
		conn.close();

		return list;
	}

	// 뷰 디테일 Script 조회
	public List<Map<String, Object>> viewDetailsScript(String schema, String viewName, DbDTO db)
			throws ClassNotFoundException, SQLException {
		String sql = "select TEXT from ALL_VIEWS where owner = ? and view_name = ?";

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;

		Connection conn = null;
		PreparedStatement pre = null;
		ResultSet result = null;

		Class.forName(driver);
		conn = DriverManager.getConnection(url, db.getDbId(), db.getDbPw());
		pre = conn.prepareStatement(sql);
		pre.setString(1, schema);
		pre.setString(2, viewName);
		result = pre.executeQuery();

		ResultSetMetaData metaData = result.getMetaData();
		int size = metaData.getColumnCount();
		String col;

		while (result.next()) {
			map = new LinkedHashMap<>();
			for (int i = 0; i < size; i++) {
				col = metaData.getColumnName(i + 1);
				map.put(col, result.getString(col));
			}
			list.add(map);
		}

		result.close();
		pre.close();
		conn.close();

		return list;
	}

	// function, package, type, package body, undefined, trigger, type body 디테일 Code
	// 조회
	public List<Map<String, Object>> detailsCode(String schema, String name, String type, DbDTO db)
			throws ClassNotFoundException, SQLException {
		String sql = "select * from all_source where owner = ? and type = ? and name = ? order by line";

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;

		Connection conn = null;
		PreparedStatement pre = null;
		ResultSet result = null;

		Class.forName(driver);
		conn = DriverManager.getConnection(url, db.getDbId(), db.getDbPw());
		pre = conn.prepareStatement(sql);
		pre.setString(1, schema);
		pre.setString(2, type);
		pre.setString(3, name);
		result = pre.executeQuery();

		ResultSetMetaData metaData = result.getMetaData();
		int size = metaData.getColumnCount();
		String col;

		while (result.next()) {
			map = new LinkedHashMap<>();
			for (int i = 0; i < size; i++) {
				col = metaData.getColumnName(i + 1);
				map.put(col, result.getString(col));
			}
			list.add(map);
		}

		result.close();
		pre.close();
		conn.close();

		return list;
	}

	// SQL 한줄 실행
	@SuppressWarnings("finally")
	public Map<String, Object> runCurrentSQL(String sql, String type, int index, DbDTO db) throws SQLException {

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
			if (type.equals("SELECT")) {
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
				if (type.equals("CREATE") || type.equals("DROP") || type.equals("ALTER") || type.equals("TRUNCATE")) {
					row.put("DbmsOutput", type.toLowerCase() + " " + sql.split(" ")[1].toLowerCase() + ".");
				} else if (type.equals("INSERT") || type.equals("UPDATE") || type.equals("DELETE")) {
					row.put("DbmsOutput", count + " rows " + type.toLowerCase() + ".");
				} else if (type.equals("GRANT") || type.equals("REVOKE")) {
					row.put("DbmsOutput", "commends complated successfully.");
				}
				data.add(row);
			}

			// 에러 발생시 에러메세지 추가
		} catch (Exception e) {
			long stopTime = System.nanoTime();
			double time = (double) (stopTime - startTime) / 1000000;
			row = new LinkedHashMap<>();
			row.put("Row", index);
			row.put("DbmsOutput", e.getMessage());
			row.put("ExecutionTime", time);

			data.add(row);

			// 에러 메세지를 보내기위해 finally에서 return
		} finally {
			if (result != null)
				result.close();
			if (pre != null)
				pre.close();
			if (conn != null)
				conn.close();
			map.put("size", size);
			map.put("data", data);

			return map;
		}
	}

	// 카프카로 받은 데이터 스케줄러 테이블에 저장
	public void userSchedulerSave(String data) throws Exception {
		String sql = "insert into userScheduler values(USERS_SEQ.NEXTVAL, \'" + data + "\', sysdate, 'N')";

		Connection conn = null;
		PreparedStatement pre = null;

		Class.forName(driver);
		conn = DriverManager.getConnection(url, username, password);
		pre = conn.prepareStatement(sql);
		pre.executeUpdate();
		pre.close();
		conn.close();

	}

	// ActionData에 존재하는 연도 가져오기
	public List<String> getChartYears() throws ClassNotFoundException, SQLException {
		List<String> list = new ArrayList<String>();

		String sql = "select year from ACTIONDATA group by year order by year desc";

		Connection conn = null;
		PreparedStatement pre = null;
		ResultSet result = null;

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

		return list;
	}

	// 연도에 해당하는 ActionData에 존재하는 월 가져오기
	public List<String> getChartMonth(String year) throws ClassNotFoundException, SQLException {
		List<String> list = new ArrayList<String>();

		String sql = "select month from (select * from ACTIONDATA where year = ?) group by month order by month desc";

		Connection conn = null;
		PreparedStatement pre = null;
		ResultSet result = null;

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

		return list;
	}

	// year와 action에 해당하는 ACTIONDATA 데이터 Json 리스트로 반환
	public List<JSONObject> getActionData(String year, String action)
			throws ClassNotFoundException, SQLException, JSONException {
		List<JSONObject> list = new ArrayList<JSONObject>();

		String sql = "select * from ACTIONDATA where action = ? and year = ? order by 1 desc, 2, 3, 4";

		Connection conn = null;
		PreparedStatement pre = null;
		ResultSet result = null;

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

		return list;
	}

	// year, month, action에 해당하는 데이터 Json 리스트로 반환
	public List<JSONObject> getActionData(String year, String month, String action)
			throws ClassNotFoundException, SQLException, JSONException {
		List<JSONObject> list = new ArrayList<JSONObject>();

		String sql = "select * from ACTIONDATA where action = ? and year = ? and month = ? order by 1 desc, 2, 3, 4";

		Connection conn = null;
		PreparedStatement pre = null;
		ResultSet result = null;

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

		return list;
	}

}
