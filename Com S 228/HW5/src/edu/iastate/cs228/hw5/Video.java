package edu.iastate.cs228.hw5;

/**
 * 
 * @author Alex Huynh
 *
 */

public class Video implements Comparable<Video>
{
	private String film;           // film title for the video
	private int numCopies; 
	private int numRentedCopies; 
	
	/**
	 * 
	 * @param film
	 * @param n
	 * @throws IllegalArgumentException if copies <= 0
	 */
	public Video(String film, int n) throws IllegalArgumentException 
	{
		if(n <= 0)	throw new IllegalArgumentException();

		this.film = film;
		numCopies = n;
		numRentedCopies = 0;
	}
	
	
	public Video(String film)
	{
		this(film, 1); 
	}

	
	/**
	 * 
	 * @return title of the Video (film) as a String
	 */
	public String getFilm()
	{
		return film; 
	}
	

	public int getNumCopies()
	{
		return numCopies; 
	}

	
	/**
	 *  Add more copies
	 * @param n
	 * @throws IllegalArgumentException if n <= 0
	 */
	public void addNumCopies(int n) throws IllegalArgumentException
	{
		if(n <= 0)	throw new IllegalArgumentException();

		numCopies += n;
	}
		
	public int getNumAvailableCopies()
	{
		return numCopies - numRentedCopies; 
	}


	public int getNumRentedCopies()
	{
		return numRentedCopies; 
	}

	
	/**
	 * Updates numRentedCopies.  If n + numRentedCopies > numCopies, sets numRentedCopies 
	 * to numCopies.  (In other words, rent out all the available copies.) i.e. Max out rent
	 * 
	 * @param n
	 * @throws IllegalArgumentException if n <= 0
	 * @throws AllCopiesRentedOutException if numRentedCopies == numCopies 
	 */
    public void rentCopies(int n) throws IllegalArgumentException, AllCopiesRentedOutException
    {
    	if(n <= 0) throw new IllegalArgumentException();
    	
    	if(numRentedCopies == numCopies)	throw new AllCopiesRentedOutException();
    	

    	if(n + numRentedCopies > numCopies)
    	{
    		numRentedCopies = numCopies;
    	}
    	else
    	{
   			numRentedCopies += n;
    	}
    }
    
    
    /**
     * Updates numRentedCopies.  If n > numRentedCopies, set numRentedCopies to zero.  
     * 
     * @param n
     * @throws IllegalArgumentException if n <= 0  
     */
    public void returnCopies(int n) throws IllegalArgumentException
    {
    	try
    	{
	    	if(n <= 0)	throw new IllegalArgumentException("Film " + film + " has an invalid request");
	    
	    	//cannot return anything that hasn't been rented yet
	    	else if(numRentedCopies == 0) throw new IllegalArgumentException("Film " + film + " has an invalid request");

	    	else if(n > numRentedCopies)
			{
				numRentedCopies = 0;
			}
			else
			{
				numRentedCopies -= n;
			}
    	}
    	catch(IllegalArgumentException e)
    	{
    		System.out.println(e.getMessage());
    	}
    }
	

    /**
	 * Compares two videos by name using string comparison.
	 */
	public int compareTo(Video vd)
	{
		return this.film.compareTo(vd.getFilm());
	}
	
	
	/**
	 * Write in the format "<film> (<numCopies>:<numRentedCopies>)", where every substring 
	 * in the form of a variable name delimited by a pair of angle brackets is replaced with the 
	 * value of the variable. For example, if a Video object has its private instance variables 
	 * take on the values below: 
	 * 
	 * film == "Forrest Gump" 
	 * numCopies == 2
	 * numRentedCopies == 1 
	 *  
	 * then the method returns the string "Forrest Gump (2:1)". 
	 */
	@Override 
	public String toString()
	{
		String out = "";
		out += film + " ";
		out += "(" + numCopies + ":" + numRentedCopies + ")";
		return out; 
	}
}
