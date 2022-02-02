package mini1;

public class LoopsAndArrays {

	public static String arrayToString(int[] array)
	{
		String s = "";
		for(int i = array.length - 1; i >= 0; i--)
		{
			s += array[i];
		}
		return s;
	}
	
	public static int[] collatz(int start, int numIterations)
	{
		
		//numIterations+1 because 0th index holds original
		int a[] = new int[numIterations+1];
		a[0] = start;
		
		for(int i = 1; i <= numIterations; i++)
		{
			if(start % 2 == 0)
			{
				start /= 2;
			}
			else
			{
				start = start * 3 + 1;
			}
			a[i] = start;
		}
		return  a;
	}
	
	public static int countMatches(String s, String t)
	{
		int maxLength = Math.min(s.length(), t.length());
		int matches = 0;
		
		for(int i = 0; i < maxLength; i++)
		{
			if(s.charAt(i) == t.charAt(i))
			{
				matches++;
			}
		}
		return matches;
	}
	
	public static int countSubstringsWithOverlap(String t, String s)
	{
		int subStringLength = t.length();
		
		String checkString = "";
		
		int count = 0;
		
		int maxIndex = s.length() - t.length() + 1;
		
		for(int i = 0; i < maxIndex; i++)
		{
			for(int j = 0; j < subStringLength; j++)
			{
				checkString += s.charAt(j + i);
			}
			if(t.equals(checkString))
			{
				count++;
			}
			checkString = "";
		}
		return count;
	}
	
	public static int[] interleaveArray(int[] a, int[] b)
	{
		int smallest = Math.min(a.length, b.length);
		
		int c[] = null;
		
		int origIndex = 0;
		
		int i;
		
		if(a.length > b.length)
		{
			c = new int[smallest * 2 + a.length - b.length];
			
			for(i = 0; i < c.length - (a.length - b.length); i+=2)
			{
				c[i] = a[origIndex];
				c[i+1] = b[origIndex];
				origIndex++;
			}
			
			for(int j = 0; j < a.length - b.length; j++)
			{
				c[i + j] = a[origIndex];
				origIndex++;
			}
		}
	
		else
		{
			c = new int[smallest * 2 + b.length - a.length];
			
			for(i = 0; i < c.length - (b.length - a.length); i+=2)
			{
				c[i] = a[origIndex];
				c[i+1] = b[origIndex];
				origIndex++;
			}
			
			for(int j = 0; j < b.length - a.length; j++)
			{
				c[i + j] = b[origIndex];
				origIndex++;
			}
		}
		return c;
	}
	
	public static boolean isArithmetic(int[] array)
	{
		for(int i = 0; i < array.length - 2; i++)
		{
			if(array[i + 1] - array[i] != array[i+2] - array[i+1])
			{
				return false;
			}
		}
		return true;
	}
	
	public static boolean isAscending(int[] a)
	{
		for(int i = 0; i < a.length - 1; i++)
		{
			if(a[i+1] - a[i] <= 0)
			{
				return false;
			}
		}
		return true;
	}
	
	public static int numFirstChar(String s)
	{
		
		if(s.length() == 0)
		{
			return 0;
		}
		
		s.toLowerCase();
		
		char firstChar = s.charAt(0);
		
		int count = 1;
		
		for(int i = 1; i < s.length(); i++)
		{
			if(firstChar == s.charAt(i))
			{
				count++;
			}
		}
		return count;
	}
	
}
