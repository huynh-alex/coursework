import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileReader;
import java.util.HashMap;

public class NaiveDifferential {
    HashMap<String, String> hm;

    public HashMap<String, String> createFilter() throws FileNotFoundException, IOException{
        hm = new HashMap<>();

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
                hm.put(key, line);
            }
        } catch(IOException noMoreLines) {}
        return hm;
    }

    public String retrieveRecord(String key) throws FileNotFoundException, IOException{
        BufferedReader reader;

        if(hm.containsKey(key)){
            return hm.get(key);
        }

        reader = new BufferedReader(new FileReader("pa1Data/database.txt"));
        try{
            String line;
            while ((line = reader.readLine()) != null) {
                if(line.contains(key)){
                    return line;
                }
            }
        } catch(IOException noMoreLines) {}

        return "";
    }

    public double searchTime(String key) throws FileNotFoundException, IOException{
        if(hm.containsKey(key)){
            return 1.0;
        }
        else{
            BufferedReader reader = new BufferedReader(new FileReader("pa1Data/database.txt"));
            try{
                String line;
                while ((line = reader.readLine()) != null) {
                    if(line.contains(key)){
                        return 2.0;
                    }
                }
            } catch(IOException noMoreLines) {}
            return 2.0;
    
        }
    }

    public NaiveDifferential() throws FileNotFoundException, IOException{
        this.hm = createFilter();
    }
}
