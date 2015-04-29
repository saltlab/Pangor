package ca.ubc.ece.salt.sdjsb.cfg;

import java.util.LinkedList;
import java.util.List;

import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode;

/**
 * A control flow graph node. Thread safe.
 */
public class CFGNode {
	
	/** Unique IDs for nodes. **/
	private static long idGen = 0;
	
	/** The unique id for this node. **/
	private long id;
	
	/** Optional name for this node. **/
	private String name;
	
	/** The AST Statement which contains the actions this node performs. From
	 * org.mozilla.javascript.Token. **/
	private ClassifiedASTNode statement;
	
	/** The edges leaving this node. **/
	private List<CFGEdge> edges;
	
	/** The corresponding source or destination CFGNode. */
	private CFGNode mappedNode;
	
	/** A semaphore for counting the edges in and visits. */
	private int edgesIn;
	
	/** 
	
	/**
	 * @param statement The statement that is executed when this node is 
	 * 		  			reached.
	 */
	public CFGNode(ClassifiedASTNode statement) {
		this.edges = new LinkedList<CFGEdge>();
		this.statement = statement;
		this.id = CFGNode.getUniqueId();
		this.name = null;
		this.setMappedNode(null);
		this.edgesIn = 0;
	}
	
	/**
	 * @param statement The statement that is executed when this node is 
	 * 		  			reached.
	 * @param name The name for this node (nice for printing and debugging).
	 */
	public CFGNode(ClassifiedASTNode statement, String name) {
		this.edges = new LinkedList<CFGEdge>();
		this.statement = statement;
		this.id = CFGNode.getUniqueId();
		this.name = name;
		this.edgesIn = 0;
	}
	
	/**
	 * Add one to the number of edges going into the node.
	 */
	public void incrementEdges() {
		this.edgesIn++;
	}
	
	/**
	 * Subtract one from the number of edges going into the node.
	 * @return true if the decrement reaches 0.
	 */
	public boolean decrementEdges() {
		this.edgesIn--;
		return this.edgesIn == 0;
	}
	
	/**
	 * Add an edge to this node. If an edge with the same condition already
	 * exists, that edge will be overwritten. 
	 * @param condition The condition for which we traverse the edge.
	 * @param node The node at the other end of this edge.
	 */
	public void addEdge(ClassifiedASTNode condition, CFGNode node) {
		CFGEdge edge = new CFGEdge(condition, this, node);
		int index = this.edges.indexOf(edge);
		if(index >= 0) {
			this.edges.get(index).setTo(node);
		}
		else {
            this.edges.add(new CFGEdge(condition, this, node));
		}
	}

	/**
	 * Add an edge to this node. If an edge with the same condition already
	 * exists, that edge will be overwritten. 
	 * @param condition The condition for which we traverse the edge.
	 * @param node The node at the other end of this edge.
	 */
	public void addEdge(ClassifiedASTNode condition, CFGNode node, boolean loopEdge) {
		CFGEdge edge = new CFGEdge(condition, this, node, loopEdge);
		int index = this.edges.indexOf(edge);
		if(index >= 0) {
			this.edges.get(index).setTo(node);
		}
		else {
            this.edges.add(new CFGEdge(condition, this, node, loopEdge));
		}
	}
	
	/**
	 * Add an edge to this node. If an edge with the same condition already
	 * exists, that edge will be overwritten. 
	 * @param edge The edge to add.
	 */
	public void addEdge(CFGEdge edge) {
		int index = this.edges.indexOf(edge);
		if(index >= 0) {
			this.edges.get(index).setTo(edge.getTo());
		}
		else {
            this.edges.add(edge);
		}
	}
	
	/**
	 * @return The edges leaving this node.
	 */
	public List<CFGEdge> getEdges() {
		return this.edges;
	}
	
	/**
	 * @param edges The new edges for the node.
	 */
	public void setEdges(List<CFGEdge> edges) {
		this.edges = edges;
	}
	
	/**
	 * @return The AST Statement which contains the actions this node performs.
	 */
	public ClassifiedASTNode getStatement() {
		return statement;
	}
	
	/**
	 * @param statement The AST Statement which contains the actions this node
	 * 					performs.
	 */
	public void setStatement(ClassifiedASTNode statement) {
		this.statement = statement;
	}

	/**
	 * @return The unique ID for this node.
	 */
	public long getId() {
		return id;
	}

	/**
	 * @return the corresponding node in the source or destination CFG.
	 */
	public CFGNode getMappedNode() {
		return mappedNode;
	}

	/**
	 * @param mappedNode the corresponding node in the source or destination CFG.	 
	 */
	public void setMappedNode(CFGNode mappedNode) {
		this.mappedNode = mappedNode;
	}
	
	public String getName() {
		
		if(this.name != null) return this.name;
		
		return this.statement.getTypeName();
		
	}

	/**
	 * Make a copy of the given node.
	 * @param node The node to copy.
	 * @return A shallow copy of the node. The condition AST and edge CFGs will
	 * 		   be the same as the original.
	 */
	public static CFGNode copy(CFGNode node) {
        CFGNode newNode = new CFGNode(node.getStatement());
        for(CFGEdge edge : node.getEdges()) newNode.addEdge(edge.getCondition(), edge.getTo());
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
	 * Reset the ID generator value. Needed in between test cases.
	 */
	public static synchronized void resetIdGen() {
		CFGNode.idGen = 0;
	}
	
	@Override
	public String toString() {
		return this.id + "_" + this.getName();
	}

}
