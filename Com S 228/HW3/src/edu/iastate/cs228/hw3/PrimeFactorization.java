package edu.iastate.cs228.hw3;

/**
 *  
 * @author Alex Huynh
 *
 */

import java.util.ListIterator;

public class PrimeFactorization implements Iterable<PrimeFactor>
{
	private static final long OVERFLOW = -1;

	private long value; 	// the factored integer; set to OVERFLOW when the number is greater than 2^63-1, max of type long
	
	/**
	 * Reference to dummy node at the head.
	 */
	private Node head;
	  
	/**
	 * Reference to dummy node at the tail.
	 */
	private Node tail;
	
	private int size;     	// number of distinct prime factors

	// ------------
	// Constructors 
	// ------------
	
    /**
	 *  Default constructor constructs an empty list to represent the number 1.
	 *  
	 *  Combined with the add() method, it can be used to create a prime factorization.  
	 */
	public PrimeFactorization() 
	{	 
		value = 1;
		size = 0;
		head = new Node();
		tail = new Node();
		
		head.next = tail;
		tail.previous = head;
	}
	
	/** 
	 * Obtains the prime factorization of n and creates a doubly linked list to store the result.   
	 * Follows the direct search factorization algorithm in Section 1.2 of the project description. 
	 * 
	 * @param n
	 * @throws IllegalArgumentException if n < 1
	 */
	public PrimeFactorization(long n) throws IllegalArgumentException 
	{
		if(n < 1)
		{
			throw new IllegalArgumentException();
		}
		
		head = new Node();
		tail = new Node();
		head.next = tail;
		tail.previous = head;
		
		if(n == 1)
		{
			value = 1;
			this.size = 0;
			return;
		}
				
		value = n;
		
		PrimeFactorizationIterator iter = iterator();
		
		//variable for remainder
		long m = n;
		//int d = divisor
		for(int d = 2; d * d <= n; d++)
		{
			int multiplicity = 0;
			while(m % d == 0)
			{
				multiplicity++;
				m = m /d;	
			}			
			if(multiplicity != 0)
			{
				iter.add(new PrimeFactor(d, multiplicity));
			}
		}
		//add final multiplicity; 1 is not prime
		if(isPrime(m))
		{
			iter.add(new PrimeFactor( (int) m, 1) );
		}
		updateValue();
	}
	
	/**
	 * Copy constructor. It is unnecessary to verify the primality of the numbers in the list.
	 * 
	 * @param pf
	 */
	public PrimeFactorization(PrimeFactorization pf)
	{
		head = new Node();
		tail = new Node();
		
		head.next = tail;
		tail.previous = head;
	
		PrimeFactorizationIterator iterIn = pf.iterator();
		PrimeFactorizationIterator iterOut = iterator();
		while(iterIn.hasNext())
		{
			PrimeFactor primeFactor = new PrimeFactor(iterIn.cursor.pFactor.prime, iterIn.cursor.pFactor.multiplicity);
			iterOut.add(primeFactor);
			iterIn.next();
		}
		updateValue();
	}
	
	/**
	 * Constructs a factorization from an array of prime factors.  Useful when the number is 
	 * too large to be represented even as a long integer. 
	 * 
	 * @param pflist
	 */
	public PrimeFactorization (PrimeFactor[] pfList)
	{
		head = new Node();
		tail = new Node();
		
		head.next = tail;
		tail.previous = head;
				
		for(int i = 0; i < pfList.length; i++)
		{
			PrimeFactor next = pfList[i];
			//use Class add method because pfList might not be sorted
			add(next.prime, next.multiplicity);
		}
		updateValue();
	}
	
	// --------------
	// Primality Test
	// --------------
	
    /**
	 * Test if a number is a prime or not.  Check iteratively from 2 to the largest 
	 * integer not exceeding the square root of n to see if it divides n. 
	 * 
	 *@param n
	 *@return true if n is a prime 
	 * 		  false otherwise 
	 */
    public static boolean isPrime(long n) 
	{
    	//TODO --> DONE
    	if(n == 0 || n == 1) { return false; }
    	if(n == 2) { return true; }
    	for(int d = 2; d * d <= n; d+=1)
    	{
    		if(n % d == 0)
    		{
    			return false;
    		}
    	}
		return true; 
	}   
   
