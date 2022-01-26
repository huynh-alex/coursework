import java.util.BitSet;
import java.util.ArrayList;

public class Statistics {
    public static double estimateSetSize(BloomFilterFNV f){
        int numZeros = 0;
        int m = f.filterSize();
        int k = f.numHashes();
        for(int i = 0; i < f.filterSize(); i++){
            if(!f.getBit(i)) 
                numZeros++;
        }

        // numZeros = m(1 - 1/m)^{k*n}; thus, we solve for n
        double n = (Math.log((numZeros + 0.0) / m) / Math.log(1.0 - 1.0/m)) / k;
        return n;
    }

    public static double estimateIntersectSize(BloomFilterFNV f1, BloomFilterFNV f2){
        int m = f1.filterSize();
        int k = f1.numHashes();
        BitSet b = new BitSet(m);
        for(int i = 0; i < m; i++){
            if(f1.getBit(i) & f2.getBit(i))
                b.set(i);
        }
        double Z = 0;
        double Z_1 = 0;
        double Z_2 = 0;
        for(int i = 0; i < m; i++){
            if(!f1.getBit(i))
                Z_1++;
            if(!f2.getBit(i))
                Z_2++;
            if(!b.get(i))
                Z++;
        }
        double lhs = (m * (Z_1 + Z_2 - Z)) / (Z_1 * Z_2);
        lhs = Math.log(lhs);
        lhs /= Math.log(1.0 - 1.0/m);
        lhs /= (-1 * k);
        return lhs;    
    }

    public static void main(String[] args){
        int NUM_COMPOSITES = 100000;
        int NUM_PRIMES = 100000;

        ArrayList<String> composites = FalsePositives.generateComposites(NUM_COMPOSITES);
        ArrayList<String> primes = FalsePositives.generatePrimes(NUM_PRIMES);

        ArrayList<Double> bfCompErrors = new ArrayList<>();
        ArrayList<Double> bfPrimeErrors = new ArrayList<>();
        ArrayList<Double> intersectionSizes = new ArrayList<>();

        for(int i = 1; i <= 16; i++){
            int bitsPerElement = i;
            
            BloomFilterFNV bf = new BloomFilterFNV(composites.size(), bitsPerElement);
            BloomFilterFNV bf2 = new BloomFilterFNV(primes.size(), bitsPerElement);

            for(String composite : composites){
                bf.add(composite);
            }
            for(String prime : primes){
                bf2.add(prime);
            }

            System.out.println("\nBF-composites");
            System.out.println("Estimated size: " + estimateSetSize(bf));
            System.out.println("Percent error: " + (Math.abs(estimateSetSize(bf)-NUM_COMPOSITES)/NUM_COMPOSITES)*100.0 + "%");
            bfCompErrors.add(Math.abs(estimateSetSize(bf)-NUM_COMPOSITES)/NUM_COMPOSITES);

            System.out.println("\nBF-primes");
            System.out.println("Estimated size: " + estimateSetSize(bf2));
            System.out.println("Percent error: " + (Math.abs(estimateSetSize(bf2)-NUM_PRIMES)/NUM_PRIMES)*100.0 + "%");
            bfPrimeErrors.add(Math.abs(estimateSetSize(bf2)-NUM_PRIMES)/NUM_PRIMES);


            System.out.println("\nIntersection size");
            System.out.println("Estimated: " + estimateIntersectSize(bf, bf2));
            System.out.println("Actual: " + 0);
            intersectionSizes.add(estimateIntersectSize(bf, bf2));
        }
        System.out.println(bfCompErrors);
        System.out.println(bfPrimeErrors);
        System.out.println(bfCompErrors);
    }
}
