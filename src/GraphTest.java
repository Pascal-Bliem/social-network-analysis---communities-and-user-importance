import java.util.List;
import java.util.Map;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GraphTest {

	/*
	 * This class is specifically for testing the functionality of other 
	 * classes in this project. 
	 *  
	 */
	
	public static void main(String[] args) throws InterruptedException, IOException {
		// start tests for correctness and performance
		
		// Uncomment this block if you want to write console output to file
		/*
		PrintStream out;
		try {
			out = new PrintStream(new FileOutputStream("data/TestGraphs/testlog.txt"));
			System.setOut(out);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		*/
		
		// make a new graph tester and start the tests
		GraphTest tester = new GraphTest();	
		System.out.println("STARTING TESTS");
		//tester.correctnessTest();
		tester.performanceTest();
		System.out.println("FINISHED");
	}
	
	private void performanceTest() throws IOException {
	    // test the algorithms used in this project with differently sized graphs
		// from 100 to 3000 vertices in steps of 100 (number of edges increases
		// quadratically). In-degree centrality, Page-Rank, and Tarjan SCC will be
		// tested 50 times for each graph to get better statistics, Girvan Newman
		// community detection, however, will only be tested 3 times for each 
		// graph between 100 annd 1000 nodes because the algorithm's higher 
		// complexity (it takes too long :) ).
		
		List<String> files = new LinkedList<String>(); 
		for (int i = 100; i < 3001; i += 100) {
			files.add("data/TestPerformance/TestPerformance"+i+".txt");
		}
	    
		// test in-degree centrality
		System.out.println("Entering in-degree centrality");
		BufferedWriter writer = new BufferedWriter(new FileWriter("data/TestPerformance/IDClog.txt"));
		for(String filename : files) {
			LinkedList<Long> times = new LinkedList<Long>();
			Graph graph = new Graph();
			GraphLoader.loadGraph(graph, filename);
			
			for(int i = 0; i < 50; i++) {
				long startTime = System.nanoTime();
				graph.getInDegreeCentrality();
				long endTime = System.nanoTime();
				long deltaTime = endTime - startTime;
				times.add(deltaTime);
			}
			System.out.println(times);
			for(long t : times) {
				writer.write(Long.toString(t)+",");	
			}
			writer.write("\n");
		}
		writer.close();
		
		// test page rank
		System.out.println("Entering Page Rank");
		writer = new BufferedWriter(new FileWriter("data/TestPerformance/PRlog.txt"));
		for(String filename : files) {
			LinkedList<Long> times = new LinkedList<Long>();
			Graph graph = new Graph();
			GraphLoader.loadGraph(graph, filename);
			
			for(int i = 0; i < 50; i++) {
				long startTime = System.nanoTime();
				graph.getPageRank();
				long endTime = System.nanoTime();
				long deltaTime = endTime - startTime;
				times.add(deltaTime);
			}
			System.out.println(times);
			for(long t : times) {
				writer.write(Long.toString(t)+",");	
			}
			writer.write("\n");
		}
		writer.close();
		
		// test tarjan scc
		System.out.println("Entering Tarjan SCC");
		writer = new BufferedWriter(new FileWriter("data/TestPerformance/SCClog.txt"));
		for(String filename : files) {
			LinkedList<Long> times = new LinkedList<Long>();
			Graph graph = new Graph();
			GraphLoader.loadGraph(graph, filename);
			
			for(int i = 0; i < 50; i++) {
				long startTime = System.nanoTime();
				graph.getSCCs(true);
				long endTime = System.nanoTime();
				long deltaTime = endTime - startTime;
				times.add(deltaTime);
			}
			System.out.println(times);
			for(long t : times) {
				writer.write(Long.toString(t)+",");	
			}
			writer.write("\n");
		}
		writer.close();
		
		// test Girvan-Newman
		System.out.println("Entering Girvan-Newman");
		writer = new BufferedWriter(new FileWriter("data/TestPerformance/GNlog.txt"));
		for(int x = 0; x<10; x++) {
			String filename = files.get(x);
			LinkedList<Long> times = new LinkedList<Long>();
			Graph graph = new Graph();
			GraphLoader.loadGraph(graph,filename);
			
			System.out.println(filename);
			for(int i = 0; i < 3; i++) {
				long startTime = System.nanoTime();
				graph.getCommunities(1);
				long endTime = System.nanoTime();
				long deltaTime = endTime - startTime;
				times.add(deltaTime);
				System.out.println("loop number "+(i+1));
			}
			
			for(long t : times) {
				writer.write(Long.toString(t)+",");	
			}
			writer.write("\n");
		}
		writer.close();		
	}
	
	
	private boolean correctnessTest() {
		boolean passed = true;
			
		// Tests with 200 node graph
		// graphs is directed
		Graph graph = this.testLoadGraph("data/TestGraphs/TestGraph200.txt");
		passed = this.testInDegreeCentrality(graph,"data/TestGraphs/idc200.txt");
		passed = this.testPageRank(graph,"data/TestGraphs/pr200.txt");

		// Tests with 2000 node graph
		// graphs is directed
		graph = this.testLoadGraph("data/TestGraphs/TestGraph2000.txt");
		passed = this.testInDegreeCentrality(graph,"data/TestGraphs/idc2000.txt");
		passed = this.testPageRank(graph,"data/TestGraphs/pr2000.txt");

		// Tests Tarjan SCC algorithm with 200 node graph with edge probabilities
		// or p = 0.015 and 0.01, graphs are directed
		graph = new Graph();
		GraphLoader.loadGraph(graph, "data/TestGraphs/TestGraph0.015.txt");
		passed = this.testTarjanSCC(graph, "data/TestGraphs/scc0.015.txt");

		graph = new Graph();
		GraphLoader.loadGraph(graph, "data/TestGraphs/TestGraph0.01.txt");
		passed = this.testTarjanSCC(graph, "data/TestGraphs/scc0.01.txt");

		// Tests multi-path BFS algorithm with 50 node 132 edges graph with edge probability 0.1
		// the graph is connected and undirected 
		graph = this.testLoadGraph("data/TestGraphs/TestGraphSP50.txt");
		passed = this.testShortestPaths(graph,"data/TestGraphs/SP50.txt");

		// Tests for edge betweenness centrality algorithm
		// 50 node 132 edges graph with edge probability 0.1
		// the graph is connected and undirected
		graph = this.testLoadGraph("data/TestGraphs/TestGraphSP50.txt");
		passed = this.testEdgeBetweennessCentrality(graph,"data/TestGraphs/EBC50.txt");
		
		// Tests Girvan Newmann algorithm for community detection with 200 node 
		// 604 edges graph with edge probability 0.015, graph is directed
		graph = this.testLoadGraph("data/TestGraphs/TestGraphGN200.txt");
		passed = this.testGetCommunities(graph,"data/TestGraphs/GN200.txt");
		
		return passed;
	}
	
	private Graph testLoadGraph(String filename) {
		System.out.println("ENTERING LOAD GRAPH TEST");
		// pass yes or not, to be returned
		boolean passed = true;
		
		Graph graph = new Graph();
        GraphLoader.loadGraph(graph, filename);
        
        System.out.println("Graph loaded "+filename);
        
        //  check if right amount of vertices and edges were parsed
        int vertCountExp = 0;
        int edgeCountExp = 0;
        if (filename.equals("data/TestGraphs/TestGraph200.txt")) {
        	vertCountExp = 200;
        	edgeCountExp = 6045;
        }
        else if(filename.equals("data/TestGraphs/TestGraph2000.txt")) {
        	vertCountExp = 2000;
        	edgeCountExp = 599569;
        }
        else if (filename.equals("data/TestGraphs/TestGraph0.015.txt")) {
        	vertCountExp = 200;
        	edgeCountExp = 604;
        }
        else if (filename.equals("data/TestGraphs/TestGraphSP.txt")) {
        	vertCountExp = 10;
        	edgeCountExp = 15;
        }
        else if (filename.equals("data/TestGraphs/TestGraphSP2.txt")) {
        	vertCountExp = 11;
        	edgeCountExp = 10;
        }
        else if (filename.equals("data/TestGraphs/TestGraphSP50.txt")) {
        	vertCountExp = 50;
        	edgeCountExp = 132;
        }
        else if (filename.equals("data/TestGraphs/TestGraphGN200.txt")) {
        	vertCountExp = 200;
        	edgeCountExp = 604;
        }
        
        int vertCount = graph.getVertexCount();
        int edgeCount = graph.getEdgeCount();
        
        System.out.println("Nodes "+vertCount);
        System.out.println("Edges "+edgeCount);
        
        if(vertCountExp==vertCount && edgeCount==edgeCountExp) {
        	System.out.println("PASSED");
        }
        else {
        	passed = false;
        	System.out.println("FAILED");
        	System.out.println("Expexted Nodes "+vertCountExp);
            System.out.println("Expected Edges "+edgeCountExp);
            passed = false;
        }
        
        //  final pass statement
        if (!passed) {
        	System.out.println("THERE WAS A MISMATCH --- PLEASE CHECK");
        }
        else {
        	System.out.println("LOAD GRAPH TEST PASSED");
        }
		return graph;
	}
	
	private boolean testInDegreeCentrality(Graph graph, String filename) {
		System.out.println("ENTERING IDC TEST");
		// pass yes or not, to be returned
		boolean passed = true;
		
        // compare IDCs, calculated vs test
        System.out.println("Calculating In-degree centralities...");
        HashMap<Integer, Double> IDCs = graph.getInDegreeCentrality();
        HashMap<Integer, Double> testIDCs = new HashMap<Integer, Double>();
        
        // scan a test file
        Scanner sc;
        try {
            sc = new Scanner(new File(filename));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        // Iterate over the lines in the file and add vertices and their IDC to testIDCs
        while(sc.hasNextInt()) {
        	int v = sc.nextInt();
            double idc = sc.nextDouble();
            testIDCs.put(v, idc);
        }
        sc.close();
        // print IDCs and compare if approximately equal
        System.out.println("In-degree centralities: ");
        
        for(int v : IDCs.keySet()) {
        	if (IDCs.get(v)-testIDCs.get(v) > 0.0001) {
        		System.out.println("MISMATCH --- PLEASE CHECK");
        		passed = false;
        	}
        	System.out.println(v+" "+IDCs.get(v)+" "+testIDCs.get(v));
        }
        
        // final pass statement
        if (!passed) {
        	System.out.println("THERE WAS A MISMATCH --- PLEASE CHECK");
        }
        else {
        	System.out.println("IDC TEST PASSED");
        }
        
        return passed;
	}
	
	private boolean testPageRank(Graph graph, String filename) {
		System.out.println("ENTERING PAGE RANK TEST");
		// pass yes or not, to be returned
		boolean passed = true;
		
        // compare page rank, calculated vs test
        System.out.println("Calculating Page-Rank scores...");
        HashMap<Integer, Double> PRs = graph.getPageRank();
        HashMap<Integer, Double> testPRs = new HashMap<Integer, Double>();
        
     // scan a test file
        Scanner sc;
        try {
            sc = new Scanner(new File(filename));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        // Iterate over the lines in the file and add vertices and their PRs to testPRs
        while(sc.hasNextInt()) {
        	int v = sc.nextInt();
            double pr = sc.nextDouble();
            testPRs.put(v, pr);
        }
        sc.close();
        
        // print PRs and compare if approximately equal
        System.out.println("Page-Rank scores: ");
        
        for(int v : PRs.keySet()) {
        	if (PRs.get(v)-testPRs.get(v) > 0.001) {
        		System.out.println("MISMATCH --- PLEASE CHECK");
        		passed = false;
        	}
        	System.out.println(v+" "+PRs.get(v)+" "+testPRs.get(v));
        }
        
        // final pass statement
        if (!passed) {
        	System.out.println("THERE WAS A MISMATCH --- PLEASE CHECK");
        }
        else {
        	System.out.println("PAGE RANK TEST PASSED");
        }
		return passed;
	}
	
	private boolean testTarjanSCC(Graph graph, String filename) {
		System.out.println("ENTERING TARJAN SCC TEST");
		// pass yes or not, to be returned
		boolean passed = true;
		
		// read SCC test file
		Scanner sc;
        try {
            sc = new Scanner(new File(filename));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        // save test SCC in hash sets
        HashSet<HashSet<Integer>> testSCCs = new HashSet<HashSet<Integer>>();
        
        // Iterate over the lines in the file and add all vertices
        // belonging to one SCC into a separate hash set
        while(sc.hasNext()) {
        	HashSet<Integer> sccComp = new HashSet<Integer>();
        	testSCCs.add(sccComp);
        	
        	String[] line = sc.nextLine().split(" "); 
        	for (int i = 0; i<line.length;i++) {
        		sccComp.add(Integer.parseInt(line[i]));
        	}         
        }
        sc.close();
        // count number of test SCCs
        int numTestSCCs = testSCCs.size();
        System.out.println("Test SCCs found: "+numTestSCCs);
        
        // get the SCCs (as calculated by the Tarjan algorithm) and store 
        // them in hash sets as well
        List<Graph> graphSCCs = graph.getSCCs();
        HashSet<HashSet<Integer>> SCCs = new HashSet<HashSet<Integer>>();
        
        for(Graph g : graphSCCs) {
            HashMap<Integer, HashSet<Integer>> curr = g.exportGraph();
            HashSet<Integer> scc = new HashSet<Integer>();
            for (Map.Entry<Integer, HashSet<Integer>> entry : curr.entrySet()) {
                scc.add(entry.getKey());
            }
            SCCs.add(scc);
        }
        
        // count number of calculated SCCs and compare to number of test SCCs
        int numSCCs = SCCs.size();
        System.out.println("SCCs found: "+numSCCs);
        
        if(numTestSCCs!=numSCCs) {
        	passed = false;
        	System.out.println("MISMATCH -- UNEVEN NUMBER OF TESTSCC AND SCC FOUND");
        }
        
        // check if the calculated and test SCCs contain the same vertices
        int numMatch = 0;
        for (HashSet<Integer> scc : SCCs){
        	for (HashSet<Integer> testscc : testSCCs){
        	    if(scc.equals(testscc)) {
        		numMatch++;
        	    }
        	}
        }
        // check if total number and matching number of SCCs is the same
        if(numSCCs!=numMatch) {
        	passed = false;
        	System.out.println("MISMATCH -- NOT ALL SCCs SEEM TO MATCH");
        }
        System.out.println("Number of SCC matches: "+numMatch);
        
        // final pass statement
        if (!passed) {
        	System.out.println("THERE WAS A MISMATCH --- PLEASE CHECK");
        }
        else {
        	System.out.println("SCC TEST PASSED");
        }
        
		return passed;
	}
	
	private boolean testShortestPaths(Graph graph, String filename) {
		// test if all calculated shortest paths (SP) for all node combinations in an 
		// undirected graph are correct
		System.out.println("ENTERING SP TEST");
		boolean passed = true;
		
		// get graph edges and create a GirvanNewman objectt which will find the SPs
		HashMap<Integer, HashSet<Integer>> edges = graph.exportUndirGraph();
		GirvanNewman gm = new GirvanNewman(edges);
        
		System.out.println("Loading test SPs from file...");
		// save test shortest paths in hash sets
		HashSet<HashSet<Integer>> testSPs = new HashSet<HashSet<Integer>>();
		// read SP test file
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(filename));
			String line = reader.readLine();
			line.trim();
	        // Iterate over the lines in the file and add all vertices
	        // belonging to one SP into a separate hash set
			while (line != null) {
				line.trim();
				// read next line
				HashSet<Integer> SP = new HashSet<Integer>();
	        	testSPs.add(SP);
	        	
	        	String[] nums = line.split(" "); 
	        	for (int i = 0; i<nums.length;i++) {
	        		SP.add(Integer.parseInt(nums[i]));
	        	}         
				line = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
			
		// how many SPs were read from file
        int numTestSPs = testSPs.size();
        System.out.println("Test SPs found: "+numTestSPs);
        
        // get the SPs (as calculated by BFS) and store 
        // them in hash sets as well
        List<List<Integer>> SPlist = gm.shortestPaths();
        HashSet<HashSet<Integer>> allSPs = new HashSet<HashSet<Integer>>();
        
        // change from list to hash set for each SP to facilitate comparison later
        for(List<Integer> verts : SPlist) {
            HashSet<Integer> sp = new HashSet<Integer>();
            sp.addAll(verts);
            allSPs.add(sp);
        }
        
        // count number of calculated SPs and compare to number of test SPs
        int numSPs = allSPs.size();
        System.out.println("SPs found: "+numSPs);
        
        if(numTestSPs!=numSPs) {
        	passed = false;
        	System.out.println("MISMATCH -- UNEVEN NUMBER OF TEST SP AND SP FOUND");
        }
        
        // check if the calculated and test SPs contain the same vertices
        int numMatch = 0;
        for (HashSet<Integer> sp : allSPs){
        	for (HashSet<Integer> testsp : testSPs){
        	    if(sp.equals(testsp)) {
        		numMatch++;
        	    }
        	}
        }
        // check if total number and matching number of SPs is the same
        if(numSPs!=numMatch) {
        	passed = false;
        	System.out.println("MISMATCH -- NOT ALL SPs SEEM TO MATCH");
        }
        System.out.println("Number of SP matches: "+numMatch);
        
        // final pass statement
        if (!passed) {
        	System.out.println("THERE WAS A MISMATCH --- PLEASE CHECK");
        }
        else {
        	System.out.println("SP TEST PASSED");
        }
		return passed;
	}
	
	private boolean testEdgeBetweennessCentrality(Graph graph, String filename) {
		// test if calculated edge betweenness is correct for all edges 
		// 
		System.out.println("ENTERING EBC TEST");
		boolean passed = true;
		
		System.out.println("Loading test EBCs from file...");
		// save test EBCs in hash maps
		HashMap<Integer, HashMap<Integer, Double>> testEBCs = new HashMap<Integer, HashMap<Integer,Double>>();
		// read EBCs test file
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(filename));
			String line = reader.readLine();
			line.trim();
	        // Iterate over the lines in the file 
			while (line != null) {
				line.trim();
				// read next line
	        
	        	String[] nums = line.split(" "); 
	        	int source = Integer.parseInt(nums[0]);
	        	int target = Integer.parseInt(nums[1]);
	        	double ebc = Double.parseDouble(nums[2]);
	        	
	        	if(!testEBCs.containsKey(source)){
					testEBCs.put(source, new HashMap<Integer,Double>());
				}
	        	if(!testEBCs.get(source).containsKey(target)){
					testEBCs.get(source).put(target, ebc);
				}
	        	
				line = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
			
		// get the calculated EBCs from the GirvanNewman class 
        HashMap<Integer, HashSet<Integer>> edges = graph.exportUndirGraph();
		GirvanNewman gm = new GirvanNewman(edges);
		gm.edgeBetweennessCentrality();
		HashMap<Integer, HashMap<Integer, Double>> EBCs = gm.getEBC();
		
        // check if the calculated and test EBCs match
        for (int source : testEBCs.keySet()){
        	for (int target : testEBCs.get(source).keySet()){
        		double ebc = EBCs.get(source).get(target);
        		double testebc = testEBCs.get(source).get(target);
        		if (testebc-ebc > 0.001) {
            		System.out.println("MISMATCH --- PLEASE CHECK");
            		passed = false;
            	}
            	System.out.println(source+" "+target+" "+ebc+" "+testebc);
        	}
        }
        
        // final pass statement
        if (!passed) {
        	System.out.println("THERE WAS A MISMATCH --- PLEASE CHECK");
        }
        else {
        	System.out.println("EBC TEST PASSED");
        }
		return passed;
	}
	
	private boolean testGetCommunities(Graph graph, String filename) {
		// test to check the communities extracted by the Girvan Newmann algorithm 
		// 
		System.out.println("ENTERING GIRVAN NEWMANN COMMUNITY TEST");
		boolean passed = true;
	
		// get the SCCs first  
		System.out.println("check for SCCs present in graph before removing any edge");
		List<Graph> graphSCC = graph.getSCCs(true); // true -> graph is directed
		
		// print SCCs
		//for(Graph g : graphSCC) {
        //   HashMap<Integer, HashSet<Integer>> curr = g.exportUndirGraph();
        //    System.out.println(curr);
		//}
		System.out.println("SCCs found: "+graphSCC.size());
		
		System.out.println("Reading test communities from file...");
		HashSet<HashSet<Integer>> testComs = new HashSet<HashSet<Integer>>();
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(filename));
			String line = reader.readLine();
			line.trim();
	        // Iterate over the lines in the file 
			while (line != null) {
				line.trim();
				// read next line
				HashSet<Integer> com = new HashSet<Integer>();
	        	testComs.add(com);
	        	String[] nums = line.split(" "); 
	        	 
	        	for (int i = 0; i<nums.length;i++) {
	        		com.add(Integer.parseInt(nums[i]));
	        	}
				line = reader.readLine();
			}
			reader.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		int numTestComs = testComs.size();
        System.out.println("Test Communities found: "+numTestComs);
		
       
		
		
		
		// testing the GN community finder only on the biggest SCC
		System.out.println("Testing the GN community finder only on the biggest SCC of the original graph");
		int k = 4;
		System.out.println("Removing edges until found communities = "+k);
		int e = 1;
		System.out.println("Removing edges at a time = "+e);
		
		// get the communities with the GN algorithm 
		List<Graph> graphCom = graph.getCommunitiesFromBiggestSCC(k,e);
		HashSet<HashSet<Integer>> coms = new HashSet<HashSet<Integer>>();
        
        for(Graph g : graphCom) {
            HashMap<Integer, HashSet<Integer>> curr = g.exportUndirGraph();
            //System.out.println(curr);
            HashSet<Integer> com = new HashSet<Integer>();
            for (Map.Entry<Integer, HashSet<Integer>> entry : curr.entrySet()) {
                com.add(entry.getKey());
            }
            coms.add(com);
        }
        
        // count number of calculated communities and compare to number of test communities
        int numComs = coms.size();
        System.out.println("Communities found: "+numComs);
        
        if(numTestComs!=numComs) {
        	passed = false;
        	System.out.println("MISMATCH -- UNEQUAL NUMBER OF TEST COMMUNITIES AND COMMUNITIES FOUND");
        }
		
        // check if the calculated and test communities contain the same vertices
        int numMatch = 0;
        for (HashSet<Integer> com : coms){
        	for (HashSet<Integer> testcom : testComs){
        	    if(com.equals(testcom)) {
        		numMatch++;
        	    }
        	}
        }
        // check if total number and matching number of communities is the same
        if(numComs!=numMatch) {
        	passed = false;
        	System.out.println("MISMATCH -- NOT ALL COMMUNITIES SEEM TO MATCH");
        }
        System.out.println("Number of community matches: "+numMatch);
        
        // final pass statement
        if (!passed) {
        	System.out.println("THERE WAS A MISMATCH --- PLEASE CHECK");
        }
        else {
        	System.out.println("GIRAVAN NEWMANN COMMUNITY DETECTION TEST PASSED");
        }
		return passed;
	}
}
