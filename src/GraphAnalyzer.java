import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class GraphAnalyzer {

	public static void main(String[] args) throws IOException {
		
		/*
		Graph graph = new Graph();
		System.out.println("Calculating IDCs for twitter data");
        GraphLoader.loadGraph(graph, "data/Analysis/twitter_combined.txt");
        BufferedWriter writer = new BufferedWriter(new FileWriter("data/Analysis/twitterIDCs.txt"));
        HashMap<Integer, Double> IDCs = graph.getInDegreeCentrality();
        for (Map.Entry<Integer, Double> e : IDCs.entrySet()) {
        	writer.write(Integer.toString(e.getKey())+","+Double.toString(e.getValue())+"\n");	
        }
        writer.close();
        
        System.out.println("Calculating PRs for twitter data");
        writer = new BufferedWriter(new FileWriter("data/Analysis/twitterPRs.txt"));
        HashMap<Integer, Double> PRs = graph.getPageRank();
        for (Map.Entry<Integer, Double> e : PRs.entrySet()) {
        	writer.write(Integer.toString(e.getKey())+","+Double.toString(e.getValue())+"\n");	
        }
        writer.close();
        
        writer = new BufferedWriter(new FileWriter("data/Analysis/twitterIDCPR.txt"));
        for (int i : IDCs.keySet()) {
        	writer.write(Integer.toString(i)+","+Double.toString(IDCs.get(i))+","+Double.toString(PRs.get(i))+"\n");
        }
        writer.close();
        */
		
		Graph graph = new Graph();
		System.out.println("Community detection");
		GraphLoader.loadGraph(graph, "data/Analysis/facebook_1000.txt");
		List<Graph> coms = graph.getCommunitiesFromBiggestSCC(30, 1);
		BufferedWriter writerAll = new BufferedWriter(new FileWriter("data/Analysis/fbComAll.txt"));
		for(int i=0;i< coms.size();i++) {
			Graph g = coms.get(i);
			HashMap<Integer, HashSet<Integer>> verts =  g.exportGraph();
			BufferedWriter writer = new BufferedWriter(new FileWriter("data/Analysis/fbCom"+i+".txt"));
			for(int s : verts.keySet()) {
				for(int t : verts.get(s)){
					writer.write(s+" "+t+"\n");
					writerAll.write(s+" "+t+"\n");
				}
			}
			writer.close();
			System.out.println("Community size: "+verts.keySet().size());
			System.out.println(verts.keySet());
		}
		writerAll.close();
	}
}
