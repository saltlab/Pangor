package ca.ubc.ece.salt.pangor.classify.alert;

import ca.ubc.ece.salt.pangor.batch.AnalysisMetaInformation;


public class GlobalToLocalAlert extends ClassifierAlert {

	private String identifier;

	public GlobalToLocalAlert(AnalysisMetaInformation ami, String functionName, String type, String identifier) {
		super(ami, functionName, type, "GLOBAL_TO_LOCAL");
		this.identifier = identifier;
	}

	@Override
	public String getAlertDescription() {
		return "The global '" + this.identifier + " was re-defined as a local.";
	}

	@Override
	public String getAlertExplanation() {
		return "The variable was defined globally but used locally. The repair defined the variable locally.";
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof GlobalToLocalAlert && super.equals(o)) {
			GlobalToLocalAlert sta = (GlobalToLocalAlert) o;
			return this.identifier.equals(sta.identifier);
		}
		return false;
	}

}
