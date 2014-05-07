package a6;

import java.util.ArrayList;

import a5.Constants;
import a5.Critter;
import a5.CritterWorld;
import ast.Program;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;

/**
 * The View of the CritterWorld. Contains two primary pains to portray the world
 * and for user input. Contains the world in a ScrollPane to allow for large
 * worlds.
 */
public class View {
	private Group g;
	protected StackPane world;
	private VBox vbox;
	private ArrayList<HexPolygon> hexes;
	private int width;
	private int height;
	private int hL;
	private double hA;
	private int diff;
	private CritterWorld cw;
	private Pane actors;
	private Pane hexesPane;
	private Polygon background;
	private ScrollPane sp;
	ArrayList<Program> programs;
	ArrayList<Double> hues;

	/**
	 * Initializes the constants of View and sets the stage and CritterWorld. 
	 * Creates HexPolygons for each hex and adds them to the pane that portrays
	 * the world.
	 * 
	 * @param s the Stage that this will be portrayed on.
	 * @param cw the CritterWorld that the view is portraying
	 */
	public View(Stage s, CritterWorld cw) {
		width = Constants.SCENE_WIDTH;
		height = Constants.SCENE_HEIGHT;
		hL = Constants.HEX_LENGTH;
		hA = Constants.HEX_APOTHEM;
		diff = Constants.HEX_DIFF;
		programs = new ArrayList<Program>();
		hues = new ArrayList<Double>();
		g = new Group();
		Scene scene = new Scene(g);
		s.setScene(scene);
		s.setWidth(width);
		s.setHeight(height);
		this.cw = cw;
		BorderPane border = new BorderPane();
		world = new StackPane();
		double width = Constants.MAX_COLUMN*hL*3/2+Constants.MAX_COLUMN*diff + hL/2;
		double height = Constants.MAX_ARRAY_ROW*hA*2+Constants.MAX_ARRAY_ROW*diff+hA;
		world.setPrefWidth(width);
		world.setPrefHeight(height);
		background = new Polygon(0,0, width, 0, width, height, 0, height);
		background.setFill(Color.WHITE);
		hexesPane = new Pane();
		hexes = new ArrayList<HexPolygon>();
		for(int i = 0; i < Constants.MAX_COLUMN; i++){
			for (int j = 0; j < Constants.MAX_ARRAY_ROW; j++){
				int x = i * hL*3/2;
				double y;
				if (i%2==0){
					y = hA + j*hA*2;
				} else {
					y = j*hA*2;
				}
				HexPolygon p = new HexPolygon(hL/2+x+diff*i, y+diff*j, hL*3/2+x+diff*i, y+diff*j, hL*2+x+diff*i, hA+y+diff*j,
						hL*3/2+x+diff*i, hA*2+y+diff*j, hL/2+x+diff*i, hA*2+y+diff*j, x+diff*i, hA+y+diff*j, i, Constants.MAX_ARRAY_ROW-j-1, this);
				hexesPane.getChildren().add(p);
				hexes.add(p);
			}
		}
		actors = new Pane();
		world.getChildren().addAll(background, actors, hexesPane);
		sp = new ScrollPane();
		sp.setContent(world);
		sp.setPrefSize(Constants.SCROLL_PANE_LENGTH,Constants.SCROLL_PANE_LENGTH);
		vbox = new VBox();
		scene.setFill(Color.color(.8,.4,.4));
		border.setLeft(sp);
		border.setRight(vbox);
		g.getChildren().add(border);
		update(cw);
	}
	
