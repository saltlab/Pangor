package ca.ubc.ece.salt.sdjsb.alert;


public class NotDefinedAlert extends Alert {
	
	private String identifier;
	
	public NotDefinedAlert(String type, String identifier) {
		super(type, "UNDEFINED_VARIABLE");
		this.identifier = identifier;
	}

	@Override
	public String getAlertDescription() {
		return "A possible undefined variable was repaired by defining '" + this.identifier + ".";
	}

	@Override
	public String getAlertExplanation() {
		return "A variable definition was inserted, but the defined variable was not used in an inserted or updated statement.";
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof NotDefinedAlert && super.equals(o)) {
			NotDefinedAlert sta = (NotDefinedAlert) o;
			return this.identifier.equals(sta.identifier);
		}
		return false;
	}

}
