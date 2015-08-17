package ca.ubc.ece.salt.sdjsb.analysis.specialtype;

import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.ConditionalExpression;
import org.mozilla.javascript.ast.DoLoop;
import org.mozilla.javascript.ast.ForLoop;
import org.mozilla.javascript.ast.IfStatement;
import org.mozilla.javascript.ast.InfixExpression;
import org.mozilla.javascript.ast.KeywordLiteral;
import org.mozilla.javascript.ast.NumberLiteral;
import org.mozilla.javascript.ast.ParenthesizedExpression;
import org.mozilla.javascript.ast.StringLiteral;
import org.mozilla.javascript.ast.UnaryExpression;
import org.mozilla.javascript.ast.WhileLoop;

import ca.ubc.ece.salt.sdjsb.analysis.AnalysisUtilities;
import ca.ubc.ece.salt.sdjsb.classify.alert.SpecialTypeAlert.SpecialType;

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
                         else if(ue.getOperator() == Token.TYPEOF && sl.getValue().equals("function")) {
                        	 specialType = SpecialType.FUNCTION;
                        	 identifier = AnalysisUtilities.getIdentifier(ue.getOperand());
                         }
					}
				}

                if(identifier != null && specialType != null) {

                    /* Check if this branches on true or false. */
                    BranchesOn branchesOn;

                    /* Get the value that this node evaluates to on the branch. */
                    if(ie == condition) branchesOn = BranchesOn.TRUE;
                    else branchesOn = SpecialTypeAnalysisUtilities.evaluatesTo(condition, ie.getParent(), BranchesOn.TRUE);

                    /* Handle the function case, where we want to dereference if the value IS a function. */
                    if(specialType == SpecialType.FUNCTION) {
                    	switch(branchesOn) {
                    	case TRUE:
                    		branchesOn = BranchesOn.FALSE;
							break;
                    	case FALSE:
                    		branchesOn = BranchesOn.TRUE;
							break;
                    	case TRUE_AND:
                    		branchesOn = BranchesOn.FALSE_OR;
							break;
                    	case FALSE_AND:
                    		branchesOn = BranchesOn.TRUE_OR;
							break;
                    	case TRUE_OR:
                    		branchesOn = BranchesOn.FALSE_AND;
							break;
                    	case FALSE_OR:
                    		branchesOn = BranchesOn.TRUE_AND;
							break;
						default:
							break;
                    	}
                    }

                    /* Determine whether this is a special type (if it is in a comparison). */
					boolean isSpecialType = true;
					if(branchesOn == BranchesOn.TRUE || branchesOn == BranchesOn.TRUE_AND) {
						isSpecialType = true;
					}
					else if(branchesOn == BranchesOn.FALSE || branchesOn == BranchesOn.FALSE_AND){
						isSpecialType = false;
					}

                    /* If the condition needs to definitely be true or false to branch, return the special type. */
                    if(ie.getOperator() == Token.NE || ie.getOperator() == Token.SHNE) {
                    	isSpecialType = !isSpecialType;
                    }

                    if(ie.getOperator() == Token.NE || ie.getOperator() == Token.EQ) {
						/* This is a value check, so it could return true for multiple values. */
						switch(specialType) {
						case UNDEFINED:
						case NULL:
							return new SpecialTypeCheck(identifier, SpecialType.NO_VALUE, isSpecialType);
						case BLANK:
						case ZERO:
						case EMPTY_ARRAY:
							return new SpecialTypeCheck(identifier, SpecialType.EMPTY, isSpecialType);
						default:
							return new SpecialTypeCheck(identifier, specialType, isSpecialType);
						}
                    }
                    else if(ie.getOperator() == Token.SHNE || ie.getOperator() == Token.SHEQ) {
                    	/* This is a value and type check. */
                    	return new SpecialTypeCheck(identifier, specialType, isSpecialType);
                    }

                }

			}

		}

		/* Is this a 'falsey' identifier? */
		else {

			String identifier = AnalysisUtilities.getIdentifier(node);

			if(identifier != null) {
				BranchesOn branchesOn;

                if(node == condition) branchesOn = BranchesOn.TRUE;
                else branchesOn = SpecialTypeAnalysisUtilities.evaluatesTo(condition, node.getParent(), BranchesOn.TRUE);

                /* If the condition needs to definitely be true or false to branch, return the special type. */
                if(branchesOn == BranchesOn.TRUE || branchesOn == BranchesOn.TRUE_AND){
                    return new SpecialTypeCheck(identifier, SpecialType.FALSEY, false);
                }
                else if(branchesOn == BranchesOn.FALSE || branchesOn == BranchesOn.FALSE_AND){
                    return new SpecialTypeCheck(identifier, SpecialType.FALSEY, true);
                }

			}

		}

		return null;
	}

	/**
	 * Determine what the value of the child node needs to be for this node to
	 * evaluate to true (i.e., to execute this branch).
	 * @param node the parent of the condition we are evaluating.
	 * @return the value that the condition must evaluate to in order for the
	 * 		   branch to be taken.
	 */
	public static BranchesOn evaluatesTo(AstNode condition, AstNode node, BranchesOn current) {

		if(node == condition.getParent()) return current;

		if(node instanceof UnaryExpression) {

			UnaryExpression ue = (UnaryExpression)node;
			if(ue.getOperator() == Token.NOT) {

				switch(current) {
				case TRUE:
					return evaluatesTo(condition, node.getParent(), BranchesOn.FALSE);
				case FALSE:
					return evaluatesTo(condition, node.getParent(), BranchesOn.TRUE);
				case TRUE_AND:
					return evaluatesTo(condition, node.getParent(), BranchesOn.FALSE_OR);
				case FALSE_AND:
					return evaluatesTo(condition, node.getParent(), BranchesOn.TRUE_OR);
				case TRUE_OR:
					return evaluatesTo(condition, node.getParent(), BranchesOn.FALSE_AND);
				case FALSE_OR:
					return evaluatesTo(condition, node.getParent(), BranchesOn.TRUE_AND);
				case UNKNOWN:
					return BranchesOn.UNKNOWN;
				}
			}

		}
		else if(node instanceof ParenthesizedExpression) {

			return evaluatesTo(condition, node.getParent(), current);

		}
		else if(node instanceof InfixExpression) {
			InfixExpression ie = (InfixExpression)node;

			if(ie.getType() == Token.AND) {

				switch(current) {
				case TRUE:
				case TRUE_AND:
					return evaluatesTo(condition, node.getParent(), BranchesOn.TRUE_AND);
				case FALSE:
				case FALSE_AND:
					return evaluatesTo(condition, node.getParent(), BranchesOn.FALSE_AND);
				case TRUE_OR:
				case FALSE_OR:
					return evaluatesTo(condition, node.getParent(), BranchesOn.UNKNOWN);
				case UNKNOWN:
					return BranchesOn.UNKNOWN;
				}

			}
			else if(ie.getType() == Token.OR) {

				switch(current) {
				case TRUE:
				case TRUE_OR:
					return evaluatesTo(condition, node.getParent(), BranchesOn.TRUE_OR);
				case FALSE:
				case FALSE_OR:
					return evaluatesTo(condition, node.getParent(), BranchesOn.FALSE_OR);
				case TRUE_AND:
				case FALSE_AND:
					return evaluatesTo(condition, node.getParent(), BranchesOn.UNKNOWN);
				case UNKNOWN:
					return BranchesOn.UNKNOWN;
				}

			}
		}

		return BranchesOn.UNKNOWN;

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
		TRUE_AND,
		FALSE_AND,
		TRUE_OR,
		FALSE_OR,
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

	/**
	 * Check if the node is part of a conditional expression and is being
	 * checked if it is truthy or falsey.
	 * @param node
	 */
	public static boolean isFalsey(AstNode node) {

		AstNode parent = node.getParent();
		String identifier = AnalysisUtilities.getIdentifier(node);

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
		AstNode comparedTo = AnalysisUtilities.getComparison(node);
		if(comparedTo != null) {
            SpecialType specialType = SpecialTypeAnalysisUtilities.getSpecialType(node);
            if(specialType == null) return false;
		}

		return false;
	}

	/**
	 * @param source The special type checked in the source function.
	 * @param destination The special type checked in the destination function.
	 * @return True if the type check has been strengthened (i.e., falsey to value or value to type).
	 */
	public static boolean isStronger(SpecialType source, SpecialType destination) {

		switch(source) {
		case FALSEY:
			if(destination != SpecialType.FALSEY) return true;
		case NO_VALUE:
			if(destination == SpecialType.UNDEFINED
				|| destination == SpecialType.NULL) return true;
		case EMPTY:
			if(destination == SpecialType.BLANK
				|| destination == SpecialType.ZERO
				|| destination == SpecialType.EMPTY_ARRAY) return true;
		default:
			return false;
		}

	}

	/**
	 * @param source The special type checked in the source function.
	 * @param destination The special type checked in the destination function.
	 * @return True if the type check has been weakend (i.e., type to value or value to falsey).
	 */
	public static boolean isWeaker(SpecialType source, SpecialType destination) {

		switch(destination) {
		case FALSEY:
			if(source != SpecialType.FALSEY) return true;
		case NO_VALUE:
			if(source == SpecialType.UNDEFINED
				|| source == SpecialType.NULL) return true;
		case EMPTY:
			if(source == SpecialType.BLANK
				|| source == SpecialType.ZERO
				|| source == SpecialType.EMPTY_ARRAY) return true;
		default:
			return false;
		}

	}


}
