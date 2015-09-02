package ca.ubc.ece.salt.pangor.analysis.argument;

import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mozilla.javascript.ast.ArrayLiteral;
import org.mozilla.javascript.ast.Assignment;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.ConditionalExpression;
import org.mozilla.javascript.ast.ExpressionStatement;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.InfixExpression;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.ObjectLiteral;
import org.mozilla.javascript.ast.PropertyGet;

import ca.ubc.ece.salt.pangor.analysis.AnalysisUtilities;
import ca.ubc.ece.salt.pangor.analysis.classify.ClassifierDataSet;
import ca.ubc.ece.salt.pangor.analysis.meta.MetaAnalysis;
import ca.ubc.ece.salt.pangor.batch.AnalysisMetaInformation;
import ca.ubc.ece.salt.pangor.classify.alert.ArgumentAlert;
import ca.ubc.ece.salt.pangor.classify.alert.ClassifierAlert;

public class ArgumentAnalysis
		extends MetaAnalysis<ClassifierAlert, ClassifierDataSet, ArgumentScopeAnalysis, ArgumentScopeAnalysis> {

	protected static final Logger logger = LogManager.getLogger(ArgumentAnalysis.class);

	public ArgumentAnalysis(ClassifierDataSet dataSet, AnalysisMetaInformation ami) {
		super(dataSet, ami, new ArgumentScopeAnalysis(dataSet, ami), new ArgumentScopeAnalysis(dataSet, ami));
	}

	/**
	 * Synthesized alerts by inspecting the results of the scope analysis on the
	 * source file and the not defined analysis on the destination file.
	 *
	 * @throws Exception
	 */
	@Override
	protected void synthesizeAlerts() throws Exception {
		/*
		 * The idea is to use the visitor to get all possible
		 * modifiedFunctionCalls and filter false positives here
		 */

		for (FunctionCall call : dstAnalysis.callVisitor.modifiedFunctionCalls) {
			/*
			 * If function definition was also modified, do not add
			 */
			if (isCallOnDefinitionSet(call, dstAnalysis.definitionVisitor.modifiedFunctionDefinitions)) {
				logger.info("Skipping {} because function defition has also been changed. Repaired commit id: {}",
						AnalysisUtilities.getFunctionCallName(call), ami.repairedCommitID);
				continue;
			}

			String fullCallName = AnalysisUtilities.getFunctionFullCallName(call);

			/*
			 * Remove false positives of jQuery.fn.extend, Backbone, React and App
			 */
			if (fullCallName.endsWith("extend") || fullCallName.equals("Class")
					|| fullCallName.contains("createClass")) {
				logger.info("Skipping extend(...), Class(...) and related calls");
				continue;
			}

			for (AstNode argument : AnalysisUtilities.getChangedArguments(call)) {
				this.registerAlert(new ArgumentAlert(ami, AnalysisUtilities.getFunctionCallName(call),
						argumentToString(argument), argument.getChangeType()));
			}
		}

		return;
	}

	private boolean isCallOnDefinitionSet(FunctionCall call, Set<FunctionNode> modifiedFunctionDefinitions) {
		String callIdentifier = "";

		if (call.getTarget() instanceof Name) {
			callIdentifier = ((Name) call.getTarget()).getIdentifier();
		} else if (call.getTarget() instanceof PropertyGet) {
			AstNode rightTarget = ((PropertyGet) call.getTarget()).getRight();

			callIdentifier = ((Name) rightTarget).getIdentifier();
		}

		for (FunctionNode defition : modifiedFunctionDefinitions) {
			if (defition.getFunctionName() != null && defition.getFunctionName().getIdentifier().equals(callIdentifier))
				return true;
		}

		return false;
	}

	private String argumentToString(AstNode argument) {
		if (argument instanceof ObjectLiteral)
			return "~objectLiteral~";

		if (argument instanceof FunctionNode)
			return "~anonymous~";

		if (argument instanceof ConditionalExpression)
			return "~conditionalExpr~";

		if (argument instanceof ExpressionStatement || argument instanceof Assignment
				|| argument instanceof InfixExpression)
			return "~expression~";

		if (argument instanceof ArrayLiteral)
			return "~array~";

		return argument.toSource().replace("\"", "");
	}
}
