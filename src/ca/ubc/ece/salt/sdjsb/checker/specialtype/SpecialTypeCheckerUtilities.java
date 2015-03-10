package ca.ubc.ece.salt.sdjsb.checker.specialtype;

import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.ArrayUtils;
import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.ConditionalExpression;
import org.mozilla.javascript.ast.DoLoop;
import org.mozilla.javascript.ast.ForLoop;
import org.mozilla.javascript.ast.IfStatement;
import org.mozilla.javascript.ast.InfixExpression;
import org.mozilla.javascript.ast.KeywordLiteral;
import org.mozilla.javascript.ast.NumberLiteral;
import org.mozilla.javascript.ast.StringLiteral;
import org.mozilla.javascript.ast.WhileLoop;

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
	 * Check if the node is part of a conditional expression and is being
	 * checked if it is truthy or falsey.
	 * @param node
	 */
	public static boolean isFalsey(AstNode node) {

		AstNode parent = node.getParent();
		String identifier = CheckerUtilities.getIdentifier(node);
		
		if(identifier == null) return false;
		
		/* If this is a direct child of a branch statement's condition, it is a
		 * truthy/falsey identifier. */
		if(parent instanceof IfStatement
		   || parent instanceof DoLoop
		   || parent instanceof ForLoop
		   || parent instanceof WhileLoop
		   || parent instanceof ConditionalExpression) {
			
			return true;
		}
		
		/* If this is a direct child of an AND or OR operator, it is a
		 * truthy/falsey identifier. */
		if(parent instanceof InfixExpression) {
			InfixExpression ie = (InfixExpression) parent;
			if(ie.getOperator() == Token.OR || ie.getOperator() == Token.AND) return true;
		}
		
		return false;
	}

	/**
	 * Checks if the identifier is used in the branch. 
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
	 * Gets the branch statement who's condition contains the given node.
	 * @param node The node that has been inserted into the destination tree.
	 * @return The branch statement (i.e. if, do, while, for, condition) or
	 * 		   null if the node is not part of a branch condition statement.
	 */
	public static AstNode getBranchStatement(AstNode node) {
		AstNode parent = node.getParent();
        
        /* Walk up the tree until we get to the branch statement. */
        while(true) {
            if(parent instanceof IfStatement) { 
                if(SpecialTypeCheckerUtilities.contains(((IfStatement) parent).getCondition(), node)) return parent; 
                else return null;
            }
            if(parent instanceof DoLoop) { 
                if(SpecialTypeCheckerUtilities.contains(((DoLoop) parent).getCondition(), node)) return parent; 
                else return null;
            }
            if(parent instanceof ForLoop) {
                if(SpecialTypeCheckerUtilities.contains(((ForLoop) parent).getCondition(), node)) return parent;
                else return null;
            }
            if(parent instanceof WhileLoop) {
                if(SpecialTypeCheckerUtilities.contains(((WhileLoop) parent).getCondition(), node)) return parent;
                else return null;
            }
            if(parent instanceof ConditionalExpression) { 
                if(SpecialTypeCheckerUtilities.contains(((ConditionalExpression) parent).getTestExpression(), node)) return parent; 
                else return null;
            }
            if(parent instanceof AstRoot) return null; // The branch statement was not found.
            parent = parent.getParent();
        }
	}
	
	/**
	 * Checks if one AstNode is a child of another AstNode.
	 * @param parent The parent AstNode to check.
	 * @param child The child AstNode to look for.
	 * @return True if {@code parent} contains {@code child}.
	 */
	public static boolean contains(AstNode parent, AstNode child) {
		ContainsTreeVisitor visitor = new ContainsTreeVisitor(child);
		parent.visit(visitor);
		return visitor.contains;
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
