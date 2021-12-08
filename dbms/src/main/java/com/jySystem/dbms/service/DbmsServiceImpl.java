package com.jySystem.dbms.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;

import com.jySystem.dbms.dto.ChartDataSetDTO;
import com.jySystem.dbms.dto.DbDTO;
import com.jySystem.dbms.dto.LoadObjectDTO;
import com.jySystem.dbms.dto.ObjectDTO;
import com.jySystem.dbms.dto.TreeDTO;
import com.jySystem.dbms.sql.DbmsSQL;

@Service
public class DbmsServiceImpl implements DbmsService {

	@Autowired
	private DbmsSQL dbmsSQL;

	// DB 커넥션 테스트
	@Override
	public boolean connectionTest(DbDTO dto) throws Exception {
		return dbmsSQL.connectionTest(dto);
	}
	
	// 모든 스키마 정보 불러오기
	@Override
	public List<TreeDTO> getAllSchemas(DbDTO dto) throws ClassNotFoundException, SQLException {
		// DB 아이디가 없을때 null 리턴
		if (dto.getDbId().equals("")) {
			return null;
		}
		return dbmsSQL.allSchemas(dto);
	}

	// 스키마 내부 항목 불러오기
	@Override
	public List<TreeDTO> schemaInfo(String schema, DbDTO dto) throws ClassNotFoundException, SQLException {
		List<TreeDTO> treeList = new ArrayList<TreeDTO>();

		TreeDTO table = dbmsSQL.schemaInfo(schema, dto, "TABLE");
		TreeDTO view = dbmsSQL.schemaInfo(schema, dto, "VIEW");
		TreeDTO synonym = dbmsSQL.schemaInfo(schema, dto, "SYNONYM");
		TreeDTO function = dbmsSQL.schemaInfo(schema, dto, "FUNCTION");
		function.setIconCls("tree-function");
		TreeDTO procedure = dbmsSQL.schemaInfo(schema, dto, "PROCEDURE");
		procedure.setIconCls("tree-procedure");
		TreeDTO pvmPackage = dbmsSQL.schemaInfo(schema, dto, "PACKAGE");
		pvmPackage.setIconCls("tree-package");
		TreeDTO type = dbmsSQL.schemaInfo(schema, dto, "TYPE");
		type.setIconCls("tree-type");
		TreeDTO trigger = dbmsSQL.schemaInfo(schema, dto, "TRIGGER");
		TreeDTO index = dbmsSQL.schemaInfo(schema, dto, "INDEX");
		TreeDTO sequence = dbmsSQL.schemaInfo(schema, dto, "SEQUENCE");
		TreeDTO dbLink = dbmsSQL.schemaInfo(schema, dto, "DBLINK");
		TreeDTO mView = dbmsSQL.schemaInfo(schema, dto, "MVIEW");
		TreeDTO mViewLog = dbmsSQL.schemaInfo(schema, dto, "MVIEWLOG");
		TreeDTO job = dbmsSQL.schemaInfo(schema, dto, "JOB");
		TreeDTO library = dbmsSQL.schemaInfo(schema, dto, "LIBRARY");
		TreeDTO pvm = dbmsSQL.schemaInfo(schema, dto, "PVM");

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
	public List<TreeDTO> objectInfo(ObjectDTO object, DbDTO dto) throws ClassNotFoundException, SQLException {
		// 뒤에 붙은 S 빼고 보내기
		object.setObject(object.getObject().substring(0, object.getObject().length() - 1));

		return dbmsSQL.objectInfo(object, dto);
	}

	// 테이블 정보 불러오기
	@Override
	public Map<String, Object> loadObject(LoadObjectDTO dto, DbDTO dbDto) throws ClassNotFoundException, SQLException {
		Map<String, Object> map = new HashMap<>();

		if (dbDto.getDbId() != null) {
			if (dto.getObjectType().equals("TABLE")) {
				List<Map<String, Object>> rows = dbmsSQL.loadObjectTable(dto, dbDto);
				List<Map<String, Object>> column = dbmsSQL.selectTableChild("column", dto, dbDto);

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
	public Map<String, Object> getTableChildren(LoadObjectDTO dto, DbDTO dbDto) throws ClassNotFoundException, SQLException {
		Map<String, Object> map = new HashMap<String, Object>();
		List<TreeDTO> children = new ArrayList<TreeDTO>();
		List<Map<String, Object>> column = dbmsSQL.selectTableChild("column", dto, dbDto);
		List<Map<String, Object>> index = dbmsSQL.selectTableChild("index", dto, dbDto);
		List<Map<String, Object>> constraint = dbmsSQL.selectTableChild("constraint", dto, dbDto);

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
		if(constraint.size() != 0) {
			childConstraint.setState("closed");
		}
		childConstraint.setIconCls("tree-constraint");
		
		// Index Tree Node 세팅
		TreeDTO childIndex = new TreeDTO();
		childIndex.setId("INDEXS");
		childIndex.setText("Index (" + index.size() + ")");
		if(index.size() != 0) {
			childIndex.setState("closed");
		}
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
	public Map<String, Object> schemaDetailsInfo(String schema, DbDTO dto) throws ClassNotFoundException, SQLException {

		Map<String, Object> info = new HashMap<String, Object>();
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();

		info = dbmsSQL.schemaDetailsInfo(schema, dto);

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
	public List<Map<String, Object>> schemaDetailsRoleGrants(String schema, DbDTO dto) throws ClassNotFoundException, SQLException {
		return dbmsSQL.schemaDetailsRoleGrants(schema, dto);
	}

	// 스키마 디테일 System Privileges 테이블 검색
	@Override
	public List<Map<String, Object>> schemaDetailsSystemPrivileges(String schema, DbDTO dto) throws ClassNotFoundException, SQLException {
		return dbmsSQL.schemaDetailsSystemPrivileges(schema, dto);
	}

	// 스키마 디테일 Extends 테이블 검색
	@Override
	public List<Map<String, Object>> schemaDetailsExtents(String schema, DbDTO dto) throws ClassNotFoundException, SQLException {
		return dbmsSQL.schemaDetailsExtents(schema, dto);
	}

	// 테이블 디테일 Table 테이블 검색
	@Override
	public Map<String, Object> tableDetailsTable(String table, String schema, DbDTO dto) throws ClassNotFoundException, SQLException {
		Map<String, Object> info = new HashMap<String, Object>();
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
		info = dbmsSQL.tableDetailsTable(table, schema, dto);

		Set<String> parameters = info.keySet();
		for (String s : parameters) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("VALUE", info.get(s));
			s = s.replace("_", " ");
			String parameter = s.substring(0,1) + s.substring(1).toLowerCase();
			map.put("PARAMETER", parameter);
			rows.add(map);
		}
		result.put("rows", rows);
		
		return result;
	}
	
	// 테이블 디테일 Columns 테이블 검색
	@Override
	public List<Map<String, Object>> tableDetailsColumns(String table, String schema, DbDTO dto)
			throws ClassNotFoundException, SQLException {
		return dbmsSQL.tableDetailsColumns(table, schema, dto);
	}

	// 테이블 디테일 Indexes Top 테이블 검색
	@Override
	public List<Map<String, Object>> tableDetailsIndexesTop(String table, String schema, DbDTO dto)
			throws ClassNotFoundException, SQLException {
		return dbmsSQL.tableDetailsIndexesTop(table, schema, dto);
	}
	
	// 테이블 디테일 Indexes Bottom 테이블 검색
	@Override
	public Map<String, Object> tableDetailsIndexesBottom(String indexName, DbDTO dto)
			throws ClassNotFoundException, SQLException {
		Map<String, Object> info = new HashMap<String, Object>();
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
		info = dbmsSQL.tableDetailsIndexesBottom(indexName, dto);

		Set<String> parameters = info.keySet();
		for (String s : parameters) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("VALUE", info.get(s));
			s = s.replace("_", " ");
			String parameter = s.substring(0,1) + s.substring(1).toLowerCase();
			map.put("PARAMETER", parameter);
			rows.add(map);
		}
		result.put("rows", rows);
		
		return result;
	}
	
	// 테이블 디테일 Constraints 테이블 검색
	@Override
	public List<Map<String, Object>> tableDetailsConstraints(String table, String schema, DbDTO dto)
			throws ClassNotFoundException, SQLException {
		return dbmsSQL.tableDetailsConstraints(table, schema, dto);
	}
	
	// 인덱스 디테일 Columns 테이블 검색
	@Override
	public List<Map<String, Object>> indexDetailsColumns(String indexName, DbDTO dto)
			throws ClassNotFoundException, SQLException {
		return dbmsSQL.indexDetailsColumns(indexName, dto);
	}
	
	// 시퀀스 디테일 Info 테이블 검색
	@Override
	public Map<String, Object> sequenceDetailsInfo(String sequenceName, DbDTO dto)
			throws ClassNotFoundException, SQLException {
		Map<String, Object> info = new HashMap<String, Object>();
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
		info = dbmsSQL.sequenceDetailsInfo(sequenceName, dto);

		Set<String> parameters = info.keySet();
		for (String s : parameters) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("VALUE", info.get(s));
			s = s.replace("_", " ");
			String parameter = s.substring(0,1) + s.substring(1).toLowerCase();
			map.put("PARAMETER", parameter);
			rows.add(map);
		}
		result.put("rows", rows);
		
		return result;
	}
	
	// 현재 SQL 한줄 실행
	@Override
	public Map<String, Object> runCurrentSQL(String sql, int cursor, DbDTO dto) throws SQLException{
		if (dto.getDbId() == null) {
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

		return dbmsSQL.runCurrentSQL(array[count], type, 1, dto);
	}

	// 전체 SQL문 실행
	@Override
	public List<Map<String, Object>> runAllSQL(String sqls, DbDTO dto) throws SQLException {
		if (dto.getDbId() == null) {
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
			list.add(dbmsSQL.runCurrentSQL(sql, type, count, dto));
		}

		return list;
	}
	
	// 차트에 들어가는 연도 구하기
	@Override
	public List<String> getChartYears() throws ClassNotFoundException, SQLException {
		return dbmsSQL.getChartYears();
	}
	
	// 차트에 해당 연도 데이터가 있는 월 구하기
	@Override
	public List<String> getChartMonth(String year) throws ClassNotFoundException, SQLException {
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
		ChartDataSetDTO readDataSet = new ChartDataSetDTO("Read","white","orange",2);
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
			int month = json.getInt("MONTH") - 1;
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
		ChartDataSetDTO readDataSet = new ChartDataSetDTO("Read","white","orange",2);
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
			int day = json.getInt("DAY") - 1;
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
