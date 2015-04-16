<?xml version="1.0" encoding="utf-8" ?>
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=windows-1256" />
		<title>ADP Checkout Page</title>
	</head>
	<body>
	<%
	String userName = null;
	//allow access only if session exists
	if(session.getAttribute("user") == null){
		response.sendRedirect("login.html");
	}else userName = (String) session.getAttribute("user");
	String sessionID = null;
	Cookie[] cookies = request.getCookies();
	if(cookies !=null){
	for(Cookie cookie : cookies){
		if(cookie.getName().equals("user")) userName = cookie.getValue();
	}
	}
	%>
	<h3>Hi <%=userName %>, do the checkout.</h3>
	<br/>
	<form action="<%=response.encodeURL("ADPLogout") %>" method="post">
	<input type="submit" value="Logout" />
	</form>
	
	</body>
</html>