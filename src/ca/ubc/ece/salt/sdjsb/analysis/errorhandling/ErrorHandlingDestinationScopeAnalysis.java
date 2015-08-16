package ca.ubc.ece.salt.sdjsb.analysis.errorhandling;

import java.util.LinkedList;
import java.util.List;

import org.mozilla.javascript.Node;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.NodeVisitor;
import org.mozilla.javascript.ast.TryStatement;

import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode.ChangeType;
import ca.ubc.ece.salt.sdjsb.analysis.AnalysisUtilities;
import ca.ubc.ece.salt.sdjsb.analysis.classify.ClassifierDataSet;
import ca.ubc.ece.salt.sdjsb.analysis.scope.Scope;
import ca.ubc.ece.salt.sdjsb.analysis.scope.ScopeAnalysis;
import ca.ubc.ece.salt.sdjsb.batch.AnalysisMetaInformation;
import ca.ubc.ece.salt.sdjsb.cfg.CFG;
import ca.ubc.ece.salt.sdjsb.classify.alert.ClassifierAlert;

/**
 * Classifies repairs that repair an uncaught exception by adding surrounding
 * the throwing statement with a try/catch block.
 */
public class ErrorHandlingDestinationScopeAnalysis extends ScopeAnalysis<ClassifierAlert, ClassifierDataSet> {

	/** Stores the possible error handling repairs. **/
	private List<ErrorHandlingCheck> errorHandlingChecks;

	public ErrorHandlingDestinationScopeAnalysis(ClassifierDataSet dataSet,
			AnalysisMetaInformation ami) {
		super(dataSet, ami);
		this.errorHandlingChecks = new LinkedList<ErrorHandlingCheck>();
	}

	/**
	 * @return The set of possible error handling repairs (or
	 * anti-patterns if this is the source file analysis.
	 */
	public List<ErrorHandlingCheck> getCallbackErrorChecks() {
		return this.errorHandlingChecks;
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

		/* We still need to inspect the functions declared in this scope. */
		for(Scope child : scope.children) {
			inspectFunctions(child);
		}

	}

	/**
	 * Visits inserted try blocks and checks that none of the internal
	 * statements have been inserted or updated (all unchanged/moved).
	 */
	private class ErrorHandlingDestinationAnalysisVisitor implements NodeVisitor {

		private Scope scope;

		public ErrorHandlingDestinationAnalysisVisitor(Scope scope) {
			this.scope = scope;
		}

		@Override
		public boolean visit(AstNode node) {

			if(node instanceof TryStatement && node.getChangeType() == ChangeType.INSERTED) {

				TryStatement tryStatement = (TryStatement) node;

				/* Check that all inner nodes are unchanged. */
				AstNode tryBlock = tryStatement.getTryBlock();

				boolean changes = false;
				if(tryBlock instanceof org.mozilla.javascript.ast.Scope) {
					/* We need to check every statement in the block. */
					org.mozilla.javascript.ast.Scope block = (org.mozilla.javascript.ast.Scope) tryBlock;
					for(Node n : block) {
						if(n instanceof AstNode) {
							AstNode statement = (AstNode) n;
							changes = ChangedSubtreeVisitor.hasChanges(statement);
						}
						if(changes) break;
					}
				}
				else {
					/* We only need to check one statement. */
					changes = ChangedSubtreeVisitor.hasChanges(tryStatement.getTryBlock());
				}


				/* Register the repair. */
				if(!changes) {
					ErrorHandlingDestinationScopeAnalysis.this.errorHandlingChecks.add(
							new ErrorHandlingCheck(this.scope,
									AnalysisUtilities.getFunctionName(this.scope.scope)));
				}

			}

			else if(node instanceof FunctionNode) {
				return false;
			}

			return true;
		}

	}

}
