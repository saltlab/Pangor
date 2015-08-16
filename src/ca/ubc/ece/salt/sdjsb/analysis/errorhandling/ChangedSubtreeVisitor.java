package ca.ubc.ece.salt.sdjsb.analysis.errorhandling;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.NodeVisitor;

import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode.ChangeType;

/**
 * Checks if a block has any changed nodes (inserted or removed).
 */
public class ChangedSubtreeVisitor implements NodeVisitor {

	/** Keeps track of whether the subtree has changes. **/
	private boolean changes;

	/**
	 * @param node The node to check for changes.
	 * @return True if the node or one of its children are labelled as inserted
	 * 		   or removed.
	 */
	public static boolean hasChanges(AstNode node) {
		ChangedSubtreeVisitor visitor = new ChangedSubtreeVisitor();
		node.visit(visitor);
		return visitor.getChanges();
	}

	private ChangedSubtreeVisitor() {
		this.changes = false;
	}

	/**
	 * @return True if the subtree has an inserted or removed change label on
	 * 		   one or more of its children.
	 */
	private boolean getChanges() {
		return this.changes;
	}

	@Override
	public boolean visit(AstNode node) {

		if(node.getChangeType() == ChangeType.INSERTED ||
				node.getChangeType() == ChangeType.REMOVED) this.changes = true;

		return !this.changes;

	}

}
