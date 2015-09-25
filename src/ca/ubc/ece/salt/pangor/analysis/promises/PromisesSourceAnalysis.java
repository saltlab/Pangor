package ca.ubc.ece.salt.pangor.analysis.promises;

import java.util.List;

import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.FunctionNode;

import ca.ubc.ece.salt.pangor.analysis.classify.ClassifierDataSet;
import ca.ubc.ece.salt.pangor.analysis.scope.Scope;
import ca.ubc.ece.salt.pangor.analysis.scope.ScopeAnalysis;
import ca.ubc.ece.salt.pangor.batch.AnalysisMetaInformation;
import ca.ubc.ece.salt.pangor.cfg.CFG;
import ca.ubc.ece.salt.pangor.classify.alert.ClassifierAlert;

public class PromisesSourceAnalysis extends ScopeAnalysis<ClassifierAlert, ClassifierDataSet> {

	//public FunctionCallVisitor visitor = new FunctionCallVisitor();

	public PromisesSourceAnalysis(ClassifierDataSet dataSet,
			AnalysisMetaInformation ami) {
		super(dataSet, ami);
	}

	@Override
	public void analyze(AstRoot root, List<CFG> cfgs) throws Exception {
		super.analyze(root, cfgs);

		/* Look at each function. */
	}

	@Override
	public void analyze(AstRoot srcRoot, List<CFG> srcCFGs, AstRoot dstRoot, List<CFG> dstCFGs) throws Exception {
		super.analyze(srcRoot, srcCFGs, dstRoot, dstCFGs);

		/* Look at each function. */
	}

	/**
	 * @param scope The function to inspect.
	 */
	private void inspectFunctions(Scope scope) {

		/* Visit the function and look for STH patterns. */
		if (scope.scope instanceof FunctionNode) {
			FunctionNode function = (FunctionNode) scope.scope;
			//function.getBody().visit(visitor);
		} else {
			//scope.scope.visit(visitor);
		}

		for (Scope child : scope.children) {
			inspectFunctions(child);
		}

	}

}
