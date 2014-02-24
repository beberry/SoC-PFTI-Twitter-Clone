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

/**
 * Servlet implementation class Login
 */
@WebServlet({ "/login", "/Login", "/login/*" })
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    
 private Cluster cluster;
 
 
 /**
  * @see HttpServlet#HttpServlet()
  */
 public Login() {
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
		
		
		
		HttpSession session = request.getSession();
		
		if ((session.getAttribute("userid") == null) || (session.getAttribute("userid") == ""))
		{
			// Display the login form.
			RequestDispatcher rd = request.getRequestDispatcher("/Login.jsp"); 
			
			rd.forward(request, response);
		}
		else
		{
			// Already logged in, don't display this form.
			
			response.sendRedirect("/leQuote/message/");
			

		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		// TODO Check if the user is not logged in already.
		
		
		HttpSession session = request.getSession();
		
		if ((session.getAttribute("userid") == null) || (session.getAttribute("userid") == ""))
		{
			// Display the login form.
			// Validate login data.
			String username = request.getParameter("username");
			String password	= request.getParameter("password");
			
			LinkedList<String> errors = new LinkedList<String>();
			
			
			if(username != null && password != null)
			{
				// Is providing login data, check if is an actual user.
				UserModel user 		= new UserModel();
				user.setCluster(cluster);
				UserStore userStore = user.login(username,password);
				
				if(userStore != null)
				{
					// The user was logged in.
					//System.out.println("test");
					// Set the http session.
					HttpSession httpSession = request.getSession();
					httpSession.setAttribute("userid",userStore.getUsername());
					response.sendRedirect("/leQuote/message");
				}
				else
				{
					// Specified user was not found.
					errors.push("Couldn't find a user with this data!");
					
					request.setAttribute("Errors", errors); //Set a bean with the list in it
					
					RequestDispatcher rd = request.getRequestDispatcher("/Login.jsp"); 
					rd.forward(request, response);
				}

			}
			else
				{//System.out.println("test3");
				// Is not providing the data needed, show the login form.
				RequestDispatcher rd = request.getRequestDispatcher("/Login.jsp"); 
				rd.forward(request, response);
				
			}
		}
		else
		{
			response.sendRedirect("/leQuote/message/");
		}
		
		
	}

}
