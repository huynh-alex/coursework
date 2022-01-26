import java.util.BitSet;
import java.util.ArrayList;
import java.util.Random;

public class NaiveBloomFilter {

    BitSet filter;
    int dataSize;
    int numHashes;
    int filterSize;
    long p;
    long a;
    long b;

    public NaiveBloomFilter(int setSize, int bitsPerElement){
        this.filter = new BitSet(setSize * bitsPerElement);
        this.dataSize = 0;
        this.filterSize = setSize * bitsPerElement; //M
        this.numHashes = 1;
        generateP();
        generateAB(p);
    }

    public boolean isPrime(long M){
        if(M <= 1) return false;

        for(int i = 2; i < (int) Math.sqrt(M); i++){
            if(M % i == 0){
                return false;
            }
        }
        return true;
    }

    public void generateP(){
        ArrayList<Long> primes = new ArrayList<>();
        for(long i = this.filterSize() + 1; i <= 2*this.filterSize(); i++){
            if(isPrime(i)){
                primes.add(i);
            }
        }
        Random rand = new Random();
        this.p = primes.get(rand.nextInt(primes.size()));
    }
    
    public void generateAB(long p){
        Random rand = new Random();
        //nextLong() produces signed values, so we take the absolute value
        this.a = Math.abs(rand.nextLong() % this.p);
        this.b = Math.abs(rand.nextLong() % this.p);
    }

    public long[] hash(String s){
        s = s.toLowerCase();
        long[] indices = new long[numHashes];
        for(int i = 0; i < this.numHashes(); i++){
            long index = (this.a * s.hashCode() + this.b) % this.p;
            indices[i] = index;
        }
        return indices;
    }

    public void add(String s){
        long[] hash_values = this.hash(s);
        for(int i = 0; i < this.numHashes(); i++){
            int index = (int) hash_values[i] % this.filterSize(); 
            this.filter.set(index);         
        }
        this.dataSize++;
    }   

    public boolean appears(String s){
        long[] hash_values = this.hash(s);
        for(int i = 0; i < this.numHashes(); i++){  
            int index = (int) hash_values[i] % this.filterSize(); 
            if(!this.filter.get(index)){
                return false;
            }      
        }
        return true;
    }

    public int filterSize(){
        return this.filterSize;
    }

    public int dataSize(){
        return this.dataSize;
    }

    public int numHashes(){
        return this.numHashes;    
    }

    public boolean getBit(int j){
        return this.filter.get(j);
    }
}
