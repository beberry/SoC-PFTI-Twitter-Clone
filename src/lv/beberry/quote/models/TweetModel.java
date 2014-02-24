package lv.beberry.quote.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.UUID;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.querybuilder.*;

import lv.beberry.quote.lib.*;
import lv.beberry.quote.stores.TweetStore;
import lv.beberry.quote.stores.UserStore;


public class TweetModel {
	
	private String keyspace = "quotes";
	private Cluster cluster;
	public TweetModel(){
		
	}

	public void setCluster(Cluster cluster){
		this.cluster=cluster;
	}
	
	/**
	 * Get quotes from a specific user.
	 */
	public LinkedList<TweetStore> getTweetsBy(String author,int limitFrom, int limitTo)
	{
		// TODO: implement limits
		Session session = cluster.connect(keyspace);
		LinkedList<TweetStore> tweetList = new LinkedList<TweetStore>();
	
		Select querySEL = QueryBuilder.select()
				.from(this.keyspace, "main_timeline")
				.where(QueryBuilder.eq("Author", author))
				.orderBy(QueryBuilder.desc("Quote_id"));

		ResultSet rs = session.execute(querySEL.toString());
		
		if (rs.isExhausted())
		{
			//System.out.println("No Tweets returned");
		}
		else
		{
			for (Row row : rs) 
			{
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
	 * Get all the quotes from the users that you are following.
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
			Select querySEL = QueryBuilder.select()
					.from(this.keyspace, "main_timeline")
					.where(QueryBuilder.eq("Author", followingList.get(i)))
					.limit(15)
					.orderBy(QueryBuilder.desc("Quote_id"));
			
			ResultSet rs = session.execute(querySEL.toString());
			
			if (rs.isExhausted()) {
				//System.out.println("No Tweets returned for user "+followingList.get(i));
			} else {
				for (Row row : rs)
				{
					TweetStore ts = new TweetStore();
					ts.setTweet(row.getString("Text"));
					ts.setUser(row.getString("Author"));
					ts.setTimeUuid(row.getUUID("Quote_id"));
					
					tweetList.add(ts);
				}
			}
		}
		
		// TODO: output just ~ 20 tweets
		session.close();
		
		Collections.sort(tweetList);
	
		return tweetList;
	}
	
	/**
	 * Check if a quote/tweet can be deleted by a specific user.
	 */
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
		{
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
	
	/**
	 * Get a tweet by its id and authors name.
	 */
	public TweetStore getTweet(String author, String quoteId)
	{
		Session session = cluster.connect(keyspace);
		
		Select querySEL = QueryBuilder.select()
				.from(this.keyspace, "main_timeline")
				.where(QueryBuilder.eq("Author", author))
				.and(QueryBuilder.eq("Quote_id", quoteId))
				.limit(15)
				.orderBy(QueryBuilder.desc("Quote_id"));

		ResultSet rs = session.execute(querySEL.toString());
		
		if (rs.isExhausted())
		{
			//System.out.println("The tweet with id "+quoteId+" for user "+author+" was not found." );
			
			return null;
		}
		else
		{
			Row row = rs.one();

			TweetStore ts = new TweetStore();
			ts.setTweet(row.getString("Text"));
			ts.setUser(row.getString("Author"));
			ts.setTimeUuid(row.getUUID("Quote_id"));
			
			return ts;
		}
	}
	
	/**
	 * Delete a specific quote.
	 */
	public boolean deleteTweet(String owner, String quoteId)
	{
		Session session = cluster.connect(keyspace);
		
		// Check if a tweet exists.
		if(this.getQuote(quoteId) != null)
		{
			Delete.Where queryDEL = QueryBuilder.delete()
					.from(this.keyspace, "main_timeline")
					.where(QueryBuilder.eq("Author", owner))
					.and(QueryBuilder.eq("Quote_id", UUID.fromString(quoteId)));
	
			ResultSet rs = session.execute(queryDEL.toString());
	
			if (rs.isExhausted())
			{
				//System.out.println("Tweet deleted..");
				
				Update.Where queryUPD = QueryBuilder
						.update("User_stats")
						.with(QueryBuilder.decr("Tweets"))
						.where(QueryBuilder.eq("Username",owner));
	
				rs = session.execute(queryUPD.toString());
				
				return true;
			}
			else
			{
				//System.out.println("Problems when deleting data");
	
				session.close();
				
				return false;
			}
		}
		else
		{
			return false;
		}
	}	
	
	/**
	 * Get a quote by a specific id.
	 */
	public TweetStore getQuote(String quoteId)
	{
		TweetStore ts;
		
		Session session = cluster.connect(keyspace);
		
		Select.Where querySEL = QueryBuilder.select()
				.from(this.keyspace, "main_timeline")
				.allowFiltering()
				.where(QueryBuilder.eq("Quote_id", UUID.fromString(quoteId)));

		ResultSet rs = session.execute(querySEL.toString());
		
		if (rs.isExhausted())
		{
			//System.out.println("The tweet with id "+quoteId+" was not found." );
			
			return null;
			
		}
		else
		{
			Row row = rs.one();
			
			ts = new TweetStore();
			ts.setTweet(row.getString("Text"));
			ts.setUser(row.getString("Author"));
			ts.setTimeUuid(row.getUUID("Quote_id"));
			
			return ts;
		}
	}
	
	/**
	 * Add a quote.
	 */
	public boolean addTweets(String username,String tweet)
	{
		Session session = cluster.connect(keyspace);
		
		// Insert into the main timeline
		Insert queryINS = QueryBuilder.insertInto(this.keyspace, "main_timeline")
				.value("Author", username)
	            .value("Quote_id", Convertors.getTimeUUID())
	            .value("Text", tweet)
	            .value("Retweets", 0);
		
		ResultSet rs = session.execute(queryINS.toString());

		if (rs.isExhausted())
		{
			//System.out.println("Tweet Added..");

			Update.Where queryUPD = QueryBuilder
					.update("User_stats")
					.with(QueryBuilder.incr("Tweets"))
					.where(QueryBuilder.eq("Username",username));
			
			rs = session.execute(queryUPD.toString());
			session.close();
			
			return true;
		}
		else
		{
			//System.out.println("Problems when inserting data");
	
			session.close();
			
			return false;
		}
	}
}
