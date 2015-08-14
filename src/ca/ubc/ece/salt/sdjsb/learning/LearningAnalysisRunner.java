package ca.ubc.ece.salt.sdjsb.learning;

import ca.ubc.ece.salt.sdjsb.ControlFlowDifferencing;
import ca.ubc.ece.salt.sdjsb.analysis.learning.LearningAnalysis;
import ca.ubc.ece.salt.sdjsb.analysis.learning.LearningDataSet;
import ca.ubc.ece.salt.sdjsb.batch.AnalysisMetaInformation;
import ca.ubc.ece.salt.sdjsb.batch.AnalysisRunner;

public class LearningAnalysisRunner extends AnalysisRunner {

	/** Stores the feature vectors that make up the data set. **/
	private LearningDataSet dataset;

	/**
	 * @param dataSetPath The file path to store the data set.
	 * @param supplementaryPath The directory path to store the supplementary
	 * 		  files.
	 */
	public LearningAnalysisRunner(String dataSetPath, String supplementaryPath) {
		this.dataset = new LearningDataSet(dataSetPath, supplementaryPath);
	}

	@Override
	protected void analyze(ControlFlowDifferencing cfd, AnalysisMetaInformation ami) throws Exception {

		LearningAnalysis analysis = new LearningAnalysis(this.dataset, ami);
		cfd.analyze(analysis);

	}

}
