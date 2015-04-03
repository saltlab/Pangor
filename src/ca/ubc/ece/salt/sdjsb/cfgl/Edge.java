package ca.ubc.ece.salt.sdjsb.cfgl;

import org.mozilla.javascript.ast.AstNode;

/**
 * A labeled, directed edge to another node.
 */
public class Edge {
	
	/** The condition in which this edge is traversed. If null then the edge
	 * is always traversed. **/
	public AstNode condition;
	
	/** The node that this edge points to. */
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