package lv.beberry.quote.lib;


import java.util.ArrayList;
import java.util.List;

import com.datastax.driver.core.*;

public final class Keyspaces {

	public Keyspaces(){
		
	}
	
	public static void SetUpKeySpaces(Cluster c){
		
		ArrayList<String> commands = new ArrayList<String>();
		
		commands.add("CREATE KEYSPACE IF NOT EXISTS Quotes WITH replication = {'class':'SimpleStrategy', 'replication_factor':1}");
		commands.add("CREATE TABLE IF NOT EXISTS Quotes.Users (Username varchar, Dateuuid uuid, Email varchar,Password varchar, Followers int, Following int, PRIMARY KEY(Username, Dateuuid))");
		commands.add("CREATE INDEX IF NOT EXISTS passwordIndex ON Quotes.Users (Password)");
		commands.add("CREATE TABLE IF NOT EXISTS Quotes.User_dictionary (Dict_Key varchar, Username varchar, PRIMARY KEY(Dict_Key,Username))");
		commands.add("CREATE TABLE IF NOT EXISTS Quotes.Main_timeline (Author varchar, Quote_id uuid, Text varchar, Retweets int, PRIMARY KEY(Author,Quote_Id))");
		commands.add("CREATE TABLE IF NOT EXISTS Quotes.Following (Username varchar, Following varchar, PRIMARY KEY(Username,Following))");
		commands.add("CREATE TABLE IF NOT EXISTS Quotes.Followed (Username varchar, Followed varchar, PRIMARY KEY(Username,Followed))");
		
		Session session = c.connect();
		
		try
		{
			for(int i=0; i<commands.size(); i++)
			{
				SimpleStatement cql = new SimpleStatement(commands.get(i));
				session.execute(cql);
			}

			System.out.println("Keyspace created");

		}
		catch(Exception e)
		{
			System.out.println("Problems when creating keyspace: "+e.getMessage());
		}
		
		session.close();
	}
}
