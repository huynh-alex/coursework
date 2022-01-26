import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

public class TestSuite {
    
    HashMap<Integer, Integer> f1; //frequency mapping
    float f2;
    int distinctElements;
    int maxFrequency;
    int numElements;
    int iterations;
    
    public TestSuite(int distinctElements, int maxFrequency){
        this.distinctElements = distinctElements;
        this.maxFrequency = maxFrequency;
    }

    public ArrayList<Integer> generateStream(){
        this.numElements = 0;
        this.f1 = new HashMap<>();
        this.f2 = 0;
        ArrayList<Integer> stream = new ArrayList<>();

        Random r = new Random();
        for(int element = 1; element <= distinctElements; element++){
            int frequency = r.nextInt(maxFrequency) + 1;
            this.f1.put(element, frequency);
            this.f2 += (frequency * frequency);
            for(int j = 0; j < frequency; j++){
                stream.add(element);
                this.numElements++;
            }
        }
        Collections.shuffle(stream);
        return stream;
    }

    public void testCMS(float epsilon, float delta, int iterations){      
        float totalWrong = 0;
        for(int i = 0; i < iterations; i++){
            ArrayList<Integer> stream = generateStream();
            CMS cms = new CMS(epsilon, delta, stream, 0, 0);
            
            float wrong = 0;
            for(int element = 1; element < distinctElements; element++){
                int estFreq = cms.approximateFrequency(element);
                int trueFreq = f1.get(element);
                if(estFreq > trueFreq + epsilon * numElements){
                    wrong++;
                }
            }
            totalWrong += wrong / distinctElements;
        }
        System.out.println("Average wrong proportion: " + totalWrong / iterations);
        System.out.println("Less than δ = " + delta + "? " + ((totalWrong / iterations) < delta));

    }

    public void testHH(float epsilon, float delta, float q, float r, int maxNumHH, int iterations){
    
        int totalWrong = 0;
        for(int i = 0; i < iterations; i++){
            ArrayList<Integer> stream = generateStream();
            ArrayList<Integer> hh = new ArrayList<>();
            ArrayList<Integer> freqs = new ArrayList<>();
            int hhElement = distinctElements;

            for(int j = 0; j < maxNumHH; j++){
                distinctElements++;
                int freq = (int)(numElements * 2 * q);
                numElements += freq;
                freqs.add(freq);
            }
            for(int freq : freqs){
                for(int j = 0; j < freq; j++)
                    stream.add(hhElement);
                f1.put(hhElement, freq);
                hh.add(hhElement);
                hhElement++;
            }
            //clean up q-heavy hitter set
            for(int x : hh){
                if(f1.get(x) < r * numElements){
                    hh.remove(x);
                }
            }
            CMS cms = new CMS(epsilon, delta, stream, q, r);            
            
            if(hh.size() != cms.approximateHH().size()){
                totalWrong++;
            }
        }
        System.out.println("Average wrong proportion: " + totalWrong / iterations);
        System.out.println("Less than δ = " + delta + "? " + ((totalWrong / iterations) < delta));

    }

    public void testCountSketch(float epsilon, float delta, int iterations){        
        float totalWrong = 0;
        for(int i = 0; i < iterations; i++){
            CountSketch cs = new CountSketch(epsilon, delta, generateStream());
            float wrong = 0;
            for(int element = 1; element < distinctElements; element++){
                int estFreq = cs.approximateFrequency(element);
                int trueFreq = f1.get(element);
                if(Math.abs(trueFreq - estFreq) >= epsilon * Math.sqrt(f2)){
                    wrong++;
                }
            }
            totalWrong += wrong / distinctElements;
        }
        System.out.println("Average wrong proportion: " + totalWrong / iterations);
        System.out.println("Less than δ = " + delta + "? " + ((totalWrong / iterations) < delta));
    }

