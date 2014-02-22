package lv.beberry.quote.stores;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import lv.beberry.quote.lib.Convertors;


public class TweetStore implements Comparable<TweetStore>{
     String Tweet;
     String User;
     UUID timeUuid;
     
     public boolean canBeDeleted;
     
     public String getTweet(){
    	 return Tweet;
     }
     public String getUser(){
    	 return User;
     }
     
     public UUID getTimeUuid()
     {
    	 return this.timeUuid;
     }
     
     public void setTimeUuid(UUID timeUuid)
     {
    	 this.timeUuid = timeUuid;
     }
     
     public void setTweet(String Tweet){
    	 this.Tweet=Tweet;
     }
     public void setUser(String User){
    	 this.User=User;
     }
     
     public String getDateFromUuid()
     {
    	 String dateF = new SimpleDateFormat("MM.dd.yyyy HH:mm").format(Convertors.getTimeFromUUID(this.timeUuid));
    	 
    	 return dateF;
     }
     

     
     @Override
     public int compareTo(TweetStore ts)
     {
    	 if(this.timeUuid.timestamp() < ts.timeUuid.timestamp())
    	 {
    		 return 1;
    	 }
    	 else if(this.timeUuid.timestamp() > ts.timeUuid.timestamp())
    	 {
    		 return -1;
		 }
    	 else
    	 {
    		 return 0;
    	 }
	  }
     
     
}
