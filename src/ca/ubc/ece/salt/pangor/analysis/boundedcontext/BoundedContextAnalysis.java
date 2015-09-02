package ca.ubc.ece.salt.pangor.analysis.boundedcontext;

import java.util.List;
import java.util.stream.Collectors;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.PropertyGet;

import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode.ChangeType;
import ca.ubc.ece.salt.pangor.analysis.AnalysisUtilities;
import ca.ubc.ece.salt.pangor.analysis.classify.ClassifierDataSet;
import ca.ubc.ece.salt.pangor.analysis.meta.MetaAnalysis;
import ca.ubc.ece.salt.pangor.batch.AnalysisMetaInformation;
import ca.ubc.ece.salt.pangor.classify.alert.BoundedContextAlert;
import ca.ubc.ece.salt.pangor.classify.alert.ClassifierAlert;

public class BoundedContextAnalysis extends
		MetaAnalysis<ClassifierAlert, ClassifierDataSet, BoundedContextScopeAnalysis, BoundedContextScopeAnalysis> {

	public BoundedContextAnalysis(ClassifierDataSet dataSet, AnalysisMetaInformation ami) {
		super(dataSet, ami, new BoundedContextScopeAnalysis(dataSet, ami),
				new BoundedContextScopeAnalysis(dataSet, ami));
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
		 * We take all the bounded context calls from destination file and
		 * filter them
		 */
		List<FunctionCall> insrtBoundedCalls = filterInsertedBoundedCalls(this.dstAnalysis.visitor.boundedContextCalls);

		for (FunctionCall call : insrtBoundedCalls) {
			this.registerAlert(new BoundedContextAlert(ami, AnalysisUtilities.getBoundedContextFunctionName(call),
							((PropertyGet) call.getTarget()).getRight().toSource()));
		}

		return;
	}

	/*
	 * Filter list
	 */
	private List<FunctionCall> filterInsertedBoundedCalls(List<FunctionCall> calls) {
		return calls.stream()
				.filter(call -> (isInsertedBoundedCall(call)))
				.collect(Collectors.toList());
	}

	/*
	 * This is where we actually do the checks for each of the calls with
	 * bounded context on the destination file
	 */
	private boolean isInsertedBoundedCall(FunctionCall call) {
		/*
		 * Case 1: GumTree detects function UNCHANGED, but target INSERTED
		 */
		if (call.getChangeType() == ChangeType.UNCHANGED && call.getTarget().getChangeType() == ChangeType.INSERTED) {
			return true;
		}

		/*
		 * Case 2: GumTree detects function as INSERTED. But sometimes its a
		 * false positive, so we check if the function call without the bounded
		 * call was on the old version
		 *
		 * First we used to check in the entire srcScope, but this was giving a
		 * lot of false positives, so now we get the enclosing function, its
		 * mapped (source version) and look for unbounded version of call there
		 */
		if (call.getChangeType() == ChangeType.INSERTED || call.getChangeType() == ChangeType.UPDATED) {
			if (call.getEnclosingFunction() != null) {
				FunctionNode function = (FunctionNode) call.getEnclosingFunction().getMapping();

				if (function != null) {
					FunctionCallVisitor visitor = new FunctionCallVisitor();
					function.visit(visitor);

					List<FunctionCall> changedCalls = visitor.normalCalls.stream()
							.filter(c -> c.getChangeType() != ChangeType.UNCHANGED).collect(Collectors.toList());

					if (isUnboundedVersionOfCallOnList(call, changedCalls))
						return true;
				}
			} else {
				if (isUnboundedVersionOfCallOnList(call, this.srcAnalysis.visitor.normalCalls))
					return true;
			}


		}

		/*
		 * Case 3: app.get('/debug', redirectToRoot) to app.get('/debug',
		 * redirectToRoot.bind(this)); And GumTree says redirectToRoot was
		 * removed from source and inserted in destination
		 *
		 * What we do is:
		 * * Get parent from call
		 * * Get mapped element
		 * * Use custom visitor to find removed nodes on it
		 * * See if target from call is in this removed list
		 */
		if (call.getChangeType() == ChangeType.INSERTED) {
			AstNode parent = call.getParent();

			if ((parent.getChangeType() == ChangeType.UNCHANGED || parent.getChangeType() == ChangeType.UPDATED)
					&& parent.getMapping() != null) {
				AstNode mapped = (AstNode) parent.getMapping();

				ChangeTypeFilterVisitor visitor = new ChangeTypeFilterVisitor(ChangeType.MOVED, ChangeType.REMOVED);
				mapped.visit(visitor);

				/*
				 * To remove false positives, we check if node is not part of a
				 * function that already has bounded context
				 */
				List<AstNode> filtered = visitor.storedNodes.stream()
						.filter(c -> (c.toSource().equals(((PropertyGet) call.getTarget()).getLeft().toSource())))
						.filter(c -> (!FunctionCallVisitor.isBoundedContextCall(c.getParent())))
						.filter(c -> (!FunctionCallVisitor.isBoundedContextCall(c.getParent().getParent())))
						.filter(c -> (!FunctionCallVisitor.isBoundedContextCall(c)))
						.collect(Collectors.toList());

				/*
				 * We try to remove all false positives from the list. If there
				 * is something left, it is a version of the function without
				 * bounded context
				 */
				if (filtered.size() > 0)
					return true;
			}
		}

		return false;
	}

	/*
	 * We are trying to find a object.method(...) equivalent of
	 * object.method.call(this, ...)
	 */
	private boolean isUnboundedVersionOfCallOnList(FunctionCall call, List<FunctionCall> normalCalls) {
		FunctionCall unbounded = convertBoundedCallToUnbouded(call);

		for (FunctionCall normalCall : normalCalls) {
			if (normalCall.toSource().equals(unbounded.toSource())) {
				return true;
			}
		}

		return false;
	}

	/*
	 * Remove .call, .bind or .apply and first argument from a function
	 */
	private FunctionCall convertBoundedCallToUnbouded(FunctionCall bounded) {
		FunctionCall unbounded = new FunctionCall();

		unbounded.setTarget(((PropertyGet) bounded.getTarget()).getLeft());

		try {
			unbounded.setArguments(bounded.getArguments().subList(1, bounded.getArguments().size()));
		} catch (IllegalArgumentException e) {
			unbounded.setArguments(bounded.getArguments());
		}

		return unbounded;
	}
}
