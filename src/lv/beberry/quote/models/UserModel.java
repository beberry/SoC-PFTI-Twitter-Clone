package lv.beberry.quote.models;

/*
 * Expects a cassandra columnfamily defined as
 * use keyspace2;
 * CREATE TABLE Tweets (
 * user varchar,
 *  interaction_time timeuuid,
 *  tweet varchar,
 *  PRIMARY KEY (user)
 * ) WITH CLUSTERING ORDER BY (interaction_time DESC);
 * To manually generate a UUID use:
 * http://www.famkruithof.net/uuid/uuidgen
 */

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

import java.io.UnsupportedEncodingException;
import java.security.*;
import java.util.Collections;
import java.util.Formatter;

import javax.servlet.http.HttpSession;

import lv.beberry.quote.lib.*;
import lv.beberry.quote.exceptions.*;
import lv.beberry.quote.stores.TweetStore;
import lv.beberry.quote.stores.UserStore;

import java.util.ArrayList;

//CREATE TABLE Users (Username varchar, Dateuuid uuid, Email varchar,Password varchar, Followers int, Following int, PRIMARY KEY(Username, Dateuuid));
// CREATE INDEX passwordIndex ON Users (Password);

public class UserModel {
	
	private String keyspace = "quotes";
	private String salt		= "3rdYearModule";
	
	Cluster cluster;
	
	public UserModel(){
		
	}

	public void setCluster(Cluster cluster){
		this.cluster=cluster;
	}
	
	
	public UserStore login(String username, String password)
	{
		// TO-DO: Check for xss and cql injection.
		
		// Hash the password.
		try
		{
			password = this.hashPass(password);
		}
		catch(UnsupportedEncodingException e)
		{
			// Exception thrown, don't log in the user.
			return null;
		}		
		catch(java.security.NoSuchAlgorithmException e)
		{
			// Exception thrown, don't log in the user.
			return null;
		}
		
		// Now find whether an user like that exists.
		
		Session session = cluster.connect(keyspace);
		
		PreparedStatement statement = session.prepare("SELECT * FROM users WHERE username='"+username+"' AND password='"+password+"';");
		BoundStatement boundStatement = new BoundStatement(statement);
		ResultSet rs = session.execute(boundStatement);
		
		if (rs.isExhausted())
		{
			System.out.println("No users found.");
			
			session.close();
			return null;
		}
		else
		{
			// User with such details found.
			
			Row row = rs.one();
			
			UserStore us = new UserStore();
			us.setUsername(row.getString("Username"));
			us.setEmail(row.getString("Email"));
			us.setHashedPass(row.getString("Password"));
			us.setValid(true);
			
			session.close();
			return us;
		}
	}
	
