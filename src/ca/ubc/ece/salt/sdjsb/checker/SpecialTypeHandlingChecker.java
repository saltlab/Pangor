package ca.ubc.ece.salt.sdjsb.checker;

import java.util.EnumSet;

import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.IfStatement;
import org.mozilla.javascript.ast.InfixExpression;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.NodeVisitor;
import org.mozilla.javascript.ast.NumberLiteral;
import org.mozilla.javascript.ast.StringLiteral;
import org.mozilla.javascript.ast.VariableDeclaration;
import org.mozilla.javascript.ast.VariableInitializer;

import ca.ubc.ece.salt.sdjsb.checker.SpecialTypeMap.SpecialType;

/**
 * Detects repairs that fix special type handling bugs. These include bugs
 * where variables that could be assigned special JavaScript types (undefined, 
 * null, blank and NaN) are dereferenced without first being checked.
 * 
 * For example, if a repair introduces a statement {@code if(a == undefined)}
 * without introducing a statement that assigns {@code a} to {@code undefined},
 * either the repair fixes a special type handling bug or the repair
 * incorrectly handles the type. Either way, the developer has incorrectly used
 * a special type at some point.
 * 
 * @author qhanam
 */
public class SpecialTypeHandlingChecker extends AbstractChecker {
	
	private static final String TYPE = "STH";
	
	/**
	 * Keeps track of assignments of variables to special types that have
	 * been introduced in the repair.
	 */
	private SpecialTypeMap assignments;
	
	/**
	 * Keeps track of comparisons between a variable and a special type that
	 * have been introduced in the repair.
	 */
	private SpecialTypeMap comparisons;
	
	public SpecialTypeHandlingChecker() {
		this.assignments = new SpecialTypeMap();
		this.comparisons = new SpecialTypeMap();
	}

	@Override
	public void sourceDelete(AstNode node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sourceUpdate(AstNode node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sourceMove(AstNode node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void destinationUpdate(AstNode node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void destinationMove(AstNode node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void destinationInsert(AstNode node) {

        AstNode assignment = null;
        AstNode condition = null;

        if (node instanceof IfStatement) {
            IfStatement ifStatement = (IfStatement) node;
            condition = ifStatement.getCondition();
        } else if(node instanceof VariableDeclaration) {
            assignment = node;
        } else if(node instanceof VariableInitializer) {
        	assignment = node;
        } else if(node instanceof InfixExpression) {
        	assignment = node;
        }
					
		if(assignment != null) {
            AssignmentTreeVisitor visitor = new AssignmentTreeVisitor();
            assignment.visit(visitor);
		} else if(condition != null) {
            ConditionalTreeVisitor visitor = new ConditionalTreeVisitor();
            condition.visit(visitor);
        }
	}

	@Override
	public void finished() {
		/* Compare the assignments sets to the comparisons sets and generate
		 * alerts. */
		for(String name : this.comparisons.getNames()) {
			EnumSet<SpecialType> types = this.comparisons.getSet(name);
			for(SpecialType type : types) {
				if(!this.assignments.setContains(name, type)){
					this.registerAlert("TYPE_ERROR_UNDEFINED", "A conditional branch was inserted that checks if a variable is or is not undefined. This could indicate a TypeError is possible in the original code.");
				}
			}
		}
	}

	@Override
	public String getCheckerType() {
		return TYPE;
	}
	
	
	/**
	 * A visitor for finding special type assignments.
	 * @author qhanam
	 */
	private class AssignmentTreeVisitor implements NodeVisitor {
		
		SpecialTypeMap assignments;
		
		public AssignmentTreeVisitor() {
			this.assignments = SpecialTypeHandlingChecker.this.assignments;
		}
		
		public boolean visit(AstNode node) {

			if (node instanceof VariableDeclaration) {
				return true; // Get the variable initializers.
			} else if (node instanceof VariableInitializer) {
				return this.visit((VariableInitializer) node);
			} else if (node instanceof InfixExpression) {
				return this.visit((InfixExpression) node);
			}
			return false;
		}
		
		private boolean visit(VariableInitializer node) {
			this.storeAssignment(node.getTarget(), node.getInitializer());
			return false;
		}
		
		private boolean visit(InfixExpression node) {
			if(node.getOperator() == Token.ASSIGN) {
				this.storeAssignment(node.getLeft(), node.getRight());
			}
			return true;
		}
		
        /**
         * Check the assignment in a variable initializer. If the assignment
         * assigns the value to a special type, store it in the assignment map.
         * @param vi The VariableInitializer node.
         */
        private void storeAssignment(AstNode lhs, AstNode rhs) {

        	String variable = null;
        	SpecialType value = null;
            
        	/* Get the identifier from the left hand side. */
            if (lhs instanceof Name) {
                variable = ((Name) lhs).getIdentifier();
            } else {
                return; // TODO: Handle fields and expressions.
            }
                    
            /* Get the special type from the right hand side (if it is special). */
            if (rhs instanceof Name) {
                String token = ((Name) rhs).getIdentifier();
                if(token.equals("undefined")) value = SpecialType.UNDEFINED;
                else return; // TODO: Handle other special types.
            } else if (rhs instanceof StringLiteral) {
                String literal = ((StringLiteral) rhs).getValue();
                if(literal.isEmpty()) value = SpecialType.BLANK;
                else return;
            } else if (rhs instanceof NumberLiteral) {
            	double literal = ((NumberLiteral) rhs).getNumber();
            	if(literal == 0) value = SpecialType.ZERO;
            	else return;
            } else {
                return; // TODO: Handle expressions (e.g., ConditionalExpression)
            }
                    
            /* Store the assignment in the map. */
            if(variable != null && value != null) {
            	this.assignments.add(variable, value);
            }
        }
	}

	/**
	 * A visitor for finding special type comparisons in the condition part of
	 * conditional statements.
	 * @author qhanam
	 */
    private class ConditionalTreeVisitor implements NodeVisitor {
    	
    	SpecialTypeMap comparisons;
    	
    	public ConditionalTreeVisitor () {
    		this.comparisons = SpecialTypeHandlingChecker.this.comparisons;
    	}
    	
    	/**
    	 * Visits the condition part of a conditional statement and looks for nodes
         * of type InfixExpression (i.e. a boolean operator expression) that use an
         * equivalence operator (i.e., ===, !==, ==, !=).
    	 */
        public boolean visit(AstNode node) {
        	
        	if(node instanceof InfixExpression) {
        		InfixExpression ie = (InfixExpression) node;

        		if(isEquivalenceOperator(ie.getOperator())) {

        			if(ie.getLeft() instanceof Name && ie.getRight() instanceof Name) {

        				String left = ((Name) ie.getLeft()).getIdentifier();
        				String right = ((Name) ie.getRight()).getIdentifier();
        				
        				if(left.equals("undefined")) { 
        					this.comparisons.add(right, SpecialType.UNDEFINED);
                        }
        				else if(right.equals("undefined")) { 
        					this.comparisons.add(left, SpecialType.UNDEFINED);
                        }
        			}         			
        		}
        	}

        	return true;
        }
        
        private boolean isEquivalenceOperator(int tokenType) {
            if(tokenType == Token.SHEQ || tokenType == Token.SHNE
            	|| tokenType == Token.EQ || tokenType == Token.NE) {
            	return true;
            }
            return false;
        }

    }
	
}
