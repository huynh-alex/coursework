package edu.iastate.cs228.hw4;

/**
 *  
 * @author Alex Huynh
 *
 */

import java.io.File;
import java.io.FileNotFoundException;

/**
 *  
 * @author Alex Huynh
 *
 */

/**
 * 
 * This class evaluates input infix and postfix expressions. 
 *
 */

import java.util.HashMap;
import java.util.Scanner;

public class InfixPostfix 
{

	/**
	 * Repeatedly evaluates input infix and postfix expressions.  See the project description
	 * for the input description. It constructs a HashMap object for each expression and passes it 
	 * to the created InfixExpression or PostfixExpression object. 
	 *  
	 * @param args
	 * @throws UnassignedVariableException 
	 * @throws ExpressionFormatException 
	 **/
	public static void main(String[] args) throws ExpressionFormatException, UnassignedVariableException 
	{			
		System.out.println("Evaluation of Infix and Postfix Expressions");
		System.out.println("keys: 1 (standard input) 2 (file input) 3 (exit)");
		System.out.println("(Enter \"I\" before an infix expression and \"P\" before a postfix expression)");
		System.out.println("");
			
		int trial = 1;
		boolean running = true;
		int input = -1;
		
		Scanner sc = new Scanner(System.in);
		
		while(running)
		{
			System.out.print("Trial " + trial + ": ");
			input = sc.nextInt();
			HashMap<Character, Integer> hm = new HashMap<>();
			
			if(input == 1)
			{
				System.out.print("Expression: ");
				sc.nextLine();
				String stringIn = sc.nextLine();
				
				stringIn = stringIn.trim();
				if(stringIn.charAt(0) == 'P')
				{
					PostfixExpression pfe = new PostfixExpression(stringIn.substring(2));
					
					System.out.println("Postfix form: " + pfe);		

					instantiateHashMap(pfe.postfixExpression, hm);
					
					assignHashMapUser(hm);
				
					PostfixExpression newPfe = new PostfixExpression(pfe.toString(), hm);
					
					int evaluation = newPfe.evaluate();
					
					System.out.println("Expression value: " + evaluation);
				}			
				else if(stringIn.charAt(0) == 'I')
				{
					InfixExpression ife = new InfixExpression(stringIn.substring(2));
					
					System.out.println("Infix form: " + ife.toString());			

					System.out.println("Posftfix form: " + ife.postfixString());
					
					//call this first to throw any exceptions
					ife.postfix();
					
					instantiateHashMap(ife.postfixExpression, hm);
					
					assignHashMapUser(hm);
					
					InfixExpression newIfe = new InfixExpression(stringIn.substring(2), hm);
									
					System.out.println("Expression value: " + newIfe.evaluate());				
				}	
			}			
			if(input == 2)
			{
				System.out.println("Input from a file");
				System.out.print("Enter file name: ");
				
				File file = new File(sc.next());

				try 
				{
					Scanner fileScanner = new Scanner(file);
					while(fileScanner.hasNextLine())
					{
						//need new HashMap every time because we need to reset variable values
						hm = new HashMap<>();
						
						String line = fileScanner.nextLine();						
						
						if(line.length() == 0)
						{
							//do nothing for blank lines
						}
						else if(line.charAt(0) == 'P')
						{
							System.out.println("");

							PostfixExpression pfe = new PostfixExpression(line.substring(2));
							System.out.println("Postfix form: " + pfe);
							
							instantiateHashMap(pfe.postfixExpression, hm);

							//get value of variables											
							if(hm.size() != 0)
							{
								System.out.println("where");
								
								for(int i = 0; i < hm.size(); i++)
								{
									try
									{
										String variable = fileScanner.nextLine();		
										assignHashMapFile(variable, hm);
									}
									//if there are less variables than hm.size(), we must throw Unassigned Variable Exception
									catch(Exception e)
									{
										for (Character name : hm.keySet()) 
										{
											if(hm.get(name) == null)
											{
												throw new UnassignedVariableException("Variable " + name + " was not assigned a value");
											}
										}
									}
								}
							}
							
							PostfixExpression newPfe = new PostfixExpression(pfe.toString(), hm);
							System.out.println("Expression value: " + newPfe.evaluate());
						}
						else if(line.charAt(0) == 'I')
						{
							System.out.println("");

							InfixExpression ife = new InfixExpression(line.substring(2));
							System.out.println("Infix form: " + ife);
							System.out.println("Posftfix form: " + ife.postfixString());
																					
							instantiateHashMap(ife.postfixExpression, hm);
										
							if(hm.size() != 0)
							{
								System.out.println("where");
								
								for(int i = 0; i < hm.size(); i++)
								{
									try
									{
										String variable = fileScanner.nextLine();		
										assignHashMapFile(variable, hm);
									}
									catch(Exception e)
									{
										for (Character name : hm.keySet()) 
										{
											if(hm.get(name) == null)
											{
												throw new UnassignedVariableException("Variable " + name + " was not assigned a value");
											}
										}
									}
								}
							}
							InfixExpression newIfe = new InfixExpression(line.substring(2), hm);		
							System.out.println("Expression value: " + newIfe.evaluate());	
						}
					}
					fileScanner.close();
				} 
				catch (FileNotFoundException e) 
				{
					System.out.println("File not found");
				}				
			}
			if(input == 3)
			{
				running = false;
			}
			System.out.println("");
			trial++;			
		}
		//can ONLY close here or else Eclipse complains when reading input for helper methods / toString() / etc.
		sc.close();
	}	

