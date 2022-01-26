import java.io.IOException;

public class MinHashAccuracy{

    int badApprox;
    int numDocs;
    int numPermutations;
    double epsilon;
    MinHashSimilarities mhs;
    
    public MinHashAccuracy(String folder, int numPermutations, double epsilon) throws IOException{
        this.mhs = new MinHashSimilarities(folder, numPermutations);
        this.numDocs = mhs.tdm.length;
        this.badApprox = 0;
        this.numPermutations = numPermutations;
        this.epsilon = epsilon;
    }

    public void compute(){
        this.badApprox = 0;
        for(int i = 0; i < numDocs; i++){
            String doc1 = mhs.mh.allDocs()[i];
            for(int j = i + 1; j < numDocs; j++){
                String doc2 = mhs.mh.allDocs()[j];
                double exact = mhs.exactJacard(doc1, doc2);
                double approx = mhs.approximateJacard(doc1, doc2);
                if(Math.abs(exact - approx) > epsilon){
                    badApprox++;
                }
            }
        }
        System.out.println("Epsilon = " + epsilon + " --> " + badApprox);
    }

    public void setEpsilon(double epsilon){
        this.epsilon = epsilon;
    }

    public static void main(String args[]) throws IOException{
        System.out.println();
        String folder = "/Users/alex/Desktop/PA2Data/space/";
        int[] numPermutations = {100,200,300,400,500,600,700,800};
        for(int i = 0; i < numPermutations.length; i++){
            System.out.println("\nNumber of permutations: " + numPermutations[i]);
            MinHashAccuracy mha = new MinHashAccuracy(folder, numPermutations[i], 0.04);
            mha.compute();
            mha.setEpsilon(0.07);
            mha.compute();
            mha.setEpsilon(0.09);
            mha.compute();
            System.out.println();
        }
    }
}