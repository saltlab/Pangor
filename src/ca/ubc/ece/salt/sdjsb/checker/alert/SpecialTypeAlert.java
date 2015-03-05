package ca.ubc.ece.salt.sdjsb.checker.alert;

import ca.ubc.ece.salt.sdjsb.checker.AbstractChecker;
import ca.ubc.ece.salt.sdjsb.checker.SpecialTypeMap.SpecialType;

public class SpecialTypeAlert extends Alert {
	
	private String variableIdentifier;
	private SpecialType specialType;
	
	public SpecialTypeAlert(AbstractChecker checker, String variableIdentifier, SpecialType specialType) {
		super(checker, "TYPE_ERROR_" + specialType.toString());
		this.variableIdentifier = variableIdentifier;
		this.specialType = specialType;
	}

	@Override
	public String getAlertDescription() {
		return "A possible TypeError was repaired by adding a " + this.specialType + " check for '" + this.variableIdentifier + "'.";
	}

	@Override
	public String getAlertExplanation() {
		return "A conditional branch was inserted that checks if a variable has a special type. This could indicate a TypeError is possible in the original code.";
	}

}
