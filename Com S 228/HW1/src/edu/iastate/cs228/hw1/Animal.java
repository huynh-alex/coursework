package edu.iastate.cs228.hw1;

/**
 * This class is to be extended by the Badger, Fox, and Rabbit classes.
 * 
 * @author Alex Huynh 
 */
public abstract class Animal extends Living implements MyAge
{
	protected int age;   // age of the animal 
	
	@Override
	/**
	 * @return age of the animal 
	 */
	public int myAge()
	{
		return age;
	}
}
