<%@ page language="java" contentType="text/html; charset=US-ASCII" pageEncoding="US-ASCII"%>
<%@ page import="lv.beberry.quote.stores.*" %><%@ page import="java.util.*" %>
<%@ page trimDirectiveWhitespaces="true" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="/leQuote/css/style.css" />
<link rel="stylesheet" type="text/css" href="/leQuote/css/bootstrap.min.css" />
<link rel="stylesheet" type="text/css" href="/leQuote/css/bootstrap-theme.css" />
<script type="text/javascript" src="/leQuote/js/jquery-2.1.0.js"></script>
<script type="text/javascript" src="/leQuote/js/formValidator.js"></script>
<title>Le Quotes</title>
</head>
<body>
    <div class="navbar navbar-inverse navbar-fixed-top" role="navigation">
      <div class="container">
        <div class="navbar-header">
          <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
            <span class="sr-only">Toggle navigation</span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </button>
          <a class="navbar-brand" href="#">Le Quote</a>
        </div>
        <div class="collapse navbar-collapse">
          <ul class="nav navbar-nav">
            <li class="active"><a href="/leQuote/message">Home</a></li>
            <li><a href="/leQuote/friend">Friends</a></li>
            <li><a href="/leQuote/profile">Profile</a></li>
          </ul>
        </div><!--/.nav-collapse -->
      </div>
    </div>

<div id="container">
<!-- Login form -->
<form id="registrationForm" role="form" action="" method="post" style="max-width:350px;" autocomplete="off">
	<h2 class="form-signin-heading">Hey! Join to see the veryveryvery important stuff that others write.</h2>
	
	<div class="submitErrorList form-group warning">
			<%
		List<String> errors = (List<String>)request.getAttribute("Errors");
		
		if (errors != null)
		{
			// Had some errors when registering.	
	 	%>
	 	<ul>
	 		<%
	 			Iterator<String> iterator;
	 			iterator = errors.iterator();     
	 			
	 		while (iterator.hasNext())
	 		{
	 			String error = (String)iterator.next();
	 		%>
	 		<li><%= error %></li>
	 		<%
	 		}
		}
	 	%>
	 	</ul>
	 </div>
	<div class="formErrorList form-group hiddeMe warning">
	</div>
	
	<div class="form-group">
		<input type="text" name="username" id="username" class="form-control input-sm" placeholder="Username">
	</div>
	<div class="form-group">
		<input type="email" name="email" id="email" class="form-control input-sm" placeholder="Email Address">
	</div>

	<div class="row">
		<div class="col-xs-6 col-sm-6 col-md-6">
			<div class="form-group">
				<input type="password" name="password" id="password" class="form-control input-sm" placeholder="Password">
			</div>
		</div>
		<div class="col-xs-6 col-sm-6 col-md-6">
			<div class="form-group">
				<input type="password" name="password_confirmation" id="password_confirmation" class="form-control input-sm" placeholder="Confirm Password">
			</div>
		</div>
	</div>
	
	<script type="text/javascript">
		$(document).ready(function () 
		{
			$('#registrationForm').formValidator("#password","#password_confirmation");
		});
	</script>

	<input type="submit" value="Register" class="btn btn-info btn-block" disabled>
</form>
<!-- End of login from. -->

</div>
</body>
</html>