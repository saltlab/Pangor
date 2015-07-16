package ca.ubc.ece.salt.sdjsb.analysis.learning.apis;

import java.util.ArrayList;
import java.util.List;

import ca.ubc.ece.salt.sdjsb.analysis.learning.apis.Keyword.KeywordType;

/**
 * Defines the default JavaScript API and stores all Node.js packages that
 * we want to include.
 */
public class TopLevelAPI extends AbstractAPI {
	protected List<PackageAPI> packages;

	/**
	 * @param keywords The JavaScript keywords.
	 * @param methodNames The methods in the API.
	 * @param fieldNames The fields in the API.
	 * @param constantNames The constants in the API.
	 * @param eventNames The events in the API.
	 */
	public TopLevelAPI(List<String> keywords, List<PackageAPI> packages,
					   List<String> methodNames, List<String> fieldNames,
					   List<String> constantNames, List<String> eventNames,
					   List<ClassAPI> classes) {
		super(methodNames, fieldNames, constantNames, eventNames, classes);

		for(String keyword : keywords) {
			this.keywords.add(new Keyword(KeywordType.RESERVED, keyword));
		}

		this.packages = packages;
	}

	public List<PackageAPI> getPackages() {
		return packages;
	}

	/**
	 * Get all occurrences of the given keyword on the API
	 *
	 * @param keyword Keyword object
	 * @return keywords a list with all occurrences of the keyword on the API.
	 *         if none is found, empty list is returned
	 */
	/*
	 * TODO: This code was copied and pasted from AbstractAPI and should be
	 * refactored as soon as possible.
	 */
	@Override
	public List<Keyword> getAllKeywords(Keyword keyword) {
		List<Keyword> keywordsList = new ArrayList<>();

		recursiveKeywordSearch(this, keyword, keywordsList);

		return keywordsList;
	}

	/**
	 * Recursively search for keyword on this API
	 *
	 * @param keyword The keyword we are looking for
	 * @return Keyword if the keyword/type is present in the API, null otherwise
	 */
	/*
	 * TODO: This code was copied and pasted from AbstractAPI and should be
	 * refactored as soon as possible. I could not find a better way to solve
	 * this at the time. recursiveKeywordSearch() should work the same for
	 * AbstractAPI and TopLevelAPI, only difference is that TopLevelAPI also has
	 * to include its own packages on the search
	 */
	protected void recursiveKeywordSearch(TopLevelAPI api, Keyword keyword, List<Keyword> outputList) {
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

		/*
		 * Otherwise, check if keyword is member of any of the packages of API,
		 * which may have subclasses itself
		 */
		for (PackageAPI pkg : api.packages) {
			recursiveKeywordSearch(pkg, keyword, outputList);
		}
	}

	@Override
	public String getPackageName() {
		return "global";
	}

	@Override
	public String getName() {
		return "global";
	}

}
