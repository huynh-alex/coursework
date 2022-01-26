import java.util.ArrayList;

public class FalsePositives {

    public static double calculate(int m, int n){
        return 1 / (Math.pow(2, (m / n) * Math.log(2)));
    }

    public static boolean isPrime(int n){
        if(n <= 1) return false;

        for(int i = 2; i < (int) Math.sqrt(n); i++){
            if(n % i == 0){
                return false;
            }
        }
        return true;
    }

    public static boolean isComposite(int n) {
        for (int i = 2; i < n; i++) {
            if (n % i == 0){
                return true;
            }
        }
        return false;
    }

    public static ArrayList<String> generateComposites(int amount){
        ArrayList<String> composites = new ArrayList<>();
        int i = 2;
        while(composites.size() < amount){
            if(isComposite(i)){
                composites.add(String.valueOf(i));
            }
            i++;
        }
        return composites;
    }

    public static ArrayList<String> generatePrimes(int amount){
        ArrayList<String> primes = new ArrayList<>();
        int i = 1;
        while(primes.size() < amount){
            if(isPrime(i)){
                primes.add(String.valueOf(i));
            }
            i++;
        }
        return primes;
    }

    public static void main(String[] args){
        System.out.println();
        int NUM_COMPOSITES = 100000;
        int NUM_PRIMES = 100000;

        ArrayList<String> composites = generateComposites(NUM_COMPOSITES);
        ArrayList<String> primes = generatePrimes(NUM_PRIMES);

        int[] bitsPerElementArray = {4,8,10};

        for(int i = 0; i < bitsPerElementArray.length; i++){
            int bitsPerElement = bitsPerElementArray[i];
            BloomFilterFNV fnvBF = new BloomFilterFNV(composites.size(), bitsPerElement);
            BloomFilterRan ranBF = new BloomFilterRan(composites.size(), bitsPerElement);
            MultiMultiBloomFilter mmBF = new MultiMultiBloomFilter(composites.size(), bitsPerElement);
            NaiveBloomFilter naiveBF = new NaiveBloomFilter(composites.size(), bitsPerElement);

            for(String composite : composites){
                fnvBF.add(composite);
                ranBF.add(composite);
                mmBF.add(composite);
                naiveBF.add(composite);
            }
    
            int numFalsePositivesFNV = 0;
            int numFalsePositivesRan = 0;
            int numFalsePositivesMM = 0;
            int numFalsePositivesNaive = 0;
            for(String prime : primes){
                if(fnvBF.appears(prime)) numFalsePositivesFNV++;
                if(ranBF.appears(prime)) numFalsePositivesRan++;
                if(mmBF.appears(prime)) numFalsePositivesMM++;
                if(naiveBF.appears(prime)) numFalsePositivesNaive++;
            }
            System.out.println(bitsPerElement + " bits per element:");
            System.out.println("FNV BF: " + (100 * numFalsePositivesFNV + 0.0) / NUM_PRIMES + "%");    
            System.out.println("Ran BF: " + (100 * numFalsePositivesRan + 0.0) / NUM_PRIMES + "%");    
            System.out.println("MM BF: " + (100 * numFalsePositivesMM + 0.0) / NUM_PRIMES + "%");    
            System.out.println("Naive BF: " + (100 * numFalsePositivesNaive + 0.0) / NUM_PRIMES + "%");    
            System.out.println();
        }
    }
}