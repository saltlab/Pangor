package ca.ubc.ece.salt.pangor.analysis.argument;

import java.util.HashSet;
import java.util.Set;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.NodeVisitor;

import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode.ChangeType;

/**
 * Visitor for storing function definition that had its parameter list changed
 * (arguments added, removed or updated). Inserted functions are not stored.
 */
public class ModifiedFunctionDefinitionVisitor implements NodeVisitor {
	public Set<FunctionNode> modifiedFunctionDefinitions = new HashSet<>();
	public Set<FunctionNode> allFunctionDefinitions = new HashSet<>();

	@Override
	public boolean visit(AstNode node) {
		// Not a function definition? Keep visiting nodes
		if (!(node instanceof FunctionNode))
			return true;

		// Is a function definition, but it was not there before? Keep visiting
		// nodes
		if (node.getChangeType() == ChangeType.INSERTED || node.getChangeType() == ChangeType.MOVED)
			return true;

		/*
		 * Get the definition
		 */
		FunctionNode definition = (FunctionNode) node;

		allFunctionDefinitions.add(definition);

		/*
		 * Look for changed arguments
		 */

		for (AstNode argument : definition.getParams()) {
			if (argument.getChangeType() != ChangeType.UNCHANGED)
				modifiedFunctionDefinitions.add(definition);
		}

		return true;
	}

}
