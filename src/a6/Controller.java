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
	double speed;
	VBox hexBox;
	Timeline timeline;
	StringBuffer sb;
	Label speedLabel;
	
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
		speed = 0;
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
		speedLabel = new Label("Current Speed(sec): 0");
		speedLabel.setFont(Font.font("Copperplate Gothic Bold", 14));
		
		Button b = new Button("Load World");
		final TextField t = new TextField("");
		v.getVBox().getChildren().addAll(stepLabel, critterLabel, speedLabel,b,t);
		
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
						JSONObject request = new JSONObject();
						request.put("definitions",jb.toString());
						System.out.println(request.toString());
						MainClient.main(new String[]{"http://localhost:8080/Assignment-7/CritterWorld/world", request.toString()});
						t.setText("");
						update();
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
		final HBox speedControls = new HBox();
		final Button b2 = new Button("Set step speed to: ");
		final TextField t = new TextField();
		final TextField t2 = new TextField();
		t.setMaxWidth(50.0); 
		t.setMinWidth(50.0);
		speedControls.getChildren().addAll(b2, t);
		v.getVBox().getChildren().addAll(b, t2, speedControls);
		
		timeline = new Timeline(new KeyFrame(Duration.seconds(speed), 
				new EventHandler<ActionEvent>(){
				
				@Override
				public void handle(ActionEvent arg0){
					if (speed != 0){
						step(1);
					}
					update();
				}
		}));
		
		b2.setOnMouseEntered(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent _){
				infoLabel.setText("Allows you to change the \nstepping speed "
						+ "to the number \nof seconds specified");
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
				infoLabel.setText("Please specify a number \nbetween .04 and 100 \n(seconds)");
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
				infoLabel.setText("Please specify the number \nod steps you wish to \nexecute");
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
						MainClient.main(new String[]{"http://localhost:8080/Assignment-7/CritterWorld/run?rate="+Double.parseDouble(t.getText()),""});
						update();
						System.out.println(cw.rate+"in controeler");
						speed = cw.rate;
						if(speed<.04) {
							speed = .04; 
							warning("That is too fast,speed \nhas been set to .04! ");
						}
						if(speed>100) {
							speed = 100; 
							warning("That is too slow,speed \nhas been set to 100! ");
						}
						t.setText("");
						timeline = new Timeline(new KeyFrame(Duration.seconds(speed), 
								new EventHandler<ActionEvent>(){
								
								@Override
								public void handle(ActionEvent arg0){
									if (speed != 0){
										step(1);
									}
									update();
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
						t2.setText("");
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
		
		
		b.setOnMouseEntered(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent _){
				infoLabel.setText("Advances the World the number\nof steps specified below");
			}
		});
		
		b.setOnMouseExited(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent _){
				infoLabel.setText("");
			}
		});
	}
	/**
	 * Method to step the world one step
	 * @throws JSONException 
	 */
	void step(int numsteps) {
		MainClient.main(new String[]{"http://localhost:8080/Assignment-7/CritterWorld/step?count="+numsteps, ""});
		update();		
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
				try{
					if(t1.getText()!= null && t2.getText()!= null){
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
						MainClient.main(new String[]{"http://localhost:8080/Assignment-7/CritterWorld/critters", request.toString()});
						t1.setText("");
						t2.setText("");
						//MainClient.main(new String[]{"http://localhost:8080/Assignment-7/CritterWorld/world"});
						//System.out.println(MainClient.getResponse().toString());
						//JSONObject j = new JSONObject(MainClient.getResponse().toString());
						//critterLabel.setText("Critters Alive: " + j.getInt("population"));
						//int num = Integer.parseInt(critterLabel.getText().substring(critterLabel.getText().indexOf(":")+1).trim());
					}
					else{
						warning("Please supply text!");
					}
				}
				catch (NumberFormatException n){
					warning("Please give a number \nin the correct format");
				}
				cw.update(v);
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
		StringBuffer sb1 = new StringBuffer("The last rule performed was \n");
		if (selected.getCritter().lastRule != null) {
			selected.getCritter().lastRule.prettyPrint(sb1);
			hexBox.getChildren().add(new Label(sb1.toString()));
		} else {
			hexBox.getChildren().add(new Label("This critter has not \nperformed a rule yet."));
		}
		hexBox.getChildren().add(b);
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
	 * Method to deselect a hex
	 */
	void deselect(){
		if(selected == null) return;
		selected.setStroke(Color.BLACK);
		clicked = "";
		selected = null;
		removeHexBox();
	}
	
	void update(){
		MainClient.main(new String[]{"http://localhost:8080/Assignment-7/CritterWorld/world"});
		JSONObject j;
		try {
			j = new JSONObject(MainClient.getResponse().toString());
			cw = new CritterWorld(j);
			cw.update(v);
			critterLabel.setText("Critters Alive: " + j.getInt("population"));
			stepLabel.setText("Steps Advanced: "+j.getInt("current_timestep"));
			speedLabel.setText("Current Speed(sec): " +j.getDouble("rate"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

//fix stepping
//take out useless controls 
//fix everything
//fix delete