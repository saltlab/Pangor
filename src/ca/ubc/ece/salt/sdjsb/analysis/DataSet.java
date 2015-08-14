package ca.ubc.ece.salt.sdjsb.analysis;


/**
 * The {@code DataSet} manages the alerts that were generated during an analysis.
 * @param <T> The type of alerts that the data set stores.
 */
public interface DataSet<T> {

	/**
	 * Adds an alert to the data set.
	 * @param alert The alert store in the data set.
	 */
	void registerAlert(T alert) throws Exception;

}
