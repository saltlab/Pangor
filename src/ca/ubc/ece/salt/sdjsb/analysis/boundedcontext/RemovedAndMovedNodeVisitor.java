package ca.ubc.ece.salt.sdjsb.analysis.boundedcontext;

import java.util.HashSet;
import java.util.Set;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.NodeVisitor;

import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode.ChangeType;

/**
 * Visitor for storing all removed and moved nodes. Used to filter
 * false-positives.
 */
public class RemovedAndMovedNodeVisitor implements NodeVisitor {
	public Set<AstNode> removedAndMovedNodes = new HashSet<>();

	@Override
	public boolean visit(AstNode node) {
		if (node.getChangeType() == ChangeType.REMOVED || node.getChangeType() == ChangeType.MOVED)
			removedAndMovedNodes.add(node);

		return true;
	}

}
