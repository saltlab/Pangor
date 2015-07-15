package ca.ubc.ece.salt.sdjsb.analysis.prediction;

import java.util.Map;

import ca.ubc.ece.salt.sdjsb.analysis.learning.apis.Keyword;
import ca.ubc.ece.salt.sdjsb.analysis.learning.apis.TopLevelAPI;

/**
 * Abstract class to model a Predictor
 */
public abstract class Predictor {
	/** The top level API where API's are looked for */
	protected TopLevelAPI api;

	protected Map<Keyword, Integer> insertedKeywords;
	protected Map<Keyword, Integer> removedKeywords;
	protected Map<Keyword, Integer> updatedKeywords;
	protected Map<Keyword, Integer> unchangedKeywords;

	public Predictor(TopLevelAPI api, Map<Keyword, Integer> insertedKeywords, Map<Keyword, Integer> removedKeywords,
			Map<Keyword, Integer> updatedKeywords, Map<Keyword, Integer> unchangedKeywords) {
		this.api = api;
		this.insertedKeywords = insertedKeywords;
		this.removedKeywords = removedKeywords;
		this.updatedKeywords = updatedKeywords;
		this.unchangedKeywords = unchangedKeywords;

		// TODO: Look on input for PACKAGEs keywords. Those are the packages
		// there were actually imported on the file and will serve as a filter
		// on our predictions
	}

	public abstract PredictionResults predictKeyword(Keyword keyword);
}
