package ca.ubc.ece.salt.sdjsb.learning.pointsto;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode.ChangeType;
import ca.ubc.ece.salt.sdjsb.learning.apis.AbstractAPI;
import ca.ubc.ece.salt.sdjsb.learning.apis.KeywordDefinition;
import ca.ubc.ece.salt.sdjsb.learning.apis.KeywordUse;
import ca.ubc.ece.salt.sdjsb.learning.apis.TopLevelAPI;
import ca.ubc.ece.salt.sdjsb.learning.apis.KeywordDefinition.KeywordType;

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
	protected Map<KeywordUse, Integer> keywords;

	public Predictor(TopLevelAPI api, Map<KeywordUse, Integer> keywords) {
		this.api = api;
		this.keywords = keywords;

		/*
		 * Look on input for PACKAGEs keywords.
		 */

		this.requiredPackagesNames = lookupRequiredPackages(
				KeywordUse.filterMapByChangeType(keywords, ChangeType.INSERTED, ChangeType.UNCHANGED));
	}


	public abstract PredictionResults predictKeyword(KeywordUse keyword);

	public abstract Set<AbstractAPI> predictKeywords(Map<KeywordUse, Integer>... keywords);

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
	protected Set<String> lookupRequiredPackages(Map<KeywordUse, Integer>... keywordsMaps) {
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

		for (Map<KeywordUse, Integer> keywordsMap : keywordsMaps) {
			/*
			 * If null map is given, skip it
			 */
			if (keywordsMap == null)
				continue;

			for (KeywordUse keyword : keywordsMap.keySet()) {
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
	protected void filterKeywordsByPackagesNames(List<KeywordDefinition> keywords, Set<String> packagesNames) {
		for (Iterator<KeywordDefinition> iterator = keywords.iterator(); iterator.hasNext();) {
			KeywordDefinition keyword = iterator.next();

			if (!packagesNames.contains(keyword.getPackageName()))
				iterator.remove();
		}
	}

	protected void filterKeywordsByPackages(List<KeywordDefinition> keywords, Set<AbstractAPI> apis) {
		Set<String> packagesNames = new HashSet<>();

		for (AbstractAPI api : apis) {
			packagesNames.add(api.getPackageName());
		}

		filterKeywordsByPackagesNames(keywords, packagesNames);
	}
}
