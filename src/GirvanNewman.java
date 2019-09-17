import java.util.List;
import java.util.PriorityQueue;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;




public class GirvanNewman {

	// the edges in the graph
	private HashMap<Integer, HashSet<Integer>> edges;
	// number of shortest paths from one vertex i to another vertex j like
	// HashMap< i, HashMap< j, number of SPs>>
	private HashMap<Integer, HashMap<Integer, Integer>> numSP;
	// edge betweenness of edge(i,j) with source vertex i and target vertex j like   
	// HashMap< i, HashMap< j, edge betweenness centrality>>
	private HashMap<Integer, HashMap<Integer, Double> > EBCs;
	
	// store the edges EBC value, source and target vertices in a priority queue
	// sorted according to their EBC value, this allows to remove several edges 
	// per iteration which may be much faster for large graphs
	private PriorityQueue<double[]> queueEBC;
	//number of edges to remove per iteration
	int e;
	
	public GirvanNewman(HashMap<Integer, HashSet<Integer>> edges) {
		// constructor initializes field variables
		this.edges = edges;
		this.numSP = new HashMap<Integer, HashMap<Integer,Integer>>();
		this.EBCs = new HashMap<Integer, HashMap<Integer,Double>>();
		this.queueEBC = new PriorityQueue<double[]>(new EBCComparator());
		this.e = 1;
	}// end of constructor GirvanNewman()

	public HashMap<Integer, HashSet<Integer>> removeEdge(int e){
		this.e = e;
		return removeEdge();
	}// end of constructor GirvanNewman() with parameter e
	
	public HashMap<Integer, HashSet<Integer>> removeEdge(){
		
		// updated edges to be returned after removal of highest EBC edge 
		HashMap<Integer, HashSet<Integer>> newEdges = new HashMap<Integer, HashSet<Integer>>(edges);
		
		// calculate EBC
		edgeBetweennessCentrality();
		
		// find the e highest EBC edges by adding them all to a priority queue
		HashSet<Integer> checked = new HashSet<Integer>();
		// loop through all source vertices 
		for(int source : EBCs.keySet()) {	
			// add current vertex to checked set
			checked.add(source);
			// loop over all possible target vertices
			for(int target : EBCs.keySet()) {
				if(EBCs.get(source).keySet().contains(target)&!checked.contains(target)) {
					// add each EBC values and edge source and target to an array and then
					// to a priority queue
					double[] ebcEdge = new double[3];
					ebcEdge[0] = EBCs.get(source).get(target);
					ebcEdge[1] = (double) source;
					ebcEdge[2] = (double) target;
					queueEBC.add(ebcEdge);
				}
			}
		}
		
		// delete e edges with highest EBC value
		for(int i = 0; i<e;i++) {
			// get the next highest ebc edge in the queue
			double[] edge = queueEBC.remove();
			double ebc = edge[0];
			int edgeSource = (int) edge[1];
			int edgeTarget = (int) edge[2];
			// remove the edge both ways
			newEdges.get(edgeSource).remove(edgeTarget);
			newEdges.get(edgeTarget).remove(edgeSource);
			System.out.println("Edge removed "+" "+edgeSource+" "+edgeTarget+" "+ebc);
		}
		return newEdges;
	}
	
	// comparator that compares edges according to their EBC value,
	// used for storing the edges in a sorted way in a priority queue
	private class EBCComparator implements Comparator<double[]> {
		@Override
	    public int compare(double[] x, double[] y) {
			if (x[0] > y[0]) {
	            return -1;
	        }
	        if (x[0] < y[0]) {
	            return 1;
	        }
	        return 0;
		}
	}
	
	
	public void edgeBetweennessCentrality() {
	    // calculate the edge betweenness centrality for all edges
		
		// get all shortest paths in the graph
		List<List<Integer>> sp = shortestPaths();
	
		// loop through all paths
		for (List<Integer> path : sp) {
			// to account for how much this path contributes to the EBC of an edge, 
			// we have to divide it by the number of all shortest paths from its 
			// source to its target vertex ( basically a normalization to ensure that 
			// each source-target-pair of vertices contributes only a total summed 
			// value of 1 to the whole calculation) 
			double div = (double) numSP.get(path.get(0)).get(path.get(path.size()-1));
			
			// loop through all edge-source-edge-target pairs on this path
			for(int i=0; i<path.size()-1;i++) {
				int edgeSource = path.get(i);
				int edgeTarget = path.get(i+1);
				
				// if this edge (edge-source-edge-target pair) is not yet in the EBCs
				// map, add it (both ways) and initialize its EBC value to 0.0
				if(!EBCs.containsKey(edgeSource)){
					EBCs.put(edgeSource, new HashMap<Integer,Double>());
				}
				if(!EBCs.get(edgeSource).containsKey(edgeTarget)){
					EBCs.get(edgeSource).put(edgeTarget, 0.0);
				}		
				if(!EBCs.containsKey(edgeTarget)){
					EBCs.put(edgeTarget, new HashMap<Integer,Double>());
				}
				if(!EBCs.get(edgeTarget).containsKey(edgeSource)){
					EBCs.get(edgeTarget).put(edgeSource, 0.0);
				}
				
				// get the current EBC value of the edge and add another
				// + 1 / div ,where div is the amount of all shortest paths 
				// form the source to the target vertex of this path 
				// (the whole path, not just this edge!)
				double curr = EBCs.get(edgeSource).get(edgeTarget) + (1.0/div);
				EBCs.get(edgeSource).put(edgeTarget, curr);
				EBCs.get(edgeTarget).put(edgeSource, curr);
				
			}
		}
	}// end of method edgeBetweennessCentrality()
	
