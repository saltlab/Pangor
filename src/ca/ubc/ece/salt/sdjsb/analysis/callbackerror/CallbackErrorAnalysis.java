package ca.ubc.ece.salt.sdjsb.analysis.callbackerror;

import java.util.Set;

import org.mozilla.javascript.ast.FunctionNode;

import ca.ubc.ece.salt.sdjsb.analysis.AnalysisUtilities;
import ca.ubc.ece.salt.sdjsb.analysis.classify.ClassifierDataSet;
import ca.ubc.ece.salt.sdjsb.analysis.meta.MetaAnalysis;
import ca.ubc.ece.salt.sdjsb.batch.AnalysisMetaInformation;
import ca.ubc.ece.salt.sdjsb.classify.alert.CallbackErrorAlert;
import ca.ubc.ece.salt.sdjsb.classify.alert.ClassifierAlert;

public class CallbackErrorAnalysis extends MetaAnalysis<ClassifierAlert, ClassifierDataSet, CallbackErrorSourceFlowAnalysis, CallbackErrorDestinationFlowAnalysis> {

	public CallbackErrorAnalysis(ClassifierDataSet dataSet, AnalysisMetaInformation ami) {
		super(dataSet, ami, new CallbackErrorSourceFlowAnalysis(dataSet, ami), new CallbackErrorDestinationFlowAnalysis(dataSet, ami));
	}

	@Override
	protected void synthesizeAlerts() throws Exception {

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
				this.registerAlert(new CallbackErrorAlert(this.ami, "[TODO: function name]", "CB", repair.functionName, repair.functionSignature, repair.identifier));
			}

		}


	}

}
