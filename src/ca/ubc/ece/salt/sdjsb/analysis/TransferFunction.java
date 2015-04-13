package ca.ubc.ece.salt.sdjsb.analysis;

import org.mozilla.javascript.ast.AstNode;

import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode.ChangeType;
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
		
		/* TODO */
		AstNode condition = (AstNode)edge.getCondition();
		if(condition != null)
            System.out.println("Transfering over edge condition: " + condition.toSource());

	}
	
	/**
	 * Transfer the lattice element over the CFGNode.
	 * @param node The node to transfer over.
	 * @return
	 */
	public void transfer(CFGNode node, LatticeElement le) {
		
		/* TODO */
		AstNode statement = (AstNode)node.getStatement();
		System.out.println("Transfering over node: " + statement.toSource());

	}

}
