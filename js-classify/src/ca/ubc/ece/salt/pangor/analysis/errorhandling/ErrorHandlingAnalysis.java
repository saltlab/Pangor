package ca.ubc.ece.salt.pangor.analysis.errorhandling;

import java.util.List;
import java.util.Map;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.ScriptNode;

import ca.ubc.ece.salt.pangor.analysis.MetaAnalysis;
import ca.ubc.ece.salt.pangor.batch.AnalysisMetaInformation;
import ca.ubc.ece.salt.pangor.classify.ClassifierDataSet;
import ca.ubc.ece.salt.pangor.classify.alert.ClassifierAlert;
import ca.ubc.ece.salt.pangor.classify.alert.ErrorHandlingAlert;
import ca.ubc.ece.salt.pangor.js.analysis.AnalysisUtilities;

/**
 * Classifies repairs that fix an uncaught error by surrounding the
 * call which throws the error with a try statement.
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
		Map<AstNode, List<String>> antiPatterns = this.srcAnalysis.getUnprotectedCalls();

		/* Possible repair patterns. */
		List<ErrorHandlingCheck> repairPatterns = this.dstAnalysis.getProtectedCalls();

		repairs: for(ErrorHandlingCheck repairPattern : repairPatterns) {

			if(antiPatterns.containsKey(repairPattern.scope.getScope().getMapping())) {

				List<String> unprotectedMethodCalls = antiPatterns.get(repairPattern.scope.getScope().getMapping());

				/* To be a repair, all the function calls need to be unprotected
				 * in the source file. */
				for(String protectedMethodCall : repairPattern.callTargetIdentifiers) {
					if(!unprotectedMethodCalls.contains(protectedMethodCall)) continue repairs;
				}

				/* Register an alert. */
				this.registerAlert(new ErrorHandlingAlert(this.ami,
						AnalysisUtilities.getFunctionName((ScriptNode)repairPattern.scope.getScope()), "EH"));

			}

		}

	}

}
