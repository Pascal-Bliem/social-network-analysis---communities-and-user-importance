import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/*
 * This class represents a graph data structure for a directed social network (e.g. Twitter)
 * with integer nodes and no attributes for vertices (except integer ID) and edges.
 * Edges are stored in adjacency lists (implemented as HashMap<VertexID, HashSet<EdgeTargetID>>)
 */
public class Graph {
	
	// store the nodes and their edges in hash maps in both 
	// a directed and undirected version
	private HashMap<Integer, HashSet<Integer>> vertices;
	private HashMap<Integer, HashSet<Integer>> undirected;
	// a constant for normalization of calculated properties (default = 1)
	private double norm;
	
	public Graph() {
		// constructor initializes field variable
		vertices = new HashMap<Integer, HashSet<Integer>>();
		undirected = new HashMap<Integer, HashSet<Integer>>();
		norm = 1.0;
	}// end of constructor Graph()
	
	public Graph(double norm) {
		this();
		this.norm = norm; 
	}
	
	public void addVertex(int num) {
		// add a new vertex to the graph
		
		// If the vertex is already in the graph, exit method,
		if (vertices.keySet().contains(num)) {
			return;
		}
		// else create a new vertex for the location and put it in the hash map
		else {
			vertices.put(num, new HashSet<Integer>());
			undirected.put(num, new HashSet<Integer>());
		}
		
	}// end of method addVertex()
	
	public void addEdge(int from, int to) throws IllegalArgumentException{
		// add a new edge to the graph
		
		// check if both source and target vertices are in the graph, if not throw exception
		if (!vertices.containsKey(from) || !vertices.containsKey(to)) {
			throw new IllegalArgumentException("At least one of the vertices is not in the graph.");
		}
		// get the source vertex from vertices and add a new edge to the target vertex
		vertices.get(from).add(to);
		// for the undirected representation, add the edge both ways
		undirected.get(from).add(to);
		undirected.get(to).add(from);
		
	}// end of method addEdge()
	
	/*
	 * Getter methods below
	 */
	
	public HashMap<Integer, Double> getInDegreeCentrality(){
	    // caluclate the in-degree centraity of each vertex 
		// (normalized with respect to number of vertices in the graph)	
	
		// store the value of each vertex in hash map
		HashMap<Integer, Double> inDegreeCentrality = new HashMap<Integer, Double>();
		// normalization constant is made smaller because it would otherwise 
		// make the in-degree centrality too small
		double normal = (getVertexCount()-1)* norm;
		
		// loop through all vertices
		for(HashSet<Integer> edges : vertices.values()) {
			// and through their edges' target vertices to get each incoming edge 
			for(int vertex : edges) {
				// if the target vertex has not occurred yet put it in the hash map and add 1 (normalized)
				if(!inDegreeCentrality.containsKey(vertex)) {
					inDegreeCentrality.put(vertex, 1.0/normal);
				}
				// if the target vertex is already in the map, take its current
				// value and add another 1 (normalized) to it
				else {
					double curr = inDegreeCentrality.get(vertex); 
					inDegreeCentrality.put(vertex, curr + 1.0/normal);
				}
			}
		}
		return inDegreeCentrality;
	}// end of method getInDegreeCentrality()
	
	public HashMap<Integer, Double> getPageRank(){
		return new PageRank(this.vertices,norm).calculatePageRank();
	}
	
	public List<Graph> getSCCs() {
		List<Graph> SCCs = getSCCs(true);
		return SCCs;
	}
	
