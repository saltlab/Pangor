package ca.ubc.ece.salt.pangor.analysis.specialtype;

import java.util.List;
import java.util.Map;

import ca.ubc.ece.salt.pangor.analysis.classify.ClassifierDataSet;
import ca.ubc.ece.salt.pangor.analysis.meta.MetaAnalysis;
import ca.ubc.ece.salt.pangor.analysis.specialtype.SpecialTypeFlowAnalysis.SpecialTypeCheckResult;
import ca.ubc.ece.salt.pangor.batch.AnalysisMetaInformation;
import ca.ubc.ece.salt.pangor.classify.alert.ClassifierAlert;
import ca.ubc.ece.salt.pangor.classify.alert.IncorrectConditionAlert;
import ca.ubc.ece.salt.pangor.classify.alert.SpecialTypeAlert;

/**
 * Classifies repairs that fix a TypeException by adding a type check before
 * the value is dereferenced.
 */
public class SpecialTypeAnalysis extends MetaAnalysis<ClassifierAlert, ClassifierDataSet, SpecialTypeFlowAnalysis, SpecialTypeFlowAnalysis> {

	public SpecialTypeAnalysis(ClassifierDataSet dataSet, AnalysisMetaInformation ami) {
		super(dataSet, ami, new SpecialTypeFlowAnalysis(dataSet, ami), new SpecialTypeFlowAnalysis(dataSet, ami));
	}

	@Override
	protected void synthesizeAlerts() throws Exception {

		/* Anti-patterns. */
		Map<String, List<SpecialTypeCheckResult>> antiPatterns = this.srcAnalysis.getSpecialTypeCheckResults();

		/* Possible repair that adds callback error handling. */
		Map<String, List<SpecialTypeCheckResult>> repairs = this.dstAnalysis.getSpecialTypeCheckResults();

		/* Iterate through the identifiers. */
		results: for(List<SpecialTypeCheckResult> results : repairs.values()) {

			/* Iterate through the special types. */
			for(SpecialTypeCheckResult result : results) {

				List<SpecialTypeCheckResult> antiResults = null;
				if(antiPatterns.containsKey(result.identifier)) {
					antiResults = antiPatterns.get(result.identifier);
				}

				/* If the same pattern was deleted, ignore the result. */
				if(antiResults != null && antiResults.contains(result)) {
					continue;
				}

				/* Check if the type check condition was made stronger or weaker. */
				if(antiResults != null) {
					for(SpecialTypeCheckResult antiResult : antiResults) {
						if(SpecialTypeAnalysisUtilities.isStronger(antiResult.specialType, result.specialType) ||
								SpecialTypeAnalysisUtilities.isWeaker(antiResult.specialType, result.specialType)) {

							/* Register an alert. */
							this.registerAlert(new IncorrectConditionAlert(this.ami,
									"[TODO: function name]", "IC", result.identifier,
									antiResult.specialType, result.specialType));

							continue results;

						}
					}
				}

				/* Must be a new condition. */
				this.registerAlert(new SpecialTypeAlert(this.ami, "[TODO: function name]", "STH", result.identifier, result.specialType));

			}

		}

	}

}
