package ca.ubc.ece.salt.sdjsb.checker.specialtype;

import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.ArrayUtils;
import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.KeywordLiteral;
import org.mozilla.javascript.ast.NumberLiteral;
import org.mozilla.javascript.ast.StringLiteral;

import ca.ubc.ece.salt.sdjsb.checker.CheckerContext;
import ca.ubc.ece.salt.sdjsb.checker.CheckerUtilities;
import ca.ubc.ece.salt.sdjsb.checker.specialtype.SpecialTypeMap.SpecialType;

public class SpecialTypeCheckerUtilities {

	/**
	 * Returns the special type of the AstNode.
	 * @param node The AstNode to check.
	 * @return The SpecialType of the AstNode, or null if the node is not
	 * 		   a Name node that holds a special type keyword or value.
	 */
	public static SpecialType getSpecialType(AstNode node) {
		
        String name = CheckerUtilities.getIdentifier(node);
        
        if(name != null) {

            if(name.equals("undefined")) return SpecialType.UNDEFINED;
            else if(name.equals("NaN")) return SpecialType.NAN;
            else return null;
            
        }
        else if (node instanceof KeywordLiteral) {
                    
            if(node.getType() == Token.NULL) return SpecialType.NULL;
            else return null;

        }
        else if (node instanceof StringLiteral) {

            String literal = ((StringLiteral) node).getValue();
            if(literal.isEmpty()) return SpecialType.BLANK;
            else return null;

        }
        else if (node instanceof NumberLiteral) {

            double literal = ((NumberLiteral) node).getNumber();
            if(literal == 0.0) return SpecialType.ZERO;
            else return null;

        }
        else {
            return null;
        }
	}

	/**
	 * Checks if the identifier was used in the branch before modifications.
	 * @param node The branch statement to look for uses in.
	 * @param identifier The variable/field/function identifier that is being
	 * 					 checked.
	 * @return true if the identifier is used, false otherwise.
	 */
	public static boolean isUsed(CheckerContext context, AstNode node, String identifier) {

        /* Remove any variable identifiers from the map that have uses
         * added inside one of the branches. */
		Set<String> identifiers = new TreeSet<String>();
		identifiers.add(identifier);

        UseTreeVisitor useVisitor = new UseTreeVisitor(context, identifiers);
        node.visit(useVisitor);
        
        if(identifiers.contains(identifier)) return false;
        
        return true;
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
