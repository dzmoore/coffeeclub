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
	<c:forEach items="${invoices_ref}" var="ea">
		<span class="entry">
			<h2 class="item">Due: &#36;${ea.due}</h2>
			<table>
				<tr>
					<th>Name</th>
					<th>Description</th>
					<th>Date Drank</th>
					<th>Creator</th>
					<th>Cost</th>
				</tr>
				<c:forEach items="${ea.drinks}" var="eaDrink">

					<tr>
						<td><a href="<c:url value="/drink?id=${eaDrink.drinkId}"/>">${eaDrink.type.name}</a></td>
						<td>${eaDrink.type.description}</td>
						<td>${eaDrink.dateConsumed}</td>
						<td>${eaDrink.type.createdBy.username}</td>
						<td>${eaDrink.cost}</td>
					</tr>
				</c:forEach>
			</table>
		</span>
	</c:forEach>
</body>
</html>