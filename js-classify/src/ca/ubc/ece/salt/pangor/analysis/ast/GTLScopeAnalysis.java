package ca.ubc.ece.salt.pangor.analysis.ast;

import java.util.List;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.ScriptNode;

import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode;
import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode.ChangeType;
import ca.ubc.ece.salt.pangor.analysis.scope.Scope;
import ca.ubc.ece.salt.pangor.batch.AnalysisMetaInformation;
import ca.ubc.ece.salt.pangor.cfg.CFG;
import ca.ubc.ece.salt.pangor.classify.ClassifierDataSet;
import ca.ubc.ece.salt.pangor.classify.alert.ClassifierAlert;
import ca.ubc.ece.salt.pangor.classify.alert.GlobalToLocalAlert;
import ca.ubc.ece.salt.pangor.js.analysis.scope.ScopeAnalysis;

/**
 * Performs an AST-only callback error handling analysis using AST visitors.
 *
 * This classifier is used for evaluation purposes only and should not be used
 * in actual data mining. Instead, use the CallbackErrorAnalysis classifier.
 */
public class GTLScopeAnalysis extends ScopeAnalysis<ClassifierAlert, ClassifierDataSet> {

	public GTLScopeAnalysis(ClassifierDataSet dataSet, AnalysisMetaInformation ami) {
		super(dataSet, ami);
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
	 *
	 * @param scope The function to inspect.
	 */
	private void inspectFunctions(Scope<AstNode> dstScope) {

		if(dstScope.getScope().getMapping() != null) {

			Scope<AstNode> srcScope = this.getSrcScope((ScriptNode)dstScope.getScope().getMapping());

            if(dstScope.getScope() instanceof FunctionNode) {

                /* Check all the new local variables. If they used to be global but
                 * are now local, generate an alert. */
                for(String localVariable : dstScope.getVariables().keySet()) {

                    AstNode variableNode = dstScope.getVariables().get(localVariable);

                    if(variableNode.getChangeType() == ChangeType.INSERTED
                    		&& (variableNode instanceof Name)
                    		&& (!(variableNode.getParent() instanceof FunctionNode))
                    		&& !srcScope.isLocal(localVariable)
                    		&& srcScope.isGlobal(localVariable)
                    		&& !deletedInScope(srcScope, localVariable)) {
                        this.registerAlert(dstScope.getVariables().get(localVariable), new GlobalToLocalAlert(this.ami, "[TODO: function name]", "AST_GTL", localVariable));
                    }

                }

            }
            else {

                /* Check all the new local variables. If they used to be global but
                 * are now local, generate an alert. */
                for(String localVariable : this.dstScope.variables.keySet()) {

                    AstNode variableNode = this.dstScope.variables.get(localVariable);

                    if(variableNode.getChangeType() == ChangeType.INSERTED
                    		&& (variableNode instanceof Name)
                    		&& ((variableNode.getParent() instanceof FunctionNode))
                    		&& this.srcScope.isGlobal(localVariable)
                    		&& !deletedInScope(srcScope, localVariable)) {
                        this.registerAlert(dstScope.getVariables().get(localVariable), new GlobalToLocalAlert(this.ami, "[TODO: function name]", "AST_GTL", localVariable));
                    }

                }

            }

        }

		/* Visit the child functions. */
		for(Scope<AstNode> child : dstScope.getChildren()) {
			inspectFunctions(child);
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
