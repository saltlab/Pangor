package ca.ubc.ece.salt.pangor.analysis.callbackerrorhandling;

import org.mozilla.javascript.ast.AstNode;

import ca.ubc.ece.salt.pangor.analysis.scope.Scope;
import ca.ubc.ece.salt.pangor.classify.alert.SpecialTypeAlert.SpecialType;

/**
 * Stores a parameter that was unchanged and had a special type check
 * inserted.
 */
public class CallbackErrorCheck {
	public Scope<AstNode> scope;
	public String functionName;
	public String functionSignature;
	public String identifier;
	public SpecialType type;

	public CallbackErrorCheck(Scope<AstNode> scope, String functionName, String functionSignature, String identifier, SpecialType type) {
		this.scope = scope;
		this.functionName = functionName;
		this.functionSignature = functionSignature;
		this.identifier = identifier;
		this.type = type;
	}

	@Override
	public boolean equals(Object o) {

		if(!(o instanceof CallbackErrorCheck)) return false;

		CallbackErrorCheck cec = (CallbackErrorCheck) o;

		if(this.scope.getScope() == cec.scope.getScope() && this.identifier.equals(cec.identifier)) return true;

		return false;

	}

	@Override
	public int hashCode() {
		return this.scope.getScope().hashCode();
	}
}