    public void compareCMSCountSketch(int numHashes, int columns, int iterations){
        float cmsTotalWrong = 0;
        float csTotalWrong = 0;

        for(int i = 0; i < iterations; i++){

            ArrayList<Integer> stream = generateStream();
            CMS cms = new CMS(1, 1, stream, 0, 0);    
            CountSketch cs = new CountSketch(1, 1, stream);

            cms.setColumnsAndNumHashes(numHashes, columns);
            cms.processStream(stream);
            cs.setColumnsAndNumHashes(numHashes, columns);
            cs.processStream(stream);
            
            float cmsWrong = 0;
            float csWrong = 0;

            for(int element = 1; element < distinctElements; element++){
                float cmsEst = cms.approximateFrequency(element);
                float csEst = cs.approximateFrequency(element);
                int trueFreq = f1.get(element);
                if(cmsEst > (trueFreq + cms.getEpsilon() * distinctElements)){
                    cmsWrong++;
                }
                if(Math.abs(trueFreq - csEst) > cs.getEpsilon() * Math.sqrt(f2)){
                    csWrong++;
                }
            }
            cmsTotalWrong += cmsWrong / distinctElements;
            csTotalWrong += csWrong / distinctElements;
        }
        System.out.println("Average wrong proportion for Count Min Sketch: " + cmsTotalWrong / iterations);
        System.out.println("Average wrong proportion for Count Sketch: " + csTotalWrong / iterations);    
    }

    public void testAMS(float epsilon, float delta, int iterations){
        
        float wrong = 0;
        for(int i = 0; i < iterations; i++){
            float approxF2 = AMS.secondFreqMoment(generateStream(), epsilon, delta);
            if(Math.abs(approxF2 - f2) >= epsilon * f2){
                wrong++;
            }
        }
        System.out.println("Average wrong proportion: " + wrong / iterations);
        System.out.println("Less than δ = " + delta + "? " + ((wrong / iterations) < delta));
    }

    public void testAMSDimRed(float epsilon, float delta, int iterations){
        
        ArrayList<Vector> inputVectors = new ArrayList<>();
        
        for(int i = 0; i < iterations; i++){
            Random r = new Random();
            float[] vectorArray = new float[distinctElements];
            for(int element = 0; element < distinctElements; element++){
                int frequency = r.nextInt(maxFrequency) + 1;
                vectorArray[element] = frequency;
            }
            inputVectors.add(new Vector(vectorArray));
        }
        ArrayList<Vector> reducedVectors = AMSDimRed.reduceDim(inputVectors, epsilon, delta);
    
        float wrong = 0;
        float lengthProportion = 0;
        for(int i = 0; i < inputVectors.size(); i++){
            Vector orig = inputVectors.get(i);
            Vector reduced = reducedVectors.get(i);
            float origLengthSquared = orig.getLength() * orig.getLength();
            float reducedLengthSquared = reduced.getLength() * reduced.getLength();
            if(Math.abs(reducedLengthSquared - origLengthSquared) >= epsilon * origLengthSquared){
                wrong++;
            }
            lengthProportion += (reduced.getLength() / orig.getLength());
        }
        System.out.println("Average length preserved: " + lengthProportion / iterations);
        System.out.println("Wrong proportion: " + wrong / iterations);
        System.out.println("Less than δ = " + delta + "? " + ((wrong / iterations) < delta));
    }

