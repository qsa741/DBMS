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
	public Map<String, Object> getSchemaDetailsInfo(DbObjectDTO dto, String userId) throws JYException {
		DbDTO db = session.getSessionID(sessionID, sessionDBID, sessionDBPW, userId);
		Map<String, Object> info = new HashMap<String, Object>();
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();

		info = dbmsSQL.getSchemaDetailsInfo(dto, db);

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
	public List<Map<String, Object>> getSchemaDetailsRoleGrants(DbObjectDTO dto, String userId) throws JYException {
		DbDTO db = session.getSessionID(sessionID, sessionDBID, sessionDBPW, userId);

		return dbmsSQL.getSchemaDetailsRoleGrants(dto, db);
	}

	// 스키마 디테일 System Privileges 테이블 검색
	@Override
	public List<Map<String, Object>> getSchemaDetailsSystemPrivileges(DbObjectDTO dto, String userId) throws JYException {
		DbDTO db = session.getSessionID(sessionID, sessionDBID, sessionDBPW, userId);

		return dbmsSQL.getSchemaDetailsSystemPrivileges(dto, db);
	}

	// 스키마 디테일 Extends 테이블 검색
	@Override
	public List<Map<String, Object>> getSchemaDetailsExtents(DbObjectDTO dto, String userId) throws JYException {
		DbDTO db = session.getSessionID(sessionID, sessionDBID, sessionDBPW, userId);

		return dbmsSQL.getSchemaDetailsExtents(dto, db);
	}

	// 테이블 디테일 Table 테이블 검색
	@Override
	public Map<String, Object> getTableDetailsTable(DbObjectDTO dto, String userId) throws JYException {
		DbDTO db = session.getSessionID(sessionID, sessionDBID, sessionDBPW, userId);
		Map<String, Object> info = new HashMap<String, Object>();
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
		info = dbmsSQL.getTableDetailsTable(dto, db);

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
	public List<Map<String, Object>> getTableDetailsColumns(DbObjectDTO dto, String userId) throws JYException {
		DbDTO db = session.getSessionID(sessionID, sessionDBID, sessionDBPW, userId);

		return dbmsSQL.getTableDetailsColumns(dto, db);
	}

	// 테이블 디테일 Indexes Top 테이블 검색
	@Override
	public List<Map<String, Object>> getTableDetailsIndexesTop(DbObjectDTO dto, String userId) throws JYException {
		DbDTO db = session.getSessionID(sessionID, sessionDBID, sessionDBPW, userId);

		return dbmsSQL.getTableDetailsIndexesTop(dto, db);
	}

	// 테이블 디테일 Indexes Bottom 테이블 검색
	@Override
	public Map<String, Object> getTableDetailsIndexesBottom(DbObjectDTO dto, String userId) throws JYException {
		DbDTO db = session.getSessionID(sessionID, sessionDBID, sessionDBPW, userId);
		Map<String, Object> info = new HashMap<String, Object>();
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
		info = dbmsSQL.getTableDetailsIndexesBottom(dto, db);

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
	public List<Map<String, Object>> getTableDetailsConstraints(DbObjectDTO dto, String userId) throws JYException {
		DbDTO db = session.getSessionID(sessionID, sessionDBID, sessionDBPW, userId);

		return dbmsSQL.getTableDetailsConstraints(dto, db);
	}

	// 테이블 디테일 Script 검색
	@Override
	public String getTableDetailsScript(DbObjectDTO dto, String userId) throws JYException {
		DbDTO db = session.getSessionID(sessionID, sessionDBID, sessionDBPW, userId);

		return dbmsSQL.getTableDetailsScript(dto, db);
	}

	// 인덱스 디테일 Columns 테이블 검색
	@Override
	public List<Map<String, Object>> getIndexDetailsColumns(DbObjectDTO dto, String userId) throws JYException {
		DbDTO db = session.getSessionID(sessionID, sessionDBID, sessionDBPW, userId);

		return dbmsSQL.getIndexDetailsColumns(dto, db);
	}

	// 시퀀스 디테일 Info 테이블 검색
	@Override
	public Map<String, Object> getSequenceDetailsInfo(DbObjectDTO dto, String userId) throws JYException {
		DbDTO db = session.getSessionID(sessionID, sessionDBID, sessionDBPW, userId);
		Map<String, Object> info = new HashMap<String, Object>();
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
		info = dbmsSQL.getSequenceDetailsInfo(dto, db);

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
	public List<Map<String, Object>> getViewDetailsColumns(DbObjectDTO dto, String userId) throws JYException {
		DbDTO db = session.getSessionID(sessionID, sessionDBID, sessionDBPW, userId);

		return dbmsSQL.getViewDetailsColumns(dto, db);
	}

	// 뷰 디테일 Columns 테이블 검색
	@Override
	public List<Map<String, Object>> getViewDetailsScript(DbObjectDTO dto, String userId) throws JYException {
		DbDTO db = session.getSessionID(sessionID, sessionDBID, sessionDBPW, userId);

		return dbmsSQL.getViewDetailsScript(dto, db);
	}

	// 펀션 디테일 Code 테이블 검색
	@Override
	public List<Map<String, Object>> getDetailsCode(DbObjectDTO dto, String userId) throws JYException {
		DbDTO db = session.getSessionID(sessionID, sessionDBID, sessionDBPW, userId);

		return dbmsSQL.getDetailsCode(dto, db);
	}

}
