package ca.ubc.ece.salt.sdjsb.cfg;

import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.FunctionNode;

/**
 * A function entry point for a CFG.
 */
public class FunctionEntryCFGNode extends LinearCFGNode {

	public FunctionEntryCFGNode(FunctionNode statement) {
		super(statement);
	}
	
	@Override 
	public String toString() {
		return Token.typeToName(this.statement.getType()) + " ENTRY";
	}

}
