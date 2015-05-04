package ca.ubc.ece.salt.sdjsb.analysis.callbackerror;

import java.util.Set;

import org.mozilla.javascript.ast.FunctionNode;

import ca.ubc.ece.salt.sdjsb.alert.CallbackErrorAlert;
import ca.ubc.ece.salt.sdjsb.analysis.AnalysisUtilities;
import ca.ubc.ece.salt.sdjsb.analysis.meta.MetaAnalysis;

public class CallbackErrorAnalysis extends MetaAnalysis<CallbackErrorSourceFlowAnalysis, CallbackErrorDestinationFlowAnalysis> {

	public CallbackErrorAnalysis() {
		super(new CallbackErrorSourceFlowAnalysis(), new CallbackErrorDestinationFlowAnalysis());
	}

	@Override
	protected void synthesizeAlerts() {
		
		/* Anti-patterns. */
		Set<CallbackErrorCheck> antiPatterns = this.srcAnalysis.getCallbackErrorChecks();
		
		/* Possible repair that adds callback error handling. */
		Set<CallbackErrorCheck> repairs = this.dstAnalysis.getCallbackErrorChecks();
		
		for(CallbackErrorCheck repair : repairs) {
			
			/* Get the (supposedly) buggy function. */
			FunctionNode buggyFunction = (FunctionNode)repair.scope.scope.getMapping();
			
			/* Check to see if the buggy function is in our list of anti-patterns. */
			boolean hasAntiPattern = false;
			for(CallbackErrorCheck antiPattern : antiPatterns) {
				if(antiPattern.scope.scope == buggyFunction) hasAntiPattern = true;
			}
			
			/* If there is no anti-pattern and the function signatures match, register an alert. */
			String buggyFunctionSignature = AnalysisUtilities.getFunctionSignature(buggyFunction);
			String repairedFunctionSignature = AnalysisUtilities.getFunctionSignature((FunctionNode)repair.scope.scope);

			if(!hasAntiPattern && buggyFunctionSignature.equals(repairedFunctionSignature)) {
				/* Register an alert. */
				this.registerAlert(new CallbackErrorAlert("CB", repair.functionName, repair.functionSignature, repair.identifier));
			}
			
		}

		
	}

}
