package hw1;
/**
 * Emulates a printer that has wi-fi, printing, as well as ink and paper re-loading capabilities.
 * @author Alex Huynh
 */

public class WirelessPrinter {
	
	/**
	 * Amount of non-blank pages that one ink cartridge can print
	 */
	public static final int PAGES_PER_CARTRIDGE = 1000;
	
	/**
	 * Maximum amount of paper that the printer can hold at one time
	 */
	public static final int TRAY_CAPACITY = 500;
	
	/**
	 * The level of ink in the printer when a new cartridge is put in
	 */
	public static final double NEW_CARTRIDGE_INK_LEVEL = 1.0;
	
	/**
	 * The amount of ink left in the the printer, from 0.0 to 1.0.
	 */
	private double inkLeft;
	
	/**
	 * The amount of paper left in the printer, from 0 to 500.
	 */
	private int paperLeft;
	
	/**
	 * The power status of the printer, where true = on and false = off.
	 */
	private boolean power;
	
	/**
	 * The wireless connection status of the printer, where true = connected and false = disconnected.
	 */
	private boolean connection;
	
	/**
	 * The amount of pages the printer has printed when there is ink.
	 */
	private int pagesPrinted;
	
	/**
	 * The amount of pages the printer has printed where there is no ink.
	 */
	private int blanksPrinted;
	
	/**
	 * Constructs a wireless printer with a half full ink cartridge and no paper.
	 */
	public WirelessPrinter()
	{
		inkLeft = NEW_CARTRIDGE_INK_LEVEL / 2;
		paperLeft = 0;
		power = false;
		connection = false;
		pagesPrinted = 0;
		blanksPrinted = 0;
	}
	
	/**
	 * Constructs a wireless printer with the specified amount of ink and paper.
	 * @param ink
	 * 	a double, the amount of ink in this printer, from 0.0 to 1.0
	 * @param paper
	 * 	an int, amount of paper in this printer, from 0 to 500
	 */
	public WirelessPrinter(double ink, int paper)
	{
		inkLeft = Math.min(ink, 1.0);
		paperLeft = Math.min(paper, 500);
		power = false;
		connection = false;
		pagesPrinted = 0;
		blanksPrinted = 0;
	}
	
	/**
	 * Returns the amount of ink left in this printer.
	 * @return
	 * 	a double, the amount of ink left, which ranges from 0.0 to 1.0 
	 */
	public double getInkLevel()
	{
		//This makes sure the return value rounds to the tenths digit
		return Math.round(inkLeft * 100.00) / 100.00;
	}
	
	/**
	 * Returns the amount of paper left in this printer as a percentage.
	 * @return
	 * 	the amount of paper left, which ranges from 0(%) to 100(%)
	 */	
	public int getPaperLevel()
	{
		/* 
		 * This converts the amount of paper left in to a decimal between 0.0 and 1.0, 
		 * so multiplying by 100% gets it to percentages
		 */
		return (int) Math.round(paperLeft / 500.0 * 100);
	}

	/**
	 * Returns the exact count of paper left in this printer.
	 * @return
	 * 	an int, the amount of paper left from 0 to 500
	 */
	public int getPaperLevelExact()
	{
		return paperLeft;
	}
	
	/**
	 * Returns the cumulative amount of pages that this printer has printed when ink is present.
	 * @return
	 * 	an int, the cumulative amount of paper printed when ink is present
	 */
	public int getTotalPagesPrinted()
	{
		return pagesPrinted;
	}
	
	/**
	 * Returns the cumulative amount of pages that this printer has printed whether there is ink or not.
	 * @return
	 * 	an int, the cumulative amount of paper that this printer has printed whether there is ink or not
	 */
	public int getTotalPaperUsed()
	{
		return pagesPrinted + blanksPrinted;
	}
	
	/**
	 * Sets the amount of inkLeft back to a value of 1.0
	 */
	public void replaceCartridge()
	{
		inkLeft = NEW_CARTRIDGE_INK_LEVEL;
	}
	
	/**
	 * Adds the amount of paper loaded into the tray, where the tray capacity cannot be overfilled.
	 * @param pages
	 * 	an int, the amount of pages desired to be loaded
	 */
	public void loadPaper(int pages)
	{
		paperLeft = Math.min(paperLeft + pages, TRAY_CAPACITY);
	}

	/**
	 * Connects the printer to the wifi.
	 */
	public void connect()
	{
		connection = true;
	}
	
	/**
	 * Disconnects the printer from the wifi.
	 */
	public void disconnect()
	{
		connection = false;
	}
	
	/**
	 * Checks whether the printer is connected to the wifi or not.
	 * @return
	 * 	true if the printer is connected and false if the printer is disconnected
	 */	
	public boolean isConnected()
	{
		return connection;
	}
	
	/**
	 * Turns the printer on and connects it to the wifi.
	 */
	public void turnOn()
	{
		power = true;
		connect();
	}	
	
	/** 
	 * Turns the printer off and disconnects it from the wifi.
	 */
	public void turnOff()
	{
		power = false;
		disconnect();
	}

	/**
	 * Checks whether the printer is on or not.
	 * @return
	 * 	returns true if the printer is on and false if the printer is off
	 */
	public boolean isOn()
	{ 
		return power;
	}

	/**
	 * Prints the requested amount of pages as long as the printer is on.
	 * If the printer runs out of ink, it will continue to print blanks until the requested amount of pages is reached.
	 * If the printer runs out of paper, it will stop, even if does not print the requested amount of pages.
	 * @param requestedPages
	 * 	the amount of pages that want to be printed
	 */
	public void print(int requestedPages)
	{
		if(!isConnected())
		{
			return;
		}
		//Compare if we run out of ink first or paper first or neither by using Math.min
		double printablePages = Math.min(Math.min( getInkLevel() * PAGES_PER_CARTRIDGE, paperLeft), requestedPages);
		
		pagesPrinted += printablePages;
		paperLeft -= printablePages;
		/*
		 * The amount of blanks printed, if needed, can only be up to the amount of paper left in the printer, 
		 * which is figured out with Math.min
		 */
		blanksPrinted = blanksPrinted + Math.min(requestedPages - (int) printablePages, paperLeft);
		inkLeft -= printablePages / PAGES_PER_CARTRIDGE;
	}
	
}
