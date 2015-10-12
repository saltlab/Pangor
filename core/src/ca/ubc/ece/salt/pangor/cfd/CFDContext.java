package ca.ubc.ece.salt.pangor.cfd;

import java.util.List;

import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode;
import ca.ubc.ece.salt.pangor.cfg.CFG;

/**
 * Stores the context for a control flow differencing analysis.
 */
public class CFDContext {

	public ClassifiedASTNode srcScript;
	public ClassifiedASTNode dstScript;
	public List<CFG> srcCFGs;
	public List<CFG> dstCFGs;

	/**
	 * @param srcScript The root node for the class or script.
	 * @param dstScript The root node for the class or script.
	 * @param srcCFGs The CFGs for each function in the source class or script.
	 * @param dstCFGs The CFGs for each function in the destination class or script.
	 */
	public CFDContext(ClassifiedASTNode srcScript, ClassifiedASTNode dstScript, List<CFG> srcCFGs, List<CFG> dstCFGs) {
		this.srcScript = srcScript;
		this.dstScript = dstScript;
		this.srcCFGs = srcCFGs;
		this.dstCFGs = dstCFGs;
	}

}
