package edu.iastate.cs228.hw5;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Scanner;


/**
 * 
 * @author Alex Huynh
 *
 */

public class VideoStore 
{
	protected SplayTree<Video> inventory;     // all the videos at the store
	
	// ------------
	// Constructors 
	// ------------
	
    /**
     * Default constructor sets inventory to an empty tree. 
     */
    public VideoStore()
    {
    	// no need to implement. 
    }
    
    
	/**
	 * Constructor accepts a video file to create its inventory.  Refer to Section 3.2 of  
	 * the project description for details regarding the format of a video file. 
	 * 
	 * Calls setUpInventory(). 
	 * 
	 * @param videoFile  no format checking on the file
	 * @throws FileNotFoundException
	 */
    public VideoStore(String videoFile) throws FileNotFoundException  
    {
    	setUpInventory(videoFile);	
    }
    
    
   /**
     * Accepts a video file to initialize the splay tree inventory.  To be efficient, 
     * add videos to the inventory by calling the addBST() method, which does not splay. 
     * 
     * Refer to Section 3.2 for the format of video file. 
     * 
     * @param  videoFile  correctly formated if exists
     * @throws FileNotFoundException 
     */
    public void setUpInventory(String videoFile) throws FileNotFoundException
    {
    	inventory = new SplayTree<>();
    	bulkImport(videoFile);
    }
	
    
    // ------------------
    // Inventory Addition
    // ------------------
    
    /**
     * Find a Video object by film title. i.e. searches SplayTree for film title and returns the Video if found
     * Basically, does it exist?
     * 
     * @param film
     * @return Video object if found; null otherwise
     */
	public Video findVideo(String film) 
	{
		return inventory.findElement(new Video(film));
	}


	/**
	 * Updates the splay tree inventory by adding a number of video copies of the film.  
	 * (Splaying is justified as new videos are more likely to be rented.) 
	 * 
	 * Calls the add() method of SplayTree to add the video object.  
	 * 
	 *     a) If true is returned, the film was not on the inventory before, and has been added.  
	 *     b) If false is returned, the film is already on the inventory. 
	 *     
	 * The root of the splay tree must store the corresponding Video object for the film. Update 
	 * the number of copies for the film.  
	 * 
	 * @param film  title of the film
	 * @param n     number of video copies 
	 */
	public void addVideo(String film, int n)  
	{
		//already in inventory, so now it is splayed to root --> add copies at root
		if(!inventory.add(new Video(film, n)))	inventory.getRoot().addNumCopies(n);
	}
	

	/**
	 * Add one video copy of the film. 
	 * 
	 * @param film  title of the film
	 */
	public void addVideo(String film)
	{
		//already in inventory, so now it is splayed to root --> add copies at root
		if(!inventory.add(new Video(film)))		inventory.getRoot().addNumCopies(1);
	}
	

	/**
     * Update the splay trees inventory by adding videos.  Perform binary search additions by 
     * calling addBST() without splaying. 
     * 
     * The videoFile format is given in Section 3.2 of the project description. 
     * 
     * @param videoFile  correctly formated if exists 
     * @throws FileNotFoundException
     */
    public void bulkImport(String videoFile) throws FileNotFoundException 
    {
    	File file = new File(videoFile);
    	
    	Scanner sc = new Scanner(file);
    	
    	while(sc.hasNext())
    	{
    		String line = sc.nextLine();
    		    		
    		String film = parseFilmName(line);
    		int copies = parseNumCopies(line);
               		
    		if(copies > 0)
    		{   
    			//import film if it doesn't exist with BST
        		if( inventory.addBST(new Video(film, copies)) == false)
        		{
        			//it did exist, so splay and add copies at root
        			inventory.splay(new Video(film));
        			inventory.getRoot().addNumCopies(copies);
        		}
    		}
    	}
    	sc.close();
    }

    
    // ----------------------------
    // Video Query, Rental & Return 
    // ----------------------------
    
