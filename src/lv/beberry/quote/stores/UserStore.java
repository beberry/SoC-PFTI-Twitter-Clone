package lv.beberry.quote.stores;

import java.util.HashSet;
import java.util.Set;

/**
 * 
 * @author beberry
 * 
 * This is a bean that will be used for the user authentication system.
 */

public class UserStore
{
	private String username;
	private String email;
	private String hashedPass;
	private String about;
	
	private int tweetCount;
	private int followerCount;
	private int followingCount;

	private Set<String> permissionsGranted;  // Not used in this version.

	public boolean valid;
    
	public UserStore()
	{
		this.permissionsGranted = new HashSet<String>();
		this.about = "";
	}
	
	public int getTweetCount()
	{
		return this.tweetCount;
	}
	
	public void setTweetCount(int count)
	{
		this.tweetCount = count;
	}
	
	public int getFollowingCount()
	{
		return this.followingCount;
	}
	
	public void setFollowingCount(int count)
	{
		this.followingCount = count;
	}
	
	public int getFollowerCount()
	{
		return this.followerCount;
	}
	
	public void setFollowerCount(int count)
	{
		this.followerCount = count;
	}
	
	// Get the username of this user.
	public String getUsername()
	{
		return this.username;
	}
	
	// Set the username of this user.
	public void setUsername(String username)
	{
		this.username = username;
	}    
	
	public String getAbout()
	{
		return this.about;
	}
	
	public void setAbout(String about)
	{
		if(about == null)
		{
			this.about = "";
		}
		else
		{
			this.about = about;
		}
	}
	
	// Get the email of this user.
	public String getEmail()
	{
		return this.email;
	}
	
	// Set the email of this user.
	public void setEmail(String email)
	{
		this.email = email;
	}
	
	public void setPermissions(Set<String> permissions)
	{
		this.permissionsGranted = permissions;
	}
	
	// Get the hashed password.
	public String getHashedPass()
	{
		return this.hashedPass;
	}
	
	// Set the hashed password.
	public void setHashedPass(String hashedPass)
	{
		this.hashedPass = hashedPass;
	}
	
	// Check if this user has been validated.
	public boolean isValid()
	{
		return this.valid;
	}
	
	// Set the state of this user.
	public void setValid(boolean state)
	{
		this.valid = state;
	}
	
	public boolean hasRights(String permission)
	{
		if(this.permissionsGranted.contains(permission))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
}