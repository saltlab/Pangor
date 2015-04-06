package ca.ubc.ece.salt.sdjsb.cfg;

import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode;
import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode.ChangeType;

/**
 * A labeled, directed edge to another node.
 */
public class Edge {
	
	/** The condition in which this edge is traversed. If null then the edge
	 * is always traversed. **/
	public ClassifiedASTNode condition;
	
	/** The node that this edge points to. */
	public CFGNode node;
	
	/** The change operation applied to the edge from source to destination. **/
	public ChangeType changeType;
	
	public Edge(ClassifiedASTNode condition, CFGNode node) {
		this.condition = condition;
		this.node = node;
		this.changeType = ChangeType.UNKNOWN;
	}
	
	@Override 
	public boolean equals(Object o) {
		if(o instanceof Edge) {
			return ((Edge)o).condition == this.condition;
		}
		return false;
	}
	
}