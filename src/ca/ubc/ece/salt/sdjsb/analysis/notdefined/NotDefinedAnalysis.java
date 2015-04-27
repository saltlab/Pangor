package ca.ubc.ece.salt.sdjsb.analysis.notdefined;

import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.ScriptNode;
import org.mozilla.javascript.ast.VariableDeclaration;
import org.mozilla.javascript.ast.VariableInitializer;

import ca.ubc.ece.salt.sdjsb.analysis.flow.Scope;
import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode.ChangeType;
import ca.ubc.ece.salt.sdjsb.alert.NotDefinedAlert;
import ca.ubc.ece.salt.sdjsb.analysis.flow.PathSensitiveFlowAnalysis;
import ca.ubc.ece.salt.sdjsb.cfg.CFGEdge;
import ca.ubc.ece.salt.sdjsb.cfg.CFGNode;

public class NotDefinedAnalysis extends PathSensitiveFlowAnalysis<NotDefinedLatticeElement> {

	@Override
	public NotDefinedLatticeElement entryValue(ScriptNode function) {
		return new NotDefinedLatticeElement();
	}

	@Override
	public void transfer(CFGEdge edge, NotDefinedLatticeElement sourceLE, Stack<Scope> scopeStack) {

		AstNode statement = (AstNode)edge.getCondition();
		
		if(statement == null) return;

		/* Check if there are any moved or unchanged variable uses that were
		 * newly defined on the path. */
		
		Set<String> usedIdentifiers = VariableNodeVisitor.getUsedVariables(statement);
		
		for(String usedIdentifier : usedIdentifiers) {
			
            /* Trigger an alert! */
			if(sourceLE.inserted.contains(usedIdentifier) && !sourceLE.deleted.contains(usedIdentifier)) {
				this.registerAlert(scopeStack.peek().scope, new NotDefinedAlert("ND", usedIdentifier));
			}
			
		}

	}

	@Override
	public void transfer(CFGNode node, NotDefinedLatticeElement sourceLE, Stack<Scope> scopeStack) {
		
		AstNode statement = (AstNode)node.getStatement();

		/* Add inserted and deleted variable declarations to the inserted and
		 * deleted sets. */

		if(statement instanceof VariableDeclaration) { 

			VariableDeclaration vd = (VariableDeclaration) statement;
			List<VariableInitializer> variables = vd.getVariables();

			for(VariableInitializer variable : variables) {
				
				if(variable.getChangeType() == ChangeType.INSERTED && variable.getTarget() instanceof Name) {
                    sourceLE.inserted.add(((Name)variable.getTarget()).getIdentifier());
				}
				else if(variable.getChangeType() == ChangeType.REMOVED && variable.getTarget() instanceof Name) {
                    sourceLE.deleted.add(((Name)variable.getTarget()).getIdentifier());
				}
				
			}

		}
		else if(statement instanceof VariableInitializer) { 

			VariableInitializer variable = (VariableInitializer) statement;

            if(variable.getChangeType() == ChangeType.INSERTED && variable.getTarget() instanceof Name) {
                sourceLE.inserted.add(((Name)variable.getTarget()).getIdentifier());
            }
            else if(variable.getChangeType() == ChangeType.REMOVED && variable.getTarget() instanceof Name) {
                sourceLE.deleted.add(((Name)variable.getTarget()).getIdentifier());
            }

		}

		/* Check if there are any moved or unchanged variable uses that were
		 * newly defined on the path. */
		
		Set<String> usedIdentifiers = VariableNodeVisitor.getUsedVariables(statement);
		
		for(String usedIdentifier : usedIdentifiers) {
			
            /* Trigger an alert! */
			if(sourceLE.inserted.contains(usedIdentifier) && !sourceLE.deleted.contains(usedIdentifier)) {
				this.registerAlert(scopeStack.peek().scope, new NotDefinedAlert("ND", usedIdentifier));
			}
			
		}
		
	}

	@Override
	public NotDefinedLatticeElement copy(NotDefinedLatticeElement le) {
		return NotDefinedLatticeElement.copy(le);
	}

}
