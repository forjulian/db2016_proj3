
public class ProjectEuler {
	private static double sqrt(double a) {
		return Math.sqrt(a);
	}
	
	public static void countLetters() {
		/* Storing the number of letters */
		int [] ones = {0, 3, 3, 5, 4, 4, 3, 5, 5, 4};	// one, two, ..., nine
		int [] teens = {3, 6, 6, 8, 8, 7, 7, 9, 8, 8};	// ten, eleven, ..., nineteen
		int [] tens = {0, 0, 6, 6, 5, 5, 5, 7, 6, 6};	// , , twenty, thirty, ..., ninety
		int hundred = 7;
		int thousand = 8;
		int and = 3;
		
		/* Storing counts */
		int [] countOnes = new int [10];
		int [] countTeens = new int [10];
		int [] countTens = new int [10];
		int countHundreds = 0;
		int countThousands = 0;
		int countAnds = 0;
		
		int sum = 0;	// Result
		
		for(int i=1; i<=1000; i++) {
			int thou = i / 1000;			// digit of thousands
			int hund = (i % 1000) / 100;	// digit of hundreds
			int ten = (i % 100) / 10;		// digit of tens
			int one = i % 10;				// digit of ones
			
			if(thou > 0) {				// when larger than one thousand
				countThousands++;
				countOnes[thou]++;
			}
			
			if(hund > 0) {				// when larger than one hundred
				countHundreds++;
				countOnes[hund]++;
				if (!(ten == 0 && one == 0)) countAnds++;
			}
						
			if(ten == 1) countTeens[one]++;	// handling teens (ten to nineteen)
			else {							// when larger than twenty
				countTens[ten]++;
				countOnes[one]++;
			}
		}
		
		for(int i=0; i<10; i++) {
			sum += ones[i] * countOnes[i];
			sum += teens[i] * countTeens[i];
			sum += tens[i] * countTens[i];
		}
		sum += hundred * countHundreds;
		sum += thousand * countThousands;
		sum += and * countAnds;
		
		System.out.println("Sum : " + sum);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private static boolean isPentagonal(int n) {
		double index = (sqrt(24 * n + 1) + 1) / 6;
		return index == (int) index;	// check whether index is a natural number
	}
	
	private static int pentagonalize(int i) {
		return i * (3 * i - 1) / 2;
	}
	
	public static void pentagonalDiff() {
		int diff = 0;
		boolean found = false;	// Use a boolean instead of setting a large default value (e.g. 10000000) to diff
		
		int k = 2, j = 1;
		while (!found) {
			int pK = pentagonalize(k);		// P_k = k * (3 * k - 1) / 2.
			for(j=k-1; j>0; j--) {			// assure that P_k > P_j
				int pJ = pentagonalize(j);	// P_j = j * (3 * j - 1) / 2.
				if((isPentagonal(pK + pJ) && isPentagonal(pK - pJ)) && (!found || diff > (pK - pJ))) {
					diff = pK - pJ;
					found = true;
				}
			}
			k++;
		}
		
		System.out.println("D = " + diff);
	}
	
	public static void main(String[] args) {
		// countLetters();
		pentagonalDiff();
	}

}
