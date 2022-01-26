import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class EmpiricalDifferential {
    public static void main(String[] args) throws FileNotFoundException, IOException{

        // NaiveDifferential filter = new NaiveDifferential();
        BloomDifferential filter = new BloomDifferential();
        System.out.println("Filter made");


        BufferedReader reader = new BufferedReader(new FileReader("pa1Data/grams.txt"));
        try{
            String key;
            double totalTime = 0;
            int linesRead = 0;
            // while ((key = reader.readLine()) != null) {
            for(int i = 0; i < 10000; i++){
                key = reader.readLine();
                double searchTime = filter.searchTime(key);
                totalTime += searchTime;
                linesRead++;
            }
            System.out.println("\n\nTotal time: " + totalTime);
            System.out.println("Average time: " + totalTime / linesRead);
            System.out.println("\n");
        } catch(IOException noMoreLines) {}
    }
}
