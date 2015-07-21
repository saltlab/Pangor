package ca.ubc.ece.salt.sdjsb.batch;

/**
 * Stores and prints a data set for an analysis.
 */
public interface DataSet {

	/**
	 * Performs any pre-processing tasks needed for the data set.
	 */
	void preProcess();
	
	/**
	 * @return The header for the data set;
	 */
	String getHeader();
	
	/**
	 * @return The entire data set as a CSV or TSV file.
	 */
	String getDataSet();

}
