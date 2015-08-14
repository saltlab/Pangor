package ca.ubc.ece.salt.sdjsb.batch;

import org.mozilla.javascript.EvaluatorException;

import ca.ubc.ece.salt.sdjsb.ControlFlowDifferencing;

/**
 * Runs an analysis on a file within a batch analysis. The batch analysis
 * engine extracts bug fixing commits from multiple projects, multiple versions
 * and multiple files. It then passes the source code contents (from source and
 * destination files) as well as the meta info (project, version and file info)
 * to be differenced and analyzed by this analysis engine.
 */
public abstract class AnalysisRunner {

	/**
	 * Performs AST-differencing and launches the analysis of the buggy/repaired
	 * source code file pair.
	 *
	 * @param ami The meta info for the analysis (i.e., project id, file paths,
	 * 			  commit IDs, etc.)
	 */
	public void analyzeFile(AnalysisMetaInformation ami) throws Exception {

        /* Control flow difference the files. */
        ControlFlowDifferencing cfd = null;
        try {
            cfd = new ControlFlowDifferencing(new String[] {"", ""}, ami.buggyCode, ami.repairedCode);
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
