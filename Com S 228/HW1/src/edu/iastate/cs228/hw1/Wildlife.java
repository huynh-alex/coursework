package edu.iastate.cs228.hw1;

import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * The Wildlife class performs a simulation of a grid plain with squares
 * inhabited by badgers, foxes, rabbits, grass, or none.
 * 
 * @author Alex Huynh
 */
public class Wildlife {

	/**
	 * Update the new plain from the old plain in one cycle.
	 * 
	 * @param pOld old plain
	 * @param pNew new plain
	 */
	public static void updatePlain(Plain pOld, Plain pNew) {
		for (int i = 0; i < pOld.getWidth(); i++) {
			for (int j = 0; j < pOld.getWidth(); j++) {
				pNew.grid[i][j] = pOld.grid[i][j].next(pNew);
			}
		}
	}

	/**
	 * Repeatedly generates plains either randomly or from reading files. Over each
	 * plain, carries out an input number of cycles of evolution.
	 * 
	 * @param args
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws FileNotFoundException {

		int trialNumber = 0;
		Scanner sc = new Scanner(System.in);
		int input = 0; // either 1,2, or 3 based on user input from keyboard

		System.out.println("Simulation of Wildlife of the Plain");
		System.out.println("Keys: 1 (Random Plain), 2 (File Input), 3 (Exit)");

		boolean running = true;

		while (running == true) {
			int gridWidth = 0;
			int numCycles = 0;
			trialNumber++;

			System.out.print("Trial " + trialNumber + ": ");

			input = sc.nextInt();
			if (input == 1) {
				System.out.println("Random Plain");

				System.out.print("Enter grid width: ");
				gridWidth = sc.nextInt();

				Plain plain = new Plain(gridWidth);
				plain.randomInit();

				while (numCycles <= 0) {
					System.out.print("Enter the number of cycles: ");
					numCycles = sc.nextInt();
				}

				System.out.println("\nInitial plain: \n");
				System.out.println(plain);

				Plain pNew = new Plain(plain.getWidth());

				Plain even = plain;	//the "old", first plain, will be defined as even
									//further iterations will be just 1 after the odd plain
				Plain odd = pNew;

				for (int i = 0; i < numCycles; i++) {
					//if the current cycle is even, then the next (new) is odd, so the second parameter must be odd
					if (i % 2 == 0) {
						updatePlain(even, odd);
					}
					//if the current cycle is odd, then the next (new) cycle is even, so the second parameter must be even
					else {
						updatePlain(odd, even);
					}
				}

				System.out.println("Final plain: \n");
				if (numCycles % 2 == 0) {
					System.out.println(even);
				} else {
					System.out.println(odd);
				}
			}

			if (input == 2) {
				System.out.println("Plain input from a file");

				System.out.print("File name: ");

				Plain plain = new Plain(sc.next());

				System.out.print("Enter the number of cycles: ");
				numCycles = sc.nextInt();

				System.out.println("\nInitial plain: \n");
				System.out.println(plain);

				Plain pNew = new Plain(plain.getWidth());

				Plain even = plain;
				Plain odd = pNew;

				for (int i = 0; i < numCycles; i++) {
					if (i % 2 == 0) {
						updatePlain(even, odd);
					} else {
						updatePlain(odd, even);
					}
				}

				System.out.println("Final plain: \n");
				if (numCycles % 2 == 0) {
					System.out.println(even);
				} else {
					System.out.println(odd);
				}
			}

			if (input == 3) {
				sc.close();
				running = false;
			}
		}
	}
}