package ca.ubc.ece.salt.pangor.classify.alert;

import ca.ubc.ece.salt.pangor.batch.AnalysisMetaInformation;

public class ErrorHandlingAlert extends ClassifierAlert {

	public ErrorHandlingAlert(AnalysisMetaInformation ami, String functionName, String type) {
		super(ami, functionName, type, "PROTECTED_WITH_TRY");
	}

	@Override
	public String getAlertDescription() {
		return "An uncaught error was caught with a try/catch statement.";
	}

	@Override
	public String getAlertExplanation() {
		return "An error was previously unhandled in the function, possibly causing the program to crash. The exception throwing statement was placed in a try/catch block.";
	}

}
