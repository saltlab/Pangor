package ca.ubc.ece.salt.pangor.classify.alert;

import ca.ubc.ece.salt.pangor.batch.AnalysisMetaInformation;
import ca.ubc.ece.salt.pangor.js.analysis.SpecialTypeAnalysisUtilities.SpecialType;

public class SpecialTypeAlert extends ClassifierAlert {

	private String variableIdentifier;
	private SpecialType specialType;

	public SpecialTypeAlert(AnalysisMetaInformation ami, String functionName, String type, String variableIdentifier, SpecialType specialType) {
		super(ami, functionName, type, "TYPE_ERROR_" + specialType.toString());
		this.variableIdentifier = variableIdentifier;
		this.specialType = specialType;
	}

	@Override
	public String getAlertDescription() {
		return "A TypeError or incorrect output was repaired by adding a " + this.specialType + " check for '" + this.variableIdentifier + "'.";
	}

	@Override
	public String getAlertExplanation() {
		return "A conditional branch was inserted that checks if a variable has a special type. This could indicate a TypeError is possible in the original code or that incorrect output is displayed to the user.";
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof SpecialTypeAlert && super.equals(o)) {
			SpecialTypeAlert sta = (SpecialTypeAlert) o;
			return this.variableIdentifier.equals(sta.variableIdentifier) && this.specialType == sta.specialType;
		}
		return false;
	}

}
