$(document).ready(function() {
	// 처음 오픈시 전체 스키마 불러오기
	$('#dbmsTree').tree({
		url: '/dbms/allSchemas',
		height: '100%'
	});

	// 중앙 탭 높이 설정
	$('#centerTabs').tabs({
		height: '100%'
	});


	$('#script').keydown(function(e) {
		// tab키 입력
		if (e.keyCode === 9) {
			// 커서의 위치 저장
			var start = this.selectionStart;
			var end = this.selectionEnd;

			var $this = $(this);
			var value = $this.val();

			// 시작 위치와 끝 위치 사이에 '\t' 넣기
			$this.val(value.substring(0, start)
				+ "\t"
				+ value.substring(end));

			// 커서 위치 탭 다음으로 바꾸기
			this.selectionStart = this.selectionEnd = start + 1;

			// 기본 이벤트 막기
			e.preventDefault();
		}
		// Ctrl + Enter 입력
		if (e.keyCode == 13 && e.ctrlKey) {
			runCurrentSQL(this.selectionEnd);
		}
		// F5 입력
		if (e.keyCode == 116) {
			runAllSQL();
			e.preventDefault();
		}
	});
	// 마우스 클릭시 커서 위치 저장
	var cursor = 0;
	$('#script').click(function(e) {
		cursor = this.selectionEnd;
	});

	// 마지막으로 선택된 SQL 한줄 실행
	$('#runCurrentSQL').click(function() {
		runCurrentSQL(cursor);
	});

	// SQL 전체 실행
	$('#runAllSQL').click(function() {
		runAllSQL();
	});

	// 이전 클릭 위치 저장
	var saveClick;
	// 클릭 이벤트	
	$('#dbmsTree').tree({
		// 클릭시 ID에 맞는 디테일 정보 불러오기
		onClick: function(node) {
			if (node.id == 'SCHEMA' && node.text != saveClick) {
				$('#include').empty();
				$('#include').load("/dbms/schemaDetails");
				saveClick = node.text;
			}
		},
		// 더블클릭시 ID에 맞는 하위 목록 불러오기
		onDblClick: function(node) {
			if (node.id == 'SCHEMA') {
				getSchemaInfo(node);
			} else if (node.id == 'TABLE') {
				loadObjectTable(node);
			} else {
				getObjectInfo(node);
			}
			folderToggle(node);
		}
	});

});

// 스키마 하위 목록 불러오기
function getSchemaInfo(node) {
	if (node.children == null) {
		$.ajax({
			url: '/dbms/schemaInfo',
			data: {
				schema: node.text
			},
			dataType: 'json',
			success: function(data) {
				if(data == null) {
					sessionOut();
				} else {
					$('#dbmsTree').tree('append', {
						parent: node.target,
						data: data
					});
				}
			}
		});
	}
}

// 오브젝트 하위 목록 불러오기
function getObjectInfo(node) {
	var root = $('#dbmsTree').tree('getRoot', node.target);
	if (node.children == null) {
		$.ajax({
			url: '/dbms/objectInfo',
			data: {
				id: root.text,
				object: node.id
			},
			dataType: 'json',
			success: function(data) {
				if(data == null) {
					sessionOut();
				} else {
					$('#dbmsTree').tree('append', {
						parent: node.target,
						data: data
					});
				}
			}
		});
	}
}

// 중앙 div 탭 추가하기
function addTab(title, url) {
	if ($('#centerTabs').tabs('exists', title)) {
		$('#centerTabs').tabs('select', title);
	} else {
		// html include 방식으로 구현
		var content = '<iframe scrolling="auto" frameborder="0"  src="' + url + '" style="width:100%;height:100%;"></iframe>';
		$('#centerTabs').tabs('add', {
			title: title,
			content: content,
			closable: true,
		});
	}
}

// 테이블 정보 불러오기
function loadObjectTable(node) {
	var root = $('#dbmsTree').tree('getRoot', node.target);
	addTab(node.text, '/dbms/loadTable?schema=' + root.text + '&objectType=' + node.id + '&objectName=' + node.text);
	getTableChildren(node);
}

// 테이블 자식 정보 불러오기 (Column, Constraint, Index)
function getTableChildren(node) {
	// 선택 노드가 Table이고 하위 항목이 없을시 실행
	if (node.id == 'TABLE' && node.children == null) {
		var root = $('#dbmsTree').tree('getRoot', node.target);
		$.ajax({
			ur: '/dbms/getTableChildren',
			data: {
				schema: root.text,
				objectType: node.id,
				objectName: node.text
			},
			dataType: 'json',
			success: function(data) {
				if(data == null) {
					sessionOut();
				} else {
					$('#dbmsTree').tree('append', {
						parent: node.target,
						data: data.children
					});
				}
			}
		});
	}
}

// 열린 폴더 닫기
function folderToggle(node) {
	$('#dbmsTree').tree('toggle', node.target);
}

// SQL 한줄 실행
function runCurrentSQL(cursor) {
	$('#dbmsOutput').datagrid('loadData', []);
	var text = $('#script').val();
	$.ajax({
		url: '/dbms/runCurrentSQL',
		data: {
			sql: text,
			cursor: cursor
		},
		dataType: 'json',
		success: function(data) {
			scriptResult(data);
		},
		error: function(e) {
			sessionOut();
		}
	});
}

// SQL 전체 실행
function runAllSQL() {
	$('#dbmsOutput').datagrid('loadData', []);
	var text = $('#script').val();
	$.ajax({
		url: '/dbms/runAllSQL',
		data: {
			sqls: text
		},
		dataType: 'json',
		success: function(dataArray) {
			for (var i = 0; i < dataArray.length; i++) {
				scriptResult(dataArray[i]);
			}
		},
		error: function(e) {
			sessionOut();
		}
	});
}

// Script 결과 출력
function scriptResult(data) {
	// SQL문이 SELECT일때
	if (data.type == 'SELECT') {
		consoleAddTab(data);
	// SELECT 외 다른 명령어 처리
	} else {
		dbmsOutput(data.data);
	}
}

// SELECT 검색 시 콘솔 탭 추가
function consoleAddTab(data) {
	// 같은 검색일 시 삭제 후 실행
	if ($('#consoleTabs').tabs('exists', data.sql)) {
		$('#consoleTabs').tabs('close', data.sql);
		consoleAddTab(data);
	} else {
		$('#consoleTabs').tabs('add', {
			title: data.sql,
			content: '<div id="' + data.key + '" class="easyui-datagrid" singleSelect="true" style="width:100%;height:100%;"></div>',
			closable: true
		});
		var columns = []
		var obj = data.cols;
		for (var value in obj) {
			var menuItem = {
				field: obj[value],
				title: obj[value],
				width: 100,
			};
			columns.push(menuItem);
		}
		$('#' + data.key).datagrid({
			columns: [columns],
			data: data.data
		});
	}
}

// CREATE, DROP, INSERT, DELETE, UPDATE 실행 시 DBMS_OUTPUT 텍스트 추가
function dbmsOutput(data) {
	$('#consoleTabs').tabs('select', 'DBMS_OUTPUT');
	$('#dbmsOutput').datagrid('appendRow', {
		Row: data[0].Row,
		DbmsOutput: data[0].DbmsOutput,
		ExecutionTime: data[0].ExecutionTime
	});
}

// 세션이 사라지면 로그인페이지로 유도
function sessionOut() {
	alert('세션이 만료되었습니다. 로그인이 필요합니다.');
	window.location='/dbms/signOut';
}

