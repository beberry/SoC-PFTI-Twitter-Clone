<%@ include file="LoginCheck.jsp" %>
<%@ include file="HeaderOpen.jsp" %>
<title>Le Quotes - Messages</title>
<%@ include file="HeaderClose.jsp" %>
<!--  New tweet form -->
<form role="form" action="" method="post">
  <div class="form-group">
    <textarea name="tweetText" class="form-control" rows="3" placeholder="The important story that everyone wants to hear..."></textarea>
  </div>
  <button type="submit" class="btn btn-default">Submit</button>
</form>
<!-- End of new tweet from. -->
<% if(request.getAttribute("PageType") !=null) 
{
	%>
<h1>All Le Quotes<%=request.getAttribute("PageType") %></h1>
<%
}
%>
<%
	if(request.getAttribute("page-owner") != null && !request.getAttribute("page-owner").equals(session.getAttribute("userid")))
	{
%>
<form role="form" action="/leQuote/friend" method="post">
<%
	if(request.getAttribute("follow") != null)
	{
%>
	<input type="hidden" name="followUsername" value="<%=request.getAttribute("page-owner") %>"/>
	<button type="submit" class="btn btn-primary">Follow</button>
  <% 
	}
	else if(request.getAttribute("unFollow") != null)
{
%>
	<input type="hidden" name="unFollowUsername" value="<%=request.getAttribute("page-owner") %>"/>
	<button type="submit" class="btn btn-danger">Unfollow</button>
  <% 
	}
%>
</form>
<% 
	}
%>


<%
System.out.println("In render");
List<TweetStore> lTweet = (List<TweetStore>)request.getAttribute("Tweets");
if (lTweet==null){
 %>
	<p>Le any Quote was not found</p>
	<% 
}else{
%>
<% 
Iterator<TweetStore> iterator;


iterator = lTweet.iterator();     
while (iterator.hasNext()){
	TweetStore ts = (TweetStore)iterator.next();
	
	%>
	<div>
		<div style="width:80%;float:left;">
			<a href="/leQuote/message/id/<%=ts.getTimeUuid().toString() %>" ><span style="display:block;width:100%;"><%=ts.getTweet() %></span></a> by <a href="/leQuote/message/user/<%=ts.getUser() %>" ><%=ts.getUser() %></a> at <%=ts.getDateFromUuid() %><br/><br /><br />
		</div>
		<% 
		if(ts.getUser().equals(session.getAttribute("userid")))
		{
		%>
		<div style="width:20%;float:left;">
			<form role="form" action="/leQuote/message" method="post">
				<input type="hidden" name="delete-ownerId" value="<%=ts.getUser() %>" />
				<input type="hidden" name="delete-quoteId" value="<%=ts.getTimeUuid() %>" />
				<button type="submit" class="btn btn-danger">Delete</button>
			</form>
		</div>
		<% 
		}
		%>
	</div>
	<%
}
}
%>
<%@ include file="HeaderOpen.jsp" %>