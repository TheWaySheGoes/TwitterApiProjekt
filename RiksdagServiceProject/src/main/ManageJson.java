package main;


import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Handles JSON- and XML-files from riksdagens API and removes unneeded data,
 * divides them to smaller & more manageable sorted files.
 * @author Henrik
 */
public class ManageJson {

	public static ManageJson jsonHandler = new ManageJson();
	public static final int INDENT_FACTOR = 1;

	public static void main(String[] args) throws MalformedURLException, SAXException, IOException {
		//jsonHandler.getParliamentVotes();
		//jsonHandler.createParliamentMembersList();
		//jsonHandler.getAllVotes();
		jsonHandler.createParliamentMemberFiles();
	}

	/*
	 * Fetches all votes from riksdagen by a given period (2018/2019).
	 */
	public void getParliamentVotes() throws SAXException, MalformedURLException, IOException {
		String allVotes = "http://data.riksdagen.se/voteringlista/?rm=2018%2F19&bet=&punkt=&valkrets=&rost=&iid=&sz=500&utformat=xml&gruppering=votering_id";

		XMLReader myReader = XMLReaderFactory.createXMLReader();
		myReader.setContentHandler(new RiksdagenXMLHandler());
		myReader.parse(new InputSource(new URL(allVotes).openStream()));	
	}
	
	/*
	 * Converts the votes XML document to JSON format and saves it as a list to the the files folder.
	 */
	class RiksdagenXMLHandler extends DefaultHandler {
		JSONArray voteList = new JSONArray();
		JSONObject vote;
		String parameterName = "";
		Map<String, String> map = new TreeMap<String, String>() {
			{ 
				put("Nej", "no");			
				put("Ja", "yes");			
				put("Avstår", "refrains");			
				put("votering_id", "id");			
				put("Frånvarande", "absent");			
			};
		};

		public void startDocument() {}
		public void endDocument() {
			writeFile("votes/voteList", voteList.toString(INDENT_FACTOR));
		}
		public void startElement(String nameSpaceURI, String localName, String qName, Attributes atts) {
			if(qName == "votering") {
				vote = new JSONObject();
			}
			parameterName = qName;
		}
		public void endElement(String nameSpaceURI, String localName, String qName) {
			if(qName == "votering") {
				voteList.put(vote);
			}
		}
		public void characters(char[] ch, int start, int length) {
			if(parameterName != "votering" && parameterName != "voteringslista") {
				String parameterValue = "";
				for (int i = start; i<(start+length);i++) {
					parameterValue += ch[i];
				}
				if(!parameterValue.contains("\n")) {
					vote.put(map.get(parameterName), parameterValue);
				}				
			}			
		}
	}

	/*
	 * Creates one list with minimal information and one with more detailed information about all parliament members.
	 */
	public void createParliamentMembersList() throws MalformedURLException, IOException {
		JSONObject jsonObj = new JSONObject(readFile("files/jumbo.json"));
		JSONObject personlistaObj = jsonObj.getJSONObject("personlista");
		JSONArray personArray = personlistaObj.getJSONArray("person");

		JSONArray parliamentList = new JSONArray();
		JSONArray parliamentDetailedList = new JSONArray();

		for (int i = 0; i < personArray.length(); i++) {
			JSONObject person = personArray.getJSONObject(i);
			JSONObject newPerson = new JSONObject();

			//Creates short list with all parliament members
			String parliamentMemberId = (String) person.get("intressent_id");
			newPerson.put("id",  parliamentMemberId);
			newPerson.put("firstName", person.get("tilltalsnamn"));
			newPerson.put("lastName", person.get("efternamn"));
			newPerson.put("party", person.get("parti"));
			newPerson.put("image", person.get("bild_url_max"));			
			parliamentList.put(newPerson);
			
			//Creates a more detailed list
			JSONObject newPersonFull = new JSONObject(newPerson.toString());
			newPersonFull.put("birthYear", person.get("fodd_ar"));
			newPersonFull.put("constituency", person.get("valkrets"));
			newPersonFull.put("status", person.get("status"));
			JSONArray personExtraInfo = person.getJSONObject("personuppgift").getJSONArray("uppgift");
			for(int k = 0; k < personExtraInfo.length(); k++) { //Gets email and other extra person info.
				if(personExtraInfo.getJSONObject(k).getString("kod").equals("Officiell e-postadress")) {
					newPersonFull.put("email", ((String) personExtraInfo.getJSONObject(k).getJSONArray("uppgift").get(0)).replace("[på]", "@"));
				}
			}
			parliamentDetailedList.put(newPersonFull);
		}		
		writeFile("parliamentMembers/parliamentList", parliamentList.toString(INDENT_FACTOR));
		writeFile("parliamentMembers/parliamentDetailedList", parliamentDetailedList.toString(INDENT_FACTOR));
	}

