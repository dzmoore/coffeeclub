<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="stylesheet" type="text/css"
	href="<c:url value="/resources/css/main.css"/>" />
<title>Coffee Coffee Coffee! Club</title>
</head>
<body>
	<jsp:include page="navmenu.jsp" />
	
	<table>
		<tr>
			<th>Name</th>
			<th>Description</th>
			<th>Date Drank</th>
			<th>Creator</th>
			<th>Cost</th>
		</tr>
		<c:forEach items="${drinks_ref}" var="ea">
			<tr>
				<td><a href="<c:url value="drink?id=${ea.drinkId}"/>">${ea.type.name}</a></td>
				<td>${ea.type.description}</td>
				<td>${ea.dateConsumed}</td>
				<td>${ea.type.createdBy.username}</td>
				<td>${ea.cost}</td>
			</tr>
		</c:forEach>
	</table>
</body>
</html>