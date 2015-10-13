package ca.ubc.ece.salt.pangor.analysis.callbackerror;

import java.util.ArrayList;
import java.util.List;

import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.KeywordLiteral;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.NodeVisitor;

public class CallbackCallVisitor implements NodeVisitor {
	public List<FunctionCall> callsPassingError = new ArrayList<>();
	public List<FunctionCall> callsWithOneParameter = new ArrayList<>();
	public List<FunctionCall> callsPassingNullError = new ArrayList<>();
	public List<FunctionCall> callsNotPassingError = new ArrayList<>();

	@Override
	public boolean visit(AstNode node) {
		/* If this is not a function call, ignore */
		if (!(node instanceof FunctionCall))
			return true;

		FunctionCall call = (FunctionCall) node;

		if (! isCallbackCall(call))
			return true;

		if (hasErrorArgument(call)) {
			callsPassingError.add(call);
		} else if (hasOnlyOneArgument(call)) {
			callsWithOneParameter.add(call);
		} else if (hasFirstArgumentNull(call)) {
			callsPassingNullError.add(call);
		} else {
			callsNotPassingError.add(call);
		}

		return true;
	}

	/**
	 * Look for callback(null, ...) calls
	 */
	private static boolean hasFirstArgumentNull(FunctionCall call) {
		if (call.getArguments().size() < 2) {
			return false;
		}

		AstNode firstArgument = call.getArguments().get(0);

		if (firstArgument instanceof KeywordLiteral) {
			return (((KeywordLiteral) firstArgument).getType() == Token.NULL);
		}

		return false;
	}

	public static boolean isCallbackCall(FunctionCall call) {
		if (!(call.getTarget() instanceof Name))
			return false;

		Name target = (Name) call.getTarget();

		// Anonymous call
		if (target.getIdentifier() == null)
			return false;

		if (target.getIdentifier().matches("(cb|callback|cback)"))
			return true;

		return false;
	}

	/**
	 * It has an error argument if it has at least one argument and it is not
	 * null
	 */
	public static boolean hasErrorArgument(FunctionCall call) {
		if (call.getArguments().size() == 0)
			return false;

		AstNode firstArgument = call.getArguments().get(0);

		if (firstArgument instanceof Name) {
			Name name = (Name) firstArgument;

			if (name.getIdentifier().matches("(?i)e(rr(or)?)?")) {
				return true;
			}
		}

		return false;
	}

	private static boolean hasOnlyOneArgument(FunctionCall call) {
		return (call.getArguments().size() == 1);
	}

}
