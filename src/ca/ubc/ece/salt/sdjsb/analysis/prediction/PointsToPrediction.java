package ca.ubc.ece.salt.sdjsb.analysis.prediction;

import java.util.List;
import java.util.Map;

import org.mozilla.javascript.ast.AstRoot;

import ca.ubc.ece.salt.sdjsb.analysis.learning.apis.AbstractAPI;
import ca.ubc.ece.salt.sdjsb.analysis.learning.apis.Keyword;
import ca.ubc.ece.salt.sdjsb.analysis.learning.apis.TopLevelAPI;

/**
 * Predicts the points-to relationships for all keywords (methods, fields,
 * events, etc.) based on the use patterns of all the keywords.
 */
public class PointsToPrediction {
	/** The top level API where API's are looked for */
	protected TopLevelAPI api;

	/**
	 * The likelihood threshold used to assume the prediction was correct.
	 * TODO: Should this actually be a modifiable field?
	 */
	protected final double LIKELIHOOD_THRESHOLD = 0.75;

	/**
	 * Build the model for predicting points-to relationships.
	 */
	public PointsToPrediction(TopLevelAPI api, Map<Keyword, Integer> insertedKeywords,
			   Map<Keyword, Integer> removedKeywords,
			   Map<Keyword, Integer> updatedKeywords,
			   Map<Keyword, Integer> unchangedKeywords) {
		this.api = api;

		// calculate all the predictions here
	}

	/**
	 * Try to predict to which API this keyword belongs to. If the prediction is
	 * above LIKELIHOOD_THRESHOLD, the likely API is stored in the Keyword's api
	 * field and true is returned. Otherwise, false is returned.
	 *
	 * @param keyword the keyword used in the prediction
	 * @return true if prediction is above confidence level and api is stored in
	 *         keyword
	 **/
	public boolean findLikelyAPI(Keyword keyword) {
		PredictionResults results = predict(keyword);
		PredictionResult result = results.poll();

		if (result != null && result.likelihood > LIKELIHOOD_THRESHOLD) {
			keyword.api = result.api;
			return true;
		} else {
			return false;
		}
	}

	/** Returns a list of APIs that are likely used in this method. **/
	public List<AbstractAPI> getAPIsUsed(AstRoot methodRoot) {
		return null;
	}

	/**
	 * Returns a list of APIs that are likely involved in a method's repair.
	 **/
	public List<AbstractAPI> getAPIsInRepair(AstRoot methodRoot) {
		return null;
	}

	protected PredictionResults predict(Keyword keyword) {
		PredictionResults results = new PredictionResults();

		/*
		 * This is where the actual prediction takes place
		 */
		Keyword keywordFound = api.getFirstKeyword(keyword);

		if (keywordFound != null) {
			PredictionResult result = new PredictionResult(keywordFound.api, 1);
			results.add(result);
		}

		return results;
	}

}