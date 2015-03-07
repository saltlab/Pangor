package ca.ubc.ece.salt.sdjsb.checker;

import org.apache.commons.lang3.ArrayUtils;
import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.InfixExpression;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.ParenthesizedExpression;

public class Utilities {

	/**
	 * Returns the variable, field or function identifier for the AstNode. If
	 * the node is not a Name, InfixEpxression or FunctionCall, or if the
	 * operator of an InfixExpression is not a field access (GETPROP), then it 
	 * can't build an identifier and it returns null.
	 * @param node The node that represents an identifier.
	 * @return The variable, field or function identifier or null if an
	 * 		   identifier can't be built.
	 */
	public static String getIdentifier(AstNode node) throws IllegalArgumentException {
        if (node instanceof Name) {
            return ((Name)node).getIdentifier();
        }
        else if (node instanceof InfixExpression) {
            InfixExpression ie = (InfixExpression) node;
            if(Utilities.isIdentifierOperator(ie.getOperator())) {
            	String left = getIdentifier(ie.getLeft());
            	String right = getIdentifier(ie.getRight());
            	if(left == null || right == null) return null;
                return left + "." + right;
            }
            return null; // Indicates we can't build an identifier for this expression.
        }
        else if (node instanceof FunctionCall) {
            FunctionCall fc = (FunctionCall) node;
            String identifier = getIdentifier(fc.getTarget());
            if(identifier == null) return null;
            return identifier + "()";
        }
        else if (node instanceof ParenthesizedExpression) {
        	ParenthesizedExpression pe = (ParenthesizedExpression) node;
        	String identifier = getIdentifier(pe.getExpression());
            if(identifier == null) return null;
            return identifier;
        }
        
        return null;
	}

	/**
	 * Returns true if the operator for the binary expression is an identifier
	 * operator (i.e. whatever is to the left should be included in the
	 * identity of the expression).
	 * @param tokenType The operator type.
	 * @return
	 */
    public static boolean isIdentifierOperator(int tokenType) {
        if(tokenType == Token.GETPROP || tokenType == Token.GETPROPNOWARN) {
            return true;
        }
        return false;
    }

	/**
	 * Returns true if the operator for the binary expression is an equivalence
	 * operator (i.e. ==, !=, ===, !==).
	 * @param tokenType The operator type.
	 * @return
	 */
    public static boolean isEquivalenceOperator(int tokenType) {
        if(tokenType == Token.SHEQ || tokenType == Token.SHNE
            || tokenType == Token.EQ || tokenType == Token.NE) {
            return true;
        }
        return false;
    }
    
    /**
     * Returns true if the operator represents an operation where the 
     * identifiers are dereferenced.
     * @param tokenType
     * @return
     */
    public static boolean isUseOperator(int tokenType) {
        int[] useOperators = new int[] { Token.GETPROP, Token.GETPROPNOWARN, 
        								 Token.BITOR, Token.BITXOR, Token.BITAND, 
        								 Token.ADD, Token.SUB , Token.MUL, 
        								 Token.DIV , Token.MOD, Token.GETELEM, 
        								 Token.SETELEM, Token.ASSIGN_BITOR, 
        								 Token.ASSIGN_BITXOR, 
        								 Token.ASSIGN_BITAND , Token.ASSIGN_LSH,
                                         Token.ASSIGN_RSH , Token.ASSIGN_ADD, 
                                         Token.ASSIGN_SUB , Token.ASSIGN_MUL,
                                         Token.ASSIGN_DIV, Token.ASSIGN_MOD, 
                                         Token.DOT, Token.INC, Token.DEC };
        return ArrayUtils.contains(useOperators, tokenType);
    }
    
}
