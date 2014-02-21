package lv.beberry.quote.servlets;

import java.io.IOException;
import java.util.LinkedList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.datastax.driver.core.Cluster;

import lv.beberry.quote.lib.*;
import lv.beberry.quote.models.*;
import lv.beberry.quote.stores.*;
import lv.beberry.quote.exceptions.*;

/**
 * Servlet implementation class Login
 */
@WebServlet({ "/register", "/Register", "/register/*" })

public class Register extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
       
    private Cluster cluster;
    
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Register() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
		cluster = CassandraHosts.getCluster();
	}
    

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		// Display the login form.
		RequestDispatcher rd = request.getRequestDispatcher("/Register.jsp"); 
		
		rd.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		// TODO prevent XSS and cql injections.
		
		HttpSession session = request.getSession();
		
		// Check if the user isn't logged in already.
		if ((session.getAttribute("userid") != null) && (session.getAttribute("userid") != ""))
		{
			// Don't do anything.. 
			
			// Display tweets?
		}
		else
		{
			// Validate login data.
			String username  = request.getParameter("username");
			String email     = request.getParameter("email");
			String password1 = request.getParameter("password");
			String password2 = request.getParameter("password_confirmation");
			
			
			if(username != null && email != null && password1 != null && password2 != null)
			{
				// Is providing registration data, try to register.
				
				UserModel user = new UserModel();
				
				LinkedList<String> errors = new LinkedList<String>();
				UserStore us = null;
				
				try
				{
					user.setCluster(cluster);
					us = user.register(username, email, password1, password2);
					
					if(us != null)
					{
						// Login the user imediately..
						HttpSession httpSession = request.getSession();
						httpSession.setAttribute("userid",us.getUsername());
						
						response.sendRedirect("/leQuote/message");
					}	
				}
				catch(UsernameTakenException e)
				{
					errors.push("This username is taken already!");
				}
				catch(PasswordsDontMatchException e)
				{
					errors.push("Entered passwords don't match!");
				}
				
				if(us == null)
				{
					request.setAttribute("Errors", errors); //Set a bean with the list in it
					RequestDispatcher rd = request.getRequestDispatcher("/Register.jsp"); 
			
					rd.forward(request, response);
				}
			}
			else
			{
				// Is not providing the data needed, show the registration form.
				RequestDispatcher rd = request.getRequestDispatcher("/Register.jsp"); 
				
				rd.forward(request, response);
			}
		}
	}
}
