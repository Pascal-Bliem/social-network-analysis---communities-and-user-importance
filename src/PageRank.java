import java.util.HashMap;
import java.util.HashSet;

public class PageRank {

	/* 
	 * This class represents the Page Rank algorithm for calculate a metric of
	 * relative importance for every vertex in a graph. It runs in O(V + E).
	 * This implementation uses a damping constant (damp, default 0.85) and 
	 * iterates until the squared difference from one iteration to the next 
	 * is smaller than the convergence criterion (eps 1e-8).
	 */ 
	
	int numVer; // number vertices 
	private double norm; // normalization constant (default N * 1)
	private double damp; // damping constant
	private double eps; // conversion criterion
	
	// current page rank score of each vertex
	private HashMap<Integer, Double> pr;  
	// page rank score of the last iteration of each vertex
	private HashMap<Integer, Double> last_pr;
	// for each vertex i, storing the contribution it gets from vertex j
	// so HashMap< i, HashMap< j, contribution> >
	private HashMap<Integer, HashMap<Integer, Double> > contribution;
	
	
	public PageRank(HashMap<Integer, HashSet<Integer>> vertices,double norm) {
		// default constructor initializes field variables
		this.numVer = vertices.keySet().size();
		this.norm = numVer * norm;
		this.damp = 0.85;
		this.eps = 1.0e-8;
		
		pr = new HashMap<Integer, Double>();
		last_pr = new HashMap<Integer, Double>();
		contribution = new HashMap<Integer, HashMap<Integer,Double>>();
		
		// calculate the relative contributions of how much vertex i is 
		// receiving of of vertex j's page rank. Stored as:
		// HashMap< i, HashMap< j, contribution> >
		// loop through all vertices
		for (int j : vertices.keySet()) {
			// loop through each vertex at the end of an edge outgoing from j
			for (int i : vertices.get(j)) {
				// if vertex i is not yet a key in the contributions map, put it in
				if(!contribution.containsKey(i)) {
					contribution.put(i, new HashMap<Integer, Double>());	
				}
				// set the contribution received by i from j as 
				// 1 / (number of edges outgoing  from j)
				contribution.get(i).put(j, (1/((double) vertices.get(j).size())));				
			}
			
			// initialize the initial page rank scores, same for each node
			pr.put(j, 1.0/norm);
			// set the score from the previous (not actually happened yet)
			// iteration to any  large value
			last_pr.put(j, 1.0/norm*100.0);
		}
	}// end of constructor default PageRank()
	
	public PageRank(HashMap<Integer, HashSet<Integer>> vertices, double norm, double eps, double damp) {
		// this constructor allows to set non-default parameters for norm, damp, and eps
		this(vertices,norm);
		this.damp = damp;
		this.eps = eps;
	}// end of constructor more parameters PageRank()
	
	public void run() {
		// run the Page Rank algorithm
		
		// count iterations
		int itercount = 0;
		// initial arbitrary mismatch (squared difference) between current and last page rank 
		double error = 100;
		
		// enter the while loop only if the error is still larger than
		// the convergence criterion eps
		while (error>eps ) {
			
			// print iteration and error
			itercount++;
			System.out.println("Page rank iteration " + itercount);
			System.out.println("Error " + error);
			
			// sum of all vertices' pr scores used for normalization 
			// at the end of each iteration
			double allPrSum = 0;

			// loop through all vertices
			for (int i : pr.keySet()) {
				//System.out.println("Entered loop of v " + i);	
				// assign the current page rank of i to its old one 
				//(basically storing the previously calculated value)
				//System.out.println("changed old pr " + last_pr.get(i));
				last_pr.put(i,pr.get(i));
				//System.out.println("to new pr " + last_pr.get(i));
				// calculate new page rank as sum of products of Page Rank scores and 
				// contributions from incoming edges (if there are any incoming edges)
				double prsum = 0;
				//System.out.println("about to get " + contribution.get(i));
				if (contribution.get(i)!=null) {
					// go through all nodes j that have outgoing edges to i
					for (int j : contribution.get(i).keySet()) {
						// calculate each js page rank contribution for i
						prsum += (damp * contribution.get(i).get(j) * pr.get(j) + (1.0-damp))/norm;
					}
				}
				// update the page rank of i and add it to sum of all PRs
				pr.put(i,prsum);
				allPrSum += prsum;
			}
			
			// normalize all vertice's page rank scores
			for (int i : pr.keySet()) {
				pr.put(i,pr.get(i)/allPrSum);
			}
			
			// recalculate the mismatch between current and last page rank iteration
			double iterErr = 0;
			for (int v : pr.keySet()) {
				iterErr += (pr.get(v) - last_pr.get(v))*(pr.get(v) - last_pr.get(v));
			}
			error = iterErr;
		}
	}// end of constructor run()
	
	public HashMap<Integer, Double> calculatePageRank(){
		// getter method to start the calculation and return the result to calling method
		run();
		return pr;
	}// end of method calculatePageRank()
}