	public List<List<Integer>> shortestPaths() {
		// this method finds all shortest paths (if there are any) 
		// for all vertex combinations in an undirected graph 
		
		// make a progress counter to be printed (for testing)
		int numVerts = edges.keySet().size();
		double counter = 0.0;
		
		// list of all shortest paths in the graph (to be returned)
		List<List<Integer>> shortestPaths = new LinkedList<List<Integer>>();
		
		// keep vertices from which a search was already performed to not 
		// go through both directions as we are assuming an undirected graph 
		HashSet<Integer> checked = new HashSet<Integer>();
		// loop through all source vertices 
		for(int source : edges.keySet()) {
			// print the progress (for testing)
			counter += 1.0/numVerts;
			System.out.println("Progress on shortest paths: "+counter);
			
			// add current vertex to checked set
			checked.add(source);
			// create an entry in the numSP for counting paths from this node
			numSP.put(source, new HashMap<Integer, Integer>());
			// loop over all possible target vertices
			for(int target : edges.keySet()) {
				if(!checked.contains(target)) {
					// create an entry in the numSP for counting paths to this node
					numSP.get(source).put(target, 0);
					
					// conducted a BFS to find all shortest paths from source to 
					// target and add them to the list of all paths
					shortestPaths.addAll(bfs(source, target));
				}
			}
		}
		// return list of all shortest paths
		return shortestPaths;
	}// end of method shortestPaths()
	
	public List<List<Integer>> bfs(int source, int target){
		// This is a breath first search (BFS) to find all possible shortest 
		// paths between a source and target vertex. In contrast to a simple BFS
		// it keeps track of the current path lengths and allows to revisit a 
		// vertex if it is on a equally long path so that several parent vertices
		// can be added for each vertex and several shortest paths 
		// (if present) can be reconstructed.
		
		// hash map that holds every visited vertex as key and the level 
		// (path length of visit) as value
		HashMap<Integer, Integer> visited = new HashMap<Integer, Integer>();
		Queue<Integer> toExplore = new LinkedList<Integer>();
		HashMap<Integer, HashSet<Integer>> parentMap = new HashMap<Integer, HashSet<Integer>>();
		
		toExplore.add(source);
		// source node has path length 0
		visited.put(source,0);
		boolean found = false;
		
		// loop while there are still nodes in the queue
		while (!toExplore.isEmpty()) {
			// take first node from queue
			int curr = toExplore.remove();
			
			// if target found
			if (curr == target) {
				found = true;
				break;
			}
			
			// loop through all of curr's neighbors
			HashSet<Integer> neighbors = edges.get(curr);
			for(int next : neighbors) {
			    // if next has not yet been visited	
				if (!visited.containsKey(next)) {
					// put it in visited and set its path lenth one more 
					// than that of curr
					visited.put(next,visited.get(curr)+1);
					// add it to the queue and parent map
					toExplore.add(next);
					parentMap.put(next, new HashSet<Integer>());
					parentMap.get(next).add(curr);
				}
				else if (visited.get(curr) == visited.get(next)-1 ) {
					// if it has been visited already and the path length of curr
					// is in the same level as next's parent's path length,
					// add curr as next's parent but do not enqueue next again
					parentMap.get(next).add(curr);
				}
			}	
		}
		
		if (!found) {
			// if no path was found
			// System.out.println("No path exists");
			return new ArrayList<List<Integer>>();
		}
		// reconstruct paths from the parent map
		List<List<Integer>> paths = reconstructPaths(source, target, parentMap);
		// set the count in numSP of how many shortest paths exist from source to target
		numSP.get(source).put(target, paths.size());
		
		return paths;
	}// end of method bfs()
	
	private List<List<Integer>> reconstructPaths(int source, int target,HashMap<Integer, HashSet<Integer>> parentMap){
		// the list of shortest paths to be returned
		List<List<Integer>> paths = new LinkedList<List<Integer>>();
		
		// there should be at least one first path
		LinkedList<Integer> firstPath = new LinkedList<Integer>();
		
		// we'll have a stack for the vertices (since the paths may split if there 
		// is more than one shortest path) and a stack that keeps the current
		// path list for the current split-of-paths
		Stack<Integer> vertStack = new Stack<Integer>();
		vertStack.push(target);
		Stack<LinkedList<Integer>> listStack = new Stack<LinkedList<Integer>>();
		listStack.push(firstPath);
		
		// while the vertices stack is not empty
		while (!vertStack.isEmpty()) {  
			
			// the current list (current path) is the one from the top of the stack
			// note that it's not popped off, it remains on the top of the stack
			LinkedList<Integer> currList = listStack.get(listStack.size()-1);
			
			// pop the top vertex off the stack and make it curr
			int currVert = vertStack.pop();
			// add the curr vertex to the (front of) curr list
			currList.add(0,currVert);
			
			// if we reach the source vertex, the current path is over
			if(currVert==source) {
				// pop the current list containing the full path off the stack
				// and continue to the next iteration of the while loop 
				paths.add(listStack.pop());
				continue;
			}
			
			// if the current vertex is not the source, get its parents
			HashSet<Integer> parents = parentMap.get(currVert);
			// push each of its parents on the stack
			int pcount = 0;
			for (int p : parents) {
				// if there are more than one parent, create copies of the current
				// list to account for the split of paths and push these copies on
				// top of the list stack
				if(pcount>0) {
					listStack.push(new LinkedList<Integer>(currList));
				}
				vertStack.push(p);
				pcount++;
			}
		}
		// when there are no more vertices on the stack, return all paths
		return paths;
	}// end of method reconstructPaths()
	
	/*
	 * Getter methods
	 */
	
	public HashMap<Integer, HashMap<Integer, Double> > getEBC(){
		return EBCs;
	}
}
