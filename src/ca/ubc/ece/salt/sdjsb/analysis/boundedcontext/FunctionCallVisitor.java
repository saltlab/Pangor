package ca.ubc.ece.salt.sdjsb.analysis.boundedcontext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.NodeVisitor;
import org.mozilla.javascript.ast.PropertyGet;

/**
 * Visitor for storing all function calls and all bounded context function calls
 * (calls with .call(), .apply() or .bind())
 */
public class FunctionCallVisitor implements NodeVisitor {
	public List<FunctionCall> normalCalls = new ArrayList<>();
	public List<FunctionCall> boundedContextCalls = new ArrayList<>();

	private static final Set<String> BOUNDED_FUNCTION_CALLS_KEYWORDS = new HashSet<>(
			Arrays.asList("call", "apply", "bind"));

	@Override
	public boolean visit(AstNode node) {
		/* If this is not a function call, ignore */
		if (! (node instanceof FunctionCall))
			return true;

		FunctionCall call = (FunctionCall) node;

		/* Is this a normal function call, or a bounded context call? */
		if (isBoundedContextCall(call)) {
			boundedContextCalls.add(call);
		} else {
			normalCalls.add(call);
		}

		/*
		 * We always return true because even if we find a FunctionCall, there
		 * may still be other FunctionCalls withing it (eg. on argument)
		 */
		return true;
	}

	/**
	 * Accepts node instead of call because it is reused by other more generic
	 * methods
	 */
	public static boolean isBoundedContextCall(AstNode node) {
		if (!(node instanceof FunctionCall))
			return false;

		FunctionCall call = (FunctionCall) node;

		/*
		 * Is call in the form of object.method? It must be, since we are
		 * looking for something.call(), .apply() or .bind()
		 */
		if (call.getTarget() instanceof PropertyGet) {
			PropertyGet target = (PropertyGet) call.getTarget();

			/* Where method is "call"? */
			if (target.getRight() instanceof Name
					&& (BOUNDED_FUNCTION_CALLS_KEYWORDS.contains(((Name) target.getRight()).getIdentifier()))) {
				return true;
			}
		}

		return false;
	}
}
