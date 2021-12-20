package com.jySystem.dbms.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.jySystem.common.config.SessionConfig;
import com.jySystem.dbms.dto.DbDTO;
import com.jySystem.dbms.dto.DbObjectDTO;
import com.jySystem.dbms.sql.DbmsDetailSQL;
import com.jySystem.exception.JYException;

@Service
public class DbmsDetailServiceImpl implements DbmsDetailService {

	@Value("${dbms.properties.session-id}")
	private String sessionID;
	@Value("${dbms.properties.session-db-id}")
	private String sessionDBID;
	@Value("${dbms.properties.session-db-pw}")
	private String sessionDBPW;
	
	@Autowired
	private DbmsDetailSQL dbmsSQL;
	
	private SessionConfig session = new SessionConfig();
	
	// 스키마 디테일 Info 데이터
	@Override
	public Map<String, Object> schemaDetailsInfo(DbObjectDTO dto, String userId) throws JYException {
		DbDTO db = session.getSessionID(sessionID, sessionDBID, sessionDBPW, userId);
		Map<String, Object> info = new HashMap<String, Object>();
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();

		info = dbmsSQL.schemaDetailsInfo(dto, db);

		Set<String> parameters = info.keySet();
		for (String s : parameters) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("PARAMETER", s);
			map.put("VALUE", info.get(s));
			rows.add(map);
		}
		result.put("rows", rows);

