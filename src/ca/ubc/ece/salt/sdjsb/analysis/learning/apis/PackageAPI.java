package ca.ubc.ece.salt.sdjsb.analysis.learning.apis;

import java.util.List;

import ca.ubc.ece.salt.sdjsb.analysis.learning.apis.Keyword.KeywordType;

/**
 * Defines the API of a Node.js package.
 */
public class PackageAPI extends AbstractAPI {
	/** The include name of the package */
	protected String includeName;

	/**
	 * @param includeName The keyword that imports the package in "include([package name keyword]);"
	 * @param methodNames The methods in the API.
	 * @param fieldNames The fields in the API.
	 * @param constantNames The constants in the API.
	 * @param eventNames The events in the API.
	 */
	public PackageAPI(String includeName, List<String> methodNames,
					  List<String> fieldNames, List<String> constantNames,
					  List<String> eventNames, List<ClassAPI> classes) {
		super(methodNames, fieldNames, constantNames, eventNames, classes);
		this.includeName = includeName;
		this.keywords.add(new Keyword(KeywordType.PACKAGE, includeName));
	}

	@Override
	public String getName() {
		return includeName;
	}
}