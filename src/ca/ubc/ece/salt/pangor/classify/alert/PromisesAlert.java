package ca.ubc.ece.salt.pangor.classify.alert;

import ca.ubc.ece.salt.pangor.batch.AnalysisMetaInformation;

/**
 * Indicates that a refactoring occurred where a callback was converted to a promise.
 */
public class PromisesAlert extends ClassifierAlert {

	public PromisesAlert(AnalysisMetaInformation ami, String functionName,
			String type, String subtype) {
		super(ami, functionName, type, subtype);
	}

	@Override
	protected String getAlertDescription() {
		return "A callback was refactored to a promise.";
	}

	@Override
	protected String getAlertExplanation() {
		return "A callback was refactored to a promise.";
	}

}
