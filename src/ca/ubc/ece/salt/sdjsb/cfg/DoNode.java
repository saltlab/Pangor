package ca.ubc.ece.salt.sdjsb.cfg;

import org.mozilla.javascript.ast.EmptyStatement;

/**
 * A CFGNode that represents the start of a do loop. This is basically a
 * a convenience class for serial printing. 
 */
public class DoNode extends StatementNode {
	
	public DoNode() {
		super(new EmptyStatement());
	}

	@Override
	public String printSubGraph(CFGNode mergeNode) {

        return "{" + this.getNext().printSubGraph(mergeNode);

	}

}
