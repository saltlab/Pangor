package ca.ubc.ece.salt.sdjsb.checker;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.Assignment;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.ExpressionStatement;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.IfStatement;
import org.mozilla.javascript.ast.InfixExpression;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.NodeVisitor;
import org.mozilla.javascript.ast.NumberLiteral;
import org.mozilla.javascript.ast.PropertyGet;
import org.mozilla.javascript.ast.StringLiteral;
import org.mozilla.javascript.ast.VariableDeclaration;
import org.mozilla.javascript.ast.VariableInitializer;

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

        if (node instanceof IfStatement) {
        	destinationBranchInsert(node);
        } else if(node instanceof VariableDeclaration) {
            destinationAssignmentInsert(node);
        } else if(node instanceof VariableInitializer) {
            destinationAssignmentInsert(node);
        } else if(node instanceof InfixExpression) {
            destinationAssignmentInsert(node);
        }
	}
	
	/**
	 * Handles the case where an assignment statement (i.e. a = b) is inserted
	 * into the AST.
	 * 
	 * TODO: This should be a visitor in case assignments occur in a child node.
	 * 
	 * @param node A statement with an assignment.
	 */
	private void destinationAssignmentInsert(AstNode node) {
        AstNode assignment = null;

		if(node instanceof VariableDeclaration) {
            assignment = node;
        } else if(node instanceof VariableInitializer) {
        	assignment = node;
        } else if(node instanceof InfixExpression) {
        	assignment = node;
        }

		if(assignment != null) {
            AssignmentTreeVisitor visitor = new AssignmentTreeVisitor();
            assignment.visit(visitor);
		}	
	}
	
	/**
	 * Handles the case where a branching statement (e.g., if, while, for) is
	 * inserted into the AST.
	 * @param node A statement with a condition and branch.
	 */
	private void destinationBranchInsert(AstNode node) {
		Map<String, SpecialType> variableIdentifiers;
        AstNode condition = null;
        AstNode block = null;

        if (node instanceof IfStatement) {
            IfStatement ifStatement = (IfStatement) node;
            condition = ifStatement.getCondition();
            block = ifStatement.getThenPart();
        }
		
		if(condition != null) {
            ConditionalTreeVisitor visitor = new ConditionalTreeVisitor();
            condition.visit(visitor);
            variableIdentifiers = visitor.variableIdentifiers;

            if(variableIdentifiers.size() > 0) {
            	
            	/* Remove any variable identifiers from the map that have uses
            	 * added inside one of the branches. */
                UseTreeVisitor useVisitor = new UseTreeVisitor(variableIdentifiers);
                node.visit(useVisitor);
                
                /* Add the remaining variable identifiers to the comparison map. */
                for(String variableIdentifier : variableIdentifiers.keySet()){
                    this.comparisons.add(variableIdentifier, variableIdentifiers.get(variableIdentifier));
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
	 * A visitor for finding variable uses.
	 * 
	 * @author qhanam
	 */
	private class UseTreeVisitor implements NodeVisitor {
		
		private CheckerContext context = SpecialTypeHandlingChecker.this.context;
		private Map<String, SpecialType> variableIdentifiers;
		
		public UseTreeVisitor(Map<String, SpecialType> variableIdentifier) {
			this.variableIdentifiers = variableIdentifier;
		}
		
		public boolean visit(AstNode node) {
			/* If this node is part of a change operation, investigate its
			 * children to see if a variableIdenfifier is used. */

			ChangeType changeType = context.getDstChangeOp(node);
			
			if(changeType == null) return true;
			
			if(changeType == ChangeType.INSERT || changeType == ChangeType.UPDATE) {
				/* Find and remove variable identifiers that have been used in this node. */
				SubUseTreeVisitor subUseTreeVisitor = new SubUseTreeVisitor(this.variableIdentifiers);
				node.visit(subUseTreeVisitor);
			}

			return true;
		}
		
	}
	
	/**
	 * Once we have a tree that has been modified, this visitor finds if any of
	 * the variable identifiers have been used.
	 * @author qhanam
	 *
	 */
	private class SubUseTreeVisitor implements NodeVisitor {

		private Map<String, SpecialType> variableIdentifiers;
		
		public SubUseTreeVisitor(Map<String, SpecialType> variableIdentifier) {
			this.variableIdentifiers = variableIdentifier;
		}

		public boolean visit(AstNode node) {

			/* One of node's ancestors was inserted or updated. Since nodes
			 * inherit the class of their parent (if they themselves aren't
			 * classified), this means:
			 * 	- If the node has not been classified, it has been inserted or
			 * 	  updated so we inspect it.
			 *  - If the node is classified as inserted or updated, we inspect
			 *    it.
			 *  - If the node is classified as moved, we do not inspect it
			 *    because it was present in the original program. */
			ChangeType changeType = context.getDstChangeOp(node);

			if(changeType == ChangeType.MOVE) {
				return false;
			}
			else {
				/* Check if this node is an identifier. */
				check(node);

				/* Investigate the subtrees. */
                if (node instanceof Assignment) {
                    visit(((Assignment)node).getRight());
                    return false;
                } else if (node instanceof InfixExpression) {
                	InfixExpression ie = (InfixExpression) node;
                	
                	/* If this is not a use operator, check that neither side
                	 * is an identifier. */
                	if(!Utilities.isUseOperator(ie.getOperator())) {

                		String left = Utilities.getIdentifier(ie.getLeft());
                		String right = Utilities.getIdentifier(ie.getRight());
                        if(left == null || !this.variableIdentifiers.containsKey(left)) visit(ie.getLeft());
                        if(right == null || !this.variableIdentifiers.containsKey(right)) visit(ie.getRight());
                        
                        return false;
                	}
                	else {
                		/* FIXME: For some reason return true doens't work here. */
                        visit(ie.getLeft());
                        visit(ie.getRight());
                	}
                }
                
                /* Anything else check the subtree. */
                return true;
			}
		}
		
		/**
		 * Checks if the AstNode is an identifier that is in the list of
		 * identifiers that were checked in the parent. If they match,
		 * the identifier has been used, so remove it from the list of
		 * checked identifiers.
		 * @param node
		 */
		public void check(AstNode node) {
            String identifier = Utilities.getIdentifier(node);

            if(identifier != null && this.variableIdentifiers.containsKey(identifier)) {
                /* We have found an instance of a variable use. */
                this.variableIdentifiers.remove(identifier);
            }
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
                else if(token.equals("NaN")) value = SpecialType.NAN;
                else if(token.equals("null")) value = SpecialType.NULL;
                else return;
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
	 * conditional statements. The variable identifiers that are compared are
	 * stored in the member variable {@code variableIdentifiers}.
	 * @author qhanam
	 */
    private class ConditionalTreeVisitor implements NodeVisitor {
    	
    	public Map<String, SpecialType> variableIdentifiers;
    	
    	public ConditionalTreeVisitor () {
    		this.variableIdentifiers = new TreeMap<String, SpecialType>();
    	}
    	
    	/**
    	 * Visits the condition part of a conditional statement and looks for nodes
         * of type InfixExpression (i.e. a boolean operator expression) that use an
         * equivalence operator (i.e., ===, !==, ==, !=).
    	 */
        public boolean visit(AstNode node) {
        	
        	if(node instanceof InfixExpression) {
        		InfixExpression ie = (InfixExpression) node;

        		if(Utilities.isEquivalenceOperator(ie.getOperator())) {
        			
        			String left = Utilities.getIdentifier(ie.getLeft());
        			String right = Utilities.getIdentifier(ie.getRight());

        			if(left != null && right != null) {
                        System.out.println("left = " + left);
                        System.out.println("right = " + right);
        				if(left.equals("undefined")) { 
        					this.variableIdentifiers.put(right, SpecialType.UNDEFINED);
                        }
        				else if(right.equals("undefined")) { 
        					this.variableIdentifiers.put(left, SpecialType.UNDEFINED);
                        }
        			}         			
        		}
        	}

        	return true;
        }
        

    }
    
	
}
