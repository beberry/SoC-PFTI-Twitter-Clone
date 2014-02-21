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
            <%  if ((session.getAttribute("userid") != null) && (session.getAttribute("userid") != "")) { %>
            <li><a href="/leQuote/logout">Log out</a></li>
            <% } %>
          </ul>
        </div><!--/.nav-collapse -->
      </div>
    </div>

<div id="container">
Profileee

</div>
</body>
</html>