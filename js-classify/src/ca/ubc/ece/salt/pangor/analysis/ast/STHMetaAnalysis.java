package ca.ubc.ece.salt.pangor.analysis.ast;

import java.util.Set;

import ca.ubc.ece.salt.pangor.analysis.MetaAnalysis;
import ca.ubc.ece.salt.pangor.analysis.ast.STHScopeAnalysis.SpecialTypeCheckResult;
import ca.ubc.ece.salt.pangor.batch.AnalysisMetaInformation;
import ca.ubc.ece.salt.pangor.classify.ClassifierDataSet;
import ca.ubc.ece.salt.pangor.classify.alert.ClassifierAlert;
import ca.ubc.ece.salt.pangor.classify.alert.SpecialTypeAlert;

public class STHMetaAnalysis extends MetaAnalysis<ClassifierAlert, ClassifierDataSet, STHScopeAnalysis, STHScopeAnalysis> {

	public STHMetaAnalysis(ClassifierDataSet dataSet, AnalysisMetaInformation ami) {
		super(dataSet, ami, new STHScopeAnalysis(dataSet, ami), new STHScopeAnalysis(dataSet, ami));
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
				this.registerAlert(new SpecialTypeAlert(this.ami, "[TODO: function name]", "AST_STH", repair.identifier, repair.specialType));

			}

		}

	}

}
