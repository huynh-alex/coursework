import java.util.BitSet;
import java.util.ArrayList;
import java.util.Random;

public class BloomFilterRan {

    BitSet filter;
    int dataSize;
    int numHashes;
    int M;
    long p;
    ArrayList<Long> a;
    ArrayList<Long> b;

    public BloomFilterRan(int setSize, int bitsPerElement){
        this.M = setSize * bitsPerElement; //M
        this.numHashes = (int) Math.ceil(Math.log(2) * bitsPerElement);
        generateP();
        generateAB();
        this.filter = new BitSet((int) this.p);
        this.dataSize = 0;
    }

    public boolean isPrime(long num){
        if(num <= 1) return false;

        for(int i = 2; i < (int) Math.sqrt(num); i++){
            if(num % i == 0){
                return false;
            }
        }
        return true;
    }

    public void generateP(){
        ArrayList<Integer> primes = new ArrayList<>();
        for(int i = this.M + 1; i <= 2*this.M; i++){
            if(isPrime(i)){
                primes.add(i);
            }
        }
        Random rand = new Random();
        this.p = primes.get(rand.nextInt(primes.size()));
    }
    
    public void generateAB(){
        this.a = new ArrayList<>();
        this.b = new ArrayList<>();
        Random rand = new Random();
        for(int i = 0; i < this.numHashes(); i++){
            //nextLong() produces signed values, so we take the absolute value
            this.a.add(Math.abs(rand.nextLong() % p));
            this.b.add(Math.abs(rand.nextLong() % p));
        }
    }

    public long[] hash(String s){
        s = s.toLowerCase();
        long[] indices = new long[numHashes];
        for(int i = 0; i < numHashes(); i++){
            long index = (this.a.get(i) * s.hashCode() + this.b.get(i)) % this.p;
            indices[i] = index;
        }
        return indices;
    }

    public void add(String s){
        long[] hashes = this.hash(s);
        for(int i = 0; i < this.numHashes(); i++){
            this.filter.set((int) hashes[i]);         
        }
        this.dataSize++;
    }   

    public boolean appears(String s){
        long[] hashes = this.hash(s);
        for(int i = 0; i < this.numHashes(); i++){  
            if(!this.filter.get((int) hashes[i])){
                return false;
            }      
        }
        return true;
    }

    public int filterSize(){
        return (int) this.p;
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
