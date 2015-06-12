package ca.ubc.ece.salt.sdjsb.analysis.learning;

import java.util.LinkedList;

import org.mozilla.javascript.ast.AstNode;

import ca.ubc.ece.salt.sdjsb.cfg.CFGNode;

/**
 * A PathFragment stores a section of a basic block who's edges are all 
 * inserted or deleted (depending on whether the source function or 
 * destination function is being analyzed).
 */
public class PathFragment {
	
	/**
	 * Stores the path fragment.
	 */
	public LinkedList<CFGNode> path;

	/** 
	 * Stores the CFGNode that the path fragment continues at (if 
	 * applicable).
	 */
	public CFGNode continuesAt;
	
	/**
	 * The condition under which this path fragment executes.
	 */
	public AstNode condition;
	

	public PathFragment(CFGNode head) {
		this.path = new LinkedList<CFGNode>();
		this.path.add(head);
		this.continuesAt = null;
		this.condition = null;
	}
}