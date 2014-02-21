<%
    if ((session.getAttribute("userid") == null) || (session.getAttribute("userid") == "")) 
    {	
    	// The user is not logged in, redirec to a different page.
        String redirectURL = "login";
        response.sendRedirect(redirectURL);
    }
%>