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
		        <hr/>
		        
<%
		if (request.getAttribute("error") != null ) {
			%>
			<font color=red><%=request.getAttribute("error") %></font>
			<hr/>
		<%
		} 
		else {
		}
	%>			
			<div class="row">
		    		<form class="form-inline" action="ADPLogin" method="POST">
				        <div class="form-group">
				            <label class="sr-only" for="inputEmail">Email</label>
				            <input type="name" name="username" class="form-control" id="inputEmail" placeholder="Email">
				        </div>
				        <div class="form-group">
				            <label class="sr-only" for="inputPassword">Password</label>
				            <input type="password" name="pwd" class="form-control" id="inputPassword" placeholder="Password">
				        </div>
				        <!--<div class="checkbox">
				            <label><input type="checkbox"> Remember me</label>
				        </div>-->
				        <button type="submit" class="btn btn-primary">Login</button>
				    </form>
				    <hr/>
				</div>
		        <div class="row">
		        	<section class='col-xs-12 col-sm-6 col-md-6'>
		              <h2>What is it?</h2>
		              <p>The Arabic Diatessaron Project (henceforth ADP) is an international 
		                research project in Digital Humanities that aims to collect, 
		                to digitalize and to encode all known manuscripts of the 
		                Arabic Diatessaron, a text which has been relatively neglected in 
		                scholarly research.</p>
				  	</section>
		          	<section class="col-xs-12 col-sm-6 col-md-6">
		                <h2>What are the goals?</h2>
		                <p>ADP's final goal is to provide a number of tools 
		                that can enable scholars to query, compare and investigate 
		                effectively all known variants of the text, 
		                that will be encoded as far as possible 
		                in compliance with TEI guidelines.</p>
		          	</section>
				</div>
		    	<div class="row">
		          <section class="col-xs-12 col-sm-6 col-md-6">
		          	<h2>Tatian's Diatessaron</h2>
		          		<p>The Diatessaron (c. 160/175) is the most important early Gospel harmony, a unified
		                narrative of the Four Gospels; its author, Tatian, was a Christian Assyrian apologist,
		               	disciple of Justin Martyr in Rome. Tatian originally wrote the Diatessaron in Greek
		               	and Syriac: the Greek version is virtually entirely lost, the Syriac version survives
		               	in quotations from later commentaries.)</p>
		          </section>
		          <section class="col-xs-12 col-sm-6 col-md-6">
	                <h2>The Arabic Diatessaron</h2>
	                <p>The oldest extant translation of the Diatessaron is an Arabic text 
	                from the 11<sup>th</sup> century, a translation from the Syriac
	                by Abū al-Faraj Ibn Ṭayyib. The variants of this text are the core
	                of the ADP.</p>
		          </section>
		        </div>
		        <hr/>
		        <footer>
		          <div class="logo"><span class="glyphicon glyphicon-copyright-mark"></span> 2015 <a href="http://host.uniroma3.it/docenti/lancioni/">Giuliano Lancioni</a></div>
		        </footer>
			</section>
		    <script src="js/jquery-1.11.2.min.js"></script>
		    <script src="js/bootstrap.min.js"></script></body>
		</div>
	</body>
</html>
