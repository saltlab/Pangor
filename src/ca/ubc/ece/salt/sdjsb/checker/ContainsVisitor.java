package ca.ubc.ece.salt.sdjsb.checker;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.NodeVisitor;

/**
 * Checks if a child AstNode is present in the tree.
 * @author qhanam
 */
public class ContainsVisitor implements NodeVisitor {
	
	AstNode child;
	boolean contains;
	
	public ContainsVisitor(AstNode child) {
		this.child = child;
		this.contains = false;
	}

	@Override
	public boolean visit(AstNode node) {
		if(this.contains) {
			return false; 
		}
		else if(node == child) {
			this.contains = true;
			return false;
		}
		return true;
	}

}
