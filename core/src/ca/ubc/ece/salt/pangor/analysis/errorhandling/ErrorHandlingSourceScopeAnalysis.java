package ca.ubc.ece.salt.pangor.analysis.errorhandling;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.NodeVisitor;
import org.mozilla.javascript.ast.TryStatement;

import ca.ubc.ece.salt.pangor.analysis.AnalysisUtilities;
import ca.ubc.ece.salt.pangor.analysis.classify.ClassifierDataSet;
import ca.ubc.ece.salt.pangor.analysis.scope.Scope;
import ca.ubc.ece.salt.pangor.analysis.scope.ScopeAnalysis;
import ca.ubc.ece.salt.pangor.batch.AnalysisMetaInformation;
import ca.ubc.ece.salt.pangor.cfg.CFG;
import ca.ubc.ece.salt.pangor.classify.alert.ClassifierAlert;

/**
 * Classifies repairs that repair an uncaught exception by adding surrounding
 * the throwing statement with a try/catch block.
 */
public class ErrorHandlingSourceScopeAnalysis extends ScopeAnalysis<ClassifierAlert, ClassifierDataSet> {

	/**
	 * Stores the unchanged calls which have been protected by an inserted
	 * try statement.
	 */
	private Map<AstNode, List<String>> unprotectedCalls;

	public ErrorHandlingSourceScopeAnalysis(ClassifierDataSet dataSet,
			AnalysisMetaInformation ami) {
		super(dataSet, ami);
		this.unprotectedCalls = new HashMap<AstNode, List<String>>();
	}

	/**
	 * @return The set of possible calls that are protected by a try statement.
	 */
	public Map<AstNode, List<String>> getUnprotectedCalls() {
		return this.unprotectedCalls;
	}

	@Override
	public void analyze(AstRoot root, List<CFG> cfgs) throws Exception {

		super.analyze(root, cfgs);

		/* Look at each function. */
		this.inspectFunctions(this.dstScope);

	}

	@Override
	public void analyze(AstRoot srcRoot, List<CFG> srcCFGs, AstRoot dstRoot,
			List<CFG> dstCFGs) throws Exception {

		super.analyze(srcRoot, srcCFGs, dstRoot, dstCFGs);

		/* Look at each function. */
		this.inspectFunctions(this.dstScope);

	}

	/**
	 * Visit each function. Trigger an alert when an inserted try statement
	 * surrounds an unchanged block.
	 * @param scope The function to inspect.
	 */
	private void inspectFunctions(Scope scope) {

		ErrorHandlingDestinationAnalysisVisitor visitor = new ErrorHandlingDestinationAnalysisVisitor(scope);
		scope.scope.visit(visitor);

		/* Store the unprotected methods for the meta analysis. */
		this.unprotectedCalls.put(scope.scope, visitor.getUnprotectedMethodCalls());

		/* We still need to inspect the functions declared in this scope. */
		for(Scope child : scope.children) {
			inspectFunctions(child);
		}

	}

	/**
	 * Visits a function and finds all method calls which are not protected by
	 * try blocks.
	 */
	private class ErrorHandlingDestinationAnalysisVisitor implements NodeVisitor {

		private Scope scope;

		/** Stores the identifiers for the unprotected method calls. **/
		private List<String> unprotectedMethodCalls;

		public ErrorHandlingDestinationAnalysisVisitor(Scope scope) {
			this.scope = scope;
			this.unprotectedMethodCalls = new LinkedList<String>();
		}

		public List<String> getUnprotectedMethodCalls() {
			return this.unprotectedMethodCalls;
		}

		@Override
		public boolean visit(AstNode node) {

			if(node instanceof FunctionCall) {
				FunctionCall call = (FunctionCall)node;
				String identifier = AnalysisUtilities.getIdentifier(call.getTarget());

				this.unprotectedMethodCalls.add(identifier);
			}
			else if(node instanceof TryStatement ||
					(node != scope.scope && node instanceof FunctionNode)) {
				return false;
			}

			return true;
		}

	}

}
