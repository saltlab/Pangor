package ca.ubc.ece.salt.pangor.analysis;

import ca.ubc.ece.salt.pangor.batch.AnalysisMetaInformation;

/**
 * Stores some information that has been inferred by static analysis.
 */
public abstract class Alert {

	/** A counter to produce unique IDs for each alert. **/
	private static int idCounter;

	/** The unique ID for the alert. **/
	public int id;

	/** The name of the function that was analyzed. **/
	public String functionName;

	/** The meta info for the analysis (project, buggy/repaired files, etc.). */
	public AnalysisMetaInformation ami;

	/**
	 * @param ami The meta information from a bulk analysis.
	 * @param functionName The name of the function that was analyzed.
	 **/
	public Alert(AnalysisMetaInformation ami, String functionName) {
		this.ami = ami;
		this.functionName = functionName;
		this.id = getNextID();
	}

	/**
	 * Used for de-serializing alerts.
	 * @param ami The meta information from a bulk analysis.
	 * @param functionName The name of the function that was analyzed.
	 **/
	public Alert(AnalysisMetaInformation ami, String functionName, int id) {
		this.ami = ami;
		this.functionName = functionName;
		this.id = id;
	}

	/**
	 * Generates unique IDs for alerts. Synchronized because it may be
	 * called by several GitProjectAnalysis threads.
	 *
	 * @return The next unique ID for an alert.
	 */
	private synchronized static int getNextID() {
		idCounter++;
		return idCounter;
	}

}