	public void logout()
	{
		
	}
	
	
	/**
	 * 
	 * @param username  - The username of the user.
	 * @param email     - The email address for this user.
	 * @param password1 - The password for this user.
	 * @param password2 - The password confirmitation.
	 * @return
	 */
	public UserStore register(String username, String email, String password1, String password2) throws UsernameTakenException, PasswordsDontMatchException
	{
		boolean usernameTaken  = true;
		boolean passwordsMatch = false;

		Session session = cluster.connect(keyspace);
		
		PreparedStatement statement;
		BoundStatement boundStatement;
		ResultSet rs;
		
		// Do some checks before inserting the user info.
		System.out.println(username);
		
		// Check if such user does not exist already.
		statement      = session.prepare("SELECT * FROM Users WHERE username='"+username+"';");
		boundStatement = new BoundStatement(statement);
		
		rs = session.execute(boundStatement);
		
		if (rs.isExhausted())
		{
			// This username is available. 
			usernameTaken = false;
		}
		
		// Check if both passwords match.
		if(password1.equals(password2))
		{
			// Passwords match.
			passwordsMatch = true;
		}
		
		
		
		// Check if any of the checks failed.
		if(usernameTaken || !passwordsMatch)
		{
			// Throw errors.
			
			if(usernameTaken)
			{
				throw new UsernameTakenException();
			}
		
			if(!passwordsMatch)
			{
				throw new PasswordsDontMatchException();
			}
			
			session.close();
			return null;
		}
		else
		{
			// Create new user store..
			UserStore us = new UserStore();
			
			
			// Hash the password.
			String hashedPass = "";
			
			try
			{
				hashedPass = this.hashPass(password1);
			}
			catch(UnsupportedEncodingException e)
			{
				// Exception thrown, don't log in the user.
				return null;
			}		
			catch(java.security.NoSuchAlgorithmException e)
			{
				// Exception thrown, don't log in the user.
				return null;
			}
			
			// Get the UUID for the user.
			String userUuid = Convertors.getTimeUUID().toString();
			
			// Can insert user info into the db.
			
			
			statement = session.prepare("INSERT INTO users (Username, Dateuuid, Email,Password, Followers, Following) values('"+username+"',"+userUuid+",'"+email+"','"+hashedPass+"',0,0);");
			boundStatement = new BoundStatement(statement);
			
			rs = session.execute(boundStatement);
			
			if (!rs.isExhausted())
			{
				System.out.println("Problems when inserting data");
				
				session.close();
				
				return null;
			} 
			else
			{
				// Managed to add the user to the db.
				System.out.println("User Added..");

				us.setUsername(username);
				us.setEmail(email);
				us.setHashedPass(hashedPass);
				
				// Add the username to the dictionary.
				String usernameLower = username.toLowerCase();
				
				String key = "";
					
				for(int i=1; i<=usernameLower.length(); i++)
				{
					key = usernameLower.substring(0,i);
							
					// Add this key to the user dict.
					
					statement = session.prepare("INSERT INTO User_dictionary (Dict_Key, Username) values('"+key+"','"+usernameLower+"');");
					boundStatement = new BoundStatement(statement);
					
					rs = session.execute(boundStatement);					
				}
				
				session.close();
				return us;
			}
		}
	}
	
	public ArrayList<String> getFollowedUsers(String userId)
	{
		// Get all users that this user is following.
		
		Session session = cluster.connect(keyspace);
		
		PreparedStatement statement = session.prepare("SELECT Following FROM Following WHERE Username='"+userId+"';");
		BoundStatement boundStatement = new BoundStatement(statement);
		
		ResultSet rs = session.execute(boundStatement);
		
		ArrayList<String> followingList = null;
		
		if (rs.isExhausted()) {
			System.out.println("No Follow records returned");
		} else {
			
			 followingList = new ArrayList<String>();
					 
			for (Row row : rs)
			{
				followingList.add(row.getString("Following"));
			}
		}
		
		session.close();
		
		return followingList;
	}
	
	public ArrayList<String> getFollowedByUsers(String userId)
	{
		// Get all users that this user is following.
		
		Session session = cluster.connect(keyspace);
		
		PreparedStatement statement = session.prepare("SELECT Followed FROM Followed WHERE Username='"+userId+"';");
		BoundStatement boundStatement = new BoundStatement(statement);
		
		ResultSet rs = session.execute(boundStatement);
		
		ArrayList<String> followingByList = null;
		
		if (rs.isExhausted()) {
			System.out.println("No Follow records returned");
		} else {
			followingByList = new ArrayList<String>();
			
			for (Row row : rs)
			{
				followingByList.add(row.getString("Followed"));
			}
		}
		
		session.close();
		
		return followingByList;
	}
	
	public ArrayList<String> getSuggestions(String q)
	{
		ArrayList<String> results = new ArrayList<String>();
		
		// Now make a request for this query.
		Session session = cluster.connect(keyspace);
		
		PreparedStatement statement = session.prepare("SELECT * FROM User_dictionary WHERE Dict_key='"+q+"';");
		BoundStatement boundStatement = new BoundStatement(statement);
		ResultSet rs = session.execute(boundStatement);
		
		if (rs.isExhausted()) {
			return null;
		} else {
			for (Row row : rs) {
				results.add(row.getString("Username"));
			}
		}
		session.close();
		
		// Now sort the list.
		Collections.sort(results.subList(1, results.size()));
		
		return results;
	}
	
