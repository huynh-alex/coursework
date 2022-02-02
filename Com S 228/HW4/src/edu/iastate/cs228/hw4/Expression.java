package edu.iastate.cs228.hw4;

/**
 * 
 * @author Alex Huynh
 * 
 */

import java.util.HashMap;

public abstract class Expression 
{
	protected String postfixExpression; 		
	protected HashMap<Character, Integer> varTable; // hash map to store variables in 

	
	protected Expression()
	{
		// no implementation needed 
		// removable when you are done
	}
	
	/**
	 * Initialization with a provided hash map. 
	 * 
	 * @param varTbl
	 */
	protected Expression(String st, HashMap<Character, Integer> varTbl)
	{
		postfixExpression = removeExtraSpaces(st);		
		varTable = new HashMap<>();
		varTable.putAll(varTbl);
	}
	
	
	/**
	 * Initialization with a default hash map.
	 * 
	 * @param st
	 */
	protected Expression(String st) 
	{
		postfixExpression = removeExtraSpaces(st);
		varTable = new HashMap<>();
	}

	
	/**
	 * Setter for instance variable varTable.
	 * @param varTbl
	 */
	public void setVarTable(HashMap<Character, Integer> varTbl) 
	{
		varTable = new HashMap<>();
		varTable.putAll(varTbl);
	}
	
	
	/**
	 * Evaluates the infix or postfix expression. 
	 * 
	 * @return value of the expression 
	 * @throws ExpressionFormatException, UnassignedVariableException
	 */
	public abstract int evaluate() throws ExpressionFormatException, UnassignedVariableException;  

	
	
	// --------------------------------------------------------
	// Helper methods for InfixExpression and PostfixExpression 
	// --------------------------------------------------------

	/** 
	 * Checks if a string represents an integer.  You may call the static method 
	 * Integer.parseInt(). 
	 * 
	 * @param s
	 * @return
	 */
	protected static boolean isInt(String s) 
	{
		try 
		{
			Integer.parseInt(s);
			return true;
		}
		catch(NumberFormatException e)
		{
			return false;
		}
	}

	
	/**
	 * Checks if a char represents an operator, i.e., one of '~', '+', '-', '*', '/', '%', '^', '(', ')'. 
	 * 
	 * @param c
	 * @return
	 */
	protected static boolean isOperator(char c) 
	{
		if(c == '~' || c == '+' || c == '-' || c == '*' || c == '/' || c == '%' || c == '^' || c == '(' || c == ')')
		{
			return true;
		}
		return false; 
	}

	
	/** 
	 * Checks if a char is a variable, i.e., a lower case English letter. 
	 * 
	 * @param c
	 * @return
	 */
	protected static boolean isVariable(char c) 
	{
		if(c >= 97 && c <= 122)
		{
			return true;
		}
		return false;
	}
	
	
	/**
	 * Removes extra blank spaces in a string. 
	 * @param s
	 * @return
	 */
	protected static String removeExtraSpaces(String s) 
	{
		String out = "";
		for(int i = 0; i < s.length(); i++)
		{
			//if not space or tab, add whatever we're looking at
			if(!(s.charAt(i) == ' ' || s.charAt(i) == '\t'))
			{
				out += s.charAt(i);
				
				//cannot extend out of bounds
				if( (i < s.length() - 2) )
				{ 
					if((s.charAt(i+1) == ' ' || s.charAt(i+1) == '\t'))
					{
						out += " ";
						i++;
					}
				}
			}
		}
		return out.trim(); 
	}
}
