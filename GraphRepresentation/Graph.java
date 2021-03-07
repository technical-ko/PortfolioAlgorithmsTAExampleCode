import java.util.LinkedList;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Queue;


/**
 * @author Keith O'Neal and Andrew Dolge
 * CISC 380 Algorithms Assignment 5
 * 
 * Represents an undirected graph of nodes and edges in adjacency list format.
 * NOTE: This implementation stores edges as Edge objects, and each node
 * holds a list of Edge objects linking it to it's neighbors. While this
 * implementation is more complicated than neccessary, it will be useful
 * in future assignments in which Edges hold their own properties.  
 * @author YOUR NAME HERE Due Date: xx/xx/xx
 */

public class Graph {

	private static final boolean DEBUG = false;
	private ArrayList<GraphNode> nodes;


	/**
	 * Constructs a graph with the given adjacency matrix. The adjacency
	 * matrix is a 2d array of booleans representing the presence of edges in the
	 * graph.
	 * 
	 * An edge between vertex i and vertex j exists if adacencyMatrix[i][j] is true.
	 * @param adjacencyMatrix a 2d boolean array representing an adjacency matrix.
	 */
	public Graph(boolean[][] adjacencyMatrix){

		nodes = new ArrayList<GraphNode>();

		// populate the graph with nodes.
		for (int i = 0; i < adjacencyMatrix.length; i++) {
			nodes.add(new GraphNode(i));
		}

		// connect the nodes based on the adjacency matrix
		for (int i = 0; i < adjacencyMatrix.length; i++) {
			for (int j = 0; j < adjacencyMatrix[i].length; j++) {
				if (adjacencyMatrix[i][j]) {
					if(adjacencyMatrix[j][i]){
						//only add edges on the first encounter
						if(i <= j)
						{
							this.connect(i, j);
						}
					}else{
							throw new IllegalArgumentException("Invalid adjacency matrix. The edge marked true for " + i + ", " + j + 
							" must be marked true for " + j + ", " + i + ".");						
						}
				}//if
			}
		}

	}// constructor

	/**
	 * Constructs a graph with the given adjacency list. The graph will
	 * have a number vertices equal to the length of the adjacency list. The
	 * adajcency list is a 2d array of integers where the array at index i
	 * represents the ids of neighbors of vertex i.
	 * 
	 * Values that are negative or greater than length of adjacencyList are ignored.
	 * 
	 * @param adjacencyList a 2d integer array representing an adjacency list of the vertices.
	 */
	public Graph(int[][] adjacencyList) {
		nodes = new ArrayList<GraphNode>();

		// populates the graph with nodes.
		for (int i = 0; i < adjacencyList.length; i++) {
			nodes.add(new GraphNode(i));
		}

		boolean confirmed;
		int other;
		// connect the nodes based on the adjacency list.
		for (int i = 0; i < adjacencyList.length; i++) {
			for (int j = 0; j < adjacencyList[i].length; j++) {
				confirmed = false;
				other = adjacencyList[i][j];
				for (int k = 0; k < adjacencyList[other].length; k++)
				{
					if(adjacencyList[other][k] == i)//corresponding entry confirmed
					{
						// if the value in the array is a valid node id, connect them.
						if (-1 < other && other < adjacencyList.length) {
							confirmed = true;
							if(i <= other)
							{
								this.connect(i, other);
							}
						}
					}	
				}
				if(!confirmed)
				{
					throw new IllegalArgumentException("Invalid adjacency list. The edge represented for row " + i + " holding " + adjacencyList[i][j] + 
						" must also be represented for row " + adjacencyList[i][j] + " holding " + i + ".");		
				}
			}

		}

	}// constructor


	/**
	 * Retrieves the number of nodes in the Graph.
	 * 
	 * @return the number of nodes in the graph.
	 */
	public int getGraphSize() {
		return this.nodes.size();
	}// getGraphSize

