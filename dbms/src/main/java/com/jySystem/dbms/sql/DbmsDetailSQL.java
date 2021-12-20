package com.jySystem.dbms.sql;

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
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.jySystem.dbms.dto.DbDTO;
import com.jySystem.dbms.dto.DbObjectDTO;
import com.jySystem.exception.JYException;

@Service
public class DbmsDetailSQL {

	// Tibero driver, url, username, password
	@Value("${spring.datasource.driver-class-name}")
	private String driver;

	@Value("${spring.datasource.url}")
	private String url;

	@Value("${spring.datasource.username}")
	private String username;

	@Value("${spring.datasource.password}")
	private String password;
	
	// 현재 내 권한 조회
	public String getGrant(DbDTO db) throws JYException {
		String sql = "SELECT GRANTED_ROLE FROM user_role_privs";
		String grant = null;

		Connection conn = null;
		PreparedStatement pre = null;
		ResultSet result = null;

		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, db.getDbId(), db.getDbPw());
			pre = conn.prepareStatement(sql);
			result = pre.executeQuery();

			while (result.next()) {
				grant = result.getString(1);
				// DBA 권한이 있을때 빠져나오기
				if (Objects.equals(grant, "DBA")) {
					break;
				}
				grant = "CONNECT";
			}

			result.close();
			pre.close();
			conn.close();
		} catch (ClassNotFoundException cnfe) {
			throw new JYException("Class Not Found Exception", cnfe);
		} catch (SQLException se) {
			throw new JYException("SQL Exception", se);
		}

		return grant;
	}

	// 스키마 디테일 Info 조회
	public Map<String, Object> getSchemaDetailsInfo(DbObjectDTO dto, DbDTO db) throws JYException {
		String sql = "SELECT * FROM all_users WHERE USERNAME = ?";
		if (Objects.equals(getGrant(db), "DBA")) {
			sql = "SELECT USERNAME, USER_ID, ACCOUNT_STATUS, LOCK_DATE, EXPIRY_DATE, DEFAULT_TABLESPACE, CREATED "
					+ "FROM dba_users WHERE USERNAME = ?";
			// 권한이 DBA가 아닐때 자기 자신 조회시 추가 정보있음
		} else if (Objects.equals(dto.getSchemaName(), db.getDbId().toUpperCase())) {
			sql = "SELECT * FROM USER_USERS WHERE USERNAME = ?";
		}

		Map<String, Object> map = new LinkedHashMap<>();

		Connection conn = null;
		PreparedStatement pre = null;
		ResultSet result = null;

		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, db.getDbId(), db.getDbPw());
			pre = conn.prepareStatement(sql);
			pre.setString(1, dto.getSchemaName());
			result = pre.executeQuery();

			ResultSetMetaData metaData = result.getMetaData();
			int size = metaData.getColumnCount();
			String col;

			while (result.next()) {
				map = new LinkedHashMap<>();
				for (int i = 0; i < size; i++) {
					col = metaData.getColumnName(i + 1);
					// 날짜형식 YYYY-MM-DD 로 고정
					if (Objects.equals(col, "CREATED")) {
						map.put(col, result.getString(col).substring(0, 10));
					} else {
						map.put(col, result.getString(col));
					}
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

		return map;
	}

	// 스키마 디테일 Role Grants 조회
	public List<Map<String, Object>> getSchemaDetailsRoleGrants(DbObjectDTO dto, DbDTO db) throws JYException {
		String sql = "SELECT * FROM user_role_privs WHERE GRANTEE = ?";
		if (getGrant(db).equals("DBA")) {
			sql = "SELECT * FROM dba_role_privs WHERE GRANTEE = ?";
		}

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		Connection conn = null;
		PreparedStatement pre = null;
		ResultSet result = null;

		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, db.getDbId(), db.getDbPw());
			pre = conn.prepareStatement(sql);
			pre.setString(1, dto.getSchemaName());
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
		} catch (ClassNotFoundException cnfe) {
			throw new JYException("Class Not Found Exception", cnfe);
		} catch (SQLException se) {
			throw new JYException("SQL Exception", se);
		}

		return list;
	}

	// 스키마 디테일 System Privileges 조회
	public List<Map<String, Object>> getSchemaDetailsSystemPrivileges(DbObjectDTO dto, DbDTO db) throws JYException {
		String sql = "SELECT PRIVILEGE, ADMIN_OPTION, USERNAME AS GRANTEE, 'USER' AS TYPE FROM user_sys_privs WHERE USERNAME = ?";
		if (Objects.equals(getGrant(db), "DBA")) {
			sql = "SELECT PRIVILEGE, ADMIN_OPTION, GRANTEE, 'USER' AS TYPE FROM dba_sys_privs WHERE GRANTEE = ?";
		}

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		Connection conn = null;
		PreparedStatement pre = null;
		ResultSet result = null;

		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, db.getDbId(), db.getDbPw());
			pre = conn.prepareStatement(sql);
			pre.setString(1, dto.getSchemaName());
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
					if (Objects.equals(col, "GRANTEE")) {
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
		} catch (ClassNotFoundException cnfe) {
			throw new JYException("Class Not Found Exception", cnfe);
		} catch (SQLException se) {
			throw new JYException("SQL Exception", se);
		}

		return list;
	}

	// 스키마 디테일 Extends 조회
	public List<Map<String, Object>> getSchemaDetailsExtents(DbObjectDTO dto, DbDTO db) throws JYException {
		String sql = "SELECT SEGMENT_TYPE AS TABLESPACE, '' AS SEGMENT_NAME, SEGMENT_NAME AS OBJECT_NAME, '' AS FILE_ID, '' AS BLOCK_ID, BLOCKS FROM user_extents ORDER BY SEGMENT_NAME";
		String grant = getGrant(db);
		if (Objects.equals(grant, "DBA")) {
			sql = "SELECT SEGMENT_TYPE AS TABLESPACE, '' AS SEGMENT_NAME, SEGMENT_NAME AS OBJECT_NAME, FILE_ID, BLOCK_ID, BLOCKS FROM dba_extents WHERE OWNER = ? ORDER BY SEGMENT_NAME";
		}
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		Connection conn = null;
		PreparedStatement pre = null;
		ResultSet result = null;

		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, db.getDbId(), db.getDbPw());
			pre = conn.prepareStatement(sql);
			if (Objects.equals(grant, "DBA")) {
				pre.setString(1, dto.getSchemaName());
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
			if (!Objects.equals(grant, "DBA") && !Objects.equals(dto.getSchemaName(), db.getDbId().toUpperCase())) {
				list = new ArrayList<Map<String, Object>>();
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

	// 테이블 디테일 Table 조회
	public Map<String, Object> getTableDetailsTable(DbObjectDTO dto, DbDTO db) throws JYException {
		String sql = "select T.TABLE_NAME AS NAME, C.COMMENTS, T.OWNER, T.PCT_FREE, T.INI_TRANS, T.LOGGING, T.NUM_ROWS, T.BLOCKS, T.AVG_ROW_LEN, "
				+ "T.SAMPLE_SIZE, T.LAST_ANALYZED, T.DURATION, T.BUFFER_POOL, T.TABLESPACE_NAME, T.COMPRESSION, T.IOT_TYPE, T.MAX_EXTENTS "
				+ " from ALL_TABLES T, ALL_TAB_COMMENTS C where T.OWNER = ? and T.TABLE_NAME = ?";

		Map<String, Object> map = null;

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
		} catch (ClassNotFoundException cnfe) {
			throw new JYException("Class Not Found Exception", cnfe);
		} catch (SQLException se) {
			throw new JYException("SQL Exception", se);
		}

		return map;
	}

	// 테이블 디테일 Columns 조회
	public List<Map<String, Object>> getTableDetailsColumns(DbObjectDTO dto, DbDTO db) throws JYException {
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

		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, db.getDbId(), db.getDbPw());

			// PK 목록 구하기
			pre = conn.prepareStatement(pkSQL);
			pre.setString(1, dto.getSchemaName());
			pre.setString(2, dto.getTableName());
			result = pre.executeQuery();

			while (result.next()) {
				pkList.add(result.getString(1));
			}

			pre = conn.prepareStatement(sql);
			pre.setString(1, dto.getSchemaName());
			pre.setString(2, dto.getTableName());
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
		} catch (ClassNotFoundException cnfe) {
			throw new JYException("Class Not Found Exception", cnfe);
		} catch (SQLException se) {
			throw new JYException("SQL Exception", se);
		}

		return list;
	}

	// 테이블 디테일 Index Top 조회
	public List<Map<String, Object>> getTableDetailsIndexesTop(DbObjectDTO dto, DbDTO db) throws JYException {
		String sql = "select * from all_cons_columns ACC, all_indexes AI "
				+ "where ACC.owner = ? and ACC.TABLE_NAME = ? and ACC.constraint_name = AI.index_name";

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		Map<String, Object> map = new LinkedHashMap<>();

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
		} catch (ClassNotFoundException cnfe) {
			throw new JYException("Class Not Found Exception", cnfe);
		} catch (SQLException se) {
			throw new JYException("SQL Exception", se);
		}

		return list;
	}

	// 테이블 디테일 Index Bottom 조회
	public Map<String, Object> getTableDetailsIndexesBottom(DbObjectDTO dto, DbDTO db) throws JYException {
		String sql = "select UNIQUENESS, INDEX_NAME, INDEX_TYPE, TABLE_OWNER, TABLE_NAME, TABLE_TYPE, TABLESPACE_NAME, INI_TRANS, PCT_FREE, INITIAL_EXTENT, NEXT_EXTENT, DISTINCT_KEYS "
				+ " from all_indexes where INDEX_NAME = ?";

		Map<String, Object> map = new LinkedHashMap<>();

		Connection conn = null;
		PreparedStatement pre = null;
		ResultSet result = null;

		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, db.getDbId(), db.getDbPw());
			pre = conn.prepareStatement(sql);
			pre.setString(1, dto.getIndexName());
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
		} catch (ClassNotFoundException cnfe) {
			throw new JYException("Class Not Found Exception", cnfe);
		} catch (SQLException se) {
			throw new JYException("SQL Exception", se);
		}

		return map;
	}

	// 테이블 디테일 Index Top 조회
	public List<Map<String, Object>> getTableDetailsConstraints(DbObjectDTO dto, DbDTO db) throws JYException {
		String sql = "select ACC.CONSTRAINT_NAME, AC.CON_TYPE, ACC.COLUMN_NAME, ACC.POSITION, AC.DELETE_RULE, AC.R_CONSTRAINT_NAME, AC.SEARCH_CONDITION, AC.R_OWNER "
				+ "from ALL_CONS_COLUMNS ACC, ALL_CONSTRAINTS AC where ACC.OWNER = ? and ACC.TABLE_NAME = ? and ACC.CONSTRAINT_NAME = AC.CONSTRAINT_NAME "
				+ "order by ACC.CONSTRAINT_NAME";

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		Map<String, Object> map = new LinkedHashMap<>();

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
		} catch (ClassNotFoundException cnfe) {
			throw new JYException("Class Not Found Exception", cnfe);
		} catch (SQLException se) {
			throw new JYException("SQL Exception", se);
		}

		return list;
	}

	// 테이블 디테일 Script 조회
	public String getTableDetailsScript(DbObjectDTO dto, DbDTO db) throws JYException {
		String sql = "select dbms_lob.substr(dbms_metadata.get_DDL('TABLE', table_name, owner) "
				+ ",dbms_lob.getlength(dbms_metadata.get_DDL('TABLE', table_name, owner))) from all_tables "
				+ "where owner = ? and table_name = ?";

		String script = "";

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

			while (result.next()) {
				script += result.getString(1);
			}

			result.close();
			pre.close();
			conn.close();
		} catch (ClassNotFoundException cnfe) {
			throw new JYException("Class Not Found Exception", cnfe);
		} catch (SQLException se) {
			throw new JYException("SQL Exception", se);
		}

		return script;
	}

	// 인덱스 디테일 Columns 조회
	public List<Map<String, Object>> getIndexDetailsColumns(DbObjectDTO dto, DbDTO db) throws JYException {
		String sql = "select * from all_ind_columns WHERE index_name = ?";

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;

		Connection conn = null;
		PreparedStatement pre = null;
		ResultSet result = null;

		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, db.getDbId(), db.getDbPw());
			pre = conn.prepareStatement(sql);
			pre.setString(1, dto.getIndexName());
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
		} catch (ClassNotFoundException cnfe) {
			throw new JYException("Class Not Found Exception", cnfe);
		} catch (SQLException se) {
			throw new JYException("SQL Exception", se);
		}

		return list;
	}

	// 시퀀스 디테일 Info 조회
	public Map<String, Object> getSequenceDetailsInfo(DbObjectDTO dto, DbDTO db) throws JYException {
		String sql = "select INCREMENT_BY, MIN_VALUE, MAX_VALUE, CYCLE_FLAG, LAST_NUMBER, CACHE_SIZE, ORDER_FLAG from all_sequences WHERE sequence_name = ?";

		Map<String, Object> map = null;

		Connection conn = null;
		PreparedStatement pre = null;
		ResultSet result = null;

		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, db.getDbId(), db.getDbPw());
			pre = conn.prepareStatement(sql);
			pre.setString(1, dto.getSequenceName());
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
		} catch (ClassNotFoundException cnfe) {
			throw new JYException("Class Not Found Exception", cnfe);
		} catch (SQLException se) {
			throw new JYException("SQL Exception", se);
		}

		return map;
	}

	// 뷰 디테일 Columns 조회
	public List<Map<String, Object>> getViewDetailsColumns(DbObjectDTO dto, DbDTO db) throws JYException {
		String sql = "select at.column_name, at.column_id, at.data_type, at.nullable, au.updatable, au.insertable, au.deletable, ac.comments "
				+ "from ALL_TAB_COLUMNS at, ALL_UPDATABLE_COLUMNS au , ALL_COL_COMMENTS ac where at.owner = ? and at.table_name = ? and at.column_name = au.column_name and at.table_name = au.table_name and at.column_name = ac.column_name and at.table_name = ac.table_name order by at.column_id";

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;

		Connection conn = null;
		PreparedStatement pre = null;
		ResultSet result = null;

		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, db.getDbId(), db.getDbPw());
			pre = conn.prepareStatement(sql);
			pre.setString(1, dto.getSchemaName());
			pre.setString(2, dto.getViewName());
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
		} catch (ClassNotFoundException cnfe) {
			throw new JYException("Class Not Found Exception", cnfe);
		} catch (SQLException se) {
			throw new JYException("SQL Exception", se);
		}

		return list;
	}

	// 뷰 디테일 Script 조회
	public List<Map<String, Object>> getViewDetailsScript(DbObjectDTO dto, DbDTO db) throws JYException {
		String sql = "select TEXT from ALL_VIEWS where owner = ? and view_name = ?";

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;

		Connection conn = null;
		PreparedStatement pre = null;
		ResultSet result = null;

		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, db.getDbId(), db.getDbPw());
			pre = conn.prepareStatement(sql);
			pre.setString(1, dto.getSchemaName());
			pre.setString(2, dto.getViewName());
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
		} catch (ClassNotFoundException cnfe) {
			throw new JYException("Class Not Found Exception", cnfe);
		} catch (SQLException se) {
			throw new JYException("SQL Exception", se);
		}

		return list;
	}

	// function, package, type, package body, undefined, trigger, type body 디테일 Code
	// 조회
	public List<Map<String, Object>> getDetailsCode(DbObjectDTO dto, DbDTO db) throws JYException {
		String sql = "select * from all_source where owner = ? and type = ? and name = ? order by line";

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;

		Connection conn = null;
		PreparedStatement pre = null;
		ResultSet result = null;

		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, db.getDbId(), db.getDbPw());
			pre = conn.prepareStatement(sql);
			pre.setString(1, dto.getSchemaName());
			pre.setString(2, dto.getObjectType());
			if (Objects.equals(dto.getObjectType(),"FUNCTION")) {
				pre.setString(3, dto.getFunctionName());
			} else if (Objects.equals(dto.getObjectType(),"PACKAGE")) {
				pre.setString(3, dto.getPackageName());
			} else if (Objects.equals(dto.getObjectType(),"TYPE")) {
				pre.setString(3, dto.getTypeName());
			} else if (Objects.equals(dto.getObjectType(),"TRIGGER")) {
				pre.setString(3, dto.getTriggerName());
			}
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
		} catch (ClassNotFoundException cnfe) {
			throw new JYException("Class Not Found Exception", cnfe);
		} catch (SQLException se) {
			throw new JYException("SQL Exception", se);
		}

		return list;
	}
	
	
}
