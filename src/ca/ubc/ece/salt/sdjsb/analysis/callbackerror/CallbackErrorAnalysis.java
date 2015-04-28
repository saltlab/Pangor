package ca.ubc.ece.salt.sdjsb.analysis.callbackerror;

import java.util.Set;

import ca.ubc.ece.salt.sdjsb.alert.CallbackErrorAlert;
import ca.ubc.ece.salt.sdjsb.analysis.callbackerror.CallbackErrorFlowAnalysis.CallbackErrorCheck;
import ca.ubc.ece.salt.sdjsb.analysis.meta.MetaAnalysis;

public class CallbackErrorAnalysis extends MetaAnalysis<CallbackErrorFlowAnalysis, CallbackErrorFlowAnalysis> {

	public CallbackErrorAnalysis() {
		super(new CallbackErrorFlowAnalysis(), new CallbackErrorFlowAnalysis());
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
				this.registerAlert(new CallbackErrorAlert("CB", repair.functionName, repair.functionSignature, repair.identifier));
				
			}
			
		}

		
	}

}
