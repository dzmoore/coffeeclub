<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
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
	<span class="centermenu"> <form:form commandName="drinktype">
			<table class="invis">
				<tr>
					<td class="invis">name</td>
					<td class="invis"><form:input path="name" /></td>
				</tr>
				<tr>
					<td class="invis">description</td>
					<td class="invis"><textarea id="description"
							name="description" rows="2" cols="20"></textarea></td>
				</tr>
				<tr>
					<td class="invis">cost per ML</td>
					<td class="invis"><form:input path="costPerML" /></td>
				</tr>
				<tr>
					<td class="invis" />
					<td class="invis"><input type="submit" value="create drink type" /></td>
				</tr>
			</table>
		</form:form>
	</span>
</body>
</html>