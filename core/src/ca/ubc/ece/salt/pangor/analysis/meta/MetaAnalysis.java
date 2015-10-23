package ca.ubc.ece.salt.pangor.analysis.meta;

import java.util.List;

import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode;
import ca.ubc.ece.salt.pangor.analysis.Alert;
import ca.ubc.ece.salt.pangor.analysis.Analysis;
import ca.ubc.ece.salt.pangor.analysis.DataSet;
import ca.ubc.ece.salt.pangor.batch.AnalysisMetaInformation;
import ca.ubc.ece.salt.pangor.cfg.CFG;

/**
 * Uses the results of the source and destination analysis to generate alerts.
 *
 * @param <U> The type of alert the data set stores.
 * @param <T> The type of data set that stores the analysis results.
 * @param <S> The type of the source file analysis.
 * @param <D> Type type of the destination file analysis.
 */
public abstract class MetaAnalysis<U extends Alert, T extends DataSet<U>,
	S extends Analysis<U, T>, D extends Analysis<U, T>> extends Analysis<U, T> {

	protected S srcAnalysis;
	protected D dstAnalysis;

	/**
	 * @param srcAnalysis The analysis to run on the source (or buggy) file.
	 * @param dstAnalysis The analysis to run on the destination (or repaired) file.
	 */
	public MetaAnalysis(T dataSet, AnalysisMetaInformation ami, S srcAnalysis, D dstAnalysis) {
		super(dataSet, ami);
		this.srcAnalysis = srcAnalysis;
		this.dstAnalysis = dstAnalysis;
	}

	@Override
	public void analyze(ClassifiedASTNode srcRoot, List<CFG> srcCFGs, ClassifiedASTNode dstRoot, List<CFG> dstCFGs) throws Exception {

		/* Analyze the two files. */
		this.srcAnalysis.analyze(srcRoot, srcCFGs);
		this.dstAnalysis.analyze(dstRoot, dstCFGs);

		/* Synthesize the alerts. */
		this.synthesizeAlerts();

	}

	@Override
	public void analyze(ClassifiedASTNode root, List<CFG> cfgs) throws Exception {

		/* Analyze the destination file only. */
		this.dstAnalysis.analyze(root, cfgs);

	}

	/**
	 * Create the set of alerts from the alerts produced by the source and
	 * destination analyses.
	 * @throws Exception
	 */
	protected abstract void synthesizeAlerts() throws Exception;

}
