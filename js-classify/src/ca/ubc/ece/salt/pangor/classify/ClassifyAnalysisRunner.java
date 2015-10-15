package ca.ubc.ece.salt.pangor.classify;

import ca.ubc.ece.salt.pangor.analysis.promises.PromisesAnalysis;
import ca.ubc.ece.salt.pangor.batch.AnalysisMetaInformation;
import ca.ubc.ece.salt.pangor.batch.AnalysisRunner;
import ca.ubc.ece.salt.pangor.cfd.ControlFlowDifferencing;
import ca.ubc.ece.salt.pangor.classify.ClassifierDataSet;
import ca.ubc.ece.salt.pangor.js.cfg.JavaScriptCFGFactory;

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
		super(new JavaScriptCFGFactory(), preProcess);
		this.dataset = new ClassifierDataSet(dataSetPath, supplementaryPath);
	}

	@Override
	protected void analyze(ControlFlowDifferencing cfd,
			AnalysisMetaInformation ami) throws Exception {

//		SpecialTypeAnalysis sth_analysis = new SpecialTypeAnalysis(this.dataset, ami);
//		cfd.analyze(sth_analysis);

//		ErrorHandlingAnalysis eh_analysis = new ErrorHandlingAnalysis(this.dataset, ami);
//		cfd.analyze(eh_analysis);

//		BoundedContextAnalysis bc_analysis = new BoundedContextAnalysis(this.dataset, ami);
//		cfd.analyze(bc_analysis);

//		ArgumentAnalysis a_analysis = new ArgumentAnalysis(this.dataset, ami);
//		cfd.analyze(a_analysis);

//		ArgumentOrderAnalysis ao_analysis = new ArgumentOrderAnalysis(this.dataset, ami);
//		cfd.analyze(ao_analysis);

//		CallbackErrorAnalysis cbe_analysis = new CallbackErrorAnalysis(this.dataset, ami);
//		cfd.analyze(cbe_analysis);

//		CallbackErrorHandlingAnalysis cbeh_analysis = new CallbackErrorHandlingAnalysis(this.dataset, ami);
//		cfd.analyze(cbeh_analysis);

//		GlobalToLocalAnalysis gtl_analysis = new GlobalToLocalAnalysis(this.dataset, ami);
//		cfd.analyze(gtl_analysis);

//		CallbackParamAnalysis cbp_analysis = new CallbackParamAnalysis(this.dataset, ami);
//		cfd.analyze(cbp_analysis);

//		ThisToThatAnalysis tth_analysis = new ThisToThatAnalysis(this.dataset, ami);
//		cfd.analyze(tth_analysis);

		PromisesAnalysis ctp_analysis = new PromisesAnalysis(this.dataset, ami);
		cfd.analyze(ctp_analysis);

	}

}
