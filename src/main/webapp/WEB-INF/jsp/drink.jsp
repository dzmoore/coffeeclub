<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="stylesheet" type="text/css"
	href="<c:url value="/resources/css/main.css"/>" />
<title>${result}</title>
</head>
<body>
	<jsp:include page="navmenu.jsp" />
	<span class="entry">
		<h2 class="item">${drink_ref.type.name}</h2>
		<br />
		<table class="invis">
			<tr class="invis">
				<td class="invis"><b class="item">Date Consumed:</b></td>
				<td class="invis"><span class="item">${drink_ref.dateConsumed}</span></td>
			</tr>
			<tr class="invis">
				<td class="invis"><b class="item">Description:</b></td>
				<td class="invis"><span class="item">${drink_ref.type.description}</span></td>
			</tr>
			<tr class="invis">
				<td class="invis"><b class="item">Cost:</b></td>
				<td class="invis"><span class="item">&#36;${drink_ref.cost}</span></td> <!-- &#36; == dollar sign -->
			</tr>
			<tr class="invis">
				<td class="invis"><b class="item">Drink Type Created By:</b></td>
				<td class="invis"><span class="item">${drink_ref.type.createdBy.username}</span></td>
			</tr>
		</table>

	</span>
</body>
</html>