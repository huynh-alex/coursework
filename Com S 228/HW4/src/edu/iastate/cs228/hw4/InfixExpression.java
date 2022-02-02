package edu.iastate.cs228.hw4;

/**
 *  
 * @author Alex Huynh
 *
 */

import java.util.HashMap;
import java.util.Scanner;

/**
 * 
 * This class represents an infix expression. It implements infix to postfix conversion using 
 * one stack, and evaluates the converted postfix expression.    
 *
 */

public class InfixExpression extends Expression 
{
	private String infixExpression;   	// the infix expression to convert		
	private boolean postfixReady = false;   // postfix already generated if true
	private int rankTotal = 0;		// Keeps track of the cumulative rank of the infix expression.
	
	private PureStack<Operator> operatorStack; 	  // stack of operators 
	
	
	/**
	 * Constructor stores the input infix string, and initializes the operand stack and 
	 * the hash map.
	 * 
	 * @param st  input infix string. 
	 * @param varTbl  hash map storing all variables in the infix expression and their values. 
	 */
	public InfixExpression (String st, HashMap<Character, Integer> varTbl)
	{
		postfixExpression = "";
		infixExpression = removeExtraSpaces(st);
		varTable = new HashMap<>();
		varTable.putAll(varTbl);		
		operatorStack = new ArrayBasedStack<>();
	}
	

	/**
	 * Constructor supplies a default hash map. 
	 * 
	 * @param s
	 */
	public InfixExpression (String s)
	{
		postfixExpression = "";
		infixExpression = removeExtraSpaces(s);
		varTable = new HashMap<>();
		operatorStack = new ArrayBasedStack<>();
	}
	

	/**
	 * Outputs the infix expression according to the format in the project description.
	 */
	@Override
	public String toString()
	{
		String out = "";
		Scanner sc = new Scanner(infixExpression);
		
		while(sc.hasNext())
		{
			String token = sc.next();
			
			if(token.equals("("))
			{
				out += "(";
			}
			else if(token.equals(")"))
			{
				out = out.substring(0, out.length() - 1);
				out+= ") ";
			}
			else
			{
				out += token + " ";
			}
		}
		return out.trim(); 
	}
	
	
	/** 
	 * @return equivalent postfix expression, or  
	 * 
	 *         a null string if a call to postfix() inside the body (when postfixReady 
	 * 		   == false) throws an exception.
	 */
	public String postfixString() 
	{
		try
		{
			if(!postfixReady)
			{
				postfix();
			}
			return postfixExpression;
		}
		catch(ExpressionFormatException e)
		{
			return null;
		}
	}


	/**
	 * Resets the infix expression. 
	 * 
	 * @param st
	 */
	public void resetInfix (String st)
	{
		infixExpression = st; 
		postfixExpression = "";
		postfixReady = false;
	}


