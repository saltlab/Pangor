package ca.ubc.ece.salt.sdjsb.checker;


/**
 * Stores an alert that notifies the user about a repair that has been found
 * by a checker.
 * 
 * @author qhanam
 */
public class Alert {
	
	private AbstractChecker checker;
	private String subtype;
	private String description;
	
	/**
	 * An alert is always associated a concrete Checker.
	 * @param checker The checker which generated the alert.
	 * @param subtype A checker may detect more than one repair subtype.
	 * @param description The description of the repair detected by the checker.
	 */
	public Alert(AbstractChecker checker, String subtype, String description) {
		this.checker = checker;
		this.subtype = subtype;
		this.description = description;
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
	 * and subtype) and the alert description.
	 * @return The repair type, subtype and description.
	 */
	public String getLongDescription() {
		return checker.getCheckerType() + "_" + this.subtype + "\n\n" + this.description;
	}
	
	@Override
	public String toString() {
		return checker.getCheckerType() + "_" + this.subtype;
	}

}
