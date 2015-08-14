package ca.ubc.ece.salt.sdjsb.analysis.meta;

import java.util.List;

import org.mozilla.javascript.ast.AstRoot;

import ca.ubc.ece.salt.sdjsb.analysis.Alert;
import ca.ubc.ece.salt.sdjsb.analysis.Analysis;
import ca.ubc.ece.salt.sdjsb.analysis.DataSet;
import ca.ubc.ece.salt.sdjsb.batch.AnalysisMetaInformation;
import ca.ubc.ece.salt.sdjsb.cfg.CFG;

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
	public void analyze(AstRoot srcRoot, List<CFG> srcCFGs, AstRoot dstRoot, List<CFG> dstCFGs) throws Exception {

		/* Analyze the two files. */
		this.srcAnalysis.analyze(srcRoot, srcCFGs);
		this.dstAnalysis.analyze(dstRoot, dstCFGs);

		/* Synthesize the alerts. */
		this.synthesizeAlerts();

	}

	@Override
	public void analyze(AstRoot root, List<CFG> cfgs) throws Exception {

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