	/**
	 * Converts infix expression to an equivalent postfix string stored at postfixExpression.
	 * If postfixReady == false, the method scans the infixExpression, and does the following
	 * (for algorithm details refer to the relevant PowerPoint slides): 
	 * 
	 *     1. Skips a whitespace character.
	 *     2. Writes a scanned operand to postfixExpression. 
	 *     3. When an operator is scanned, generates an operator object.  In case the operator is 
	 *        determined to be a unary minus, store the char '~' in the generated operator object.
	 *     4. If the scanned operator has a higher input precedence than the stack precedence of 
	 *        the top operator on the operatorStack, push it onto the stack.   
	 *     5. Otherwise, first calls outputHigherOrEqual() before pushing the scanned operator 
	 *        onto the stack. No push if the scanned operator is ). 
     *     6. Keeps track of the cumulative rank of the infix expression. 
     *     
     *  During the conversion, catches errors in the infixExpression by throwing 
     *  ExpressionFormatException with one of the following messages:
     *  
     *      -- "Operator expected" if the cumulative rank goes above 1;
     *      -- "Operand expected" if the rank goes below 0; 
     *      -- "Missing '('" if scanning a �)� results in popping the stack empty with no '(';
     *      -- "Missing ')'" if a '(' is left unmatched on the stack at the end of the scan; 
     *      -- "Invalid character" if a scanned char is neither a digit nor an operator; 
     *   
     *  If an error is not one of the above types, throw the exception with a message you define.
     *      
     *  Sets postfixReady to true.  
	 */
	public void postfix() throws ExpressionFormatException
	{
		boolean previousOperator = false;
		boolean newSubexpression = false;
		boolean unaryMinus = false;
		operatorStack = new ArrayBasedStack<>();

		rankTotal = 0;
		
		if(postfixReady == false)
		{
			for(int i = 0; i < infixExpression.length();)
			{
				boolean spaceEncountered = true;
				String token = "";
				
				int j = i;
				while(j < infixExpression.length() && !(infixExpression.charAt(j) == ' ' || infixExpression.charAt(j) == '\t') )
				{
					spaceEncountered = false;
					
					//append to token, moving both indices
						token += infixExpression.charAt(j);
						j++;
						i++;
				}
								
				//we found a space; do nothing
				if(spaceEncountered)
				{
					i++;
				}
				//we found an operand as an integer
				else if(isInt(token))
				{
					postfixExpression += (Integer.parseInt(token) + " ");
					rankTotal += 1;
					previousOperator = false;
				}
				
				//we found an operand as a variable
				else if(isVariable(token.charAt(0)))
				{
					postfixExpression += token.charAt(0) + " ";
					rankTotal += 1;
					previousOperator = false;
				}
				
				//we found an operator
				else if(isOperator(token.charAt(0)))
				{
					char tokenChar = token.charAt(0);
					Operator op = null;
					
					//new sub-expression if we have an opening parentheses
					newSubexpression = tokenChar == '(';

					//check if unary minus
					if(tokenChar == '-' && (previousOperator || newSubexpression || i == 1))
					{
						op = new Operator('~');
						unaryMinus = true;
					}
					//not unary minus; carry on
					else
					{
						op = new Operator(tokenChar);
						unaryMinus = false;
					}
					
					// Three main situations:
					// 1. Stack is empty. Easy push it on and adjust rank accordingly.
					// 2. We are able to push on stack immediately
					// 3. We must pop from stack until we can push into stack.
					
					//stack is empty; push it on
					if(operatorStack.size() == 0)
					{
						operatorStack.push(op);
						//if the thing we just pushed has 0 rank, then add 0 rank. Otherwise, subtract 1 rank.
						if(op.getOp() == '(' || op.getOp() == ')' || unaryMinus)
						{
							rankTotal += 0;
						}
						else
						{
							rankTotal -= 1;
						}
					}
					// a special case where our op is ) but stack has only 1 item and it is not (
//					else if(operatorStack.size() == 1 && op.getOp() == ')' && operatorStack.peek().getOp() != '(')
//					{
//						throw new ExpressionFormatException("Missing '('");
//					}
					
					// op has higher precendence than top of stack; push op onto stack
					else if( operatorStack.peek().compareTo(op) == -1 )
					{
						//we are pushing ) onto (, so pop, which equates to dealing with both ( and ). No change in rank
						if(op.getOp() == ')' && operatorStack.peek().getOp() == '(')
						{
							operatorStack.pop();
						}
						else
						{
							operatorStack.push(op);
							if(op.getOp() == '(' || op.getOp() == ')' || unaryMinus) { rankTotal += 0; }
							else { rankTotal -= 1; }
						}				
					}
					
					// pop until we can place; don't add if our op is ) [case where input precedence is low]
					else
					{	
						outputHigherOrEqual(op);
						if(op.getOp() != ')')
						{
							operatorStack.push(op);
							if(op.getOp() != '~')
							{
								rankTotal -= 1;
							}
						}
					}					
					previousOperator = op.getOp() != ')';
				}
				
				//we found something forbidden
				else
				{
					throw new ExpressionFormatException("Invalid character");
				}
				
				if(rankTotal > 1)
				{
					throw new ExpressionFormatException("Operator expected");
				}
				else if(rankTotal < 0)
				{
					throw new ExpressionFormatException("Operand expected");
				}
			}
			
			//if ( is left on top of stack after everything, we have an exception
			while(operatorStack.size() != 0)
			{
				if(operatorStack.peek().getOp() == '(')
				{
					throw new ExpressionFormatException("Missing ')'");
				}
				if(rankTotal == 0)
				{
					throw new ExpressionFormatException("Operand expected");
				}
				postfixExpression += operatorStack.pop().getOp() + " ";
			}
		}
		postfixExpression = postfixExpression.trim();

		postfixReady = true;
	}
	
	
	/**
	 * This function first calls postfix() to convert infixExpression into postfixExpression. Then 
	 * it creates a PostfixExpression object and calls its evaluate() method (which may throw  
	 * an exception).  It also passes any exception thrown by the evaluate() method of the 
	 * PostfixExpression object upward the chain. 
	 * 
	 * @return value of the infix expression 
	 * @throws ExpressionFormatException, UnassignedVariableException
	 * @throws UnassignedVariableException 
	 */
	public int evaluate() throws ExpressionFormatException, UnassignedVariableException  
    {
    	// TODO
		postfix();
		
		PostfixExpression pfe = new PostfixExpression(postfixExpression, varTable);
		
		return pfe.evaluate();  

    }


	/**
	 * Pops the operator stack and output as long as the operator on the top of the stack has a 
	 * stack precedence greater than or equal to the input precedence of the current operator op.  
	 * Writes the popped operators to the string postfixExpression.  
	 * 
	 * If op is a ')', and the top of the stack is a '(', also pops '(' from the stack but does 
	 * not write it to postfixExpression. 
	 * 
	 * @param op  current operator
	 * @throws ExpressionFormatException 
	 */
	private void outputHigherOrEqual(Operator op) throws ExpressionFormatException
	{
		// TODO 
		while(operatorStack.size() != 0 && operatorStack.peek().compareTo(op) >= 0)
		{
			if(operatorStack.size() == 1 && op.getOp() == ')' && operatorStack.peek().getOp() != '(')
			{
				throw new ExpressionFormatException("Missing '('");
			}
			postfixExpression += operatorStack.pop().getOp() + " ";			
		}
		if( op.getOp() == ')' && operatorStack.peek().getOp() == '(')
		{
			operatorStack.pop();
		}
	}
	
	// other helper methods if needed
}