	// ---------------------------
	// Multiplication and Division 
	// ---------------------------
	
	/**
	 * Multiplies the integer v represented by this object with another number n.  Note that v may 
	 * be too large (in which case this.value == OVERFLOW). You can do this in one loop: Factor n and 
	 * traverse the doubly linked list simultaneously. For details refer to Section 3.1 in the 
	 * project description. Store the prime factorization of the product. Update value and size. 
	 * 
	 * @param n
	 * @throws IllegalArgumentException if n < 1
	 */
	public void multiply(long n) throws IllegalArgumentException 
	{
		if(n < 1)
		{
			throw new IllegalArgumentException();
		}
		PrimeFactorizationIterator iter = iterator();
		//Direct Search Factorization
		long m = n;
		for(int d = 2; d * d <= n; d++)
		{
			int multiplicity = 0;
			while(m % d == 0)
			{
				multiplicity++;
				m = m /d;	
			}
			//we now have a PrimeFactor of the parameter n; next, we add it
			
			if(multiplicity != 0)
			{
				boolean foundSpot = false;
				
				while(!foundSpot)
				{
					//at end of DLL and/or nothing found
					if(iter.cursor == tail)
					{
						foundSpot = true;
						link(iter.cursor.previous, new Node(d, multiplicity));
					}
					//find location
					else
					{
						PrimeFactor next = iter.cursor.pFactor;
						
						//same prime found
						if(d == next.prime)
						{
							foundSpot = true;
							iter.add(new PrimeFactor(d, multiplicity));
						}
						//larger prime found, so therefore we put it behind that PrimeFactor
						else if(d < iter.cursor.pFactor.prime )
						{
							foundSpot = true;
							link(iter.cursor.previous, new Node(d, multiplicity));
							size++;
						}
						//smaller prime found, but there may be yet another smaller prime after that
						else if(d > next.prime)
						{
							//if after that is tail or is a bigger prime, link it 
							if(iter.cursor.next == tail || d < iter.cursor.next.pFactor.prime)
							{
								foundSpot = true;
								link(iter.cursor, new Node(d, multiplicity));
								size++;
							}
							//yet another smaller prime is found, so move on
							else
							{
								iter.next();
							}
						}
					}
				}
			}
		}
		
		//last factor: it may not be the largest, so we have to iteratively find the position to add it using Class's add()
		if(m > 1)
		{
			add( (int) m, 1);
		}
		updateValue();
	}
	
	/**
	 * Multiplies the represented integer v with another number in the factorization form.  Traverse both 
	 * linked lists and store the result in this list object.  See Section 3.1 in the project description 
	 * for details of algorithm. 
	 * 
	 * @param pf 
	 */
	public void multiply(PrimeFactorization pf)
	{
		PrimeFactorizationIterator thisIter = iterator();
		PrimeFactorizationIterator paramIter = pf.iterator();
		
		while(paramIter.hasNext())
		{
			PrimeFactor toAdd = new PrimeFactor(paramIter.cursor.pFactor.prime, paramIter.cursor.pFactor.multiplicity);
			
			//if at the end, attach it there
			if(thisIter.cursor == tail)
			{
				thisIter.add(toAdd);
				paramIter.next();
			}
			//primes are same
			else if(thisIter.cursor.pFactor.prime == toAdd.prime)
			{
				thisIter.cursor.pFactor.multiplicity += toAdd.multiplicity;
				paramIter.next();
			}
			//param prime is smaller than this prime
			else if(toAdd.prime < thisIter.cursor.pFactor.prime)
			{
				thisIter.add(toAdd);
				paramIter.next();
			}
			//param prime is larger than this prime
			else if(toAdd.prime > thisIter.cursor.pFactor.prime)
			{
				//next is not a larger (it fits) or is tail
				if(thisIter.cursor.next == tail || toAdd.prime < thisIter.cursor.next.pFactor.prime)
				{
					thisIter.next();
					thisIter.add(toAdd);
					paramIter.next();
				}
				//next is still smaller, so move thisIter.cursor
				else
				{
					thisIter.next();
				}
			}
		}		
		updateValue();
	}
		
