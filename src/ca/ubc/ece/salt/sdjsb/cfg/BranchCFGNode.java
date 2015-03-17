package ca.ubc.ece.salt.sdjsb.cfg;

import org.mozilla.javascript.ast.AstNode;

/**
 * A CFGNode that has two exit edges: one for the true condition and one for
 * the false condition.
 * @author qhanam
 */
public class BranchCFGNode extends CFGNode {
	
	private CFGNode trueBranch;
	private CFGNode falseBranch;
	
	public BranchCFGNode(AstNode statement) {
		super(statement);
	}
	
	public void setTrueBranch(CFGNode trueBranch) {
		this.trueBranch = trueBranch;
	}
	
	public void setFalseBranch(CFGNode falseBranch) {
		this.falseBranch = falseBranch;
	}
	
	public CFGNode getTrueBranch() {
		return this.trueBranch;
	}
	
	public CFGNode getFalseBranch() {
		return this.falseBranch;
	}

}
