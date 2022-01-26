import java.io.IOException;

public class MinHashTime {
    
    String folder;
    int numPermutations;
    
    public MinHashTime(String folder, int numPermutations){
       this.folder = folder;
       this.numPermutations = numPermutations;
    }

    public void timer() throws IOException{
    
        long mhsStart = System.nanoTime();    
        MinHashSimilarities mhs = new MinHashSimilarities(folder, numPermutations);
        long mhsEnd = System.nanoTime();    
        System.out.println("\nTime taken (s) to construct an instance of MinHashSimilarities: " + (mhsEnd - mhsStart) / 1_000_000_000.0);

        int numDocs = mhs.mh.allDocs().length;
        
        long exactStart = System.nanoTime();    
        for(int i = 0; i < numDocs; i++){
            String doc1 = mhs.mh.allDocs()[i];
            for(int j = i + 1; j < numDocs; j++){
                String doc2 = mhs.mh.allDocs()[j];
                mhs.exactJacard(doc1, doc2);
            }
        }
        long exactEnd = System.nanoTime();    
        System.out.println("Time taken (s) to compute the exact Jaccard similarity between all pairs: " + (exactEnd - exactStart) / 1_000_000_000.0);

        long approxStart = System.nanoTime();    
        for(int i = 0; i < numDocs; i++){
            String doc1 = mhs.mh.allDocs()[i];
            for(int j = i + 1; j < numDocs; j++){
                String doc2 = mhs.mh.allDocs()[i];
                mhs.approximateJacard(doc1, doc2);    
            }
        }
        long approxEnd = System.nanoTime();   
        System.out.println("Time taken (s) to compute the approximate Jaccard similarity between all pairs: " + (approxEnd - approxStart) / 1_000_000_000.0);
        System.out.println();
    }

    public static void main(String[] args) throws IOException{
        String folder = "/Users/alex/Desktop/PA2Data/space/";
        MinHashTime mht = new MinHashTime(folder, 600);
        mht.timer();
    }
}
