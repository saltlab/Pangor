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
	
	/** The AST Statement which contains the actions this node performs. From
	 * org.mozilla.javascript.Token. **/
	private AstNode statement;
	
	/** The edges leaving this node. **/
	private List<Edge> edges;
	
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
	 * Add an edge to this node. If an edge with the same condition already
	 * exists, that edge will be overwritten. 
	 * @param condition The condition for which we traverse the edge.
	 * @param node The node at the other end of this edge.
	 */
	public void addEdge(AstNode condition, CFGNode node) {
		Edge edge = new Edge(condition, node);
		int index = this.edges.indexOf(edge);
		if(index >= 0) {
			this.edges.get(index).node = node;
		}
		else {
            this.edges.add(new Edge(condition, node));
		}
	}
	
	/**
	 * @return The edges leaving this node.
	 */
	public List<Edge> getEdges() {
		return this.edges;
	}
	
	/**
	 * @return The AST Statement which contains the actions this node performs.
	 */
	public AstNode getStatement() {
		return statement;
	}
	
	/**
	 * @param statement The AST Statement which contains the actions this node
	 * 					performs.
	 */
	public void setStatement(AstNode statement) {
		this.statement = statement;
	}

	/**
	 * @return The unique ID for this node.
	 */
	public long getId() {
		return id;
	}

	/**
	 * Make a copy of the given node.
	 * @param node The node to copy.
	 * @return A shallow copy of the node. The condition AST and edge CFGs will
	 * 		   be the same as the original.
	 */
	public static CFGNode copy(CFGNode node) {
        CFGNode newNode = new CFGNode(node.getStatement());
        for(Edge edge : node.getEdges()) newNode.addEdge(edge.condition, edge.node);
        return newNode;
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
		
		@Override 
		public boolean equals(Object o) {
			if(o instanceof Edge) {
				return ((Edge)o).condition == this.condition;
			}
			return false;
		}
	}

}
