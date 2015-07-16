package ca.ubc.ece.salt.sdjsb.analysis.learning.apis;

import java.util.List;

import ca.ubc.ece.salt.sdjsb.analysis.learning.apis.Keyword.KeywordType;

/**
 * Defines the API of a Node.js class.
 */
public class ClassAPI extends AbstractAPI {
	/** The identifier of the class */
	protected String className;

	/**
	 * @param className The identifier of the class;"
	 * @param methodNames The methods in the API.
	 * @param fieldNames The fields in the API.
	 * @param constantNames The constants in the API.
	 * @param eventNames The events in the API.
	 */
	public ClassAPI(String className, List<String> methodNames,
					  List<String> fieldNames, List<String> constantNames,
					  List<String> eventNames, List<ClassAPI> classes) {
		super(methodNames, fieldNames, constantNames, eventNames, classes);
		this.className = className;
		this.keywords.add(new Keyword(KeywordType.CLASS, className, this));
	}

	@Override
	public String getName() {
		return className;
	}
}
