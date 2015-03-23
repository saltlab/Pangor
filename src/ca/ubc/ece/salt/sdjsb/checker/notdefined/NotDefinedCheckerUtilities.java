package ca.ubc.ece.salt.sdjsb.checker.notdefined;

import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.InfixExpression;
import org.mozilla.javascript.ast.Name;

public class NotDefinedCheckerUtilities {

	/**
	 * Check if the Name (AstNode) is a variable.
	 * @param node
	 */
	public static boolean isVariable(Name name) {
		
		AstNode parent = name.getParent();

		/* If the parent is field access, make sure it is on the LHS. */
		if(parent instanceof InfixExpression) {
			InfixExpression ie = (InfixExpression) parent;
			if(ie.getOperator() == Token.GETPROP || ie.getOperator() == Token.GETPROPNOWARN) {
				if(ie.getRight() == name) return false;
			}
		}
		
		return true;
	}
    
}
