package lv.beberry.quote.models;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.querybuilder.*;

import java.io.UnsupportedEncodingException;
import java.security.*;
import java.util.Collections;
import java.util.Formatter;








import java.util.UUID;

import lv.beberry.quote.lib.*;
import lv.beberry.quote.exceptions.*;
import lv.beberry.quote.stores.UserStore;

import java.util.ArrayList;

public class UserModel {
	
	private String keyspace = "quotes";
	private String salt		= "3rdYearModule";
	private Cluster cluster;
	
	public UserModel(){
		
	}

	public void setCluster(Cluster cluster){
		this.cluster=cluster;
	}
	
	/**
	 * Login method.
	 */
	public UserStore login(String username, String password)
	{
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
		
		Select.Where querySEL = QueryBuilder.select()
				.from(this.keyspace, "Users")
				.where(QueryBuilder.eq("Username", username))
				.and(QueryBuilder.eq("Password", password));
		
		ResultSet rs = session.execute(querySEL.toString());
		
		if (rs.isExhausted())
		{
			//System.out.println("No users found.");
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
			us.setPermissions(row.getSet("Permissions", String.class));
			us.setAbout(row.getString("About"));
			us.setValid(true);
			
			us = this.setUserStats(us);
			
			session.close();
			
			return us;
		}
	}
	
	/**
	 * Logout method, currently not implemented.
	 */
	public void logout()
	{
	}
	
