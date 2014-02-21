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
<form role="form" action="/leQuote/login" method="post" style="max-width:350px;" autocomplete="off">
 <h2 class="form-signin-heading">Please sign in</h2>
        <input type="text" name="username" class="form-control" placeholder="Username" required autofocus>
        <input type="password" name="password" class="form-control" placeholder="Password" required>
        <label class="checkbox">
          <input type="checkbox" value="remember-me"> Remember me
        </label>
        <label>
        	<a href="/leQuote/register">Register</a>
        </label>
        <button class="btn btn-lg btn-primary btn-block" type="submit">Sign in</button>
</form>
<!-- End of login from. -->

</div>
</body>
</html>