<%@ include file="LoginCheck.jsp" %>
<%@ include file="HeaderOpen.jsp" %>
<title>Le Quotes - Profile</title>
<%@ include file="HeaderClose.jsp" %>
<% 

System.out.println(request.getAttribute("userData"));
if(request.getAttribute("userData") != null)
{
	UserStore userData = (UserStore)request.getAttribute("userData");
	%>
<!--  New profile form -->
<form role="form" action="" method="post">
<h2 class="form-signin-heading">Update profile data</h2>
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
	<div class="form-group">
		<input type="email" name="email" id="email" class="form-control input-sm" placeholder="Email Address" value="<%=userData.getEmail() %>" >
	</div>
  <div class="form-group">
    <textarea name="aboutMe" class="form-control" rows="3" placeholder="Tell about yourself.."><%=userData.getAbout() %></textarea>
  </div>
  	<div class="form-group">
		<div class="col-xs-6 col-md-4">
			<div class="form-group">
				<input type="password" name="old_password" id="old_password" class="form-control input-sm" placeholder="Enter Old Password">
			</div>
		</div>
		<div class="col-xs-6 col-md-4">
			<div class="form-group">
				<input type="password" name="password" id="password" class="form-control input-sm" placeholder="New Password">
			</div>
		</div>
		<div class="col-xs-6 col-md-4">
			<div class="form-group">
				<input type="password" name="password_confirmation" id="password_confirmation" class="form-control input-sm" placeholder="Confirm New Password">
			</div>
		</div>
	</div>
        <input type="submit" value="Update" class="btn btn-info btn-block">
</form>
<!-- End of new tweet from. -->

<% 
	}
	%>
<%@ include file="Footer.jsp" %>