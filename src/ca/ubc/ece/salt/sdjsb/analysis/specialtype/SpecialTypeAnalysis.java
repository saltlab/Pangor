package ca.ubc.ece.salt.sdjsb.analysis.specialtype;

import java.util.Set;

import ca.ubc.ece.salt.sdjsb.analysis.classify.ClassifierDataSet;
import ca.ubc.ece.salt.sdjsb.analysis.meta.MetaAnalysis;
import ca.ubc.ece.salt.sdjsb.analysis.specialtype.SpecialTypeFlowAnalysis.SpecialTypeCheckResult;
import ca.ubc.ece.salt.sdjsb.batch.AnalysisMetaInformation;
import ca.ubc.ece.salt.sdjsb.classify.alert.ClassifierAlert;
import ca.ubc.ece.salt.sdjsb.classify.alert.SpecialTypeAlert;

public class SpecialTypeAnalysis extends MetaAnalysis<ClassifierAlert, ClassifierDataSet, SpecialTypeFlowAnalysis, SpecialTypeFlowAnalysis> {

	public SpecialTypeAnalysis(ClassifierDataSet dataSet, AnalysisMetaInformation ami) {
		super(dataSet, ami, new SpecialTypeFlowAnalysis(dataSet, ami), new SpecialTypeFlowAnalysis(dataSet, ami));
	}

	@Override
	protected void synthesizeAlerts() throws Exception {

		/* Anti-patterns. */
		Set<SpecialTypeCheckResult> antiPatterns = this.srcAnalysis.getSpecialTypeCheckResults();

		/* Possible repair that adds callback error handling. */
		Set<SpecialTypeCheckResult> repairs = this.dstAnalysis.getSpecialTypeCheckResults();

		for(SpecialTypeCheckResult repair : repairs) {

			if(!antiPatterns.contains(repair)) {

				/* Register an alert. */
				this.registerAlert(new SpecialTypeAlert(this.ami, "[TODO: function name]", "STH", repair.identifier, repair.specialType));

			}

		}

	}

}
