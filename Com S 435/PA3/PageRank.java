import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Arrays;
import java.util.Collections;

public class PageRank {

    // our graph is is represented as a mapping of strings to lists: specifically, it is an arraylist of strings
    LinkedHashMap<String, ArrayList<String>> graph;
    LinkedHashMap<String, Double> sortedRankingsMap;
    HashMap<String, Integer> vertexStringToIndexMap;
    HashMap<Integer, String> vertexIndexToStringMap;
    int numEdges;
    int numVertices;
    double beta;
    double epsilon;

    public PageRank(String edgesFile, double epsilon, double beta) throws IOException{
        this.beta = beta;
        this.epsilon = epsilon;
        this.graph = new LinkedHashMap<>();
        vertexStringToIndexMap = new HashMap<>();
        vertexIndexToStringMap = new HashMap<>();
        BufferedReader br = new BufferedReader(new FileReader(edgesFile));        
        this.numVertices = Integer.parseInt(br.readLine());
        
        int vertexIndex = 0;
        String line;
        while ((line = br.readLine()) != null) {
            String[] vertices = line.split(" ");
            //vertices[0] is the main vertex, vertices[1] is connected to it
            if(!graph.containsKey(vertices[0])){
                graph.put(vertices[0], new ArrayList<>());
                vertexStringToIndexMap.put(vertices[0], vertexIndex);
                vertexIndexToStringMap.put(vertexIndex, vertices[0]);
                vertexIndex++;
            }
            graph.get(vertices[0]).add(vertices[1]);

            if(!graph.containsKey(vertices[1])){
                graph.put(vertices[1], new ArrayList<>());
                vertexStringToIndexMap.put(vertices[1], vertexIndex);
                vertexIndexToStringMap.put(vertexIndex, vertices[1]);
                vertexIndex++;
            }
            this.numEdges++;
        }
        br.close();

        double[] rankings = computePageRank();

        //some rankings might have the same value, so we map to an ArrayList, not Integer
        LinkedHashMap<Double, ArrayList<Integer>> rankingsMap = new LinkedHashMap<>();
        for(int i = 0; i < this.numVertices; i++){
            if(!rankingsMap.containsKey(rankings[i])){
                rankingsMap.put(rankings[i], new ArrayList<>());
            }
            rankingsMap.get(rankings[i]).add(i);
        }

        ArrayList<Double> sortedRankingsList = new ArrayList<>();
        sortedRankingsList.addAll(rankingsMap.keySet());
        Collections.sort(sortedRankingsList, Collections.reverseOrder());

        sortedRankingsMap = new LinkedHashMap<>();
        for(double ranking : sortedRankingsList){
            ArrayList<Integer> indices = rankingsMap.get(ranking);
            for(Integer index : indices){
                sortedRankingsMap.put(vertexIndexToStringMap.get(index), ranking);
            }
        }
    }
    
    public double numEdges(){
        return this.numEdges;
    }
    
    private double norm(double[] u, double[] v){
        double sum = 0;
        for(int i = 0; i < u.length; i++){
            sum += Math.abs(u[i] - v[i]);
        }
        return sum;
    }

    private double[] iterate(double[] distribution){
        double[] nextDistribution = new double[this.numVertices];
        for(int i = 0; i < this.numVertices; i++){
            nextDistribution[i] = (1 - this.beta) / (this.numVertices);
        }

        for(String vertex : graph.keySet()){
            int pIndex = vertexStringToIndexMap.get(vertex);
            int outdegree = graph.get(vertex).size();
            if(outdegree > 0){
                ArrayList<String> Q = graph.get(vertex);
                for(String q : Q){
                    int qIndex = vertexStringToIndexMap.get(q);
                    nextDistribution[qIndex] += ((this.beta) * (distribution[pIndex] / outdegree));
                }
            }
            else{
                for(int i = 0; i < this.numVertices; i++){
                    nextDistribution[i] += ((this.beta) *  (distribution[pIndex] / this.numVertices));
                }
            }
        }
        return nextDistribution;
    }

    private double[] computePageRank(){
        double[] P_0 = new double[numVertices];
        for(int i = 0; i < numVertices; i++){
            P_0[i] = 1.0 / numVertices;
        }

        double[] P_n = Arrays.copyOf(P_0, P_0.length);
        boolean converged = false;
        int n = 0;
        while(!converged){
            double[] P_next = iterate(P_n);
            if(norm(P_next, P_n) <= this.epsilon){
                converged = true;
            }
            P_n = Arrays.copyOf(P_next, P_next.length);
            n++;
        }
        System.out.println("\nEpsilon = " + this.epsilon + ", Beta = " + this.beta + " -> Iterations: " + n);
        return P_n;
    }

    public String[] topKPageRank(int k){
        String topK[] = new String[k];
        int i = 0;
        for(String page: sortedRankingsMap.keySet()){
            topK[i] = page;
            i++;
            if(i == k) break;
        }
        return topK;
    }

    public double pageRankOf(String vertex){
        return sortedRankingsMap.get(vertex);
    }
    
    public static void main(String[] args) throws IOException{
        String folder = "/Users/alex/Desktop/PA3_Data/WikiSportsGraph.txt";

        double epsilon = 0.001;
        double beta = 0.85;
        int k = 10;
        PageRank pr = new PageRank(folder, epsilon, beta);
        System.out.println(Arrays.toString(pr.topKPageRank(k)) + "\n");

        epsilon = 0.00001;
        beta = 0.85;
        pr = new PageRank(folder, epsilon, beta);
        System.out.println(Arrays.toString(pr.topKPageRank(k)) + "\n");
        
        epsilon = 0.001;
        beta = 0.5;
        pr = new PageRank(folder, epsilon, beta);
        System.out.println(Arrays.toString(pr.topKPageRank(k)) + "\n");
        
        epsilon = 0.00001;
        beta = 0.5;
        pr = new PageRank(folder, epsilon, beta);
        System.out.println(Arrays.toString(pr.topKPageRank(k)) + "\n");

    }
}
