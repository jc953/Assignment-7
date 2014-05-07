package a5;


/**
 * The Hex class represents a hex in CritterWorld. Contains data on what is 
 * on the hex.
 */
public class Hex {
	public int food = 0;
	public boolean rock = false;
	public Critter critter = null;
	public double rockDir = Math.random()*360;
	
	/**
	 * Checks if there is currently a rock or critter on this space. Returns true
	 * if there are neither, false if there is either.
	 * 
	 * @return true if the space is free for a rock or critter, false otherwise.
	 */
	public boolean isFree(){
		return (!rock && critter==null);
	}
	
	/**
	 * Determines the contents on this hex and returns an integer value representation.
	 * If nothing is on this hex, it will return 0. If a rock is present, it returns -1.
	 * If a critter is present, it returns the critters appearance. If food is present, it
	 * returns -(food+1). The boolean allows the user to ignore the critter and return 
	 * the food value instead.
	 * 
	 * @param ignoreCritter the case where this ignores the critter and returns the food value
	 * @return the numerical description of the contents on this hex
	 */
	public int determineContents(boolean ignoreCritter){
		if (critter==null && !rock && food==0){
			return 0;
		}
		if(rock) return -1;
		else if(critter != null && !ignoreCritter) 
			return critter.mem[6]*100000+critter.mem[3]*1000+critter.mem[7]*10+critter.direction;
		else return -(food+1);
	}
	
	/**
	 * Returns the info of this hex to be displayed in the grid. If a rock is 
	 * present, it will return #. If a critter and food are present, it will 
	 * return G and the direction the critter is facing. If only a critter is
	 * present, it will return C and the direction the critter is facing. If only
	 * food is present, it will return F. If this hex is empty, it returns -.
	 * 
	 * @return the string value to represent this hex
	 */
	public String getWorldInfo(){
		if (rock){
			return "# ";
		} else if (critter != null && food > 0){
			return "G" + critter.direction;
		} else if (critter != null){
			return "C" + critter.direction;
		} else if (food > 0){
			return "F ";
		} else {
			return "- ";
		}
	}
	
	/**
	 * Prints the information of this grid out to the console. It tells the user
	 * if this hex contains a rock, critter, or food. If a critter is present,
	 * it will show the critters memory locations, rule set, and its last rule
	 * executed. If food is present, it prints the amount of food.
	 */
	public void getInfo(){
		if (rock){
			System.out.println("This hex contains a rock.");
			return;
		}
		if (critter != null) {
			critter.getInfo();
		}
		if (food > 0){
			System.out.println("This hex contains " + food + " food.");
		} else {
			System.out.println("This hex does not contain food.");
		}
	}
}
