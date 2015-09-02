package ca.ubc.ece.salt.pangor.classify.alert;

import ca.ubc.ece.salt.pangor.batch.AnalysisMetaInformation;

/**
 * Alert to notify changes on the bounded context of a function call through the
 * use of .call(), .apply() or .bind(). It stores the functionName and type is
 * being used to mark whether call, apply or bind was used.
 */
public class BoundedContextAlert extends ClassifierAlert {

	public BoundedContextAlert(AnalysisMetaInformation ami, String functionName, String subtype) {
		super(ami, functionName, "BOUNDEDCONTEXT", subtype);
	}

	@Override
	public String getAlertDescription() {
		return "The function call '" + this.functionName + "' has a new bounded context.";
	}

	@Override
	public String getAlertExplanation() {
		return "The function was being called with a undesired bounded context. The repair is using call() apply() or bind() to pass the desired context.";
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof BoundedContextAlert && super.equals(o)) {
			BoundedContextAlert wbca = (BoundedContextAlert) o;
			return (this.functionName.equals(wbca.functionName) && this.subtype.equals(wbca.subtype));
		}
		return false;
	}

}
