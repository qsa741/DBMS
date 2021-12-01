package com.project.dbms.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;

import com.project.dbms.dto.ChartDataSetDTO;
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

		// Column Tree Node 세팅
		TreeDTO childColumn = new TreeDTO();
		childColumn.setId("COLUMNS");
		childColumn.setText("Column (" + column.size() + ")");
		childColumn.setState("closed");
		childColumn.setIconCls("tree-column");
		
		// Constraint Tree Node 세팅
		TreeDTO childConstraint = new TreeDTO();
		childConstraint.setId("CONSTRAINTS");
		childConstraint.setText("Constraint (" + constraint.size() + ")");
		childConstraint.setState("closed");
		childConstraint.setIconCls("tree-constraint");
		
		// Index Tree Node 세팅
		TreeDTO childIndex = new TreeDTO();
		childIndex.setId("INDEXS");
		childIndex.setText("Index (" + index.size() + ")");
		childIndex.setState("closed");
		childIndex.setIconCls("tree-index");

		List<TreeDTO> colTreeList = new ArrayList<>();
		List<TreeDTO> conTreeList = new ArrayList<>();
		List<TreeDTO> idxTreeList = new ArrayList<>();

		// Column은 한개 이상 필수로 존재하므로 바로 for문 실행
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
		for (int i = 1; i < sql.length(); i++) {
			if (i == cursor) {
				break;
			}
			if (sql.charAt(i - 1) == ';') {
				count++;
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
	
	// 차트에 들어가는 연도 구하기
	@Override
	public List<String> getChartYears() {
		return dbmsSQL.getChartYears();
	}
	
	// 차트에 해당 연도 데이터가 있는 월 구하기
	@Override
	public List<String> getChartMonth(String year) {
		return dbmsSQL.getChartMonth(year);
	}
	
	// mChart 정보 세팅 : 선택된 연도 데이터 정리
	@Override
	public Map<String, Object> mChartInfo(String year) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		
		// action별로 데이터 생성
		List<JSONObject> create = dbmsSQL.getActionData(year, "C");
		List<JSONObject> read = dbmsSQL.getActionData(year, "R");
		List<JSONObject> update = dbmsSQL.getActionData(year, "U");
		List<JSONObject> delete = dbmsSQL.getActionData(year, "D");
		
		String[] monthArray = {"01","02","03","04","05","06","07","08","09","10","11","12"};
		
		// 디자인 관련 초기값 생성자로 세팅
		ChartDataSetDTO createDataSet = new ChartDataSetDTO("Create","white","red",2);
		ChartDataSetDTO readDataSet = new ChartDataSetDTO("Read","white","yellow",2);
		ChartDataSetDTO updateDataSet = new ChartDataSetDTO("Update","white","blue",2);
		ChartDataSetDTO deleteDataSet = new ChartDataSetDTO("Delete","white","black",2);
		
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		data.add(mChartDataSet(create, createDataSet));
		data.add(mChartDataSet(read, readDataSet));
		data.add(mChartDataSet(update, updateDataSet));
		data.add(mChartDataSet(delete, deleteDataSet));
		
		result.put("labels", monthArray);
		result.put("datasets", data);
		
		return result;
	}

	// mChart에 들어갈 DataSet 세팅
	@Override
	public Map<String, Object> mChartDataSet(List<JSONObject> list, ChartDataSetDTO dto) throws Exception {
		// 1월 ~ 12월
		int[] monthCount = new int[12];
		
		for(JSONObject json : list) {
			int month = json.getInt("MONTH");
			monthCount[month] += json.getInt("COUNT");
		}
		
		List<Integer> data = new ArrayList<Integer>();
		
		for(int num : monthCount) {
			data.add(num);
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("label", dto.getLabel());
		map.put("backgroundColor", dto.getBackgroundColor());
		map.put("borderColor", dto.getBorderColor());
		map.put("borderWidth", dto.getBorderWidth());
		map.put("data", data);
		
		return map;
	}
	
	// dChart 차트 정보 세팅
	@Override
	public Map<String, Object> dChartInfo(String year, String month) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		
		// action별로 데이터 세팅
		List<JSONObject> create = dbmsSQL.getActionData(year, month, "C");
		List<JSONObject> read = dbmsSQL.getActionData(year, month, "R");
		List<JSONObject> update = dbmsSQL.getActionData(year, month, "U");
		List<JSONObject> delete = dbmsSQL.getActionData(year, month, "D");
		
		List<String> labels = new ArrayList<String>();
		// 31일까지 있는 달
		String[] monthArray = {"01", "03", "05", "07", "08", "10", "12"};
		int day = 0;
		
		if(Arrays.stream(monthArray).anyMatch(month::equals)) {
			day = 31;
		// 2월 (28일까지)
		} else if(month.equals("02")){
			day = 28;
			// 윤년
			if(Integer.parseInt(year)/4 == 0) {
				day += 1;
			}
		// 나머지 월
		} else {
			day = 30;
		}
		
		// 1자리수의 숫자 앞에 "0" 추가
		for(int i = 1; i <= day; i++) {
			if(i/10.0 < 1) {
				labels.add("0" + i);
			} else {
				labels.add("" + i);
			}
		}
		
		// 디자인 관련 초기값 생성자로 세팅
		ChartDataSetDTO createDataSet = new ChartDataSetDTO("Create","white","red",2);
		ChartDataSetDTO readDataSet = new ChartDataSetDTO("Read","white","yellow",2);
		ChartDataSetDTO updateDataSet = new ChartDataSetDTO("Update","white","blue",2);
		ChartDataSetDTO deleteDataSet = new ChartDataSetDTO("Delete","white","black",2);
		
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		data.add(dChartDataSet(create, day, createDataSet));
		data.add(dChartDataSet(read, day, readDataSet));
		data.add(dChartDataSet(update, day, updateDataSet));
		data.add(dChartDataSet(delete, day, deleteDataSet));
		
		result.put("labels", labels);
		result.put("datasets", data);
		
		return result;
	}
	
	// dChart에 들어갈 데이터 세팅
	@Override
	public Map<String, Object> dChartDataSet(List<JSONObject> list, int days, ChartDataSetDTO dto) throws Exception {
		int[] dayCount = new int[days];
		
		for(JSONObject json : list) {
			int day = json.getInt("DAY");
			dayCount[day] += json.getInt("COUNT");
		}
		
		List<Integer> data = new ArrayList<Integer>();
		
		for(int num : dayCount) {
			data.add(num);
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("label", dto.getLabel());
		map.put("backgroundColor", dto.getBackgroundColor());
		map.put("borderColor", dto.getBorderColor());
		map.put("borderWidth", dto.getBorderWidth());
		map.put("data", data);
		
		return map;
	}
	
}
