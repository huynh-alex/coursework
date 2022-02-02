package edu.iastate.cs228.hw5;


import java.io.FileNotFoundException;
import java.util.Scanner; 

/**
 *  
 * @author Alex Huynh
 *
 */

/**
 * 
 * The Transactions class simulates video transactions at a video store. 
 *
 */
public class Transactions 
{
	
	/**
	 * The main method generates a simulation of rental and return activities.  
	 *  
	 * @param args
	 * @throws FileNotFoundException
	 * @throws AllCopiesRentedOutException 
	 * @throws FilmNotInInventoryException 
	 * @throws IllegalArgumentException 
	 */
	@SuppressWarnings("static-access")
	public static void main(String[] args) throws FileNotFoundException, IllegalArgumentException, FilmNotInInventoryException, AllCopiesRentedOutException
	{	
		// 1. Construct a VideoStore object.
		// 2. Simulate transactions as in the example given in Section 4 of the 
		//    the project description. 

		VideoStore vs = new VideoStore("videoList1.txt");

		System.out.println("Transaction at a Video Store");
		System.out.println("keys:\t1 (rent)\t2 (bulk rent)");
		System.out.println("\t3 (return)\t4 (bulk return)");
		System.out.println("\t5 (summary)\t6 (exit)\n");
		
		boolean running = true;
		int trans = -1;
		Scanner sc = new Scanner(System.in);

		while(running)
		{
			System.out.print("Transaction: ");
			try
			{
				trans = sc.nextInt();
			}
			catch(Exception e)
			{
				System.out.println("Invalid transaction");
			}

			//need to re-initialize or else Scanner fails to function properly
			sc = new Scanner(System.in);

			if(trans == 1)
			{
				System.out.print("Film to rent: ");
				String line = sc.nextLine().trim();
				vs.videoRent(vs.parseFilmName(line), vs.parseNumCopies(line));
			}
			else if(trans == 2)
			{
				String fileToRent = "";
				System.out.print("Video file (rent): ");
				fileToRent = sc.nextLine();
				vs.bulkRent(fileToRent);
			}
			else if(trans == 3)
			{
				System.out.print("Film to return: ");
				String line = sc.nextLine();
				vs.videoReturn(vs.parseFilmName(line), vs.parseNumCopies(line));
			}
			else if(trans == 4)
			{
				String fileToReturn = "";
				System.out.print("Video file (return): ");
				fileToReturn = sc.nextLine();
				vs.bulkReturn(fileToReturn);
			}
			else if(trans == 5)	System.out.println(vs.transactionsSummary());
			else if(trans == 6)	running = false;		
			System.out.println();
		}
		sc.close();		
	}
}