	/**
	 * Multiplies the integers represented by two PrimeFactorization objects.  
	 * 
	 * @param pf1
	 * @param pf2
	 * @return object of PrimeFactorization to represent the product 
	 */
	public static PrimeFactorization multiply(PrimeFactorization pf1, PrimeFactorization pf2)
	{
		long product = 1;
		
		for(PrimeFactor primeFactors1 : pf1)
		{
			if(primeFactors1 != null)
			{
				for(int i = 0; i < primeFactors1.multiplicity; i++)
				{
					product = Math.multiplyExact(product, primeFactors1.prime);
				}
			}
		}
		for(PrimeFactor primeFactors2 : pf2)
		{
			if(primeFactors2 != null)
			{
				for(int i = 0; i < primeFactors2.multiplicity; i++)
				{
					product = Math.multiplyExact(product, primeFactors2.prime);
				}			
			}
		}
		PrimeFactorization out = new PrimeFactorization(product);
		out.updateValue();
		
		return out;
		
	}
    
	/**
	 * Divides the represented integer v by n.  Make updates to the list, value, size if divisible.  
	 * No update otherwise. Refer to Section 3.2 in the project description for details. 
	 *  
	 * @param n
	 * @return  true if divisible 
	 *          false if not divisible 
	 * @throws IllegalArgumentException if n <= 0
	 */
	public boolean dividedBy(long n) throws IllegalArgumentException
	{
		if(n <= 0)
		{
			throw new IllegalArgumentException();
		}
		if(this.value != OVERFLOW && this.value  < n)
		{
			return false;
		}
		
		PrimeFactorization npf = new PrimeFactorization(n);
		
		dividedBy(npf);
		
		return true; 
	}
	
	/**
	 * Division where the divisor is represented in the factorization form.  Update the linked 
	 * list of this object accordingly by removing those nodes housing prime factors that disappear  
	 * after the division.  No update if this number is not divisible by pf. Algorithm details are 
	 * given in Section 3.2. 
	 * 
	 * @param pf
	 * @return	true if divisible by pf
	 * 			false otherwise
	 */
	public boolean dividedBy(PrimeFactorization pf)
	{
		//a case of nondivisibility
		if( (this.value != -1 && pf.value != -1 && this.value < pf.value) || (this.value != -1 && pf.value == -1) )
		{
			return false;
		}
		
		//dividing number by itself
		else if( pf.value == this.value && (pf.value != OVERFLOW && this.value != OVERFLOW) )
		{
			clearList();
			updateValue();
			return true;
		}
		
		PrimeFactorization copy = new PrimeFactorization(this);
		
		PrimeFactorizationIterator iterCopy = copy.iterator();
		PrimeFactorizationIterator iterPf = pf.iterator();
				
		//following 3.2 guidelines
		while(iterCopy.hasNext() && iterPf.hasNext())
		{	
			PrimeFactor copyNext = iterCopy.cursor.pFactor;
			PrimeFactor paramNext = iterPf.cursor.pFactor;
			
			if(copyNext.prime >= paramNext.prime)
			{
				if(copyNext.prime > paramNext.prime)
				{
					return false;
				}
				else if(copyNext.prime == paramNext.prime 
						&& copyNext.multiplicity < paramNext.multiplicity)
				{
					return false;
				}
				else if(copyNext.prime == paramNext.prime
							&& copyNext.multiplicity >= paramNext.multiplicity)	
				{
					copyNext.multiplicity -= paramNext.multiplicity;
					if(copyNext.multiplicity == 0)
					{
						unlink(iterCopy.cursor);
						copy.size--;
					}
					iterCopy.next();
					iterPf.next();
				}
			}
			else if( !iterCopy.hasNext() && iterPf.hasNext() )
			{
				return false;
			}
			else
			{
				iterCopy.next();
			}
		}

		this.head = copy.head;
		this.tail = copy.tail;
		this.size = copy.size;
		updateValue();
			
		return true; 
	}
	
