package ca.ubc.ece.salt.sdjsb.analysis.specialtype;

import java.util.Set;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.ScriptNode;

import ca.ubc.ece.salt.sdjsb.alert.SpecialTypeAlert;
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
		
        //System.out.println("Transfering over edge condition: " + condition.toSource());
		
		/* Check if condition has an inserted special type check and whether
		 * the check evaluates to true or false. */
		SpecialTypeVisitor visitor = new SpecialTypeVisitor(condition);
		condition.visit(visitor);

		/* Add any special type checks to the lattice element. */
		for(SpecialTypeCheck specialTypeCheck : visitor.getSpecialTypeChecks()) {
			if(specialTypeCheck.isSpecialType) {
				sourceLE.specialTypes.put(specialTypeCheck.identifier, specialTypeCheck.specialType);
				//System.out.println(specialTypeCheck.identifier + " is " + specialTypeCheck.specialType);
			}
			else {
				sourceLE.nonSpecialTypes.put(specialTypeCheck.identifier, specialTypeCheck.specialType);
				//System.out.println(specialTypeCheck.identifier + " is not " + specialTypeCheck.specialType);
			}
		}
		
	}

	@Override
	public void transfer(CFGNode node, SpecialTypeLatticeElement sourceLE) {

		AstNode statement = (AstNode)node.getStatement();
//		System.out.println("Transfering over node: " + statement.toSource());
//		System.out.println("Non-special types:  " + sourceLE.nonSpecialTypes);
//		System.out.println("Special types:  " + sourceLE.specialTypes);
		
		/* Check if the statement has a moved or unchanged identifier use. */
        Set<String> usedIdentifiers = SpecialTypeAnalysisUtilities.getUsedIdentifiers(statement);

        for(String identifier : sourceLE.nonSpecialTypes.keySet()) {
        	if(usedIdentifiers.contains(identifier)) {
        		
        		/* Trigger an alert! */
        		this.registerAlert(new SpecialTypeAlert("STH", identifier, sourceLE.nonSpecialTypes.get(identifier)) );
        		
        		/* Remove the identifier so we don't log redundant alerts. */
        		sourceLE.nonSpecialTypes.remove(identifier);

        	}
        }
        
        /* Check if the statement has an assignment. */
        Set<String> assignedIdentifiers = SpecialTypeAnalysisUtilities.getIdentifierAssignments(statement);
        
        for(String identifier : sourceLE.nonSpecialTypes.keySet()) {
        	if(assignedIdentifiers.contains(identifier)) {
        		
        		/* Remove the identifier. */
        		sourceLE.nonSpecialTypes.remove(identifier);

        	}
        }
		
	}

	@Override
	public SpecialTypeLatticeElement copy(SpecialTypeLatticeElement le) {
		return SpecialTypeLatticeElement.copy(le);
	}
	
}
