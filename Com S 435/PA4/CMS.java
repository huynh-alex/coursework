import java.util.ArrayList;
import java.util.HashMap;

public class CMS {

    long FNV_INIT = Long.parseUnsignedLong("14695981039346656037");
    long FNV_PRIME = 109951168211L;
    int numHashes; //k
    int columns; //L
    int[][] sketch;
    int N;
    float q;
    float r;
    HashMap<Integer, Integer> heavyHitters;

    public CMS(float epsilon, float delta, ArrayList<Integer> s, float q, float r){
        this.numHashes = (int) Math.log(1.0 / delta);
        this.columns = (int) (2 / epsilon);
        this.q = q;
        this.r = r;
        this.heavyHitters = new HashMap<>();

        this.sketch = new int[numHashes][columns];
        for(int i = 0; i < numHashes; i++){
            for(int j = 0; j < columns; j++){
                sketch[i][j] = 0;
            }
        }
        processStream(s);
    }

    public void processStream(ArrayList<Integer> s){
        for(Integer i : s){
            String element = Integer.toString(i);
            this.N++;
            for(int hashIndex = 0; hashIndex < numHashes; hashIndex++){
                int column = saltAndHash(element, hashIndex);
                sketch[hashIndex][column]++;
            }
            int approxFreq = approximateFrequency(i);
            if(approxFreq >= q * this.N){
                heavyHitters.put(i, approxFreq);
            }
            else if(approxFreq < r * this.N && heavyHitters.containsKey(i)){
                heavyHitters.remove(i);
            }
        }
        updateHH();
    }
    
    public int saltAndHash(String s, int hashIndex){
        s += hashIndex;
        for(int i = 0; i < (int) Math.log(hashIndex); i++){
            s += "+";
        }
        s += hashIndex;
        s += s.hashCode();
        int saltedHash = Math.abs((int) (hash(s) % this.columns));
        return saltedHash;
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

    public int approximateFrequency(int x){
        int freq = Integer.MAX_VALUE;
        
        String element = Integer.toString(x);
        for(int hashIndex = 0; hashIndex < this.numHashes; hashIndex++){
            int column = saltAndHash(element, hashIndex);
            freq = Math.min(freq, sketch[hashIndex][column]);
        }
        return freq;
    }

    public ArrayList<Integer> approximateHH(){
        return new ArrayList<>(heavyHitters.keySet());
    }

    public void updateHH(){
        ArrayList<Integer> removeThese = new ArrayList<>();
        for(Integer i : this.heavyHitters.keySet()){
            if(heavyHitters.get(i) < r * this.N){
                removeThese.add(i);
            }
        }
        for(Integer i : removeThese){
            heavyHitters.remove(i);
        }
    }

    public void setColumnsAndNumHashes(int numHashes, int columns){
        this.numHashes = numHashes;
        this.columns = columns;

        this.sketch = new int[numHashes][columns];
        for(int i = 0; i < numHashes; i++){
            for(int j = 0; j < columns; j++){
                this.sketch[i][j] = 0;
            }
        }
    }

    public float getEpsilon(){
        return 2 / this.columns;
    }

    public int numRows(){
        return sketch.length;
    }

    public int numCols(){
        return sketch[0].length;
    }
}