	// -------------------------------------------------
	// 					Helper methods
	// -------------------------------------------------
	
	/**
	 *  Set up basic hashmap with just keys
	 * @param str - We want to see what variables are in this String
	 * @param hm - We want to put the variables in the passed HashMap as keys
	 */
	private static void instantiateHashMap(String str, HashMap<Character, Integer> hm)
	{		
		for(int i = 0; i < str.length(); i++)
		{
			if(str.charAt(i) >= 97 && str.charAt(i) <= 122)
			{
				hm.put(str.charAt(i), null);
			}
		}	
	}
	
	/**
	 *  Assign hashmap keys with values to complete key-value pair
	 * @param hm - We want to update the values of the keys in the HashMap
	 */
	private static void assignHashMapUser(HashMap<Character, Integer> hm)
	{
		
		if(hm.size() != 0)
		{
			Scanner sc = new Scanner(System.in);

			System.out.println("where");
			for (Character name : hm.keySet()) 	
			{
				System.out.print(name + " = ");
				hm.put(name, sc.nextInt());
			}
		}
	}
	
	/**
	 *  Analyze the string to see which variable and value it has, followed by putting it into the HashMap
	 * @param str - The line in the file with the variable name and value
	 * @param hm - The Hashmap where we want to input the variable name and value
	 */
	private static void assignHashMapFile(String str, HashMap<Character, Integer> hm)
	{
		char variableChar = 0;
		int variableValue = 0;
		String valueAsString = "";
					
		//now need to cut spaces off variable
		int j = 0;
			
		while(j < str.length())
		{
			//get the variable's character (e.g. x)
			if(!(str.charAt(j) == ' ' || str.charAt(j) == '\t') && variableChar == 0)
			{
				variableChar = str.charAt(j);
			}
			//get the variable's value
			else if(!(str.charAt(j) == '=' || str.charAt(j) == ' ' || str.charAt(j) == '\t') && variableChar != 0)
			{
				int k = j;
				//this loop ensures we can obtain values longer than .length() of 1
				while(k < str.length() && str.charAt(k) != ' ')
				{
					valueAsString += str.charAt(k);
					k++;
					j++;
				}
				variableValue = Integer.parseInt(valueAsString);
				System.out.println(variableChar + " = " + variableValue);
				hm.put(variableChar, variableValue);
			}
			j++;
		}
	}
}
