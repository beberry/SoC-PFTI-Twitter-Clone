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


import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.UUID;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

import lv.beberry.quote.lib.*;
import lv.beberry.quote.stores.TweetStore;


public class TweetModel {
	
	private String keyspace = "quotes";
	Cluster cluster;
	public TweetModel(){
		
	}

	public void setCluster(Cluster cluster){
		this.cluster=cluster;
	}
	
	
	//CREATE TABLE Tweets (Id uuid Primary Key,Username varchar, UserId uuid, Tweet varchar,Retweets int,Date timestamp);
	
	public LinkedList<TweetStore> getTweetsBy(String author,int limitFrom, int limitTo) {
		
		// TODO: implement limits
		LinkedList<TweetStore> tweetList = new LinkedList<TweetStore>();
		Session session = cluster.connect(keyspace);
		
		PreparedStatement statement = session.prepare("SELECT * FROM qUOTES.Main_timeline WHERE Author='"+author+"' ORDER BY Quote_id DESC;");
		BoundStatement boundStatement = new BoundStatement(statement);
		
		ResultSet rs = session.execute(boundStatement);
		
		if (rs.isExhausted()) {
			System.out.println("No Tweets returned");
		} else {
			for (Row row : rs) {
				TweetStore ts = new TweetStore();
				ts.setTweet(row.getString("Text"));
				ts.setUser(row.getString("Author"));
				ts.setTimeUuid(row.getUUID("Quote_id"));
				tweetList.add(ts);
			}
		}
		session.close();
		return tweetList;
	}
	
	/**
	 *  Get all the tweets from the users that you are following.
	 */
	public LinkedList<TweetStore> getTweetsFollowing(String userId)
	{
		LinkedList<TweetStore> tweetList = new LinkedList<TweetStore>();
		
		UserModel um = new UserModel();
		um.setCluster(cluster);
		
		ArrayList<String> followingList  = um.getFollowedUsers(userId);
		
		followingList.add(userId); // Add this users tweets as well.
		
		// Now get Last 15 tweets from all the users that you are following.
		
		Session session = cluster.connect(keyspace);
		
		for (int i=0; i<followingList.size(); i++)
		{
			PreparedStatement statement = session.prepare("SELECT * FROM main_timeline WHERE Author='"+followingList.get(i)+"' ORDER BY Quote_id DESC LIMIT 15;");
			BoundStatement boundStatement = new BoundStatement(statement);
			
			ResultSet rs = session.execute(boundStatement);
			
			if (rs.isExhausted()) {
				System.out.println("No Tweets returned for user "+followingList.get(i));
			} else {
				for (Row row : rs) {
					TweetStore ts = new TweetStore();
					ts.setTweet(row.getString("Text"));
					ts.setUser(row.getString("Author"));
					ts.setTimeUuid(row.getUUID("Quote_id"));
					
					tweetList.add(ts);
				}
			}
		}
		// TODO: Sort the tweets, and output just ~ 20
		session.close();
		
		Collections.sort(tweetList);
		
		return tweetList;
	}
	
	public boolean addTweets(String username,String tweet)
	{
		
		/* String query = "INSERT INTO " + keyspace_name + "." + column_family + " (" + column_names + ") VALUES (" + column_values + ");";
		statement = session.prepare(query);
		boundStatement = new BoundStatement(statement);
		
		boundStatement.setString(0, key);
		boundStatement.setString(1, subColNames[k]);
		boundStatement.setMap(2, colValues);
		session.execute(boundStatement);*/
		
		
		String tweetUuid = Convertors.getTimeUUID().toString();
		
		Session session = cluster.connect(keyspace);
		// TODO: enscape strings!!!!!!
		// Insert into the main timeline
		PreparedStatement statement = session.prepare("INSERT INTO main_timeline (Author, Quote_id, Text, Retweets) values('"+username+"',"+tweetUuid+",'"+tweet+"',0);");
		
		BoundStatement boundStatement = new BoundStatement(statement);
		ResultSet rs = session.execute(boundStatement);
		
		
		System.out.println("aaainseeery");
		
		
		if (rs.isExhausted()) {
			System.out.println("Problems when inserting data");
			
			session.close();
			
			return true;
		} else {
			//..
			System.out.println("Tweet Added..");
			session.close();
			
			return false;
		}
		
	}
}