	/**
	 * Returns a string representation of all the nodes in the graph. The string
	 * displays the nodes data, and a list of all of its outgoing Nodes.
	 *
	 * @return a string representation of the graph.
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		// for every node
		for (int i = 0; i < this.nodes.size(); i++) {
			// append the string representation to the result.
			GraphNode current = this.nodes.get(i);
			sb.append(String.format("%-8s Deg: %3d \n", current.toString(),current.getDegree()));
		}
		return sb.toString();
	}// toString

	/**
	 * adds the node u as a neighbor to v.
	 *
	 * @param v  the data of a node to receive a neighbor
	 * @param u  the data of a node to receive a neighbor
	 */
	private void connect(int v, int u) {

		if (0 > v || v >= this.getGraphSize()) {
			throw new ArrayIndexOutOfBoundsException("Cannot connect nonexistent vertex with value: " + v
					+ ". Valid Nodes are between 0 and " + (this.nodes.size() - 1) + ".");
		}

		if (0 > u || u >= this.getGraphSize()) {
			throw new ArrayIndexOutOfBoundsException("Cannot connect nonexistent vertex with value: " + u
					+ ". Valid Nodes are between 0 and " + (this.nodes.size() - 1) + ".");

		}

		GraphNode uNode = findNode(u);
		GraphNode vNode = findNode(v);

		// create an edge object
		Edge edge = new Edge(vNode, uNode);
		uNode.getEdges().add(edge);
		vNode.getEdges().add(edge);
		uNode.incrementDegree();
		vNode.incrementDegree();

	}// connect


	/**
	 * Finds a node in the graph, if it exists.
	 * 
	 * @throws ArrayIndedOutOfBoundsException if the node does not exist.
	 * @return a GraphNode with the given data.
	 * 
	 */
	private GraphNode findNode(int data) {
		if(0 <= data && data < this.nodes.size()){
			return nodes.get(data);
		}else{
			return null;
		}


	}// findNode
	

	/**
	 * Single Source shortest path: BFS
	 * Explores all vertices of a graph reachable from start.
	 * Values are stored in dist and prev arrays.
	 */
	public void BFS(int start, int[] dist, int[] prev)
	{
		Queue<GraphNode> Q = new LinkedList<>();
		GraphNode w;
		GraphNode z;
		ListIterator<Edge> edges;
		
		Q.add(this.findNode(start));
		dist[start] = 0;
		while(!Q.isEmpty())
		{
			w = Q.remove();
			edges = w.getEdges().listIterator();
			while(edges.hasNext())
			{
				z = edges.next().getOther(w.getData());
				if(dist[z.data] == Integer.MAX_VALUE)
				{
					Q.add(z);
					dist[z.data] = dist[w.data] + 1;
					prev[z.data] = w.data;
				}
			} 
		}
	}


	/**
	 * Representation of a vertex of the graph, uniquely identified by the data.
	 */
	private static class GraphNode {

		private int data;
		private int degree;

		private LinkedList<Edge> edges;

		public GraphNode(int data) {

			this.data = data;
			this.edges = new LinkedList<Edge>();

		}// constructor

		/**
		 * increments the degree. 
		 * 
		 */
		public void incrementDegree() {
			this.degree++;
		}


		/**
		 * returns this node's degree.
		 * This is the number of nodes that this node has as neighbors.
		 * @return the degree of this node.
		 */
		public int getDegree() {
			return this.degree;
		}//getInDegree


		/**
		 * getter method for the data of the node. This should uniquely identify the
		 * node.
		 * 
		 * @return the data within this node.
		 */
		public int getData() {
			return this.data;
		}// getData


		/**
		 * retrieves a reference to a list of this node's neighbors.
		 * 
		 * @return a LinkedList of nodes that are connected to this node.
		 * 
		 */
		public List<Edge> getEdges() {
			return this.edges;
		}// getNeighbors


		/**
		 * returns a string representation of the node. Displays the current data of the
		 * node, and a list of the data of all of its neighbors.
		 * 
		 * @return a string representation of the node.
		 */
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(this.getData() + ":[");
			for (int i = 0; i < this.edges.size(); i++) {
				if (i == this.edges.size() - 1) {
					sb.append(edges.get(i).getOther(this.getData()).getData());
				} else {
					sb.append(edges.get(i).getOther(this.getData()).getData() + ", ");
				} // else
			} // for
			sb.append("]");
			return sb.toString();

		}// toString

	}// class GraphNode



	/**
	 * A class for representing the edges connecting Graph nodes.
	 */
	private static class Edge {
		GraphNode v;
		GraphNode u;

		public Edge(GraphNode v, GraphNode u)
		{
			this.v = v;
			this.u = u;
		}

		public GraphNode getV() {
			return v;
		}
		public GraphNode getU() {
			return u;
		}

		/**
		 * If the value of curr matches the data value of u or v, returns the other
		 * node. If curr matches neither, this function returns null.
		 * @param curr
		 * @return u if curr matches v, v if curr matches u, and null otherwise.
		 */
		public GraphNode getOther(int curr) {
			GraphNode v = this.getV();
			GraphNode u = this.getU();
			if(curr == v.getData()){return u;}
			if(curr == u.getData()){return v;}
			throw new IllegalArgumentException("Curr is not an endpoint for this edge.");
		}

	}


}// class
