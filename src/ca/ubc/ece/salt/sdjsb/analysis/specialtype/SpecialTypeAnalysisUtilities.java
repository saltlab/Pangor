package ca.ubc.ece.salt.sdjsb.analysis.specialtype;

import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.InfixExpression;
import org.mozilla.javascript.ast.KeywordLiteral;
import org.mozilla.javascript.ast.NumberLiteral;
import org.mozilla.javascript.ast.ParenthesizedExpression;
import org.mozilla.javascript.ast.StringLiteral;
import org.mozilla.javascript.ast.UnaryExpression;

import ca.ubc.ece.salt.sdjsb.alert.SpecialTypeAlert.SpecialType;
import ca.ubc.ece.salt.sdjsb.analysis.AnalysisUtilities;

public class SpecialTypeAnalysisUtilities {

	/**
	 * Returns the special type of the AstNode.
	 * @param node The AstNode to check.
	 * @return The SpecialType of the AstNode, or null if the node is not
	 * 		   a Name node that holds a special type keyword or value.
	 */
	public static SpecialType getSpecialType(AstNode node) {
		
        String name = AnalysisUtilities.getIdentifier(node);
        
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
	 * Returns the special type identified by the string literal.
	 * @param stringLiteral
	 * @return
	 */
	public static SpecialType getSpecialTypeString(StringLiteral stringLiteral) {
		
		switch(stringLiteral.getValue()) {
		case "undefined":
		
		}
		return null;
	}

	/**
	 * @param condition the top level branch condition.
	 * @param node a child of the branch condition.
	 * @return the details of the special type check represented by the given
	 * 		   node, or null if it is not a special type check.
	 */
	public static SpecialTypeCheck getSpecialTypeCheck(AstNode condition, AstNode node) {
		
		/* Is this part of some binary operation? */
		if(node instanceof InfixExpression) {
			InfixExpression ie = (InfixExpression) node;
			
			/* Is the operator an equivalence operator? */
			if(AnalysisUtilities.isEquivalenceOperator(ie.getOperator())) {
				String identifier = null;
				SpecialType specialType = null;
				
				/* Get the identifier and the special type. */
				System.out.println(ie.toSource());
				if(getSpecialType(ie.getLeft()) != null) {
					specialType = getSpecialType(ie.getLeft());
					identifier = AnalysisUtilities.getIdentifier(ie.getRight());
				}
				else if(getSpecialType(ie.getRight()) != null) {
					specialType = getSpecialType(ie.getRight());
					identifier = AnalysisUtilities.getIdentifier(ie.getLeft());
				}
				else {
					/* Handle the 'typeof' case. */
                     UnaryExpression ue = null;
                     StringLiteral sl = null;

					if(ie.getLeft() instanceof UnaryExpression && ie.getRight() instanceof StringLiteral) {
						 ue = (UnaryExpression)ie.getLeft();
						 sl = (StringLiteral)ie.getRight();
					}
					else if(ie.getRight() instanceof UnaryExpression && ie.getLeft() instanceof StringLiteral) {
						 ue = (UnaryExpression)ie.getRight();
						 sl = (StringLiteral)ie.getLeft();
					}

					if(ue != null && sl != null) {
                         if(ue.getOperator() == Token.TYPEOF && sl.getValue().equals("undefined")) {
                        	 specialType = SpecialType.UNDEFINED;
                        	 identifier = AnalysisUtilities.getIdentifier(ue.getOperand());
                         }
					}
				}

                if(identifier != null && specialType != null) {
                	
                    /* Check if this branches on true or false. */
                    BranchesOn branchesOn;
                    
                    /* The special type. */
                    boolean isSpecialType = false;

                    /* Get the value that this node evaluates to on the branch. */
                    if(ie == condition) branchesOn = BranchesOn.TRUE;
                    else branchesOn = SpecialTypeAnalysisUtilities.branchesOn(condition, ie.getParent());
                    
                    if(branchesOn == BranchesOn.UNKNOWN) return null;

                    if(ie.getOperator() == Token.EQ || ie.getOperator() == Token.SHEQ) {
                    	isSpecialType = branchesOn == BranchesOn.TRUE ? true : false;
                    }
                    else {
                    	isSpecialType = branchesOn == BranchesOn.TRUE ? false : true;
                    }
                    	
                    if(branchesOn != BranchesOn.UNKNOWN) 
                    	return new SpecialTypeCheck(identifier, specialType, isSpecialType);
                    
                }
				
			}
				
		}
		
		/* Is this a 'falsey' identifier? */
		else {
			
			String identifier = AnalysisUtilities.getIdentifier(node);

			if(identifier != null) {
				BranchesOn branchesOn;

                if(node == condition) branchesOn = BranchesOn.TRUE;
                else branchesOn = SpecialTypeAnalysisUtilities.branchesOn(condition, node.getParent());

                if(branchesOn == BranchesOn.TRUE) 
                	return new SpecialTypeCheck(identifier, SpecialType.FALSEY, false);
                else if(branchesOn == BranchesOn.FALSE) 
                	return new SpecialTypeCheck(identifier, SpecialType.FALSEY, true);
			}
			
		}
		
		return null;
	}
	
	/**
	 * Determine what the value of the child node needs to be for this node to
	 * evaluate to true (i.e., to execute this branch). 
	 * 
	 * If the child is part
	 * of an or expression or an equals expression that does not compare it to
	 * a boolean, then we cannot reason about the value of the child node on
	 * this branch and we return {@code BranchesOn.UNKNOWN}.
	 * @param node the parent of the condition we are evaluating.
	 * @return the value that the condition must evaluate to in order for the 
	 * 		   branch to be taken.
	 */
	public static BranchesOn branchesOn(AstNode condition, AstNode node) {
		
		BranchesOn branchesOn = BranchesOn.UNKNOWN;
		
		if(node instanceof UnaryExpression) {

			UnaryExpression ue = (UnaryExpression)node;
			if(ue.getOperator() == Token.NOT) {
				branchesOn = BranchesOn.FALSE;
			}

		}
		
		else if(node instanceof InfixExpression) {
			InfixExpression ie = (InfixExpression)node;
			
			if(ie.getType() == Token.EQ || ie.getType() == Token.SHEQ) {

                if(ie.getLeft() instanceof KeywordLiteral) {

                    KeywordLiteral kl = (KeywordLiteral)ie.getLeft();
                    if(kl.getType() == Token.TRUE) {
                        branchesOn = BranchesOn.TRUE;
                    }
                    else if(kl.getType() == Token.FALSE) {
                        branchesOn = BranchesOn.FALSE;
                    }

                }
                
			}

			else if(ie.getType() == Token.NE || ie.getType() == Token.SHNE) {

                if(ie.getLeft() instanceof KeywordLiteral) {

                    KeywordLiteral kl = (KeywordLiteral)ie.getLeft();
                    if(kl.getType() == Token.TRUE) {
                        branchesOn = BranchesOn.FALSE;
                    }
                    else if(kl.getType() == Token.FALSE) {
                        branchesOn = BranchesOn.TRUE;
                    }

                }
                
			}
			
			else if(ie.getType() == Token.AND) {
				
                branchesOn = BranchesOn.TRUE;
				
			}

		}
		
		else if(node instanceof ParenthesizedExpression) {
			
            branchesOn = BranchesOn.TRUE;
			
		}
		
		if(node == condition) return branchesOn;

		switch(branchesOn) {
		case TRUE:
            return branchesOn(condition, node.getParent());
		case FALSE:
            return neg(branchesOn(condition, node.getParent()));
        default:
            return BranchesOn.UNKNOWN;
		}
		
	}
	
	/**
	 * @param val the value to negate.
	 * @return the negation of the BranchesOn value.
	 */
	public static BranchesOn neg(BranchesOn val) {

		if(val == BranchesOn.UNKNOWN) return val;
		if(val == BranchesOn.TRUE) return BranchesOn.FALSE;
		return BranchesOn.TRUE;
		
	}
	
	/**
	 * The value that a condition must evaluate to in order for the branch to
	 * be taken.
	 */
	public enum BranchesOn {
		TRUE,
		FALSE,
		UNKNOWN
	}
	
	/**
	 * Generates a list of identifers that are assigned to in the tree.
	 * @param node the tree to look for assignments in.
	 * @return the list of identifiers that are assignd to.
	 */
	public static List<Pair<String, AstNode>> getIdentifierAssignments(AstNode node) {

        AssignmentTreeVisitor assignmentVisitor = new AssignmentTreeVisitor();
        node.visit(assignmentVisitor);
        return assignmentVisitor.getAssignedIdentifiers();
		
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
