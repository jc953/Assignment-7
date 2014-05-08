package a5;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import parse.*;
import ast.*;

/**
 * The Critter class represents an individual critter. It has information on the
 * critter state, rules and attributes and coordinates critter functions and
 * actions.
 */
public class Critter {
	public int id;
	static int currentId = 1;
	public String species_id;
	CritterWorld critterworld;
	public Program program;
	public Rule lastRule;
	public int[] mem;
	public int direction;
	public int column;
	public int row; 
	boolean matePossible;

	/**
	 * Constructor for Critter loaded from file
	 * 
	 * @param file
	 *            the file which contains the Critter's rules and attributes
	 * @param direction
	 *            direction new Critter should face
	 * @param column
	 *            column of new Critter
	 * @param row
	 *            row of new Critter
	 * @param critterworld
	 *            the world that this Critter exists in
	 */
	public Critter(String file, int direction, int column, int row,
			CritterWorld critterworld) throws FileNotFoundException{
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(file)));
			int[] memNums = new int[6];
			for (int i = 0; i < memNums.length; i++) {
				String line = br.readLine();
				String[] str = line.split(" ");
				if (str.length != 2)
					throw new FileNotFoundException();
				memNums[i] = Integer.parseInt(str[1]);
			}
			mem = new int[memNums[0]];
			for (int i = 0; i < memNums.length - 1; i++) {
				mem[i] = memNums[i];
			}
			if (mem[4] > mem[3] * Constants.ENERGY_PER_SIZE)
				mem[4] = mem[3] * Constants.ENERGY_PER_SIZE;
			mem[5] = 1;
			mem[6] = 0;
			mem[7] = memNums[5];
			for (int i = 8; i < mem[0]; i++) {
				mem[i] = 0;
			}
			ParserImpl p = new ParserImpl();
			program = p.parse(br);
			this.direction = direction;
			this.critterworld = critterworld;
			this.column = column;
			this.row = row;
			lastRule = null;
			matePossible = false;
			br.close();
			id = currentId;
			currentId++;
			species_id = "supercritter";
		} catch (IOException e) {
			throw new FileNotFoundException();
		}
	}

	/**
	 * Constructor for Critter born out of budding or mating of parents
	 * 
	 * @param program
	 *            the new Critter's rule set
	 * @param mem
	 *            the new Critter's memory
	 * @param direction
	 *            the new Critter's direction
	 * @param column
	 *            the new Critter's column
	 * @param row
	 *            the new Critter's row
	 * @param critterworld
	 *            the world that the new Critter belongs to
	 */
	public Critter(Program program, int[] mem, int direction, int column,
			int row, CritterWorld critterworld) {
		this.mem = mem;
		mem[3] = 1;
		mem[4] = Constants.INITIAL_ENERGY;
		mem[5] = 1;
		for (int i = 6; i < mem[0]; i++) {
			mem[i] = 0;
		}
		this.program = program;
		this.direction = direction;
		this.column = column;
		this.row = row;
		this.critterworld = critterworld;
		lastRule = null;
		matePossible = false;
		id = currentId;
		currentId++;
		species_id="supercritter";
	}
	
	public Critter(JSONObject json, CritterWorld critterworld) throws JSONException{
		id = json.getInt("id");
		species_id = json.getString("species_id");
		this.critterworld = critterworld;
		this.column = json.getInt("col");
		this.row = json.getInt("row");
		this.direction = json.getInt("direction");
		JSONArray mems = json.getJSONArray("mem");
		this.mem = new int[mems.length()];
		for (int i = 0; i < mems.length(); i++){
			this.mem[i] = mems.getInt(i);
		}
		matePossible = false;
		lastRule = null;
		StringReader str = new StringReader(json.getString("program")+";");
		ParserImpl p = new ParserImpl();
		System.out.println(json.getString("program"));
		program = p.parse(str);
	}

	/**
	 * method to coordinate the actions and reactions of this Critter when the
	 * world advances a timestep.
	 * 
	 * @throws InterruptedException
	 */
	public void step() {
		mem[5] = 0;
		boolean actionDone = false;
		while (!actionDone && mem[5] < 999) {
			for (Rule r : program.rules) {
				if (r.condition.eval(this)) {
					for (Update u : r.updates) {
						if (u.idx.eval(this) == 7) {
							if (u.val.eval(this) >= 0 || u.val.eval(this) <= 99) {
								mem[u.idx.eval(this)] = u.val.eval(this);
							}
						} else if (u.idx.eval(this) > 7
								&& u.idx.eval(this) < mem.length) {
							mem[u.idx.eval(this)] = u.val.eval(this);
						}
					}
					if (r.must != null) {
						if (r.must instanceof NullaryAction) {
							NullaryAction act = (NullaryAction) r.must;
							switch (act.op) {
							case WAIT:
								waitTurn();
								break;
							case FORWARD:
								move(1);
								break;
							case BACKWARD:
								move(-1);
								break;
							case LEFT:
								turn(-1);
								break;
							case RIGHT:
								turn(1);
								break;
							case EAT:
								eat();
								break;
							case ATTACK:
								attack();
								break;
							case GROW:
								grow();
								break;
							case BUD:
								bud();
								break;
							case MATE:
								mate();
								break;
							}
							actionDone = true;
						} else if (r.must instanceof UnaryAction) {
							UnaryAction act = (UnaryAction) r.must;
							switch (act.op) {
							case TAG:
								tag(act.expr.eval(this));
								break;
							case SERVE:
								serve(act.expr.eval(this));
								break;
							}
							actionDone = true;
						} else if (r.must instanceof Update) {
							Update u = (Update) r.must;
							mem[u.idx.eval(this)] = u.val.eval(this);
						}
					}
					lastRule = r;
					break;
				}
			}
			mem[5]++;
		}
		if (!actionDone) {
			waitTurn();
		}
	}

	/**
	 * method to give the adjacent positions to the Critter's current position
	 * 
	 * @param column
	 *            the Critter's current column
	 * @param row
	 *            the Critter's current row
	 * @return int array [nextColumn, nextRow, prevColumn, prevRow]
	 */
	public int[] getAdjacentPositions(int column, int row) {
		int[] result = new int[4];
		if (column % 2 == 0) {
			switch (direction) {
			case 0:
				result[0] = column;
				result[1] = row + 1;
				result[2] = column;
				result[3] = row - 1;
				break;
			case 1:
				result[0] = column + 1;
				result[1] = row;
				result[2] = column - 1;
				result[3] = row - 1;
				break;
			case 2:
				result[0] = column + 1;
				result[1] = row - 1;
				result[2] = column - 1;
				result[3] = row;
				break;
			case 3:
				result[0] = column;
				result[1] = row - 1;
				result[2] = column;
				result[3] = row + 1;
				break;
			case 4:
				result[0] = column - 1;
				result[1] = row - 1;
				result[2] = column + 1;
				result[3] = row;
				break;
			case 5:
				result[0] = column - 1;
				result[1] = row;
				result[2] = column + 1;
				result[3] = row - 1;
				break;
			}
		} else {
			switch (direction) {
			case 0:
				result[0] = column;
				result[1] = row + 1;
				result[2] = column;
				result[3] = row - 1;
				break;
			case 1:
				result[0] = column + 1;
				result[1] = row + 1;
				result[2] = column - 1;
				result[3] = row;
				break;
			case 2:
				result[0] = column + 1;
				result[1] = row;
				result[2] = column - 1;
				result[3] = row + 1;
				break;
			case 3:
				result[0] = column;
				result[1] = row - 1;
				result[2] = column;
				result[3] = row + 1;
				break;
			case 4:
				result[0] = column - 1;
				result[1] = row;
				result[2] = column + 1;
				result[3] = row + 1;
				break;
			case 5:
				result[0] = column - 1;
				result[1] = row + 1;
				result[2] = column + 1;
				result[3] = row;
				break;
			}
		}
		return result;
	}

	/**
	 * method to check whether the Critter is trying to situate itself on a
	 * rock, another Critter or off the edge of the world
	 * 
	 * @param column
	 *            Critter's current column
	 * @param row
	 *            Critter's current row
	 * @return true if the Critter can occupy the position it is trying to
	 *         currently occupy. False otherwise
	 */
	public boolean validHex(int column, int row) {
		return column >= 0 && column < Constants.MAX_COLUMN && row >= 0
				&& row < Constants.MAX_ARRAY_ROW
				&& critterworld.hexes[column][row].isFree();
	}

	/**
	 * method to get the Critter's complexity
	 * 
	 * @return Critter's complexity
	 */
	public int getComplexity() {
		int a = program.rules.size() * Constants.RULE_COST;
		int b = (mem[1] + mem[2]) * Constants.ABILITY_COST;
		return a + b;
	}

	/**
	 * method to perform the wait action
	 */
	public void waitTurn() {
		mem[4] = mem[4] + Constants.SOLAR_FLUX;
		if (mem[4] > mem[3] * Constants.ENERGY_PER_SIZE)
			mem[4] = mem[3] * Constants.ENERGY_PER_SIZE;
	}

	/**
	 * method to perform the forward or backward action
	 */
	public void move(int forOrBack) {
		assert (forOrBack == -1 || forOrBack == 1);
		if (Constants.MOVE_COST >= mem[4]) {
			critterworld.kill(this);
			return;
		}
		mem[4] -= Constants.MOVE_COST;
		int[] pos = getAdjacentPositions(column, row);
		if (forOrBack == -1) {
			if (!validHex(pos[2], pos[3])) {
				return;
			} else {
				critterworld.hexes[column][row].critter = null;
				critterworld.hexes[pos[2]][pos[3]].critter = this;
				column = pos[2];
				row = pos[3];
				return;
			}
		} else {
			if (!validHex(pos[0], pos[1])) {
				return;
			} else {
				critterworld.hexes[column][row].critter = null;
				critterworld.hexes[pos[0]][pos[1]].critter = this;
				column = pos[0];
				row = pos[1];
				return;
			}
		}
	}

	/**
	 * method to perform the left or right action
	 */
	public void turn(int n) {
		assert (n == 1 || n == -1);
		direction = direction + n;
		if (direction < 0)
			direction += 6;
		if (direction > 5)
			direction -= 6;
		mem[4] -= mem[3];
		if (mem[4] <= 0) {
			critterworld.kill(this);
		}
	}

	/**
	 * method to perform the eat action
	 */
	public void eat() {
		mem[4] -= mem[3];
		if (mem[4] + critterworld.hexes[column][row].food > Constants.ENERGY_PER_SIZE
				* mem[3]) {
			int difference = Constants.ENERGY_PER_SIZE * mem[3] - mem[4];
			mem[4] = Constants.ENERGY_PER_SIZE * mem[3];
			critterworld.hexes[column][row].food = critterworld.hexes[column][row].food
					- difference;
		} else {
			mem[4] = mem[4] + critterworld.hexes[column][row].food;
			critterworld.hexes[column][row].food = 0;
		}
		if (mem[4] <= 0) {
			critterworld.kill(this);
		}
	}

	/**
	 * method to perform the serve action
	 */
	public void serve(int amountServed) {
		if (amountServed < mem[4]) {
			mem[4] -= (amountServed + mem[3]);
			critterworld.hexes[column][row].food += amountServed;
		} else {
			critterworld.hexes[column][row].food += mem[4];
			critterworld.kill(this);
		}
	}

	/**
	 * method to perform the attack action
	 */
	public void attack() {
		int[] pos = getAdjacentPositions(column, row);
		if (pos[0] < 0 || pos[0] > Constants.MAX_COLUMN) {
			return;
		} else if (pos[1] < 0 || pos[1] > Constants.MAX_ARRAY_ROW) {
			return;
		}
		if (critterworld.hexes[pos[0]][pos[1]].critter != null) {
			critterworld.hexes[pos[0]][pos[1]].critter.attacked(this);
			mem[4] -= mem[3] * Constants.ATTACK_COST;
		}
	}

	/**
	 * method to coordinate a Critter's response to being attacked by another
	 * Critter
	 */
	public void attacked(Critter attacker) {
		double x = Constants.DAMAGE_INC
				* ((attacker.mem[3] * attacker.mem[2]) - (mem[3] * mem[1]));
		double p = 1.0 / (1.0 + Math.pow(Math.E, -x));
		double damage = Constants.BASE_DAMAGE * attacker.mem[3] * p;
		if (damage >= mem[4]) {
			critterworld.kill(this);
		} else {
			mem[4] = mem[4] - (int) (damage);
		}
	}

	/**
	 * method to perform the tag action
	 */
	public void tag(int tagNumber) {
		mem[4] -= mem[3];
		if (tagNumber > 99 && tagNumber < 0) {
			return;
		}
		int[] pos = getAdjacentPositions(column, row);
		if (pos[0] < 0 || pos[0] > Constants.MAX_COLUMN) {
			return;
		} else if (pos[1] < 0 || pos[1] > Constants.MAX_ARRAY_ROW) {
			return;
		}
		if (critterworld.hexes[pos[0]][pos[1]].critter != null) {
			critterworld.hexes[pos[0]][pos[1]].critter.mem[6] = tagNumber;
		}
	}

	/**
	 * method to perform the grow action
	 */
	public void grow() {
		if (mem[3] * getComplexity() * Constants.GROW_COST >= mem[4]) {
			critterworld.kill(this);
		} else {
			mem[4] -= mem[3] * getComplexity() * Constants.GROW_COST;
			mem[3]++;
		}
	}

	/**
	 * method to perform the bud action
	 */
	public boolean bud() {
		mem[4] -= Constants.BUD_COST * getComplexity();
		if (mem[4] < 0) {
			critterworld.kill(this);
			return false;
		}
		int[] pos = getAdjacentPositions(column, row);
		if (!validHex(pos[2], pos[3])) {
			return false;
		} else {
			Program tempProg = program.dup(program);
			int[] tempMem = Arrays.copyOf(mem, mem.length);
			while (Math.random() < 0.25) {
				if (Math.random() < 0.5) {
					Mutation.mutate(tempProg);
				} else {
					tempMem = mutateAttributes(tempMem);
				}
			}
			Critter c = new Critter(tempProg, tempMem, direction, pos[2],
					pos[3], critterworld);
			critterworld.addCritter(c, c.column, c.row);
			if (mem[4] == 0)
				critterworld.kill(this);
			return true;
		}
	}

	/**
	 * method to perform the mate action
	 */
	public boolean mate() {
		int[] pos = getAdjacentPositions(column, row);
		Critter mate = critterworld.hexes[pos[0]][pos[1]].critter;
		if (mate == null)
			return false;
		int[] matePos = getAdjacentPositions(mate.column, mate.row);
		if ((mate.direction - direction) % 3 == 0) {
			matePossible = true;
		}
		if (matePossible && mate.matePossible) {
			mem[4] -= Constants.MATE_COST * getComplexity();
			mate.mem[4] -= Constants.MATE_COST * mate.getComplexity();
			if (mem[4] < 0 || mate.mem[4] < 0) {
				if (mem[4] < 0) {
					critterworld.kill(this);
				}
				if (mate.mem[4] < 0) {
					critterworld.kill(mate);
				}
				return false;
			}
			int babyCol;
			int babyRow;
			int babyDir;
			if (!validHex(pos[2], pos[3]) && !validHex(matePos[2], matePos[3])) {
				mem[4] += Constants.MATE_COST * getComplexity();
				mem[4] -= mem[3];
				mate.mem[4] += Constants.MATE_COST * mate.getComplexity();
				mate.mem[4] -= mate.mem[3];
				return false;
			} else if (!validHex(matePos[2], matePos[3])) {
				babyCol = pos[2];
				babyRow = pos[3];
				babyDir = direction;
			} else if (!validHex(pos[2], pos[3])) {
				babyCol = matePos[2];
				babyRow = matePos[3];
				babyDir = mate.direction;
			} else {
				if (Math.random() < 0.5) {
					babyCol = pos[2];
					babyRow = pos[3];
					babyDir = direction;
				} else {
					babyCol = matePos[2];
					babyRow = matePos[3];
					babyDir = mate.direction;
				}
			}
			int rulesize;
			Program tempProg = new Program();
			if (Math.random() < 0.5) {
				rulesize = program.rules.size();
			} else {
				rulesize = mate.program.rules.size();
			}
			int i = 0;
			for (; i < program.rules.size() && i < mate.program.rules.size(); i++) {
				if (Math.random() < 0.5) {
					tempProg.rules.add(program.rules.get(i));
				} else {
					tempProg.rules.add(mate.program.rules.get(i));
				}
			}
			if (program.rules.size() > i && program.rules.size() == rulesize) {
				for (; i < program.rules.size(); i++) {
					tempProg.rules.add(program.rules.get(i));
				}
			}
			if (mate.program.rules.size() > i
					&& mate.program.rules.size() == rulesize) {
				for (; i < mate.program.rules.size(); i++) {
					tempProg.rules.add(mate.program.rules.get(i));
				}
			}
			int[] tempMem;
			if (Math.random() < 0.5) {
				tempMem = new int[mem[0]];
			} else {
				tempMem = new int[mate.mem[0]];
			}
			tempMem[0] = tempMem.length;
			for (int j = 1; j <= 2; j++) {
				if (Math.random() < 0.5) {
					tempMem[j] = mem[j];
				} else {
					tempMem[j] = mate.mem[j];
				}
			}
			while (Math.random() < 0.25) {
				if (Math.random() < 0.5) {
					Mutation.mutate(tempProg);
				} else {
					tempMem = mutateAttributes(tempMem);
				}
			}
			Critter c = new Critter(tempProg, tempMem, babyDir, babyCol,
					babyRow, critterworld);
			critterworld.addCritter(c, c.column, c.row);
			if (mem[4] == 0) {
				critterworld.kill(this);
			}
			if (mate.mem[4] == 0) {
				critterworld.kill(mate);
			}
			return true;
		} else {
			return false;
		}
	}

	/**
	 * method to coordinate the mutation of the Critter's offspring's attributes
	 * at the time of budding or mating
	 */
	public int[] mutateAttributes(int[] mem) {
		double i = Math.random();
		if (i < 1.0 / 3) {
			if (mem[0] == 8) {
				mem[0]++;
				mem = Arrays.copyOf(mem, mem[0]);
				return mem;
			}
			if (Math.random() < 0.5) {
				mem[0]++;
			} else {
				mem[0]--;
			}
			mem = Arrays.copyOf(mem, mem[0]);
			return mem;
		} else if (i < 2.0 / 3) {
			if (mem[1] == 1) {
				mem[1]++;
				return mem;
			}
			if (Math.random() < 0.5) {
				mem[1]++;
			} else {
				mem[1]--;
			}
			return mem;
		} else {
			if (mem[2] == 1) {
				mem[2]++;
				return mem;
			}
			if (Math.random() < 0.5) {
				mem[2]++;
			} else {
				mem[2]--;
			}
			return mem;
		}
	}

	/**
	 * method to perform the nearby sensory action
	 */
	public int nearby(int dir) {
		int originalDir = direction;
		direction = (direction + dir) % 6;
		while (direction < 0)
			direction += 6;
		int pos[] = getAdjacentPositions(column, row);
		if (pos[0] < 0 || pos[0] >= Constants.MAX_COLUMN || pos[1] < 0
				|| pos[1] >= Constants.MAX_ARRAY_ROW) {
			return 0;
		}
		int ans = critterworld.hexes[pos[0]][pos[1]].determineContents(false);
		direction = originalDir;
		return ans;
	}

	/**
	 * method to perform the ahead sensory action
	 */
	public int ahead(int dist) {
		if (dist == 0)
			return critterworld.hexes[column][row].determineContents(false);
		if (dist == -1)
			return critterworld.hexes[column][row].determineContents(true);
		if (dist < -1)
			dist = -dist - 1;
		int tempCol = column;
		int tempRow = row;
		for (int i = 1; i < Math.abs(dist); i++) {
			int[] pos = getAdjacentPositions(tempCol, tempRow);
			tempCol = pos[0];
			tempRow = pos[1];
			if (pos[0] < 0 || pos[0] >= Constants.MAX_COLUMN || pos[1] < 0
					|| pos[1] >= Constants.MAX_ARRAY_ROW) {
				return 0;
			}
		}
		int ans = critterworld.hexes[tempCol][tempRow].determineContents(false);
		return ans;
	}

	/**
	 * method to display comprehensive information about this Critter and all
	 * its attributes and rules
	 */
	public void getInfo() {
		System.out.println("This hex contains a critter.");
		System.out.println("MEMSIZE : " + mem[0]);
		System.out.println("DEFENSE : " + mem[1]);
		System.out.println("OFFENSE : " + mem[2]);
		System.out.println("SIZE : " + mem[3]);
		System.out.println("ENERGY : " + mem[4]);
		System.out.println("PASS : " + mem[5]);
		System.out.println("TAG : " + mem[6]);
		System.out.println("POSTURE : " + mem[7]);
		for (int i = 8; i < mem.length; i++) {
			System.out.println("mem[" + i + "] : " + mem[i]);
		}
		StringBuffer sb = new StringBuffer();
		program.prettyPrint(sb);
		System.out.println(sb);
		sb = new StringBuffer("The last rule performed was \n");
		if (lastRule != null) {
			lastRule.prettyPrint(sb);
			System.out.println(sb);
		} else {
			System.out.println("This critter has not performed a rule yet.");
		}
	}
	
	public JSONObject getJson() throws JSONException{
		JSONObject json = new JSONObject();
		StringBuffer sb = new StringBuffer();
		json.put("id", id);
		json.put("species_id", species_id);
		program.prettyPrint(sb);
		json.put("program", sb);
		json.put("row", row+(column+1)/2);
		json.put("column", column);
		json.put("direction", direction);
		json.put("mem", mem);
		sb = new StringBuffer();
		if (lastRule == null){
			json.put("recently_executed_rule", "No rule executed yet");
		} else {
			lastRule.prettyPrint(sb);
			json.put("recently_executed_rule", sb);
		}
		return json;
	}
}
