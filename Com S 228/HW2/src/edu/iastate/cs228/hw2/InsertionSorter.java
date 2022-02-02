package edu.iastate.cs228.hw2;

import java.io.FileNotFoundException;
import java.lang.NumberFormatException; 
import java.lang.IllegalArgumentException; 
import java.util.InputMismatchException;


/**
 *  
 * This class implements insertion sort.   
 * @author Alex Huynh
 * 
 */

public class InsertionSorter extends AbstractSorter 
{	
	/**
	 * Constructor takes an array of points.  It invokes the superclass constructor, and also 
	 * set the instance variables algorithm in the superclass.
	 * 
	 * @param pts  
	 */
	public InsertionSorter(Point[] pts) 
	{
		super(pts);
		
		super.algorithm = "insertion sort";
	}	

	
	/** 
	 * Perform insertion sort on the array points[] of the parent class AbstractSorter.  
	 */
	@Override 
	public void sort()
	{
		for(int i = 1; i < points.length; i++)
		{
			int temp = i;
			int j = i - 1;
			
			while(j >= 0 && pointComparator.compare(points[j], points[temp] ) > 0)
			{
				swap(j + 1, j);
				j--;
				temp--;
			}
		}
	}		
}
