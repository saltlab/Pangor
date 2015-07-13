package ca.ubc.ece.salt.sdjsb.analysis.learning.apis;

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
}
