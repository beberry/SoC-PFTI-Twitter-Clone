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

import org.apache.commons.lang.*;

import javax.servlet.http.HttpSession;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.querybuilder.*;
import com.datastax.driver.core.querybuilder.Update.Where;

import lv.beberry.quote.lib.*;
import lv.beberry.quote.stores.TweetStore;
import lv.beberry.quote.stores.UserStore;


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
		
		if(followingList == null)
		{
			followingList = new ArrayList<String>();
		}
		
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
	
	public boolean canTweetBeDeleted(String byWho, String author, String tweetId)
	{
		// Check if the author is the one who's trying to delete.
		
		UserModel um = new UserModel();
		um.setCluster(cluster);
		
		UserStore us = new UserStore();
		
		if(byWho.equals(author))
		{

			return true;
		}
		else
		{			System.out.println(author+"lol"+byWho);
			us = um.getUser(byWho);
			
			if(us != null)
			{
				if(us.hasRights("DELETE_TWEET"))
				{
					return true;
				}
			}
			
			return false;
		}
	}
	
	public TweetStore getTweet(String author, String tweetId)
	{
		Session session = cluster.connect(keyspace);
		
		PreparedStatement statement = session.prepare("SELECT * FROM main_timeline WHERE Author='"+author+"' AND Quote_id="+tweetId+";");
		BoundStatement boundStatement = new BoundStatement(statement);
		
		ResultSet rs = session.execute(boundStatement);
		
		if (rs.isExhausted()) {
			System.out.println("The tweet with id "+tweetId+" for user "+author+" was not found." );
			
			return null;
			
		} else {
			Row row = rs.one();
			
				TweetStore ts = new TweetStore();
				ts.setTweet(row.getString("Text"));
				ts.setUser(row.getString("Author"));
				ts.setTimeUuid(row.getUUID("Quote_id"));
				
				return ts;
		}
	}
	
	public boolean deleteTweet(String owner, String quoteId)
	{		
		// Delete the tweet.
		System.out.println(quoteId);
		Session session = cluster.connect(keyspace);

		PreparedStatement statement = session.prepare("DELETE FROM Main_timeline WHERE Author='"+owner+"' AND Quote_id="+quoteId+"");
		
		System.out.println(statement.getQueryString());
		
		BoundStatement boundStatement = new BoundStatement(statement);
		ResultSet rs = session.execute(boundStatement);
		
		
		
		if (rs.isExhausted()) {
			
			System.out.println("Tweet deleted..");
			
			statement = session.prepare("UPDATE User_stats SET Tweets=Tweets-1 WHERE Username='"+owner+"'");
			boundStatement = new BoundStatement(statement);
			
			rs = session.execute(boundStatement);
			
			return true;
		} else {
			//..
			System.out.println("Problems when deleting data");
	
			
			session.close();
			
			return false;
		}
		
	}	
	
	public TweetStore getQuote(String id)
	{
		TweetStore ts;
		
		Session session = cluster.connect(keyspace);
		
		PreparedStatement statement = session.prepare("SELECT * FROM main_timeline WHERE Quote_id="+id+" ALLOW FILTERING;");
		BoundStatement boundStatement = new BoundStatement(statement);
		
		ResultSet rs = session.execute(boundStatement);
		
		if (rs.isExhausted()) {
			System.out.println("The tweet with id "+id+" was not found." );
			
			return null;
			
		} else {
			Row row = rs.one();
			
				ts = new TweetStore();
				ts.setTweet(row.getString("Text"));
				ts.setUser(row.getString("Author"));
				ts.setTimeUuid(row.getUUID("Quote_id"));
				
				return ts;
		}
	}
	
	public boolean addTweets(String username,String tweet)
	{
		//String tweetUuid = Convertors.getTimeUUID().toString();
		
		Session session = cluster.connect(keyspace);
		
		tweet = StringEscapeUtils.escapeHtml(tweet);

		System.out.println(tweet);
		
		// Insert into the main timeline
		Insert queryINS = QueryBuilder.insertInto(this.keyspace, "main_timeline")
				.value("Author", username)
	            .value("Quote_id", Convertors.getTimeUUID())
	            .value("Text", tweet)
	            .value("Retweets", 0);
		
		ResultSet rs = session.execute(queryINS.toString());
		
		
		System.out.println("aaainseeery");
		
		
		if (rs.isExhausted()) {
			System.out.println("Tweet Added..");
			
			Update.Where queryUPD = QueryBuilder.update("User_stats").with(QueryBuilder.incr("Tweets")).where(QueryBuilder.eq("Username",username));
			


			rs = session.execute(queryUPD.toString());
			session.close();
			
			return true;
		} else {
			//..
			System.out.println("Problems when inserting data");
			

			
			session.close();
			
			return false;
		}
		
	}
}
