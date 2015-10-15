package ca.ubc.ece.salt.pangor.analysis.errorhandling;

import java.util.LinkedList;
import java.util.List;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.NodeVisitor;
import org.mozilla.javascript.ast.TryStatement;

import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode;
import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode.ChangeType;
import ca.ubc.ece.salt.pangor.analysis.scope.Scope;
import ca.ubc.ece.salt.pangor.batch.AnalysisMetaInformation;
import ca.ubc.ece.salt.pangor.cfg.CFG;
import ca.ubc.ece.salt.pangor.classify.ClassifierDataSet;
import ca.ubc.ece.salt.pangor.classify.alert.ClassifierAlert;
import ca.ubc.ece.salt.pangor.js.analysis.scope.ScopeAnalysis;

/**
 * Classifies repairs that repair an uncaught exception by adding surrounding
 * the throwing statement with a try/catch block.
 */
public class ErrorHandlingDestinationScopeAnalysis extends ScopeAnalysis<ClassifierAlert, ClassifierDataSet> {

	/**
	 * Stores the unchanged calls which have been protected by an inserted
	 * try statement.
	 */
	private List<ErrorHandlingCheck> protectedCalls;

	public ErrorHandlingDestinationScopeAnalysis(ClassifierDataSet dataSet,
			AnalysisMetaInformation ami) {
		super(dataSet, ami);
		this.protectedCalls = new LinkedList<ErrorHandlingCheck>();
	}

	/**
	 * @return The set of possible calls that are protected by an inserted
	 * try statement.
	 */
	public List<ErrorHandlingCheck> getProtectedCalls() {
		return this.protectedCalls;
	}

	@Override
	public void analyze(ClassifiedASTNode root, List<CFG> cfgs) throws Exception {

		super.analyze(root, cfgs);

		/* Look at each function. */
		this.inspectFunctions(this.dstScope);

	}

	@Override
	public void analyze(ClassifiedASTNode srcRoot, List<CFG> srcCFGs, ClassifiedASTNode dstRoot,
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
	private void inspectFunctions(Scope<AstNode> scope) {

		ErrorHandlingDestinationAnalysisVisitor visitor = new ErrorHandlingDestinationAnalysisVisitor(scope);
		scope.getScope().visit(visitor);

		/* We still need to inspect the functions declared in this scope. */
		for(Scope<AstNode> child : scope.getChildren()) {
			inspectFunctions(child);
		}

	}

	/**
	 * Visits inserted try blocks and checks that none of the internal
	 * statements have been inserted or updated (all unchanged/moved).
	 */
	private class ErrorHandlingDestinationAnalysisVisitor implements NodeVisitor {

		private Scope<AstNode> scope;

		public ErrorHandlingDestinationAnalysisVisitor(Scope<AstNode> scope) {
			this.scope = scope;
		}

		@Override
		public boolean visit(AstNode node) {

			if(node instanceof TryStatement && node.getChangeType() == ChangeType.INSERTED) {

				TryStatement tryStatement = (TryStatement) node;

				/* Check that there are no inserted function calls and get a
				 * list of the unchanged function calls. */

				ProtectedMethodCallVisitor visitor = new ProtectedMethodCallVisitor();
				tryStatement.visit(visitor);

				List<String> insertedMethodCalls = visitor.getInsertedMethodCalls();
				List<String> unchangedMethodCalls = visitor.getUnchangedMethodCalls();

				/* If there are inserted calls, they may be the reason why the
				 * try/catch block was inserted, so do not register a repair. */
				if(insertedMethodCalls.size() == 0) {

					/* Register the potential repair. */
					ErrorHandlingDestinationScopeAnalysis.this.protectedCalls.add(
							new ErrorHandlingCheck(this.scope, unchangedMethodCalls));

				}

			}

			else if(node != this.scope.getScope() && node instanceof FunctionNode) {
				return false;
			}

			return true;
		}

	}

}