		return result;
	}

	// 스키마 디테일 Role Grants 테이블 검색
	@Override
	public List<Map<String, Object>> schemaDetailsRoleGrants(DbObjectDTO dto, String userId) throws JYException {
		DbDTO db = session.getSessionID(sessionID, sessionDBID, sessionDBPW, userId);

		return dbmsSQL.schemaDetailsRoleGrants(dto, db);
	}

	// 스키마 디테일 System Privileges 테이블 검색
	@Override
	public List<Map<String, Object>> schemaDetailsSystemPrivileges(DbObjectDTO dto, String userId) throws JYException {
		DbDTO db = session.getSessionID(sessionID, sessionDBID, sessionDBPW, userId);

		return dbmsSQL.schemaDetailsSystemPrivileges(dto, db);
	}

	// 스키마 디테일 Extends 테이블 검색
	@Override
	public List<Map<String, Object>> schemaDetailsExtents(DbObjectDTO dto, String userId) throws JYException {
		DbDTO db = session.getSessionID(sessionID, sessionDBID, sessionDBPW, userId);

		return dbmsSQL.schemaDetailsExtents(dto, db);
	}

	// 테이블 디테일 Table 테이블 검색
	@Override
	public Map<String, Object> tableDetailsTable(DbObjectDTO dto, String userId) throws JYException {
		DbDTO db = session.getSessionID(sessionID, sessionDBID, sessionDBPW, userId);
		Map<String, Object> info = new HashMap<String, Object>();
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
		info = dbmsSQL.tableDetailsTable(dto, db);

		Set<String> parameters = info.keySet();
		for (String s : parameters) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("VALUE", info.get(s));
			s = s.replace("_", " ");
			String parameter = s.substring(0, 1) + s.substring(1).toLowerCase();
			map.put("PARAMETER", parameter);
			rows.add(map);
		}
		result.put("rows", rows);

		return result;
	}

	// 테이블 디테일 Columns 테이블 검색
	@Override
	public List<Map<String, Object>> tableDetailsColumns(DbObjectDTO dto, String userId) throws JYException {
		DbDTO db = session.getSessionID(sessionID, sessionDBID, sessionDBPW, userId);

		return dbmsSQL.tableDetailsColumns(dto, db);
	}

	// 테이블 디테일 Indexes Top 테이블 검색
	@Override
	public List<Map<String, Object>> tableDetailsIndexesTop(DbObjectDTO dto, String userId) throws JYException {
		DbDTO db = session.getSessionID(sessionID, sessionDBID, sessionDBPW, userId);

		return dbmsSQL.tableDetailsIndexesTop(dto, db);
	}

	// 테이블 디테일 Indexes Bottom 테이블 검색
	@Override
	public Map<String, Object> tableDetailsIndexesBottom(DbObjectDTO dto, String userId) throws JYException {
		DbDTO db = session.getSessionID(sessionID, sessionDBID, sessionDBPW, userId);
		Map<String, Object> info = new HashMap<String, Object>();
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
		info = dbmsSQL.tableDetailsIndexesBottom(dto, db);

		Set<String> parameters = info.keySet();
		for (String s : parameters) {
			Map<String, Object> map = new HashMap<String, Object>();
			s = s.replace("_", " ");
			String parameter = s.substring(0, 1) + s.substring(1).toLowerCase();

			map.put("VALUE", info.get(s));
			map.put("PARAMETER", parameter);
			rows.add(map);
		}
		result.put("rows", rows);

		return result;
	}

	// 테이블 디테일 Constraints 테이블 검색
	@Override
	public List<Map<String, Object>> tableDetailsConstraints(DbObjectDTO dto, String userId) throws JYException {
		DbDTO db = session.getSessionID(sessionID, sessionDBID, sessionDBPW, userId);

		return dbmsSQL.tableDetailsConstraints(dto, db);
	}

	// 테이블 디테일 Script 검색
	@Override
	public String tableDetailsScript(DbObjectDTO dto, String userId) throws JYException {
		DbDTO db = session.getSessionID(sessionID, sessionDBID, sessionDBPW, userId);

		return dbmsSQL.tableDetailsScript(dto, db);
	}

	// 인덱스 디테일 Columns 테이블 검색
	@Override
	public List<Map<String, Object>> indexDetailsColumns(DbObjectDTO dto, String userId) throws JYException {
		DbDTO db = session.getSessionID(sessionID, sessionDBID, sessionDBPW, userId);

		return dbmsSQL.indexDetailsColumns(dto, db);
	}

	// 시퀀스 디테일 Info 테이블 검색
	@Override
	public Map<String, Object> sequenceDetailsInfo(DbObjectDTO dto, String userId) throws JYException {
		DbDTO db = session.getSessionID(sessionID, sessionDBID, sessionDBPW, userId);
		Map<String, Object> info = new HashMap<String, Object>();
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
		info = dbmsSQL.sequenceDetailsInfo(dto, db);

		Set<String> parameters = info.keySet();
		for (String s : parameters) {
			Map<String, Object> map = new HashMap<String, Object>();
			s = s.replace("_", " ");
			String parameter = s.substring(0, 1) + s.substring(1).toLowerCase();

			map.put("VALUE", info.get(s));
			map.put("PARAMETER", parameter);
			rows.add(map);
		}
		result.put("rows", rows);

		return result;
	}

	// 뷰 디테일 Columns 테이블 검색
	@Override
	public List<Map<String, Object>> viewDetailsColumns(DbObjectDTO dto, String userId) throws JYException {
		DbDTO db = session.getSessionID(sessionID, sessionDBID, sessionDBPW, userId);

		return dbmsSQL.viewDetailsColumns(dto, db);
	}

	// 뷰 디테일 Columns 테이블 검색
	@Override
	public List<Map<String, Object>> viewDetailsScript(DbObjectDTO dto, String userId) throws JYException {
		DbDTO db = session.getSessionID(sessionID, sessionDBID, sessionDBPW, userId);

		return dbmsSQL.viewDetailsScript(dto, db);
	}

	// 펀션 디테일 Code 테이블 검색
	@Override
	public List<Map<String, Object>> detailsCode(DbObjectDTO dto, String userId) throws JYException {
		DbDTO db = session.getSessionID(sessionID, sessionDBID, sessionDBPW, userId);

		return dbmsSQL.detailsCode(dto, db);
	}

}
