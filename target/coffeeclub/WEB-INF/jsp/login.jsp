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
	<span class="loginbox">
		<form method="post" action="login">
			username <input type="text" name="username" value="${username}" /><br/>
			password <input type="password" name="password" value="${password}" /><br/>
			<input type="submit" value="login" /><br />
		</form>
	</span>
</body>
</html>