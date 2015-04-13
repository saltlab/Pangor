package ca.ubc.ece.salt.sdjsb.analysis;

import org.mozilla.javascript.ast.AstNode;

public class LatticeOperations {
	
	public boolean atLeastAsPrecise(LatticeElement info, LatticeElement reference, AstNode node) {
		return false;
	}
	
	public LatticeElement bottom() {
		return null;
	}
	
	public LatticeElement copy(LatticeElement original) {
		LatticeElement copy = new LatticeElement();

		return copy;
	}

	public LatticeElement join() {
		return null;
	}

}
