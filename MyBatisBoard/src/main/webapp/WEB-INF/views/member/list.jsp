<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<link rel="stylesheet" type="text/css"
	href="${cp}/resources/css/normal.css" />
</head>
<body>
	<h1>회원 목록</h1>
	<table border="1">
		<tr>
			<th>번호</th>
			<th>아이디</th>
			<th>패스워드</th>
			<th>이름</th>
			<th>나이</th>
			<th>성별</th>
			<th>이메일</th>
			<th>프로필</th>
		</tr>
		<c:forEach var="member" items="${memberList}">
			<tr>
				<td>${member.memIdx}</td>
				<td>${member.memID}</td>
				<td>${member.memPassword}</td>
				<td>${member.memName}</td>
				<td>${member.memAge}</td>
				<td>${member.memGender}</td>
				<td>${member.memEmail}</td>
				<td>${member.memProfile}</td>
			</tr>
		</c:forEach>
	</table>

	<a href="${cp}/">메인으로</a>
	<br>
</body>
</html>
