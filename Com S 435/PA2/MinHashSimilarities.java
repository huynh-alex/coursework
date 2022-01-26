import java.io.FileNotFoundException;
import java.io.IOException;


public class MinHashSimilarities {

    MinHash mh;
    int[][] tdm;
    int[][] mhm;

    public MinHashSimilarities(String folder, int numPermutations) throws IOException{
        this.mh = new MinHash(folder, numPermutations);
        this.tdm = mh.termDocumentMatrix();
        this.mhm = mh.minHashMatrix();
    }

    public double exactJacard(String file1, String file2){
        int indexA = mh.getDocIndex(file1);
        int indexB = mh.getDocIndex(file2);
        int[] file1Terms = tdm[indexA];
        int[] file2Terms = tdm[indexB];

        //we can approximate using frequencies, thus we use the normal set U (learned from the discussion board)
        int intersection = 0;
        int union = 0;
        int i;
        for(i = 0; i < mh.termsSet.size(); i++){
            intersection += Math.min(file1Terms[i], file2Terms[i]);
            union += Math.max(file1Terms[i], file2Terms[i]);
        }
        return intersection / (union + 0.0);
    }

    public double approximateJacard(String file1, String file2){
        
        int[] mh_A = minHashSig(file1);
        int[] mh_B = minHashSig(file2);
        int matches = 0;
        for(int i = 0; i < mh.numPermutations; i++){
            matches += (mh_A[i] == mh_B[i]) ? 1 : 0;
        }   
        return (matches + 0.0) / mh.numPermutations;
    }

    public int[] minHashSig(String fileName){
        int index = mh.getDocIndex(fileName);
        return mhm[index];
    }
    public static void main(String[] args) throws IOException{
        // String folder = "mild";
        String folder = "/Users/alex/Desktop/PA2Data/space/";
        int[] numPermutations = {100,200,300,400,500,600,700,800};
        MinHashSimilarities mhs = new MinHashSimilarities(folder, numPermutations[0]);
        System.out.println(mhs.exactJacard("space-0.txt", "space-0 copy.txt"));
    }

}