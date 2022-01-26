import java.util.ArrayList;
import java.lang.Runtime;

public class ExtraTestCases {

    public static void main(String[] args){
        Runtime runtime = java.lang.Runtime.getRuntime();
        long memStart = runtime.totalMemory() - runtime.freeMemory();

        System.out.println();
        int NUM_COMPOSITES = 100000;

        ArrayList<String> composites = FalsePositives.generateComposites(NUM_COMPOSITES);

        int[] bitsPerElementArray = {4,8,10};

        for(int i = 0; i < bitsPerElementArray.length; i++){

            int bitsPerElement = bitsPerElementArray[i];
            System.out.println(bitsPerElement + " bits per element:");

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

    
            int numFalseNegativesFNV = 0;
            int numFalseNegativesRan = 0;
            int numFalseNegativesMM = 0;
            int numFalseNegativesNaive = 0;
            for(String composite : composites){
                if(!fnvBF.appears(composite)) numFalseNegativesFNV++;
                if(!ranBF.appears(composite)) numFalseNegativesRan++;
                if(!mmBF.appears(composite)) numFalseNegativesMM++;
                if(!naiveBF.appears(composite)) numFalseNegativesNaive++;
            }
            System.out.println("FNV BF: " + (100 * numFalseNegativesFNV + 0.0) / NUM_COMPOSITES + "%");    
            System.out.println("Ran BF: " + (100 * numFalseNegativesRan + 0.0) / NUM_COMPOSITES + "%");    
            System.out.println("MM BF: " + (100 * numFalseNegativesMM + 0.0) / NUM_COMPOSITES + "%");    
            System.out.println("Naive BF: " + (100 * numFalseNegativesNaive + 0.0) / NUM_COMPOSITES + "%");    
            System.out.println();
        }
        System.out.println(((runtime.totalMemory() - runtime.freeMemory()) - memStart)/(1024*1024) + " MB\n");
    }
}