	/**
	 * Divide the integer represented by the object pf1 by that represented by the object pf2. 
	 * Return a new object representing the quotient if divisible. Do not make changes to pf1 and 
	 * pf2. No update if the first number is not divisible by the second one. 
	 *  
	 * @param pf1
	 * @param pf2
	 * @return quotient as a new PrimeFactorization object if divisible
	 *         null otherwise 
	 */
	public static PrimeFactorization dividedBy(PrimeFactorization pf1, PrimeFactorization pf2)
	{
		if(pf1.value % pf2.value != 0)
		{
			return null;
		}
		else
		{
			pf1.dividedBy(pf2);
			return pf1;
		}
	}

	
	// -----------------------
	// Greatest Common Divisor
	// -----------------------

	/**
	  * Implements the Euclidean algorithm to compute the gcd of two natural numbers m and n. 
	  * The algorithm is described in Section 4.1 of the project description. 
	  * 
	  * @param m
	  * @param n
	  * @return gcd of m and n. 
	  * @throws IllegalArgumentException if m < 1 or n < 1
	  */
 	public static long Euclidean(long m, long n) throws IllegalArgumentException
	{
 		// TODO 
 		if(m < 1 || n < 1)
 		{
 			throw new IllegalArgumentException();
 		}
 		if(m % n == 0)
 		{
 			return n;
 		}
 		long remainder = m % n;

 		return Euclidean(n, remainder); 		
	}

	/**
	 * Computes the greatest common divisor (gcd) of the represented integer v and an input integer n.
	 * Returns the result as a PrimeFactor object.  Calls the method Euclidean() if 
	 * this.value != OVERFLOW.
	 *     
	 * It is more efficient to factorize the gcd than n, which can be much greater. 
	 *     
	 * @param n
	 * @return prime factorization of gcd
	 * @throws IllegalArgumentException if n < 1
	 */
	public PrimeFactorization gcd(long n) throws IllegalArgumentException
	{
		if(n < 1)
		{
			throw new IllegalArgumentException();
		}
		else if(this.value != OVERFLOW)
		{
			PrimeFactorization out = new PrimeFactorization(Euclidean(n, this.value));
			out.updateValue();
			return out;
		}
		else
		{
			return this.gcd(new PrimeFactorization(n));
		}
	}

	/**

	 * Computes the gcd of the values represented by this object and pf by traversing the two lists.  No 
	 * direct computation involving value and pf.value. Refer to Section 4.2 [5] in the project description 
	 * on how to proceed.  
	 * 
	 * @param  pf
	 * @return prime factorization of the gcd
	 */
	public PrimeFactorization gcd(PrimeFactorization pf)
	{
		PrimeFactorization out = new PrimeFactorization();
		PrimeFactorizationIterator outIter = out.iterator();
		
		PrimeFactorizationIterator thisIter = iterator();
		PrimeFactorizationIterator pfIter = pf.iterator();
		
		while(pfIter.hasNext())
		{
			if(!thisIter.hasNext())
			{
				out.updateValue();
				
				return out; 			
			}
			
			int thisPrime = thisIter.cursor.pFactor.prime;
			int paramPrime = pfIter.cursor.pFactor.prime;

			//same primes at cursor
			if(paramPrime == thisPrime)
			{
				PrimeFactor add = new PrimeFactor(pfIter.cursor.pFactor.prime, Math.min(pfIter.cursor.pFactor.multiplicity, 
																						thisIter.cursor.pFactor.multiplicity));
				outIter.add(add);
				thisIter.next();
				pfIter.next();
			}
			
			//prime at pf is larger than this: move thisIter's cursor
			else if(paramPrime > thisPrime)
			{
				thisIter.next();
			}
			else if(paramPrime < thisPrime)
			{
				pfIter.next();
			}
		}
		out.updateValue();
				
		return out; 
	}
	
	/**
	 * 
	 * @param pf1
	 * @param pf2
	 * @return prime factorization of the gcd of two numbers represented by pf1 and pf2
	 */
	public static PrimeFactorization gcd(PrimeFactorization pf1, PrimeFactorization pf2)
	{
		return new PrimeFactorization(pf1.gcd(pf2)); 
	}

	
	// ------------
	// List Methods
	// ------------
	
