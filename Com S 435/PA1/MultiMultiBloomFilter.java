import java.util.BitSet;
import java.util.ArrayList;
import java.util.Random;


public class MultiMultiBloomFilter{

    ArrayList<BitSet> filters;
    int setSize; //N
    int numHashes;
    int dataSize;
    ArrayList<Long> p;
    ArrayList<Long> a;
    ArrayList<Long> b;


    public MultiMultiBloomFilter(int setSize, int bitsPerElement){
        this.setSize = setSize;
        this.numHashes = bitsPerElement;
        generateP();
        generateAB();

        this.filters = new ArrayList<>();        
        for(int i = 0; i < numHashes; i++){
            filters.add(new BitSet(p.get(i).intValue()));
        }
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
        this.p = new ArrayList<>();
        ArrayList<Long> primes = new ArrayList<>();
        for(long i = this.setSize + 1; i <= 2 * this.setSize; i++){
            if(isPrime(i)){
                primes.add(i);
            }
        }
        Random rand = new Random();
        for(int i = 0; i < this.numHashes(); i++){
            this.p.add(primes.get(rand.nextInt(primes.size())));
        }
    }
    
    public void generateAB(){
        this.a = new ArrayList<>();
        this.b = new ArrayList<>();
        Random rand = new Random();
        for(int i = 0; i < this.numHashes(); i++){
            //nextLong() produces signed values, so we take the absolute value
            this.a.add(Math.abs(rand.nextLong() % p.get(i)));
            this.b.add(Math.abs(rand.nextLong() % p.get(i)));
        }
    }
    
    public void add(String s){
        long[] hashes = this.hash(s);
        for(int i = 0; i < numHashes(); i++){
            int index = (int) hashes[i] % this.filters.get(i).size();
            this.filters.get(i).set(index);         
        }
        this.dataSize++;
    }

    public boolean appears(String s){
        long[] hashes = this.hash(s);
        for(int i = 0; i < this.numHashes(); i++){
            int box = i;
            int index = (int) hashes[i] % this.filters.get(i).size();
            if(!this.getBit(box, index)){
                return false;
            }
        }
        return true;
    }

    private long[] hash(String s){
        s = s.toLowerCase();
        long[] indices = new long[this.numHashes()];
        for(int i = 0; i < this.numHashes(); i++){
            long index = (this.a.get(i) * s.hashCode() + this.b.get(i)) % this.p.get(i);
            indices[i] = index;
        }
        return indices;     
    }

    public int filterSize(){
        int totalSize = 0;
        for(int i = 0; i < numHashes; i++){
            totalSize += this.filters.get(i).size();
        }
        return totalSize;
    }

    public int dataSize(){
        return this.dataSize;
    }

    public int numHashes(){
        return this.numHashes;    
    }

    public boolean getBit(int box, int index){
        return this.filters.get(box).get(index);
    }
}