package ca.ubc.ece.salt.sdjsb.analysis.specialtype;

import java.util.List;
import java.util.Map;

import ca.ubc.ece.salt.sdjsb.analysis.classify.ClassifierDataSet;
import ca.ubc.ece.salt.sdjsb.analysis.meta.MetaAnalysis;
import ca.ubc.ece.salt.sdjsb.analysis.specialtype.SpecialTypeFlowAnalysis.SpecialTypeCheckResult;
import ca.ubc.ece.salt.sdjsb.batch.AnalysisMetaInformation;
import ca.ubc.ece.salt.sdjsb.classify.alert.ClassifierAlert;
import ca.ubc.ece.salt.sdjsb.classify.alert.IncorrectConditionAlert;
import ca.ubc.ece.salt.sdjsb.classify.alert.SpecialTypeAlert;
import ca.ubc.ece.salt.sdjsb.classify.alert.SpecialTypeAlert.SpecialType;

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
						if(isStronger(antiResult.specialType, result.specialType) ||
								isWeaker(antiResult.specialType, result.specialType)) {

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

	/**
	 * @param source The special type checked in the source function.
	 * @param destination The special type checked in the destination function.
	 * @return True if the type check has been strengthened (i.e., falsey to value or value to type).
	 */
	private static boolean isStronger(SpecialType source, SpecialType destination) {

		switch(source) {
		case FALSEY:
			if(destination != SpecialType.FALSEY) return true;
		case NO_VALUE:
			if(destination == SpecialType.UNDEFINED
				|| destination == SpecialType.NULL) return true;
		case EMPTY:
			if(destination == SpecialType.BLANK
				|| destination == SpecialType.ZERO
				|| destination == SpecialType.EMPTY_ARRAY) return true;
		default:
			return false;
		}

	}

	/**
	 * @param source The special type checked in the source function.
	 * @param destination The special type checked in the destination function.
	 * @return True if the type check has been weakend (i.e., type to value or value to falsey).
	 */
	private static boolean isWeaker(SpecialType source, SpecialType destination) {

		switch(destination) {
		case FALSEY:
			if(source != SpecialType.FALSEY) return true;
		case NO_VALUE:
			if(source == SpecialType.UNDEFINED
				|| source == SpecialType.NULL) return true;
		case EMPTY:
			if(source == SpecialType.BLANK
				|| source == SpecialType.ZERO
				|| source == SpecialType.EMPTY_ARRAY) return true;
		default:
			return false;
		}

	}

}
