<?xml version="1.0" encoding="utf-8" ?>
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta charset="utf-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
  <title>The Arabic Diatessaron Project</title>
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <link rel="stylesheet" type="text/css" href="css/bootstrap.min.css">
  <link rel="stylesheet" href="css/bootstrap-theme.min.css">
  <link rel="stylesheet" href="css/diatessaron.css">
</head>
	<body>
	<%
	//allow access only if session exists
	String user = null;
	if(session.getAttribute("user") == null){
		response.sendRedirect("login.html");
	}else user = (String) session.getAttribute("user");
	String userName = null;
	String sessionID = null;
	Cookie[] cookies = request.getCookies();
	if(cookies !=null){
	for(Cookie cookie : cookies){
		if(cookie.getName().equals("user")) userName = cookie.getValue();
		if(cookie.getName().equals("JSESSIONID")) sessionID = cookie.getValue();
	}
	}else{
		sessionID = session.getId();
	}
	%>
		<div class = "diatessaron">
			<nav role="navigation" class="navbar navbar-default">
		        <!-- Brand and toggle get grouped for better mobile display -->
		        <div class="navbar-header">
		            <button type="button" data-target="#navbarCollapse" data-toggle="collapse" class="navbar-toggle">
		                <span class="sr-only">Toggle navigation</span>
		                <span class="icon-bar"></span>
		                <span class="icon-bar"></span>
		                <span class="icon-bar"></span>
		            </button>
		            <a href="#" class="navbar-brand">ArabicDiatessaronProject</a>
		        </div>
		        <!-- Collection of nav links, forms, and other content for toggling -->
		        <div id="navbarCollapse" class="collapse navbar-collapse">
		            <ul class="nav navbar-nav">
		                <li class="active"><a href="#">Home</a></li>
		                <li><a href="#">Profile</a></li>
		                <li class="dropdown">
		                    <a data-toggle="dropdown" class="dropdown-toggle" href="#">Messages <b class="caret"></b></a>
		                    <ul role="menu" class="dropdown-menu">
		                        <li><a href="#">Inbox</a></li>
		                        <li><a href="#">Drafts</a></li>
		                        <li><a href="#">Sent Items</a></li>
		                        <li class="divider"></li>
		                        <li><a href="#">Trash</a></li>
		                    </ul>
		                </li>
		            </ul>
		            <form role="search" class="navbar-form navbar-left">
		                <div class="form-group">
		                    <input type="text" placeholder="Search" class="form-control">
		                </div>
		            </form>
		            <ul class="nav navbar-nav navbar-right">
		                <li><a href="#">Login</a></li>
		            </ul>
		        </div>
		    </nav>
		    <section class='container-fluid'>
		    	<hgroup>
		            <h1>The Arabic Diatessaron Project</h1>
		        </hgroup>
		<h3>Hi <%=userName %>, Login successful. Your Session ID=<%=sessionID %></h3>
		<br/>
		User=<%=user %>
		<br/>
		<!-- need to encode all the URLs where we want session information to be passed -->
		<!-- db.diatessaron.find({'verses': {"$elemMatch": {'_id': '2'}}},
{'verses.$': 1}) -->
		<a href="<%=response.encodeURL("ADPCheckoutPage.jsp") %>">Checkout Page</a>
		<form action="<%=response.encodeURL("ADPLogout") %>" method="post">
			<input type="submit" value="Logout" />
		</form>
			</section>
		    <script src="js/jquery-1.11.2.min.js"></script>
		    <script src="js/bootstrap.min.js"></script></body>
		</div>	
	</body>
</html>