package ca.ubc.ece.salt.sdjsb.analysis.argument;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.FunctionNode;

import ca.ubc.ece.salt.sdjsb.analysis.classify.ClassifierDataSet;
import ca.ubc.ece.salt.sdjsb.analysis.scope.Scope;
import ca.ubc.ece.salt.sdjsb.analysis.scope.ScopeAnalysis;
import ca.ubc.ece.salt.sdjsb.batch.AnalysisMetaInformation;
import ca.ubc.ece.salt.sdjsb.cfg.CFG;
import ca.ubc.ece.salt.sdjsb.classify.alert.ClassifierAlert;

public class ArgumentScopeAnalysis extends ScopeAnalysis<ClassifierAlert, ClassifierDataSet> {
	protected static final Logger logger = LogManager.getLogger(ArgumentScopeAnalysis.class);

	public ModifiedFunctionCallVisitor callVisitor = new ModifiedFunctionCallVisitor();
	public ModifiedFunctionDefinitionVisitor definitionVisitor = new ModifiedFunctionDefinitionVisitor();

	public ArgumentScopeAnalysis(ClassifierDataSet dataSet, AnalysisMetaInformation ami) {
		super(dataSet, ami);
	}

	@Override
	public void analyze(AstRoot root, List<CFG> cfgs) throws Exception {
		super.analyze(root, cfgs);

		/*
		 * Tests gave a lot of false positives! Skipping them
		 */
		if (ami.repairedFile.contains("test/") || ami.repairedFile.contains("spec/")
				|| ami.repairedFile.contains("tests/") || ami.repairedFile.contains("__test__")) {
			logger.info("Skipping test file: " + ami.repairedFile);
			return;
		}

		/*
		 * Minified files gave a lot of false positives! Skipping them
		 */
		if (ami.repairedFile.endsWith(".min.js")) {
			logger.info("Skipping minimifed file file: " + ami.repairedFile);
			return;
		}

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
			function.getBody().visit(callVisitor);
			function.getBody().visit(definitionVisitor);
		} else {
			scope.scope.visit(callVisitor);
			scope.scope.visit(definitionVisitor);
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
