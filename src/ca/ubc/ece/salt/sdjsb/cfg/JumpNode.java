package ca.ubc.ece.salt.sdjsb.cfg;

import org.mozilla.javascript.ast.AstNode;

/**
 * A CFGNode that jumps to another node in the program. This includes break and
 * continue statements.
 */
public class JumpNode extends StatementNode {

	public JumpNode(AstNode statement) {
		super(statement);
	}

}
