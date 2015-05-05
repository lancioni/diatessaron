<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
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
		        <form role="search" class="navbar-form navbar-left">
		        	<div class="form-group">
		        	Chapter
		        		<select placeholder="Chapter" class="form-control" id="chapter" dir="rtl">
		        		</select>
		            </div>
		        </form>
		        <hr/>
				<form action="<%=response.encodeURL("ADPLogout") %>" method="post" class="navbar-form navbar-left">
					<div class="form-group">
						<button type="submit" class="btn btn-primary">Logout</button>
					</div>
				</form>
		        
		    </section>
		</div>	
		        <footer>
		          <div class="logo"><span class="glyphicon glyphicon-copyright-mark"></span> 2015 <a href="http://host.uniroma3.it/docenti/lancioni/">Giuliano Lancioni</a></div>
		        </footer>
	    <script src="js/jquery-1.11.2.min.js"></script>
	    <script src="js/bootstrap.min.js"></script>
	    <script>
	    	$(document).ready(function(event) {
                $.post(
                        "ADPSearch", 
                        {loading: "ADPSearch"},
//                        { sentenceToParse: sentenceToParse, 
//                            catType: catType }, 
                        function(responseText) { 
//                                $('#wait').text(responseText);
							var aLoading = responseText.loading;
							var chapters_options = '';
                            for (var i=0; i<aLoading.length; i++) {
                                chapters_options += '\n<option value=' + aLoading[i] + '>' + aLoading[i] + '</option>';
                                }
                            var chapter = $( "#chapter" );
							chapter.append(chapters_options);
                        },
                    'json');
	    	});
		    </script>
	</body>
</html>