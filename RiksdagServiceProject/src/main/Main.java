package main;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * This Module can do 2 things.
 * 1. Get all data at once from riksdags api called Jumbo,
 * and write it to the disk as json file.
 * 2. Get data for a given name from jumbo.json file. 
 * This is called Bulk because names are taken from jumbo.json file one by one,
 * and all json files for everyone are created in one go.
 * To run this initiate an Object with one of commands : JUMBO or BULK.
 * (or just run it without commands)
 * This is designed to do every one of the commands in a thread and pause;
 * the idea is to call JUMBO once a month for example, and then generate 
 * jsons for everyone. Those personal json files could be accessed later, often,
 * without overloading the server.
 * 
 * //TODO simple on off buttons GUI + while loop + timeintervall pause
 * 
 * @author lukas
 *
 */
public class Main implements Runnable{
	boolean isRunning = true;
	String host = "data.riksdagen.se/personlista/?";
	int port = 80;
	InetAddress addr;
	Socket socket;
	int waitTime =3600;
	String filesFolder="files";
	
	public enum DataType {JUMBO, BULK};
	private DataType command;
	
	public Main(DataType command, int timeIntervalHours,String saveFolder) {
		this.command=command;
		this.waitTime*=timeIntervalHours;
		this.filesFolder=saveFolder;
	}
	
	public Main(DataType command) {
		this.command=command;
	}
	
	

	/**
	 * works in IDE or when compiled, it works in console with argument JUMBO or BULK
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("arg:  DataType.[ JUMBO | BULK ]");
		if(args.length==0) {
			Thread jumboThread = new Thread(new Main(DataType.JUMBO));
			jumboThread.start();
			Thread bulkThread = new Thread(new Main(DataType.BULK));
			bulkThread.start();	
		}else if(args[0]=="JUMBO") {
			Thread jumboThread= new Thread(new Main(DataType.JUMBO));
			jumboThread.start();	
		}else if(args[0]=="BULK") {
			Thread bulkThread = new Thread(new Main(DataType.BULK));
			bulkThread.start();		
		}
	}
	
	
	

	@Override
	public void run() {
		switch (command) {
		case JUMBO:
			writeJumboFile(getJumbo());
		break;
		case BULK:
			makeBulkPersonalFiles();
		break;
	}
		
	}

	/**
	 * fetching people one by one from jumbo.json file and making files for everyone.
	 * jumbo.json should be fetched and made first with getJumbo() and writeJumbo();
	 * 
	 * @param data
	 * @return
	 */
	public void makeBulkPersonalFiles() {
		JSONObject jsonObj = new JSONObject(readFile(filesFolder+"/"+"jumbo.json"));
		JSONObject personlistaObj = jsonObj.getJSONObject("personlista");
		JSONArray personArray = personlistaObj.getJSONArray("person");
		JSONObject person;
		String data =null;
	//	System.out.println(personArray);
		for (int i = 0; i < personArray.length(); i++) {
			person = personArray.getJSONObject(i);
//			System.out.println(person);
			String[] tilltalsnamn = person.getString("tilltalsnamn").split(" ");
			String[] efternamn = person.getString("efternamn").split(" ");
			String sorteringsnamn = person.getString("sorteringsnamn");
			
			//allot of ifs BUT constant complexity
			if(efternamn.length==1) {
				if(tilltalsnamn.length==1) {
					System.out.println(tilltalsnamn[0]+" "+efternamn[0]);
					data = getPerson(tilltalsnamn[0], efternamn[0]);
					System.out.println(data);
					writePersonalFile(tilltalsnamn[0], efternamn[0], data);
					
				}else if (tilltalsnamn.length==2) {
					System.out.println(tilltalsnamn[0]+""+tilltalsnamn[1]+" "+efternamn[0]);
					data = getPerson(tilltalsnamn[0]+""+tilltalsnamn[1], efternamn[0]);
					System.out.println(data);
					writePersonalFile(tilltalsnamn[0]+""+tilltalsnamn[1], efternamn[0], data);
				}
			}else if (efternamn.length==2) {
				if(tilltalsnamn.length==1) {
					System.out.println(tilltalsnamn[0]+" "+efternamn[0]+""+efternamn[1]);
					data = getPerson(tilltalsnamn[0], efternamn[0]+""+efternamn[1]);
					System.out.println(data);
					writePersonalFile(tilltalsnamn[0], efternamn[0]+""+efternamn[1], data);
				}else if (tilltalsnamn.length==2) {
					System.out.println(tilltalsnamn[0]+""+tilltalsnamn[1]+" "+efternamn[0]+""+efternamn[1]);
					data = getPerson(tilltalsnamn[0]+""+tilltalsnamn[1], efternamn[0]+""+efternamn[1]);
					System.out.println(data);
					writePersonalFile(tilltalsnamn[0]+""+tilltalsnamn[1], efternamn[0]+""+efternamn[1], data);
				}
			}
			
			try {
				Thread.sleep(100); // must sleep to for riksdagens server not to crush
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}

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
	
	/**
	 * gets all "Jumbo" data from Riksdag Api
	 * @return
	 */
	private String getJumbo() {
		System.out.println("START - fetching Jumbo... wait...");
		StringBuilder temp = new StringBuilder();
		try {
			// setup html connection
			URL url = new URL("http://data.riksdagen.se/personlista/?iid=&fnamn=&enamn=" + "&f_ar=&kn=&parti="
					+ "&valkrets=&rdlstatus=&org=&utformat=json&termlista=");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");
			// catch server error response
			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}
			// setup stream for reading from connection
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream()), "UTF-8"));
			System.out.println("Output from Server .... \n");
			// change stream data to a string
			String tempOutput = "";
			while ((tempOutput = br.readLine()) != null) {
				temp.append(tempOutput + "\n");
//				System.out.println(tempOutput);
			}
			// close connection
			conn.disconnect();

			// System.out.println(temp);

		} catch (MalformedURLException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		}
		System.out.println("STOP - fetching Jumbo");
		return temp.toString();
	}

	/**
	 * makes json file for a Jumbo data 
	 * @param data
	 * @return
	 */
	public boolean writeJumboFile(String data) {
		try {
			FileWriter fw = new FileWriter(filesFolder+"/"+"jumbo.json");
			fw.write(data);
			fw.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * returns a json Data for a given person from riksdag api
	 * not used in automation. NOT EFFICIENT to download people one by one
	 * @param fName
	 * @param lName
	 * @return
	 */
	public String getPerson(String fName, String lName) {
		System.out.println("START - geting person...wait");
		StringBuilder temp = new StringBuilder();
		try {
			// setup html connection
			URL url = new URL("http://data.riksdagen.se/personlista/?fnamn=" + fName + "&enamn=" + lName
					+ "&utformat=json&termlista=");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");
			// catch server error response
			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}
			// setup stream for reading from connection
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream()), "UTF-8"));
			System.out.println("Output from Server .... \n");
			// change stream data to a string
			String tempOutput = "";
			while ((tempOutput = br.readLine()) != null) {
				temp.append(tempOutput + "\n");
//				System.out.println(tempOutput);
			}
			// close connection
			conn.disconnect();

		} catch (MalformedURLException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		}
		System.out.println("STOP - geting person");
		return temp.toString();
	}

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





}
