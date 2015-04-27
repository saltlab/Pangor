package ca.ubc.ece.salt.sdjsb.analysis.notdefined;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.ScriptNode;
import org.mozilla.javascript.ast.VariableDeclaration;
import org.mozilla.javascript.ast.VariableInitializer;

import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode.ChangeType;
import ca.ubc.ece.salt.sdjsb.analysis.flow.PathSensitiveFlowAnalysis;
import ca.ubc.ece.salt.sdjsb.analysis.scope.Scope;
import ca.ubc.ece.salt.sdjsb.cfg.CFGEdge;
import ca.ubc.ece.salt.sdjsb.cfg.CFGNode;

public class NotDefinedDestinationAnalysis extends PathSensitiveFlowAnalysis<NotDefinedLatticeElement> {
	
	List<GlobalToLocal> notDefinedRepairs;
	
	public NotDefinedDestinationAnalysis() {
		this.notDefinedRepairs = new LinkedList<GlobalToLocal>();
	}

	@Override
	public NotDefinedLatticeElement entryValue(ScriptNode function) {
		return new NotDefinedLatticeElement();
	}

	@Override
	public void transfer(CFGEdge edge, NotDefinedLatticeElement sourceLE, Scope scope) {

		AstNode statement = (AstNode)edge.getCondition();
		
		if(statement == null) return;

		/* Check if there are any moved or unchanged variable uses that were
		 * newly defined on the path. */
		
		Set<String> usedIdentifiers = VariableNodeVisitor.getUsedVariables(statement);
		
		for(String usedIdentifier : usedIdentifiers) {
			
            /* Trigger an alert! */
			if(sourceLE.inserted.contains(usedIdentifier) && !sourceLE.deleted.contains(usedIdentifier)) {
				this.notDefinedRepairs.add(new GlobalToLocal(scope, usedIdentifier));
			}
			
		}

	}

	@Override
	public void transfer(CFGNode node, NotDefinedLatticeElement sourceLE, Scope scope) {
		
		AstNode statement = (AstNode)node.getStatement();

		/* Add inserted and deleted variable declarations to the inserted and
		 * deleted sets. */

		if(statement instanceof VariableDeclaration) { 

			VariableDeclaration vd = (VariableDeclaration) statement;
			List<VariableInitializer> variables = vd.getVariables();

			for(VariableInitializer variable : variables) {
				
				if(variable.getChangeType() == ChangeType.INSERTED && variable.getTarget() instanceof Name) {
					Name name = (Name) variable.getTarget();
                    sourceLE.inserted.add(name.getIdentifier());
				}
				
			}

		}
		else if(statement instanceof VariableInitializer) { 

			VariableInitializer variable = (VariableInitializer) statement;

            if(variable.getChangeType() == ChangeType.INSERTED && variable.getTarget() instanceof Name) {
                sourceLE.inserted.add(((Name)variable.getTarget()).getIdentifier());
            }

		}

		/* Check if there are any moved or unchanged variable uses that were
		 * newly defined on the path. */
		
		Set<String> usedIdentifiers = VariableNodeVisitor.getUsedVariables(statement);
		
		for(String usedIdentifier : usedIdentifiers) {
			
            /* Trigger an alert! */
			if(sourceLE.inserted.contains(usedIdentifier) && !sourceLE.deleted.contains(usedIdentifier)) {
				this.notDefinedRepairs.add(new GlobalToLocal(scope, usedIdentifier));
			}
			
		}
		
	}

	@Override
	public NotDefinedLatticeElement copy(NotDefinedLatticeElement le) {
		return NotDefinedLatticeElement.copy(le);
	}
	
	/**
	 * Stores a variable that has been newly declared on a path but used as a
	 * global at a later point. 
	 */
	 class GlobalToLocal {
		public Scope scope;
		public String identifier;
		
		public GlobalToLocal(Scope scope, String identifier) {
			this.scope = scope;
			this.identifier = identifier;
		}
	}

}
