package a6;

import org.json.JSONObject;

import a5.*;
import a7.MainClient;
import a7.Servlet;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
	public CritterWorld cw;
	public Controller c;
	public View v;

	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * Method to launch application
	 */
	@Override
	public void start(Stage s) throws Exception {
		Constants.read("src/constants.txt");
		MainClient.main(new String[]{"http://localhost:8080/Assignment-7/CritterWorld/world"});
		JSONObject j = new JSONObject(MainClient.getResponse().toString());
		cw = new CritterWorld(j);
		v = new View(s, cw);
		c = new Controller(v, cw);
		s.show();
	}
}
