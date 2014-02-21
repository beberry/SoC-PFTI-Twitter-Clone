package lv.beberry.quote.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

// Table for username dictionary
// CREATE TABLE User_dictionary (Dict_Key varchar, Username varchar, PRIMARY KEY(DictKey,Username));


/**
 * Servlet implementation class Tweet
 */
@WebServlet({ "/friend", "/friend/*" })
public class Friend extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private Cluster cluster;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Friend() {
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
			// TODO Auto-generated method stub
			String args[]=Convertors.SplitRequestPath(request);
			
			if(args.length > 2)
			{
				if(args[2].equals("json"))
				{
					// Is trying to get a suggestion list of users.
					if(request.getParameter("q") != null)
					{
						// Todo Check the request for cql injections.
						
						// Get the suggestion JSON
						String q = request.getParameter("q").toLowerCase();
						
						UserModel um = new UserModel();
						um.setCluster(cluster);
						
						ArrayList<String> suggestions = um.getSuggestions(q);
						
						String json = new Gson().toJson(suggestions);
						
						response.setContentType("application/json");
						PrintWriter out = response.getWriter();
						// Assuming your json object is **jsonObject**, perform the following, it will return your json object  
						out.print(json);
						out.flush();
					}
				}
				else
				{
				}
			}
			else
			{
				UserModel um = new UserModel();
				um.setCluster(cluster);
				
				ArrayList<String> following = um.getFollowedUsers((String)session.getAttribute("userid"));
				
				if(following != null)
				{
					request.setAttribute("following", following);
				}
				
				ArrayList<String> followedBy = um.getFollowedByUsers((String)session.getAttribute("userid"));
				
				if(followedBy != null)
				{
					request.setAttribute("followedBy", followedBy);
				}
				
				//request.setAttribute("Tweets", tweetList); //Set a bean with the list in it
				RequestDispatcher rd = request.getRequestDispatcher("/Friend.jsp"); 
		
				rd.forward(request, response);
			}
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
			// Add a friend.
			UserModel um = new UserModel();
			um.setCluster(cluster);
			
			String currentUser  = (String)session.getAttribute("userid");
			String followUser   = request.getParameter("followUsername");
			String unFollowUser = request.getParameter("unFollowUsername");
			
			
			String otherUser = "";
			
			// Check if the userId that you want to follow actually exists.
			
			if(followUser != null)
			{
				otherUser = followUser;
				
				if(um.userExists(followUser))
				{
					um.startFollowing(currentUser,followUser);
				}
			}
			
			if(unFollowUser != null)
			{
				otherUser = unFollowUser;
				
				if(um.userExists(unFollowUser))
				{
					um.endFollowing(currentUser,unFollowUser);
				}
			}
			
			String args[]=Convertors.SplitRequestPath(request);
			
			if(args.length > 2)
			{
				if(!args[2].equals("json"))
				{
					// Wasn't a json / ajax request, redirect back to user profile.
					
					if(followUser != null || unFollowUser != null)
					{
						response.sendRedirect("/leQuote/message/user/"+otherUser);
					}
					else
					{
						response.sendRedirect("/leQuote/message/");
					}
				}
			}
			else
			{
				// Wasn't a json / ajax request, redirect back to user profile.
				if(followUser != null || unFollowUser != null)
				{
					response.sendRedirect("/leQuote/message/");
				}
			}
		}
	}
}
