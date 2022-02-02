package mini2;

public class Mini2 {

	public static String everyNth(String s, int n) {
		if (s.length() < n) {
			return "";
		} else {
			return s.charAt(n - 1) + everyNth(s.substring(n), n);
		}
	}

	public static String noNth(String s, int n) {
		if (s.length() < n) {
			return s;
		} else {
			return s.substring(0, n - 1) + noNth(s.substring(n), n);
		}
	}

	public static String unique(String s) {
		if (s.length() == 0) {
			return "";
		}
		if (s.charAt(0) != s.charAt(1)) {
			char val = s.charAt(0);

			String next = s.substring(1);

			if (s.length() == 2) {
				return s;
			}

			return val + unique(next);
		} else {
			if (s.length() == 2) {
				return s.charAt(0) + "";
			}
			String next = s.substring(1);
			return unique(next);
		}
	}

	public static int toInt(String s) {
		if (s.length() == 1) {
			return Character.getNumericValue(s.charAt(0));
		} else if (s.charAt(0) == '-') {
			return -1 * toInt(s.substring(1));
		} else {
			return (Character.getNumericValue(s.charAt(0)) * (int) pow(10, s.length() - 1)) + toInt(s.substring(1));
		}
	}

	public static boolean isMatched(String s) {
		if (s.length() == 0) {
			return true;
		}
		if (s.length() == 1) {
			return false;
		}

		char start = s.charAt(0);
		char end = ' ';

		if (s.charAt(0) == '{') {
			end = '}';
		}
		if (s.charAt(0) == '[') {
			end = ']';
		}
		if (s.charAt(0) == '(') {
			end = ')';
		}
		if (s.charAt(0) == '<') {
			end = '>';
		}

		int previousLargestEnd = -1;
		int currentLargestEnd = -1;

		for (int i = s.length() - 1; i >= 1; i--) {
			if (s.charAt(i) == end) {
				if (i > previousLargestEnd) {
					previousLargestEnd = currentLargestEnd;
				}
				currentLargestEnd = i;
			}

			if (s.charAt(i) == start) {
				currentLargestEnd = previousLargestEnd;
			}
		}
		if (currentLargestEnd == -1) {
			return false;
		}

		if (currentLargestEnd + 1 < s.length()) {
			return isMatched(s.substring(1, currentLargestEnd))
					&& isMatched(s.substring(currentLargestEnd + 1, s.length()));
		} else {
			return isMatched(s.substring(1, currentLargestEnd));
		}
	}

	public static double pow(double base, int exponent) {
		if (exponent == 0) {
			return 1;
		}
		return base * pow(base, exponent - 1);
	}

}