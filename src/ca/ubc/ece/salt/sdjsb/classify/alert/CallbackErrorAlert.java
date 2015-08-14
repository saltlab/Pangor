package ca.ubc.ece.salt.sdjsb.classify.alert;

import ca.ubc.ece.salt.sdjsb.batch.AnalysisMetaInformation;


public class CallbackErrorAlert extends ClassifierAlert {

	private String function;
	private String signature;
	private String parameter;

	public CallbackErrorAlert(AnalysisMetaInformation ami, String functionName, String type, String function, String signature, String parameter) {
		super(ami, functionName, type, "UNCHECKED_ERROR_PARAMETER");
		this.function = function.isEmpty() ? "[anonymous]" : function;
		this.signature = signature;
		this.parameter = parameter;
	}

	@Override
	public String getAlertDescription() {
		return "An unhandled callback error was repaired by checking the error parameter '" + this.parameter + "' in function '" + this.function + this.signature + "'.";
	}

	@Override
	public String getAlertExplanation() {
		return "A parameter containing an exception was checked in a callback function where it was previously unchecked.";
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof CallbackErrorAlert && super.equals(o)) {
			CallbackErrorAlert sta = (CallbackErrorAlert) o;
			return this.function.equals(sta.function) && this.signature.equals(sta.signature) && this.parameter.equals(sta.parameter);
		}
		return false;
	}

}
