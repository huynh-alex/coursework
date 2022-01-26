import java.util.ArrayList;
import java.util.Random;

public class AMSDimRed {

    static long p;
    static ArrayList<Long> a;
    static ArrayList<Long> b;

    public static ArrayList<Vector> reduceDim(ArrayList<Vector> inputVectors, float epsilon, float delta){
        ArrayList<Vector> reduced = new ArrayList<>();
        int numHashes = (int) ((15/Math.pow(epsilon, 2)) * 24 * Math.log(2 / delta));
        p = generateP();
        a = generateRands(numHashes, p);
        b = generateRands(numHashes, p);
        
        for(Vector v : inputVectors){
            float[] sketch = new float[numHashes];
            for(int i = 0; i < numHashes; i++){
                    sketch[i] = 0;
            }
            for(int streamValue = 0; streamValue < v.getDim(); streamValue++){
                for(int hashIndex = 0; hashIndex < numHashes; hashIndex++){
                    sketch[hashIndex] += randHash(String.valueOf(streamValue), hashIndex) * v.getIthCoor(streamValue);
                }
            }
            for(int hashIndex = 0; hashIndex < numHashes; hashIndex++){
                sketch[hashIndex] /= Math.sqrt(numHashes);
            }
            reduced.add(new Vector(sketch));
        }
        return reduced;
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
    
    public static ArrayList<Long> generateRands(int numHashes, long p){
        ArrayList<Long> rands = new ArrayList<>();
        Random rand = new Random();
        for(int hashIndex = 0; hashIndex < numHashes; hashIndex++){
            rands.add(Math.abs(rand.nextLong() % p));
        }
        return rands;
    }
    
    public static long randHash(String s, int hashIndex){
        return ((a.get(hashIndex) * s.hashCode() + b.get(hashIndex)) % p) % 2 == 0 ? 1 : -1;
    }

    public static void main(String[] args){
        float epsilon = 0.05f;
        float delta = 0.05f;

        float[] s = new float[500];
        for(int i = 0; i < s.length; i++){
                s[i] = i;
        }
        // for(int i = 100; i < 200; i++){
        //     s[i] = 50;
        // }

        Vector v = new Vector(s);
        System.out.println(v.getLength() * v.getLength());
        
        ArrayList<Vector> inputVectors = new ArrayList<>();
        inputVectors.add(v);
        ArrayList<Vector> reduced = reduceDim(inputVectors, epsilon, delta);
        System.out.println(reduced.get(0).getLength() * reduced.get(0).getLength());
    }
}
