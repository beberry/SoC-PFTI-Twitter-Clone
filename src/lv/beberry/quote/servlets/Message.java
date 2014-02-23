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
 * Servlet implementation class Tweet
 */
@WebServlet({ "", "/message", "/message/*" })
public class Message extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private Cluster cluster;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Message() {
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
			
			
			TweetModel tm = new TweetModel();
			tm.setCluster(cluster);
			
			LinkedList<TweetStore> tweetList = null;
			
			
			if(args.length > 2)
			{
				if(args[2].equals("user"))
				{
					
					if(args.length > 3)
					{
						
						UserModel um = new UserModel();
						um.setCluster(cluster);
						
						String reqUsername = args[3];
						
						if(um.userExists(reqUsername))
						{
							tweetList = tm.getTweetsBy(reqUsername,0,0);
					
							request.setAttribute("PageType", " by "+reqUsername);
							request.setAttribute("page-owner", reqUsername);
							
							if(um.checkIfFollowing((String)session.getAttribute("userid"), reqUsername))
							{
								request.setAttribute("unFollow", true);
							}
							else
							{
								request.setAttribute("follow", true);
							}
						}
						else
						{
							// User not found
							tweetList = null;
						}
					}
					else
					{
						tweetList = null;
					}
				}
				else if(args[2].equals("id"))
				{
					// Display a specific message
					if(args.length > 3)
					{
						String reqQuoteId = args[3];
						
						tweetList = new LinkedList<TweetStore>();
						
						tweetList.add(tm.getQuote(reqQuoteId));
					}
				}
				else
				{
					tweetList = tm.getTweetsFollowing((String)session.getAttribute("userid"));
					request.setAttribute("PageType", " posted by your friends");
				}
			}
			else
			{
				tweetList = tm.getTweetsFollowing((String)session.getAttribute("userid"));
				
				request.setAttribute("PageType", " posted by your friends");
			}
			
			if(tweetList != null)
			{
				request.setAttribute("Tweets", tweetList); //Set a bean with the list in it
			}
			
			RequestDispatcher rd = request.getRequestDispatcher("/RenderTweets.jsp"); 
	
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
			TweetModel tm = new TweetModel();
			tm.setCluster(cluster);
			
			String args[]=Convertors.SplitRequestPath(request);
			
			if(request.getParameter("delete-quoteId") != null)
			{
				// Is trying to delete a quote.
				
				if(tm.canTweetBeDeleted((String)session.getAttribute("userid"),request.getParameter("delete-ownerId"),request.getParameter("delete-quoteId")))
				{
					// The user can delete this quote.
					
					// Delete the quote.
					tm.deleteTweet(request.getParameter("delete-ownerId"),request.getParameter("delete-quoteId"));
				}	
			}
			else
			{
				String username = (String)session.getAttribute("userid");
				String tweet	= request.getParameter("tweetText");
				
				
				
				
				// To-Do: Remove html, and other crap from the data + trim it.
				
				
				// Insert data into the db.
				
				tm.addTweets(username,tweet);
			}
			
			// Display the list of tweets.
	
			
			// TODO Auto-generated method stub
			tm.setCluster(cluster);
			
			LinkedList<TweetStore> tweetList;
			
			
			if(args.length > 2)
			{
				if(args[2].equals("user"))
				{
					
					if(args.length > 3)
					{
						
						UserModel um = new UserModel();
						um.setCluster(cluster);
						
						String reqUsername = args[3];
						
						if(um.userExists(reqUsername))
						{
							tweetList = tm.getTweetsBy(reqUsername,0,0);
					
							request.setAttribute("PageType", " by "+reqUsername);
							request.setAttribute("page-owner", reqUsername);
						}
						else
						{
							// User not found
							tweetList = null;
						}
					}
					else
					{
						tweetList = null;
					}
				}
				else
				{
					tweetList = tm.getTweetsFollowing((String)session.getAttribute("userid"));
					request.setAttribute("PageType", " posted by your friends");
				}
			}
			else
			{
				tweetList = tm.getTweetsFollowing((String)session.getAttribute("userid"));
				
				request.setAttribute("PageType", " posted by your friends");
			}
			
			if(tweetList != null)
			{
				request.setAttribute("Tweets", tweetList); //Set a bean with the list in it
			}
			
			RequestDispatcher rd = request.getRequestDispatcher("/RenderTweets.jsp"); 
	
			rd.forward(request, response);
		}
		
	}
	// Question: can you call doGet from doPost?
	// Question: addTweets method in the message.java? Do I need to use tweetstore when inserting?
	// Question: XSS, SQL injections?
}
