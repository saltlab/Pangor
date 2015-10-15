package ca.ubc.ece.salt.pangor.analysis2;

import ca.ubc.ece.salt.pangor.analysis.Alert;
import ca.ubc.ece.salt.pangor.analysis.DataSet;
import ca.ubc.ece.salt.pangor.batch.AnalysisMetaInformation;

/**
 * Gathers facts about one commit and synthesizes alerts based on those facts.
 *
 * @param <U> The type of alert the data set stores.
 * @param <T> The type of data set that stores the analysis results.
 * @param <S> The type of the source file analysis.
 * @param <D> Type type of the destination file analysis.
 *
 * TODO: Here is where we should create pre and post conditions. When both conditions are met, we register
 * some alert... which also needs to be defined by the subclass.
 */
public abstract class CommitAnalysis<U extends Alert, T extends DataSet<U>,
	S extends SourceCodeFileAnalysis, D extends SourceCodeFileAnalysis> {

	/**
	 * The data set manages the alerts by storing and loading alerts to and
	 * from the disk, performing pre-processing tasks and calculating metrics.
	 */
	private T dataSet;

	private S srcAnalysis;
	private D dstAnalysis;

	/**
	 * @param srcAnalysis The analysis to run on the source (or buggy) file.
	 * @param dstAnalysis The analysis to run on the destination (or repaired) file.
	 */
	public CommitAnalysis(T dataSet, AnalysisMetaInformation ami, S srcAnalysis, D dstAnalysis) {
		this.srcAnalysis = srcAnalysis;
		this.dstAnalysis = dstAnalysis;
	}

	/**
	 * Analyze the commit. Each file in the commit is analyzed separately, and
	 * facts are gathered from each analysis. Once all the files are analyzed,
	 * alerts are synthesized by checking that pre-conditions and post-conditions
	 * are all met.
	 * @param fai
	 * @throws Exception
	 */
	public void analyze(/* Commit?? */) throws Exception {

		/* TODO: Iterate through the files in the commit and call the SourceCodeFileAnalysis on them. */

		/* Synthesize the alerts. */
		this.synthesizeAlerts();

	}

	/**
	 * Registers alerts based on the patterns found by the analysis.
	 *
	 * For each pattern, checks that pre-conditions are met and that there
	 * are no anti-patterns.
	 *
	 * @throws Exception
	 */
	protected void synthesizeAlerts() throws Exception {

		/* TODO: Compute the set of (P - A) n C ... that is patters minus antipatterns intersecting preconditions. */

	}

}
