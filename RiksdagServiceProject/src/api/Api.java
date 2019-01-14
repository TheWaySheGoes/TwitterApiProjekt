package api;

import static spark.Spark.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;

import spark.Spark;

/**
 * The API endpoints.
 * 
 * @author André Hansson
 */
public class Api {

	private UI ui;


	public Api(UI ui) {
		this.ui = ui;
	}

	public void start() {
		port(5000);
		
		after((request, response) -> {
		    response.header("Access-Control-Allow-Methods", "GET");
			response.header("Access-Control-Allow-Origin", "*");
			response.header("Content-Type", "application/json");
		});

		get("/party", (req, res) -> {
			ui.log("/party from ip: " + req.ip());

			return readFile("files/party/party.json");
		});

		get("/party/:p", (req, res) -> {
			String parti = req.params(":p");

			ui.log("/party/" + parti + " from ip: " + req.ip());

			String jsonString = readFile("files/party/party.json");

			JSONArray jsonArray = null;

			try {
				jsonArray = new JSONArray(jsonString);
			} catch (Exception e) {
				e.printStackTrace();
			}

			for (Object object : jsonArray) {
				JSONObject party = (JSONObject) object;

				if (party.get("förkortning").equals(parti)) {
					return party;
				}
			}

			res.status(404);
			return "Party not found";
		});

		get("/parliamentMembers", (req, res) -> {
			ui.log("/parliamentMembers from ip: " + req.ip());

			return readFile("files/parliamentMembers/parliamentList.json");
		});
		
		get("/parliamentMembers/:party", (req, res) -> {
			String party = req.params(":party");

			ui.log("/parliamentMembers/" + party + " from ip: " + req.ip());
			
			String jsonString = readFile("files/parliamentMembers/parliamentList.json");

			JSONArray jsonArray = null;

			try {
				jsonArray = new JSONArray(jsonString);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			JSONArray members = new JSONArray();
			
			for (Object object : jsonArray) {
				JSONObject member = (JSONObject) object;

				if (member.get("party").equals(party)) {
					members.put(member);
				}
			}

			return members;
		});

		get("/parliamentMember/:id", (req, res) -> {
			String id = req.params(":id");

			ui.log("/parliamentMember/" + id + " from ip: " + req.ip());
			
			String jsonString = readFile("files/parliamentMembers/parliamentList.json");

			JSONArray jsonArray = null;

			try {
				jsonArray = new JSONArray(jsonString);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			for (Object object : jsonArray) {
				JSONObject member = (JSONObject) object;

				if (member.get("id").equals(id)) {
//					JSONObject person = new JSONObject(readFile("files/" + member.getString("firstName") + "_" + member.getString("lastName") + ".json"));
//										
//					JSONObject personList = person.getJSONObject("personlista");
//										
//					return personList.get("person");
					
					JSONObject person = new JSONObject(readFile("files/parliamentMembers/minimal/" + member.getString("firstName") + "_" + member.getString("lastName") + ".json"));

					return person;
				}
			}

			res.status(404);
			return "Member not found";
		});
		
		
		get("/votes", (req, res) -> {
			ui.log("/votes/ from ip: " + req.ip());
			return readFile("files/votes/voteListDetailed.json");
		});
		
		get("/votes/:id", (req, res) -> {
			String id = req.params(":id");
			
			ui.log("/votes/" + id + " from ip: " + req.ip());
			
			String jsonFile = readFile("files/votes/full/" + id + ".json");
			
			if(jsonFile != null) {
				return jsonFile;
			} else {
				res.status(404);
				return "Votes not found";
			}
		});
		
		get("/tweets/:name", (req, res) -> {
			
			String name = req.params(":name");
			
			String jsonFile = readFile("files/twitter/" + name + ".json");
			
			if(jsonFile != null) {
				return jsonFile;
			} else {
				res.status(404);
				return "Tweets not found";
			}			
		});

		get("/tweets/:amount/:id", (req, res) -> {
			String amount = req.params(":amount");
			String id = req.params(":id");

			ui.log("/tweets/" + amount + "/" + id + " from ip: " + req.ip());

			return amount + " st Tweets från ledarmöte med id " + id;
		});

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
			return null;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return tempStr.toString();

	}

	public void stop() {
		Spark.stop();
	}
	
	public static void main(String[] args) {
		new UI();
	}

}