package com.project.dbms.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.dbms.dto.LoadObjectDTO;
import com.project.dbms.dto.ObjectDTO;
import com.project.dbms.dto.TreeDTO;
import com.project.dbms.sql.DbmsSQL;

@Service
public class DbmsServiceImpl implements DbmsService {

	@Autowired
	private DbmsSQL dbmsSQL;

	@Autowired
	private UserServiceImpl userService;

	// 모든 스키마 정보 불러오기
	@Override
	public List<TreeDTO> getAllSchemas() {
		// 세션이 없을때 null 리턴
		if (userService.getSessionDbId() == null) {
			return null;
		}
		return dbmsSQL.allSchemas();
	}

	// 스키마 내부 항목 불러오기
	@Override
	public List<TreeDTO> schemaInfo(String schema) {
		List<TreeDTO> treeList = new ArrayList<TreeDTO>();

		TreeDTO table = dbmsSQL.schemaInfo(schema, "TABLE");
		TreeDTO view = dbmsSQL.schemaInfo(schema, "VIEW");
		TreeDTO synonym = dbmsSQL.schemaInfo(schema, "SYNONYM");
		TreeDTO function = dbmsSQL.schemaInfo(schema, "FUNCTION");
		function.setIconCls("tree-function");
		TreeDTO procedure = dbmsSQL.schemaInfo(schema, "PROCEDURE");
		procedure.setIconCls("tree-procedure");
		TreeDTO pvmPackage = dbmsSQL.schemaInfo(schema, "PACKAGE");
		pvmPackage.setIconCls("tree-package");
		TreeDTO type = dbmsSQL.schemaInfo(schema, "TYPE");
		type.setIconCls("tree-type");
		TreeDTO trigger = dbmsSQL.schemaInfo(schema, "TRIGGER");
		TreeDTO index = dbmsSQL.schemaInfo(schema, "INDEX");
		TreeDTO sequence = dbmsSQL.schemaInfo(schema, "SEQUENCE");
		TreeDTO dbLink = dbmsSQL.schemaInfo(schema, "DBLINK");
		TreeDTO mView = dbmsSQL.schemaInfo(schema, "MVIEW");
		TreeDTO mViewLog = dbmsSQL.schemaInfo(schema, "MVIEWLOG");
		TreeDTO job = dbmsSQL.schemaInfo(schema, "JOB");
		TreeDTO library = dbmsSQL.schemaInfo(schema, "LIBRARY");
		TreeDTO pvm = dbmsSQL.schemaInfo(schema, "PVM");

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
	public List<TreeDTO> objectInfo(ObjectDTO object) {
		// 뒤에 붙은 S 빼고 보내기
		object.setObject(object.getObject().substring(0, object.getObject().length() - 1));

		return dbmsSQL.objectInfo(object);
	}

	// 테이블 정보 불러오기
	@Override
	public Map<String, Object> loadObject(LoadObjectDTO dto) {
		Map<String, Object> map = new HashMap<>();

		if (userService.getSessionDbId() != null) {
			if (dto.getObjectType().equals("TABLE")) {
				List<Map<String, Object>> rows = dbmsSQL.loadObjectTable(dto);
				List<Map<String, Object>> column = dbmsSQL.selectTableChild("column", dto);

				List<String> columnName = new ArrayList<>();
				for (Map<String, Object> c : column) {
					columnName.add((String) c.get("COLUMN_NAME"));
				}

				Map<String, Object> data = new HashMap<>();

				data.put("rows", rows);
				data.put("total", rows.size());
				map.put("columns", columnName);
				map.put("data", data);
			}
		} else {
			return null;
		}
		return map;
	}

	// 테이블 자식 정보 불러오기
	@Override
	public Map<String, Object> getTableChildren(LoadObjectDTO dto) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<TreeDTO> children = new ArrayList<TreeDTO>();
		List<Map<String, Object>> column = dbmsSQL.selectTableChild("column", dto);
		List<Map<String, Object>> index = dbmsSQL.selectTableChild("index", dto);
		List<Map<String, Object>> constraint = dbmsSQL.selectTableChild("constraint", dto);

		TreeDTO childColumn = new TreeDTO();
		childColumn.setId("COLUMNS");
		childColumn.setText("Column (" + column.size() + ")");
		childColumn.setState("closed");
		childColumn.setIconCls("tree-column");
		TreeDTO childConstraint = new TreeDTO();
		childConstraint.setId("CONSTRAINTS");
		childConstraint.setText("Constraint (" + constraint.size() + ")");
		childConstraint.setState("closed");
		childConstraint.setIconCls("tree-constraint");
		TreeDTO childIndex = new TreeDTO();
		childIndex.setId("INDEXS");
		childIndex.setText("Index (" + index.size() + ")");
		childIndex.setState("closed");
		childIndex.setIconCls("tree-index");

		List<TreeDTO> colTreeList = new ArrayList<>();
		List<TreeDTO> conTreeList = new ArrayList<>();
		List<TreeDTO> idxTreeList = new ArrayList<>();

		for (Map<String, Object> c : column) {
			TreeDTO tree = new TreeDTO();
			tree.setId("COLUMN");
			tree.setIconCls("tree-column");
			String text = (String) c.get("COLUMN_NAME") + " : " + (String) c.get("DATA_TYPE");
			if (c.get("DATA_TYPE").equals("VARCHAR")) {
				text += " (" + c.get("DATA_LENGTH") + ")";
			}
			tree.setText(text);
			colTreeList.add(tree);
		}
		childColumn.setChildren(colTreeList);

		if (constraint.size() > 0) {
			for (Map<String, Object> c : constraint) {
				TreeDTO tree = new TreeDTO();
				tree.setId("CONSTRAINT");
				tree.setIconCls("tree-constraint");
				tree.setText((String) c.get("CONSTRAINT_NAME"));
				conTreeList.add(tree);
			}
			childConstraint.setChildren(conTreeList);
		}

		if (index.size() > 0) {
			for (Map<String, Object> i : index) {
				TreeDTO tree = new TreeDTO();
				tree.setId("INDEX");
				tree.setIconCls("tree-index");
				tree.setText((String) i.get("INDEX_NAME"));
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

	// 스키마 디테일 Info 데이터
	@Override
	public Map<String, Object> schemaDetailsInfo(String schema) {

		Map<String, Object> info = new HashMap<String, Object>();
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();

		info = dbmsSQL.schemaDetailsInfo(schema);

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
	public List<Map<String, Object>> schemaDetailsRoleGrants(String schema) {
		return dbmsSQL.schemaDetailsRoleGrants(schema);
	}

	// 스키마 디테일 System Privileges 테이블 검색
	@Override
	public List<Map<String, Object>> schemaDetailsSystemPrivileges(String schema) {
		return dbmsSQL.schemaDetailsSystemPrivileges(schema);
	}

	// 스키마 디테일 Extends 테이블 검색
	@Override
	public List<Map<String, Object>> schemaDetailsExtents(String schema) {
		return dbmsSQL.schemaDetailsExtents(schema);
	}

	// 현재 SQL 한줄 실행
	@Override
	public Map<String, Object> runCurrentSQL(String sql, int cursor) {
		if (userService.getSessionDbId() == null) {
			return null;
		}
		sql = sql.replace("\r", "").replace("\n", "").replace("\t", "");
		String[] array = sql.split(";");
		int count = 0;
		// 커서의 위치 계산 후 SQL문 실행
		for (int i = 0; i < sql.length(); i++) {
			if (i == cursor) {
				break;
			}
			if (i != 0) {
				if (sql.charAt(i - 1) == ';') {
					count++;
				}
			}
		}

		String type = array[count].split(" ")[0].toUpperCase();

		return dbmsSQL.runCurrentSQL(array[count], type, 1);
	}

	// 전체 SQL문 실행
	@Override
	public List<Map<String, Object>> runAllSQL(String sqls) {
		if (userService.getSessionDbId() == null) {
			return null;
		}
		sqls = sqls.replace("\r", "").replace("\n", "").replace("\t", "");
		String[] array = sqls.split(";");
		List<Map<String, Object>> list = new ArrayList<>();
		int count = 0;
		for (int i = 0; i < array.length; i++) {
			String sql = array[i];
			String type = sql.split(" ")[0].toUpperCase();
			// SELECT문이 아닐시 DBMS OUTPUT에 적용할 Row count 사용
			if (!type.equals("SELECT")) {
				count++;
			}
			list.add(dbmsSQL.runCurrentSQL(sql, type, count));
		}

		return list;
	}

}
