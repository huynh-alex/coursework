import java.util.BitSet;

public class BloomFilterFNV{

    long FNV_INIT = Long.parseUnsignedLong("14695981039346656037");
    long FNV_PRIME = 109951168211L;

    BitSet filter;
    int dataSize;
    int numHashes;
    int filterSize;

    public BloomFilterFNV(int setSize, int bitsPerElement){
        this.filterSize = setSize * bitsPerElement; //M
        this.filter = new BitSet(this.filterSize);
        this.dataSize = 0;
        this.numHashes = (int) Math.ceil(Math.log(2) * bitsPerElement);
    }

    public void add(String s){
        for(int i = 0; i < this.numHashes(); i++){
            for(int j = 0; j < i; j++){
                s += "+";
            }
            s += i;
            long hash = hash(s);
            int index = (int) Math.floorMod(hash, this.filterSize());
            this.filter.set(index);
        }
        this.dataSize++;
    }

    public boolean appears(String s){
        for(int i = 0; i < this.numHashes(); i++){
            for(int j = 0; j < i; j++){
                s += "+";
            }
            s += i;
            long hash = this.hash(s);
            int index = (int) Math.floorMod(hash, this.filterSize());
            if(!this.getBit(index)){
                return false;
            }
        }
        return true;
    }

    private long hash(String s){
        s = s.toLowerCase();
        long h = FNV_INIT;
        byte[] bytes = s.getBytes();
        for(byte b : bytes){
            h = h * FNV_PRIME;
            h = h ^ b;
        }
        return h;
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