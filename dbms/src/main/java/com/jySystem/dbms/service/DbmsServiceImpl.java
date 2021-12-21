package com.jySystem.dbms.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.jySystem.common.config.SessionConfig;
import com.jySystem.dbms.dto.DbDTO;
import com.jySystem.dbms.dto.DbObjectDTO;
import com.jySystem.dbms.dto.TreeDTO;
import com.jySystem.dbms.sql.DbmsSQL;
import com.jySystem.exception.JYException;

@Service
public class DbmsServiceImpl implements DbmsService {

	@Value("${dbms.properties.session-id}")
	private String sessionID;
	@Value("${dbms.properties.session-db-id}")
	private String sessionDBID;
	@Value("${dbms.properties.session-db-pw}")
	private String sessionDBPW;

	@Autowired
	private DbmsSQL dbmsSQL;

	private SessionConfig session = new SessionConfig();

	// DB 커넥션 테스트
	@Override
	public boolean connectionTest(DbDTO dto) throws JYException {
		return dbmsSQL.connectionTest(dto);
	}

	// 모든 스키마 정보 불러오기
	@Override
	public List<TreeDTO> getAllSchemas(String userId) throws JYException {
		DbDTO db = session.getSessionID(sessionID, sessionDBID, sessionDBPW, userId);

		// DB 아이디가 없을때 null 리턴
		if (db.getDbId() == null || Objects.equals(db.getDbId(), "")) {
			return null;
		}
		return dbmsSQL.allSchemas(db);
	}

	// 스키마 내부 항목 불러오기
	@Override
	public List<TreeDTO> getSchemaInfo(DbObjectDTO dto, String userId) throws JYException {
		List<TreeDTO> treeList = new ArrayList<TreeDTO>();
		DbDTO db = session.getSessionID(sessionID, sessionDBID, sessionDBPW, userId);

		TreeDTO table = dbmsSQL.getSchemaInfo(dto.getSchemaName(), "TABLE", db);
		TreeDTO view = dbmsSQL.getSchemaInfo(dto.getSchemaName(), "VIEW", db);
		TreeDTO synonym = dbmsSQL.getSchemaInfo(dto.getSchemaName(), "SYNONYM", db);
		TreeDTO function = dbmsSQL.getSchemaInfo(dto.getSchemaName(), "FUNCTION", db);
		function.setIconCls("tree-function");
		TreeDTO procedure = dbmsSQL.getSchemaInfo(dto.getSchemaName(), "PROCEDURE", db);
		procedure.setIconCls("tree-procedure");
		TreeDTO pvmPackage = dbmsSQL.getSchemaInfo(dto.getSchemaName(), "PACKAGE", db);
		pvmPackage.setIconCls("tree-package");
		TreeDTO type = dbmsSQL.getSchemaInfo(dto.getSchemaName(), "TYPE", db);
		type.setIconCls("tree-type");
		TreeDTO trigger = dbmsSQL.getSchemaInfo(dto.getSchemaName(), "TRIGGER", db);
		TreeDTO index = dbmsSQL.getSchemaInfo(dto.getSchemaName(), "INDEX", db);
		TreeDTO sequence = dbmsSQL.getSchemaInfo(dto.getSchemaName(), "SEQUENCE", db);
		TreeDTO dbLink = dbmsSQL.getSchemaInfo(dto.getSchemaName(), "DBLINK", db);
		TreeDTO mView = dbmsSQL.getSchemaInfo(dto.getSchemaName(), "MVIEW", db);
		TreeDTO mViewLog = dbmsSQL.getSchemaInfo(dto.getSchemaName(), "MVIEWLOG", db);
		TreeDTO job = dbmsSQL.getSchemaInfo(dto.getSchemaName(), "JOB", db);
		TreeDTO library = dbmsSQL.getSchemaInfo(dto.getSchemaName(), "LIBRARY", db);
		TreeDTO pvm = dbmsSQL.getSchemaInfo(dto.getSchemaName(), "PVM", db);

		List<TreeDTO> pvmChildren = new ArrayList<TreeDTO>();
		pvmChildren.add(function);
		pvmChildren.add(procedure);
		pvmChildren.add(pvmPackage);
		pvmChildren.add(type);
		pvm.setChildren(pvmChildren);

		treeList.add(table);
		treeList.add(view);
		treeList.add(synonym);
		treeList.add(pvm);
		treeList.add(trigger);
		treeList.add(index);
		treeList.add(sequence);
		treeList.add(dbLink);
		treeList.add(mView);
		treeList.add(mViewLog);
		treeList.add(job);
		treeList.add(library);

		return treeList;
	}

	// 오브젝트 불러오기
	@Override
	public List<TreeDTO> getObjectInfo(DbObjectDTO dto, String userId) throws JYException {
		DbDTO db = session.getSessionID(sessionID, sessionDBID, sessionDBPW, userId);

		// 뒤에 붙은 Folder 빼고 보내기
		dto.setObjectType(dto.getObjectType().replace("Folder", ""));

		return dbmsSQL.getObjectInfo(dto, db);
	}

