package a5;
import java.io.*;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import a6.View;
import ast.Program;
/**
 * The CritterWorld class is a representation of a critter world. This contains
 * a 2D array representing every hex in the world and keeps track of the critters
 * currently alive.
 */
public class CritterWorld {
	public Hex[][] hexes;
	public ArrayList<Critter> critters;
	public int steps;
	public double rate;
	public ArrayList<Hex[][]> log;
	
	/**
	 * Uses a text file to create a critter world. It will read in the rocks 
	 * and critters and create them using the createRock and createCritter 
	 * helper methods. 
	 * 
	 * @param file the text file that will be used to create the critter world.
	 */
	public CritterWorld(String file) throws FileNotFoundException {
		hexes = new Hex[Constants.MAX_COLUMN][Constants.MAX_ARRAY_ROW];
		for (int i = 0; i < hexes.length; i++){
			for (int j = 0; j < hexes[0].length; j++){
				hexes[i][j] = new Hex();
			}
		}
		critters = new ArrayList<Critter>();
		steps = 0;
		log = new ArrayList<Hex[][]>();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			String line = br.readLine();
			while (line != null){
				if (line.charAt(0)=='r'){
					createRock(line);
				} else if (line.charAt(0)=='c'){
					createCritter(line);
				} else {
					throw new FileNotFoundException();
				}
				line = br.readLine();
			}
			br.close();
		} catch (IOException e) {
			throw new FileNotFoundException();
		}
	}
	
	public CritterWorld(StringBuffer jb) throws FileNotFoundException {
		String str = jb.toString();
		hexes = new Hex[Constants.MAX_COLUMN][Constants.MAX_ARRAY_ROW];
		for (int i = 0; i < hexes.length; i++){
			for (int j = 0; j < hexes[0].length; j++){
				hexes[i][j] = new Hex();
			}
		}
		critters = new ArrayList<Critter>();
		steps = 0;
		log = new ArrayList<Hex[][]>();
		String[] lines = str.split("\n");
		for(int i=0;i<lines.length;i++){
			if (lines[i].charAt(0)=='r'){
				createRock(lines[i]);
			} else if (lines[i].charAt(0)=='c'){
				createCritter(lines[i]);
			}
		}
	}	
	
	/**
	 * Creates a new critter world, possibly with randomized rocks. The number 
	 * of randomly generated rocks is determined by the total number of hexes 
	 * in the world.
	 */
	public CritterWorld(){
		hexes = new Hex[Constants.MAX_COLUMN][Constants.MAX_ARRAY_ROW];
		for (int i = 0; i < hexes.length; i++){
			for (int j = 0; j < hexes[0].length; j++){
				hexes[i][j] = new Hex();
			}
		}
		critters = new ArrayList<Critter>();
		steps = 0;		
		log = new ArrayList<Hex[][]>();
		int numberRocks = Constants.MAX_COLUMN*Constants.MAX_ARRAY_ROW/30;
		for (int i = 0; i < numberRocks; i++){
			int col = (int)(Math.random() * hexes.length);
			int row = (int)(Math.random() * hexes[0].length);
			while (hexes[col][row].rock){
				col = (int)(Math.random() * hexes.length);
				row = (int)(Math.random() * hexes[0].length);
			}
			hexes[col][row].rock = true;
		}
	}
	
	public CritterWorld(JSONObject json) throws JSONException{
		hexes = new Hex[json.getInt("cols")][json.getInt("rows")];
		for (int i = 0; i < hexes.length; i++){
			for (int j = 0; j < hexes[0].length; j++){
				hexes[i][j] = new Hex();
			}
		}
		critters = new ArrayList<Critter>();
		steps = json.getInt("current_timestep");	
		log = new ArrayList<Hex[][]>();
		JSONArray state = json.getJSONArray("state");
		for (int i = 0; i < state.length(); i++){
			JSONObject hex = state.getJSONObject(i);
			String type = hex.getString("type");
			if (type.equals("rock"))
				hexes[hex.getInt("col")][hex.getInt("row")-hex.getInt("col")/2].rock = true;
			else if (type.equals("food")){
				hexes[hex.getInt("col")][hex.getInt("row")].food=hex.getInt("value");
			} else if (type.equals("critter")){
				hexes[hex.getInt("col")][hex.getInt("row")].critter=new Critter(hex, this);
				critters.add(hexes[hex.getInt("col")][hex.getInt("row")].critter);
			}
		}
	}
	
	/**
	 * Uses a text file to create a rock. Creates a rock at the chosen column 
	 * and row, as long as they are within the boundaries.
	 * 
	 * @param line the text file that will be used to create the rock.
	 */
	public void createRock(String line){
		String[] str = line.split(" ");
		if (str.length != 3){
			System.out.println("Please enter a file with correct syntax.");
			throw new RuntimeException();
		}
		int row = Integer.parseInt(str[1]);
		int column = Integer.parseInt(str[2]);
		int arrayRow = row - ((column+1)/2);
		if (column < 0 || column >= Constants.MAX_COLUMN || arrayRow < 0 || arrayRow >= Constants.MAX_ARRAY_ROW){
			System.out.println("A rock you tried to create breached the boundaries.");
			return;
		}
		hexes[column][arrayRow].rock = true;
	}
	
	/**
	 * Uses a text file to create a critter. Creates a critter with a program,
	 * direction, column, and row, all specified by the text, as long as they
	 * are within the boundaries.
	 * 
	 * @param line the text file that will be used to create the critter.
	 */
	public void createCritter(String line) throws FileNotFoundException{
		String[] str = line.split(" ");
		if (str.length != 5){
			throw new FileNotFoundException();
		}
		int row = Integer.parseInt(str[2]);
		int column = Integer.parseInt(str[3]);
		int arrayRow = row - ((column+1)/2);
		if (column < 0 || column >= Constants.MAX_COLUMN || arrayRow < 0 || arrayRow >= Constants.MAX_ARRAY_ROW){
			System.out.println("A critter you tried to create breached the boundaries.");
			return;
		}
		if (hexes[column][arrayRow].isFree()){
			Critter c = new Critter(str[1], Integer.parseInt(str[4]), column, arrayRow, this);
			hexes[column][arrayRow].critter = c;
			critters.add(c);
		}
	}
	
	/**
	 * Uses a text file to create a critter on a randomly chosen free hex.
	 * 
	 * @param filename the text file that will be used to create the critter.
	 */
	public void addRandomCritter(String filename) throws FileNotFoundException{
		int col = (int)(Math.random() * hexes.length);
		int row = (int)(Math.random() * hexes[0].length);
		while (!hexes[col][row].isFree()){
			col = (int)(Math.random() * hexes.length);
			row = (int)(Math.random() * hexes[0].length);
		}
		Critter c = new Critter(filename, (int)(Math.random()*6), col, row, this);
		hexes[col][row].critter = c;
		critters.add(c);
	}
	
	public int addRandomCritter(Program program, int[] mem, String spec){
		int col = (int)(Math.random() * hexes.length);
		int row = (int)(Math.random() * hexes[0].length);
		while (!hexes[col][row].isFree()){
			col = (int)(Math.random() * hexes.length);
			row = (int)(Math.random() * hexes[0].length);
		}
		Critter c = new Critter(program, mem, (int)(Math.random()*6), col, row, this);
		c.species_id = spec;
		hexes[col][row].critter = c;
		critters.add(c);
		return c.id;
	}
	/**
	 * Creates a critter using a given text file on a given hex position.
	 * 
	 * @param column the column of the critter.
	 * @param row the row of the critter.
	 * @param filename the text file that will be used to create the critter.
	 */
	public void addCritterHere(int column, int row, String filename) throws FileNotFoundException{
		Critter c = new Critter(filename, (int)(Math.random()*6), column, row, this);
		hexes[column][row].critter = c;
		critters.add(c);
	}
	
	public int addCritterHere(Program program, int[] mem, int column, int row, String spec) throws FileNotFoundException{
		Critter c = new Critter(program, mem, (int)(Math.random()*6), column, row, this);
		c.species_id = spec;
		hexes[column][row].critter = c;
		critters.add(c);
		return c.id;
	}
	/**
	 * Adds a Critter to the critter list at the given column and row. Mainly
	 * used when a Critter buds or mates.
	 * 
	 * @param c the Critter that is added.
	 * @param column the column that the Critter is located in.
	 * @param arrayRow the row that the Critter is located in.
	 */
	public void addCritter(Critter c, int column, int arrayRow){
		if (column < 0 || column >= Constants.MAX_COLUMN ||arrayRow < 0 || arrayRow >= Constants.MAX_ARRAY_ROW){
			System.out.println("A critter you tried to create breached the boundaries.");
			return;
		}
		if (hexes[column][arrayRow].isFree()){
			hexes[column][arrayRow].critter = c;
			critters.add(c);
		}
	}
	
	/**
	 * Steps through each critter in the order that they were created. Increases 
	 * the step count by one.
	 */
	public void step(){
		Hex[][] state = new Hex[Constants.MAX_COLUMN][Constants.MAX_ARRAY_ROW];
		try{
			for (int i = 0; i < hexes.length; i++){
				for (int j = 0; j < hexes[0].length; j++){
					state[i][j]= new Hex();
					state[i][j].rock = hexes[i][j].rock;
					state[i][j].food = hexes[i][j].food;
					state[i][j].critter = hexes[i][j].critter;
				}
			}
		} catch (Exception e) {
			System.out.println("oops, something went wrong!");
		}
		log.add(state);
		ArrayList<Critter> tempCrits = new ArrayList<Critter>(critters);
		for (Critter c : tempCrits){
			if (c!=null){
				c.step();
			}
		}
		steps++;
	}
	
	/**
	 * Removes a critter from the critters list and from the hex. Also creates
	 * food on the hex where the critter died.
	 * 
	 * @param c the Critter to be killed.
	 */
	public void kill(Critter c){
		int column = c.column;
		int row = c.row;
		critters.remove(c);
		hexes[column][row].critter = null;
		hexes[column][row].food += c.mem[3]*Constants.FOOD_PER_SIZE;
	}
	
	/**
	 * Prints the info of this critter world to the console. Contains information
	 * on the time steps elapsed and number of critters alive. Portrays an ASCII 
	 * map of the world using the information of each Hex. Since the world is
	 * a hexagonal grid, each column is staggered by one line.
	 */
	public void info(){
		System.out.println(steps==1 ? steps + " step has elapsed.": steps + " steps have elapsed.");
		System.out.println(critters.size()==1 ?critters.size() + " critter is alive." : critters.size() + " critters are alive");
		if (hexes.length == 1){
			for (int i = hexes[0].length-1; i >= 0; i--){
				System.out.println(hexes[0][i].getWorldInfo()+"\n");
			}
		}
		for (int i = hexes[0].length-1; i >= 0; i--){
			for (int j = 1; j < hexes.length; j += 2){
				System.out.print("  "+hexes[j][i].getWorldInfo());
			}
			System.out.println();
			for (int j = 0; j < hexes.length; j += 2){
				System.out.print(hexes[j][i].getWorldInfo()+"  ");
			}
			System.out.println();
		}
	}
	
	/**
	 * Updates the View to show the proper critter world state.
	 * 
	 * @param v the View that will be updated.
	 */
	public void update(View v){
		v.update(this);
	}
	
	/**
	 * Performs a zoom on the view. If b is true, it will zoom in. If b is 
	 * false, it will zoom out.
	 * 
	 * @param v the View that will be zoomed.
	 * @param b the boolean that determines whether view will be zoomed in or out.
	 */
	public void zoom(View v, boolean b){
		v.zoom(b);
	}
	
	public JSONObject getJson(int a, int b, int c, int d) throws JSONException{
		JSONObject json = new JSONObject();
		json.put("current_timestep", steps);
		json.put("rate", rate);
		json.put("population", critters.size());
		json.put("rows", hexes[0].length);
		json.put("cols", hexes.length);
		JSONArray temp = new JSONArray();
		for (int i = c; i < d; i++){
			for (int j = a; j < b; j++){
				if (hexes[i][j].rock){
					JSONObject rock = new JSONObject();
					rock.put("row", j+(i+1)/2);
					rock.put("col", i);
					rock.put("type", "rock");
					temp.put(rock);
				} 
				if (hexes[i][j].food > 0){
					JSONObject food = new JSONObject();
					food.put("row", j+(i+1)/2);
					food.put("col", i);
					food.put("type", "food");
					food.put("value", hexes[i][j].food);
					temp.put(food);
				}
				if (hexes[i][j].critter != null){
					JSONObject critter = new JSONObject();
					Critter c1 = hexes[i][j].critter;
					critter.put("type", "critter");
					StringBuffer sb = new StringBuffer();
					critter.put("id", c1.id);
					critter.put("species_id", c1.species_id);
					c1.program.prettyPrint(sb);
					critter.put("program", sb);
					critter.put("row", j+(i+1)/2);
					critter.put("col", i);
					critter.put("direction", c1.direction);
					critter.put("mem", c1.mem);
					sb = new StringBuffer();
					if (c1.lastRule == null){
						critter.put("recently_executed_rule", "No rule executed yet");
					} else {
						c1.lastRule.prettyPrint(sb);
						critter.put("recently_executed_rule", sb);
					}
					temp.put(critter);
				}
			}
		}
		json.put("state", temp);
		return json;
	}
	
	public JSONObject getJson(int updatesince, int a, int b, int c, int d) throws JSONException{
		JSONObject json = new JSONObject();
		json.put("current_timestep", steps);
		json.put("update_since", updatesince);
		json.put("rate", rate);
		json.put("population", critters.size());
		json.put("rows", hexes[0].length);
		json.put("cols", hexes.length);
		JSONArray temp = new JSONArray();
		Hex[][] tempState = log.get(updatesince);
		for (int i = c; i < d; i++){
			for (int j = a; j < b; j++){
				Hex hex = tempState[i][j];
				Hex cur = hexes[i][j];
				if (cur.food > 0 && hex.food!=cur.food){
					JSONObject food = new JSONObject();
					food.put("row", j+(i+1)/2);
					food.put("col", i);
					food.put("type", "food");
					food.put("value", cur.food);
					temp.put(food);
				}
				if (cur.critter != null){
					JSONObject critter = new JSONObject();
					Critter c1 = cur.critter;
					critter.put("type", "critter");
					StringBuffer sb = new StringBuffer();
					critter.put("id", c1.id);
					critter.put("species_id", c1.species_id);
					c1.program.prettyPrint(sb);
					critter.put("program", sb);
					critter.put("row", j+(i+1)/2);
					critter.put("col", i);
					critter.put("direction", c1.direction);
					critter.put("mem", c1.mem);
					sb = new StringBuffer();
					if (c1.lastRule == null){
						critter.put("recently_executed_rule", "No rule executed yet");
					} else {
						c1.lastRule.prettyPrint(sb);
						critter.put("recently_executed_rule", sb);
					}
					temp.put(critter);
				}
				if (cur.food==0&&cur.critter==null&&hex.critter!=null){
					JSONObject none = new JSONObject();
					none.put("type", "nothing");
					none.put("row", j+(i+1)/2);
					none.put("col", i);
					temp.put(none);
				}	
			}
		}
		json.put("state", temp);
		return json;
	}
}