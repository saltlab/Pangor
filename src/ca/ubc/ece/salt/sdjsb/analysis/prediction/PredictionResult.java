package ca.ubc.ece.salt.sdjsb.analysis.prediction;

import ca.ubc.ece.salt.sdjsb.analysis.learning.apis.AbstractAPI;

/**
 * Stores an API and a likelihood calculated by a prediction
 */
public class PredictionResult implements Comparable<PredictionResult> {
	/** The API predicted */
	public AbstractAPI api;

	/** The likelihood calculated by the prediction */
	public double likelihood;

	public PredictionResult(AbstractAPI api, double likelihood) {
		this.api = api;
		this.likelihood = likelihood;
	}

	/**
	 * Override the class's natural ordering so we can order the predictions by
	 * their likelihoods
	 */
	@Override
	public int compareTo(PredictionResult o) {
		if (this.likelihood > o.likelihood)
			return 1;
		else if (this.likelihood < o.likelihood)
			return -1;
		else
			return 0;
	}
}
