package ca.ubc.ece.salt.pangor.classify.alert;

import ca.ubc.ece.salt.pangor.batch.AnalysisMetaInformation;
import ca.ubc.ece.salt.pangor.js.analysis.SpecialTypeAnalysisUtilities;
import ca.ubc.ece.salt.pangor.js.analysis.SpecialTypeAnalysisUtilities.SpecialType;

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
		if(SpecialTypeAnalysisUtilities.isStronger(this.oldSpecialType, this.newSpecialType)) {
			return "An incorrect condition was repaired by strenthening a " + this.oldSpecialType + " check to a " + this.newSpecialType + " check for '" + this.variableIdentifier + "'.";
		}
		else {
			return "An incorrect condition was repaired by weakening a " + this.oldSpecialType + " check to a " + this.newSpecialType + " check for '" + this.variableIdentifier + "'.";
		}
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
