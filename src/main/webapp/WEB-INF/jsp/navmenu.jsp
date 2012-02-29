<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<span class="navbox">
	<a href="<c:url value="/main"/>">${user_ref.username}</a><br />
	<a href="<c:url value="/newdrink"/>">Create Drink</a><br />
	<a href="<c:url value="/newdrinktype"/>">Create Drink</a><br />
	<a href="<c:url value="/newinvoice"/>">Create Invoice</a><br />
</span>
