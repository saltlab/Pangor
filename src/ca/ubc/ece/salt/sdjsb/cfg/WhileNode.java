package ca.ubc.ece.salt.sdjsb.cfg;

import org.mozilla.javascript.ast.AstNode;

/**
 * A CFGNode that has two exit edges: one for the true condition and one for
 * the false condition. The WhileNode also contains a loop on exit from the
 * true condition.
 */
public class WhileNode extends CFGNode {
	
	private CFGNode trueBranch;
	private CFGNode falseBranch;
	public CFGNode mergeNode;
	
	public WhileNode(AstNode statement) {
		super(statement);
	}
	
	public void setTrueBranch(CFGNode trueBranch) {
		this.trueBranch = trueBranch;
	}
	
	public CFGNode getTrueBranch() {
		return this.trueBranch;
	}
	
	public CFGNode getFalseBranch() {
		return this.falseBranch;
	}
	
	@Override
	public void mergeInto(CFGNode nextNode) {
		
		this.mergeNode = nextNode;
		
		/* The true branch is a loop. If there are no subgraphs, the statement
		 * loops back into itself (which in JavaScript is an infinite loop). */
		
		if(this.trueBranch == null) {
			this.trueBranch = this;
		}

		/* The false branch is merged directly into the next node after the 
		 * statement. */

        this.falseBranch = nextNode;
		
	}

}
