package ca.ubc.ece.salt.sdjsb.cfg;

import org.mozilla.javascript.ast.AstNode;

/**
 * A CFGNode that has only one exit edge.
 * 
 * Since this is an intra-procedural analysis, we do not add an edge between
 * call sites and callees. Statements that include method calls are 
 * therefore LinearCFGNodes.
 * 
 * @author qhanam
 */
public class LinearCFGNode extends CFGNode {
	
	private CFGNode next;
	
	public LinearCFGNode(AstNode statement) {
		super(statement);
	}
	
	public void setNext(CFGNode next) {
		this.next = next;
	}
	
	public CFGNode getNext() {
		return this.next;
	}

}
