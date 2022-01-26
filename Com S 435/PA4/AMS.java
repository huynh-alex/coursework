import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class AMS {

    static long p;
    static ArrayList<ArrayList<Long>> a;
    static ArrayList<ArrayList<Long>> b;

    public static float secondFreqMoment(ArrayList<Integer> s, float epsilon, float delta){
        
        int numHashes = (int) ((15 / Math.pow(epsilon, 2))); //k
        int copiesOfB = (int) (24 * (Math.log(2 / delta))); //median of these values
        
        p = generateP();
        a = generateRands(numHashes, copiesOfB, p);
        b = generateRands(numHashes, copiesOfB, p);
        return processStream(s, numHashes, copiesOfB);
    }

    public static float processStream(ArrayList<Integer> s, int numHashes, int copiesOfB){

        float[][] sketch = new float[copiesOfB][numHashes];
        for(int bIndex = 0; bIndex < copiesOfB; bIndex++){
            for(int hashIndex = 0; hashIndex < numHashes; hashIndex++){
                sketch[bIndex][hashIndex] = 0;
            }
        }
        for(Integer i : s){
            for(int bIndex = 0; bIndex < copiesOfB; bIndex++){
                for(int hashIndex = 0; hashIndex < numHashes; hashIndex++){
                    sketch[bIndex][hashIndex] += randHash(i.toString(), bIndex, hashIndex);
                }
            }
        }
        for(int bIndex = 0; bIndex < copiesOfB; bIndex++){
            for(int hashIndex = 0; hashIndex < numHashes; hashIndex++){
                sketch[bIndex][hashIndex] = (sketch[bIndex][hashIndex] * sketch[bIndex][hashIndex]);
            }
        }

        float bValues[] = new float[copiesOfB];
        for(int i = 0 ; i < copiesOfB; i++){
            bValues[i] = mean(sketch[i]);
        }
        Arrays.sort(bValues);
        return median(bValues);
    }

    public static float mean(float[] a){
        float sum = 0;
        for(float x : a){
            sum += x;
        }
        return sum / a.length;
    }
    public static float median(float[] a){
        int len = a.length;
        if(len % 2 == 0){
            return (float) (a[len/2 - 1] + a[len/2]) / 2;
        }
        else{
            return (float) a[(int) Math.floor(len/2)];
        }
    }

    public static boolean isPrime(long num){
        if(num <= 1) return false;

        for(int i = 2; i < (int) Math.sqrt(num); i++){
            if(num % i == 0){
                return false;
            }
        }
        return true;
    }

    public static long generateP(){
        int M = 100001;
        ArrayList<Integer> primes = new ArrayList<>();
        for(int i = M; i <= 2 * M; i+=2){
            if(isPrime(i)){
                primes.add(i);
            }
        }
        Random rand = new Random();
        return primes.get(rand.nextInt(primes.size()));
    }
    
    public static ArrayList<ArrayList<Long>> generateRands(int numHashes, int copiesOfB, long p){
        ArrayList<ArrayList<Long>> rands = new ArrayList<>();
        Random rand = new Random();
        
        for(int bIndex = 0; bIndex < copiesOfB; bIndex++){
            ArrayList<Long> bthRand = new ArrayList<>();
            for(int hashIndex = 0; hashIndex < numHashes; hashIndex++){
                bthRand.add(Math.abs(rand.nextLong() % p));
            }
            rands.add(bthRand);
        }
        return rands;
    }
    
    public static long randHash(String s, int bIndex, int hashIndex){
        return ((a.get(bIndex).get(hashIndex) * s.hashCode() + b.get(bIndex).get(hashIndex)) % p) % 2 == 0 ? 1 : -1;
    }

    public static void main(String[] args){

        float epsilon = 0.05f;
        float delta = 0.05f;

        ArrayList<Integer> s = new ArrayList<>();
        for(int i = 0; i < 100; i++){
            s.add(123);
        }
        for(int i = 0; i < 100; i++){
            s.add(321);
        }
        for(int i = 0; i < 100; i++){
            s.add(500);
        }
        
        float sfm = AMS.secondFreqMoment(s, epsilon, delta);
        System.out.println(sfm);
    }
}
