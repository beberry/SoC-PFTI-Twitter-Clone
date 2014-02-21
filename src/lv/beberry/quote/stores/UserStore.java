package lv.beberry.quote.stores;


/**
 * 
 * @author beberry
 * 
 * This is a bean that will be used for the user authentication system.
 */

public class UserStore {
	
	private String username;
	private String email;
	private String hashedPass;
	public boolean valid;
    
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
}