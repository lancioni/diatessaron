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
		    	<div class="row">
		            						<div class="col-sm-10">
		            <h1>The Arabic Diatessaron Project</h1>
		            </div>
		            						<div class="col-sm-2">
		    		<form class="form-inline" action="<%=response.encodeURL("ADPLogout") %>" method="post">
				        <button type="submit" class="btn btn-primary">Logout</button>
				    </form>
				    </div>
		            </div>
		        </hgroup>
		        <form role="search" class="form-horizontal">
		        <div class="row">
				  </div>
		        	<div class="col-sm-4">
		        	
		        		<label for="chapter">Chapter</label>
		        		</div>
		        	<div class="col-sm-2">
		        		<select placeholder="Chapter" class="form-control" id="chapter" dir="rtl">
		        		</select>
		        	</div>
		        	<div class="col-sm-2">
		        	</div>
		        	<div class="col-sm-2">
		        		<label for="verse">Verse</label>
		        		</div>
		        	<div class="col-sm-2">
		        		<select placeholder="Verse" class="form-control" id="verse" dir="rtl">
		        		</select>
		            </div>
		            <div class="col-sm-1">
		            </div>
		            </div>
		        </form>
		        
		        </div>
						<div class="row">
						<div class="col-sm-1">
						</div>
				</div>
			
		        </section>
		        
				<div class="bs-example">
				    <div class="panel-group" id="accordion">
				    </div>
				</div>
		        <footer>
		          <div class="logo"><span class="glyphicon glyphicon-copyright-mark"></span> 2015 <a href="http://host.uniroma3.it/docenti/lancioni/">Giuliano Lancioni</a></div>
		        </footer>
	    <script src="js/jquery-1.11.2.min.js"></script>
	    <script src="js/bootstrap.min.js"></script>
	    <script>
	    	$( "#chapter" ).change(function(event) {
		    	var chapter = $( "#chapter" )
                $.post(
                        "ADPSearch", 
                        {chapter: chapter.val()},
                        function(responseJson) {
							//var aVerses = $.parseJSON(responseText.verses);
							var verses_options = '';
							$.each(responseJson, function(key,value) {
								var aVerses = JSON.parse(value); //$.parseJSON(value);
								var verses_options = '';
								var accordions = '';
								//alert($( ".carousel-indicators" ).html());
								var sel_verse = $( 'verse' ).val();
								var cur_verse;
	                            for (var i=0; i<aVerses.length; i++) {
		                            cur_verse = aVerses[i]['_id'];
		                            cur_verse_text = aVerses[i]['text'];
	                                verses_options += '\n<option value=' + cur_verse + '>' + cur_verse + '</option>';
	        				        accordions += '\n<div class="panel panel-default">';
	        				        accordions += '<div class="panel-heading">';
	        				        accordions += '<h4 class="panel-title" dir="rtl">';
	        				        accordions += '<a data-toggle="collapse" data-parent="#accordion" href="#collapse'+ cur_verse + '">';
	        				        accordions += cur_verse + '. ' + cur_verse_text + '</a>';
	        				        accordions += '</h4>';
	        				        accordions += '</div>';
	        				        sel_in = (sel_verse == cur_verse) ? ' in' : '';
	        				        accordions += '<div id="collapse'+ cur_verse + '" class="panel-collapse collapse'+ sel_in + '">'; //in">';
	        				        accordions += '<div class="panel-body">';
	        				        accordions += '<p>';
	        				        accordions += 'Translation (to be added...)';
	        				        accordions += '</p>';
	        				        accordions += '</div>';
	        				        accordions += '</div>';
	        				        accordions += '</div>';
	    	                                
	                                }
	                            var verse = $( "#verse" );
								verse.html(verses_options);
								
								//alert(carouselIndicators);
								var accordion = $( "#accordion" );
								accordion.html( accordions );
								//chapter.trigger("change");
 
							}
							)
                        },
                        'json')
	    	});
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
							chapter.trigger("change");
                        },
                    'json');
	    	});
		    </script>
	</body>
</html>