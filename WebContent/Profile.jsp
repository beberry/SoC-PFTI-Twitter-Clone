<%@ include file="LoginCheck.jsp" %>
<%@ include file="HeaderOpen.jsp" %>
<script type="text/javascript" src="/leQuote/js/jquery-2.1.0.js"></script>
<script type="text/javascript" src="/leQuote/js/ajaxPost.js"></script>
<title>Le Quotes - Profile</title>
<%@ include file="HeaderClose.jsp" %>
<% 

//System.out.println(request.getAttribute("userData"));
if(request.getAttribute("userData") != null)
{
	UserStore userData = (UserStore)request.getAttribute("userData");
	%>
<div id="ajaxInfoWrap">
	<div class="info" class="hideMe"></div>
</div>
<div id="profileForm">
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
			<input type="email" name="email" id="email" class="form-control input-sm" placeholder="Email Address" value="<%=Convertors.cl(userData.getEmail()) %>" >
		</div>
	  <div class="form-group">
	    <textarea name="aboutMe" class="form-control" rows="3" placeholder="Tell about yourself.."><%=Convertors.cl(userData.getAbout()) %></textarea>
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
</div>
<!-- End of profile from. -->

<script type="text/javascript">
	$(document).ready(function () 
	{
		$('#profileForm').ajaxPost("/leQuote/profile/",null,"#ajaxInfoWrap","saveProfile","POST",false);
	});
</script>
			
<% 
	}
	%>
<%@ include file="Footer.jsp" %>