	/**
	 * Traverses the list to determine if p is a prime factor. 
	 * 
	 * Precondition: p is a prime. 
	 * 
	 * @param p  
	 * @return true  if p is a prime factor of the number v represented by this linked list
	 *         false otherwise 
	 * @throws IllegalArgumentException if p is not a prime
	 */
	public boolean containsPrimeFactor(int p) throws IllegalArgumentException
	{
		// TODO
		if(!isPrime(p))
		{
			throw new IllegalArgumentException();
		}
		
		PrimeFactorizationIterator iter = iterator();
		
		while(iter.hasNext())
		{
			PrimeFactor temp = iter.next();
			if(temp.prime == p)
			{
				return true;
			}
		}
		return false;
	}
	
	// The next two methods ought to be private but are made public for testing purpose. Keep
	// them public 
	
	/**
	 * Adds a prime factor p of multiplicity m.  Search for p in the linked list.  If p is found at 
	 * a node N, add m to N.multiplicity.  Otherwise, create a new node to store p and m. 
	 *  
	 * Precondition: p is a prime. 
	 * 
	 * @param p  prime 
	 * @param m  multiplicity
	 * @return   true  if m >= 1
	 *           false if m < 1   
	 */
    public boolean add(int p, int m) 
    {
    	if(m < 1) { return false; }
    	if(p == 1) { return true; }
    	int highestPrime = 1;
		
    	PrimeFactorizationIterator iter = iterator();


    	//get highest prime in list
    	while(iter.hasNext())
    	{
    		PrimeFactor next = iter.next();
    		if(next.prime > highestPrime)
    		{
    			highestPrime = next.prime;
    		}
    	}
    	   	
    	iter = iterator();
    	
  
    	while(iter.hasNext())
    	{    		
    		PrimeFactor next = iter.next();

	    	//if the prime already exists, add the multiplicity; 
			if(next.prime == p)
			{
				iter.pending.pFactor.multiplicity += m;
				return true;
			}
	    	//the prime is in between the list, so link it behind the next biggest prime compared to itself
			else if(next.prime > p)
			{
				link(iter.pending.previous, new Node(p, m));
				size++;
				return true;
			}
    		
        	//if the prime is larger than highest prime is list, attach after it    	
    		else if(p > highestPrime)
    		{
    			link(tail.previous, new Node(p, m));
    			size++;
    			return true; 	
    		}
    	}
    	//last node needs to be added if necessary
    	if(!iter.hasNext())
    	{
    		link(tail.previous, new Node(p, m));
    		size++;
    	}
    	return true;
    }
	    
    /**
     * Removes m from the multiplicity of a prime p on the linked list.  It starts by searching 
     * for p.  Returns false if p is not found, and true if p is found. In the latter case, let 
     * N be the node that stores p. If N.multiplicity > m, subtracts m from N.multiplicity.  
     * If N.multiplicity <= m, removes the node N.  
     * 
     * Precondition: p is a prime. 
     * 
     * @param p
     * @param m
     * @return true  when p is found. 
     *         false when p is not found. 
     * @throws IllegalArgumentException if m < 1
     */
    public boolean remove(int p, int m) throws IllegalArgumentException
    {
    	if(m < 1)
    	{
    		throw new IllegalArgumentException();
    	}
    	PrimeFactorizationIterator iter = iterator();
    	
    	while(iter.hasNext())
    	{
    		PrimeFactor temp = iter.cursor.pFactor;
    		if(temp.prime == p)
    		{
    			iter.cursor.pFactor.multiplicity -= m;
    			
    			if(iter.cursor.pFactor.multiplicity <= 0)
    			{
    				unlink(iter.cursor);
    				size--;
    			}
    			return true;
    		}
    		else
    		{
    			iter.next();
    		}
    	} 	
    	return false; 
    }

    /**
     * 
     * @return size of the list
     */
	public int size() 
	{
		return size; 
	}
	
	/**
	 * Writes out the list as a factorization in the form of a product. Represents exponentiation 
	 * by a caret.  For example, if the number is 5814, the returned string would be printed out 
	 * as "2 * 3^2 * 17 * 19". 
	 */
	@Override 
	public String toString()
	{
		String s = "";
		PrimeFactorizationIterator iterator = iterator();

		if(size == 0)
		{
			return "1";
		}
		while(iterator.hasNext())
		{
			PrimeFactor temp = iterator.next();
			
			if(temp.multiplicity == 1)
			{
				s+= temp.prime;
			}
			else
			{
				s += temp.toString();
			}
			if(iterator.hasNext())
			{
				s += " * ";
			}
		}
			return s;
	}
	