	public List<Graph> getSCCs(boolean directed) {
		// Finding the strongly connected components of the Graph with the 
		// recursive Tarjan algorithm
		
		// create a Tarjan algorithm object and let it get the SCCs
		// Note: This method is recursive and for very large graphs, hence very
		// deep recursion, it will require a lot of stack memory. 
		// If a stack overflow occurs, try increasing stack size or implement the algorithm 
		// with its own stack.
		TarjanSCC tarjan;
		if(directed) {
			tarjan = new TarjanSCC(vertices);
		}
		else {
			tarjan = new TarjanSCC(undirected);
		}
		tarjan.run();
		List<HashSet<Integer>> sccList = tarjan.getConnectedComponentList();
		
		// construct graphs representing the strongly connected components form 
		// the SCC list returned by the Tarjan algorithm and put them in a list of graphs
		List<Graph> SCCs = new LinkedList<Graph>();
		for (HashSet<Integer> scc : sccList) {
			// one new graph for each SCC
			Graph sccGraph = new Graph();
			for (int s : scc) {
				sccGraph.addVertex(s);
				for(int t : scc) {
					sccGraph.addVertex(t);
					if (this.vertices.get(s).contains(t)) {
						sccGraph.addEdge(s, t);
					}
				}
			}
			// TODO: reconstruct edges for the SCCs from the original graph as well
			// add the SCC to list of SCCs
			SCCs.add(sccGraph);
		}
		
		return SCCs;
	}// end of method getSCCs()
	
	

	public List <Graph> getCommunities(HashMap<Integer, HashSet<Integer>> undirEdges, int k, int e){
		
		// list of communities to be returned
		List <Graph> communities = new LinkedList<Graph>();
		
		HashMap<Integer, HashSet<Integer>> edges = undirEdges;
		
		// loop until number k communities is reached
		int numIter = 0;
		int numCom = 0;	
		while(numCom < k) {
			numIter++;
			System.out.println("Girvan Newman iteraion: "+numIter);
			// remove the highest EBC edge with the Girvan Newmann algorithm
			edges = new GirvanNewman(edges).removeEdge(e);

			// create a new graph from the new edges
			Graph newGraph = new Graph();
			for (int source : edges.keySet()) {
				newGraph.addVertex(source);
				for (int target : edges.get(source)) {
					newGraph.addVertex(target);
					newGraph.addEdge(source, target);
				}
			}
			// get the SCCs of the new graph which each represent one community after removing an edge
			communities = newGraph.getSCCs(); 
			numCom = communities.size();
			System.out.println("Communities so far: "+numCom);
		}
		return communities;
	}
    
	public List <Graph> getCommunities(int k, int e){
		return getCommunities(undirected,k,e);
	}
	
	public List <Graph> getCommunities(int k){
		int e = 1;
		return getCommunities(undirected,k,e);
	}
	
	public List <Graph> getCommunitiesFromBiggestSCC(int k, int e){
		// only the biggest SCC of the original graph will be considered for the 
		// community search
		
		// get SCCs of graph
		TarjanSCC tarjan = new TarjanSCC(vertices);
		tarjan.run();
		List<HashSet<Integer>> sccList = tarjan.getConnectedComponentList();
		HashSet<Integer> biggestSCC = sccList.get(0);
		// find the biggest
		for(HashSet<Integer> scc : sccList) {
			if (scc.size()>biggestSCC.size()) {
				biggestSCC = scc;
			}
		}
		//System.out.println(biggestSCC);
		// extract the edges belonging to this biggest SCC from the original graph
		HashMap<Integer, HashSet<Integer>> SCCedges = new HashMap<Integer, HashSet<Integer>>(undirected);
		for (int v : undirected.keySet()) {
			if(!biggestSCC.contains(v)) {
				SCCedges.remove(v);
				for(HashSet<Integer> es : SCCedges.values()) {
					es.remove(v);
				}
			}
			
		}
		//System.out.println(SCCedges);
		List<Graph> com = getCommunities(SCCedges,k,e);
		
		return com;
	}
	
	public HashMap<Integer, HashSet<Integer>> exportGraph() {
		return vertices;
	}// end of method exportGraph()
	
	public HashMap<Integer, HashSet<Integer>> exportUndirGraph() {
		return undirected;
	}// end of method exportUndirGraph()
	
	public int getVertexCount() {
		return vertices.keySet().size();
	}// end of method getVertexCount()
	
	public int getEdgeCount() {
		int count = 0;
		for (HashSet<Integer> edges : vertices.values()) {
			count += edges.size();
		}
		return count;
	}// end of method getEdgeCount()
	
	public int getUndirEdgeCount() {
		int count = 0;
		for (HashSet<Integer> edges : undirected.values()) {
			count += edges.size();
		}
		return count;
	}// end of method getUndirEdgeCount()
	
}// end of class Graph