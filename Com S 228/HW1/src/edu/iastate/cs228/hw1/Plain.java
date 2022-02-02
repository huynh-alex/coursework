package edu.iastate.cs228.hw1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.Random;

/**
 * 
 * The plain is represented as a square grid of size width x width.
 * 
 * @author Alex Huynh
 */
public class Plain {
	private int width; // grid size: width X width

	public Living[][] grid;

	/**
	 * Default constructor reads from a file
	 * 
	 * @param inputFileName Name of file
	 * @throws FileNotFoundException
	 */
	public Plain(String inputFileName) throws FileNotFoundException {
		File inputFile = new File(inputFileName);

		Scanner sc = new Scanner(inputFile);

		int charsPerLine = sc.nextLine().length();

		// if there are not 1/2 extra spaces at the end, this will fix the length to not
		// truncate
		while (charsPerLine % 3 != 0) {
			charsPerLine++;
		}
		width = (charsPerLine / 3); // every square grid takes 3 space on a text file

		grid = new Living[width][width];

		// close and re-open because we want to start at the first line
		sc.close();

		sc = new Scanner(inputFile);

		for (int i = 0; i < width; i++) {
			for (int j = 0; j < width; j++) {
				String input = sc.next();

				if (input.charAt(0) == 'B') {
					// -48 to convert ASCII char to int
					grid[i][j] = new Badger(this, i, j, (((int) input.charAt(1))) - 48);
				} else if (input.charAt(0) == 'F') {
					grid[i][j] = new Fox(this, i, j, (((int) input.charAt(1))) - 48);
				} else if (input.charAt(0) == 'R') {
					grid[i][j] = new Rabbit(this, i, j, (((int) input.charAt(1))) - 48);
				} else if (input.charAt(0) == 'G') {
					grid[i][j] = new Grass(this, i, j);
				} else // must be empty
				{
					grid[i][j] = new Empty(this, i, j);
				}
			}
		}
		sc.close();
	}

	/**
	 * Constructor that builds a w x w grid without initializing it.
	 * 
	 * @param w width the grid
	 */
	public Plain(int w) {
		grid = new Living[w][w];
		width = w;
	}

	/**
	 * Returns the width of the plain grid
	 * 
	 * @return grid width
	 */
	public int getWidth() {
		return grid.length;
	}

	/**
	 * Initialize the plain by randomly assigning to every square of the grid one of
	 * BADGER, FOX, RABBIT, GRASS, or EMPTY.
	 * 
	 * Every animal starts at age 0.
	 */
	public void randomInit() {
		Random generator = new Random();

		for (int i = 0; i < width; i++) {
			for (int j = 0; j < width; j++) {
				int spawn = generator.nextInt(5);
				if (spawn == 0) {
					grid[i][j] = new Badger(this, i, j, 0);
				}
				if (spawn == 1) {
					grid[i][j] = new Fox(this, i, j, 0);
				}
				if (spawn == 2) {
					grid[i][j] = new Rabbit(this, i, j, 0);
				}
				if (spawn == 3) {
					grid[i][j] = new Grass(this, i, j);
				}
				if (spawn == 4) {
					grid[i][j] = new Empty(this, i, j);
				}
			}
		}
	}

	/**
	 * Output the plain grid. For each square, output the first letter of the living
	 * form occupying the square. If the living form is an animal, then output the
	 * age of the animal followed by a blank space; otherwise, output two blanks.
	 */
	public String toString() {
		String out = "";
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < width; j++) {

				Living input = grid[i][j];

				State inputState = input.who();

				if (inputState == State.BADGER) {
					out += "B" + ((Badger) input).myAge() + " ";
				} else if (inputState == State.FOX) {
					out += "F" + ((Fox) input).myAge() + " ";
				} else if (inputState == State.RABBIT) {
					out += "R" + ((Rabbit) input).myAge() + " ";
				} else if (inputState == State.GRASS) {
					out += "G" + "  ";
				} else // must be empty
				{
					out += "E" + "  ";
				}
			}
			out += "\n";
		}
		return out;
	}

	/**
	 * Write the plain grid to an output file. Also useful for saving a randomly
	 * generated plain for debugging purpose.
	 * 
	 * @param outputFileName Name of output file
	 * @throws FileNotFoundException
	 */
	public void write(String outputFileName) throws FileNotFoundException {

		Scanner in = new Scanner(this.toString());

		File file = new File(outputFileName);

		PrintWriter out = new PrintWriter(file);

		for (int i = 0; i < width; i++) {

			for (int j = 0; j < width; j++) {

				String input = in.next();

				if (input.length() == 1) {
					out.print(input + "  ");
				} else if (input.length() == 2) {
					out.print(input + " ");
				}
			}
			if (i < width - 1) {
				out.println();
			}
		}
		out.close();
		in.close();
	}
}
