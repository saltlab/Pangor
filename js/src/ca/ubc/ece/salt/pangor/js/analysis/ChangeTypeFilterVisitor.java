package ca.ubc.ece.salt.pangor.js.analysis;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.NodeVisitor;

import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode.ChangeType;

/**
 * Visitor for storing all nodes with a certain ChangeType. Change types are set
 * up on constructor Used on ArgumentAnalysis to recursively look for changes on
 * object literals Used on BoundedContextAnalysis to look for removed and moved
 * nodes from mapped function calls
 */
public class ChangeTypeFilterVisitor implements NodeVisitor {
	public Set<AstNode> storedNodes = new HashSet<>();
	public Set<ChangeType> changeTypes;

	public ChangeTypeFilterVisitor(ChangeType... changeTypes) {
		super();
		this.changeTypes = new HashSet<>(Arrays.asList(changeTypes));
	}

	@Override
	public boolean visit(AstNode node) {
		if (changeTypes.contains(node.getChangeType()))
			storedNodes.add(node);

		return true;
	}

}
