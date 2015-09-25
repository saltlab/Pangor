package ca.ubc.ece.salt.pangor.analysis.promises;

import ca.ubc.ece.salt.pangor.analysis.classify.ClassifierDataSet;
import ca.ubc.ece.salt.pangor.analysis.scope.ScopeAnalysis;
import ca.ubc.ece.salt.pangor.batch.AnalysisMetaInformation;
import ca.ubc.ece.salt.pangor.classify.alert.ClassifierAlert;

public class PromisesDestinationAnalysis extends ScopeAnalysis<ClassifierAlert, ClassifierDataSet> {

	public PromisesDestinationAnalysis(ClassifierDataSet dataSet,
			AnalysisMetaInformation ami) {
		super(dataSet, ami);
	}

}
