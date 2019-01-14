package main;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.json.JSONObject;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterModule implements Runnable{
	ConfigurationBuilder cb;
	TwitterFactory tf;
	 Twitter twitter;
	 String filesFolder="files/twitter";
	 String[] people= {"Annie_Lööf.json", "Ebba_BuschThor.json","Jan_Björklund.json","Ulf_Kristersson.json"
			 ,"Jonas_Sjöstedt.json"};
	 boolean isRunning=true;
	 Thread T = null;
		ModuleGui riksdagModuleGui;
		int sleepTime = 3600000;
	 
	
		
	 public TwitterModule(ModuleGui riksdagModuleGui) {
		 cb = new ConfigurationBuilder();
		 cb.setDebugEnabled(true)
		   .setOAuthConsumerKey("4kQHu6tcOcF88lMQqr5LbfD7N")
		   .setOAuthConsumerSecret("RbvavJhOFGq5PcY0C20f7ZqHSlMo3sfG7Z9IcMjVE7PKIzpcvr")
		   .setOAuthAccessToken("1082989341423603712-etBuYJr2qvQs5WUazZXqEYUieKP8Wh")
		   .setOAuthAccessTokenSecret("yeCHE02JJOC9wsFUh36rORG5gPzvPEO4YpJoddELBKHkB");
		tf = new TwitterFactory(cb.build());
		  twitter = tf.getInstance();
	 
	 }
	
		public void setTimeInterval(int timeInterva) {
			this.sleepTime*= timeInterva;
		}
	 
		/**
		 * reads json file to a string
		 * 
		 * @param path
		 */
		public String readFile(String path) {
			FileReader fr;
			StringBuilder tempStr = new StringBuilder();
			try {
				fr = new FileReader(path);

				int tempInt;

				while ((tempInt = fr.read()) != -1) {

					tempStr.append((char) tempInt);
				}
				// System.out.println(tempStr.toString());
				fr.close();

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return tempStr.toString();

		}
	 
	 public void modifyPersonalFile(String pers,String key,Object value){
		 JSONObject jsonObj = new JSONObject(readFile(filesFolder+"/"+pers));
		 jsonObj.append(key, value);
		 try {
				FileWriter fw = new FileWriter(filesFolder+"/" + pers);
				fw.write(jsonObj.toString());
				fw.close();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
	 
	 };
	 
		/**
		 * makes a json file for a specific person and writes it to the disk
		 * 
		 * @param firstName
		 * @param lastName
		 * @param data
		 * @return
		 */
		public boolean writePersonalFile(String firstName, String lastName, String data) {
			try {
				FileWriter fw = new FileWriter(filesFolder+"/" + firstName + "_" + lastName+".json");
				fw.write(data);
				fw.close();
				return true;
			} catch (IOException e) {
				e.printStackTrace();
			}
			return false;
		}
	 
	/**
	 * gets a list of tweets from api 
	 * @param searchQuery
	 * @return
	 */
	public String  getTwitts(String searchQuery) {
		 List<Status> tweets=null; 
		 StringBuilder returnString =new StringBuilder();
			returnString.append("{\"tweetes\": [");
		 
		

	        try {

	            Query query = new Query(searchQuery);

	            QueryResult result;

	            do {

	                result = twitter.search(query);

	                tweets = result.getTweets();

	                for (Status tweet : tweets) {
	                	returnString.append("{"+"@" + tweet.getUser().getScreenName() + " - " + tweet.getText() +"},");
	                   riksdagModuleGui.displayTxt("@" + tweet.getUser().getScreenName() + " - " + tweet.getText());
	                	System.out.println("@" + tweet.getUser().getScreenName() + " - " + tweet.getText());
	                    
	                }
	                returnString.deleteCharAt(returnString.length()-1);
	                returnString.append("]}");
	                
	            } while ((query = result.nextQuery()) != null);

	           // System.exit(0);

	        } catch (TwitterException te) {

	            te.printStackTrace();

	            System.out.println("Failed to search tweets: " + te.getMessage());

	            System.exit(-1);

	        }
	        
		return returnString.toString();
	}
	
	public void run() {
		while(isRunning) {
		String backString =getTwitts("Jan + Björklund");
		writePersonalFile("Jan", "Björklund", backString);
		
		backString =getTwitts("Annie + Lööf");
		writePersonalFile("Annie", "Lööf", backString);
		
		backString =getTwitts("Ebba + Busch + Thor");
		writePersonalFile("Ebba", "BuschThor", backString);
		
		backString =getTwitts("Ulf + Kristersson");
		writePersonalFile("Ulf", "Kristersson", backString);
		
		backString =getTwitts("Jonas + Sjöstedt");
		writePersonalFile("Jonas", "Sjöstedt", backString);
		try {
			Thread.sleep(sleepTime); // must sleep to for riksdagens server not to crush
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		}
	}
	
	public void stop() {
		if (T != null) {
			isRunning = false;
		}

	}

	public void exit() {
		if (T != null) {
			T.interrupt();
			isRunning = false;
		}
	}

	public void start() {
		if (T == null) {
			T = new Thread(this);
			T.start();
			isRunning = true;

		}

	}
	
	public static void main(String[] args) {
		//TwitterModule tm = new TwitterModule();
	//	tm.run();
	}

}
