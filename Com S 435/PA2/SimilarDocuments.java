import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class SimilarDocuments {
    
    String folder;
    int numPermutations;
    double simThreshold;
    LSH lsh;
    MinHashSimilarities mhs;
    
    public SimilarDocuments(String folder, int numPermutations, double simThreshold) throws IOException{
        this.folder = folder;
        this.numPermutations = numPermutations;
        this.simThreshold = simThreshold;

        mhs = new MinHashSimilarities(folder, numPermutations);
        MinHash mh = mhs.mh;
        int bands = getOptimalBands(numPermutations, simThreshold);
        lsh = new LSH(mh.minHashMatrix(), mh.allDocs(), bands);
    }

    private double fx(double x, int k, double s){
        return (x * Math.log(x)) + (k * Math.log(s));
    }

    private double dfx(double x, int k, double s){
        return Math.log(x) + 1.0;
    }
    
    private int getOptimalBands(int k, double s){
        //solve for optimal bands using Newton's method
        double tolerance = 0.000000001;
        double x0 = 100.0; //guess that bands = 100
        while(true){
            double x1 = x0 - (fx(x0, k, s) / dfx(x0, k, s)); 
            if(Math.abs(x1 - x0) < tolerance)
                break;
            x0 = x1;
        }
        return (int) x0;
    }

    public ArrayList<String> similaritySearch(String doc){
        ArrayList<String> sim = new ArrayList<>();
        ArrayList<String> candidates = lsh.retrieveSim(doc);
        for(String candidate : candidates){
            //?! false negative
            if(mhs.approximateJacard(doc, candidate) > simThreshold){
                if(!candidate.equals(new File(doc).getName())) //dont consider adding this same document since it's trivially similar
                    sim.add(new File(candidate).getName()); //making a File object and then calling .getName() for better formatting purposes
            }
        }
        return sim;
    }

    public static void main(String[] args) throws IOException{
        
        double s = 0.5;
        int k = 200;
    
        String folder = "/Users/alex/Desktop/PA2Data/LSHData/";
        SimilarDocuments sd = new SimilarDocuments(folder, k, s);
        String fileToSearch = "/Users/alex/Desktop/PA2Data/LSHData/baseball1.txt.copy5";
        System.out.println(sd.similaritySearch(fileToSearch));    

        folder = "LSHData/";
        sd = new SimilarDocuments(folder, k, s);
        fileToSearch = "LSHData/baseball1.txt.copy5";
        System.out.println(sd.similaritySearch(fileToSearch));    

        folder = "/Users/alex/Desktop/PA2Data/LSHData/";
        sd = new SimilarDocuments(folder, k, s);
        fileToSearch = "baseball1.txt.copy5";
        System.out.println(sd.similaritySearch(fileToSearch));    

        folder = "LSHData/";
        sd = new SimilarDocuments(folder, k, s);
        fileToSearch = "baseball1.txt.copy5";
        System.out.println(sd.similaritySearch(fileToSearch));    

        boolean reportTesting = false;
        if(reportTesting){
            s = 0.5;
            k = 400;
            folder = "/Users/alex/Desktop/PA2Data/LSHData/";
            sd = new SimilarDocuments("/Users/alex/Desktop/PA2Data/LSHData", k, s);
            System.out.println("\nPermutations (k) = " + k +  ", Bands (b) = " + sd.lsh.b + ", Rows (r) = " + sd.lsh.r + ", Similarity threshold (s) = " + s);
            
            String baseFileToSearch = folder + "baseball";
            for(int i = 5; i < 100; i+=10){
                fileToSearch = baseFileToSearch + i + ".txt";      
                System.out.println("\nFiles similar to " + new File(fileToSearch).getName());
                System.out.println(sd.similaritySearch(fileToSearch));    
            }
            System.out.println();
        }
    }
}
