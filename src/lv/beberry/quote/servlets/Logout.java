package lv.beberry.quote.servlets;

import java.io.IOException;

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
@WebServlet({ "/logout", "/Logout", "/logout/*" })
public class Logout extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    
 private Cluster cluster;
 
 
 /**
  * @see HttpServlet#HttpServlet()
  */
 public Logout() {
     super();
     // TODO Auto-generated constructor stub
 }
 
 public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub

	}
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		// Display the login form.
		HttpSession httpSession = request.getSession();
		//httpSession.setAttribute("userid",userStore.getUsername());
		
		if(httpSession.getAttribute("userid") != null && httpSession.getAttribute("userid") != "")
		{
			// Log out the user..
			httpSession.invalidate(); // Invalidate the session
		}
		
		response.sendRedirect("/leQuote/message");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	}

}
