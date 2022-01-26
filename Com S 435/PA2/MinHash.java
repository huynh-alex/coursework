import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileOutputStream;

public class MinHash{

    File folder;
    int numPermutations;
    String[] allDocs;
    File[] allFiles;
    int numDocs;
    LinkedHashMap<String, Integer> allDocsMap; //maps the documents to an index for quick access compared to a list

    LinkedHashSet<String> termsSet; //This is U, the set of all terms not including multiples
    LinkedHashSet<String> termsMultiSet; //This is multi-U, the set of all terms including multiples. Items are in this form: string + s.freq

    LinkedHashMap<String, HashMap<String, Integer>> mappingDocToTermFreqMap; //This maps documents -> [map of terms in that document -> frequency]
    LinkedHashMap<String, LinkedHashSet<String>> mappingDocToMultiSet; //This maps documents to multiset of terms in that document
    ArrayList<HashMap<String,Integer>> permutations; //list of all random permutations (which are mappings from integers to integers)

    int[][] minHashMatrix;
    int[][] termDocumentMatrix;

    public MinHash(String folder, int numPermutations) throws IOException{
        this.numPermutations = numPermutations;
        this.permutations = new ArrayList<HashMap<String,Integer>>();

        File files = new File(folder);
        this.numDocs = files.list().length;

        for (File doc : files.listFiles()) {
            String docName = doc.getName();
            if(docName.equals(".DS_Store")){
                this.numDocs -= 1;
            }
        }
        //has the String name of each file in the folder
        this.allDocs = new String[numDocs];
        
        //has the File object of each file in the folder
        this.allFiles = new File[numDocs];
        
        //this maps each document to a unique index
        this.allDocsMap = new LinkedHashMap<>();

        int docIndex = 0;
        for (File doc : files.listFiles()) {
            String docName = doc.getName();
            if(!(docName.equals(".DS_Store"))){
                allDocsMap.put(docName, docIndex);
                allDocs[docIndex] = docName;
                allFiles[docIndex] = doc;
                docIndex++;
            }
        }

        this.termsSet = new LinkedHashSet<>();
        this.mappingDocToTermFreqMap = new LinkedHashMap<>(); 
        this.termsMultiSet = new LinkedHashSet<String>();
        this.mappingDocToMultiSet = new LinkedHashMap<>();
        
        //Turn on preprocessing if needed; if documents are already processed, then turn off for performance reasons
        preprocess();
        retrieveTerms();
        // System.out.println("Terms: " + termsSet.size());
        // System.out.println("Multi-terms: " + termsMultiSet.size());
        buildTermDocumentMatrix();
        // System.out.println("Built TDM");
        buildPermutations();
        // System.out.println("Built Permutations");
        buildMinHashMatrix();
        // System.out.println("Built MHM");
    }

    //works
    public String[] allDocs(){
        return this.allDocs;
    }
    
    //works
    public int getDocIndex(String doc){
        File docFile = new File(doc);
        return allDocsMap.get(docFile.getName());
    }
    
    //works
    //helper function 
    //params: terms = terms in D_i; permutation is pi_i
    private int computeMinHash(LinkedHashSet<String> terms, HashMap<String,Integer> permutation){
        int min = Integer.MAX_VALUE;
        for(String term : terms){
            min = Math.min(min, permutation.get(term));
        }
        return min;
    }

    //works
    //helper function
    private void buildMinHashMatrix() throws FileNotFoundException{
        this.minHashMatrix = new int[numDocs][numPermutations];
        int docIndex = 0;
        for(String doc : allDocs){
            for(int permutationIndex = 0; permutationIndex < numPermutations; permutationIndex++){
                minHashMatrix[docIndex][permutationIndex] = computeMinHash(mappingDocToMultiSet.get(doc), permutations.get(permutationIndex));
            }
            docIndex++;
        }
    }
    
    public int[][] minHashMatrix(){        
        return this.minHashMatrix;
    }

