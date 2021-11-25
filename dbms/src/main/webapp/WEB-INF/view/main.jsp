<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>메인</title>
	<script type="text/javascript" src="/resources/easyui/jquery.min.js"></script>
	<script type="text/javascript" src="/resources/js/main.js"></script>
</head>
<body>
	<div>
		${sessionScope.JYSESSION}
		${sessionScope.JYDBID}
		${sessionScope.JYDBPW}
	</div>
	<div>
		<input type="button" value="DBMS 이용하기" onclick="window.location='http://10.47.39.102:8080/dbms/dbms'">
		<input type="button" value="로그아웃" onclick="window.location='http://10.47.39.102:8080/dbms/signOut'">
		<input id="sessionCheck" type="button" value="세션 확인">
		<input id="kafkaSend" type="button" value="카프카 메세지 보내기" >
	</div>
</body>
</html>