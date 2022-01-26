import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;

public class QueryProcessor {

    PositionalIndex pi;

    public QueryProcessor(String folder) throws IOException{
        this.pi = new PositionalIndex(folder);
        
    }

    public ArrayList<String> topKDocs(String query, int k){
        //Set the query vector so we don't have to keep computing it
        pi.setQueryVector(query);

        //For each document, map the score to the document(s) with that score
        //This deals with potential duplicate scores.
        //That is why we use an ArrayList<String> as the value. For example, 3 documents can have the exact same score.
        LinkedHashMap<Double, ArrayList<String>> scoreDocMap = new LinkedHashMap<>();        
        for(String doc : pi.allDocs){
            double score = pi.Relevance(query, doc);
            if(!scoreDocMap.containsKey(score)){
                scoreDocMap.put(score, new ArrayList<>());
            }
            scoreDocMap.get(score).add(doc);
        }

        //sort the scores in descending order (highest first)
        ArrayList<Double> sortedScores = new ArrayList<>();
        sortedScores.addAll(scoreDocMap.keySet());        
        Collections.sort(sortedScores, Collections.reverseOrder());

        //builds the ArrayList with the top k documents
        ArrayList<String> top = new ArrayList<>();
        for(double score: sortedScores){
            ArrayList<String> docsWithThisScore = scoreDocMap.get(score);
            for(String doc : docsWithThisScore){
                top.add(doc);
                if(top.size() == k) return top;
            }
        }
        return top;
    }

    public String scores(String doc, String query){
        String score = "";
        score += doc;
        score += (": TPS Score: " + pi.TPScore(query, doc));
        score += (", VSS Score: " + pi.VSScore(query, doc));
        score += (", Relevance Score: " + pi.Relevance(query, doc) + "\n");
        return score;
    }

    public static void main(String args[]) throws IOException{
        String folder = "/Users/alex/Desktop/PA3_Data/IR";
        QueryProcessor qp = new QueryProcessor(folder);
        int k = 10;
        
        System.out.println("\n\n");
        String query = "left-handed";
        ArrayList<String> top = qp.topKDocs(query, k);
        System.out.println("Query: " + query + "\n");
        int i = 1; 
        for(String doc : top){
            System.out.println(i + ". " + qp.scores(doc, query));
            i++;
        }

        query = "delicious food";
        top = qp.topKDocs(query, k);
        System.out.println("Query: " + query + "\n");
        i = 1; 
        for(String doc : top){
            System.out.println(i + ". " + qp.scores(doc, query));
            i++;
        }

        query = "many points scored";
        top = qp.topKDocs(query, k);
        System.out.println("Query: " + query + "\n");
        i = 1; 
        for(String doc : top){
            System.out.println(i + ". " + qp.scores(doc, query));
            i++;
        }

        query = "rain and windy weather";
        top = qp.topKDocs(query, k);
        System.out.println("Query: " + query + "\n");
        i = 1; 
        for(String doc : top){
            System.out.println(i + ". " + qp.scores(doc, query));
            i++;
        }

        query = "most boring match of all time";
        top = qp.topKDocs(query, k);
        System.out.println("Query: " + query + "\n");
        i = 1; 
        for(String doc : top){
            System.out.println(i + ". " + qp.scores(doc, query));
            i++;
        }
    }
}
