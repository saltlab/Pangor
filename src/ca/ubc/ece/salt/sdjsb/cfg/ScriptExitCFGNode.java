package ca.ubc.ece.salt.sdjsb.cfg;

import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.AstNode;

public class ScriptExitCFGNode extends CFGNode {

	public ScriptExitCFGNode(AstNode statement) {
		super(statement);
	}

	@Override 
	public String toString() {
		return Token.typeToName(this.statement.getType()) + " EXIT";
	}

	@Override
	public void mergeInto(CFGNode node) {
		/* Nothing to do yet. */
	}

	@Override
	public String printSubGraph(CFGNode mergeNode) {
        return this.toString();
	}

}