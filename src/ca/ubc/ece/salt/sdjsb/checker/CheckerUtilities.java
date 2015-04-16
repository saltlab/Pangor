package ca.ubc.ece.salt.sdjsb.checker;

import java.util.List;

import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.ConditionalExpression;
import org.mozilla.javascript.ast.DoLoop;
import org.mozilla.javascript.ast.ForLoop;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.IfStatement;
import org.mozilla.javascript.ast.InfixExpression;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.ParenthesizedExpression;
import org.mozilla.javascript.ast.WhileLoop;

import ca.ubc.ece.salt.sdjsb.alert.SpecialTypeAlert.SpecialType;
import ca.ubc.ece.salt.sdjsb.checker.specialtype.SpecialTypeCheckerUtilities;

public class CheckerUtilities {
	
	/**
	 * Returns the function signature.
	 * @param function
	 * @return The string representation of the signature.
	 */
	public static String getFunctionSignature(FunctionNode function) {

		List<AstNode> params = function.getParams();
        String signature = "(";

        for(AstNode param : params) {
            if(param instanceof Name) {
                if(!signature.equals("(")) signature += ",";
                signature += ((Name)param).getIdentifier();
            }
        }

        signature += ")";
        return signature;

	}
	
	/**
	 * Returns true if the AstNode is part of the parameter list of any of the
	 * functions in the variables scope.
	 * @param name The variable to check.
	 * @return The parameter declaration if a function in the variable's scope
	 * 		   contains the variable as a parameter (well, a variable with the 
	 * 		   same name anyways, which won't always be correct but close 
	 * 		   enough for us).
	 */
	public static AstNode isParameter(Name name) {
		
		AstNode parent = name.getParent();
		
		while(!(parent instanceof AstRoot)) {
			
			if(parent instanceof FunctionNode) {
				List<AstNode> parameters = ((FunctionNode)parent).getParams();
				for(AstNode parameter : parameters) {
					if(parameter instanceof Name) {
						if(((Name)parameter).getIdentifier().equals(name.getIdentifier())) return parameter;
					}
				}
			}
			
			parent = parent.getParent();
		}
		
		return null;
		
	}
	
	/**
	 * Gets the other side of an equivalence binary operator.
	 * @param node The node that may be part of an equivalence check.
	 * @return The AstNode on the other side of the equivalence operator
	 * 		   or null if the parent is not an equivalence operator.
	 */
	public static AstNode getComparison(AstNode node) {
		AstNode parent = node.getParent();
		
		/* Is this part of some binary operation? */
		if(parent instanceof InfixExpression) {
			InfixExpression ie = (InfixExpression) parent;
			
			/* Is the operator an equivalence operator? */
			if(CheckerUtilities.isEquivalenceOperator(ie.getOperator())) {
				
				if(ie.getRight() == node) {
                    return ie.getLeft(); //CheckerUtilities.getIdentifier(ie.getLeft());
				}
				else {
                    return ie.getRight(); //CheckerUtilities.getIdentifier(ie.getRight());
				}
			}
		}
		
		return null;
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

		/* If this is part of an equivalence operator that is being compared to
		 * a special (falsey) type, it is a truthy/falsey identifier. */
		AstNode comparedTo = getComparison(node);
		if(comparedTo != null) {
            SpecialType specialType = SpecialTypeCheckerUtilities.getSpecialType(node);
            if(specialType == null) return false;
		}
		
		return false;
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
                if(CheckerUtilities.contains(((IfStatement) parent).getCondition(), node)) return parent; 
                else return null;
            }
            if(parent instanceof DoLoop) { 
                if(CheckerUtilities.contains(((DoLoop) parent).getCondition(), node)) return parent; 
                else return null;
            }
            if(parent instanceof ForLoop) {
                if(CheckerUtilities.contains(((ForLoop) parent).getCondition(), node)) return parent;
                else return null;
            }
            if(parent instanceof WhileLoop) {
                if(CheckerUtilities.contains(((WhileLoop) parent).getCondition(), node)) return parent;
                else return null;
            }
            if(parent instanceof ConditionalExpression) { 
                if(CheckerUtilities.contains(((ConditionalExpression) parent).getTestExpression(), node)) return parent; 
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
	 * Returns the top level identifier for a node. If the node is a name, it will
	 * check the parent nodes until it gets to the top of the identifier.
	 * @param node A subnode of an identifier.
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static AstNode getTopLevelFieldIdentifier(AstNode node) throws IllegalArgumentException {
		
		/* If this node is a field or method, get the parent. */
		if(node.getParent() instanceof InfixExpression) {

			InfixExpression ie = (InfixExpression) node.getParent();

			if(CheckerUtilities.isIdentifierOperator(ie.getOperator()) && 
			   !(ie.getLeft() instanceof FunctionCall || ie.getRight() instanceof FunctionCall)) {
				return CheckerUtilities.getTopLevelIdentifier(ie);
			}
			
		}
		
		return node;
		
	}
	
	/**
	 * Returns the top level identifier for a node. If the node is a name, it will
	 * check the parent nodes until it gets to the top of the identifier.
	 * @param node A subnode of an identifier.
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static AstNode getTopLevelIdentifier(AstNode node) throws IllegalArgumentException {
		
		/* If this node is a field or method, get the parent. */
		if(node.getParent() instanceof InfixExpression) {

			InfixExpression ie = (InfixExpression) node.getParent();

			if(CheckerUtilities.isIdentifierOperator(ie.getOperator())) {
				return CheckerUtilities.getTopLevelIdentifier(ie);
			}
			
		}
		
		return node;
		
	}

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
            if(CheckerUtilities.isIdentifierOperator(ie.getOperator())) {
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
	 * Returns the list of variable, field or function identifiers contained
	 * in an OR separated list. Use for getting all the identifiers on the
	 * right hand side of an assignment. 
	 * @param node The node that represents the right hand side of an assignment.
	 * @return The list of variable, field or function identifiers.	 
	 */
	public static List<String> getRHSIdentifiers(AstNode node) {
		IdentifiersTreeVisitor visitor = new IdentifiersTreeVisitor();
		node.visit(visitor);
		return visitor.variableIdentifiers;
		
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
     * Returns true if the operatory for the binary expression is an assignment
     * operator (i.e., an assignment or object 
     * @param tokenType
     * @return
     */
    public static boolean isAssignmentOperator(int tokenType) {
    	if(tokenType == Token.ASSIGN || tokenType == Token.COLON) {
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

}