	// The next three methods are for testing, but you may use them as you like.  
	
	/**
	 * @return true if this PrimeFactorization is representing a value that is too large to be within 
	 *              long's range. e.g. 999^999. false otherwise.
	 *              
	 * @return value represented by this PrimeFactorization, or -1 if valueOverflow()
	 */
	public boolean valueOverflow() {
		return value == OVERFLOW;
	}
	public long value() {
		return value;
	}	
	public PrimeFactor[] toArray() {
		PrimeFactor[] arr = new PrimeFactor[size];
		int i = 0;
		for (PrimeFactor pf : this)
			arr[i++] = pf;
		return arr;
	}
	
	@Override
	public PrimeFactorizationIterator iterator()
	{
	    return new PrimeFactorizationIterator();
	}
	
	/**
	 * Doubly-linked node type for this class.
	 */
    private class Node 
    {
		public PrimeFactor pFactor;			// prime factor 
		public Node next;
		public Node previous;
		
		/**
		 * Default constructor for creating a dummy node.
		 */
		public Node()
		{
			pFactor = null;
			this.next = null;
			this.previous = null;
		}
	    
		/**
		 * Precondition: p is a prime
		 * 
		 * @param p	 prime number 
		 * @param m  multiplicity 
		 * @throws IllegalArgumentException if m < 1 
		 */
		public Node(int p, int m) throws IllegalArgumentException 
		{	
			if(m < 1) {	throw new IllegalArgumentException(); }
			
			if(isPrime(p))
			{
				pFactor = new PrimeFactor(p, m);
				next = null;
				previous = null;
			}
		}   
	
		/**
		 * Constructs a node over a provided PrimeFactor object. 
		 * 
		 * @param pf
		 * @throws IllegalArgumentException
		 */
		public Node(PrimeFactor pf)  
		{
			pFactor = pf;
			next = null;
			previous = null;
		}

		/**
		 * Printed out in the form: prime + "^" + multiplicity.  For instance "2^3". 
		 * Also, deal with the case pFactor == null in which a string "dummy" is 
		 * returned instead.  
		 */
		@Override
		public String toString() 
		{
			if(pFactor == null)
			{
				return "dummy";
			}
			if(pFactor.multiplicity == 1)
			{
				return pFactor.prime + "";
			}
			return pFactor.prime + "^" + pFactor.multiplicity;
		}
	}	
 
    private class PrimeFactorizationIterator implements ListIterator<PrimeFactor>
    {  	
        // Class invariants: 
        // 1) logical cursor position is always between cursor.previous and cursor
        // 2) after a call to next(), cursor.previous refers to the node just returned 
        // 3) after a call to previous() cursor refers to the node just returned 
        // 4) index is always the logical index of node pointed to by cursor

        private Node cursor = head.next;
        private Node pending = null;    // node pending for removal
        private int index = 0;      
  	  
    	// other instance variables ... 

        /**
    	 * Default constructor positions the cursor before the smallest prime factor.
    	 */
    	public PrimeFactorizationIterator()
    	{
    		// TODO 
    		this.cursor = head.next;
    		this.pending = null;
    		this.index = 0;
    	}

    	@Override
    	public boolean hasNext()
    	{
    		//TODO --> DONE
    		if(cursor == null)
    		{
    			return false;
    		}
    		else if(cursor == tail)
    		{
    			return false;
    		}
    		return true;
       	}

    	@Override
    	public boolean hasPrevious()
    	{
    		//TODO --> DONE
    		return cursor.previous != null;
    	}
 
    	@Override 
    	public PrimeFactor next() 
    	{
    		// TODO --> DONE
    		if(hasNext())
    		{
    			index++;
    			pending = cursor;
        		cursor = cursor.next;
        		return pending.pFactor;
    		}
    		return null; 
    	}

    	@Override 
    	public PrimeFactor previous() 
    	{
    		// TODO --> DONE
    		if(hasPrevious())
    		{
    			index--;
    			pending = cursor.previous;
    			cursor = cursor.previous;
    			return pending.pFactor;
    		}
    		return null; 
    	}
   
