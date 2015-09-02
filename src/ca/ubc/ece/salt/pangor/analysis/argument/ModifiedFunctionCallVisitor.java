package ca.ubc.ece.salt.pangor.analysis.argument;

import java.util.ArrayList;
import java.util.List;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.NodeVisitor;

import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode.ChangeType;
import ca.ubc.ece.salt.pangor.analysis.AnalysisUtilities;

/**
 * Visitor for storing function calls that had its parameter list changed
 * (arguments added, removed or updated). Inserted functions are not stored.
 */
public class ModifiedFunctionCallVisitor implements NodeVisitor {
	public List<FunctionCall> modifiedFunctionCalls = new ArrayList<>();

	@Override
	public boolean visit(AstNode node) {
		// Not a function? Keep visiting nodes
		if (!(node instanceof FunctionCall))
			return true;

		// Is a function, but it was not there before? Keep visiting nodes
		if (node.getChangeType() == ChangeType.INSERTED || node.getChangeType() == ChangeType.MOVED)
			return true;

		/*
		 * Get the call
		 */
		FunctionCall call = (FunctionCall) node;

		/*
		 * False positives: sometimes a function call has been substituted by
		 * another one, and GumTree tags it as UNCHANGED, but target is CHANGED.
		 * So, unless target is also unchanged, we skip it
		 */
		if (call.getTarget().getChangeType() != ChangeType.UNCHANGED)
			return true;

		if (AnalysisUtilities.getChangedArguments(call).size() > 0)
			modifiedFunctionCalls.add(call);

		return true;
	}


}
