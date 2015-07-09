package ca.ubc.ece.salt.sdjsb.analysis.learning.apis;

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
		
		for(String methodName : methodNames) {
			this.keywords.add(new Keyword(KeywordType.METHOD_NAME, methodName));
		}
		
		for(String fieldName : fieldNames) {
			this.keywords.add(new Keyword(KeywordType.FIELD, fieldName));
		}
		
		for(String constantName : constantNames) {
			this.keywords.add(new Keyword(KeywordType.CONSTANT, constantName));
		}
		
		for(String eventName : eventNames) {
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
	public double isMemberOf(String type, String keyword) { 
		throw new UnsupportedOperationException(); 
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
		throw new UnsupportedOperationException(); 
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
		throw new UnsupportedOperationException();
	}
	
}