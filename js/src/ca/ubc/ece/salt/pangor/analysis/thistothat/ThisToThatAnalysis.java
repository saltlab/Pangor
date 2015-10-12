package ca.ubc.ece.salt.pangor.analysis.thistothat;

import java.util.List;
import java.util.stream.Collectors;

import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.PropertyGet;

import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode.ChangeType;
import ca.ubc.ece.salt.pangor.analysis.classify.ClassifierDataSet;
import ca.ubc.ece.salt.pangor.analysis.meta.MetaAnalysis;
import ca.ubc.ece.salt.pangor.batch.AnalysisMetaInformation;
import ca.ubc.ece.salt.pangor.classify.alert.ClassifierAlert;
import ca.ubc.ece.salt.pangor.classify.alert.ThisToThatAlert;
import ca.ubc.ece.salt.pangor.js.analysis.AnalysisUtilities;

/**
 * Checker to find inserted if statements in a function where the "then part" of
 * the if statement has at least 2 assignments in which the LHS is a function
 * argument and 1 assignment in which the RHS is a function argument. In
 * practice, people do that to check if some argument is null and change the
 * order of them. They try somehow to simulate method overloading.
 */
public class ThisToThatAnalysis extends
 MetaAnalysis<ClassifierAlert, ClassifierDataSet, ThisToThatScopeAnalysis, ThisToThatScopeAnalysis> {

	public ThisToThatAnalysis(ClassifierDataSet dataSet, AnalysisMetaInformation ami) {
		super(dataSet, ami, new ThisToThatScopeAnalysis(dataSet, ami), new ThisToThatScopeAnalysis(dataSet, ami));
	}

	/**
	 * Synthesized alerts by inspecting the results of the scope analysis on the
	 * source file and the not defined analysis on the destination file.
	 *
	 * @throws Exception
	 */
	@Override
	protected void synthesizeAlerts() throws Exception {

		for (PropertyGet removedThis : this.srcAnalysis.visitor.removedThis) {
			/*
			 * For each removedThis, first we see if it is tagged as updated. If
			 * yes, we go to mapped node and check what was inserted there.
			 * However, sometimes it is tagged as removed, so we have to compare
			 * with all inserted property gets and try to match manually :(
			 */

			if (removedThis.getChangeType() == ChangeType.UPDATED) {
				PropertyGet mappedPropertyGet = (PropertyGet) removedThis.getMapping();

				// Reuse our visitor implementation!
				RemovedThisInsertedThatVisitor visitor = new RemovedThisInsertedThatVisitor();
				mappedPropertyGet.visit(visitor);

				if (visitor.insertedThat.size() == 1) {
					/*
					 * Even if we have UPDATED, it may be MOVED, so we check
					 */
					if (visitor.insertedThat.get(0).getEnclosingFunction().getMapping() == removedThis
							.getEnclosingFunction()) {

						this.registerAlert(new ThisToThatAlert(ami,
								AnalysisUtilities.getFunctionName(removedThis.getEnclosingFunction()),
								visitor.insertedThat.get(0).getLeft().toSource()));
					}
				}
			}

			if (removedThis.getChangeType() == ChangeType.REMOVED) {
				/*
				 * If 'this' was removed, look for any added 'that' on mapped
				 * enclosing function
				 */

				// Get enclosing function
				FunctionNode enclosingFunction = removedThis.getEnclosingFunction();

				// Get mapped function (destination)
				FunctionNode mappedFunction = (FunctionNode) enclosingFunction.getMapping();

				if (mappedFunction == null)
					continue;

				// Reuse our visitor
				RemovedThisInsertedThatVisitor visitor = new RemovedThisInsertedThatVisitor();
				visitor.setVisitFunctions(false);
				mappedFunction.visit(visitor);

				// Check if removedThis matches any of addedThat
				List<PropertyGet> matches = visitor.insertedThat.stream()
						.filter(p -> p.getRight().toSource().equals(removedThis.getRight().toSource()))
						.collect(Collectors.toList());

				if (matches.size() > 0)
					this.registerAlert(new ThisToThatAlert(ami,
							AnalysisUtilities.getFunctionName(removedThis.getEnclosingFunction()),
							matches.get(0).getLeft().toSource()));

			}
		}

		return;
	}
}