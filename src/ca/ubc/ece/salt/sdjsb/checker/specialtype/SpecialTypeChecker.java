package ca.ubc.ece.salt.sdjsb.checker.specialtype;

import java.util.EnumSet;

import org.mozilla.javascript.ast.Assignment;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.InfixExpression;
import org.mozilla.javascript.ast.VariableInitializer;

import ca.ubc.ece.salt.sdjsb.checker.AbstractChecker;
import ca.ubc.ece.salt.sdjsb.checker.CheckerContext;
import ca.ubc.ece.salt.sdjsb.checker.CheckerUtilities;
import ca.ubc.ece.salt.sdjsb.checker.specialtype.SpecialTypeMap.SpecialType;

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
public class SpecialTypeChecker extends AbstractChecker {
	
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
	
	public SpecialTypeChecker(CheckerContext context) {
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

		SpecialType type = SpecialTypeCheckerUtilities.getSpecialType(node);
		
		/* This could be a falsey check. */
		if(type == null) {
			this.storeFalsey(node);
		}
		/* This could be an assignment or a comparison involving a special type. */
		else {
            this.storeAssignment(node);
            this.storeComparison(node, type);
		}
		
	}
	
	/**
	 * If the node:
	 * 	1. Is an identifier that evaluates to true or false.
	 *  2. Is part of a branch statement's condition.
	 *  3. Is not used inside the branch.
	 * Then store the comparison. 
	 * @param node
	 */
	private void storeFalsey(AstNode node) {
		String identifier = CheckerUtilities.getIdentifier(node);
		
		if(identifier != null && SpecialTypeCheckerUtilities.isFalsey(node)) {

            AstNode branchStatement = SpecialTypeCheckerUtilities.getBranchStatement(node);
            
            if(branchStatement != null && !SpecialTypeCheckerUtilities.isUsed(this.context, branchStatement, identifier)) {
                this.comparisons.add(identifier, SpecialType.FALSEY);
            }
			
		}
	}
	
	/**
	 * If the node is part of a comparison, store the comparison.
	 * @param node The special type node. 
	 * @param type The special type being compared.
	 */
	private void storeComparison(AstNode node, SpecialType type) {
		AstNode parent = node.getParent();
		
		/* Handle comparisons to a special type. */
		if(parent instanceof InfixExpression) {
			InfixExpression ie = (InfixExpression) parent;
			
			/* The comparison must be an equivalence comparison */
			if(SpecialTypeCheckerUtilities.isEquivalenceOperator(ie.getOperator())) {
				
				String identifier = CheckerUtilities.getIdentifier(ie.getLeft());
				
				if(identifier != null) {

                    AstNode branchStatement = SpecialTypeCheckerUtilities.getBranchStatement(node);
                    
                    if(branchStatement != null && !SpecialTypeCheckerUtilities.isUsed(this.context, branchStatement, identifier)) {
                        this.comparisons.add(identifier, type);
                    }
					
				}
			}
		}
	}
	
	/**
	 * If the node is part of an assignment, store the assignment.
	 * @param node The special type node.
	 */
	private void storeAssignment(AstNode node) {
		AstNode parent = node.getParent();
		
		if(parent instanceof Assignment) {
			Assignment assignment = (Assignment) parent;
			this.storeAssignment(assignment.getLeft(), assignment.getRight());
		}
		else if(parent instanceof VariableInitializer) {
			VariableInitializer vi = (VariableInitializer) parent;
			this.storeAssignment(vi.getTarget(), vi.getInitializer());
			
		}
	}

    /**
     * Check the assignment in a variable initializer. If the assignment
     * assigns the value to a special type, store it in the assignment map.
     * @param leftNode The node on the left hand of the assignment.
     * @param rightNode The node on the right hand of the assignment.
     */
    private void storeAssignment(AstNode leftNode, AstNode rightNode) {

        String identifier = CheckerUtilities.getIdentifier(leftNode);
        SpecialType value = SpecialTypeCheckerUtilities.getSpecialType(rightNode);
        
        if(identifier == null || value == null) return;
        
        /* Store the assignment in the map. */
        this.assignments.add(identifier, value);
    }
    
	@Override
	public void pre() { 
		return; 
	}
	
	@Override
	public void post() {
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
	
	
}
