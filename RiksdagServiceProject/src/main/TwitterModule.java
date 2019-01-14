package main;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterModule {
	ConfigurationBuilder cb;
	TwitterFactory tf;
	 Twitter twitter;
	
	 public TwitterModule() {
		 cb = new ConfigurationBuilder();
		 cb.setDebugEnabled(true)
		   .setOAuthConsumerKey("4kQHu6tcOcF88lMQqr5LbfD7N")
		   .setOAuthConsumerSecret("RbvavJhOFGq5PcY0C20f7ZqHSlMo3sfG7Z9IcMjVE7PKIzpcvr")
		   .setOAuthAccessToken("1082989341423603712-etBuYJr2qvQs5WUazZXqEYUieKP8Wh")
		   .setOAuthAccessTokenSecret("yeCHE02JJOC9wsFUh36rORG5gPzvPEO4YpJoddELBKHkB");
		tf = new TwitterFactory(cb.build());
		  twitter = tf.getInstance();
	 
	 }
	
	
	public  List<Status> getTwitts(String searchQuery) {
		 List<Status> tweets=null; 
		 
		

	        try {

	            Query query = new Query(searchQuery);

	            QueryResult result;

	            do {

	                result = twitter.search(query);

	                tweets = result.getTweets();

	                for (Status tweet : tweets) {

	                    System.out.println("@" + tweet.getUser().getScreenName() + " - " + tweet.getText());

	                }

	            } while ((query = result.nextQuery()) != null);

	            System.exit(0);

	        } catch (TwitterException te) {

	            te.printStackTrace();

	            System.out.println("Failed to search tweets: " + te.getMessage());

	            System.exit(-1);

	        }
	        
		return tweets;
	}
	

}
