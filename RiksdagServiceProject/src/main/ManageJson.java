package main;

import java.io.FileNotFoundException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class ManageJson {
	
	public static void main(String[] args) throws MalformedURLException, SAXException, IOException {
		ManageJson jsonHandler = new ManageJson();
		jsonHandler.getParliamentVotes();
		//createParliamentList();
	}
	
	public void getParliamentVotes() throws SAXException, MalformedURLException, IOException {
		String allVotes = "http://data.riksdagen.se/voteringlista/?rm=2018%2F19&bet=&punkt=&valkrets=&rost=&iid=&sz=500&utformat=xml&gruppering=votering_id";
		
		XMLReader myReader = XMLReaderFactory.createXMLReader();
		myReader.setContentHandler(new RiksdagenXMLHandler());
		myReader.parse(new InputSource(new URL(allVotes).openStream()));
		
		//System.out.println(myReader.getProperty("voteringlista"));
		
		
		try {
			FileWriter fw = new FileWriter("files/allVotes.xml");
			fw.write(myReader.toString());
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
		
	public void createParliamentList() {
		JSONObject jsonObj = new JSONObject(readFile("files/jumbo.json"));
		JSONObject personlistaObj = jsonObj.getJSONObject("personlista");
		JSONArray personArray = personlistaObj.getJSONArray("person");
		
		JSONArray parliamentList = new JSONArray();
		
		for (int i = 0; i < personArray.length(); i++) {
			JSONObject person = personArray.getJSONObject(i);
			JSONObject newPerson = new JSONObject();
				
			newPerson.append("firstName", person.get("tilltalsnamn"));
			newPerson.append("lastName", person.get("efternamn"));
			newPerson.append("party", person.get("parti"));
			newPerson.append("image", person.get("bild_url_max"));
			
			
			parliamentList.put(newPerson);
		}
		
		try {
			FileWriter fw = new FileWriter("files/parliamentList.json");
			fw.write(parliamentList.toString(10));
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
			// System.out.println(tempStr.toString());
			fr.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return tempStr.toString();

	}
	
	class RiksdagenXMLHandler extends DefaultHandler {
		JSONArray voteList = new JSONArray();
		JSONObject vote;
		String parameterName = "";
		
		public void startDocument() {}
		public void endDocument() {
			System.out.println("yo");
			writeFile("allVotes", voteList.toString());
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
					vote.append(parameterName, parameterValue);
				}				
			}			
		}
	}
	
	public void writeFile(String fileName, String data) {
		try {
			FileWriter fw = new FileWriter("files/" + fileName + ".json");
			fw.write(data);
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
