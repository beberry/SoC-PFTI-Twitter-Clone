<%@ include file="LoginCheck.jsp" %>
<%@ page language="java" contentType="text/html; charset=US-ASCII" pageEncoding="US-ASCII"%>
<%@ page import="lv.beberry.quote.stores.*" %>
<%@ page import="java.util.*" %>
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
<script type="text/javascript" src="/leQuote/js/suggestions.js"></script>
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
	<h3>Find a friend</h3>
	<!-- User search form -->
	<div id="userSuggest" style="position:relative;">
		<form autocomplete="off">
			<input id="userSuggestInp" type="text" name="username" class="form-control" id="tweetUsername" placeholder="Search for a friend">
			<ul id="suggestionList" class="typeahead dropdown-menu" style="width:100%;top: 36px; position:absolute; display: none;">
			</ul>
			<!-- End the user search form. -->
			<script type="text/javascript">
				$(document).ready(function () 
				{
					$('#userSuggest').suggestions("/leQuote/friend/","#userSuggestInp","#suggestionList","/leQuote/message/user/");
				});
			</script>
		</form>
	</div>
	<h3>You Follow</h3>
	<%
	if(request.getAttribute("following") != null)
	{
		ArrayList<String> following = (ArrayList<String>)request.getAttribute("following");
		
		if(following != null)
		{
			Iterator<String> iterator;
	
	
			iterator = following.iterator();     
			while (iterator.hasNext())
			{
				String user = (String)iterator.next();
				
				%>
				<a href="/leQuote/message/user/<%=user %>" ><%=user %></a><br />
		<%
			}
		}
		else
		{
		%>
			You are not following anyone!
		<% 
		} 
	%>
	<% 
	}
	else
	{
	%>
		You are not following anyone!
	<% 
	} 
	%>
	<h3>People Following You</h3>
		<%
	if(request.getAttribute("following") != null)
	{
		ArrayList<String> followedBy = (ArrayList<String>)request.getAttribute("followedBy");
		
		if(followedBy != null)
		{
			Iterator<String> iterator;
	
	
			iterator = followedBy.iterator();     
			while (iterator.hasNext())
			{
				String user = (String)iterator.next();
				
				%>
				<a href="/leQuote/message/user/<%=user %>" ><%=user %></a><br />
	<%
			}
		}
		else
		{
		%>
			No one is following you!
		<% 
		} 
	%>
	<% 
	}
	else
	{
	%>
		No one is following you!
	<% 
	} 
	%>

</div>
</body>
</html>