    	/**
    	 *  Removes the prime factor returned by next() or previous()
    	 *  
    	 *  @throws IllegalStateException if pending == null 
    	 */
    	@Override
    	public void remove() throws IllegalStateException
    	{    		
    		if(pending == null)
    		{
    			throw new IllegalStateException();
    		}
    		
    		//if last call was next
    		if(pending == cursor.previous)
    		{
        		unlink(pending);
    		}
    		//if last call was previous
    		if(pending == cursor)
    		{
    			cursor = cursor.next;
        		unlink(pending);
    		}
    		size--;
    	}
 
    	/**
    	 * Adds a prime factor at the cursor position (behind cursor).  The cursor is at a wrong position 
    	 * in either of the two situations below: 
    	 * 
    	 *    a) pf.prime < cursor.previous.pFactor.prime if cursor.previous != head. 
    	 *    b) pf.prime > cursor.pFactor.prime if cursor != tail. 
    	 * 
    	 * Take into account the possibility that pf.prime == cursor.pFactor.prime. 
    	 * 
    	 * Precondition: pf.prime is a prime. 
    	 * 
    	 * @param pf  
    	 * @throws IllegalArgumentException if the cursor is at a wrong position. 
    	 */
    	@Override
        public void add(PrimeFactor pf) throws IllegalArgumentException 
        {    		
			if( cursor.previous != head && pf.prime < cursor.previous.pFactor.prime )
			{
				throw new IllegalArgumentException();
			}
			if( cursor != tail && pf.prime > cursor.pFactor.prime ) 
			{
				throw new IllegalArgumentException();
			}
    		
    		if(isPrime(pf.prime))
    		{
    			if(cursor != tail && pf.prime == cursor.pFactor.prime)
    			{    			
    				cursor.pFactor.multiplicity += pf.multiplicity;
    			}
    			else
    			{
    				Node newNode = new Node(pf);
        			link(cursor.previous, newNode);
        			size++;
        			index++;
    			}
    			pending = null;
    		}   		
        }

    	@Override
		public int nextIndex() 
		{
			return index;
		}

    	@Override
		public int previousIndex() 
		{
			return index - 1;
		}

		@Deprecated
		@Override
		public void set(PrimeFactor pf) 
		{
			throw new UnsupportedOperationException(getClass().getSimpleName() + " does not support set method");
		}
        
    }
    
    // --------------
    // Helper methods 
    // --------------   
    
    /**

     * Inserts toAdd into the list after current without updating size.
     * 
     * Precondition: current != null, toAdd != null
     */
    private void link(Node current, Node toAdd)
    {
    	if(current != null && toAdd != null)
    	{
    		current.next.previous = toAdd;
    		toAdd.next = current.next;
    		current.next = toAdd;
    		toAdd.previous = current;
    	}
    }

    /**
     * Removes toRemove from the list without updating size.
     */
    private void unlink(Node toRemove)
    {
    	toRemove.previous.next = toRemove.next;
    	toRemove.next.previous = toRemove.previous; 
    }

    /**
	  * Remove all the nodes in the linked list except the two dummy nodes. 
	  * 
	  * Made public for testing purpose.  Ought to be private otherwise. 
	  */
	public void clearList()
	{
		head.next = tail;
		tail.previous = head;
		size = 0;
		value = 1;
	}	
	
	/**
	 * Multiply the prime factors (with multiplicities) out to obtain the represented integer.  
	 * Use Math.multiply(). If an exception is throw, assign OVERFLOW to the instance variable value.  
	 * Otherwise, assign the multiplication result to the variable. 
	 * 
	 */
	private void updateValue()
	{
		long result = 1;
		try {		
			for(PrimeFactor pf : this)
			{
				if(pf != null)
				{
					long product = 1;
					for(int i = 0; i < pf.multiplicity; i++)
					{
						product = Math.multiplyExact(product, pf.prime);
					}	
					result = Math.multiplyExact(result, product);
				}
			}
			value = result;
		} 
			
		catch (ArithmeticException e) 
		{
			value = OVERFLOW;
		}
		
	}
}
