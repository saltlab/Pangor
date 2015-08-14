package ca.ubc.ece.salt.sdjsb.classify.alert;

import ca.ubc.ece.salt.sdjsb.batch.AnalysisMetaInformation;


public class CallbackParameterAlert extends ClassifierAlert {

	private String function;
	private String signature;
	private String parameter;

	public CallbackParameterAlert(AnalysisMetaInformation ami, String functionName, String type, String function, String signature, String parameter) {
		super(ami, functionName, type, "MISSING_ERROR_PARAMETER");
		this.function = function == null? "[anonymous]" : function;
		this.signature = signature;
		this.parameter = parameter;
	}

	@Override
	public String getAlertDescription() {
		return "An unhandled callback error was repaired by adding an error parameter '" + this.parameter + "' to function '" + this.function + this.signature + "'.";
	}

	@Override
	public String getAlertExplanation() {
		return "A parameter containing an exception was added to a callback function. This could mean an error returned by the callee through the callback was not being handled.";
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof CallbackParameterAlert && super.equals(o)) {
			CallbackParameterAlert sta = (CallbackParameterAlert) o;
			return this.function.equals(sta.function) && this.signature.equals(sta.signature) && this.parameter.equals(sta.parameter);
		}
		return false;
	}

}
