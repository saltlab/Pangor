package ca.ubc.ece.salt.sdjsb.alert;

public class SpecialTypeAlert extends Alert {
	
	private String variableIdentifier;
	private SpecialType specialType;
	
	public SpecialTypeAlert(String type, String variableIdentifier, SpecialType specialType) {
		super(type, "TYPE_ERROR_" + specialType.toString());
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

	@Override
	public boolean equals(Object o) {
		if(o instanceof SpecialTypeAlert && super.equals(o)) {
			SpecialTypeAlert sta = (SpecialTypeAlert) o;
			return this.variableIdentifier.equals(sta.variableIdentifier) && this.specialType == sta.specialType;
		}
		return false;
	}

	/**
	 * The list of special types that a variable could be assigned to. Note
	 * that the FALSEY type indicates that a variable could be one of 
	 * {undefined, NaN, blank, zero} (i.e. the variable evaluates to false in
	 * a condition expression).
	 * 
	 * @author qhanam
	 */
	public enum SpecialType {
		FALSEY,
		UNDEFINED,
		NULL,
		NAN,
		BLANK,
		ZERO
	}

}
