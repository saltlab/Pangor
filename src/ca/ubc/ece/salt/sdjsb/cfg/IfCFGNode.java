package ca.ubc.ece.salt.sdjsb.cfg;

import org.mozilla.javascript.ast.AstNode;

/**
 * A CFGNode that has two exit edges: one for the true condition and one for
 * the false condition.
 * @author qhanam
 */
public class IfCFGNode extends CFGNode {
	
	private CFG trueCFG;
	private CFG falseCFG;
	
	private CFGNode trueBranch;
	private CFGNode falseBranch;
	
	public IfCFGNode(AstNode statement, CFG trueCFG, CFG falseCFG) {
		super(statement);
		this.trueCFG = trueCFG;
		this.falseCFG = falseCFG;
	}
	
	public CFGNode getTrueBranch() {
		return this.trueBranch;
	}
	
	public CFGNode getFalseBranch() {
		return this.falseBranch;
	}
	
	@Override
	public void mergeInto(CFGNode nextNode) {
		
//		if(trueCFG != null) {
//			CFGFactory.merge(this.trueCFG.exitNodes, nextNode);
//		}
//		else {
//			this.trueBranch = nextNode;
//		}
//
//		if(falseCFG != null) {
//			CFGFactory.merge(this.falseCFG.exitNodes, nextNode);
//		}
//		else {
//			this.falseBranch = nextNode;
//		}
		
	}

}
