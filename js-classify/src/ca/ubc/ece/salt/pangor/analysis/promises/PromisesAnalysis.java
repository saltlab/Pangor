package ca.ubc.ece.salt.pangor.analysis.promises;

import ca.ubc.ece.salt.pangor.analysis.MetaAnalysis;
import ca.ubc.ece.salt.pangor.batch.AnalysisMetaInformation;
import ca.ubc.ece.salt.pangor.classify.ClassifierDataSet;
import ca.ubc.ece.salt.pangor.classify.alert.ClassifierAlert;
import ca.ubc.ece.salt.pangor.classify.alert.PromisesAlert;

/**
 * Registers an alert if the function meets pre and post conditions for a
 * callback to promises refactoring.
 */
public class PromisesAnalysis extends MetaAnalysis<ClassifierAlert, ClassifierDataSet, PromisesSourceAnalysis, PromisesDestinationAnalysis> {

	public PromisesAnalysis(ClassifierDataSet dataSet,
			AnalysisMetaInformation ami) {
		super(dataSet, ami, new PromisesSourceAnalysis(dataSet, ami), new PromisesDestinationAnalysis(dataSet, ami));
	}

	@Override
	protected void synthesizeAlerts() throws Exception {

		if(this.srcAnalysis.meetsPreConditions() && this.dstAnalysis.meetsPostConditions()) {
			this.registerAlert(new PromisesAlert(ami, "UNKNOWN", "REF", "PROM"));
		}

	}

}
