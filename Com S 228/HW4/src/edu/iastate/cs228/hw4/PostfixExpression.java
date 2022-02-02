package edu.iastate.cs228.hw4;

/**
 *  
 * @author Alex Huynh
 *
 */

import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Scanner; 

/**
 * 
 * This class evaluates a postfix expression using one stack.    
 *
 */

public class PostfixExpression extends Expression 
{
	private int leftOperand;            // left operand for the current evaluation step             
	private int rightOperand;           // right operand (or the only operand in the case of 
	                                    // a unary minus) for the current evaluation step	

	private PureStack<Integer> operandStack;  // stack of operands
	

	/**
	 * Constructor stores the input postfix string and initializes the operand stack.
	 * 
	 * @param st      input postfix string. 
	 * @param varTbl  hash map that stores variables from the postfix string and their values.
	 */
	public PostfixExpression (String st, HashMap<Character, Integer> varTbl)
	{
		postfixExpression = removeExtraSpaces(st);		
		varTable = new HashMap<>();
		varTable.putAll(varTbl);
		operandStack = new ArrayBasedStack<>();
	}
	
	
	/**
	 * Constructor supplies a default hash map. 
	 * 
	 * @param s
	 */
	public PostfixExpression (String s)
	{
		super(removeExtraSpaces(s));
		operandStack = new ArrayBasedStack<>();
	}

	
	/**
	 * Outputs the postfix expression according to the format in the project description.
	 */
	@Override 
	public String toString()
	{
		String out = "";
		Scanner sc = new Scanner(postfixExpression);
		
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
	 * Resets the postfix expression. 
	 * @param st
	 */
	public void resetPostfix (String st)
	{
		postfixExpression = removeExtraSpaces(st); 
		super.varTable = new HashMap<>();
	}


	/**
     * Scan the postfixExpression and carry out the following:  
     * 
     *    1. Whenever an integer is encountered, push it onto operandStack.
     *    2. Whenever a binary (unary) operator is encountered, invoke it on the two (one) elements popped from  
     *       operandStack,  and push the result back onto the stack.  
     *    3. On encountering a character that is not a digit, an operator, or a blank space, stop 
     *       the evaluation. 
     *       
     * @return value of the postfix expression 
     * @throws ExpressionFormatException with one of the messages below: 
     *  
     *           -- "Invalid character" if encountering a character that is not a digit, an operator
     *              or a whitespace (blank, tab); 
     *           --	"Too many operands" if operandStack is non-empty at the end of evaluation; 
     *           -- "Too many operators" if getOperands() throws NoSuchElementException; 
     *           -- "Divide by zero" if division or modulo is the current operation and rightOperand == 0;
     *           -- "0^0" if the current operation is "^" and leftOperand == 0 and rightOperand == 0;
     *           -- self-defined message if the error is not one of the above.
     *           
     *         UnassignedVariableException if the operand as a variable does not have a value stored
     *            in the hash map.  In this case, the exception is thrown with the message
     *            
     *           -- "Variable <name> was not assigned a value", where <name> is the name of the variable.  
	 * @throws UnassignedVariableException 
     *           
     */
	public int evaluate() throws ExpressionFormatException, UnassignedVariableException 
    {
		for(int i = 0; i < postfixExpression.length(); i++)
		{
			String token = "";
			
			int j = i;
			while(j < postfixExpression.length() && postfixExpression.charAt(j) != ' ')
			{
				token += postfixExpression.charAt(j);
				j++;
				i++;
			}
			char tokenChar = token.charAt(0);
			
			//we found an operand
			if(isInt(token))
			{
				operandStack.push(Integer.parseInt(token));
			}
			
			//we found a variable
			else if(isVariable(tokenChar))
			{
				Integer varsValue = varTable.get(tokenChar);
				if(varsValue == null)
				{ 
					throw new UnassignedVariableException("Variable " + tokenChar + " was not assigned a value");
				}
				else
				{
					operandStack.push( (int) varsValue);
				}
			}
			
			else if(isOperator(tokenChar))
			{
				try
				{
					if(operandStack.size() == 1 && tokenChar != '~')
					{
						throw new ExpressionFormatException("Missing operand");
					}
					getOperands(tokenChar);
					if( (tokenChar == '/' || tokenChar == '%') && rightOperand == 0 )
					{
						throw new ExpressionFormatException("Divide by zero");
					}
					if(tokenChar == '^' && leftOperand == 0 && rightOperand == 0)
					{
						throw new ExpressionFormatException("0^0");
					}
					operandStack.push(compute(tokenChar));
				}
				catch(NoSuchElementException e)
				{
					throw new ExpressionFormatException("Too many operators");
				}
			}

			else if(tokenChar == ' '){ }
			
			else if( !isInt(token) && !isOperator(tokenChar) && tokenChar != ' ')  
			{
				throw new ExpressionFormatException("Invalid character");
			}
		}
		int evaluation = operandStack.pop();
		
		//after the entire process, stack is non-empty, meaning too many operands
		if(operandStack.size() != 0)
		{
			throw new ExpressionFormatException("Too many operands");
		}
		return evaluation;  
    }
	

	/**
	 * For unary operator, pops the right operand from operandStack, and assign it to rightOperand. The stack must have at least
	 * one entry. Otherwise, throws NoSuchElementException.
	 * For binary operator, pops the right and left operands from operandStack, and assign them to rightOperand and leftOperand, respectively. The stack must have at least
	 * two entries. Otherwise, throws NoSuchElementException.
	 * @param op
	 * 			char operator for checking if it is binary or unary operator.
	 */
	private void getOperands(char op) throws NoSuchElementException 
	{
		// TODO 
		if(op == '~')
		{
			if(operandStack.size() >= 1)
			{
				rightOperand = operandStack.pop();
			}
			else
			{
				throw new NoSuchElementException();
			}
		}
		else
		{
			if(operandStack.size() >= 2)
			{
				rightOperand = operandStack.pop();
				leftOperand = operandStack.pop();		
			}	
			else
			{
				throw new NoSuchElementException();
			}
		}		
	}


	/**
	 * Computes "leftOperand op rightOprand" or "op rightOprand" if a unary operator. 
	 * 
	 * @param op operator that acts on leftOperand and rightOperand. 
	 * @return
	 */
	private int compute(char op)  
	{
		// TODO 
		if(op == '~')
		{
			return rightOperand * -1;
		}
		else
		{
			switch(op)
			{
				case '+':
					return leftOperand + rightOperand;
				case '-':
					return leftOperand - rightOperand;
				case '*':
					return leftOperand * rightOperand;
				case '/':
					return leftOperand / rightOperand;
				case '%':
					return leftOperand % rightOperand;
				case '^':
					return (int) Math.pow(leftOperand, rightOperand);
			}
			return 0;
		}
	}
}
