package edu.iastate.cs228.hw2;

import java.io.FileNotFoundException;
import java.util.Scanner; 
import java.util.Random; 

/**
 *
 * This class executes four sorting algorithms: selection sort, insertion sort, mergesort, and
 * quicksort, over randomly generated integers as well integers from a file input. It compares the 
 * execution times of these algorithms on the same input. 
 * 
 * @author Alex Huynh
 * 
 */

public class CompareSorters 
{
	/**
	 * Repeatedly take integer sequences either randomly generated or read from files. 
	 * Use them as coordinates to construct points.  Scan these points with respect to their 
	 * median coordinate point four times, each time using a different sorting algorithm.  
	 * 
	 * @param args
	 **/
	public static void main(String[] args) throws FileNotFoundException
	{	
		boolean running = true;
		int input = -1;
		int trial = 1;
		Scanner sc = new Scanner(System.in);		
		RotationalPointScanner[] scanners = new RotationalPointScanner[4]; 

		System.out.println("Performances of Four Sorting Algorithms in Point Scanning");
		System.out.println("keys: 1 (random integers) 2 (file input) 3 (exit)");

		while(running)
		{
			System.out.print("Trial " + trial + ": ");

			input = sc.nextInt();
			
			if(input == 1)
			{
				System.out.print("Enter number of random points: ");
				
				Random gen = new Random();
				
				Point[] points = generateRandomPoints(sc.nextInt(), gen);
						
				scanners[0] = new RotationalPointScanner(points, Algorithm.SelectionSort);		
				scanners[1] = new RotationalPointScanner(points, Algorithm.InsertionSort);		
				scanners[2] = new RotationalPointScanner(points, Algorithm.MergeSort);		
				scanners[3] = new RotationalPointScanner(points, Algorithm.QuickSort);		
		
				System.out.println("algorithm time size (ns)");

				for(int i = 0; i < scanners.length; i++)
				{
					scanners[i].scan();
					
					scanners[i].draw();		
				}

				System.out.println("----------------------------------");
				for(int i = 0; i < scanners.length; i++)
				{
					System.out.println(scanners[i].stats());
				}
				System.out.println("----------------------------------");
				
				trial++;
			}
			else if(input == 2)
			{
				System.out.println("Points from a file");
				System.out.print("File name: ");
				
				String fileName = sc.next();
				
				scanners[0] = new RotationalPointScanner(fileName, Algorithm.SelectionSort);
				scanners[1] = new RotationalPointScanner(fileName, Algorithm.InsertionSort);
				scanners[2] = new RotationalPointScanner(fileName, Algorithm.MergeSort);
				scanners[3] = new RotationalPointScanner(fileName, Algorithm.QuickSort);
		
				for(int i = 0; i < scanners.length; i++)
				{
					scanners[i].scan();
					
					scanners[i].draw();
				}
				
				System.out.println("----------------------------------");
				for(int i = 0; i < scanners.length; i++)
				{
					System.out.println(scanners[i].stats());
					
					scanners[i].writePointsToFile();
				}
				System.out.println("----------------------------------");

				trial++;
			}
			else if(input == 3)
			{
				running = false;
			}	
		}
		sc.close();
	}
	
	
	/**
	 * This method generates a given number of random points.
	 * The coordinates of these points are pseudo-random numbers within the range 
	 * [-50,50] � [-50,50]. Please refer to Section 3 on how such points can be generated.
	 * 
	 * Ought to be private. Made public for testing. 
	 * 
	 * @param numPts  	number of points
	 * @param rand      Random object to allow seeding of the random number generator
	 * @throws IllegalArgumentException if numPts < 1
	 */
	public static Point[] generateRandomPoints(int numPts, Random rand) throws IllegalArgumentException
	{ 
		if(numPts < 1)
		{
			throw new IllegalArgumentException();
		}

		Point[] out = new Point[numPts];
		
		for(int i = 0; i < out.length; i++)
		{
			out[i] = new Point(rand.nextInt(101) - 50, rand.nextInt(101) - 50);
		}
		
		return out; 
	}
	
}
