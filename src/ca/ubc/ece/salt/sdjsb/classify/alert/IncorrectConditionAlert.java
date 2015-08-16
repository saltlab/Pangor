package ca.ubc.ece.salt.sdjsb.classify.alert;

import ca.ubc.ece.salt.sdjsb.batch.AnalysisMetaInformation;
import ca.ubc.ece.salt.sdjsb.classify.alert.SpecialTypeAlert.SpecialType;

public class IncorrectConditionAlert extends ClassifierAlert {

	private String variableIdentifier;
	private SpecialType oldSpecialType;
	private SpecialType newSpecialType;

	public IncorrectConditionAlert(AnalysisMetaInformation ami, String functionName, String type, String variableIdentifier, SpecialType oldSpecialType, SpecialType newSpecialType) {
		super(ami, functionName, type, oldSpecialType.toString() + "_TO_" + newSpecialType.toString());
		this.variableIdentifier = variableIdentifier;
		this.oldSpecialType = oldSpecialType;
		this.newSpecialType = newSpecialType;
	}

	@Override
	public String getAlertDescription() {
		return "An incorrect condition was repaired by changing a " + this.oldSpecialType + " check to a " + this.newSpecialType + " check for '" + this.variableIdentifier + "'.";
	}

	@Override
	public String getAlertExplanation() {
		return "A condition was strengthened or weakend to capture fewer or more  types.";
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof IncorrectConditionAlert && super.equals(o)) {
			IncorrectConditionAlert sta = (IncorrectConditionAlert) o;
			return this.variableIdentifier.equals(sta.variableIdentifier) && this.oldSpecialType == sta.oldSpecialType && this.newSpecialType == sta.newSpecialType;
		}
		return false;
	}

}
