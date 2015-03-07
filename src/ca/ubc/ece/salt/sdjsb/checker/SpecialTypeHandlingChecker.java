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

        /* If the node is a name, we need to handle two cases:
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
			if(Utilities.isEquivalenceOperator(ie.getOperator())){
				
				String identifier = Utilities.getIdentifier(ie.getLeft());
				
				if(identifier != null) {

                    Map<String, SpecialType> variableIdentifiers = new TreeMap<String, SpecialType>();
                    variableIdentifiers.put(identifier, type); 
                    
                    /* Walk up the tree until we get to the branch statement. */
                    while(true) {
                    	if(parent instanceof IfStatement) { break; }
                    	if(parent instanceof DoLoop) { break; }
                    	if(parent instanceof ForLoop) { break; }
                    	if(parent instanceof WhileLoop) { break; }
                    	if(parent instanceof ConditionalExpression) { break; }
                    	if(parent instanceof AstRoot) return; // The branch statement was not found.
                    	parent = parent.getParent();
                    }

                    /* Remove any variable identifiers from the map that have uses
                     * added inside one of the branches. */
                    UseTreeVisitor useVisitor = new UseTreeVisitor(variableIdentifiers);
                    parent.visit(useVisitor);
                    
                    /* Add the remaining variable identifiers to the comparison map. */
                    for(String variableIdentifier : variableIdentifiers.keySet()){
                        this.comparisons.add(variableIdentifier, variableIdentifiers.get(variableIdentifier));
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
	 * Returns the special type of the AstNode.
	 * @param node The AstNode to check.
	 * @return The SpecialType of the AstNode, or null if the node is not
	 * 		   a Name node that holds a special type keyword or value.
	 */
	private static SpecialType getSpecialType(AstNode node) {
		
            String name = Utilities.getIdentifier(node);
            
            if(name != null) {

            	if(name.equals("undefined")) return SpecialType.UNDEFINED;
            	else if(name.equals("null")) return SpecialType.NULL;
            	else if(name.equals("NaN")) return SpecialType.NAN;
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
