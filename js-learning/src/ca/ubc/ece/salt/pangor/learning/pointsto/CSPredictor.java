package ca.ubc.ece.salt.pangor.learning.pointsto;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ca.ubc.ece.salt.pangor.learning.apis.AbstractAPI;
import ca.ubc.ece.salt.pangor.learning.apis.KeywordDefinition;
import ca.ubc.ece.salt.pangor.learning.apis.KeywordUse;
import ca.ubc.ece.salt.pangor.learning.apis.TopLevelAPI;

/**
 * Predictor implementation using "confidence" and "support" measures.
 * Confidence is the number of unique keywords found in the method that are also
 * in the API. Support is the number of non-unique keywords found in the method
 * that are also in the API.
 */
public class CSPredictor extends Predictor {
	/*
	 * Data structures used to calculate API's score
	 */
	Set<AbstractAPI> apisFound = new HashSet<>();
	Map<AbstractAPI, Integer> confidenceMap = new HashMap<>();
	Map<AbstractAPI, Integer> supportMap = new HashMap<>();
	Map<AbstractAPI, Integer> scoreMap = new HashMap<>();

	public CSPredictor(TopLevelAPI api, Map<KeywordUse, Integer> keywords) {
		super(api, keywords);

		calculateScore();
	}

	@Override
	public PredictionResults predictKeyword(KeywordUse keyword) {
		/*
		 * Check if this keyword was on the input.
		 */
		if (!isKeywordOnInput(keyword))
			throw new RuntimeException("Keyword " + keyword + " was not given on input");

		/*
		 * Get all APIs from TopLevelAPI which contain this keyword
		 */
		Set<AbstractAPI> apis = getAPIsFromKeyword(keyword);

		/*
		 * Create a PredictionResults object with their scores
		 */
		PredictionResults results = new PredictionResults();

		for (AbstractAPI api : apis) {
			results.add(new PredictionResult(api, scoreMap.get(api)));
		}

		return results;
	}

	/**
	 * Check if keyword is on input. Can not use keywords.containsKey because it
	 * will try to match the ChangeType, and we do not care for it right now
	 *
	 * @param keyword
	 * @return
	 */
	private boolean isKeywordOnInput(KeywordUse keyword) {
		for (KeywordUse k : keywords.keySet()) {
			if (k.type == keyword.type && k.keyword.equals(keyword.keyword))
				return true;
		}

		return false;
	}

	@Override
	public Set<AbstractAPI> predictKeywords(@SuppressWarnings("unchecked") Map<KeywordUse, Integer>... keywords) {
		Set<AbstractAPI> allAPIs = new HashSet<>();

		for (Map<KeywordUse, Integer> keywordMap : keywords) {
			for (KeywordUse keyword : keywordMap.keySet()) {
				allAPIs.addAll(getAPIsFromKeyword(keyword));
			}

		}

		return allAPIs;
	}

	/**
	 * Calculate the score for all used APIs and store it in a scoreMap
	 */
	protected void calculateScore() {
		/*
		 * Iterate over keywords, and look for their APIs
		 */
		for (KeywordUse keyword : keywords.keySet()) {
			List<KeywordDefinition> keywordsFound = api.getAllKeywords(keyword);

			filterKeywordsByPackagesNames(keywordsFound, requiredPackagesNames);

			/*
			 * If there is only one occurrence, register it as Confidence
			 */
			if (keywordsFound.size() == 1) {
				apisFound.add(keywordsFound.get(0).api);
				addOrIncrement(keywordsFound.get(0), confidenceMap);
			}

			/*
			 * If there are more than one, register it as Support
			 */
			if (keywordsFound.size() > 1) {
				for (KeywordDefinition k : keywordsFound) {
					apisFound.add(k.api);
					addOrIncrement(k, supportMap);
				}
			}
		}

		/*
		 * Calculates the score
		 */
		for (AbstractAPI api : apisFound) {
			Integer confidence = (confidenceMap.get(api) != null ? confidenceMap.get(api) : 0);
			Integer support = (supportMap.get(api) != null ? supportMap.get(api) : 0);

			Integer score = scoreFormula(confidence, support);

			scoreMap.put(api, score);
		}
	}

	/**
	 * The actual formula to calculate the score.
	 * TODO: At the moment this is a random formula, used just for testing purposes
	 */
	private int scoreFormula(Integer confidence, Integer support) {
		return (confidence * 3) + support;
	}

	/**
	 * Helper method to manipulate the maps. It tries to add a keyword to a map.
	 * If it already exists, it increments its value. If not, it inserts on the
	 * map
	 *
	 * @param keyword
	 * @param map
	 */
	private void addOrIncrement(KeywordDefinition keyword, Map<AbstractAPI, Integer> map) {
		if (map.containsKey(keyword.api)) {
			map.put(keyword.api, map.get(keyword.api) + 1);
		} else {
			map.put(keyword.api, 1);
		}
	}

	protected Set<AbstractAPI> getAPIsFromKeyword(KeywordDefinition keyword) {
		List<KeywordDefinition> keywordsFound = api.getAllKeywords(keyword);
		Set<AbstractAPI> apis = new HashSet<>();

		filterKeywordsByPackages(keywordsFound, apisFound);

		for (KeywordDefinition k : keywordsFound)
			apis.add(k.api);

		return apis;
	}
}
