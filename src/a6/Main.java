package a6;

import a5.*;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
	public static CritterWorld cw;
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
		cw = new CritterWorld();
		v = new View(s, cw);
		c = new Controller(v, cw);
		s.show();
	}
	
}
