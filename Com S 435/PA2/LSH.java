import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.io.File;
import java.io.FileNotFoundException;

public class LSH {

    int[][] mhm;
    String[] docNames;
    HashMap<String, Integer> allDocsMap; //maps the documents to an index for quick access compared to a list
    ArrayList<HashMap<Integer, ArrayList<String>>> tables;
    int b;
    int k;
    int r;
    int N;

    //for every document in the minhash matrix
    //hash its b bands into the b tables, placing the document name in the last of the table
    public LSH(int[][] minHashMatrix, String[] docNames, int bands) throws FileNotFoundException{
        this.mhm = minHashMatrix;
        this.docNames = docNames;
        this.b = bands;
        this.N = docNames.length;
        this.k = minHashMatrix[0].length;
        this.r = k/b;

        tables = new ArrayList<>();
        for(int i = 0; i < b; i++){
            tables.add(new HashMap<Integer, ArrayList<String>>());
        }

        int allDocsMapIndex = 0;        
        allDocsMap = new HashMap<>();
        for(String doc : docNames){
            allDocsMap.put(doc, allDocsMapIndex);
            allDocsMapIndex++;
        }

        for(int i = 0; i < N; i++){ //for each document
            String doc = docNames[i];
            for(int j = 0; j < b; j++){ //for each band (therefore for each hash table)
                HashMap<Integer, ArrayList<String>> table = tables.get(j);

                //Hash function maps r_tuple to {0,1,...,T} (where T = N*ln(N)) ... (this is in accordance to lecture notes that T > N)
                //code adapted from https://stackoverflow.com/a/24461797
                int[] r_tuple = Arrays.copyOfRange(mhm[i], (r * j), (r + 1) * j);
                int index = Math.abs(Arrays.hashCode(r_tuple)) % ((int) (N * Math.log(N)));
                
                if(!table.containsKey(index)){
                    table.put(index, new ArrayList<>()); //create new chain if not already created
                }
                table.get(index).add(doc);
                tables.set(j, table);
            }
        }
    }

    public String[] docNames(){
        return docNames;
    }

    public int getDocIndex(String doc){
        File docFile = new File(doc);
        return allDocsMap.get(docFile.getName());
    }

    public ArrayList<String> retrieveSim(String docName){
        int docIndex = getDocIndex(docName);

        HashSet<String> similarSet = new HashSet<>();
        for(int j = 0; j < b; j++){
            HashMap<Integer, ArrayList<String>> table = tables.get(j);

            int[] r_tuple = Arrays.copyOfRange(mhm[docIndex], (r * j), (r + 1) * j);
            int index = Math.abs(Arrays.hashCode(r_tuple)) % ((int) (N * Math.log(N)));
            
            for (String doc : table.get(index)) {
                similarSet.add(doc);
            }
        }
        ArrayList<String> similarList = new ArrayList<>();
        similarList.addAll(similarSet);
        return similarList;
    }
}