	/*
	 * Fetches more detailed information about all saved votes and saves them as individual files.
	 */
	public void getAllVotes() throws MalformedURLException, IOException {
		JSONArray voteList = new JSONArray(readFile("files/votes/voteList.json"));
		JSONArray voteNewList = new JSONArray();
		for (int i = 0; i < voteList.length(); i++) {			
			String voteId = voteList.getJSONObject(i).getString("id");		
			writeFileFromURL("files/votes/full/" + voteId + ".json", "http://data.riksdagen.se/votering/" + voteId + "/json");

			JSONObject voteFile = new JSONObject(readFile("files/votes/full/" + voteId + ".json"));
			JSONObject voteInfo = voteFile.getJSONObject("votering").getJSONObject("dokument");

			JSONObject voteShortInfo = voteList.getJSONObject(i);
			voteShortInfo.put("docId", voteInfo.get("dok_id"));
			voteShortInfo.put("title", voteInfo.get("titel"));
			voteShortInfo.put("date", voteInfo.get("datum"));
			voteShortInfo.put("pdfFile", "data.riksdagen.se/fil/" +  voteId);
			writeFile("votes/minimal/" + voteId, voteShortInfo.toString(INDENT_FACTOR));
			voteNewList.put(voteShortInfo);
		}
		writeFile("votes/voteListDetailed", voteNewList.toString(INDENT_FACTOR));
		
	}
	
	/*
	 * Creates a detailed JSON file for every parliament member.
	 */
	public void createParliamentMemberFiles() {
		JSONArray memberList = new JSONArray(readFile("files/parliamentMembers/parliamentDetailedList.json"));		
		JSONObject parliamentMembers = new JSONObject();		
		for(int i = 0; i < memberList.length(); i++) {
			JSONObject parliamentMember = memberList.getJSONObject(i);
			parliamentMember.put("votes", new JSONArray());
			parliamentMembers.put(parliamentMember.getString("id"), parliamentMember);			
		}
		
		JSONArray voteList = new JSONArray(readFile("files/votes/voteListDetailed.json"));
		for(int i = 0; i < voteList.length(); i++) {
			JSONObject vote = voteList.getJSONObject(i);
			String voteTitle = vote.getString("title");
			vote = new JSONObject(readFile("files/votes/full/" + vote.getString("id") + ".json"));
			JSONArray votes = vote.getJSONObject("votering").getJSONObject("dokvotering").getJSONArray("votering");			
			
			for(int k = 0; k < votes.length(); k++) {
				vote = votes.getJSONObject(k);
				JSONObject voteShortInfo = new JSONObject();
				voteShortInfo.put("id", vote.get("votering_id"));
				voteShortInfo.put("vote", vote.get("rost"));
				voteShortInfo.put("date", vote.get("datum"));
				voteShortInfo.put("title", voteTitle);
				if(parliamentMembers.has(vote.getString("intressent_id"))) { //If not off duty or similar
					parliamentMembers.getJSONObject(vote.getString("intressent_id")).getJSONArray("votes").put(voteShortInfo);	
				}							
			}
		}		
		
		for(int i = 0; i < memberList.length(); i++) {
			String id = memberList.getJSONObject(i).getString("id");
			JSONObject parliamentMember = parliamentMembers.getJSONObject(id);
			writeFile("parliamentMembers/minimal/" + parliamentMember.getString("firstName") + "_" + parliamentMember.getString("lastName"), parliamentMember.toString(INDENT_FACTOR));
		}
	}

	public void writeFileFromURL(String fileName, String URL) throws MalformedURLException, IOException {
		InputStream in = new URL(URL).openStream();
		Files.copy(in, Paths.get(fileName), StandardCopyOption.REPLACE_EXISTING);
	}

	public void writeFile(String fileName, String data) {
		System.out.println(fileName);
		try {
			FileWriter fw = new FileWriter("files/" + fileName + ".json");
			fw.write(data);
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	
	
	public static String readFile(String path) {
		FileReader fr;
		StringBuilder tempStr = new StringBuilder();
		try {
			fr = new FileReader(path);

			int tempInt;

			while ((tempInt = fr.read()) != -1) {

				tempStr.append((char) tempInt);
			}
			fr.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return tempStr.toString();

	}


}
