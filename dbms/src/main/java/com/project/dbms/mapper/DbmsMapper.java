package com.project.dbms.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.project.dbms.dto.LoadObjectDTO;
import com.project.dbms.dto.ObjectDTO;
import com.project.dbms.dto.TreeDTO;

@Mapper
public interface DbmsMapper {
	
	// 모든 스키마 불러오기
	List<TreeDTO> allSchemas();
	
	// 스키마 상세 항목 불러오기
	int schemaInfoTable(String schema);
	int schemaInfoView(String schema);
	int schemaInfoSynonym(String schema);
	int schemaInfoFunction(String schema);
	int schemaInfoProcedure(String schema);
	int schemaInfoPackage(String schema);
	int schemaInfoType(String schema);
	int schemaInfoTrigger(String schema);
	int schemaInfoIndex(String schema);
	int schemaInfoSequence(String schema);
	int schemaInfoDbLink(String schema);
	int schemaInfoMView(String schema);
	int schemaInfoMViewLog(String schema);
	int schemaInfoJob(String schema);
	int schemaInfoLibrary(String schema);
	
	// 오브젝트 불러오기
	List<TreeDTO> objectInfo(ObjectDTO object);
	
	// 테이블 정보 불러오기
	List<Map<String, Object>> loadObjectTable(LoadObjectDTO dto);
	
	// 테이블 컬럼 검색
	List<Map<String, Object>>  selectTableColumns(LoadObjectDTO dto);
	
	// 테이블 인덱스 검색
	List<Map<String, Object>>  selectTableIndex(LoadObjectDTO dto);

	// 테이블 제약조건 검색
	List<Map<String, Object>>  selectTableConstraint(LoadObjectDTO dto);
	
	// 현재 접속중인 유저 검색
	String currentUser();
	
	// 스키마 디테일 정보 테이블 검색
	Map<String, Object> schemaDetailsInfo(String scheme);

	// 내 스키마 디테일 정보 테이블 검색
	Map<String, Object> mySchemaDetailsInfo();
	
	// 스키마 디테일 Role Grants 테이블 검색
	List<Map<String, Object>> schemaDetailsRoleGrants();
}
