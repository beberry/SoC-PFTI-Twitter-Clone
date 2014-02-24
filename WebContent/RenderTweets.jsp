<%@ include file="LoginCheck.jsp" %>
<%@ include file="HeaderOpen.jsp" %>
<script type="text/javascript" src="/leQuote/js/jquery-2.1.0.js"></script>
<script type="text/javascript" src="/leQuote/js/ajaxPost.js"></script>
<title>Le Quotes - Messages</title>
<%@ include file="HeaderClose.jsp" %>
<div id="ajaxInfoWrap">
	<div class="info" class="hideMe"></div>
</div>
<div id="quoteForm">
	<!--  New tweet form -->
	<form role="form" action="" method="post">
	  <div class="form-group">
	    <textarea name="tweetText" class="form-control" rows="3" placeholder="The important story that everyone wants to hear..."></textarea>
	  </div>
	  <button type="submit" class="btn btn-default">Submit</button>
	</form>
	<!-- End of new tweet from. -->
</div>
<script type="text/javascript">
	$(document).ready(function () 
	{
		$('#quoteForm').ajaxPost("/leQuote/message/",null,"#ajaxInfoWrap","submitTweetCallback","POST",true);
	});
</script>
<% if(request.getAttribute("PageType") !=null) 
{
	%>
<h1>All Le Quotes<%=Convertors.cl(request.getAttribute("PageType").toString()) %></h1>
<%
}
%>
<%
	if(request.getAttribute("page-owner") != null && !request.getAttribute("page-owner").equals(session.getAttribute("userid")))
	{
%>
<div id="aboutFieldWrap">
	<div id="aboutField">
	<%
		if(request.getAttribute("page-ownerUS") != null)
		{
			UserStore us = (UserStore)request.getAttribute("page-ownerUS");
		%>
			<span class="about">ABOUT:</span> <i><%=us.getAbout() %></i>
		<%
		}%>
	</div>
	<div id="followForm">
		<form role="form" action="/leQuote/friend" method="post" class="followForm">
		<%
			if(request.getAttribute("follow") != null)
			{
		%>
			<input type="hidden" name="followUsername" value="<%=Convertors.cl(request.getAttribute("page-owner").toString()) %>"/>
			<button type="submit" class="btn btn-primary">Follow</button>
		  <% 
			}
			else if(request.getAttribute("unFollow") != null)
		{
		%>
			<input type="hidden" name="unFollowUsername" value="<%=Convertors.cl(request.getAttribute("page-owner").toString()) %>"/>
			<button type="submit" class="btn btn-danger">Unfollow</button>
		  <% 
			}
		%>
		</form>
	</div>
</div>
<script type="text/javascript">
	$(document).ready(function () 
	{
		$('#followForm').ajaxPost("/leQuote/friend/",null,"#ajaxInfoWrap","followers","POST",false);
	});
</script>
<% 
	}
%>
<%
//System.out.println("In render");
List<TweetStore> lTweet = (List<TweetStore>)request.getAttribute("Tweets");
if (lTweet==null){
 %>
	<p>Le any Quote was not found</p>
	<% 
}else{
%>
<div id="tweetList">
<% 
Iterator<TweetStore> iterator;


iterator = lTweet.iterator();     
while (iterator.hasNext()){
	TweetStore ts = (TweetStore)iterator.next();
	
	%>
	<div class="tweet">
		<div class="body">
			<a href="/leQuote/message/id/<%=ts.getTimeUuid().toString() %>" ><span class="text"><%=Convertors.cl(ts.getTweet()) %></span></a><span class="info">by <a href="/leQuote/message/user/<%=ts.getUser() %>" ><%=Convertors.cl(ts.getUser()) %></a> at <span class="date"><%=ts.getDateFromUuid() %></span></span>
		</div>
		<% 
		if(ts.getUser().equals(session.getAttribute("userid")))
		{
		%>
		<div class="del">
			<form role="form" action="/leQuote/message" method="post" class="deleteTweetForm">
				<input type="hidden" name="delete-ownerId" value="<%=Convertors.cl(ts.getUser()) %>" />
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
%>
</div>
	<script type="text/javascript">
		$(document).ready(function () 
		{
			$('#tweetList').ajaxPost("/leQuote/message/","delete-quoteId","#ajaxInfoWrap","deleteTweet","DELETE",false);
		});
	</script>
<%
}
%>
<%@ include file="Footer.jsp" %>