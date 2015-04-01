package ca.ubc.ece.salt.sdjsb.cfg2;

import java.util.LinkedList;
import java.util.List;

import org.mozilla.javascript.ast.AstNode;

/**
 * A control flow graph node. Thread safe.
 */
public class CFGNode {
	
	/** Unique IDs for nodes. **/
	private static long idGen = 0;
	
	/** The unique id for this node. **/
	private long id;
	
	/** The AST Statement which contains the actions this node performs. **/
	private AstNode statement;
	
	/** The edges leaving this node. **/
	List<Edge> edges;
	
	/**
	 * @param statement The statement that is executed when this node is 
	 * 		  			reached.
	 */
	public CFGNode(AstNode statement) {
		this.edges = new LinkedList<Edge>();
		this.statement = statement;
		this.id = CFGNode.getUniqueId();
	}
	
	/**
	 * Add an edge to this node.
	 * @param condition The condition for which we traverse the edge.
	 * @param node The node at the other end of this edge.
	 */
	public void addEdge(AstNode condition, CFGNode node) {
		this.edges.add(new Edge(condition, node));
	}
	
	/**
	 * @return The AST Statement which contains the actions this node performs.
	 */
	public AstNode getStatement() {
		return statement;
	}

	/**
	 * @return The unique ID for this node.
	 */
	public long getId() {
		return id;
	}
	
	/**
	 * @return A unique ID for a new node
	 */
	private static synchronized long getUniqueId() {
		long id = CFGNode.idGen;
		CFGNode.idGen++;
		return id;
	}

	/**
	 * A labeled edge from from this node to another.
	 */
	public class Edge {
		
		/* The condition in which this edge is traversed. If null then the edge
		 * is always traversed. */
		public AstNode condition;
		public CFGNode node;
		
		public Edge(AstNode condition, CFGNode node) {
			this.condition = condition;
			this.node = node;
		}
	}

}
