package ca.ubc.ece.salt.sdjsb.classify;

import ca.ubc.ece.salt.sdjsb.ControlFlowDifferencing;
import ca.ubc.ece.salt.sdjsb.analysis.classify.ClassifierDataSet;
import ca.ubc.ece.salt.sdjsb.analysis.errorhandling.ErrorHandlingAnalysis;
import ca.ubc.ece.salt.sdjsb.batch.AnalysisMetaInformation;
import ca.ubc.ece.salt.sdjsb.batch.AnalysisRunner;

public class ClassifyAnalysisRunner extends AnalysisRunner {

	/** Stores the alerts that make up the data set. **/
	private ClassifierDataSet dataset;

	/**
	 * @param dataSetPath The file path to store the data set.
	 * @param supplementaryPath The directory path to store the supplementary
	 * 		  files.
	 */
	public ClassifyAnalysisRunner(String dataSetPath, String supplementaryPath) {
		this.dataset = new ClassifierDataSet(dataSetPath, supplementaryPath);
	}

	@Override
	protected void analyze(ControlFlowDifferencing cfd,
			AnalysisMetaInformation ami) throws Exception {

//		SpecialTypeAnalysis sth_analysis = new SpecialTypeAnalysis(this.dataset, ami);
//		cfd.analyze(sth_analysis);

		ErrorHandlingAnalysis eh_analysis = new ErrorHandlingAnalysis(this.dataset, ami);
		cfd.analyze(eh_analysis);

        	/* These analyses are full analyses. */
//        	tasks.add(new CFDTask(cfd, new SpecialTypeAnalysis()));
//        	tasks.add(new CFDTask(cfd, new GlobalToLocalAnalysis()));
//        	tasks.add(new CFDTask(cfd, new CallbackParamAnalysis()));
//        	tasks.add(new CFDTask(cfd, new CallbackErrorAnalysis()));

        	/* These analyses are AST level only. */
//        	tasks.add(new CFDTask(cfd, new STHMetaAnalysis()));
//        	tasks.add(new CFDTask(cfd, new STHScopeAnalysis()));
//        	tasks.add(new CFDTask(cfd, new CBEMetaAnalysis()));
//        	tasks.add(new CFDTask(cfd, new CBEDestinationScopeAnalysis()));
//        	tasks.add(new CFDTask(cfd, new GTLScopeAnalysis()));

	}

}
