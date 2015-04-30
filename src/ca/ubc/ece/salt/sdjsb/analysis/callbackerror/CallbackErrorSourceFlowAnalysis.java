package ca.ubc.ece.salt.sdjsb.analysis.callbackerror;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.ScriptNode;

import ca.ubc.ece.salt.sdjsb.analysis.AnalysisUtilities;
import ca.ubc.ece.salt.sdjsb.analysis.flow.PathInsensitiveFlowAnalysis;
import ca.ubc.ece.salt.sdjsb.analysis.scope.Scope;
import ca.ubc.ece.salt.sdjsb.analysis.specialtype.SpecialTypeCheck;
import ca.ubc.ece.salt.sdjsb.analysis.specialtype.SpecialTypeVisitor;
import ca.ubc.ece.salt.sdjsb.cfg.CFGEdge;
import ca.ubc.ece.salt.sdjsb.cfg.CFGNode;

public class CallbackErrorSourceFlowAnalysis extends PathInsensitiveFlowAnalysis<CallbackErrorLatticeElement> {
	
	/** Stores the possible callback error check repairs. */
	private Set<CallbackErrorCheck> callbackErrorChecks;
	
	public CallbackErrorSourceFlowAnalysis() {
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
		
		/* If this is the script (no parameters), there is nothing to do. The
		 * function must also have a mapping to the source file (the function
		 * should not be inserted) */
		if(!(node instanceof FunctionNode) || node.getMapping() == null) return cele;

		/* Look through the parameters to see if there is an error parameter. */
        FunctionNode function = (FunctionNode) node;
        for(AstNode parameter : function.getParams()) {
            if(parameter instanceof Name) {
                
                /* Match only error parameters for now. */
                Name name = (Name) parameter;
                if(name.getIdentifier().matches("(?i)e(rr(or)?)?")) {
                    
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
        List<SpecialTypeCheck> specialTypeChecks = SpecialTypeVisitor.getSpecialTypeChecks(condition, false);
        
        for(SpecialTypeCheck specialTypeCheck : specialTypeChecks) {
        	if(sourceLE.parameters.contains(specialTypeCheck.identifier)) {
        		
        		FunctionNode function = (FunctionNode) scope.scope;
        		
                /* Register an alert. */
                String signature = AnalysisUtilities.getFunctionSignature(function);
                this.callbackErrorChecks.add(new CallbackErrorCheck(scope, function.getName(), signature, specialTypeCheck.identifier, specialTypeCheck.specialType));
        		
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

}
