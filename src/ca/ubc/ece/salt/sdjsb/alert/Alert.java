package ca.ubc.ece.salt.sdjsb.alert;

/**
 * Stores an alert that notifies the user about a repair that has been found
 * by a checker.
 * 
 * @author qhanam
 */
public abstract class Alert {
	
	private String type;
	private String subtype;
	
	/**
	 * An alert is always associated a concrete Checker.
	 * @param checker The checker which generated the alert.
	 * @param subtype A checker may detect more than one repair subtype.
	 */
	public Alert(String type, String subtype) {
		this.type = type;
		this.subtype = subtype;
	}
	
	/**
	 * The type of the alert.
	 * @return
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * The subtype of the alert.
	 * @return
	 */
	public String getSubType() {
		return this.subtype;
	}
	
	/**
	 * The type/subtype identifier.
	 * @return
	 */
	public String getIdentifier() {
		return this.type + "_" + this.subtype;
	}
	
	/**
	 * A short description of the repair alert.
	 * @return The alert type and subtype.
	 */
	public String getShortDescription() {
		return this.type + "_" + this.subtype;
	}
	
	/**
	 * The long description of the repair alert including the identifier (type
	 * and subtype) and specific details about the alert (e.g. variable names,
	 * locations, etc.).
	 * @return The repair type, subtype and description.
	 */
	public String getLongDescription() {
		return this.type + "_" + this.subtype + ": " + this.getAlertDescription();
	}
	
	/**
	 * Returns specific details about the alert (e.g., variable names,
	 * locations, etc.).
	 * @return The long description of the alert.
	 */
	protected abstract String getAlertDescription();
	
	/** 
	 * Returns a description of the repair detected by the checker.
	 */
	protected abstract String getAlertExplanation();
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof Alert) {
			Alert a = (Alert) o;
			return this.type.equals(a.type) && this.subtype.equals(a.subtype);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return this.type + "_" + this.subtype;
	}

}
