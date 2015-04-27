package ca.ubc.ece.salt.sdjsb.analysis.notdefined;

import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.InfixExpression;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.UnaryExpression;

public class NotDefinedAnalysisUtilities {

	/**
	 * Check if the Name (AstNode) is a variable.
	 * @param node
	 */
	public static boolean isVariable(Name name) {
		
		AstNode parent = name.getParent();

		if(parent instanceof InfixExpression) {
			InfixExpression ie = (InfixExpression) parent;
			if(ie.getOperator() == Token.GETPROP || ie.getOperator() == Token.GETPROPNOWARN) {
                /* If the parent is field access, make sure it is on the LHS. */
				if(ie.getRight() == name) return false;
			}
			else {
				/* It is some other boolean operator, so it should be a variable. */
				return true;
			}
		}
		if(parent instanceof UnaryExpression) {
			/* It is a variable that is being operated on by a unary expression. */
			return true;
		}
		
		return true;
	}
    
}
