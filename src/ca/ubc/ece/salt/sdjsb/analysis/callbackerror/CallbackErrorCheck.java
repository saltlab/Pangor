package ca.ubc.ece.salt.sdjsb.analysis.callbackerror;

import ca.ubc.ece.salt.sdjsb.alert.SpecialTypeAlert.SpecialType;
import ca.ubc.ece.salt.sdjsb.analysis.scope.Scope;

/**
 * Stores a parameter that was unchanged and had a special type check
 * inserted.
 */
public class CallbackErrorCheck {
	public Scope scope;
	public String functionName;
	public String functionSignature;
	public String identifier;
	public SpecialType type;
	
	public CallbackErrorCheck(Scope scope, String functionName, String functionSignature, String identifier, SpecialType type) {
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
		
		if(this.scope.scope == cec.scope.scope && this.identifier.equals(cec.identifier)) return true;
		
		return false;
		
	}
	
	@Override
	public int hashCode() {
		return this.scope.scope.hashCode();
	}
}