package ca.ubc.ece.salt.pangor.analysis.promises;

import ca.ubc.ece.salt.pangor.analysis.classify.ClassifierDataSet;
import ca.ubc.ece.salt.pangor.analysis.meta.MetaAnalysis;
import ca.ubc.ece.salt.pangor.batch.AnalysisMetaInformation;
import ca.ubc.ece.salt.pangor.classify.alert.ClassifierAlert;

public class PromisesAnalysis extends MetaAnalysis<ClassifierAlert, ClassifierDataSet, PromisesSourceAnalysis, PromisesDestinationAnalysis> {

	public PromisesAnalysis(ClassifierDataSet dataSet,
			AnalysisMetaInformation ami, PromisesSourceAnalysis srcAnalysis,
			PromisesDestinationAnalysis dstAnalysis) {
		super(dataSet, ami, srcAnalysis, dstAnalysis);
	}

	@Override
	protected void synthesizeAlerts() throws Exception {
		// TODO Auto-generated method stub

	}

}