	// 테이블 정보 불러오기
	@Override
	public Map<String, Object> loadObject(DbObjectDTO dto, String userId) throws JYException {
		Map<String, Object> map = new HashMap<>();
		DbDTO db = session.getSessionID(sessionID, sessionDBID, sessionDBPW, userId);

		if (db.getDbId() != null) {
			if (Objects.equals(dto.getObjectType(), "TABLE")) {
				List<Map<String, Object>> rows = dbmsSQL.loadObjectTable(dto, db);
				List<Map<String, Object>> column = dbmsSQL.selectTableChild("column", dto, db);

				List<String> columnName = new ArrayList<>();
				for (Map<String, Object> c : column) {
					columnName.add((String) c.get("COLUMN_NAME"));
				}

				Map<String, Object> data = new HashMap<>();

				data.put("rows", rows);
				data.put("total", rows.size());
				map.put("columns", columnName);
				map.put("data", data);
				map.put("key", dto.getSchemaName() + dto.getTableName());
				map.put("title", dto.getTableName());
			} else {
				return null;
			}
		}

		return map;
	}

	// 테이블 자식 정보 불러오기
	@Override
	public Map<String, Object> getTableChildren(DbObjectDTO dto, String userId) throws JYException {
		DbDTO db = session.getSessionID(sessionID, sessionDBID, sessionDBPW, userId);
		Map<String, Object> map = new HashMap<String, Object>();
		List<TreeDTO> children = new ArrayList<TreeDTO>();
		List<Map<String, Object>> column = dbmsSQL.selectTableChild("column", dto, db);
		List<Map<String, Object>> index = dbmsSQL.selectTableChild("index", dto, db);
		List<Map<String, Object>> constraint = dbmsSQL.selectTableChild("constraint", dto, db);

		// Column Tree Node 세팅
		TreeDTO childColumn = new TreeDTO("COLUMNS", "Column (" + column.size() + ")");
		childColumn.setState("closed");
		childColumn.setIconCls("tree-column");

		// Constraint Tree Node 세팅
		TreeDTO childConstraint = new TreeDTO("CONSTRAINTS", "Constraint (" + constraint.size() + ")");
		if (constraint.size() != 0) {
			childConstraint.setState("closed");
		}
		childConstraint.setIconCls("tree-constraint");

		// Index Tree Node 세팅
		TreeDTO childIndex = new TreeDTO("INDEXS", "Index (" + index.size() + ")");
		if (index.size() != 0) {
			childIndex.setState("closed");
		}
		childIndex.setIconCls("tree-index");

		List<TreeDTO> colTreeList = new ArrayList<>();
		List<TreeDTO> conTreeList = new ArrayList<>();
		List<TreeDTO> idxTreeList = new ArrayList<>();

		// Column은 한개 이상 필수로 존재하므로 바로 for문 실행
		for (Map<String, Object> c : column) {
			String text = (String) c.get("COLUMN_NAME") + " : " + (String) c.get("DATA_TYPE");
			if (Objects.equals(c.get("DATA_TYPE"), "VARCHAR")) {
				text += " (" + c.get("DATA_LENGTH") + ")";
			}
			TreeDTO tree = new TreeDTO("COLUMN", text);
			tree.setIconCls("tree-column");
			colTreeList.add(tree);
		}
		childColumn.setChildren(colTreeList);

		if (constraint.size() > 0) {
			for (Map<String, Object> c : constraint) {
				TreeDTO tree = new TreeDTO("CONSTRAINT", (String) c.get("CONSTRAINT_NAME"));
				tree.setIconCls("tree-constraint");
				conTreeList.add(tree);
			}
			childConstraint.setChildren(conTreeList);
		}

		if (index.size() > 0) {
			for (Map<String, Object> i : index) {
				TreeDTO tree = new TreeDTO("INDEX", (String) i.get("INDEX_NAME"));
				tree.setIconCls("tree-index");
				idxTreeList.add(tree);
			}
			childIndex.setChildren(idxTreeList);
		}

		children.add(childColumn);
		children.add(childConstraint);
		children.add(childIndex);

		map.put("children", children);

		return map;
	}

	// 현재 SQL 한줄 실행
	@Override
	public Map<String, Object> runCurrentSQL(String sql, int cursor, String userId) throws JYException {
		DbDTO db = session.getSessionID(sessionID, sessionDBID, sessionDBPW, userId);
		if (db.getDbId() == null) {
			return null;
		}
		sql = sql.replace("\r", "").replace("\n", "").replace("\t", "");
		String[] array = sql.split(";", -1);
		
		int count = 0;
		// 커서의 위치 계산 후 SQL문 실행
		for (int i = 1; i < sql.length(); i++) {
			if (i == cursor) {
				break;
			}
			if (sql.charAt(i - 1) == ';') {
				count++;
			}
		}

		String type = array[count].split(" ")[0].toUpperCase();

		return dbmsSQL.runCurrentSQL(array[count], type, 1, db);
	}

	// 전체 SQL문 실행
	@Override
	public List<Map<String, Object>> runAllSQL(String sqls, String userId) throws JYException {
		DbDTO db = session.getSessionID(sessionID, sessionDBID, sessionDBPW, userId);
		if (db.getDbId() == null) {
			return null;
		}
		sqls = sqls.replace("\r", "").replace("\n", "").replace("\t", "");
		String[] array = sqls.split(";", -1);
		int last = array.length;
		
		if(array[array.length - 1].length() < 4) {
			last -= 1;
		}
		
		List<Map<String, Object>> list = new ArrayList<>();
		int count = 0;
		
		for (int i = 0; i < last; i++) {
			String sql = array[i];
			String type = sql.split(" ")[0].toUpperCase();
			
			// SELECT문이 아닐시 DBMS OUTPUT에 적용할 Row count 사용
			if (!Objects.equals(type, "SELECT")) {
				count++;
			}
			list.add(dbmsSQL.runCurrentSQL(sql, type, count, db));
		}

		return list;
	}

}
