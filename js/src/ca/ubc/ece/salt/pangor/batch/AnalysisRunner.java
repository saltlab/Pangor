package ca.ubc.ece.salt.pangor.batch;

import org.mozilla.javascript.EvaluatorException;

import ca.ubc.ece.salt.pangor.cfd.ControlFlowDifferencing;
import ca.ubc.ece.salt.pangor.cfg.CFGFactory;

/**
 * Runs an analysis on a file within a batch analysis. The batch analysis
 * engine extracts bug fixing commits from multiple projects, multiple versions
 * and multiple files. It then passes the source code contents (from source and
 * destination files) as well as the meta info (project, version and file info)
 * to be differenced and analyzed by this analysis engine.
 */
public abstract class AnalysisRunner {

	/** Set to true to enable AST pre-processing. **/
	private boolean preProcess;

	/** Specifies the CFG factory to use. **/
	private CFGFactory cfgFactory;

	public AnalysisRunner(CFGFactory cfgFactory) {
		this.preProcess = false;
		this.cfgFactory = cfgFactory;
	}

	/**
	 * @param preProcess Set to true to enable AST pre-processing.
	 */
	public AnalysisRunner(CFGFactory cfgFactory, boolean preProcess) {
		this.preProcess = preProcess;
		this.cfgFactory = cfgFactory;
	}

	/**
	 * Performs AST-differencing and launches the analysis of the buggy/repaired
	 * source code file pair.
	 *
	 * @param ami The meta info for the analysis (i.e., project id, file paths,
	 * 			  commit IDs, etc.)
	 */
	public void analyzeFile(AnalysisMetaInformation ami) throws Exception {
		this.analyzeFile(ami, false);
	}

	/**
	 * Performs AST-differencing and launches the analysis of the buggy/repaired
	 * source code file pair.
	 *
	 * @param ami The meta info for the analysis (i.e., project id, file paths,
	 * 			  commit IDs, etc.)
	 * @param preProcess Set to true to enable AST pre-processing.
	 */
	public void analyzeFile(AnalysisMetaInformation ami, boolean preProcess) throws Exception {

        /* Control flow difference the files. */
        ControlFlowDifferencing cfd = null;
        try {
			String[] args = this.preProcess ? new String[] {"", "", "-pp"} : new String[] {"", ""};
            cfd = new ControlFlowDifferencing(this.cfgFactory, args, ami.buggyCode, ami.repairedCode);
        }
        catch(ArrayIndexOutOfBoundsException e) {
        	System.err.println("ArrayIndexOutOfBoundsException: possibly caused by empty file.");
        	return;
        }
        catch(EvaluatorException e) {
        	System.err.println("Evaluator exception: " + e.getMessage());
        	return;
        }
        catch(Exception e) {
        	throw e;
        }

        /* Run the analysis. */
        this.analyze(cfd, ami);

	}

	/**
	 * Performs the file analysis.
	 *
	 * @param cfd The control flow differencing results.
	 * @param ami The meta info for the analysis (i.e., project id, file paths,
	 * 			  commit IDs, etc.)
	 */
	protected abstract void analyze(ControlFlowDifferencing cfd, AnalysisMetaInformation ami) throws Exception;


}
