package ca.ubc.ece.salt.sdjsb.analysis.globaltolocal;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.ScriptNode;
import org.mozilla.javascript.ast.VariableDeclaration;
import org.mozilla.javascript.ast.VariableInitializer;

import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode.ChangeType;
import ca.ubc.ece.salt.sdjsb.analysis.flow.PathInsensitiveFlowAnalysis;
import ca.ubc.ece.salt.sdjsb.analysis.scope.Scope;
import ca.ubc.ece.salt.sdjsb.cfg.CFGEdge;
import ca.ubc.ece.salt.sdjsb.cfg.CFGNode;

public class GlobalToLocalFlowAnalysis extends PathInsensitiveFlowAnalysis<GlobalToLocalLatticeElement> {
	
	/** Stores the possible not defined variable repairs. */
	List<GlobalToLocal> notDefinedRepairs;
	
	public GlobalToLocalFlowAnalysis() {
		this.notDefinedRepairs = new LinkedList<GlobalToLocal>();
	}

	@Override
	public GlobalToLocalLatticeElement entryValue(ScriptNode function) {
		return new GlobalToLocalLatticeElement();
	}

	@Override
	public void transfer(CFGEdge edge, GlobalToLocalLatticeElement sourceLE, Scope scope) {

		AstNode statement = (AstNode)edge.getCondition();
		
		if(statement == null) return;

		/* Check if there are any moved or unchanged variable uses that were
		 * newly defined on the path. */
		
		Set<String> usedIdentifiers = VariableNodeVisitor.getUsedVariables(statement);
		
		for(String usedIdentifier : usedIdentifiers) {
			
            /* Trigger an alert! */
			if(sourceLE.inserted.contains(usedIdentifier)) {
				this.notDefinedRepairs.add(new GlobalToLocal(scope, usedIdentifier));
			}
			
		}

	}

	@Override
	public void transfer(CFGNode node, GlobalToLocalLatticeElement sourceLE, Scope scope) {
		
		AstNode statement = (AstNode)node.getStatement();


		if(statement instanceof VariableDeclaration) { 
			
			/* The function should not be a new function. If it is, then
			 * declaring new variables for it makes sense and is not considered
			 * a repair. */
			
			if(scope.scope instanceof FunctionNode) {
				
				FunctionNode function = (FunctionNode) scope.scope;
				if(function.getChangeType() == ChangeType.INSERTED) return;
				
			}
			
            /* Add inserted variable declarations to the inserted and deleted sets. */

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
                Name name = (Name) variable.getTarget();
                sourceLE.inserted.add(name.getIdentifier());
            }

		}

		/* Check if there are any moved or unchanged variable uses that were
		 * newly defined on the path. */
		
		Set<String> usedIdentifiers = VariableNodeVisitor.getUsedVariables(statement);
		
		for(String usedIdentifier : usedIdentifiers) {
			
            /* Trigger an alert! */
			if(sourceLE.inserted.contains(usedIdentifier)) {
				this.notDefinedRepairs.add(new GlobalToLocal(scope, usedIdentifier));
			}
			
		}
		
	}

	@Override
	public GlobalToLocalLatticeElement copy(GlobalToLocalLatticeElement le) {
		return GlobalToLocalLatticeElement.copy(le);
	}

	@Override
	protected GlobalToLocalLatticeElement join(GlobalToLocalLatticeElement left,
			GlobalToLocalLatticeElement right) {

		if(left == null) return right;
		else if(right == null) return left;
		
		GlobalToLocalLatticeElement ndle = new GlobalToLocalLatticeElement();
		ndle.inserted.addAll(left.inserted);
		ndle.inserted.addAll(right.inserted);
		
		return ndle;
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