	/**
	 * Get a userstore by a specific username.
	 */
	public UserStore getUser(String username)
	{
		Session session = cluster.connect(keyspace);
		
		Select.Where querySEL = QueryBuilder.select()
				.from(this.keyspace, "Users")
				.where(QueryBuilder.eq("Username", username));
		
		ResultSet rs = session.execute(querySEL.toString());
		
		if (rs.isExhausted())
		{
			//System.out.println("User not found found.");
			
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
			us.setPermissions(row.getSet("Permissions", String.class));
			us.setAbout(row.getString("About"));
			us.setValid(true);
			
			session.close();
			
			us = this.setUserStats(us);
			
			return us;
		}
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
		ResultSet rs;
		
		// Do some checks before inserting the user info.
		//System.out.println(username);
		
		// Check if such user does not exist already.
		Select.Where querySEL = QueryBuilder.select()
				.from(this.keyspace, "Users")
				.where(QueryBuilder.eq("Username", username));
		
		rs = session.execute(querySEL.toString());
		
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
			
			// Can insert user info into the db.
			
			Insert queryINS = QueryBuilder.insertInto(this.keyspace, "Users")
					.value("Username", username)
		            .value("Dateuuid", Convertors.getTimeUUID())
		            .value("Email", email)
		            .value("Password", hashedPass);
			
			rs = session.execute(queryINS.toString());
			
			if (!rs.isExhausted())
			{
				//System.out.println("Problems when inserting data");
				
				session.close();
				
				return null;
			} 
			else
			{
				// Managed to add the user to the db.
				//System.out.println("User Added..");

				us.setUsername(username);
				us.setEmail(email);
				us.setHashedPass(hashedPass);
				
				us = this.setUserStats(us);
				
				// Add the username to the dictionary.
				String usernameLower = username.toLowerCase();
				
				String key = "";
					
				for(int i=1; i<=usernameLower.length(); i++)
				{
					key = usernameLower.substring(0,i);
							
					// Add this key to the user dict.
					
					queryINS = QueryBuilder.insertInto(this.keyspace, "User_dictionary")
							.value("Dict_Key", key)
				            .value("Username", usernameLower);
					
					rs = session.execute(queryINS.toString());					
				}
				
				session.close();
				
				return us;
			}
		}
	}
	
	/**
	 * Get a list of users that this user follows.
	 */
	public ArrayList<String> getFollowedUsers(String userId)
	{
		Session session = cluster.connect(keyspace);
		
		Select.Where querySEL = QueryBuilder.select("Following")
				.from(this.keyspace, "Following")
				.where(QueryBuilder.eq("Username", userId));
		
		ResultSet rs = session.execute(querySEL.toString());
		
		ArrayList<String> followingList = null;
		
		if (rs.isExhausted()) {
			//System.out.println("No Follow records returned");
		}
		else
		{
			 followingList = new ArrayList<String>();
					 
			for (Row row : rs)
			{
				followingList.add(row.getString("Following"));
			}
		}
		
		session.close();
		
		return followingList;
	}
	
	/**
	 * Get a list of users who follow this user.
	 */
	public ArrayList<String> getFollowedByUsers(String userId)
	{
		// Get all users that this user is following.
		Session session = cluster.connect(keyspace);
		
		Select.Where querySEL = QueryBuilder.select("Followed")
				.from(this.keyspace, "Followed")
				.where(QueryBuilder.eq("Username", userId));
	
		ResultSet rs = session.execute(querySEL.toString());
		
		ArrayList<String> followingByList = null;
		
		if (rs.isExhausted())
		{
			//System.out.println("No Follow records returned");
		}
		else
		{
			followingByList = new ArrayList<String>();
			
			for (Row row : rs)
			{
				followingByList.add(row.getString("Followed"));
			}
		}
		
		session.close();
		
		return followingByList;
	}
	
	/**
	 * Get suggestions for "friends".
	 */
	public ArrayList<String> getSuggestions(String q)
	{
		ArrayList<String> results = new ArrayList<String>();
		
		// Now make a request for this query.
		Session session = cluster.connect(keyspace);
		
		Select.Where querySEL = QueryBuilder.select()
				.from(this.keyspace, "User_dictionary")
				.where(QueryBuilder.eq("Dict_key", q));

		ResultSet rs = session.execute(querySEL.toString());
		
		if (rs.isExhausted())
		{
			return null;
		}
		else
		{
			for (Row row : rs)
			{
				results.add(row.getString("Username"));
			}
		}
		
		session.close();
		
		// Now sort the list.
		Collections.sort(results.subList(1, results.size()));

		return results;
	}
	
	/**
	 * Check if a user exists.
	 */
	public boolean userExists(String username)
	{
		// Now make a request for this query.
		Session session = cluster.connect(keyspace);
		
		Select.Where querySEL = QueryBuilder.select()
				.from(this.keyspace, "Users")
				.where(QueryBuilder.eq("Username", username));
		
		ResultSet rs = session.execute(querySEL.toString());
		
		if (rs.isExhausted())
		{
			session.close();
			return false;
		}
		else
		{
			session.close();
			return true;
		}
	}
	
	/**
	 * Check if a user is following another user.
	 */
	public boolean checkIfFollowing(String byWho, String who)
	{
		Session session = cluster.connect(keyspace);
		
		Select.Where querySEL = QueryBuilder.select()
				.from(this.keyspace, "Following")
				.where(QueryBuilder.eq("Username", byWho))
				.and(QueryBuilder.eq("Following", who));

		ResultSet rs = session.execute(querySEL.toString());
		
		if (rs.isExhausted())
		{
			session.close();
			return false;
		}
		else
		{
			session.close();
			return true;
		}
	}
	
	/**
	 * Start following a user.
	 */
	public void startFollowing(String whoUsername,String followsUsername)
	{
		if(!this.checkIfFollowing(whoUsername, followsUsername))
		{
			// Insert the info.
			Session session = cluster.connect(keyspace);
			
			// Add the record - which user is following which
			Insert queryINS = QueryBuilder.insertInto(this.keyspace, "Following")
					.value("Username", whoUsername)
		            .value("Following", followsUsername);
	
			ResultSet rs = session.execute(queryINS.toString());
			
			// Add the record - which user is followed by which
			queryINS = QueryBuilder.insertInto(this.keyspace, "Followed")
					.value("Username", followsUsername)
		            .value("Followed", whoUsername);
			
			rs = session.execute(queryINS.toString());
			
			// Incerement userstats
			
			if (rs.isExhausted()) 
			{
				Update.Where queryUPD = QueryBuilder
						.update("User_stats")
						.with(QueryBuilder.incr("Following"))
						.where(QueryBuilder.eq("Username",whoUsername));
				
				rs = session.execute(queryUPD.toString());
				
				queryUPD = QueryBuilder
						.update("User_stats")
						.with(QueryBuilder.incr("Followers"))
						.where(QueryBuilder.eq("Username",followsUsername));
	
				rs = session.execute(queryUPD.toString());
				
				session.close();
			}
			else
			{
				session.close();
			}
		}
	}	
	
	/**
	 * End following a user.
	 */
	public void endFollowing(String whoUsername,String unfollowsUsername)
	{
		if(this.checkIfFollowing(whoUsername, unfollowsUsername))
		{
			// Insert the info.
			Session session = cluster.connect(keyspace);
			
			// Delete the records.
			Delete.Where queryDEL = QueryBuilder.delete()
					.from(this.keyspace, "Following")
					.where(QueryBuilder.eq("Username", whoUsername))
					.and(QueryBuilder.eq("Following", unfollowsUsername));
	
			ResultSet rs = session.execute(queryDEL.toString());
			
			
			queryDEL = QueryBuilder.delete()
					.from(this.keyspace, "Followed")
					.where(QueryBuilder.eq("Username", unfollowsUsername))
					.and(QueryBuilder.eq("Followed", whoUsername));
			
			rs = session.execute(queryDEL.toString());
			
			if (rs.isExhausted())
			{
				Update.Where queryUPD = QueryBuilder
						.update("User_stats")
						.with(QueryBuilder.decr("Following"))
						.where(QueryBuilder.eq("Username",whoUsername));
				
				rs = session.execute(queryUPD.toString());
				
				queryUPD = QueryBuilder
						.update("User_stats")
						.with(QueryBuilder.decr("Followers"))
						.where(QueryBuilder.eq("Username",unfollowsUsername));
	
				rs = session.execute(queryUPD.toString());
				
				session.close();
			}
			else
			{
				session.close();
			}
		}
	}
	
	/**
	 * Get user statistics.
	 */
	public UserStore setUserStats(UserStore us)
	{
		String username = us.getUsername();
		//System.out.println(username);
		// Insert the info.
		Session session = cluster.connect(keyspace);
		
		// Add the record - which user is following which
		Select.Where querySEL = QueryBuilder.select()
				.from(this.keyspace, "User_stats")
				.where(QueryBuilder.eq("Username", username));
		
		ResultSet rs = session.execute(querySEL.toString());
		
		if (!rs.isExhausted())
		{
			Row row = rs.one();
			
			if(row != null)
			{
				us.setFollowerCount((int)row.getLong("Followers"));
				us.setFollowingCount((int)row.getLong("Following"));
				us.setTweetCount((int)row.getLong("Tweets"));
			}
			else
			{
				us.setFollowerCount(0);
				us.setFollowingCount(0);
				us.setTweetCount(0);
			}

			session.close();
		}
		else
		{
			session.close();
		}
		
		return us;
	}
	
	/**
	 * Update profile data for a user.
	 */
	public UserStore updateProfile(UserStore us, String email, String about)
	{
		// TODO: clean passed data.
		
		Session session = cluster.connect(keyspace);
		
		if(Convertors.isValidEmail(email))
		{
			us.setEmail(email);
		}
	
		us.setAbout(about);

		
		// Add the record - which user is following which
		Update.Where queryUPD = QueryBuilder
				.update("Users")
				.with(QueryBuilder.set("Email", us.getEmail()))
				.and(QueryBuilder.set("About", us.getAbout()))
				.where(QueryBuilder.eq("Username",us.getUsername()));
		
		ResultSet rs = session.execute(queryUPD.toString());

		session.close();

		return us;
	}
	
	/**
	 * Update password for a user.
	 */
	public UserStore changePassword(UserStore us, String oldPassword,String newPassword,String newPasswordConf) throws WrongPasswordException, PasswordsDontMatchException, PasswordCantBeEmptyExceotion
	{
		// Check if all data is enterd.
		if(oldPassword == null)
		{
			throw new PasswordCantBeEmptyExceotion();
		}
		else
		{
			if(oldPassword.length() < 1)
			{
				// warn that no password has been enterd
				throw new PasswordCantBeEmptyExceotion();
			}
		}
		
		if(newPassword == null)
		{
			throw new PasswordCantBeEmptyExceotion();
		}
		else
		{
			if(newPassword.length() < 1)
			{
				// warn that no password has been enterd
				throw new PasswordCantBeEmptyExceotion();
			}
		}
		
		if(newPasswordConf == null)
		{
			throw new PasswordCantBeEmptyExceotion();
		}
		else
		{
			if(newPasswordConf.length() < 1)
			{
				// warn that no password has been enterd
				throw new PasswordCantBeEmptyExceotion();
			}
		}
		
		// Check if new passwords match.
		if(newPassword.equals(newPasswordConf))
		{
			// Check if old password was valid.
			String oldHashed = "";
			
			try
			{
				oldHashed = this.hashPass(oldPassword);
			}
			catch(UnsupportedEncodingException e)
			{
				// Exception thrown, don't log in the user.
				return us;
			}		
			catch(java.security.NoSuchAlgorithmException e)
			{
				// Exception thrown, don't log in the user.
				return us;
			}
			
			Session session = cluster.connect(keyspace);
			
			Select.Where querySEL = QueryBuilder.select()
					.from(this.keyspace, "users")
					.allowFiltering()
					.where(QueryBuilder.eq("Username", us.getUsername()))
					.and(QueryBuilder.eq("Password", oldHashed));
			
			ResultSet rs = session.execute(querySEL);
			
			if (rs.isExhausted())
			{
				//System.out.println("No users found.");
				
				session.close();
				
				throw new WrongPasswordException();
			}
			else
			{
				// update password.
				

				try
				{
					newPassword = this.hashPass(newPassword);
					
					// Update the password.
					Update.Where queryUPD = QueryBuilder
							.update("Users")
							.with(QueryBuilder.set("Password", newPassword))
							.where(QueryBuilder.eq("Username",us.getUsername()));
					
					rs = session.execute(queryUPD.toString());
				}
				catch(UnsupportedEncodingException e)
				{
					// Exception thrown, don't log in the user.
					return us;
				}		
				catch(java.security.NoSuchAlgorithmException e)
				{
					// Exception thrown, don't log in the user.
					return us;
				}
				
				session.close();
				
				return us;
			}
		}
		else
		{
			// Throw exception
			throw new PasswordsDontMatchException();
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
