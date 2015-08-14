package ca.ubc.ece.salt.sdjsb.analysis.ast;

import java.util.Set;

import ca.ubc.ece.salt.sdjsb.analysis.callbackerror.CallbackErrorCheck;
import ca.ubc.ece.salt.sdjsb.analysis.classify.ClassifierDataSet;
import ca.ubc.ece.salt.sdjsb.analysis.meta.MetaAnalysis;
import ca.ubc.ece.salt.sdjsb.batch.AnalysisMetaInformation;
import ca.ubc.ece.salt.sdjsb.classify.alert.CallbackErrorAlert;
import ca.ubc.ece.salt.sdjsb.classify.alert.ClassifierAlert;

public class CBEMetaAnalysis extends MetaAnalysis<ClassifierAlert, ClassifierDataSet, CBESourceScopeAnalysis, CBEDestinationScopeAnalysis> {

	public CBEMetaAnalysis(ClassifierDataSet dataSet, AnalysisMetaInformation ami) {
		super(dataSet, ami, new CBESourceScopeAnalysis(dataSet, ami), new CBEDestinationScopeAnalysis(dataSet, ami));
	}

	@Override
	protected void synthesizeAlerts() throws Exception {

		/* Anti-patterns. */
		Set<CallbackErrorCheck> antiPatterns = this.srcAnalysis.getCallbackErrorChecks();

		/* Possible repair that adds callback error handling. */
		Set<CallbackErrorCheck> repairs = this.dstAnalysis.getCallbackErrorChecks();

		for(CallbackErrorCheck repair : repairs) {

			if(!antiPatterns.contains(repair)) {

				/* Register an alert. */
				this.registerAlert(new CallbackErrorAlert(this.ami, "[TODO: function name]", "AST_CB", repair.functionName, repair.functionSignature, repair.identifier));

			}

		}

	}

}
