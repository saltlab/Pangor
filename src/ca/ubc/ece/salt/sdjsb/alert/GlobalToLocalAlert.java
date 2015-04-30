package ca.ubc.ece.salt.sdjsb.alert;


public class GlobalToLocalAlert extends Alert {
	
	private String identifier;
	
	public GlobalToLocalAlert(String type, String identifier) {
		super(type, "GLOBAL_TO_LOCAL");
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
