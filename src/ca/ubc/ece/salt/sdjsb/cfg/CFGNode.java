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
	
	@Override
	public String toString() {
		return Token.typeToName(this.statement.getType());
	}

}
