package ca.ubc.ece.salt.sdjsb.analysis.meta;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mozilla.javascript.ast.AstRoot;

import ca.ubc.ece.salt.sdjsb.alert.Alert;
import ca.ubc.ece.salt.sdjsb.analysis.Analysis;
import ca.ubc.ece.salt.sdjsb.cfg.CFG;

/**
 * Uses the results of the source and destination analysis to generate alerts.
 * @author qhanam
 *
 */
public abstract class MetaAnalysis<S extends Analysis, D extends Analysis> implements Analysis {
	
	Set<Alert> alerts;
	
	protected S srcAnalysis;
	protected D dstAnalysis;
	
	public MetaAnalysis(S srcAnalysis, D dstAnalysis) {
		
		this.alerts = new HashSet<Alert>();

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
		
		/* The alerts are the same as the flow analysis. */
		this.alerts = this.dstAnalysis.getAlerts();
		
	}


	@Override
	public Set<Alert> getAlerts() {
		return this.alerts;
	}
	
	/**
	 * Create the set of alerts from the alerts produced by the source and
	 * destination analyses.
	 * @throws Exception 
	 */
	protected abstract void synthesizeAlerts() throws Exception;
	
	/**
	 * Registers an alert to be reported to the user.
	 * @param alert
	 */
	protected void registerAlert(Alert alert) {
		this.alerts.add(alert);
	}

}
