package ca.ubc.ece.salt.sdjsb.alert;

import ca.ubc.ece.salt.sdjsb.analysis.learning.PathFragment;

public class PathFragmentAlert extends Alert {
	
	/** Unique identifier for the function that contains this path fragment. */
	public String functionID;
	
	/** The path fragment. **/
	public PathFragment pathFragment;

	public PathFragmentAlert(String functionID) {
		super("Path Fragment", "NA");

		this.functionID = functionID;
	}

	@Override
	protected String getAlertDescription() {
		return "Fragment statistics (features): TODO";
	}

	@Override
	protected String getAlertExplanation() {
		return "This alert stores a path fragment for a function.";
	}

}
