package ca.ubc.ece.salt.sdjsb.analysis.prediction;

import java.util.List;
import java.util.Map;

import ca.ubc.ece.salt.sdjsb.analysis.learning.apis.AbstractAPI;
import ca.ubc.ece.salt.sdjsb.analysis.learning.apis.Keyword;
import ca.ubc.ece.salt.sdjsb.analysis.learning.apis.Keyword.KeywordType;
import ca.ubc.ece.salt.sdjsb.analysis.learning.apis.TopLevelAPI;

/**
 * Predicts the points-to relationships for all keywords (methods, fields,
 * events, etc.) based on the use patterns of all the keywords.
 */
public class PointsToPrediction {
	/**
	 * The likelihood threshold used to assume the prediction was correct.
	 * TODO: Should this actually be a modifiable field?
	 */
	protected final double LIKELIHOOD_THRESHOLD = 0;

	/**
	 * The Predictor used in the predictions
	 */
	protected Predictor predictor;

	/**
	 * Build the model for predicting points-to relationships.
	 * @param insertedKeywords
	 * @param removedKeywords
	 * @param updatedKeywords
	 * @param unchangedKeywords
	 */
	public PointsToPrediction(TopLevelAPI api, Map<Keyword, Integer> insertedKeywords,
			   Map<Keyword, Integer> removedKeywords,
			   Map<Keyword, Integer> updatedKeywords,
			   Map<Keyword, Integer> unchangedKeywords) {
		this.predictor = new CSPredictor(api, insertedKeywords, removedKeywords, updatedKeywords, unchangedKeywords);
	}

	/**
	 * Try to predict to which API this keyword belongs to. If the prediction is
	 * above LIKELIHOOD_THRESHOLD, the most likely API is stored in the
	 * Keyword's api field and true is returned. Otherwise, false is returned.
	 *
	 * @param keyword the keyword used in the prediction
	 * @return true if prediction is above confidence level and api is stored in
	 *         keyword
	 **/
	public Keyword getKeyword(KeywordType context, String token) {
		/*
		 * Create the keyword object
		 */
		Keyword keyword = new Keyword(context, token);

		/*
		 * Use the predictor and get the result with the highest likelihood of
		 * being the correct one (stored on the head of PredictionResults queue)
		 */
		PredictionResults results = predictor.predictKeyword(keyword);
		PredictionResult result = results.poll();

		if (result != null && result.likelihood > LIKELIHOOD_THRESHOLD) {
			keyword.api = result.api;
			return keyword;
		}

		return null;
	}


	/** Returns a list of APIs that are likely used in this method. **/
	public List<AbstractAPI> getAPIsUsed(Map<Keyword, Integer> insertedKeywords,
			   Map<Keyword, Integer> removedKeywords,
			   Map<Keyword, Integer> updatedKeywords,
			   Map<Keyword, Integer> unchangedKeywords) {
		return null;
	}

	/**
	 * Returns a list of APIs that are likely involved in a method's repair.
	 **/
	public List<AbstractAPI> getAPIsInRepair(Map<Keyword, Integer> insertedKeywords,
			   Map<Keyword, Integer> removedKeywords,
			   Map<Keyword, Integer> updatedKeywords,
			   Map<Keyword, Integer> unchangedKeywords) {
		return null;
	}

}