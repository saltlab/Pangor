package ca.ubc.ece.salt.pangor.classify.alert;

import ca.ubc.ece.salt.pangor.batch.AnalysisMetaInformation;

public class ArgumentOrderAlert extends ClassifierAlert {

	public ArgumentOrderAlert(AnalysisMetaInformation ami, String functionName, String subtype) {
		super(ami, functionName, "ARGUMENTORDER", subtype);
	}

	@Override
	public String getAlertDescription() {
		return "The function call '" + this.functionName + "' checks for the argument order.";
	}

	@Override
	public String getAlertExplanation() {
		return "The function may accept different parameters signatures but this was not checked. The repair is inserting an if condition and changing the order we parse the parameters.";
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof ArgumentOrderAlert && super.equals(o)) {
			ArgumentOrderAlert aoa = (ArgumentOrderAlert) o;
			return (this.functionName.equals(aoa.functionName) && this.subtype.equals(aoa.subtype));
		}
		return false;
	}

}
