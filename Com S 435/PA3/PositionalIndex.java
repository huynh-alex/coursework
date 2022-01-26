import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

public class PositionalIndex {

    // HashMap: String (term) -> LinkedHashMap
    // LinkedHashMap: String (document) -> ArrayList (positions)
    HashMap<String, LinkedHashMap<String, ArrayList<Integer>>> invertedIndex;
    LinkedHashSet<String> terms;
    HashMap<String, double[]> documentVectors;
    String folder;
    File[] allFiles;
    String[] allDocs;

    double[] queryVector;
    String[] queryTerms;
    LinkedHashMap<String, Integer> queryTermsFreqMap;
        
    public PositionalIndex(String folder) throws IOException{
        invertedIndex = new HashMap<>();
        terms = new LinkedHashSet<>();
        documentVectors = new HashMap<>();
        this.allFiles = new File(folder).listFiles();
        allDocs = new String[this.allFiles.length];
        for(int i = 0; i < this.allFiles.length; i++){
            allDocs[i] = allFiles[i].getName();
        }
        preprocess();
        retrieveTerms();
        // System.out.println("Terms retrieved: " + terms.size());
        buildInvertedIndex();
        // System.out.println("Inverted index built.");
    }

    public void preprocess() throws FileNotFoundException, IOException{
        for(File file: this.allFiles){
            //code adapted from https://stackoverflow.com/questions/20039980/java-replace-line-in-text-file
            StringBuffer inputBuffer = new StringBuffer();
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                line = line.toLowerCase();
                line = line.replaceAll("[.,”“?\':;]", " ");      // removes specific punctuation
                line = line.replaceAll("[\"]", " ");             // removes quotes: " 
                line = line.replaceAll("[\\[\\](){}]", " ");     // removes delimeters
                line = line.replaceAll("\\bthe\\b", " ");       // removes the word "the"
                line = line.replaceAll("\\bis\\b", " ");        // removes the word "is"
                line = line.replaceAll("\\bare\\b", " ");       // removes the word "are"
                if(!line.isEmpty()){
                    inputBuffer.append(line + '\n');
                }
            }
            br.close();
            FileOutputStream fileOut = new FileOutputStream(file);
            fileOut.write(inputBuffer.toString().getBytes());
            fileOut.close();
        }
    }

    public void retrieveTerms() throws IOException{
        for(File file: this.allFiles){ //go through every document
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                for(String term : line.split("\\s+")){
                    terms.add(term);
                }
            }
            br.close();
        }
    }

    public void buildInvertedIndex() throws IOException{
        for(File file : this.allFiles){
            String fileName = file.getName();
            String docTerms = "";
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            //get all terms of this document
            while ((line = br.readLine()) != null) {
                docTerms += (line + " ");
            }
            String[] splitTerms = docTerms.split("\\s+");
            int pos = 0;
            for(String term : splitTerms){
                //make sure inverted index has the term
                if(!invertedIndex.containsKey(term)){
                    invertedIndex.put(term, new LinkedHashMap<>());
                }
                //this is the scenario when the first time a term appears in that document
                if(!invertedIndex.get(term).containsKey(fileName)){
                    invertedIndex.get(term).put(fileName, new ArrayList<>());
                }
                invertedIndex.get(term).get(fileName).add(pos);
                pos++;
            }
            br.close();
        }
    }

    String postingsList(String t){
        LinkedHashMap<String, ArrayList<Integer>> posting = invertedIndex.get(t);
        
        Set<String> docs = posting.keySet();
        Iterator<String> iter = docs.iterator();

        String out = "";
        out += "[";
        while(iter.hasNext()){
            String doc = iter.next();
            out += "<";
            out += doc;
            out += " : ";
            ArrayList<Integer> positions = posting.get(doc);
            for(int i = 0; i < positions.size(); i++){
                out += positions.get(i);
                if(i != positions.size() - 1)
                    out += ",";
            }
            out += ">";
            if(iter.hasNext()){
                out += ", ";
            }
        }
        out += "]";
        return out;
    }

    //works
    double TPScore(String query, String doc){
        if(this.queryTerms.length == 1){
            return 0;
        }
        double numerator = this.queryTerms.length;
        double denominator = 0;
        for(int i = 0; i < this.queryTerms.length - 1; i++){
            denominator += (distance(doc, this.queryTerms[i], this.queryTerms[i+1]));
        }
        return numerator / denominator;
    }

    //works
    int distance(String doc, String t1, String t2){
        int distance = Integer.MAX_VALUE;
        if(!invertedIndex.containsKey(t1) || !invertedIndex.containsKey(t2)){
            return 17;
        }
        ArrayList<Integer> p = invertedIndex.get(t1).get(doc);
        ArrayList<Integer> r = invertedIndex.get(t2).get(doc);
        if(r == null || p == null){
            return 17;
        }
        for(int i = 0; i < r.size(); i++){
            for(int j = 0; j < p.size(); j++){
                if(r.get(i) > p.get(j)){
                    distance = Math.min(distance, r.get(i) - p.get(j));
                }
            }
        }
        distance = Math.min(distance, 17);
        return distance;
    }

    //works
    int termFrequency(String term, String doc){
        if(invertedIndex.containsKey(term) && invertedIndex.get(term).containsKey(doc)){
            return invertedIndex.get(term).get(doc).size();
        }
        return 0;
    }

    //works
    int docFrequency(String term){
        if(!invertedIndex.containsKey(term)){
            return 0;
        }
        return invertedIndex.get(term).size();
    }

    //works
    double weight(String t, String d){
        if(docFrequency(t) == 0){
            return 0;
        }
        return Math.sqrt(termFrequency(t, d)) * Math.log10(allFiles.length/(docFrequency(t) + 0.0));
    }

    double[] getDocumentVector(String d){
        double[] vector = new double[terms.size()];
        int i = 0;
        for(String term : terms){
            if(termFrequency(term, d) == 0){
                vector[i] = 0;
            }
            else{
                vector[i] = weight(term, d);
            }
            i++;
        }
        return vector;
    }

    double[] getShortenedDocumentVector(String d){
        double[] vector = new double[this.queryTermsFreqMap.size()];
        int i = 0;
        for(String term : this.queryTermsFreqMap.keySet()){
            vector[i] = weight(term, d);
            i++;
        }
        return vector;
    }

    void setQueryVector(String q){
        this.queryTerms = q.split("\\s+");
        
        this.queryTermsFreqMap = new LinkedHashMap<>();
        for(String term : queryTerms){
            if(queryTermsFreqMap.containsKey(term)){
                queryTermsFreqMap.put(term, queryTermsFreqMap.get(term) + 1);
            }
            else{
                queryTermsFreqMap.put(term, 1);     
            }
        }

        int i = 0;
        this.queryVector = new double[queryTermsFreqMap.size()];
        for(String term : queryTermsFreqMap.keySet()){
            this.queryVector[i] = queryTermsFreqMap.get(term);
            i++;
        }
    }

    double[] getQueryVector(){
        return this.queryVector;
    }
    
    //works
    double VSScore(String query, String doc){
        for(String term : this.queryTerms){
            if(invertedIndex.containsKey(term) && invertedIndex.get(term).containsKey(doc)){
                double score = 0;
                double[] vq = getQueryVector();
                double[] vd = getShortenedDocumentVector(doc);
                if(vd.length == 0){
                    return 0;
                }
                //compute "fast" cosine similarity
                for(int i = 0; i < vq.length; i++){
                    score += (vq[i] * vd[i]);
                }
                score /= (vd.length);                
                return score;
            }
        }
        return 0;
    }

    //works
    double Relevance(String query, String doc){
        return (0.6 * TPScore(query, doc)) + (0.4 * VSScore(query, doc));
    }

    public static void main(String[] args) throws IOException{
    }
}
