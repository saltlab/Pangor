package ca.ubc.ece.salt.pangor.analysis.callbackerror;

import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.FunctionNode;

import ca.ubc.ece.salt.pangor.analysis.classify.ClassifierDataSet;
import ca.ubc.ece.salt.pangor.analysis.meta.MetaAnalysis;
import ca.ubc.ece.salt.pangor.batch.AnalysisMetaInformation;
import ca.ubc.ece.salt.pangor.classify.alert.CallbackErrorAlert;
import ca.ubc.ece.salt.pangor.classify.alert.ClassifierAlert;
import ca.ubc.ece.salt.pangor.js.analysis.AnalysisUtilities;

/**
 * Checker for finding insertion of callback calls, passing an error arguments
 * on functions that did not have such a call before
 */
public class CallbackErrorAnalysis extends
		MetaAnalysis<ClassifierAlert, ClassifierDataSet, CallbackErrorScopeAnalysis, CallbackErrorScopeAnalysis> {

	public CallbackErrorAnalysis(ClassifierDataSet dataSet, AnalysisMetaInformation ami) {
		super(dataSet, ami, new CallbackErrorScopeAnalysis(dataSet, ami), new CallbackErrorScopeAnalysis(dataSet, ami));
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
		 * We have two approaches:
		 *
		 * 1) If we see a call passing "err" or "error", that's good. We just
		 * have to check if we didn't already have such call on the enclosing
		 * function, as this pattern is about inserting propagating errors to
		 * callbacks on functions that no errors has been propagated before
		 *
		 * 2) If we see a callback call with just an argument, that can either
		 * be a call passing an error, or a callback that does not follow the
		 * convention of having first argument as error. That's very common on
		 * jQuery context. So for those cases, we check if there was any other
		 * callback(null, ...) on the enclosing mapped function
		 */

		for (FunctionCall call : dstAnalysis.visitor.callsPassingError) {
			/*
			 * Visit mapped function
			 */
			CallbackCallVisitor mappedVisitor = visitMappedFunction(call.getEnclosingFunction());

			if (mappedVisitor == null)
				continue;

			/*
			 * We check if the function didn't already had a callback call
			 * passing error
			 */
			if (mappedVisitor.callsPassingError.size() > 0)
				continue;

			this.registerAlert(
					new CallbackErrorAlert(ami, AnalysisUtilities.getFunctionName(call.getEnclosingFunction()),
							AnalysisUtilities.getFunctionCallName(call)));
		}

		for (FunctionCall call : dstAnalysis.visitor.callsWithOneParameter) {
			/*
			 * Visit mapped function
			 */
			CallbackCallVisitor mappedVisitor = visitMappedFunction(call.getEnclosingFunction());

			if (mappedVisitor == null)
				continue;

			/*
			 * We check if the function didn't already had a callback call
			 * passing error as cb('error');
			 */
			if (mappedVisitor.callsWithOneParameter.size() > 0)
				continue;

			/*
			 * We check if the function didn't already had a callback call
			 * passing error as cb(err);
			 */
			if (mappedVisitor.callsPassingError.size() > 0)
				continue;

			/*
			 * To remove false positives, we check if it had a callback call
			 * passing null on error. Callbacks on jQuery context doesn't always
			 * follow the error as first argument convention, so we must see if
			 * this is the case
			 */
			if (mappedVisitor.callsPassingNullError.size() == 0)
				continue;

			this.registerAlert(
					new CallbackErrorAlert(ami, AnalysisUtilities.getFunctionName(call.getEnclosingFunction()),
							AnalysisUtilities.getFunctionCallName(call)));
		}

		return;
	}

	private CallbackCallVisitor visitMappedFunction(FunctionNode function) {
		FunctionNode mappedFunction = (FunctionNode) function.getMapping();

		if (mappedFunction == null)
			return null;

		CallbackCallVisitor mappedVisitor = new CallbackCallVisitor();
		mappedFunction.visit(mappedVisitor);

		return mappedVisitor;
	}
}