<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="en">
<head>
<title>마이바티스 + 게시판 + 회원</title>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.min.css">
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
<script
	src="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/js/bootstrap.min.js"></script>
</head>
<body>
	<div class="container">
		<h2>마이바티스 + 게시판 + 회원</h2>
		<div class="panel panel-default">
			<a href="${cp}/memberList.do">유저목록</a> <br>
			<c:if test="${empty sessionScope.member}">
				<a href="${cp}/loginForm.do">로그인</a>
			</c:if>
			<c:if test="${not empty sessionScope.member}">
				<a href="logout.do">로그아웃</a>
			</c:if>
			<div class="panel-heading" onClick="location.href='boardList.do'">게시판</div>
			<div class="panel-body">Panel Content</div>
			<div class="panel-footer"></div>
		</div>
	</div>
</body>
</html>
