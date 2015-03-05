package ca.ubc.ece.salt.sdjsb.checker.alert;

import ca.ubc.ece.salt.sdjsb.checker.AbstractChecker;


/**
 * Stores an alert that notifies the user about a repair that has been found
 * by a checker.
 * 
 * @author qhanam
 */
public abstract class Alert {
	
	private AbstractChecker checker;
	private String subtype;
	
	/**
	 * An alert is always associated a concrete Checker.
	 * @param checker The checker which generated the alert.
	 * @param subtype A checker may detect more than one repair subtype.
	 */
	public Alert(AbstractChecker checker, String subtype) {
		this.checker = checker;
		this.subtype = subtype;
	}
	
	/**
	 * A short description of the repair alert.
	 * @return The alert type and subtype.
	 */
	public String getShortDescription() {
		return checker.getCheckerType() + "_" + this.subtype;
	}
	
	/**
	 * The long description of the repair alert including the identifier (type
	 * and subtype) and specific details about the alert (e.g. variable names,
	 * locations, etc.).
	 * @return The repair type, subtype and description.
	 */
	public String getLongDescription() {
		return checker.getCheckerType() + "_" + this.subtype + ": " + this.getAlertDescription();
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
	public String toString() {
		return checker.getCheckerType() + "_" + this.subtype;
	}

}
