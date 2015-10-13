package ca.ubc.ece.salt.pangor.learning.pointsto;

import java.util.Map;
import java.util.Set;

import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode.ChangeType;
import ca.ubc.ece.salt.pangor.learning.apis.AbstractAPI;
import ca.ubc.ece.salt.pangor.learning.apis.KeywordDefinition.KeywordType;
import ca.ubc.ece.salt.pangor.learning.apis.KeywordUse;
import ca.ubc.ece.salt.pangor.learning.apis.KeywordUse.KeywordContext;
import ca.ubc.ece.salt.pangor.learning.apis.TopLevelAPI;

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
	 */
	public PointsToPrediction(TopLevelAPI api, Map<KeywordUse, Integer> keywords) {
		this.predictor = new CSPredictor(api, keywords);
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
	public KeywordUse getKeyword(KeywordType type, KeywordContext context,
			String token, ChangeType changeType) {
		/*
		 * Create the keyword object
		 */
		KeywordUse keyword = new KeywordUse(type, context, token, changeType);

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

	public KeywordUse getKeyword(KeywordType type, String token) {
		return getKeyword(type, KeywordContext.UNKNOWN, token, ChangeType.UNKNOWN);
	}


	/** Returns a set of APIs that are likely used in this method. **/
	@SuppressWarnings("unchecked")
	public Set<AbstractAPI> getAPIsUsed(Map<KeywordUse, Integer> insertedKeywords,
			Map<KeywordUse, Integer> removedKeywords, Map<KeywordUse, Integer> updatedKeywords,
			Map<KeywordUse, Integer> unchangedKeywords) {
		return predictor.predictKeywords(insertedKeywords, unchangedKeywords);
	}

	/** Returns a set of APIs that are likely used in this method. **/
	@SuppressWarnings("unchecked")
	public Set<AbstractAPI> getAPIsUsed(Map<KeywordUse, Integer> keywords) {
		return predictor
				.predictKeywords(KeywordUse.filterMapByChangeType(keywords, ChangeType.INSERTED, ChangeType.UNCHANGED));
	}

	/**
	 * Returns a set of APIs that are likely involved in a method's repair.
	 **/
	@SuppressWarnings("unchecked")
	public Set<AbstractAPI> getAPIsInRepair(Map<KeywordUse, Integer> insertedKeywords,
			Map<KeywordUse, Integer> removedKeywords, Map<KeywordUse, Integer> updatedKeywords,
			Map<KeywordUse, Integer> unchangedKeywords) {
		return predictor.predictKeywords(updatedKeywords, removedKeywords);
	}

	/** Returns a set of APIs that are likely used in this method. **/
	@SuppressWarnings("unchecked")
	public Set<AbstractAPI> getAPIsInRepair(Map<KeywordUse, Integer> keywords) {
		return predictor
				.predictKeywords(KeywordUse.filterMapByChangeType(keywords, ChangeType.UPDATED, ChangeType.REMOVED));
	}

}