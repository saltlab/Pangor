package ca.ubc.ece.salt.sdjsb.learning.apis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.ubc.ece.salt.sdjsb.learning.apis.KeywordDefinition.KeywordType;

/**
 * Provides functions to extract keywords from APIs and determine which APIs a
 * repair is likely making changes to.
 *
 * This is used to build a data mining / machine learning data set by counting
 * the keywords of an API that are discovered in a program.
 */
public abstract class AbstractAPI {

	protected List<KeywordDefinition> keywords;
	protected List<ClassAPI> classes;

	/**
	 * Pointer to the parent to maintain a tree-like structure. Used to navigate
	 * the tree from bottom to top to look for which package an API belongs to
	 */
	protected AbstractAPI parent;

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
			this.keywords.add(new KeywordDefinition(KeywordType.METHOD, methodName, this));
		}

		for (String fieldName : fieldNames) {
			this.keywords.add(new KeywordDefinition(KeywordType.FIELD, fieldName, this));
		}

		for (String constantName : constantNames) {
			this.keywords.add(new KeywordDefinition(KeywordType.CONSTANT, constantName, this));
		}

		for (String eventName : eventNames) {
			this.keywords.add(new KeywordDefinition(KeywordType.EVENT, eventName, this));
		}

		this.classes = classes;
		addParentToChildren(this.classes);
	}

	/**
	 * Get the first occurrence of the given keyword on the API
	 *
	 * @param type The type of the token.
	 * @param keyword The name of the token.
	 * @return keyword if the keyword/type is present in the API, otherwise,
	 *         null
	 */
	public KeywordDefinition getFirstKeyword(KeywordType type, String keyword) {
		return getFirstKeyword(new KeywordDefinition(type, keyword));
	}

	/**
	 * Get the first occurrence of the given keyword on the API
	 *
	 * @param keyword Keyword object
	 * @return keyword if the keyword/type is present in the API, otherwise,
	 *         null
	 */
	public KeywordDefinition getFirstKeyword(KeywordDefinition keyword) {
		List<KeywordDefinition> keywordsList = getAllKeywords(keyword);

		if (keywordsList.size() > 0)
			return keywordsList.get(0);
		else
			return null;
	}

	/**
	 * Get all occurrences of the given keyword on the API
	 *
	 * @param keyword Keyword object
	 * @return keywords a list with all occurrences of the keyword on the API.
	 *         if none is found, empty list is returned
	 */
	public List<KeywordDefinition> getAllKeywords(KeywordDefinition keyword) {
		List<KeywordDefinition> keywordsList = new ArrayList<>();

		recursiveKeywordSearch(this, keyword, keywordsList);

		return keywordsList;
	}

	/**
	 * Computes the likelihood that the function repair involved the API.
	 * @param keywords A map of the keywords that were found in the function.
	 * 				   The key for the map is the keyword and the value for the
	 * 				   map is the number of occurrences of the keyword.
	 * @return A likelihood between 0 and 1.
	 */
	public double getChangeLikelihood(Map<KeywordDefinition, Integer> insertedKeywords,
							   Map<KeywordDefinition, Integer> removedKeywords,
							   Map<KeywordDefinition, Integer> updatedKeywords,
							   Map<KeywordDefinition, Integer> unchangedKeywords) {
		return getUseLikelihood(insertedKeywords, removedKeywords, updatedKeywords, unchangedKeywords);
	}

	/**
	 * Computes the likelihood that the function being repaired uses the API.
	 * @param keywords A map of the keywords that were found in the function.
	 * 				   The key for the map is the keyword and the value for the
	 * 				   map is the number of occurrences of the keyword.
	 * @return A likelihood between 0 and 1.
	 */
	public double getUseLikelihood(Map<KeywordDefinition, Integer> insertedKeywords,
							   Map<KeywordDefinition, Integer> removedKeywords,
							   Map<KeywordDefinition, Integer> updatedKeywords,
							   Map<KeywordDefinition, Integer> unchangedKeywords) {
		/*
		 * On this first implementation, we assume that if any of the keywords
		 * inserted, removed, updated or unchanged belongs to this API, the
		 * likelihood is 1. Otherwise, it is 0
		 */

		// Merge all maps
		Map<KeywordDefinition, Integer> mergedMap = new HashMap<KeywordDefinition, Integer>();
		if (insertedKeywords != null)
			mergedMap.putAll(insertedKeywords);
		if (removedKeywords != null)
			mergedMap.putAll(removedKeywords);
		if (updatedKeywords != null)
			mergedMap.putAll(updatedKeywords);
		if (unchangedKeywords != null)
			mergedMap.putAll(unchangedKeywords);

		// Look if any of the keywords given as input is present on this API
		// not implemented yet

		return 1;
	}

	/**
	 * Return the name of this API. Subclasses should override
	 * with relevant name (e.g. class name for ClassAPI or package
	 * name for PackageAPI).
	 *
	 * @return String relevant identifier for API
	 */
	public abstract String getName();

	public String getPackageName() {
		/*
		 * Go up in the tree until we find a package. I know this is hard to
		 * read, sorry. But it seems to work.
		 */
		AbstractAPI lastParent = parent;

		while (lastParent.parent != null) {
			lastParent = lastParent.parent;
		}

		return lastParent.getPackageName();
	}

	/**
	 * Recursively search for keyword on this API
	 *
	 * @param keyword The keyword we are looking for
	 * @return Keyword if the keyword/type is present in the API, null otherwise
	 */
	protected void recursiveKeywordSearch(AbstractAPI api, KeywordDefinition keyword,
			List<KeywordDefinition> outputList) {
		/*
		 * Check if keyword is present on the keywords list. If it is, add the
		 * keyword we found on the list, which may contain more information
		 */
		int index = api.keywords.indexOf(keyword);
		if (index != -1)
			outputList.add(api.keywords.get(index));

		/*
		 * Otherwise, check if keyword is member of any of the classes of API,
		 * which may have subclasses itself
		 */
		for (ClassAPI klass : api.classes) {
			recursiveKeywordSearch(klass, keyword, outputList);
		}
	}

	/**
	 * Add pointer to this instance on its children
	 *
	 * @param children
	 */
	protected void addParentToChildren(List<ClassAPI> children) {
		for (ClassAPI child : children)
			child.parent = this;
	}
}
