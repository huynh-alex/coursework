import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class BloomDifferential {
    
    BloomFilterFNV bf;

    public BloomFilterFNV createFilter() throws FileNotFoundException{
        int setSize = 0;
        // try{
        //     BufferedReader reader = new BufferedReader(new FileReader("pa1Data/DiffFile.txt"));
        //     try{
        //         // https://stackoverflow.com/questions/19486077/java-fastest-way-to-read-through-text-file-with-2-million-lines
        //         String line;
        //         while ((line = reader.readLine()) != null) {
        //             setSize++;
        //         }
        //     }
        //     catch(IOException noMoreLines) {}
        // } catch(FileNotFoundException fileNotFound) {}
        setSize = 1262147;
        BloomFilterFNV bf = new BloomFilterFNV(setSize, 8);
        BufferedReader reader = new BufferedReader(new FileReader("pa1Data/DiffFile.txt"));
        try{
            String line;
            while ((line = reader.readLine()) != null) {
                String key = "";
                String[] words = line.split("\\s+");
                for(int i = 0; i < 4; i++){
                    key += words[i] + " ";
                }
                key = key.substring(0, key.length() - 1); //remove space at the end
                bf.add(line);
            }
        } catch(IOException noMoreLines) {}
        return bf;
    }

    public String retrieveRecord(String key) throws FileNotFoundException, IOException{
        BufferedReader reader;

        if(bf.appears(key)){
            reader = new BufferedReader(new FileReader("pa1Data/DiffFile.txt"));
        }
        else{
            reader = new BufferedReader(new FileReader("pa1Data/database.txt"));
        }

        try{
            String line;
            while ((line = reader.readLine()) != null) {
                if(line.contains(key)){
                    return line;
                }
            }
        } catch(IOException noMoreLines) { //false positive, thus read from database.txt
            reader = new BufferedReader(new FileReader("pa1Data/database.txt"));
            String line;
            while ((line = reader.readLine()) != null) {
                if(line.contains(key)){
                    reader.close();
                    return line;
                }
            }
        }
        return "";
    }

    public double searchTime(String key) throws FileNotFoundException, IOException{
        BufferedReader reader;

        if(bf.appears(key)){
            reader = new BufferedReader(new FileReader("pa1Data/DiffFile.txt"));
        }
        else{
            reader = new BufferedReader(new FileReader("pa1Data/database.txt"));
        }

        try{
            String line;
            while ((line = reader.readLine()) != null) {
                if(line.contains(key)){
                    return 1.001;
                }
            }
        } catch(IOException noMoreLines) {
            reader = new BufferedReader(new FileReader("pa1Data/database.txt"));
            String line;
            while ((line = reader.readLine()) != null) {
                if(line.contains(key)){
                    reader.close();
                    return 2.001;
                }
            }
        }
        return 2.001;
    }

    public BloomDifferential() throws FileNotFoundException{
        this.bf = createFilter();
    }
}