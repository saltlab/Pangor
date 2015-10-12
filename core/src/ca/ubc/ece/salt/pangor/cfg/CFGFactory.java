package ca.ubc.ece.salt.pangor.cfg;

import java.util.List;

import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode;

/**
 * Builds a CFG given some AST.
 *
 * Classes that implement this interface will use one specific parser and
 * will therefore have an implementation for a specific AST.
 */
public interface CFGFactory {

	/**
	 * Builds intra-procedural control flow graphs for the given artifact.
	 * @param root The class or script to build CFGs for.
	 * @return One CFG for each function in the class or script.
	 */
	List<CFG> createCFGs(ClassifiedASTNode root);

}
