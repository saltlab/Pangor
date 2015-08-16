package ca.ubc.ece.salt.sdjsb.analysis.errorhandling;

import java.util.List;

import ca.ubc.ece.salt.sdjsb.analysis.classify.ClassifierDataSet;
import ca.ubc.ece.salt.sdjsb.analysis.meta.MetaAnalysis;
import ca.ubc.ece.salt.sdjsb.batch.AnalysisMetaInformation;
import ca.ubc.ece.salt.sdjsb.classify.alert.ClassifierAlert;
import ca.ubc.ece.salt.sdjsb.classify.alert.ErrorHandlingAlert;

/**
 * Classifies repairs that fix an uncaught error by surrounding the
 * statement which throws the error with a try statement.
 */
public class ErrorHandlingAnalysis extends MetaAnalysis<ClassifierAlert, ClassifierDataSet,
														ErrorHandlingSourceScopeAnalysis,
														ErrorHandlingDestinationScopeAnalysis> {

	public ErrorHandlingAnalysis(ClassifierDataSet dataSet, AnalysisMetaInformation ami) {
		super(dataSet, ami,
				new ErrorHandlingSourceScopeAnalysis(dataSet, ami),
				new ErrorHandlingDestinationScopeAnalysis(dataSet, ami));
	}

	@Override
	protected void synthesizeAlerts() throws Exception {

		/* Anti-patterns. */
		List<ErrorHandlingCheck> antiPatterns = this.srcAnalysis.getCallbackErrorChecks();

		/* Possible repair patterns. */
		List<ErrorHandlingCheck> repairPatterns = this.dstAnalysis.getCallbackErrorChecks();

		/* Register the alert. */
		for(ErrorHandlingCheck repairPattern : repairPatterns) {
			if(!antiPatterns.contains(repairPattern)) this.registerAlert(new ErrorHandlingAlert(this.ami, repairPattern.functionName, "EH"));
		}

	}

}
