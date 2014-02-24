<%@ include file="LoginCheck.jsp" %>
<%@ include file="HeaderOpen.jsp" %>
<script type="text/javascript" src="/leQuote/js/jquery-2.1.0.js"></script>
<script type="text/javascript" src="/leQuote/js/suggestions.js"></script>
<title>Le Quotes - Friends</title>
<%@ include file="HeaderClose.jsp" %>
	<h3>Find a friend</h3>
	<!-- User search form -->
	<div id="userSuggest" style="position:relative;">
			<input id="userSuggestInp" type="text" name="usernamezz" class="form-control" id="tweetUsername" placeholder="Search for a friend" autocomplete="off">
			<ul id="suggestionList" class="typeahead dropdown-menu" style="width:100%;top: 36px; position:absolute; display: none;">
			</ul>
			<!-- End the user search form. -->
			<script type="text/javascript">
				$(document).ready(function () 
				{
					$('#userSuggest').suggestions("/leQuote/friend/","#userSuggestInp","#suggestionList","/leQuote/message/user/");
				});
			</script>
	</div>
	<% UserStore us = (UserStore)request.getAttribute("myUserData"); %>
	<h3>You Follow (<%=us.getFollowingCount() %>)</h3>
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
				String user = Convertors.cl((String)iterator.next());
				
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
	<h3>People Following You (<%=us.getFollowerCount() %>)</h3>
		<%
	if(request.getAttribute("followedBy") != null)
	{
		ArrayList<String> followedBy = (ArrayList<String>)request.getAttribute("followedBy");
		
		if(followedBy != null)
		{
			Iterator<String> iterator;
	
	
			iterator = followedBy.iterator();     
			while (iterator.hasNext())
			{
				String user = Convertors.cl((String)iterator.next());
				
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
<%@ include file="Footer.jsp" %>