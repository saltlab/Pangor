package ca.ubc.ece.salt.sdjsb.classify;

import ca.ubc.ece.salt.sdjsb.ControlFlowDifferencing;
import ca.ubc.ece.salt.sdjsb.analysis.classify.ClassifierDataSet;
import ca.ubc.ece.salt.sdjsb.analysis.specialtype.SpecialTypeAnalysis;
import ca.ubc.ece.salt.sdjsb.batch.AnalysisMetaInformation;
import ca.ubc.ece.salt.sdjsb.batch.AnalysisRunner;

public class ClassifyAnalysisRunner extends AnalysisRunner {

	/** Stores the alerts that make up the data set. **/
	private ClassifierDataSet dataset;

	/**
	 * @param dataSetPath The file path to store the data set.
	 * @param supplementaryPath The directory path to store the supplementary
	 * 		  files.
	 * @param preProcess Set to true to enable AST pre-processing.
	 */
	public ClassifyAnalysisRunner(String dataSetPath, String supplementaryPath, boolean preProcess) {
		super(preProcess);
		this.dataset = new ClassifierDataSet(dataSetPath, supplementaryPath);
	}

	@Override
	protected void analyze(ControlFlowDifferencing cfd,
			AnalysisMetaInformation ami) throws Exception {

		SpecialTypeAnalysis sth_analysis = new SpecialTypeAnalysis(this.dataset, ami);
		cfd.analyze(sth_analysis);

//		ErrorHandlingAnalysis eh_analysis = new ErrorHandlingAnalysis(this.dataset, ami);
//		cfd.analyze(eh_analysis);
//
//		BoundedContextAnalysis bc_analysis = new BoundedContextAnalysis(this.dataset, ami);
//		cfd.analyze(bc_analysis);
//
//		ArgumentAnalysis a_analysis = new ArgumentAnalysis(this.dataset, ami);
//		cfd.analyze(a_analysis);
//
//		ArgumentOrderAnalysis ao_analysis = new ArgumentOrderAnalysis(this.dataset, ami);
//		cfd.analyze(ao_analysis);
//
//		CallbackErrorAnalysis cbe_analysis = new CallbackErrorAnalysis(this.dataset, ami);
//		cfd.analyze(cbe_analysis);

	}

}
