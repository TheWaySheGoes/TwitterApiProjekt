package api;

import static spark.Spark.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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

		get("/hello", (req, res) -> {
			ui.log("/hello from ip: " + req.ip());
			return "Hello spark!!!";
		});

		get("/parti", (req, res) -> {
			res.header("Content-Type", "application/json");
			
			ui.log("/parti from ip: " + req.ip());

			return "Alla partier";
		});

		get("/parti/:p", (req, res) -> {
			res.header("Content-Type", "application/json");
			String parti = req.params(":p");
			
			ui.log("/parti/" + parti +" from ip: " + req.ip());

			if (parti.equals("s") || parti.equals("S")) {
				return "Socialdemokraterna";
			} else if (parti.equals("m") || parti.equals("M")) {
				return "Moderaterna";
			} else {
				res.status(400);
				return null;
			}
		});

		get("/parliamentMembers", (req, res) -> {
			res.header("Content-Type", "application/json");
			
			ui.log("/parliamentMembers from ip: " + req.ip());
			
			return readFile("files/jumbo.json");
		});

		get("/parliamentMember/:id", (req, res) -> {
			res.header("Content-Type", "application/json");
			String id = req.params(":id");
			
			ui.log("/parliamentMember/" + id + " from ip: " + req.ip());
			
			return "Ledarmöte med id " + id;
		});

		get("/tweets/:amount/:id", (req, res) -> {
			res.header("Content-Type", "application/json");
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
		} catch (IOException e) {
			e.printStackTrace();
		}
		return tempStr.toString();

	}

	public void stop() {
		Spark.stop();
	}

}