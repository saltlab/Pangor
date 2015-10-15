package ca.ubc.ece.salt.pangor.analysis.promises;

import java.util.List;

import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.NewExpression;
import org.mozilla.javascript.ast.NodeVisitor;

import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode;
import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode.ChangeType;
import ca.ubc.ece.salt.pangor.analysis.scope.Scope;
import ca.ubc.ece.salt.pangor.batch.AnalysisMetaInformation;
import ca.ubc.ece.salt.pangor.cfg.CFG;
import ca.ubc.ece.salt.pangor.classify.ClassifierDataSet;
import ca.ubc.ece.salt.pangor.classify.alert.ClassifierAlert;
import ca.ubc.ece.salt.pangor.js.analysis.scope.ScopeAnalysis;

public class PromisesDestinationAnalysis extends ScopeAnalysis<ClassifierAlert, ClassifierDataSet> {

	/** Set to true if the analysis determines the pre-conditoins are met. **/
	private boolean meetsPostConditions;

	public PromisesDestinationAnalysis(ClassifierDataSet dataSet,
			AnalysisMetaInformation ami) {
		super(dataSet, ami);
		this.meetsPostConditions = false;
	}

	/**
	 * @return True if the pre-conditions are met for the refactoring.
	 */
	public boolean meetsPostConditions() {
		return this.meetsPostConditions;
	}

	@Override
	public void analyze(ClassifiedASTNode root, List<CFG> cfgs) throws Exception {
		super.analyze(root, cfgs);

		/* Look at each function. */
		this.inspectFunctions(this.dstScope);
	}

	@Override
	public void analyze(ClassifiedASTNode srcRoot, List<CFG> srcCFGs, ClassifiedASTNode dstRoot, List<CFG> dstCFGs) throws Exception {
		super.analyze(srcRoot, srcCFGs, dstRoot, dstCFGs);

		/* Look at each function. */
		this.inspectFunctions(this.dstScope);
	}

	/**
	 * @param scope The function to inspect.
	 */
	private void inspectFunctions(Scope<AstNode> scope) {

		/* Visit the function and look for REF_PROM patterns. */
		if (scope.getScope() instanceof FunctionNode) {
			FunctionNode function = (FunctionNode) scope.getScope();
			if(function.getChangeType() != ChangeType.INSERTED && meetsPostConditions(function)) this.meetsPostConditions = true;
		} else {
			if(meetsPostConditions(scope.getScope())) this.meetsPostConditions = true;
		}

		for (Scope<AstNode> child : scope.getChildren()) {
			inspectFunctions(child);
		}

	}

	/**
	 * Checks the if the preconditions for a callback to promise
	 * refactoring exists.
	 * @param body The script or body of a function.
	 * @return True if this function meets the pre-conditions for a
	 * 		   callback to promise refactoring.
	 */
	private boolean meetsPostConditions(AstNode function) {
		PromisesDestinationVisitor visitor = new PromisesDestinationVisitor(function);
		function.visit(visitor);
		return visitor.meetsPostConditions;
	}

	/**
	 * Visits a function to find anti-patterns or pre-conditions for a callback
	 * to promises refactoring.
	 */
	private class PromisesDestinationVisitor implements NodeVisitor {

		public boolean meetsPostConditions;
		private AstNode function;

		public PromisesDestinationVisitor(AstNode function) {
			this.meetsPostConditions = false;
			this.function = function;
		}

		@Override
		public boolean visit(AstNode node) {

			/* Look for inserted 'new Promise' expressions. */
			if(node.getType() == Token.NEW && node.getChangeType() == ChangeType.INSERTED) {
				NewExpression ne = (NewExpression)node;
				if(ne.getTarget().getType() == Token.NAME && ne.getTarget().toSource().equals("Promise")) {
					this.meetsPostConditions = true;
				}
			}
			else if(node.getType() == Token.FUNCTION && node != this.function) {
				return false;
			}

			return true;
		}

	}
}