    public static void main(String[] args){
        int distinctElements = 100;
        int maxFrequency = 100;
        float epsilon;
        float delta;
        int iterations;
        TestSuite ts = new TestSuite(distinctElements, maxFrequency);
        
        // epsilon = 0.02f;
        // delta = 0.02f;
        // iterations = 10;
        // System.out.println("\nCount Min Sketch benchmark");
        // System.out.format("(distinct elements = %d, max frequency = %d, epsilon = %.2f, delta = %.2f, iterations = %d)\n", distinctElements, maxFrequency, epsilon, delta, iterations);
        // ts.testCMS(epsilon, delta, iterations);

        // epsilon = 0.1f;
        // delta = 0.05f;
        // iterations = 10;
        // System.out.println("\nCount Sketch benchmark");
        // System.out.format("(distinct elements = %d, max frequency = %d, epsilon = %.2f, delta = %.2f, iterations = %d)\n", distinctElements, maxFrequency, epsilon, delta, iterations);
        // ts.testCountSketch(epsilon, delta, iterations);

        // epsilon = 0.02f;
        // delta = 0.05f;
        // float q = 0.1f;
        // float r = 0.05f;
        // iterations = 100;
        // System.out.println("\nHeavy Hitters benchmark");
        // int maxNumHH = 1;
        // System.out.format("(distinct elements = %d, max frequency = %d, epsilon = %.2f, delta = %.2f, iterations = %d, q = %.2f, r = %.2f, max # of HH = %d)\n", distinctElements, maxFrequency, epsilon, delta, iterations, q, r, maxNumHH);
        // ts.testHH(epsilon, delta, q, r, maxNumHH, iterations);
        // maxNumHH = 2;
        // System.out.format("(distinct elements = %d, max frequency = %d, epsilon = %.2f, delta = %.2f, iterations = %d, q = %.2f, r = %.2f, max # of HH = %d)\n", distinctElements, maxFrequency, epsilon, delta, iterations, q, r, maxNumHH);
        // ts.testHH(epsilon, delta, q, r, maxNumHH, iterations);
        // maxNumHH = 3;
        // System.out.format("(distinct elements = %d, max frequency = %d, epsilon = %.2f, delta = %.2f, iterations = %d, q = %.2f, r = %.2f, max # of HH = %d)\n", distinctElements, maxFrequency, epsilon, delta, iterations, q, r, maxNumHH);
        // ts.testHH(epsilon, delta, q, r, maxNumHH, iterations);
        // maxNumHH = 4;
        // System.out.format("(distinct elements = %d, max frequency = %d, epsilon = %.2f, delta = %.2f, iterations = %d, q = %.2f, r = %.2f, max # of HH = %d)\n", distinctElements, maxFrequency, epsilon, delta, iterations, q, r, maxNumHH);
        // ts.testHH(epsilon, delta, q, r, maxNumHH, iterations);
        // maxNumHH = 5;
        // System.out.format("(distinct elements = %d, max frequency = %d, epsilon = %.2f, delta = %.2f, iterations = %d, q = %.2f, r = %.2f, max # of HH = %d)\n", distinctElements, maxFrequency, epsilon, delta, iterations, q, r, maxNumHH);
        // ts.testHH(epsilon, delta, q, r, maxNumHH, iterations);

        // epsilon = 0.15f;
        // delta = 0.05f;
        // iterations = 1;
        // System.out.println("\nAMS sketch benchmark");
        // System.out.format("(distinct elements = %d, max frequency = %d, epsilon = %.2f, delta = %.2f, iterations = %d)\n", distinctElements, maxFrequency, epsilon, delta, iterations);
        // ts.testAMS(epsilon, delta, iterations);

        // epsilon = 0.15f;
        // delta = 0.05f;
        // iterations = 100;
        // System.out.println("\nAMS dimensionality reduction benchmark");
        // System.out.format("(distinct elements = %d, max frequency = %d, epsilon = %.2f, delta = %.2f, iterations = %d)\n", distinctElements, maxFrequency, epsilon, delta, iterations);
        // ts.testAMSDimRed(epsilon, delta, iterations);

        int numHashes = 3;
        int columns = 250;
        iterations = 50;
        System.out.println("\nComparing CMS and Count Sketch");
        System.out.format("(distinct elements = %d, max frequency = %d, numHashes = %d, columns = %d, iterations = %d)\n", distinctElements, maxFrequency, numHashes, columns, iterations);
        ts.compareCMSCountSketch(numHashes, columns, iterations);
        
        System.out.println();
    }
}