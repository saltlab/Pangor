package ca.ubc.ece.salt.sdjsb.analysis.errorhandling;

import ca.ubc.ece.salt.sdjsb.analysis.scope.Scope;

/**
 * Stores the information from a potential exception handling repair.
 */
public class ErrorHandlingCheck {
	public Scope scope;
	public String functionName;

	public ErrorHandlingCheck(Scope scope, String functionName) {
		this.scope = scope;
		this.functionName = functionName;
	}

	@Override
	public boolean equals(Object o) {

		if(!(o instanceof ErrorHandlingCheck)) return false;

		ErrorHandlingCheck cec = (ErrorHandlingCheck) o;

		/* Check if the scope is the same or has a mapping to the same. The
		 * mapping is needed so we can match source error checks to
		 * destination error checks. */
		if(this.scope.scope == cec.scope.scope ||
				this.scope.scope.getMapping() == cec.scope.scope) return true;

		return false;

	}

	@Override
	public int hashCode() {
		return this.scope.scope.hashCode();
	}
}