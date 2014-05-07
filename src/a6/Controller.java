package a6;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import a5.*;
import a7.MainClient;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.text.Font;
/**
 * The Controller class that handles user input for CritterWorld. Contains 
 * buttons and information that the user can interact with.
 */
public class Controller {
	Label stepLabel;
	View v;
	CritterWorld cw;
	Label critterLabel;
	VBox warning;
	Label infoLabel;
	Label hexSelected;
	Label position;
	String clicked;
	HexPolygon selected;
	Label hexCritterInfo;
	Label hexRockInfo;
	Label hexFoodInfo;
	int speed;
	VBox hexBox;
	Timeline timeline;
	StringBuffer sb;
	
	/**
	 * Constructor for Controller. 
	 * This method builds and coordinates the different components
	 * of the Controller.
	 * @param v The View of this program
	 * @param cw The current CritterWorld
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws JSONException 
	 */
	public Controller(View v, CritterWorld cw) throws FileNotFoundException, IOException, JSONException{
		this.cw = cw;
		this.v = v;
		v.getVBox().setSpacing(3.5);
		speed = 1000;
		infoLabel = new Label("Hover cursor over a command "
				+ "\nand watch this space for help");
		infoLabel.setFont(Font.font("Comic Sans MS",14));
		infoLabel.setMinHeight(80.0);
		infoLabel.setMaxHeight(80.0);
		v.getVBox().setMinWidth(200.0);
		v.getVBox().setMaxWidth(200.0);
		v.getVBox().getChildren().add(infoLabel);
		createWorld();
		setWorldSteps();
		createCritters();
		zoomSettings();
		hexSelected = new Label("Hex Selected:");
		position = new Label("click on desired Hex");
		v.getVBox().getChildren().add(hexSelected);
		v.getVBox().getChildren().add(position);
		clicked = "";
		hexSelection();
		
		
	}
	/**
	 * Method to create the controls for loading a world and advancing its
	 * steps
	 * Gives information about the world state
	 * Gives controls that allow users to create new worlds and critters
	 */
	void createWorld(){
		stepLabel = new Label("Steps Advanced: 0");
		stepLabel.setFont(Font.font("Copperplate Gothic Bold",14));
		critterLabel = new Label("Critters Alive: 0");
		critterLabel.setFont(Font.font("Copperplate Gothic Bold", 14));
		Button b = new Button("Load World");
		final TextField t = new TextField("");
		v.getVBox().getChildren().addAll(stepLabel, critterLabel,b,t);
		
		b.setOnAction(new EventHandler<ActionEvent>() {
			@Override
            public void handle(ActionEvent _) {
				if(!t.getText().equals("")){
					try{
						BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(t.getText())));
						StringBuffer jb = new StringBuffer();
						String line = null;
						while ((line = reader.readLine()) != null) {
						 	jb.append(line +"\n");
						}
						MainClient.main(new String[]{"http://localhost:8080/Assignment-7/CritterWorld/world", jb.toString()});
						t.setText("");
						MainClient.main(new String[]{"http://localhost:8080/Assignment-7/CritterWorld/world"});
				        System.out.println(MainClient.getResponse().toString());
				        System.out.println("WFAFSFSAFSDA");
						JSONObject j = new JSONObject(MainClient.getResponse().toString());
						stepLabel.setText("Steps Advanced: " + j.getInt("current_timestep"));
						critterLabel.setText("Critters Alive: " + j.getInt("population"));
						cw = new CritterWorld(j);
						cw.update(v);
					}
					catch (FileNotFoundException fnfe){
						warning("The file you specified \nwas in the wrong format!");
					}
					catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else{
					warning("Please supply text!");
				}
				cw.update(v);
            }
        });
		
