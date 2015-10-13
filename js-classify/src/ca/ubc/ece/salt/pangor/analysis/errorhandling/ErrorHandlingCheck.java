package ca.ubc.ece.salt.pangor.analysis.errorhandling;

import java.util.List;

import org.mozilla.javascript.ast.AstNode;

import ca.ubc.ece.salt.pangor.analysis.scope.Scope;

/**
 * Stores the information from a potential exception handling repair.
 */
public class ErrorHandlingCheck {

	public Scope<AstNode> scope;
	public List<String> callTargetIdentifiers;

	public ErrorHandlingCheck(Scope<AstNode> scope, List<String> callTargetIdentifiers) {
		this.scope = scope;
		this.callTargetIdentifiers = callTargetIdentifiers;
	}

}