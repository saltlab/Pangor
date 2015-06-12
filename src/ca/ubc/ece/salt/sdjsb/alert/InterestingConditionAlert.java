package ca.ubc.ece.salt.sdjsb.alert;

import org.mozilla.javascript.ast.AstNode;

public class InterestingConditionAlert extends Alert {

	/** Unique identifier for the function that contains this path fragment. */
	public String functionID;
	
	/** The interesting condition. **/
	public AstNode interestingCondition;

	public InterestingConditionAlert(String functionID) {
		super("LRN", "INTERESTING_CONDITION");
		this.functionID = functionID;
	}

	@Override
	protected String getAlertDescription() {
		return "Condition statistics (features): TODO";
	}

	@Override
	protected String getAlertExplanation() {
		return "This alert stores an interesting condition for a function.";
	}

}
