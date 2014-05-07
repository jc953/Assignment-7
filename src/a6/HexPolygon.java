package a6;

import a5.Constants;
import a5.Critter;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

/**
 * The HexPolygon class represents each Hex in the GUI of CritterWorld. It is
 * given a location in the world and will change its background to a nice 
 * light green color or the image of whatever object is currently at the hex.
 */
public class HexPolygon extends Polygon {
	int column, row, arrRow;
	View v;
	double x, y;
	ImageView imgv;
	final static Color background = Color.color(.2, .6, .2);
	final static Image rock = new Image("file:src/rock.png");
	final static Image critter = new Image("file:src/critter.png");
	final static Image critterfood = new Image("file:src/critterfood.png");
	final static Image food = new Image("file:src/food.png");
	
	/**
	 * Creates a HexPolygon at a given location, using doubles a through l. Every
	 * two numbers gives the location of each vertex of the polygon. Is also 
	 * given the column and row of the hex in relation to the CritterWorld it is 
	 * portraying. Sets the view to the given View.
	 * 
	 * @param a x coordinate of first vertex
	 * @param b y coordinate of first vertex
	 * @param c x coordinate of second vertex
	 * @param d y coordinate of second vertex
	 * @param e x coordinate of third vertex
	 * @param f y coordinate of third vertex
	 * @param g x coordinate of fourth vertex
	 * @param h y coordinate of fourth vertex
	 * @param i x coordinate of fifth vertex
	 * @param j y coordinate of fifth vertex
	 * @param k x coordinate of sixth vertex
	 * @param l y coordinate of sixth vertex
	 * @param col the column of the hex from CritterWorld that this represents
	 * @param row the row of the hex from the CritterWorld that this represents
	 * @param v the View that this is shown on
	 */
	public HexPolygon(double a, double b, double c, double d, double e, double f, 
			double g, double h, double i, double j, double k, double l, int col, int row, View v){
		super(a, b, c, d, e, f, g, h, i, j, k, l);
		column = col;
		this.arrRow = row;
		this.row = arrRow + (col+1)/2;
		this.v = v;
		imgv = new ImageView();
		x = (a+c)/2.0;
		y = f;
		setStroke(Color.BLACK);
		setFill(background);
	}
	
	/**
	 * Determines whether an object currently exists on this hex.
	 * 
	 * @return true if an object does exist, false otherwise.
	 */
	public boolean hasObject(){
		return isRock() || getCritter() != null || getFood() > 0;
	}
	
	/**
	 * Returns whether or not this hex contains a rock.
	 * 
	 * @return true if it does contain a rock, false otherwise.
	 */
	public boolean isRock(){
		return v.getCritterWorld().hexes[column][arrRow].rock;
	}
	
	/**
	 * Returns the amount of food on this hex.
	 * 
	 * @return amount of food on this hex.
	 */
	public int getFood(){
		return v.getCritterWorld().hexes[column][arrRow].food;
	}
	
	/**
	 * Returns the critter that is on this hex. If none, it will return null.
	 * 
	 * @return critter on this hex, null if there is none.
	 */
	public Critter getCritter(){
		return v.getCritterWorld().hexes[column][arrRow].critter;
	}
	
	/**
	 * Resets the background to the original color.
	 */
	public void reset(){
		setFill(background);
	}
	
	/**
	 * Returns an ImageView of the object existing on this hex with the correct 
	 * x and y coordinates. Will adjust the color of the critter depending on its
	 * program type. Sets the fill to transparent because the ImageView is sent to 
	 * another pane that will be beneath this pane, allowing users to still click
	 * on this polygon.
	 * 
	 * @return the ImageView of the object on this hex, null if there is no object.
	 */
	public ImageView draw(){
		setFill(Color.TRANSPARENT);
		if (isRock()){
			imgv.setImage(rock);
			imgv.setFitHeight(Constants.HEX_LENGTH);
			imgv.setFitWidth(Constants.HEX_LENGTH);
			imgv.setX(x-Constants.HEX_LENGTH/2);
			imgv.setY(y-Constants.HEX_LENGTH/2);
			imgv.setRotate(v.getCritterWorld().hexes[column][arrRow].rockDir);
			return imgv;
		} else if (getCritter() != null){
			if (getFood() > 0) {
				imgv.setImage(critterfood);
			} else {
				imgv.setImage(critter);
			}
			double size = Constants.HEX_LENGTH/1.5 + getCritter().mem[3]*Constants.HEX_LENGTH/20.0;
			if (size > Constants.HEX_LENGTH) size = Constants.HEX_LENGTH;
			imgv.setFitHeight(size);
			imgv.setFitWidth(size);
			imgv.setX(x-size/2.0);
			imgv.setY(y-size/2.0);
			imgv.setRotate(getCritter().direction*60);
			ColorAdjust color = new ColorAdjust();
			int index = 0;
			for (int i = 0; i < v.programs.size(); i++){
				if (v.programs.get(i).equals(getCritter().program)) index = i;
			}
			double hue = v.hues.get(index);
			color.setHue(hue);
			imgv.setEffect(color);
			return imgv;
		} else if (getFood() > 0){
			imgv.setImage(food);
			imgv.setFitHeight(Constants.HEX_LENGTH);
			imgv.setFitWidth(Constants.HEX_LENGTH);
			imgv.setX(x-Constants.HEX_LENGTH/2);
			imgv.setY(y-Constants.HEX_LENGTH/2);
			imgv.setRotate(0);
			return imgv;
		} else {
			System.out.println("Cannot draw rock or critter here");
			return null;
		}
	}	
}
