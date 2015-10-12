package ca.ubc.ece.salt.pangor.analysis.errorhandling;

import java.util.LinkedList;
import java.util.List;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.NodeVisitor;

import ca.ubc.ece.salt.pangor.js.analysis.AnalysisUtilities;

/**
 * Checks if a block has any changed nodes (inserted or removed).
 */
public class ProtectedMethodCallVisitor implements NodeVisitor {

	/** Store the identifiers for inserted method calls. **/
	private List<String> insertedMethodCalls;

	/** Store the identifiers for removed method calls. **/
	private List<String> removedMethodCalls;

	/** Store the identifiers for unchanged, moved and updated method calls. **/
	private List<String> unchangedMethodCalls;

	public ProtectedMethodCallVisitor() {
		this.insertedMethodCalls = new LinkedList<String>();
		this.removedMethodCalls = new LinkedList<String>();
		this.unchangedMethodCalls = new LinkedList<String>();
	}

	/**
	 * @return The method calls that were inserted into the block.
	 */
	public List<String> getInsertedMethodCalls() {
		return this.insertedMethodCalls;
	}

	/**
	 * @return The method calls that were removed from the block.
	 */
	public List<String> getRemovedMethodCalls() {
		return this.removedMethodCalls;
	}

	/**
	 * @return The method calls that are unchanged in the block.
	 */
	public List<String> getUnchangedMethodCalls() {
		return this.unchangedMethodCalls;
	}

	@Override
	public boolean visit(AstNode node) {

		if(node instanceof FunctionCall) {

			FunctionCall call = (FunctionCall)node;
			String identifier = AnalysisUtilities.getIdentifier(call.getTarget());

			switch(node.getChangeType()) {
			case INSERTED:
				this.insertedMethodCalls.add(identifier);
				break;
			case REMOVED:
				this.removedMethodCalls.add(identifier);
				break;
			case UPDATED:
			case MOVED:
			case UNCHANGED:
				this.unchangedMethodCalls.add(identifier);
				break;
			case UNKNOWN:
			default:
			}

		}

		if(node instanceof FunctionNode) {
			return false;
		}

		return true;

	}

}
