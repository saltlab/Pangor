package ca.ubc.ece.salt.sdjsb.classify.alert;

import ca.ubc.ece.salt.sdjsb.analysis.Alert;
import ca.ubc.ece.salt.sdjsb.batch.AnalysisMetaInformation;


/**
 * Stores an alert that notifies the user about a repair that has been found
 * by a checker.
 *
 * The alert includes meta information. The meta information (e.g., project,
 * commit #, file names, etc.) is used to investigate classification.
 */
public abstract class ClassifierAlert extends Alert {

	/** The alert type. **/
	private String type;

	/** The alert subtype. **/
	private String subtype;

	/**
	 * An alert is always associated a concrete Checker.
	 * @param checker The checker which generated the alert.
	 * @param subtype A checker may detect more than one repair subtype.
	 */
	public ClassifierAlert(AnalysisMetaInformation ami, String functionName, String type, String subtype) {
		super(ami, functionName);
		this.type = type;
		this.subtype = subtype;
	}

	/**
	 * This method serializes the alert. This is useful when writing
	 * a data set to the disk.
	 * @return The serialized version of the alert.
	 */
	public String serialize() {

		String serialized = id + "," + this.ami.projectID + "," + this.ami.projectHomepage
				+ "," + this.ami.buggyFile + "," + this.ami.repairedFile
				+ "," + this.ami.buggyCommitID + "," + this.ami.repairedCommitID
				+ "," + this.functionName + "," + this.type + "," + this.subtype
				+ "," + this.getLongDescription() + "," + this.getAlertExplanation();

		return serialized;

	}

	/**
	 * @return A unique identifier for the alert.
	 */
	public String getID() {
		throw new UnsupportedOperationException();
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
	 * A custom description specified by the concrete alert.
	 */
	public String getFeatureVector(String uri, String sourceFile, String destinationFile, String buggyCommit, String repairedCommit) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @return The source code for the alert.
	 */
	public String getSourceCode() {
		throw new UnsupportedOperationException();
	}

	/**
	 * @return The destination code for the alert.
	 */
	public String getDestinationCode() {
		throw new UnsupportedOperationException();
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
		if(o instanceof ClassifierAlert) {
			ClassifierAlert a = (ClassifierAlert) o;
			return this.type.equals(a.type) && this.subtype.equals(a.subtype);
		}
		return false;
	}

	@Override
	public String toString() {
		return this.type + "_" + this.subtype;
	}

	@Override
	public int hashCode() {
		return this.getLongDescription().hashCode();
	}

}
