package ca.ubc.ece.salt.sdjsb.ast;

import org.mozilla.javascript.ast.AstRoot;

/**
 * Performs a pre-processing operation on the AST. This could be used in a
 * number of places:
 * 	- Before running GumTree (e.g., process conditional expressions).
 * 	- After running Gum Tree (e.g., fix incorrect classifiers).
 * 
 * @author qhanam
 */
public interface PreProcessor {
	
	void process(AstRoot root);

}
