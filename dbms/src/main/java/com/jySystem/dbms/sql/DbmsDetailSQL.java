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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.jySystem.common.util.ConvertSqlToString;
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
	
	@Autowired
	private ConvertSqlToString converter;
	
	// 현재 내 권한 조회
	public String getGrant(DbDTO db) throws JYException {
		String selectSQL = converter.Convert("sql/DbmsDetailSQL/getGrant.sql");
		String grant = null;

		Connection conn = null;
		PreparedStatement pre = null;
		ResultSet result = null;

		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, db.getDbId(), db.getDbPw());
			pre = conn.prepareStatement(selectSQL);
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
		String selectSQL = converter.Convert("sql/DbmsDetailSQL/SchemaDetails/getSchemaDetailsInfoDefault.sql");
		if (Objects.equals(getGrant(db), "DBA")) {
			selectSQL = converter.Convert("sql/DbmsDetailSQL/SchemaDetails/getSchemaDetailsInfoDBA.sql");
			// 권한이 DBA가 아닐때 자기 자신 조회시 추가 정보있음
		} else if (Objects.equals(dto.getSchemaName(), db.getDbId().toUpperCase())) {
			selectSQL = converter.Convert("sql/DbmsDetailSQL/SchemaDetails/getSchemaDetailsInfoMyself.sql");
		}

		Map<String, Object> map = new LinkedHashMap<>();

		Connection conn = null;
		PreparedStatement pre = null;
		ResultSet result = null;

		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, db.getDbId(), db.getDbPw());
			pre = conn.prepareStatement(selectSQL);
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
		String selectSQL = converter.Convert("sql/DbmsDetailSQL/SchemaDetails/getSchemaDetailsRoleGrantsDefault.sql");
		if (getGrant(db).equals("DBA")) {
			selectSQL = converter.Convert("sql/DbmsDetailSQL/SchemaDetails/getSchemaDetailsRoleGrantsDBA.sql");
		}

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		Connection conn = null;
		PreparedStatement pre = null;
		ResultSet result = null;

		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, db.getDbId(), db.getDbPw());
			pre = conn.prepareStatement(selectSQL);
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
		String selectSQL = converter.Convert("sql/DbmsDetailSQL/SchemaDetails/getSchemaDetailsSystemPrivilegesDefault.sql");
		if (Objects.equals(getGrant(db), "DBA")) {
			selectSQL = converter.Convert("sql/DbmsDetailSQL/SchemaDetails/getSchemaDetailsSystemPrivilegesDBA.sql");
		}

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		Connection conn = null;
		PreparedStatement pre = null;
		ResultSet result = null;

		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, db.getDbId(), db.getDbPw());
			pre = conn.prepareStatement(selectSQL);
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
		String selectSQL = converter.Convert("sql/DbmsDetailSQL/SchemaDetails/getSchemaDetailsExtentsDefault.sql");
		String grant = getGrant(db);
		if (Objects.equals(grant, "DBA")) {
			selectSQL = converter.Convert("sql/DbmsDetailSQL/SchemaDetails/getSchemaDetailsExtentsDBA.sql");
		}
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		Connection conn = null;
		PreparedStatement pre = null;
		ResultSet result = null;

		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, db.getDbId(), db.getDbPw());
			pre = conn.prepareStatement(selectSQL);
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
		String selectSQL = converter.Convert("sql/DbmsDetailSQL/TableDetails/getTableDetailsTable.sql");

		Map<String, Object> map = null;

		Connection conn = null;
		PreparedStatement pre = null;
		ResultSet result = null;

		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, db.getDbId(), db.getDbPw());
			pre = conn.prepareStatement(selectSQL);
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
		String pkSQL = converter.Convert("sql/DbmsDetailSQL/TableDetails/getTableDetailsColumnsPK.sql");

		String selectSQL = converter.Convert("sql/DbmsDetailSQL/TableDetails/getTableDetailsColumns.sql");

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

			pre = conn.prepareStatement(selectSQL);
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
		String selectSQL = converter.Convert("sql/DbmsDetailSQL/TableDetails/getTableDetailsIndexesTop.sql");
		
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		Map<String, Object> map = new LinkedHashMap<>();

		Connection conn = null;
		PreparedStatement pre = null;
		ResultSet result = null;

		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, db.getDbId(), db.getDbPw());
			pre = conn.prepareStatement(selectSQL);
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
		String selectSQL = converter.Convert("sql/DbmsDetailSQL/TableDetails/getTableDetailsIndexesBottom.sql");

		Map<String, Object> map = new LinkedHashMap<>();

		Connection conn = null;
		PreparedStatement pre = null;
		ResultSet result = null;

		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, db.getDbId(), db.getDbPw());
			pre = conn.prepareStatement(selectSQL);
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
		String selectSQL = converter.Convert("sql/DbmsDetailSQL/TableDetails/getTableDetailsConstraints.sql");

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		Map<String, Object> map = new LinkedHashMap<>();

		Connection conn = null;
		PreparedStatement pre = null;
		ResultSet result = null;

		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, db.getDbId(), db.getDbPw());
			pre = conn.prepareStatement(selectSQL);
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
		String selectSQL = converter.Convert("sql/DbmsDetailSQL/TableDetails/getTableDetailsScript.sql");

		String script = "";

		Connection conn = null;
		PreparedStatement pre = null;
		ResultSet result = null;

		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, db.getDbId(), db.getDbPw());
			pre = conn.prepareStatement(selectSQL);
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
		String selectSQL = converter.Convert("sql/DbmsDetailSQL/IndexDetails/getIndexDetailsColumns.sql");

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;

		Connection conn = null;
		PreparedStatement pre = null;
		ResultSet result = null;

		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, db.getDbId(), db.getDbPw());
			pre = conn.prepareStatement(selectSQL);
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
		String selectSQL = converter.Convert("sql/DbmsDetailSQL/SequenceDetails/getSequenceDetailsInfo.sql");

		Map<String, Object> map = null;

		Connection conn = null;
		PreparedStatement pre = null;
		ResultSet result = null;

		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, db.getDbId(), db.getDbPw());
			pre = conn.prepareStatement(selectSQL);
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
		String selectSQL = converter.Convert("sql/DbmsDetailSQL/ViewDetails/getViewDetailsColumns.sql");
		
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;

		Connection conn = null;
		PreparedStatement pre = null;
		ResultSet result = null;

		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, db.getDbId(), db.getDbPw());
			pre = conn.prepareStatement(selectSQL);
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
		String selectSQL = converter.Convert("sql/DbmsDetailSQL/ViewDetails/getViewDetailsScript.sql");

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;

		Connection conn = null;
		PreparedStatement pre = null;
		ResultSet result = null;

		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, db.getDbId(), db.getDbPw());
			pre = conn.prepareStatement(selectSQL);
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

	// function, package, type, package body, trigger, type body, procedure 디테일 Code
	// 조회
	public List<Map<String, Object>> getDetailsCode(DbObjectDTO dto, DbDTO db) throws JYException {
		String selectSQL = converter.Convert("sql/DbmsDetailSQL/getDetailsCode.sql");

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;

		Connection conn = null;
		PreparedStatement pre = null;
		ResultSet result = null;

		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, db.getDbId(), db.getDbPw());
			pre = conn.prepareStatement(selectSQL);
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
			} else if (Objects.equals(dto.getObjectType(),"PROCEDURE")) {
				pre.setString(3, dto.getProcedureName());
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
