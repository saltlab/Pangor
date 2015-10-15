package ca.ubc.ece.salt.pangor.analysis.globaltolocal;

import java.util.LinkedList;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.FunctionNode;

import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode.ChangeType;
import ca.ubc.ece.salt.pangor.analysis.MetaAnalysis;
import ca.ubc.ece.salt.pangor.analysis.globaltolocal.GlobalToLocalFlowAnalysis.GlobalToLocal;
import ca.ubc.ece.salt.pangor.analysis.scope.Scope;
import ca.ubc.ece.salt.pangor.batch.AnalysisMetaInformation;
import ca.ubc.ece.salt.pangor.classify.ClassifierDataSet;
import ca.ubc.ece.salt.pangor.classify.alert.ClassifierAlert;
import ca.ubc.ece.salt.pangor.classify.alert.GlobalToLocalAlert;
import ca.ubc.ece.salt.pangor.js.analysis.scope.JavaScriptScope;
import ca.ubc.ece.salt.pangor.js.analysis.scope.ScopeAnalysis;

public class GlobalToLocalAnalysis extends MetaAnalysis<ClassifierAlert, ClassifierDataSet, ScopeAnalysis<ClassifierAlert, ClassifierDataSet>, GlobalToLocalFlowAnalysis> {

	public GlobalToLocalAnalysis(ClassifierDataSet dataSet, AnalysisMetaInformation ami) {
		super(dataSet, ami, new ScopeAnalysis<ClassifierAlert, ClassifierDataSet>(dataSet, ami), new GlobalToLocalFlowAnalysis(dataSet, ami));
	}

	/**
	 * Synthesized alerts by inspecting the results of the scope analysis on
	 * the source file and the not defined analysis on the destination file.
	 * @throws Exception
	 */
	@Override
	protected void synthesizeAlerts() throws Exception {

		for(GlobalToLocal gtl : new LinkedList<GlobalToLocal>(this.dstAnalysis.notDefinedRepairs)) {

			/* Remove identifiers that were deleted in the source scope.
			 * Check the entire scope tree. this reduces false positives from
			 * methods which are renamed. */

			JavaScriptScope srcScope = this.srcAnalysis.getDstScope();

			if(this.deletedInScope(srcScope, gtl.identifier)) {
				this.dstAnalysis.notDefinedRepairs.remove(gtl);
			}

			if(gtl.scope.getScope() instanceof FunctionNode) {

                /* Check that the variable was previously in the global scope and not previously in the local scope. */

				FunctionNode sourceFunction = (FunctionNode)gtl.scope.getScope().getMapping();
				if(sourceFunction != null) {

					Scope<AstNode> sourceFunctionScope = srcScope.getFunctionScope(sourceFunction);
					if(!sourceFunctionScope.isGlobal(gtl.identifier) || sourceFunctionScope.isLocal(gtl.identifier)) {

						/* The variable was not previously part of the global scope. */
						this.dstAnalysis.notDefinedRepairs.remove(gtl);

					}

				}
			}
			else {

                /* Check that the variable was not explicitly declared previously. */
				if(!srcScope.globals.containsKey(gtl.identifier)) {

					/* The variable did not exist in the global source (maybe it was a field). */
					this.dstAnalysis.notDefinedRepairs.remove(gtl);

				}


			}

		}

		/* Generate alerts for the remaining GlobalToLocal elements. */
		for(GlobalToLocal gtl : this.dstAnalysis.notDefinedRepairs) {
			this.registerAlert(new GlobalToLocalAlert(this.ami, "[TODO: function name]", "GTL", gtl.identifier));
		}

	}

	/**
	 * Determines if the identifier was deleted in some source scope.
	 * @param scope The source scope to check.
	 * @param notDefinedRepairs The list of potential not defined repairs.
	 * @return true if the identifier was deleted in the scope or a method scope.
	 */
	private boolean deletedInScope(Scope<AstNode> scope, String identifier) {

		AstNode node = scope.getVariables().get(identifier);
		if(node != null && node.getChangeType() == ChangeType.REMOVED) return true;

		for(Scope<AstNode> child : scope.getChildren()) {
			if(deletedInScope(child, identifier)) return true;
		}

		return false;

	}

}