	/**
	 * Search the splay tree inventory to determine if a video is available. 
	 * Basically, is there at least 1 copy?
	 * 
	 * @param  film
	 * @return true if available
	 */
	public boolean available(String film)
	{
		Video vid = inventory.findElement(new Video(film, 1));
		
		//Video does not exist
		if(vid == null) return false;
		
		return vid.getNumAvailableCopies() > 0;
	}
	
	
	/**
     * Update inventory. 
     * 
     * Search if the film is in inventory by calling findElement(new Video(film, 1)). 
     * 
     * If the film is not in inventory, prints the message "Film <film> is not 
     * in inventory", where <film> shall be replaced with the string that is the value 
     * of the parameter film.  If the film is in inventory with no copy left, prints
     * the message "Film <film> has been rented out".
     * 
     * If there is at least one available copy but n is greater than the number of 
     * such copies, rent all available copies. In this case, no AllCopiesRentedOutException
     * is thrown.  
     * 
     * @param film   
     * @param n 
     * @throws IllegalArgumentException      if n <= 0 or film == null or film.isEmpty()
	 * @throws FilmNotInInventoryException   if film is not in the inventory
	 * @throws AllCopiesRentedOutException   if there is zero available copy for the film.
	 */
	public void videoRent(String film, int n) throws IllegalArgumentException, FilmNotInInventoryException,  
									     			 AllCopiesRentedOutException 
	{
		Video vidInInventory = inventory.findElement(new Video(film));
		
		try
		{
			if(n <= 0 || film == null || film.isEmpty())	throw new IllegalArgumentException("Film " + film + " has an invalid request");

			else if(vidInInventory == null)	 throw new FilmNotInInventoryException("Film " + film + " is not in inventory");
			
			else if(vidInInventory.getNumAvailableCopies() == 0)	throw new AllCopiesRentedOutException
														("Film " + film + " has been rented out");
			
			//renting more than is available, rent out all
			else if(n > vidInInventory.getNumAvailableCopies())
			{
				vidInInventory.rentCopies(vidInInventory.getNumCopies());
			}
			else
			{
				vidInInventory.rentCopies(n);
			}
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
	}

	
	/**
	 * Update inventory.
	 * 
	 *    1. Calls videoRent() repeatedly for every video listed in the file.  
	 *    2. For each requested video, do the following: 
	 *       a) If it is not in inventory or is rented out, an exception will be 
	 *          thrown from videoRent().  Based on the exception, prints out the following 
	 *          message: "Film <film> is not in inventory" or "Film <film> 
	 *          has been rented out." In the message, <film> shall be replaced with 
	 *          the name of the video. 
	 *       b) Otherwise, update the video record in the inventory.
	 * 
	 * For details on handling of multiple exceptions and message printing, please read Section 3.4 
	 * of the project description. 
	 *       
	 * @param videoFile  correctly formatted if exists
	 * @throws FileNotFoundException
     * @throws IllegalArgumentException     if the number of copies of any film is <= 0
	 * @throws FilmNotInInventoryException  if any film from the videoFile is not in the inventory 
	 * @throws AllCopiesRentedOutException  if there is zero available copy for some film in videoFile
	 */
	public void bulkRent(String videoFile) throws FileNotFoundException, IllegalArgumentException, 
												  FilmNotInInventoryException, AllCopiesRentedOutException 
	{
		int[] exceptions = new int[3];
		String exceptionMsg = "";
		
		try 
		{		
			File file = new File(videoFile);
		
			Scanner sc = new Scanner(file);
			
			while(sc.hasNext())
			{
				String line = sc.nextLine();
	    		
	    		String film = parseFilmName(line);
	    		int copies = parseNumCopies(line);
	    		    		
	    		if(copies > 0)
	    		{
	    			try
	    			{
	    				videoRent(film, Math.max(copies, 1));
	    			}
	    			catch(FilmNotInInventoryException e)
	    			{
	    				exceptions[1]++;
	    				exceptionMsg += "\nFilm " + film + " is not in inventory";
	    			}
	    			catch(AllCopiesRentedOutException e)
	    			{
	    				exceptions[2]++;
	    				exceptionMsg += "\nFilm " + film + " has been rented out";
	    			}
	    		}
	    		else
	    		{
	    			exceptions[0]++;
    				exceptionMsg += "\nFilm " + film + " has an invalid request";
	    		}
			}
			sc.close();
		}
		catch(FileNotFoundException e)
		{
			throw new FileNotFoundException("File " + videoFile +" not found");
		}
		
		//throw highest exception
		try 
		{
			if(exceptions[0] > 0)	throw new IllegalArgumentException(exceptionMsg);
			else if(exceptions[1] > 0)	throw new FilmNotInInventoryException(exceptionMsg);
			else if(exceptions[2] > 0)	throw new AllCopiesRentedOutException(exceptionMsg);	
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
	}

	
	/**
	 * Update inventory.
	 * 
	 * If n exceeds the number of rented video copies, accepts up to that number of rented copies
	 * while ignoring the extra copies. 
	 * 
	 * @param film
	 * @param n
	 * @throws IllegalArgumentException     if n <= 0 or film == null or film.isEmpty()
	 * @throws FilmNotInInventoryException  if film is not in the inventory
	 */
	public void videoReturn(String film, int n) throws IllegalArgumentException, FilmNotInInventoryException 
	{
		try
		{
			if(n <= 0 || film == null || film.isEmpty())	throw new IllegalArgumentException("Film " + film + " has an invalid request");
			
			else if(findVideo(film) == null) throw new FilmNotInInventoryException("Film " + film + " is not in inventory");
			
			else if(n > inventory.getRoot().getNumRentedCopies() && inventory.getRoot().getNumRentedCopies() > 0)
			{
				inventory.getRoot().returnCopies((inventory.getRoot().getNumRentedCopies()));
			}
			else
			{
				inventory.getRoot().returnCopies(n);
			}
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
			
	}
	
	
	/**
	 * Update inventory. 
	 * 
	 * Handles excessive returned copies of a film in the same way as videoReturn() does.  See Section 
	 * 3.4 of the project description on how to handle multiple exceptions. 
	 * 
	 * @param videoFile
	 * @throws FileNotFoundException
	 * @throws IllegalArgumentException    if the number of return copies of any film is <= 0
	 * @throws FilmNotInInventoryException if a film from videoFile is not in inventory
	 */
	public void bulkReturn(String videoFile) throws FileNotFoundException, IllegalArgumentException,
													FilmNotInInventoryException												
	{
		int[] exceptions = new int[2];
		String exceptionMsg = "";
		
		//METHOD LOOKS GOOD
		try 
		{		
			File file = new File(videoFile);
		
			Scanner sc = new Scanner(file);
			
			while(sc.hasNext())
			{
				String line = sc.nextLine();
	    		
	    		String film = parseFilmName(line);
	    		int copies = parseNumCopies(line);
	    		
	    		if(copies > 0)
	    		{
	    			try
	    			{
	    				videoReturn(film, Math.max(copies, 1));
	    			}
	    			catch(FilmNotInInventoryException e)
	    			{
	    				exceptions[1]++;
	    				exceptionMsg += "\nFilm " + film + " is not in inventory";
	    			}
	    		}
	    		else
	    		{
	    			exceptions[0]++;
    				exceptionMsg += "\nFilm " + film + " has an invalid request";
	    		}
			}
			sc.close();
		}
		catch(FileNotFoundException e)
		{
			throw new FileNotFoundException("File " + videoFile + " not found");	
		}
		
		//throw appropriate message
		try
		{		
			if(exceptions[0] > 0)	throw new IllegalArgumentException(exceptionMsg);
			else if(exceptions[1] > 0)	throw new FilmNotInInventoryException(exceptionMsg);
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
	}
		

	// ------------------------
	// Methods without Splaying
	// ------------------------
		
	/**
	 * Performs inorder traversal on the splay tree inventory to list all the videos by film 
	 * title, whether rented or not.  Below is a sample string if printed out: 
	 * 
	 * 
	 * Films in inventory: 
	 * 
	 * A Streetcar Named Desire (1) 
	 * Brokeback Mountain (1) 
	 * Forrest Gump (1)
	 * Psycho (1) 
	 * Singin' in the Rain (2)
	 * Slumdog Millionaire (5) 
	 * Taxi Driver (1) 
	 * The Godfather (1) 
	 * 
	 * 
	 * @return
	 */
	public String inventoryList()
	{
		String out = "";
		Iterator<Video> iter = inventory.iterator();
		
		while(iter.hasNext())
		{
			Video vid = (Video) iter.next();
			out += vid.getFilm() + " (" + vid.getNumCopies() + ")" + "\n";
		}		
		return out.trim();
	}
	
	/**
	 * Calls rentedVideosList() and unrentedVideosList() sequentially.  For the string format, 
	 * see Transaction 5 in the sample simulation in Section 4 of the project description. 
	 *   
	 * @return 
	 */
	public String transactionsSummary()
	{
		String out = "";
		out += "Rented films: \n\n";
		out += rentedVideosList();
		out += "\nFilms in inventory:\n\n";
		out += unrentedVideosList();
		return out;
	}	
	
	/**
	 * Performs inorder traversal on the splay tree inventory.  Use a splay tree iterator.
	 * 
	 * Below is a sample return string when printed out:
	 * 
	 * Rented films: 
	 * 
	 * Brokeback Mountain (1)
	 * Forrest Gump (1) 
	 * Singin' in the Rain (2)
	 * The Godfather (1)
	 * 
	 * 
	 * @return
	 */
	private String rentedVideosList()
	{
		String out = "";
		Iterator<Video> iter = inventory.iterator();
		
		while(iter.hasNext())
		{
			Video vid = (Video) iter.next();
			if(vid.getNumRentedCopies() > 0)
			{
				out += vid.getFilm() + " (" + vid.getNumRentedCopies() + ")" + "\n";
			}
		}
		return out;
	}

	
	/**
	 * Performs inorder traversal on the splay tree inventory.  Use a splay tree iterator.
	 * Prints only the films that have unrented copies. 
	 * 
	 * Below is a sample return string when printed out:
	 * 
	 * 
	 * Films remaining in inventory:
	 * 
	 * A Streetcar Named Desire (1) 
	 * Forrest Gump (1)
	 * Psycho (1) 
	 * Slumdog Millionaire (4) 
	 * Taxi Driver (1) 
	 * 
	 * 
	 * @return
	 */
	private String unrentedVideosList()
	{
		String out = "";
		Iterator<Video> iter = inventory.iterator();
		
		while(iter.hasNext())
		{
			Video vid = (Video) iter.next();
			if(vid.getNumAvailableCopies() > 0)
			{
				out += vid.getFilm() + " (" + vid.getNumAvailableCopies() + ")" + "\n";
			}
		}
		//have to trim this and not rentedVideoList() to make it look nicer because this comes after
		return out.trim();	
	}	

	
	/**
	 * Parse the film name from an input line. 
	 * 
	 * @param line
	 * @return
	 */
	public static String parseFilmName(String line) 
	{ 
		String film = "";
		int i = 0;
		while( i < line.length())
		{
			if(line.charAt(i) == '(')
			{
				i = line.length();
			}
			else
			{
				film += line.charAt(i);
			}
			i++;	
		}
		return film.trim();
	}
	
	
	/**
	 * Parse the number of copies from an input line. 
	 * 
	 * @param line
	 * @return
	 */
	public static int parseNumCopies(String line) 
	{
		int copies = 0;
		int i = parseFilmName(line).length();
		while( i < line.length())
		{
			if(line.charAt(i) == '(')
			{
				String copiesString = "";
				int j = i + 1;
				if(line.charAt(j) == '-')
				{
					return 0;
				}
				while(line.charAt(j) != ')')
				{
					copiesString += line.charAt(j);
					j++;
				}
				copies = Integer.parseInt(copiesString);
				i = line.length();
			}
			i++;	
		}
		return Math.max(copies, 1);
	}
}