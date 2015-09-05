package ca.ubc.ece.salt.pangor.cfg;

import org.mozilla.javascript.ast.AstNode;

import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode;
import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode.ChangeType;

/**
 * A labeled, directed edge to another node.
 */
public class CFGEdge {

	/** Unique IDs for nodes. **/
	private static long idGen = 0;

	/** The unique id for this node. **/
	private long id;

	/** The condition in which this edge is traversed. If null then the edge
	 * is always traversed. **/
	private ClassifiedASTNode condition;

	/** The node that this edge exits. */
	private CFGNode from;

	/** The node that this edge points to. */
	private CFGNode to;

	/** The change operation applied to the edge from source to destination. **/
	public ChangeType changeType;

	/** Does this edge traverse a path that loops? */
	public boolean loopEdge;

	public CFGEdge(ClassifiedASTNode condition, CFGNode from, CFGNode to) {
		this.condition = condition;
		this.to = to;
		this.from = from;
		this.changeType = ChangeType.UNKNOWN;
		this.id = CFGEdge.getUniqueId();
		this.loopEdge = false;
	}

	public CFGEdge(ClassifiedASTNode condition, CFGNode from, CFGNode to, boolean loopEdge) {
		this.condition = condition;
		this.to = to;
		this.from = from;
		this.changeType = ChangeType.UNKNOWN;
		this.id = CFGEdge.getUniqueId();
		this.loopEdge = loopEdge;
	}

	/**
	 * @return a shallow copy of the edge.
	 */
	public CFGEdge copy() {
		return new CFGEdge(this.condition, this.from, this.to, this.loopEdge);
	}

	/**
	 * @param to the node this edge enters.
	 */
	public void setTo(CFGNode to) {
		this.to = to;
	}

	/**
	 * @return the node this edge enters.
	 */
	public CFGNode getTo() {
		return to;
	}

	/**
	 * @param from the node this edge exits.
	 */
	public void setFrom(CFGNode from) {
		this.from = from;
	}

	/**
	 * @return the node this edge exits.
	 */
	public CFGNode getFrom() {
		return from;
	}

	/**
	 * @param condition the condition for which this edge is traversed.
	 */
	public void setCondition(ClassifiedASTNode condition) {
		this.condition = condition;
	}

	/**
	 * @return the condition for which this edge is traversed.
	 */
	public ClassifiedASTNode getCondition() {
		return condition;
	}

	/**
	 * @return the unique ID for the edge.
	 */
	public long getId() {
		return id;
	}

	/**
	 * @return A unique ID for a new edge
	 */
	private static synchronized long getUniqueId() {
		long id = CFGEdge.idGen;
		CFGEdge.idGen++;
		return id;
	}

	/**
	 * Reset the ID generator value. Needed in between test cases.
	 */
	public static synchronized void resetIdGen() {
		CFGEdge.idGen = 0;
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof CFGEdge) {
			return ((CFGEdge)o).condition == this.condition;
		}
		return false;
	}

	@Override
	public int hashCode() {
		assert false : "hashCode not designed";
		return 42; // any arbitrary constant will do
	}

}