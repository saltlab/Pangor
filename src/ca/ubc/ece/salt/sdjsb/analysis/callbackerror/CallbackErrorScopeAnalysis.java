package ca.ubc.ece.salt.sdjsb.analysis.callbackerror;

import java.util.List;

import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.FunctionNode;

import ca.ubc.ece.salt.sdjsb.analysis.classify.ClassifierDataSet;
import ca.ubc.ece.salt.sdjsb.analysis.scope.Scope;
import ca.ubc.ece.salt.sdjsb.analysis.scope.ScopeAnalysis;
import ca.ubc.ece.salt.sdjsb.batch.AnalysisMetaInformation;
import ca.ubc.ece.salt.sdjsb.cfg.CFG;
import ca.ubc.ece.salt.sdjsb.classify.alert.ClassifierAlert;

public class CallbackErrorScopeAnalysis extends ScopeAnalysis<ClassifierAlert, ClassifierDataSet> {
	CallbackCallVisitor visitor = new CallbackCallVisitor();

	public CallbackErrorScopeAnalysis(ClassifierDataSet dataSet, AnalysisMetaInformation ami) {
		super(dataSet, ami);
	}

	@Override
	public void analyze(AstRoot root, List<CFG> cfgs) throws Exception {
		super.analyze(root, cfgs);

		/* Look at each function. */
		this.inspectFunctions(this.dstScope);
	}

	@Override
	public void analyze(AstRoot srcRoot, List<CFG> srcCFGs, AstRoot dstRoot, List<CFG> dstCFGs) throws Exception {
		super.analyze(srcRoot, srcCFGs, dstRoot, dstCFGs);

		/* Look at each function. */
		this.inspectFunctions(this.dstScope);
	}

	/**
	 * @param scope The function to inspect.
	 */
	private void inspectFunctions(Scope scope) {
		/* Visit the function and look for STH patterns. */
		if (scope.scope instanceof FunctionNode) {
			FunctionNode function = (FunctionNode) scope.scope;
			function.getBody().visit(visitor);
		} else {
			scope.scope.visit(visitor);
		}

		/*
		 * This method was copied and pasted from somewhere else. But Why should
		 * we visit the child functions, if visitor is already going to do it?
		 */
		// for (Scope child : scope.children) {
		// // inspectFunctions(child);
		// }

	}

}
