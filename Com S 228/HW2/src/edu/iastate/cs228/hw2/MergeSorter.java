package edu.iastate.cs228.hw2;

import java.io.FileNotFoundException;
import java.lang.NumberFormatException; 
import java.lang.IllegalArgumentException; 
import java.util.InputMismatchException;

/**
 * This class implements the mergesort algorithm.   
 *  
 *  @author Alex Huynh
 */

public class MergeSorter extends AbstractSorter
{
	
	/** 
	 * Constructor takes an array of points.  It invokes the superclass constructor, and also 
	 * set the instance variables algorithm in the superclass.
	 *  
	 * @param pts   input array of integers
	 */
	public MergeSorter(Point[] pts) 
	{
		super(pts);
		
		super.algorithm = "merge sort";
	}


	/**
	 * Perform mergesort on the array points[] of the parent class AbstractSorter. 
	 * 
	 */
	@Override 
	public void sort()
	{
		mergeSortRec(points);
	}

	
	/**
	 * This is a recursive method that carries out mergesort on an array pts[] of points. One 
	 * way is to make copies of the two halves of pts[], recursively call mergeSort on them, 
	 * and merge the two sorted subarrays into pts[].   
	 * 
	 * @param pts	point array 
	 */
	private void mergeSortRec(Point[] pts)
	{
		if(pts.length == 1)
		{
			return;
		}
		Point[] left = new Point[pts.length / 2];
		
		int i;
		for(i = 0; i < left.length; i++)
		{
			left[i] = pts[i];
		}
		
		Point[] right = new Point[pts.length - pts.length / 2];
		
		int j;
		for(j = 0; j < right.length; j++)
		{
			right[j] = pts[i];
			i++;
		}		
		
		mergeSortRec(left);
		mergeSortRec(right);
		
		merge(pts, left, right);		
	}

	private void merge(Point[] out, Point[] a, Point[] b)
	{		
		int aCounter = 0;
		int bCounter = 0;
		
		for(aCounter = 0; aCounter < a.length && bCounter < b.length;)
		{
			if(pointComparator.compare(a[aCounter], b[bCounter]) < 0)
			{
				out[aCounter + bCounter] = a[aCounter];
				aCounter++;
			}
			else
			{
				out[aCounter + bCounter] = b[bCounter];
				bCounter++;
			}
		}
		for(; aCounter < a.length; aCounter++)
		{
			out[aCounter + bCounter] = a[aCounter];
		}
		for(; bCounter < b.length; bCounter++)
		{
			out[aCounter + bCounter] = b[bCounter];
		}		
	}
}
