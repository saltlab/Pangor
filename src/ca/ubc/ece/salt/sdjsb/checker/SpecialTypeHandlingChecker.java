package ca.ubc.ece.salt.sdjsb.checker;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.Assignment;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.ConditionalExpression;
import org.mozilla.javascript.ast.DoLoop;
import org.mozilla.javascript.ast.ExpressionStatement;
import org.mozilla.javascript.ast.ForLoop;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.IfStatement;
import org.mozilla.javascript.ast.InfixExpression;
import org.mozilla.javascript.ast.KeywordLiteral;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.NodeVisitor;
import org.mozilla.javascript.ast.NumberLiteral;
import org.mozilla.javascript.ast.PropertyGet;
import org.mozilla.javascript.ast.StringLiteral;
import org.mozilla.javascript.ast.VariableDeclaration;
import org.mozilla.javascript.ast.VariableInitializer;
import org.mozilla.javascript.ast.WhileLoop;

import ca.ubc.ece.salt.sdjsb.checker.CheckerContext.ChangeType;
import ca.ubc.ece.salt.sdjsb.checker.SpecialTypeMap.SpecialType;
import ca.ubc.ece.salt.sdjsb.checker.alert.SpecialTypeAlert;

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
	
	public SpecialTypeHandlingChecker(CheckerContext context) {
		super(context);
		this.assignments = new SpecialTypeMap();
		this.comparisons = new SpecialTypeMap();
	}

	@Override
	public void sourceDelete(AstNode node) { return; }

	@Override
	public void sourceUpdate(AstNode node) { return; }

	@Override
	public void sourceMove(AstNode node) { return; }

	@Override
	public void destinationUpdate(AstNode node) { 
		return; 
    }

	@Override
	public void destinationMove(AstNode node) { 
		return; 
	}

	@Override
	public void destinationInsert(AstNode node) {

        /* If the node is a special type, we need to handle two cases:
         * 	1. A special type node is inserted in an assignment. 
         *  2. A special type node is inserted in a branch condition. */

		SpecialType type = SpecialTypeHandlingChecker.getSpecialType(node);
		
		if(type == null) return;
		
		AstNode parent = node.getParent();

		/* Handle assignments to a special type. */
		if(parent instanceof Assignment || parent instanceof VariableInitializer) {
            AssignmentTreeVisitor visitor = new AssignmentTreeVisitor();
            parent.visit(visitor);
		}
		/* Handle comparisons to a special type. */
		else if(parent instanceof InfixExpression) {
			InfixExpression ie = (InfixExpression) parent;
			
			/* The comparison must be an equivalence comparison
			 * TODO: Also handle the case where we are checking if the node is 'falsey'. */
			if(Utilities.isEquivalenceOperator(ie.getOperator())) {
				
				String identifier = Utilities.getIdentifier(ie.getLeft());
				
				if(identifier != null) {

                    AstNode branchStatement = Utilities.getBranchStatement(node);
                    
                    if(branchStatement != null && !Utilities.isUsed(this.context, branchStatement, identifier)) {
                    	
                        this.comparisons.add(identifier, type);

                    }
					
				}
			}
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
					this.registerAlert(new SpecialTypeAlert(this.getCheckerType(), name, type));
				}
			}
		}
	}

	@Override
	public String getCheckerType() {
		return TYPE;
	}
	
	/**
	 * Check if the node is part of a conditional expression and is being
	 * checked if it is truthy or falsey.
	 * @param node
	 */
	private boolean isFalsey(AstNode node) {

		AstNode parent = node.getParent();
		String identifier = Utilities.getIdentifier(node);
		
		if(identifier == null) return false;
		
		if(parent instanceof IfStatement) return true;
		
		if(parent instanceof InfixExpression) {
			InfixExpression ie = (InfixExpression) parent;
			if(ie.getOperator() == Token.OR || ie.getOperator() == Token.AND) return true;
		}
		
		return false;
	}
	
	/**
	 * Returns the special type of the AstNode.
	 * @param node The AstNode to check.
	 * @return The SpecialType of the AstNode, or null if the node is not
	 * 		   a Name node that holds a special type keyword or value.
	 */
	private static SpecialType getSpecialType(AstNode node) {
		
            String name = Utilities.getIdentifier(node);
            
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
			} else if (node instanceof Assignment) {
				return this.visit((Assignment) node);
			}
			return false;
		}
		
		private boolean visit(VariableInitializer node) {
			this.storeAssignment(node.getTarget(), node.getInitializer());
			return true;
		}
		
		private boolean visit(Assignment node) {
			if(node.getOperator() == Token.ASSIGN) {
				this.storeAssignment(node.getLeft(), node.getRight());
			}
			return true;
		}
		
        /**
         * Check the assignment in a variable initializer. If the assignment
         * assigns the value to a special type, store it in the assignment map.
         * @param leftNode The node on the left hand of the assignment.
         * @param rightNode The node on the right hand of the assignment.
         */
        private void storeAssignment(AstNode leftNode, AstNode rightNode) {

            String identifier = Utilities.getIdentifier(leftNode);
            SpecialType value = SpecialTypeHandlingChecker.getSpecialType(rightNode);
            
            if(identifier == null || value == null) return;
            
            /* Store the assignment in the map. */
            this.assignments.add(identifier, value);
        }
	}
	
}
