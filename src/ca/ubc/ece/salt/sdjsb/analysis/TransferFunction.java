package ca.ubc.ece.salt.sdjsb.analysis;

import java.util.Set;

import org.mozilla.javascript.ast.AstNode;

import ca.ubc.ece.salt.sdjsb.analysis.specialtype.SpecialTypeAnalysisUtilities;
import ca.ubc.ece.salt.sdjsb.analysis.specialtype.SpecialTypeCheck;
import ca.ubc.ece.salt.sdjsb.analysis.specialtype.SpecialTypeVisitor;
import ca.ubc.ece.salt.sdjsb.cfg.CFGEdge;
import ca.ubc.ece.salt.sdjsb.cfg.CFGNode;

/**
 * A SpecialTypeTransferFunction
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
public class TransferFunction {
	
	/**
	 * Transfer the lattice element over the CFGEdge.
	 * 
	 * For the SpecialTypeTransferFunction, we check if there is an inserted
	 * special type check. 
	 * @param edge
	 * @return
	 */
	public void transfer(CFGEdge edge, LatticeElement sourceLE) {
		
		AstNode condition = (AstNode)edge.getCondition();
		if(condition == null) return;
		
        System.out.println("Transfering over edge condition: " + condition.toSource());
		
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

	/**
	 * Transfer the lattice element over the CFGNode.
	 * @param node The node to transfer over.
	 * @return
	 */
	public void transfer(CFGNode node, LatticeElement le) {
		
		AstNode statement = (AstNode)node.getStatement();
		System.out.println("Transfering over node: " + statement.toSource());
		System.out.println("Non-special types:  " + le.nonSpecialTypes);
		System.out.println("Special types:  " + le.specialTypes);
		
		/* Check if the statement has a moved or unchanged identifier use. */
        Set<String> usedIdentifiers = SpecialTypeAnalysisUtilities.getUsedIdentifiers(statement);

        for(String identifier : le.nonSpecialTypes.keySet()) {
        	if(usedIdentifiers.contains(identifier)) {
        		
        		/* Trigger an alert! */
        		System.out.println("ALERT: A " + le.nonSpecialTypes.get(identifier) + " check was inserted for " + identifier);
        		le.nonSpecialTypes.remove(identifier);

        	}
        }
        
        /* Check if the statement has an assignment. */
        Set<String> assignedIdentifiers = SpecialTypeAnalysisUtilities.getIdentifierAssignments(statement);
        
        for(String identifier : le.nonSpecialTypes.keySet()) {
        	if(assignedIdentifiers.contains(identifier)) {
        		
        		/* Remove the identifier. */
        		le.nonSpecialTypes.remove(identifier);

        	}
        }

	}
	
}
