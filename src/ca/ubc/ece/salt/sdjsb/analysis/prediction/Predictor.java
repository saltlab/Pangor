package ca.ubc.ece.salt.sdjsb.analysis.prediction;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ca.ubc.ece.salt.sdjsb.analysis.learning.apis.AbstractAPI;
import ca.ubc.ece.salt.sdjsb.analysis.learning.apis.Keyword;
import ca.ubc.ece.salt.sdjsb.analysis.learning.apis.Keyword.KeywordType;
import ca.ubc.ece.salt.sdjsb.analysis.learning.apis.TopLevelAPI;

/**
 * Abstract class to model a Predictor
 */
public abstract class Predictor {
	/** The top level API where API's are looked for */
	protected TopLevelAPI api;

	/**
	 * The "required name" of packages that were actually imported on the file
	 * and will serve as a filter on our predictions. Looked up from the given
	 * keywords
	 */
	protected Set<String> requiredPackagesNames;

	/**
	 * Given keywords
	 */
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

		/*
		 * Look on input for PACKAGEs keywords.
		 */

		requiredPackagesNames = lookupRequiredPackages(insertedKeywords, unchangedKeywords);
	}

	public abstract PredictionResults predictKeyword(Keyword keyword);

	/**
	 * Return a list of names of required packages
	 *
	 * @return a list of names of required packages
	 */
	public Set<String> getRequiredPackagesNames() {
		return requiredPackagesNames;
	}

	/**
	 * Internal method to look for KeywordType.PACKAGE keywords on the input
	 */
	protected Set<String> lookupRequiredPackages(Map<Keyword, Integer>... keywordsMaps) {
		Set<String> outputSet = new HashSet<>();

		/*
		 * we assume "global" is always imported
		 */
		outputSet.add("global");

		/*
		 * If no input is given, just return empty list
		 */
		if (keywordsMaps.length == 0)
			return outputSet;

		for (Map<Keyword, Integer> keywordsMap : keywordsMaps) {
			/*
			 * If null map is given, skip it
			 */
			if (keywordsMap == null)
				continue;

			for (Keyword keyword : keywordsMap.keySet()) {
				if (keyword.type.equals(KeywordType.PACKAGE))
					outputSet.add(keyword.keyword);
			}
		}

		return outputSet;
	}

	/**
	 * Given a list of keywords and some packages, remove all keywords from the
	 * list that do not belong to any of the packages
	 *
	 * @param keywords list of keywords
	 * @param packagesNames list of package names
	 */
	protected void filterKeywordsByPackagesNames(List<Keyword> keywords, Set<String> packagesNames) {
		for (Iterator<Keyword> iterator = keywords.iterator(); iterator.hasNext();) {
			Keyword keyword = iterator.next();

			if (!packagesNames.contains(keyword.getPackageName()))
				iterator.remove();
		}
	}

	protected void filterKeywordsByPackages(List<Keyword> keywords, Set<AbstractAPI> apis) {
		Set<String> packagesNames = new HashSet<>();

		for (AbstractAPI api : apis) {
			packagesNames.add(api.getPackageName());
		}

		filterKeywordsByPackagesNames(keywords, packagesNames);
	}
}