	/**
	 * Updates the View based on the CritterWorld. If the CritterWorld changes,
	 * as in a new world is loaded, it will reset programs and hues to reduce
	 * memory use. Removes the current actors from the world pane and creates 
	 * new ones by going through each and getting the ImageView from them. 
	 * 
	 * @param cw the CritterWorld that this is portraying
	 */
	public void update(CritterWorld cw){ 
		if (!this.cw.equals(cw)){
			programs = new ArrayList<Program>();
			hues = new ArrayList<Double>();
			this.cw=cw;
		}
		world.getChildren().removeAll(actors, hexesPane);
		actors = new Pane();
		world.getChildren().addAll(actors, hexesPane);
		setColors();
		for (HexPolygon h : hexes){
			h.reset();
			if (h.hasObject()){
				actors.getChildren().add(h.draw());
			}
		}
	}
	
	/**
	 * Sets the colors that will be used by the critters to determine different
	 * species.
	 */
	public void setColors(){
		for (Critter c : cw.critters){
			boolean exists = false;
				for (Program p : programs){
					if (c.program.equals(p)){
						exists = true;
						break;
					}
				}
			if (!exists){
				programs.add(c.program);
				hues.add(Math.random()*2-1);
			}
		}
	}

	/**
	 * Zooms in or out of the world pane. Has a cap as to how much it can zoom
	 * in or out.
	 * 
	 * @param in true if it zooms in, false if it zooms out.
	 */
	public void zoom(boolean in){
		if (in && Constants.HEX_LENGTH < 100){
			Constants.HEX_LENGTH += 10;
		} else if (!in && Constants.HEX_LENGTH > 10){
			Constants.HEX_LENGTH -= 10;
		} else {
			return;
		}
		Constants.HEX_APOTHEM = Constants.HEX_LENGTH/2*Math.pow(3, 0.5);
		Constants.HEX_DIFF = Constants.HEX_LENGTH/10;
		hL = Constants.HEX_LENGTH;
		hA = Constants.HEX_APOTHEM;
		diff = Constants.HEX_DIFF;
		world.getChildren().removeAll(background, actors, hexesPane);
		double width = Constants.MAX_COLUMN*hL*3/2+Constants.MAX_COLUMN*diff + hL/2;
		double height = Constants.MAX_ARRAY_ROW*hA*2+Constants.MAX_ARRAY_ROW*diff+hA;
		world.setPrefWidth(width);
		world.setPrefHeight(height);
		background = new Polygon(0,0, width, 0, width, height, 0, height);
		background.setFill(Color.WHITE);
		hexesPane = new Pane();
		hexes = new ArrayList<HexPolygon>();
		for(int i = 0; i < Constants.MAX_COLUMN; i++){
			for (int j = 0; j < Constants.MAX_ARRAY_ROW; j++){
				int x = i * hL*3/2;
				double y;
				if (i%2==0){
					y = hA + j*hA*2;
				} else {
					y = j*hA*2;
				}
				HexPolygon p = new HexPolygon(hL/2+x+diff*i, y+diff*j, hL*3/2+x+diff*i, y+diff*j, hL*2+x+diff*i, hA+y+diff*j,
						hL*3/2+x+diff*i, hA*2+y+diff*j, hL/2+x+diff*i, hA*2+y+diff*j, x+diff*i, hA+y+diff*j, i, Constants.MAX_ARRAY_ROW-j-1, this);
				hexesPane.getChildren().add(p);
				hexes.add(p);
			}
		}
		actors = new Pane();
		world.getChildren().addAll(background, actors, hexesPane);
		update(cw);
	}

	/**
	 * Returns the vbox of this View.
	 * 
	 * @return vbox of View
	 */
	public VBox getVBox() {
		return vbox;
	}
	
	/**
	 * Returns the CritterWorld of this View.
	 * 
	 * @return CritterWorld of View
	 */
	public CritterWorld getCritterWorld(){
		return cw;
	}
	
	/**
	 * Returns the hex polygons of this view.
	 * 
	 * @return hex polygons of View
	 */
	public ArrayList<HexPolygon> getHexes(){
		return hexes;
	}
	
	/**
	 * Returns the world pane of this view.
	 * 
	 * @return world pane of View
	 */
	public Pane getWorld(){
		return world;
	}
}