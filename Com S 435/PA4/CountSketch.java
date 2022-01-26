import java.util.ArrayList;
import java.util.Arrays;

public class CountSketch {

    long FNV_INIT = Long.parseUnsignedLong("14695981039346656037");
    long FNV_PRIME = 109951168211L;
    int numHashes; //k
    int columns; //L
    int[][] sketch;
    int N;
    
    public CountSketch(float epsilon, float delta, ArrayList<Integer> s){
        this.numHashes = (int) (24 * Math.log(2.0 / delta));
        this.columns = (int) (3 / Math.pow(epsilon, 2));
        
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
                int column = saltAndHashH(element, hashIndex);
                int sign = saltAndHashG(element, hashIndex);
                sketch[hashIndex][column] += (sign);
            }
        }
    }

    public int saltAndHashH(String s, int hashIndex){
        s += hashIndex;
        for(int i = 0; i < (int) Math.log(hashIndex); i++){
            s += "+";
        }
        s += hashIndex;
        s += s.hashCode();
        int column = Math.abs((int) (hashFunctionH(s) % this.columns));
        return column;
    }

    public int saltAndHashG(String s, int hashIndex){
        s += hashIndex;
        for(int i = 0; i < (int) Math.log(hashIndex); i++){
            s += "+";
        }
        s += hashIndex;
        s += s.hashCode();
        int sign = hashFunctionG(s);
        return sign;
    }

    private long hashFunctionH(String s){
        s = s.toLowerCase();
        long h = FNV_INIT;
        byte[] bytes = s.getBytes();
        for(byte b : bytes){
            h = h * FNV_PRIME;
            h = h ^ b;
        }
        return h;
    } 

    private int hashFunctionG(String s){
        s = s.toLowerCase();
        return s.hashCode() % 2 == 0 ? 1 : -1;
    } 

    public int approximateFrequency(int x){
        int[] values = new int[numHashes];
        for(int i = 0; i < numHashes; i++){
            values[i] = 0;
        }

        String s = Integer.toString(x);
        for(int hashIndex = 0; hashIndex < this.numHashes; hashIndex++){
            int column = saltAndHashH(s, hashIndex);
            int sign = saltAndHashG(s, hashIndex);
            values[hashIndex] = (sketch[hashIndex][column] * sign);
        }
        Arrays.sort(values);

        if(numHashes % 2 == 0){
            return (values[numHashes/2 - 1] + values[numHashes/2]) / 2;
        }
        else{
            return values[(int) Math.floor(numHashes/2)];
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
        return (float) Math.sqrt(3 / this.columns);
    }

    public static void main(String[] args){
        float epsilon = 0.001f;
        float delta = 0.002f;
        ArrayList<Integer> s = new ArrayList<>();
        for(int i = 1; i <= 1000; i++){
            for(int j = 0; j < 1; j++){
                s.add(i);
            }
        }
        for(int i = 0; i < 10000; i++){
            s.add(123);
        }

        CountSketch cs = new CountSketch(epsilon, delta, s);
        System.out.println(cs.approximateFrequency(123));

    }
}
