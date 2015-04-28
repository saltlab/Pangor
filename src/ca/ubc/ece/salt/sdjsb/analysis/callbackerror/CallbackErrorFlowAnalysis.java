package ca.ubc.ece.salt.sdjsb.analysis.callbackerror;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.ScriptNode;

import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode.ChangeType;
import ca.ubc.ece.salt.sdjsb.alert.SpecialTypeAlert.SpecialType;
import ca.ubc.ece.salt.sdjsb.analysis.AnalysisUtilities;
import ca.ubc.ece.salt.sdjsb.analysis.flow.PathInsensitiveFlowAnalysis;
import ca.ubc.ece.salt.sdjsb.analysis.scope.Scope;
import ca.ubc.ece.salt.sdjsb.analysis.specialtype.SpecialTypeCheck;
import ca.ubc.ece.salt.sdjsb.analysis.specialtype.SpecialTypeVisitor;
import ca.ubc.ece.salt.sdjsb.cfg.CFGEdge;
import ca.ubc.ece.salt.sdjsb.cfg.CFGNode;

public class CallbackErrorFlowAnalysis extends PathInsensitiveFlowAnalysis<CallbackErrorLatticeElement> {
	
	/** Stores the possible callback error check repairs. */
	private Set<CallbackErrorCheck> callbackErrorChecks;
	
	public CallbackErrorFlowAnalysis() {
		this.callbackErrorChecks = new HashSet<CallbackErrorCheck>();
	}
	
	/**
	 * @return The set of possible callback error check repairs (or
	 * anti-patterns if this is the source file analysis.
	 */
	public Set<CallbackErrorCheck> getCallbackErrorChecks() {
		return this.callbackErrorChecks;
	}

	@Override
	protected CallbackErrorLatticeElement join(
			CallbackErrorLatticeElement left, CallbackErrorLatticeElement right) {

		if(left == null) return right;
		else if(right == null) return left;

		return left.join(right);

	}

	@Override
	public CallbackErrorLatticeElement entryValue(ScriptNode node) {

		/* Add any parameters that are not new and htat look like an error 
		 * parameter. */

		CallbackErrorLatticeElement cele = new CallbackErrorLatticeElement();
		
		/* If this is the script (no parameters) or the function was inserted, there is nothing to do. */
		if(!(node instanceof FunctionNode) || node.getChangeType() == ChangeType.INSERTED) return cele;

		/* Look through the parameters to see if there is an unchanged error parameter. */
        FunctionNode function = (FunctionNode) node;
        for(AstNode parameter : function.getParams()) {
            if(parameter instanceof Name) {
                
                /* Match only error parameters for now. */
                Name name = (Name) parameter;
                if(name.getIdentifier().matches("(?i)e(rr(or)?)?") && name.getChangeType() != ChangeType.INSERTED) {
                    
                    /* Add the parameter to the lattice element. */
                    cele.parameters.add(name.getIdentifier());

                }

            }
        }
			
		return cele;

	}

	@Override
	public void transfer(CFGEdge edge, CallbackErrorLatticeElement sourceLE,
			Scope scope) {
		
		AstNode condition = (AstNode) edge.getCondition();

		if(condition == null || !(scope.scope instanceof FunctionNode)) return;
		
		/* Look for inserted parameter checks. */
        List<SpecialTypeCheck> specialTypeChecks = SpecialTypeVisitor.getSpecialTypeChecks(condition);
        
        for(SpecialTypeCheck specialTypeCheck : specialTypeChecks) {
        	if(sourceLE.parameters.contains(specialTypeCheck.identifier)) {
        		
        		FunctionNode function = (FunctionNode) scope.scope;
        		
                /* Register an alert. */
                String signature = AnalysisUtilities.getFunctionSignature(function);
                this.callbackErrorChecks.add(new CallbackErrorCheck(function.getName(), signature, specialTypeCheck.identifier, specialTypeCheck.specialType));
        		
        	}
        }
		
	}

	@Override
	public void transfer(CFGNode node, CallbackErrorLatticeElement sourceLE,
			Scope scope) {
		/* Nothing to do. */
	}

	@Override
	public CallbackErrorLatticeElement copy(CallbackErrorLatticeElement le) {
		return le.copy();
	}

	/**
	 * Stores a parameter that was unchanged and had a special type check
	 * inserted.
	 */
	public class CallbackErrorCheck {
		public String functionName;
		public String functionSignature;
		public String identifier;
		public SpecialType type;
		
		public CallbackErrorCheck(String functionName, String functionSignature, String identifier, SpecialType type) {
			this.functionName = functionName;
			this.functionSignature = functionSignature;
			this.identifier = identifier;
			this.type = type;
		}
		
		@Override
		public boolean equals(Object o) {
			
			if(!(o instanceof CallbackErrorCheck)) return false;
			
			CallbackErrorCheck cec = (CallbackErrorCheck) o;
			
			if(this.functionName.equals(cec.functionName) && this.identifier.equals(cec.identifier)) return true;
			
			return false;
			
		}
	}

}
