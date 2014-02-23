<%@ include file="HeaderOpen.jsp" %>
<title>Le Quotes - Messages</title>
<%@ include file="HeaderClose.jsp" %>

<!-- Login form -->
<form role="form" action="/leQuote/login" method="post" style="max-width:350px;" autocomplete="off">
 <h2 class="form-signin-heading">Please sign in</h2>
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
        <input type="text" name="username" class="form-control" placeholder="Username" required autofocus>
        <input type="password" name="password" class="form-control" placeholder="Password" required>
        <label>
        	<a href="/leQuote/register">Register</a>
        </label>
        <button class="btn btn-lg btn-primary btn-block" type="submit">Sign in</button>
</form>
<!-- End of login from. -->

<%@ include file="Footer.jsp" %>