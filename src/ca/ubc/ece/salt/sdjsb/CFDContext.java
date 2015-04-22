package ca.ubc.ece.salt.sdjsb;

import java.util.List;

import org.mozilla.javascript.ast.AstRoot;

import ca.ubc.ece.salt.sdjsb.cfg.CFG;

/**
 * Stores the context for a control flow differencing analysis.
 */
public class CFDContext {
	
	public AstRoot srcScript;
	public AstRoot dstScript;
	public List<CFG> srcCFGs;
	public List<CFG> dstCFGs;
	
	public CFDContext(AstRoot srcScript, AstRoot dstScript, List<CFG> srcCFGs, List<CFG> dstCFGs) {
		this.srcScript = srcScript;
		this.dstScript = dstScript;
		this.srcCFGs = srcCFGs;
		this.dstCFGs = dstCFGs;
	}

}
