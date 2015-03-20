package ca.ubc.ece.salt.sdjsb.cfg;

import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.AstNode;

public abstract class CFGNode {

	/**
	 * The underlying statement for this CFG node.
	 */
	protected AstNode statement;
	
	public CFGNode(AstNode statement) {
		this.statement = statement;
	}
	
	public AstNode getStatement() {
		return statement;
	}
	
	/**
	 * Merges this and all sub-path exit nodes into the given node. The merge
	 * node is the node which follows all exit nodes from a sub-graph (e.g., 
	 * the last statement in an {@code if} branch or a {@code break} statement
	 * in a loop).
	 * @param node The node which the sub-graph merges into.
	 */
	public abstract void mergeInto(CFGNode node);
	
	@Override
	public String toString() {
		return Token.typeToName(this.statement.getType());
	}

}
