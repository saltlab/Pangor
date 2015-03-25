package ca.ubc.ece.salt.sdjsb.cfg;

import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.AstNode;

public class ScriptEntryCFGNode extends StatementNode {

	public ScriptEntryCFGNode(AstNode statement) {
		super(statement);
	}

	@Override 
	public String toString() {
		return Token.typeToName(this.statement.getType()) + " ENTRY";
	}

}
