package ca.ubc.ece.salt.sdjsb.analysis.learning.apis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.ubc.ece.salt.sdjsb.analysis.learning.apis.Keyword.KeywordType;

/**
 * Provides functions to extract keywords from APIs and determine which APIs a
 * repair is likely making changes to. 
 * 
 * This is used to build a data mining / machine learning data set by counting
 * the keywords of an API that are discovered in a program.
 */
public abstract class AbstractAPI {

	protected List<Keyword> keywords;
	protected List<ClassAPI> classes;

	/**
	 * @param includeName The keyword that imports the package in "include([package name keyword]);"
	 * @param methodNames The methods in the API.
	 * @param fieldNames The fields in the API.
	 * @param constantNames The constants in the API.
	 * @param eventNames The events in the API.
	 */
	public AbstractAPI(List<String> methodNames, List<String> fieldNames,
			   List<String> constantNames, List<String> eventNames,
			   List<ClassAPI> classes) {
		// Initialize lists
		this.keywords = new ArrayList<>();
		this.classes = new ArrayList<>();

		for (String methodName : methodNames) {
			this.keywords.add(new Keyword(KeywordType.METHOD_NAME, methodName));
		}

		for (String fieldName : fieldNames) {
			this.keywords.add(new Keyword(KeywordType.FIELD, fieldName));
		}

		for (String constantName : constantNames) {
			this.keywords.add(new Keyword(KeywordType.CONSTANT, constantName));
		}

		for (String eventName : eventNames) {
			this.keywords.add(new Keyword(KeywordType.EVENT, eventName));
		}

		this.classes = classes;
	}

	/**
	 * Checks if the keyword is a member of the API.
	 * @param type The type of the token.
	 * @param keyword The name of the token.
	 * @return True if the keyword/type is present in the API.
	 */
	public boolean isMemberOf(KeywordType type, String keyword) {
		return recursiveKeywordSearch(this, new Keyword(type, keyword));
	}

	/**
	 * Computes the likelihood that the function repair involved the API.
	 * @param keywords A map of the keywords that were found in the function. 
	 * 				   The key for the map is the keyword and the value for the
	 * 				   map is the number of occurrences of the keyword.	 
	 * @return A likelihood between 0 and 1.
	 */
	public double getChangeLikelihood(Map<Keyword, Integer> insertedKeywords,
							   Map<Keyword, Integer> removedKeywords,
							   Map<Keyword, Integer> updatedKeywords,
							   Map<Keyword, Integer> unchangedKeywords) {
		return getUseLikelihood(insertedKeywords, removedKeywords, updatedKeywords, unchangedKeywords);
	}

	/**
	 * Computes the likelihood that the function being repaired uses the API.
	 * @param keywords A map of the keywords that were found in the function. 
	 * 				   The key for the map is the keyword and the value for the
	 * 				   map is the number of occurrences of the keyword.	 
	 * @return A likelihood between 0 and 1.
	 */
	public double getUseLikelihood(Map<Keyword, Integer> insertedKeywords,
							   Map<Keyword, Integer> removedKeywords,
							   Map<Keyword, Integer> updatedKeywords,
							   Map<Keyword, Integer> unchangedKeywords) {
		/*
		 * On this first implementation, we assume that if any of the keywords
		 * inserted, removed, updated or unchanged belongs to this API, the
		 * likelihood is 1. Otherwise, it is 0
		 */

		// Merge all maps
		Map<Keyword, Integer> mergedMap = new HashMap<Keyword, Integer>();
		if (insertedKeywords != null)
			mergedMap.putAll(insertedKeywords);
		if (removedKeywords != null)
			mergedMap.putAll(removedKeywords);
		if (updatedKeywords != null)
			mergedMap.putAll(updatedKeywords);
		if (unchangedKeywords != null)
			mergedMap.putAll(unchangedKeywords);

		// Look if any of the keywords given as input is present on this API
		for (Map.Entry<Keyword, Integer> entry : mergedMap.entrySet()) {
			if (recursiveKeywordSearch(this, entry.getKey()))
				return 1;
		}

		return 0;
	}

	/**
	 * Recursively search for keyword on this API
	 * 
	 * @param keyword
	 *            The keyword we are looking for
	 * @return True if the keyword/type is present in the API.
	 */
	private boolean recursiveKeywordSearch(AbstractAPI api, Keyword keyword) {
		// Check if keyword is on keywords list of API
		if (api.keywords.contains(keyword))
			return true;
		
		// Otherwise, check if keyword is member of any of the classes of
		// API, which may have subclasses itself
		for (ClassAPI klass : api.classes) {
			if (recursiveKeywordSearch(klass, keyword))
				return true;
		}
		
		// If keyword was not found anywhere
		return false;
	}

}
