import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Stack;
import java.util.List;

/* This class represents the Tarja algorithm for finding strongly 
 * connected components in a graph. It runs in O(V + E).
 * This method is recursive and for very large graphs, hence very
 * deep recursion, it will require a lot of stack memory. 
 * If a stack overflow occurs, try increasing stack size or 
 * implement the algorithm with its own stack.
 */ 

public class TarjanSCC {
	  
	// set up a stack, and hash maps for the vertices and their edges
	// as well as their low link values, and a hash set to keep track of
	// visited nodes.
	private Stack<Integer> stack;
	private HashMap<Integer, HashSet<Integer>> vertices;
	private HashMap<Integer, Integer> lowLinks;
	private HashSet<Integer> visited;
	private List<HashSet<Integer>> connectedComponentList;
	private int time = 0; // keep the order of node that's been visited
	private int count = 0; // count how many SCC there are

	public TarjanSCC(HashMap<Integer, HashSet<Integer>> vertices) {
		// constructor initializes field variables
		this.stack = new Stack<Integer>();
		this.vertices = vertices;
		this.lowLinks = new HashMap<Integer, Integer>();
		this.visited = new HashSet<Integer>();
		this.connectedComponentList = new LinkedList<HashSet<Integer>>();
	}// end of constructor TarjanSCC()
	
	public void run() {
		// run the recursive algorithm for all vertices that are not yet visited
		for(int vertex : this.vertices.keySet())
			if(!visited.contains(vertex))
				dfs(vertex);
	}// end of method run()
	
	 private void dfs(int vertex) {
		 // perform depth-first-search
		 
		 // set low link value, add to visited, push on stack and assume is SCC root
		 lowLinks.put(vertex, time++);
		 visited.add(vertex);
		 stack.push(vertex);
		 boolean isComponentRoot = true;
		 
		 // for all v neighbors of vertex
		 for(int v : vertices.get(vertex)) {
			 if(!visited.contains(v)) {
				 // Recursively visit neighbor v
				 dfs(v);
			 }
			 
			// if there is a back edge
			 if(lowLinks.get(vertex) > lowLinks.get(v)) {
				 lowLinks.put(vertex, lowLinks.get(v));
				 // So vertex is not the root of a SCC
				 isComponentRoot = false;
			 }
		 }
		 
		// only for the root SCC node
		 if(isComponentRoot) {
			 count++;
			 // vertex is the root of a SCC
			 
			 // make a list that will contains the nodes of this SCC
			 HashSet<Integer> component = new HashSet<Integer>();
			 
			 while(true) {
				 // pop up next vertex from stack
				 int actualVertex = stack.pop();
				 // So the vertex actualVertex is in SCC number #count
				 // add it to the list
				 component.add(actualVertex);
				 // and set its low link to infinity
				 lowLinks.put(actualVertex, Integer.MAX_VALUE);
				 
				 // Run until it hits the root SCC node
				 if(actualVertex==vertex) {
					 break;
				 }
			 }
			 
			 // add the list of SCC nodes to the list of SCC lists
			 connectedComponentList.add(component);
		 }
	 }// end of method dfs()
	 
	 
	 /*
	  *  Getter methods below
	  */
	 
	 public void printComponents(){
		 System.out.println(connectedComponentList);
	 }// end of method printComponents()
	 
	 public int getSCCCount() {
		 return count;
	 }// end of method getSCCCount()
	 
	 public List<HashSet<Integer>> getConnectedComponentList() {
		 return connectedComponentList;
	 }// end of method getConnectedComponentList()
}
