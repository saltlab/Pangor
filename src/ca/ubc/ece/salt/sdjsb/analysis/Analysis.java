package ca.ubc.ece.salt.sdjsb.analysis;

import java.util.List;
import java.util.Set;

import org.mozilla.javascript.ast.AstRoot;

import ca.ubc.ece.salt.sdjsb.alert.Alert;
import ca.ubc.ece.salt.sdjsb.cfg.CFG;

public interface Analysis {
	
	/**
	 * Perform a single-file analysis.
	 * @param root The script.
	 * @param cfgs The list of CFGs in the script (one for each function plus
	 * 			   one for the script).
	 */
	void analyze(AstRoot root, List<CFG> cfgs) throws Exception;
	
	/**
	 * Perform a source/destination analysis.
	 * @param srcRoot The source script.
	 * @param srcCFGs The list of source CFGs in the script.
	 * @param dstRoot The destination script.
	 * @param dstCFGs The list of destination CFGs in the script.
	 */
	void analyze(AstRoot srcRoot, List<CFG> srcCFGs, AstRoot dstRoot, List<CFG> dstCFGs) throws Exception;
	
	/**
	 * @return a list of the alerts from the analysis.
	 */
	Set<Alert> getAlerts();

}