package ca.ubc.ece.salt.sdjsb.cfgl;

import java.util.LinkedList;
import java.util.List;

import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.AstNode;

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
		this.name = null;
	}
	
	/**
	 * @param statement The statement that is executed when this node is 
	 * 		  			reached.
	 * @param name The name for this node (nice for printing and debugging).
	 */
	public CFGNode(AstNode statement, String name) {
		this.edges = new LinkedList<Edge>();
		this.statement = statement;
		this.id = CFGNode.getUniqueId();
		this.name = name;
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
	 * Add an edge to this node. If an edge with the same condition already
	 * exists, that edge will be overwritten. 
	 * @param edge The edge to add.
	 */
	public void addEdge(Edge edge) {
		int index = this.edges.indexOf(edge);
		if(index >= 0) {
			this.edges.get(index).node = edge.node;
		}
		else {
            this.edges.add(edge);
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
	
	public String getName() {
		
		if(this.name != null) return this.name;
		
        String name;

        try {
            name = Token.typeToName(this.statement.getType());
        }
        catch(IllegalStateException e) {
            name = Token.keywordToName(this.statement.getType());
        }

        return name;
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

}
