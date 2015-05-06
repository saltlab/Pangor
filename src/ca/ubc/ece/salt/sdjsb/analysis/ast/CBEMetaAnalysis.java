package ca.ubc.ece.salt.sdjsb.analysis.ast;

import java.util.Set;

import ca.ubc.ece.salt.sdjsb.alert.CallbackErrorAlert;
import ca.ubc.ece.salt.sdjsb.analysis.callbackerror.CallbackErrorCheck;
import ca.ubc.ece.salt.sdjsb.analysis.meta.MetaAnalysis;

public class CBEMetaAnalysis extends MetaAnalysis<CBESourceScopeAnalysis, CBEDestinationScopeAnalysis> {

	public CBEMetaAnalysis() {
		super(new CBESourceScopeAnalysis(), new CBEDestinationScopeAnalysis());
	}

	@Override
	protected void synthesizeAlerts() {

		/* Anti-patterns. */
		Set<CallbackErrorCheck> antiPatterns = this.srcAnalysis.getCallbackErrorChecks();
		
		/* Possible repair that adds callback error handling. */
		Set<CallbackErrorCheck> repairs = this.dstAnalysis.getCallbackErrorChecks();
		
		for(CallbackErrorCheck repair : repairs) {
			
			if(!antiPatterns.contains(repair)) {
				
				/* Register an alert. */
				this.registerAlert(new CallbackErrorAlert("AST_CB", repair.functionName, repair.functionSignature, repair.identifier));
				
			}
			
		}
		
	}

}
