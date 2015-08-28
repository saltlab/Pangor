package ca.ubc.ece.salt.sdjsb.classify.alert;

import ca.ubc.ece.salt.sdjsb.batch.AnalysisMetaInformation;

public class ThisToThatAlert extends ClassifierAlert {

	public ThisToThatAlert(AnalysisMetaInformation ami, String functionName, String subtype) {
		super(ami, functionName, "THISTOTHAT", subtype);
	}

	@Override
	public String getAlertDescription() {
		return "The function '" + this.functionName + "' replaced an occurance of 'this' with '" + subtype + "'.";
	}

	@Override
	public String getAlertExplanation() {
		return "The function may have been using 'this' with an undesired context. The repair is storing the right context in a variable (like 'that') and passing it to this function.";
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof ThisToThatAlert && super.equals(o)) {
			ThisToThatAlert aoa = (ThisToThatAlert) o;
			return (this.functionName.equals(aoa.functionName) && this.subtype.equals(aoa.subtype));
		}
		return false;
	}

}