	public boolean userExists(String username)
	{
		// Now make a request for this query.
		Session session = cluster.connect(keyspace);
		
		PreparedStatement statement = session.prepare("SELECT * FROM Users WHERE Username='"+username+"';");
		BoundStatement boundStatement = new BoundStatement(statement);
		ResultSet rs = session.execute(boundStatement);
		
		if (rs.isExhausted()) {
			session.close();
			return false;
		} else {
			session.close();
			return true;
		}
	}
	
	public boolean checkIfFollowing(String byWho, String who)
	{
		Session session = cluster.connect(keyspace);
		
		PreparedStatement statement = session.prepare("SELECT * FROM Following WHERE Username='"+byWho+"' AND Following='"+who+"';");
		BoundStatement boundStatement = new BoundStatement(statement);
		ResultSet rs = session.execute(boundStatement);
		
		if (rs.isExhausted()) {
			session.close();
			return false;
		} else {
			session.close();
			return true;
		}
	}
	
	public void startFollowing(String whoUsername,String followsUsername)
	{
		// Insert the info.
		Session session = cluster.connect(keyspace);
		
		// Add the record - which user is following which
		PreparedStatement statement = session.prepare("INSERT INTO Following (Username, Following) VALUES('"+whoUsername+"','"+followsUsername+"');");
		BoundStatement boundStatement = new BoundStatement(statement);
		
		ResultSet rs = session.execute(boundStatement);
		
		// Add the record - which user is followed by which
		statement = session.prepare("INSERT INTO Followed (Username, Followed) VALUES('"+followsUsername+"','"+whoUsername+"');");
		boundStatement = new BoundStatement(statement);
		
		rs = session.execute(boundStatement);
		
		if (rs.isExhausted()) {
			session.close();
		} else {
			session.close();
		}
	}	
	
	public void endFollowing(String whoUsername,String unfollowsUsername)
	{
		// Insert the info.
		Session session = cluster.connect(keyspace);
		
		// Add the record - which user is following which
		PreparedStatement statement = session.prepare("DELETE FROM Following WHERE Username='"+whoUsername+"' AND Following='"+unfollowsUsername+"'");
		BoundStatement boundStatement = new BoundStatement(statement);
		
		ResultSet rs = session.execute(boundStatement);
		
		// Add the record - which user is followed by which
		statement = session.prepare("DELETE FROM Followed WHERE Username='"+unfollowsUsername+"' AND Followed='"+whoUsername+"'");
		boundStatement = new BoundStatement(statement);
		
		rs = session.execute(boundStatement);
		
		if (rs.isExhausted()) {
			session.close();
		} else {
			session.close();
		}
	}
	
	
	/**
	 * 
	 * @param password
	 * @return The hashed password.
	 * 
	 * A method which adds salt to passwords and then hashes them.
	 */
	public String hashPass(String password) throws UnsupportedEncodingException, java.security.NoSuchAlgorithmException
	{
		// Add some salt.
		String tmpPassword = password+this.salt;
			
		// SHA1
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		md.reset();
		md.update(tmpPassword.getBytes("UTF-8"));
		
		byte[] bytesOfPass = md.digest();
		tmpPassword = byteToHex(bytesOfPass);
		
		// MD5
		md = MessageDigest.getInstance("MD5");
		bytesOfPass = md.digest(tmpPassword.getBytes("UTF-8"));

		return byteToHex(bytesOfPass);
	}
	
	private static String byteToHex(final byte[] hash)
	{
		// Method taken from http://stackoverflow.com/questions/4895523/java-string-to-sha1, author - petrnohejl
	    Formatter formatter = new Formatter();
	    for (byte b : hash)
	    {
	        formatter.format("%02x", b);
	    }
	    String result = formatter.toString();
	    formatter.close();
	    return result;
	}
	
}
