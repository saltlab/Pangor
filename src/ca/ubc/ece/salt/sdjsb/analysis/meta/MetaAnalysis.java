package ca.ubc.ece.salt.sdjsb.analysis.meta;

import java.util.List;
import java.util.Set;

import org.mozilla.javascript.ast.AstRoot;

import ca.ubc.ece.salt.sdjsb.alert.Alert;
import ca.ubc.ece.salt.sdjsb.analysis.Analysis;
import ca.ubc.ece.salt.sdjsb.analysis.flow.FlowAnalysis;
import ca.ubc.ece.salt.sdjsb.cfg.CFG;

/**
 * Uses the results of the source and destination analysis to generate alerts.
 * @author qhanam
 *
 */
public class MetaAnalysis implements Analysis {
	
	Set<Alert> alerts;
	
	FlowAnalysis<?> srcAnalysis;
	FlowAnalysis<?> dstAnalysis;
	
	public MetaAnalysis(FlowAnalysis<?> srcAnalysis, FlowAnalysis<?> dstAnalysis) {
		
		this.srcAnalysis = srcAnalysis;
		this.dstAnalysis = dstAnalysis;
		
	}
	
	@Override
	public void analyze(AstRoot srcRoot, List<CFG> srcCFGs, AstRoot dstRoot, List<CFG> dstCFGs) throws Exception {

		/* Analyze the two files. */
		this.srcAnalysis.analyze(srcRoot, srcCFGs);
		this.dstAnalysis.analyze(dstRoot, dstCFGs);
		
		/* Synthesize the alerts. */
		
	}

	@Override
	public void analyze(AstRoot root, List<CFG> cfgs) throws Exception {
		
		/* Analyze the destination file only. */
		this.dstAnalysis.analyze(root, cfgs);
		
	}


	@Override
	public Set<Alert> getAlerts() {
		return this.alerts;
	}
	
	/**
	 * Registers an alert to be reported to the user.
	 * @param alert
	 */
	protected void registerAlert(Alert alert) {
		this.alerts.add(alert);
	}

}
