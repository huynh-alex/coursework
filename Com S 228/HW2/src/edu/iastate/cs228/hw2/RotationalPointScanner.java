package edu.iastate.cs228.hw2;

import java.io.File;

/**
 * 
 * @author 
 *
 */

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;


/**
 * 
 * This class sorts all the points in an array by polar angle with respect to a reference point whose x and y 
 * coordinates are respectively the medians of the x and y coordinates of the original points. 
 * 
 * It records the employed sorting algorithm as well as the sorting time for comparison. 
 *
 * @author Alex Huynh
 */
public class RotationalPointScanner  
{
	private Point[] points; 
	
	private Point medianCoordinatePoint;  // point whose x and y coordinates are respectively the medians of 
	                                      // the x coordinates and y coordinates of those points in the array points[].
	private Algorithm sortingAlgorithm;    
	
	protected String outputFileName;   // "select.txt", "insert.txt", "merge.txt", or "quick.txt"
	
	protected long scanTime; 	       // execution time in nanoseconds. 
	
	/**
	 * This constructor accepts an array of points and one of the four sorting algorithms as input. Copy 
	 * the points into the array points[]. Set outputFileName. 
	 * 
	 * @param  pts  input array of points 
	 * @throws IllegalArgumentException if pts == null or pts.length == 0.
	 */
	public RotationalPointScanner(Point[] pts, Algorithm algo) throws IllegalArgumentException
	{
		points = new Point[pts.length];
		for(int i = 0; i < points.length; i ++)
		{
			points[i] = pts[i];
		}
		
		sortingAlgorithm = algo;
		
		outputFileName = algo.toString();
	}

	
	/**
	 * This constructor reads points from a file. Set outputFileName. 
	 * 
	 * @param  inputFileName
	 * @throws FileNotFoundException 
	 * @throws InputMismatchException   if the input file contains an odd number of integers
	 */
	protected RotationalPointScanner(String inputFileName, Algorithm algo) throws FileNotFoundException, InputMismatchException
	{
		File in = new File(inputFileName);

		Scanner sc = new Scanner(in);
		
		int length = 0;
		while(sc.hasNextInt())
		{	
			length++;
			sc.nextInt();

			if(!sc.hasNextInt())
			{
				throw new InputMismatchException();
			}
			sc.nextInt();				

		}
		
		points = new Point[length];
		
		int index = 0;
		
		sc = new Scanner(in);

		while(sc.hasNextInt())
		{
			
			int x = sc.nextInt();
			int y = sc.nextInt();
			
			points[index] = new Point(x,y);
			index++;
		}
		sc.close();
		
		sortingAlgorithm = algo;
		
		outputFileName = algo.toString();		
	}

	
	/**
	 * Carry out three rounds of sorting using the algorithm designated by sortingAlgorithm as follows:  
	 *    
	 *     a) Sort points[] by the x-coordinate to get the median x-coordinate. 
	 *     b) Sort points[] again by the y-coordinate to get the median y-coordinate.
	 *     c) Construct medianCoordinatePoint using the obtained median x- and y-coordinates. 
	 *     d) Sort points[] again by the polar angle with respect to medianCoordinatePoint.
	 *  
	 * Based on the value of sortingAlgorithm, create an object of SelectionSorter, InsertionSorter, MergeSorter,
	 * or QuickSorter to carry out sorting. Copy the sorting result back onto the array points[] by calling 
	 * the method getPoints() in AbstractSorter. 
	 *      
	 * @param algo
	 * @return
	 */
	public void scan()
	{		
		AbstractSorter aSorter = null;
		
		switch(sortingAlgorithm)
		{
		case SelectionSort: 
			aSorter = new SelectionSorter(points);
			break; 
		case InsertionSort: 
			aSorter = new InsertionSorter(points);
			break; 
		case MergeSort: 
			aSorter = new MergeSorter(points);
			break; 
		case QuickSort: 
			aSorter = new QuickSorter(points);
			break; 
		default: 
			break; 		
		}
		
		aSorter.setComparator(0);

		Long beforeTime = System.nanoTime();
		aSorter.sort();
		Long afterTime = System.nanoTime();
		scanTime += afterTime - beforeTime;
		
		aSorter.getPoints(points);
		
		Point xMedian = aSorter.getMedian();
		
		aSorter.setComparator(1);
		
		beforeTime = System.nanoTime();
		aSorter.sort();
		afterTime = System.nanoTime();
		scanTime += afterTime - beforeTime;
	
		aSorter.getPoints(points);
		
		Point yMedian = aSorter.getMedian();
		
		medianCoordinatePoint = new Point(xMedian.getX(), yMedian.getY());
		
		aSorter.setReferencePoint(medianCoordinatePoint);

		aSorter.setComparator(2);
		
		beforeTime = System.nanoTime();
		aSorter.sort();
		afterTime = System.nanoTime();
		scanTime += afterTime - beforeTime;
		
		aSorter.getPoints(points);
				
	}
	
	
	/**
	 * Outputs performance statistics in the format: 
	 * 
	 * <sorting algorithm> <size>  <time>
	 * 
	 * For instance, 
	 * 
	 * selection sort   1000	  9200867
	 * 
	 * Use the spacing in the sample run in Section 2 of the project description. 
	 */
	public String stats()
	{
		return sortingAlgorithm.toString() + "\t" + points.length + "\t" + scanTime;
	}
	
	
	/**
	 * Write points[] after a call to scan().  When printed, the points will appear 
	 * in order of polar angle with respect to medianCoordinatePoint with every point occupying a separate 
	 * line.  The x and y coordinates of the point are displayed on the same line with exactly one blank space 
	 * in between. 
	 */
	@Override
	public String toString()
	{
		String pointsString = "";
		
		for(int i = 0; i < points.length; i++)
		{
			pointsString += points[i].getX() + " " + points[i].getY();
			pointsString += "\r\n";
		}
			
		return pointsString; 
	}

	
	/**
	 *  
	 * This method, called after scanning, writes point data into a file by outputFileName. The format 
	 * of data in the file is the same as printed out from toString().  The file can help you verify 
	 * the full correctness of a sorting result and debug the underlying algorithm. 
	 * 
	 * @throws FileNotFoundException
	 */
	public void writePointsToFile() throws FileNotFoundException
	{
		File file = new File(outputFileName + ".txt");
		
		try 
		{
			FileWriter writer = new FileWriter(file);
						
			writer.close();
		} 
		catch (IOException e) {}
	}	

	
	/**
	 * This method is called after each scan for visually check whether the result is correct.  You  
	 * just need to generate a list of points and a list of segments, depending on the value of 
	 * sortByAngle, as detailed in Section 4.1. Then create a Plot object to call the method myFrame().  
	 */
	public void draw()
	{		
		// Based on Section 4.1, generate the line segments to draw for display of the sorting result.
		// Assign their number to numSegs, and store them in segments[] in the order. 
		
		int numSegs = 0;  // number of segments to draw 

		int nonDistinctPoints = 0;
		
		//every pair of points is a segment if they're different
		for(int i = 0; i < points.length - 1; i++)
		{
			if(!points[i].equals(points[i+1]))
			{
				numSegs++;
			}
			else
			{
				nonDistinctPoints++;
			}
		}
		//if first point is different than last point, that is a segment
		if(!points[0].equals(points[points.length-1]))
		{
			numSegs++;
		}
		
		//segments from median to distinct points (points - non-distinct points)
		numSegs += points.length - nonDistinctPoints;
	
		Segment[] segments = new Segment[numSegs]; 
		
		int segIndex = 0;

		for(int i = 0; i < points.length - 1; i++)
		{
			if(!points[i].equals(points[i+1]))
			{
				segments[segIndex] = new Segment(points[i], points[i + 1]);
				segIndex++;
			}
		}
	
		if(!points[0].equals(points[points.length-1]))
		{
			segments[segIndex] = new Segment(points[0], points[points.length-1]);
			segIndex++;
		}
			
		int mcpIndex = 0;
		for(int i = 0; i < points.length - 1; i++)
		{
			if(!points[i].equals(points[i+1]))
			{
				segments[segIndex] = new Segment(medianCoordinatePoint, points[mcpIndex]);
				segIndex++;
			}
			mcpIndex++;
		}

		//check last two points if they can make a segment
		if(!points[points.length - 2].equals(points[points.length - 1])) 
		{
			segments[segments.length - 1] = new Segment(medianCoordinatePoint, points[points.length - 1]);
		}

		String sort = null; 
		
		switch(sortingAlgorithm)
		{
		case SelectionSort: 
			sort = "Selection Sort"; 
			break; 
		case InsertionSort: 
			sort = "Insertion Sort"; 
			break; 
		case MergeSort: 
			sort = "Mergesort"; 
			break; 
		case QuickSort: 
			sort = "Quicksort"; 
			break; 
		default: 
			break; 		
		}
		
		// The following statement creates a window to display the sorting result.
		Plot.myFrame(points, segments, sort);
		
	}
		
}
