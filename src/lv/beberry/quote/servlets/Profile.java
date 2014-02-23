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

import lv.beberry.quote.exceptions.PasswordCantBeEmptyExceotion;
import lv.beberry.quote.exceptions.PasswordsDontMatchException;
import lv.beberry.quote.exceptions.WrongPasswordException;
import lv.beberry.quote.lib.*;
import lv.beberry.quote.models.*;
import lv.beberry.quote.stores.*;


/**
 * Servlet implementation class Tweet
 */
@WebServlet({ "/profile", "/profile/*" })
public class Profile extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private Cluster cluster;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Profile() {
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
	
			HttpSession session = request.getSession();
			
			if ((session.getAttribute("userid") == null) || (session.getAttribute("userid") == ""))
			{
				// Display login page...
				RequestDispatcher rd = request.getRequestDispatcher("/Login.jsp"); 
				
				rd.forward(request, response);
			}
			else
			{
				// Get this user data.				
				UserModel um = new UserModel();
				um.setCluster(cluster);
				
				UserStore us = um.getUser((String)session.getAttribute("userid"));
				
				request.setAttribute("userData", us);
				
				RequestDispatcher rd = request.getRequestDispatcher("/Profile.jsp"); 
				
				rd.forward(request, response);
			}
			
	
			
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		HttpSession session = request.getSession();
		
		
		if ((session.getAttribute("userid") == null) || (session.getAttribute("userid") == ""))
		{
			// Display login page...
			RequestDispatcher rd = request.getRequestDispatcher("/Login.jsp"); 
			
			rd.forward(request, response);
		}
		else
		{
			LinkedList<String> errors = new LinkedList<String>();
			
			String username = (String)session.getAttribute("userid");
			
			UserModel um = new UserModel();
			um.setCluster(cluster);
			UserStore us = um.getUser(username);
			
			// The user is logged in.
			String email = request.getParameter("email");
			String about = request.getParameter("aboutMe");
			
			String oldPassword 	   = request.getParameter("old_password");
			String newPassword 	   = request.getParameter("password");
			String newPasswordConf = request.getParameter("password_confirmation");
			
			us = um.updateProfile(us, email, about);
			
			if(oldPassword != null || newPassword != null || newPasswordConf != null)
			{
				try
				{
					us = um.changePassword(us, oldPassword,newPassword,newPasswordConf);
				}
				catch(WrongPasswordException e)
				{
					errors.push("Wrong Password!");
				}
				catch(PasswordsDontMatchException e)
				{
					errors.push("New passwords don't match!");
				}
				catch(PasswordCantBeEmptyExceotion e)
				{
					errors.push("Password can't be empy!");
				}
				
				request.setAttribute("Errors", errors); //Set a bean with the list in
			}
		
			
			
			
			// Get this user data.
			
			request.setAttribute("userData", us);
		
			
			RequestDispatcher rd = request.getRequestDispatcher("/Profile.jsp"); 
	
			rd.forward(request, response);
		}
		
	}
	// Question: can you call doGet from doPost?
	// Question: addTweets method in the message.java? Do I need to use tweetstore when inserting?
	// Question: XSS, SQL injections?
}