		b.setOnMouseEntered(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent _){
				infoLabel.setText("Loads the world from the file \nspecified below");
			}
		});
		
		t.setOnMouseEntered(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent _){
				infoLabel.setText("Please Specify a file");
			}
		});
		
		b.setOnMouseExited(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent _){
				infoLabel.setText("Hover cursor over a command "
						+ "\nand watch this space for help");
			}
		});
		
		t.setOnMouseExited(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent _){
				infoLabel.setText("Hover cursor over a command "
						+ "\nand watch this space for help");
			}
		});
	}
	/**
	 * Method to create controls for stepping the world
	 * Gives controls that allows the user to step the world
	 * at different rates
	 */
	void setWorldSteps(){
		Button b = new Button("Advance Steps");
		final Button b1 = new Button("Step Continuously");
		final HBox speedControls = new HBox();
		final Button b2 = new Button("Set step speed to: ");
		final TextField t = new TextField();
		final TextField t2 = new TextField();
		t.setMaxWidth(50.0); 
		t.setMinWidth(50.0);
		speedControls.getChildren().addAll(b2, t);
		v.getVBox().getChildren().addAll(b, t2, b1, speedControls);
		
		timeline = new Timeline(new KeyFrame(Duration.millis(speed), 
				new EventHandler<ActionEvent>(){
				
				@Override
				public void handle(ActionEvent arg0){
					if (b1.getText() == "Stop Stepping"){
						step(1);
					}
					cw.update(v);
				}
		}));
		
		b2.setOnMouseEntered(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent _){
				infoLabel.setText("Allows you to change the \nstepping speed "
						+ "to the number \nof milliseconds specified");
			}
		});
		
		b2.setOnMouseExited(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent _){
				infoLabel.setText("Hover cursor over a command "
						+ "\nand watch this space for help");
			}
		});
		
		t.setOnMouseEntered(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent _){
				infoLabel.setText("Please specify a number \nbetween 50 and 10,000 \n(milliseconds)");
			}
		});
		
		t.setOnMouseExited(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent _){
				infoLabel.setText("Hover cursor over a command "
						+ "\nand watch this space for help");
			}
		});
		
		t2.setOnMouseEntered(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent _){
				infoLabel.setText("Please specify a number \nbetween 50 and 500 \n (number of critters to create)");
			}
		});
		
		t2.setOnMouseExited(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent _){
				infoLabel.setText("Hover cursor over a command "
						+ "\nand watch this space for help");
			}
		});
		
		b2.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent _){
				try{
					if(t.getText()!= null){
						Main.main(new String[]{"http://localhost:8080/Assignment-7/CritterWorld/run?rate="+Integer.parseInt(t.getText()),""});
						//speed = Integer.parseInt(t.getText());
						if(speed<50) {
							speed = 50; 
							warning("That is too fast,speed \nhas been set to 50! ");
						}
						if(speed>10000) {
							speed = 10000; 
							warning("That is too slow,speed \nhas been set to 10,000! ");
						}
						t.setText("");
						timeline = new Timeline(new KeyFrame(Duration.millis(speed), 
								new EventHandler<ActionEvent>(){
								
								@Override
								public void handle(ActionEvent arg0){
									if (b1.getText() == "Stop Stepping"){
										step(1);
									}
									cw.update(v);
								}
						}));
					}
					else{
						warning("Please supply text!");
					}
				}
				catch (NumberFormatException nfe){
					warning("Please give a number \nin the correct format!");
				}
			}
		});
		
		b.setOnAction(new EventHandler<ActionEvent>() {
			@Override
            public void handle(ActionEvent _) {
				if (cw != null) {
					try{
						step(Integer.parseInt(t2.getText()));
					} catch (NumberFormatException n){
						warning("Numbers only!");
					}
				}
				else{
					warning("Please load a world!");
				}
				cw.update(v);
            }
        });
		
		
		b1.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent _){
				if(b1.getText() == "Step Continuously"){
					b1.setText("Stop Stepping");
					timeline.setCycleCount(Timeline.INDEFINITE);
					timeline.play();
				}
				else{
					b1.setText("Step Continuously");
					timeline.stop();
				}
				cw.update(v);
			}
		});
		
		b.setOnMouseEntered(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent _){
				infoLabel.setText("Advances the World one step");
			}
		});
		
		b.setOnMouseExited(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent _){
				infoLabel.setText("");
			}
		});
		
		b1.setOnMouseEntered(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent _){
				if(b1.getText() == "Stop Stepping"){
					infoLabel.setText("Stops automatic advancement");
				}
				else{
					infoLabel.setText("Advances the world \ncontinuously at rate defined\n"
							+ " or by default, one \nstep per second");
				}
			}
		});
		
		b1.setOnMouseExited(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent _){
				infoLabel.setText("Hover cursor over a command "
						+ "\nand watch this space for help");
			}
		});
	}
	/**
	 * Method to step the world one step
	 */
	void step(int numsteps){
		MainClient.main(new String[]{"http://localhost:8080/Assignment-7/CritterWorld/step?count="+numsteps, ""});
		//cw.step();
		//get for numsteps
        //stepLabel.setText("Steps Advanced: " + cw.steps);
        //critterLabel.setText("Critters Alive: " + cw.critters.size());
		//deselect();
	}
	/**
	 * Method to create controls for creating critters
	 * Gives user controls to create different numbers of critters
	 * from different files
	 */
	void createCritters() throws IOException, FileNotFoundException, JSONException{
		Button b = new Button("Load Critters");
		final TextField t1 = new TextField("");
		final TextField t2 = new TextField("");
		v.getVBox().getChildren().addAll(b, t1,t2);
		
		b.setOnAction(new EventHandler<ActionEvent>() {
			@Override
            public void handle(ActionEvent _){
				if (cw == null){
					warning("You must load a world first!"); 
					return;
				}
				BufferedReader reader = null;
				try {
					reader = new BufferedReader(new InputStreamReader(new FileInputStream(t2.getText())));
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				String line = null;
				try {
					line = reader.readLine();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				JSONObject request = new JSONObject();
				if(line != null && line.charAt(0) == 'm'){
			 		int[] mem = new int[Integer.parseInt(line.substring(line.indexOf(":")+1).trim())];
			 		try {
						line = reader.readLine();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			 		mem[0] = mem.length;
			 		for(int i=1;i<5;i++){
			 			mem[i] = Integer.parseInt(line.substring(line.indexOf(":")+1).trim());
			 			try {
							line = reader.readLine();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			 		}
			 		mem[5] = 0;
			 		mem[6] = 0;
			 		mem[7] = Integer.parseInt(line.substring(line.indexOf(":")+1).trim());
			 		JSONArray value = null;
					try {
						value = new JSONArray(mem);
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
			 		try {
						request.put("mem", value);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			 	}
				String jb = new String(line + "\n");
				try {
					while((line=reader.readLine()) != null){
						jb+=line + "\n";
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try{
					request.put("program", jb);
					request.put("species_id", t2.getText());
					if(t1.getText() != ""){
						request.put("num", Integer.parseInt(t1.getText()));
					}
				} catch(JSONException j){
					j.printStackTrace();
				}
				//else have something for position set
				MainClient.main(new String[]{"http://localhost:8080/Assignment-7/CritterWorld/critters", jb.toString()});
				//change critters alive to MainClient.getNumCreated();
				//try{
				//	if(t1.getText()!= null && t2.getText()!= null){
				//		try{
				//			for(int i=0;i<Integer.parseInt(t1.getText());i++){
				//				cw.addRandomCritter(t2.getText());
				//			}
				//		}
				//		catch (FileNotFoundException fnfe){
				//			warning("The file you specified was\nin the wrong format!");
				//		}
				//		t1.setText("");
				//		t2.setText("");
				//		critterLabel.setText("Critters Alive: " + cw.critters.size());
				//	}
				//	else{
				//		warning("Please supply text!");
				//	}
				//}
				//catch (NumberFormatException nfe){
					//warning("Please give a number \nin the correct format");
				//}
				//cw.update(v);
            }
        });
		
		b.setOnMouseEntered(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent _){
				infoLabel.setText("Loads the number of critters \nspecified "
						+ "below from file \nspecified below");
			}
		});
		
		b.setOnMouseExited(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent _){
				infoLabel.setText("Hover cursor over a command "
						+ "\nand watch this space for help");
			}
		});
		
		t1.setOnMouseEntered(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent _){
				infoLabel.setText("The number of critters to \ngenerate");
			}
		});
		
		t1.setOnMouseExited(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent _){
				infoLabel.setText("Hover cursor over a command "
						+ "\nand watch this space for help");
			}
		});
		
		t2.setOnMouseEntered(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent _){
				infoLabel.setText("The file to generate the \ncritters from");
			}
		});
		
		t2.setOnMouseExited(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent _){
				infoLabel.setText("Hover cursor over a command "
						+ "\nand watch this space for help");
			}
		});
	}
	/**
	 * Method to generate warning for the user
	 * @param w the message displayed in the warning
	 */
	void warning(String w){
		warning = new VBox();
		warning.setAlignment(Pos.TOP_CENTER);
		Button ok = new Button("OK");
		infoLabel.setText("Click OK to dismiss window");
		ok.setPrefWidth(75);
		Label warn = new Label(w);
		Image img = new Image("file:src/sad_ladybug.png");
		ImageView imgv = new ImageView();
		imgv.setImage(img);
		warning.getChildren().addAll(warn,ok,imgv);
		final Stage s1 = new Stage();
        Group g = new Group();
        g.getChildren().add(warning);
        Scene scene = new Scene(g);
        s1.setScene(scene);
        s1.setWidth(150);
        s1.setHeight(230);
        s1.show();
		ok.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent _){
				s1.close();
			}
		});
		
	}
	/**
	 * Method to create controls for selecting and manipulating hexes
	 * Gives user information about currently selected hex
	 * Gives user controls to select a hex
	 */
	void hexSelection(){
		v.getWorld().setOnMouseExited(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent _){
				if(clicked == ""){
					position.setText("click on desired Hex");
				}
				else{
					position.setText(clicked);
				}
				
			}
		});
		
		for(final HexPolygon h: v.getHexes()){
			h.setOnMouseEntered(new EventHandler<MouseEvent>(){
				@Override
				public void handle(MouseEvent _){
					position.setText("("+h.column+","+h.row+")");
				}
			});
			
			h.setOnMouseClicked(new EventHandler<MouseEvent>(){
				@Override
				public void handle(MouseEvent _){
					clicked = "("+h.column+","+h.row+")";
					position.setText(clicked);
					if(selected != null){
						selected.setStroke(h.getStroke());
					}
					if(h.equals(selected)){
						deselect();
					}
					else{
						selected = h;
						removeHexBox();
						hexControls();
						h.setStroke(Color.RED);
					}
				}
			});
		}
		
		if(selected != null){
			selected.setOnMouseClicked(new EventHandler<MouseEvent>(){
				@Override
				public void handle(MouseEvent _){
					selected.setStroke(Color.BLACK);
					selected = null;
				}
			});
		}
		cw.update(v);
	}
	
	/**
	 * Method to create controls and display information for
	 * currently selected hex
	 * Gives user information about currently selected hex
	 * Gives user controls to manipulate currently selected hex
	 */
	void hexControls(){
		if (selected == null) return;
		hexBox = new VBox();
		if(selected.isRock()){
			hexRockInfo = new Label("Hex Information:");
			hexRockInfo.setFont(Font.font("Copperplate Gothic Bold", 14));
			hexBox.getChildren().addAll(hexRockInfo, new Label ("This hex is a rock"));
			v.getVBox().getChildren().add(hexBox);
			return;
		}
		hexRockInfo = new Label("Hex Information:");
		hexRockInfo.setFont(Font.font("Copperplate Gothic Bold", 14));
		hexFoodInfo = new Label("Food value: " + selected.getFood());
		if (selected.getCritter() == null){
			hexCritterInfo = new Label("There is no critter\ncurrently inhabiting\nthis hex, but...");
			hexBox.getChildren().addAll(hexRockInfo, hexFoodInfo, hexCritterInfo);
			Button b = new Button("You can add one! From\n"
					+ " from file:");
			final TextField t1 = new TextField("");
			hexBox.getChildren().addAll(b, t1);
			
			b.setOnMouseEntered(new EventHandler<MouseEvent>(){
				@Override
				public void handle(MouseEvent _){
					infoLabel.setText("Loads the critter from\nfile specified below \n and places it "
							+ "on this \nhex");
				}
			});
			
			b.setOnMouseExited(new EventHandler<MouseEvent>(){
				@Override
				public void handle(MouseEvent _){
					infoLabel.setText("Hover cursor over a command "
							+ "\nand watch this space for help");
				}
			});
			
			t1.setOnMouseEntered(new EventHandler<MouseEvent>(){
				@Override
				public void handle(MouseEvent _){
					infoLabel.setText("Please specify a file");
				}
			});
			
			t1.setOnMouseExited(new EventHandler<MouseEvent>(){
				@Override
				public void handle(MouseEvent _){
					infoLabel.setText("Hover cursor over a command "
							+ "\nand watch this space for help");
				}
			});
			
			b.setOnAction(new EventHandler<ActionEvent>() {
				@Override
	            public void handle(ActionEvent _) {
					if(!t1.getText().equals("")){
						try{
							cw.addCritterHere(selected.column, selected.arrRow, t1.getText());
							t1.setText("");
							critterLabel.setText("Critters Alive: " + cw.critters.size());
							cw.update(v);
							removeHexBox();
							hexControls();
						}
						catch (FileNotFoundException fnfe){
							warning("The file you specified \nwas in the wrong format!");
						}
					}
					else{
						warning("Please Supply Text!");
					}
				}
			});			
			v.getVBox().getChildren().add(hexBox);
		}
		displayCritterInfo();
	}
	
	
	/**
	 * Method to create controls and display information for
	 * critter on currently selected hex
	 * Gives user information about critter on currently selected hex
	 * Gives user controls to manipulate critter
	 */
	void displayCritterInfo(){
		if(selected.getCritter() == null) return;
		hexCritterInfo = new Label("Critter Vital Statistics:");
		hexCritterInfo.setFont(Font.font("Copperplate Gothic Bold", 14));
		hexBox.getChildren().addAll(hexRockInfo, hexFoodInfo, hexCritterInfo,
				new Label("Memory size: " + selected.getCritter().mem[0]), 
				new Label("Defensive ability: " + selected.getCritter().mem[1]), 
				new Label("Offensive ability: " + selected.getCritter().mem[2]), 
				new Label("Size: " + selected.getCritter().mem[3]), 
				new Label("Energy: " + selected.getCritter().mem[4]), 
				new Label("Pass value: " + selected.getCritter().mem[5]), 
				new Label("Tag value: " + selected.getCritter().mem[6]), 
				new Label("Posture value: " + selected.getCritter().mem[7]));
		for(int i = 8; i < selected.getCritter().mem.length;i++){
			hexBox.getChildren().add(new Label("mem["+i+"]: "+ selected.getCritter().mem[i]));
		}
		sb = new StringBuffer();
		Button b = new Button("Program");
		Button b1 = new Button("View Hex Controls");
		selected.getCritter().program.prettyPrint(sb);
		b.setOnAction(new EventHandler<ActionEvent>() {
			@Override
            public void handle(ActionEvent _) {
				VBox programInfo = new VBox();
				programInfo.setAlignment(Pos.CENTER);
				Label program = new Label(sb.toString());
				programInfo.getChildren().addAll(new Label("This Critter's "
						+ "Program is:"),program);
				final Stage s1 = new Stage();
		        Group g = new Group();
		        g.getChildren().add(programInfo);
		        Scene scene = new Scene(g);
		        s1.setScene(scene);
		        s1.setWidth(300);
		        s1.setHeight(500);
		        s1.show();			
			}
        });		
		
		b1.setOnAction(new EventHandler<ActionEvent>() {
			@Override
            public void handle(ActionEvent _) {
				VBox controls = new VBox();
				controls.setAlignment(Pos.CENTER);
				final Stage s1 = new Stage();
				Button step = new Button("step"), wait = new Button("wait"), move1 = new Button("move forward"), 
						move2 = new Button("move backward"), turn1 = new Button("turn left"), 
						turn2 = new Button("turn right"), eat = new Button("eat"), 
						attack = new Button("attack"), grow = new Button("grow"), 
						bud = new Button("bud"), mate = new Button("mate"), serve = new Button("serve");
				TextField serveAmount = new TextField("Enter amount to serve");
		        controlCritters(step, wait, move1, move2, turn1, 
		        		turn2, eat, attack, grow, bud, mate, serve, serveAmount, s1);
				controls.getChildren().addAll(new Label("Make this "
						+ "Critter:"), step, wait, move1, move2, turn1, 
						turn2, eat, attack, grow, bud, mate, serve, serveAmount);
		        Group g = new Group();
		        g.getChildren().add(controls);
		        Scene scene = new Scene(g);
		        s1.setScene(scene);
		        s1.setWidth(200);
		        s1.setHeight(530);
		        s1.show();			
			}
        });
		StringBuffer sb1 = new StringBuffer("The last rule performed was \n");
		if (selected.getCritter().lastRule != null) {
			selected.getCritter().lastRule.prettyPrint(sb1);
			hexBox.getChildren().add(new Label(sb1.toString()));
		} else {
			hexBox.getChildren().add(new Label("This critter has not \nperformed a rule yet."));
		}
		hexBox.getChildren().addAll(b,b1);
		v.getVBox().getChildren().add(hexBox);
	}
	
	
	/**
	 * Method to give user control over zoom of current view
	 */
	void zoomSettings(){
		HBox zoom = new HBox();
		Label l = new Label("Zoom options: ");
		Button b1 = new Button("+");
		Button b2 = new Button("-");
		b1.setFont(Font.font("Copperplate Gothic Bold", 20));
		b2.setFont(Font.font("Copperplate Gothic Bold", 20));
		zoom.getChildren().addAll(l,b1,b2);
		v.getVBox().getChildren().add(zoom);
		
		zoom.setOnMouseExited(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent _){
				infoLabel.setText("Hover cursor over a command "
						+ "\nand watch this space for help");
			}
		});
		
		b1.setOnMouseEntered(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent _){
				infoLabel.setText("Click to zoom in");
			}
		});
		
		b2.setOnMouseEntered(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent _){
				infoLabel.setText("Click to zoom out");
			}
		});
		
		b1.setOnAction(new EventHandler<ActionEvent>() {
			@Override
            public void handle(ActionEvent _) {
				cw.zoom(v, true);
				hexSelection();
            }
        });
		
		b2.setOnAction(new EventHandler<ActionEvent>() {
			@Override
            public void handle(ActionEvent _) {
				cw.zoom(v, false);
				hexSelection();
            }
        });
		
	}
	/**
	 * Helper method when deselecting any hex
	 */
	void removeHexBox(){
		v.getVBox().getChildren().remove(hexBox);
		hexBox = new VBox();
	}
	/**
	 * Method to give user control of a particular critter's actions
	 * @param step Button that gives user control over a critter action
	 * @param wait Button that gives user control over a critter action
	 * @param move1 Button that gives user control over a critter action 
	 * @param move2 Button that gives user control over a critter action
	 * @param turn1 Button that gives user control over a critter action
	 * @param turn2 Button that gives user control over a critter action
	 * @param eat Button that gives user control over a critter action
	 * @param attack Button that gives user control over a critter action
	 * @param grow Button that gives user control over a critter action
	 * @param bud Button that gives user control over a critter action
	 * @param mate Button that gives user control over a critter action
	 * @param serve Button that gives user control over a critter action
	 * @param serveA TextField that gives user control over a critter action
	 * @param s1 Stage to display critter actions to user
	 */
	void controlCritters(Button step,Button wait,Button move1,Button move2,Button turn1, 
			Button turn2,Button eat,Button attack,Button grow,Button bud,Button mate, Button serve
			, TextField serveA, Stage s1){
		final Stage s = s1;
		final TextField serveAmount = serveA; 
		step.setOnAction(new EventHandler<ActionEvent>() {
			@Override
            public void handle(ActionEvent _) {
				selected.getCritter().step();
				cw.update(v);
				deselect();
				s.close();
            }
        });
		
		wait.setOnAction(new EventHandler<ActionEvent>() {
			@Override
            public void handle(ActionEvent _) {
				selected.getCritter().waitTurn();
				cw.update(v);
				deselect();
				s.close();
            }
        });
		
		move1.setOnAction(new EventHandler<ActionEvent>() {
			@Override
            public void handle(ActionEvent _) {
				selected.getCritter().move(1);
				cw.update(v);
				deselect();
				s.close();
				
            }
        });
		
		move2.setOnAction(new EventHandler<ActionEvent>() {
			@Override
            public void handle(ActionEvent _) {
				selected.getCritter().move(-1);
				cw.update(v);
				deselect();
				s.close();
            }
        });
		
		turn1.setOnAction(new EventHandler<ActionEvent>() {
			@Override
            public void handle(ActionEvent _) {
				selected.getCritter().turn(-1);
				cw.update(v);
				deselect();
				s.close();
            }
        });
		
		turn2.setOnAction(new EventHandler<ActionEvent>() {
			@Override
            public void handle(ActionEvent _) {
				selected.getCritter().turn(1);
				cw.update(v);
				deselect();
				s.close();
            }
        });
		
		eat.setOnAction(new EventHandler<ActionEvent>() {
			@Override
            public void handle(ActionEvent _) {
				selected.getCritter().eat();
				cw.update(v);
				deselect();
				s.close();
            }
        });
		
		grow.setOnAction(new EventHandler<ActionEvent>() {
			@Override
            public void handle(ActionEvent _) {
				selected.getCritter().grow();
				cw.update(v);
				deselect();
				s.close();
            }
        });
		
		bud.setOnAction(new EventHandler<ActionEvent>() {
			@Override
            public void handle(ActionEvent _) {
				selected.getCritter().bud();
				cw.update(v);
				deselect();
				s.close();
            }
        });
		
		attack.setOnAction(new EventHandler<ActionEvent>() {
			@Override
            public void handle(ActionEvent _) {
				selected.getCritter().attack();
				cw.update(v);
				deselect();
				s.close();
            }
        });
		
		mate.setOnAction(new EventHandler<ActionEvent>() {
			@Override
            public void handle(ActionEvent _) {
				selected.getCritter().mate();
				int[] pos = selected.getCritter().getAdjacentPositions(selected.column,selected.row);
				if(cw.hexes[pos[0]][pos[1]].critter != null) cw.hexes[pos[0]][pos[1]].critter.mate();
				cw.update(v);
				deselect();
				s.close();
            }
        });
		
		serve.setOnAction(new EventHandler<ActionEvent>() {
			@Override
            public void handle(ActionEvent _) {
				try{
					selected.getCritter().serve(Integer.parseInt(serveAmount.getText()));
					cw.update(v);
					deselect();
					s.close();
				}
				catch (NumberFormatException nfe){
					warning("Please enter a number \nin the correct format!");
				}
            }
        });
		
		serveAmount.setOnMouseEntered(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent _){
				serveAmount.setText("");
			}
		});
		
		serveAmount.setOnMouseExited(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent _){
				if(serveAmount.getText().equals("")){
					serveAmount.setText("Enter amount to serve");
				}
			}
		});
	}
	/**
	 * Method to deselect a hex
	 */
	void deselect(){
		if(selected == null) return;
		selected.setStroke(Color.BLACK);
		clicked = "";
		selected = null;
		removeHexBox();
	}
}