package a5;

import java.io.FileNotFoundException;
import java.util.Scanner;

/** The console user interface for Assignment 5. */
public class Console {
	private Scanner scan;
	private static boolean done;
	CritterWorld cw;

	public static void main(String[] args) throws FileNotFoundException {
		Console console = new Console();
		Constants.read("src/constants.txt");

		while (!done) {
			System.out.println("Enter a command or \"help\" for a list of commands.");
			console.handleCommand();
		}
	}

	/**
	 * Processes a single console command provided by the user.
	 * @throws FileNotFoundException 
	 */
	void handleCommand() throws FileNotFoundException {
		String command = scan.next();

		if (command.equals("new")) {
			newWorld();
		} else if (command.equals("load")) {
			String filename = scan.next();
			loadWorld(filename);
		} else if (command.equals("critters")) {
			String filename = scan.next();
			int n = scan.nextInt();
			loadCritters(filename, n);
		} else if (command.equals("step")) {
			int n = scan.nextInt();
			advanceTime(n);
		} else if (command.equals("info")) {
			worldInfo();
		} else if (command.equals("hex")) {
			int c = scan.nextInt();
			int r = scan.nextInt();
			hexInfo(c, r);
		} else if (command.equals("help")) {
			printHelp();
		} else if (command.equals("exit")) {
			done = true;
		} else
			System.out.println(command + " is not a valid command.");
	}

	/**
	 * Constructs a new Console capable of reading the standard input.
	 */
	public Console() {
		scan = new Scanner(System.in);
		done = false;
	}

	/**
	 * Starts new random world simulation
	 */
	private void newWorld() {
		cw = new CritterWorld();
	}

	/**
	 * Starts new simulation with world specified in filename
	 * 
	 * @param filename
	 * @throws FileNotFoundException 
	 */
	private void loadWorld(String filename) throws FileNotFoundException {
		cw = new CritterWorld(filename);
	}

	/**
	 * Loads critter definition from filename and randomly places n critters
	 * with that definition into the world
	 * 
	 * @param filename
	 * @param n
	 * @throws FileNotFoundException 
	 */
	private void loadCritters(String filename, int n) throws FileNotFoundException {
		for (int i = 0; i < n; i++) {
			cw.addRandomCritter(filename);
		}
	}

	/**
	 * advances the world by n timesteps
	 * 
	 * @param n
	 */
	private void advanceTime(int n) {
		for (int i = 0; i < n; i++) {
			cw.step();
		}
	}

	/**
	 * prints current timestep, number of critters, and world map of the
	 * simulation
	 */
	private void worldInfo() {
		cw.info();
	}

	/**
	 * prints description of the contents of hex (c,r)
	 * 
	 * @param c
	 *            column of hex
	 * @param r
	 *            row of hex
	 */
	private void hexInfo(int c, int r) {
		int arrayRow = r - ((c + 1) / 2);
		cw.hexes[c][arrayRow].getInfo();
	}

	/**
	 * Prints a list of possible commands to the standard output.
	 */
	private void printHelp() {
		System.out.println("new: start a new simulation with a random world");
		System.out.println("load <world_file>: start a new simulation with"
				+ "the world loaded from world_file");
		System.out.println("critters <critter_file> <n>: add n critters"
				+ "defined by critter_file randomly into the world");
		System.out.println("step <n>: advance the world by n timesteps");
		System.out.println("info: print current timestep, number of critters"
				+ "living, and map of world");
		System.out.println("hex <c> <r>: print contents of hex"
				+ "at column c, row r");
		System.out.println("exit: exit the program");
	}
}