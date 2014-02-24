<%@ include file="HeaderOpen.jsp" %>
<script type="text/javascript" src="/leQuote/js/jquery-2.1.0.js"></script>
<script type="text/javascript" src="/leQuote/js/formValidator.js"></script>
<title>Le Quotes - Register</title>
<%@ include file="HeaderClose.jsp" %>
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
	<div class="formErrorList form-group hideMe warning">
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
<%@ include file="Footer.jsp" %>