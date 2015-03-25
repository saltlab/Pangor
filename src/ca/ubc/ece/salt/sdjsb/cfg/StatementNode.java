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
public class StatementNode extends CFGNode {
	
	private CFGNode next;
	
	public StatementNode(AstNode statement) {
		super(statement);
	}
	
	@Override
	public void mergeInto(CFGNode node) {
		this.next = node;
	}
	
	public CFGNode getNext() {
		return this.next;
	}

}
