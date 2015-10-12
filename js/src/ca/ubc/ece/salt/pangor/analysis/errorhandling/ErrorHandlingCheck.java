package ca.ubc.ece.salt.pangor.analysis.errorhandling;

import java.util.List;

import ca.ubc.ece.salt.pangor.js.analysis.scope.Scope;

/**
 * Stores the information from a potential exception handling repair.
 */
public class ErrorHandlingCheck {

	public Scope scope;
	public List<String> callTargetIdentifiers;

	public ErrorHandlingCheck(Scope scope, List<String> callTargetIdentifiers) {
		this.scope = scope;
		this.callTargetIdentifiers = callTargetIdentifiers;
	}

}