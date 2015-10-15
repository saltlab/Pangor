package ca.ubc.ece.salt.pangor.analysis2;

import java.util.List;

import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode;
import ca.ubc.ece.salt.pangor.batch.AnalysisMetaInformation;
import ca.ubc.ece.salt.pangor.cfg.CFG;

/**
 * Provides the information for one modified file in a commit.
 */
public class FileAnalysisInformation {

	/** Meta information about the file we are analyzing. **/
	public AnalysisMetaInformation ami;

	/** The root of the source AST for this file. **/
	public ClassifiedASTNode srcRoot;

	/** The root of the destination AST for this file. **/
	public ClassifiedASTNode dstRoot;

	/** The CFGs for the functions in this file. **/
	public List<CFG> srcCFGs;

	/** The CFGs for the destination functions in this file. **/
	public List<CFG> dstCFGs;

}
