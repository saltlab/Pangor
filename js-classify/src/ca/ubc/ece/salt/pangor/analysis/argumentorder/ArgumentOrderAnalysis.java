package ca.ubc.ece.salt.pangor.analysis.argumentorder;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

import org.mozilla.javascript.Node;
import org.mozilla.javascript.ast.Assignment;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.ExpressionStatement;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.IfStatement;
import org.mozilla.javascript.ast.Name;

import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode.ChangeType;
import ca.ubc.ece.salt.pangor.analysis.MetaAnalysis;
import ca.ubc.ece.salt.pangor.batch.AnalysisMetaInformation;
import ca.ubc.ece.salt.pangor.classify.ClassifierDataSet;
import ca.ubc.ece.salt.pangor.classify.alert.ArgumentOrderAlert;
import ca.ubc.ece.salt.pangor.classify.alert.ClassifierAlert;
import ca.ubc.ece.salt.pangor.js.analysis.AnalysisUtilities;

/**
 * Checker to find inserted if statements in a function where the "then part" of
 * the if statement has at least 2 assignments in which the LHS is a function
 * argument and 1 assignment in which the RHS is a function argument. In
 * practice, people do that to check if some argument is null and change the
 * order of them. They try somehow to simulate method overloading.
 */
public class ArgumentOrderAnalysis extends
		MetaAnalysis<ClassifierAlert, ClassifierDataSet, ArgumentOrderScopeAnalysis, ArgumentOrderScopeAnalysis> {

	public ArgumentOrderAnalysis(ClassifierDataSet dataSet, AnalysisMetaInformation ami) {
		super(dataSet, ami, new ArgumentOrderScopeAnalysis(dataSet, ami), new ArgumentOrderScopeAnalysis(dataSet, ami));
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
		 * Take all inserted if statements and check which ones have assignments
		 * with one of function arguments on the LHS
		 */

		for (IfStatement statement : this.dstAnalysis.visitor.storedNodes) {
			// Get enclosing function
			FunctionNode enclosingFunction = statement.getEnclosingFunction();

			if (enclosingFunction == null || enclosingFunction.getChangeType() == ChangeType.INSERTED
					|| enclosingFunction.getChangeType() == ChangeType.MOVED) {
				continue;
			}

			// Get parameters which are Names and store identifiers
			Set<String> parameters = enclosingFunction.getParams().stream()
					.filter(p -> p instanceof Name)
					.map(p -> ((Name) p).getIdentifier())
					.collect(Collectors.toSet());

			// Get modified variables from if block
			Set<String> modifiedVariablesLHS = variablesOnSideAssignmentsOfBlock(statement.getThenPart(), true);
			Set<String> modifiedVariablesRHS = variablesOnSideAssignmentsOfBlock(statement.getThenPart(), false);

			// See if there is any intersection between modified variables and
			// parameters
			// WARNING: retainAll is destructive
			Set<String> parametersClone = new HashSet<>(parameters);

			parameters.retainAll(modifiedVariablesLHS);
			parametersClone.retainAll(modifiedVariablesRHS);

			// We look for 2 modified parameters (LHS) and 1 used (RHS)
			if (parameters.size() >= 2 && parametersClone.size() >= 1) {
				this.registerAlert(new ArgumentOrderAlert(ami,
						AnalysisUtilities.getFunctionName(statement.getEnclosingFunction()), "TODO"));
			}

		}

		return;
	}

	/*
	 * Using normal iterator over children. I think having a visitor for that is
	 * an overkill.
	 */
	private static Set<String> variablesOnSideAssignmentsOfBlock(AstNode block, boolean leftHandSide) {
		Set<String> output = new HashSet<>();

		Iterator<Node> blockIterator = block.iterator();
		while (blockIterator.hasNext()) {
			Node node = blockIterator.next();

			if (!(node instanceof ExpressionStatement))
				continue;

			ExpressionStatement expression = (ExpressionStatement) node;

			if (!(expression.getExpression() instanceof Assignment))
				continue;

			Assignment assignment = (Assignment) expression.getExpression();

			if (leftHandSide) {
				if (assignment.getLeft() instanceof Name)
					output.add(((Name) assignment.getLeft()).getIdentifier());
			} else {
				if (assignment.getRight() instanceof Name)
					output.add(((Name) assignment.getRight()).getIdentifier());
			}

		}

		return output;
	}
}