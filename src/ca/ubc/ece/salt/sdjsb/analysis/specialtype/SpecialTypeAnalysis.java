package ca.ubc.ece.salt.sdjsb.analysis.specialtype;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.ScriptNode;

import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode.ChangeType;
import ca.ubc.ece.salt.sdjsb.alert.SpecialTypeAlert;
import ca.ubc.ece.salt.sdjsb.alert.SpecialTypeAlert.SpecialType;
import ca.ubc.ece.salt.sdjsb.analysis.PathSensitiveFlowAnalysis;
import ca.ubc.ece.salt.sdjsb.cfg.CFGEdge;
import ca.ubc.ece.salt.sdjsb.cfg.CFGNode;

/**
 * A change-sensitive analysis that finds special type repairs in JavaScript.
 * 
 * Not special type path:
 * 	1. New edge conditions that check that an identifier is not a special type.
 *  2. That identifier is used after the check, but before it is assigned and
 *     that use was in the original program.
 *  *. If the variable is used or assigned, we remove it from the map (after
 *     generating an alert if needed).
 *     
 * Special type path:
 * 	1. New edge conditions that check that an identifier is a special type.
 *  2. That identifier is assigned before it is used. The assignment is
 *     inserted and the use is in the original program.
 *     
 */
public class SpecialTypeAnalysis extends PathSensitiveFlowAnalysis<SpecialTypeLatticeElement> {
	
	@Override
	public SpecialTypeLatticeElement entryValue(ScriptNode function) {
		return new SpecialTypeLatticeElement();
	}

	@Override
	public void transfer(CFGEdge edge, SpecialTypeLatticeElement sourceLE) {

		AstNode condition = (AstNode)edge.getCondition();
		if(condition == null) return;
		
		/* Check if condition has an inserted special type check and whether
		 * the check evaluates to true or false. */
		SpecialTypeVisitor visitor = new SpecialTypeVisitor(condition);
		condition.visit(visitor);

		/* Add any special type checks to the lattice element. */
		for(SpecialTypeCheck specialTypeCheck : visitor.getSpecialTypeChecks()) {
			if(specialTypeCheck.isSpecialType) {
				sourceLE.specialTypes.put(specialTypeCheck.identifier, specialTypeCheck.specialType);
				System.out.println(specialTypeCheck.identifier + " is " + specialTypeCheck.specialType);
			}
			else {
				sourceLE.nonSpecialTypes.put(specialTypeCheck.identifier, specialTypeCheck.specialType);
				System.out.println(specialTypeCheck.identifier + " is not " + specialTypeCheck.specialType);
			}
		}
		
	}

	@Override
	public void transfer(CFGNode node, SpecialTypeLatticeElement sourceLE) {

		AstNode statement = (AstNode)node.getStatement();
		
		/* Check if the statement has a moved or unchanged identifier use. */
        Set<String> usedIdentifiers = SpecialTypeAnalysisUtilities.getUsedIdentifiers(statement);

        for(String identifier : sourceLE.nonSpecialTypes.keySet()) {
        	if(usedIdentifiers.contains(identifier)) {
        		
        		/* Check that this identifier hasn't been newly assigned to
        		 * the special type we are checking. */
        		SpecialType assignedTo = sourceLE.assignments.get(identifier);
        		if(assignedTo != SpecialType.FALSEY && assignedTo != sourceLE.nonSpecialTypes.get(identifier)) {
        		
                    /* Trigger an alert! */
                    this.registerAlert(new SpecialTypeAlert("STH", identifier, sourceLE.nonSpecialTypes.get(identifier)) );
                   
        		}
        		
        		/* Remove the identifier so we don't log redundant alerts. */
        		// We can't do this while we're looping through sourceLE elements! Also, we're already getting redundant elements from multiple paths.
        		//sourceLE.nonSpecialTypes.remove(identifier);

        	}
        }
        
        /* Check if the statement has an assignment. */
        List<Pair<String, AstNode>> assignments = SpecialTypeAnalysisUtilities.getIdentifierAssignments(statement);
        
        for(Pair<String, AstNode> assignment : assignments) {
        	
        	SpecialType specialType = SpecialTypeAnalysisUtilities.getSpecialType(assignment.getValue());
        	
        	/* Store the assignment if it is a new special type assignment. */
        	if(specialType != null && (assignment.getValue().getChangeType() == ChangeType.INSERTED 
        			|| assignment.getValue().getChangeType() == ChangeType.REMOVED
        			|| assignment.getValue().getChangeType() == ChangeType.UPDATED)) {
        		sourceLE.assignments.put(assignment.getKey(), specialType);
        	}
        	
        	/* Remove the assignment (if it exists) if it is not (any old
        	 * assignments are no longer relevant). */
        	else {
        		sourceLE.assignments.remove(assignment.getKey());
        	}
        	
        	/* Remove the identifier from the special type set (if it exists). */
        	if(sourceLE.nonSpecialTypes.containsKey(assignment.getKey())) {

        		/* Remove the identifier. */
        		sourceLE.nonSpecialTypes.remove(assignment.getKey());
        		
        	}
        	
        }
		
	}

	@Override
	public SpecialTypeLatticeElement copy(SpecialTypeLatticeElement le) {
		return SpecialTypeLatticeElement.copy(le);
	}
	
}
