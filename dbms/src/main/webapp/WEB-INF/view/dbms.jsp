<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>메인</title>
	<script type="text/javascript" src="/resources/easyui/jquery.min.js"></script>
	<script type="text/javascript" src="/resources/easyui/jquery.easyui.min.js"></script>
	<script type="text/javascript" src="/resources/easyui/plugins/jquery.draggable.js"></script>
	<script type="text/javascript" src="/resources/easyui/plugins/jquery.droppable.js"></script>
	<script type="text/javascript" src="/resources/easyui/plugins/jquery.datagrid.js"></script>
	<script type="text/javascript" src="/resources/easyui/plugins/jquery.panel.js"></script>
	<script type="text/javascript" src="/resources/easyui/plugins/jquery.resizable.js"></script>
	<script type="text/javascript" src="/resources/easyui/plugins/jquery.linkbutton.js"></script>
	<script type="text/javascript" src="/resources/js/dbms.js"></script>
	<link rel="stylesheet" type="text/css" href="/resources/css/reset.css">
	<link rel="stylesheet" type="text/css" href="/resources/easyui/themes/icon.css">
	<link rel="stylesheet" type="text/css" href="/resources/easyui/themes/default/easyui.css">
	<link rel="stylesheet" type="text/css" href="/resources/css/dbms.css">
	<link rel="stylesheet" type="text/css" href="/resources/css/detail.css">
</head>
<script type="text/javascript">
	$(document).ready(function() {
		if(${sessionScope.JYDBID eq null}) {
			$('#script').val('DB ID/PW 로그인 확인 후 사용 가능합니다.');
			$('#script').attr("readonly", true);
			$('#scriptToolbar button').attr("disabled", true);
		}
	})
</script>
<body id="mainBody">
	<div id="container">
		<div id="header">
			<div id="user" class="easyui-menubutton" data-options="menu:'#userMenu',hasDownArrow:false,showEvent:'click'">
				<img src="/resources/img/user_icon.png" />
				<div id="userLabel">
					<c:if test="${sessionScope.JYSESSION != null}">
						${sessionScope.JYSESSION}
					</c:if>
					<c:if test="${sessionScope.JYSESSION == null}">
						비회원
					</c:if>
				</div>
				<div id="userBtn">
					<span></span>
					<span></span>
					<span></span>
				</div>
			</div>
			<div id="userMenu">
				<c:if test="${sessionScope.JYSESSION != null}">
					<div onclick="window.location='/dbms/signOut'">로그아웃</div>
					<div onclick="window.location='/dbms/modifyUser'">회원정보 수정</div>
					<div onclick="window.location='/dbms/deleteUser'">회원 탈퇴</div>
				</c:if>
				<c:if test="${sessionScope.JYSESSION == null}">
					<div onclick="window.location='/dbms/signIn'">로그인</div>
					<div onclick="window.location='/dbms/signUp'">회원가입</div>
				</c:if>
			</div>
		</div>
		<div class="easyui-layout" style="width:100%;height:100%;">
			<div class="side easyui-layout" data-options="region:'west',title:'Schema',split:true">
				<div class="top" data-options="region:'center',split:true">
					<ul id="dbmsTree" class="easyui-tree">
					</ul>
				</div>
				<div class="bottom" data-options="region:'south',split:true">
					<div id="include">
					</div>
				</div>
			</div>
			<div class="content easyui-layout" data-options="region:'center',split:true">
				<div class="top"  data-options="region:'center',split:true">
					<div id="centerTabs" class="easyui-tabs">
						<div class="tab" title="Script" style="display:none;">
							<div id="scriptToolbar">
								<button id="runAllSQL" class="easyui-linkbutton" title="전체 실행 (F5)">Run All SQL</button>
								<button id="runCurrentSQL" class="easyui-linkbutton" title="한줄 실행 (Ctrl + Enter)">Run Current SQL</button>
							</div>
							<div id="scriptBody">
								<textarea id="script" spellcheck = "false"></textarea>
							</div>
						</div>
					</div>
				</div>
				<div class="bottom" data-options="region:'south',split:true">
					<div id="console">
						<div id="consoleTabs" class="easyui-tabs" style="width:100%;">
							<div class="tab" title="DBMS_OUTPUT" style="display:none;">
								<table id="dbmsOutput" class="easyui-datagrid" singleSelect="true">
									<thead>
										<tr>
											<th data-options="field:'Row',width:'100px'">Row</th>
											<th data-options="field:'DbmsOutput',width:'350px'">Dbms Output</th>
											<th data-options="field:'ExecutionTime'">Execution Time (ms)</th>
										</tr>
									</thead>
								</table>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>