    //helper function
    private void buildTermDocumentMatrix() throws FileNotFoundException{

        this.termDocumentMatrix = new int[numDocs][termsSet.size()];
        for(int i = 0; i < numDocs; i++){
            for(int j = 0; j < termsSet.size(); j++){
                termDocumentMatrix[i][j] = 0;
            }
        }

        //maps each term to an index from 0 to |U|-1
        HashMap<String, Integer> termMapping = new HashMap<>();
        int termMappingIndex = 0;
        for(String term : termsSet){
            termMapping.put(term, termMappingIndex);
            termMappingIndex++;
        }
        int docIndex = 0;

        //for every document, get its term and its frequency via multiSetMap
        //put the frequency in the respective entry of the term document matrix
        for(String doc : allDocs){
            HashMap<String, Integer> docTermsMultiSet = mappingDocToTermFreqMap.get(doc);
            for(String term : docTermsMultiSet.keySet()){
                int termIndex = termMapping.get(term);
                int freq = docTermsMultiSet.get(term);
                    termDocumentMatrix[docIndex][termIndex] = freq;
            }
            docIndex++;
        }
    }

    public int[][] termDocumentMatrix(){
        return termDocumentMatrix;
    }

    public void preprocess() throws FileNotFoundException, IOException{
        for(File doc: this.allFiles){ //go through every document
            //code adapted from https://stackoverflow.com/questions/20039980/java-replace-line-in-text-file
            BufferedReader file = new BufferedReader(new FileReader(doc));
            StringBuffer inputBuffer = new StringBuffer();

            String line;
            while ((line = file.readLine()) != null) {
                String[] splitLine = line.split("\\s+");
                line = "";
                for(String term : splitLine){
                    if(term.length() > 2){
                        line += (term + " ");
                    }
                }
                // line = line.replaceAll("\\b\\w{1,2}\\b"," ");   //removes 1-2 letter words
                line = line.toLowerCase();
                line = line.replaceAll("[.,:;']", " ");        //removes specific punctuation
                line = line.replaceAll("\\bthe\\b", " ");       //removes the word "the"
                line = line.trim();
                if(!line.isEmpty()){
                    inputBuffer.append(line + '\n');
                }
            }
            file.close();
            FileOutputStream fileOut = new FileOutputStream(doc);
            fileOut.write(inputBuffer.toString().getBytes());
            fileOut.close();
        }
    }
 
    //helper function to obtain termsSet, mappingDocToTermFreqMap, termsMultiSet, and mappingDocToMultiSet
    private void retrieveTerms() throws IOException{
        for(File file: this.allFiles){ //go through every document
            LinkedHashMap<String, Integer> docTermsFreqMap = new LinkedHashMap<>(); //each file has its own frequency mapping
            LinkedHashSet<String> docTermsMultiSet = new LinkedHashSet<>();     //each file has its own multiset of terms
            LinkedHashSet<String> docTermsSet = new LinkedHashSet<>();          //each file has its own set of terms
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                for(String term : line.split("\\s+")){
                    docTermsSet.add(term);
                    if(docTermsFreqMap.containsKey(term))
                        docTermsFreqMap.put(term, docTermsFreqMap.get(term) + 1);
                    else
                        docTermsFreqMap.put(term, 1);
                    docTermsMultiSet.add(term + docTermsFreqMap.get(term));
                }
            }
            br.close();

            termsSet.addAll(docTermsSet);
            mappingDocToTermFreqMap.put(file.getName(), docTermsFreqMap);
            termsMultiSet.addAll(docTermsMultiSet);
            mappingDocToMultiSet.put(file.getName(), docTermsMultiSet);
        }
    }

    public void buildPermutations(){
        List<Integer> range = new ArrayList<>();
        for(int i = 1; i <= permutationDomain(); i++){
            range.add(i);
        }
        for(int i = 0; i < numPermutations; i++){
            HashMap<String,Integer> permutation = new HashMap<>();
            java.util.Collections.shuffle(range);
            int j = 0;
            for(String string : termsMultiSet){
                permutation.put(string, range.get(j));
                j++;
            }
            permutations.add(permutation);
        }
    }

    public int permutationDomain(){
        return termsMultiSet.size();
    }

    public int numPermutations(){
        return this.numPermutations;
    }

    public static void main(String[] args) throws FileNotFoundException, IOException{
        String folder = "/Users/alex/Desktop/PA2Data/space/";        
        MinHash mh = new MinHash(folder, 1);
    }
}