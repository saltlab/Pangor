package ca.ubc.ece.salt.pangor.classify.alert;

import ca.ubc.ece.salt.pangor.batch.AnalysisMetaInformation;

/**
 * TODO
 */
public class CallbackErrorAlert extends ClassifierAlert {

	public CallbackErrorAlert(AnalysisMetaInformation ami, String functionName, String subtype) {
		super(ami, functionName, "CALLBACKERROR", subtype);
	}

	@Override
	public String getAlertDescription() {
		return "The function '" + this.functionName + "' did not have any callback call propagating errors.";
	}

	@Override
	public String getAlertExplanation() {
		return "The function had a callback and it never called the callback propagating any error, but now it does.";
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof CallbackErrorAlert && super.equals(o)) {
			CallbackErrorAlert cea = (CallbackErrorAlert) o;
			return (this.functionName.equals(cea.functionName) && this.subtype.equals(cea.subtype));
		}
		return false;
	}

}
