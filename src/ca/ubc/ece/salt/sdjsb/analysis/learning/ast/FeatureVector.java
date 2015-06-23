package ca.ubc.ece.salt.sdjsb.analysis.learning.ast;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode.ChangeType;

public class FeatureVector {

	/**
	 * Keywords to track including:
	 *  - Reserved words
	 *  - Objects
	 *  - Methods
	 *  - Properties
	 */
	public static final List<String> KEYWORDS  = Arrays.asList("arguments", "boolean", // Reserved words
			"byte", "char", "class", "debugger", "default", "delete", "double",
			"enum", "export", "false", "float", "instanceof", "int",
			"long", "native", "null", "short", "super", "this", "transient",
			"true", "typeof", "volatile",
			"Array", "Date", "Math", "NaN", "Number", "Object", "String", // Objects
			"undefined",
			"eval", "hasOwnProperty", "isFinite", "isNaN", "isPrototypeOf", // Methods
			"toString", "valueOf",
			"length", "name", "prototype", // Properties
			"zero", "blank", "empty_object", "empty_array", "error", // Custom
			"callback", "~getNextKey", "~hasNextKey", "~error~");
	
	/** A counter to produce unique IDs for each feature vector. **/
	private static int idCounter;
	
	/** The unique ID for the feature vector. **/
	public int id;
	
	/** The ID for the commit where the bug is present. **/
	public String buggyCommitID;
	
	/** The ID for the commit where the bug is repaired. **/
	public String repairedCommitID;
	
	/** The function from which this feature vector was constructed. **/
	public String functionName;
	
	/** The statement types in each fragment. **/
	public Map<String, Integer> insertedStatementMap;
	public Map<String, Integer> removedStatementMap;
	public Map<String, Integer> updatedStatementMap;
	
	/** The keyword counts in each fragment. **/
	public Map<String, Integer> insertedKeywordMap;
	public Map<String, Integer> removedKeywordMap;
	public Map<String, Integer> updatedKeywordMap;

	public FeatureVector() {
		this.id = FeatureVector.getNextID();
		this.insertedStatementMap = FeatureVector.buildStatementMap();
		this.removedStatementMap = FeatureVector.buildStatementMap();
		this.updatedStatementMap = FeatureVector.buildStatementMap();
		this.insertedKeywordMap = FeatureVector.buildKeywordMap();
		this.removedKeywordMap = FeatureVector.buildKeywordMap();
		this.updatedKeywordMap = FeatureVector.buildKeywordMap();
	}
	
	/**
	 * Joins a source feature vector with this (the destination) feature vector.
	 * @param source The source feature vector.
	 */
	public void join(FeatureVector source) {
		this.removedStatementMap = source.removedStatementMap;
		this.removedKeywordMap = source.removedKeywordMap;
	}
	
	/**
	 * If the given token is a statement, that statement's count is incremented
	 * by one.
	 * @param token The string to check against the statement list.
	 */
	@SuppressWarnings("incomplete-switch")
	public void addStatement(String token, ChangeType changeType) {

		switch(changeType) {

		case INSERTED:
			if(this.insertedStatementMap.containsKey(token)) {
				this.insertedStatementMap.put(token,  this.insertedStatementMap.get(token) + 1);
			}
			break;

		case REMOVED:
			if(this.removedStatementMap.containsKey(token)) {
				this.removedStatementMap.put(token,  this.removedStatementMap.get(token) + 1);
			}
			break;

		case UPDATED:
			if(this.updatedStatementMap.containsKey(token)) {
				this.updatedStatementMap.put(token,  this.updatedStatementMap.get(token) + 1);
			}
			break;

		}
	}
	
	/**
	 * If the given token is a keyword, that keyword's count is incremented by
	 * one.
	 * @param token The string to check against the keyword list.
	 */
	@SuppressWarnings("incomplete-switch")
	public void addKeyword(String token, ChangeType changeType) {

		switch(changeType) {

		case INSERTED:
			if(this.insertedKeywordMap.containsKey(token)) {
				this.insertedKeywordMap.put(token,  this.insertedKeywordMap.get(token) + 1);
			}
			break;

		case REMOVED:
			if(this.removedKeywordMap.containsKey(token)) {
				this.removedKeywordMap.put(token,  this.removedKeywordMap.get(token) + 1);
			}
			break;

		case UPDATED:
			if(this.updatedKeywordMap.containsKey(token)) {
				this.updatedKeywordMap.put(token,  this.updatedKeywordMap.get(token) + 1);
			}
			break;

		}
	}
	
	/**
	 * @return true if all values in the statement and keyword maps are zero.
	 */
	public boolean isEmpty() {

//		if(!isEmpty(this.insertedStatementMap)) return false;
//		if(!isEmpty(this.removedStatementMap)) return false;
//		if(!isEmpty(this.updatedStatementMap)) return false;
		if(!isEmpty(this.insertedKeywordMap)) return false;
		if(!isEmpty(this.removedKeywordMap)) return false;
		if(!isEmpty(this.updatedKeywordMap)) return false;

		return true;

	}
	
	/**
	 * Checks if an integer in the map is greater than zero.
	 * @param map the map to check
	 * @return true if all values in the map are zero.
	 */
	private static boolean isEmpty(Map<String, Integer> map) {
		for(Integer value : map.values()) {
			if(value > 0) return false;
		}
		return true;
	}
	
	/**
	 * @return A map initialized with the statements we want to track.
	 */
	private static Map<String, Integer> buildStatementMap() {

		Map<String, Integer> statementsInFragments = new HashMap<String, Integer>();

		statementsInFragments.put("EmptyStatement", 0);
		statementsInFragments.put("ExpressionStatement", 0);
		statementsInFragments.put("VariableDeclaration", 0);
		statementsInFragments.put("FunctionNode", 0);
		statementsInFragments.put("ReturnStatement", 0);
		statementsInFragments.put("LabeledStatement", 0);
		statementsInFragments.put("ThrowStatement", 0);
		statementsInFragments.put("TryStatement", 0);
		statementsInFragments.put("WithStatement", 0);
		statementsInFragments.put("BreakStatement", 0);
		statementsInFragments.put("ContinueStatement", 0);
		statementsInFragments.put("SwitchStatement", 0);
		
		return statementsInFragments;
		
	}
	
	/**
	 * @return the CSV header.
	 */
	public static String getHeader() {

		String header = "ID\tBuggy Commit\tRepaired Commit\tFunction";

		for(String statementType : FeatureVector.buildStatementMap().keySet()) {
			header += "\tInserted-" + statementType;
			header += "\tRemoved-" + statementType;
			header += "\tUpdated-" + statementType;
		}
		
		for(String keyword : KEYWORDS) {
			header += "\tInserted-" + keyword;
			header += "\tRemoved-" + keyword;
			header += "\tUpdated-" + keyword;
		}

		return header;

	}
	
	/**
	 * @return the CSV row (the feature vector) as a string.
	 */
	@Override
	public String toString() {

		String vector = id + "\t" + buggyCommitID + "\t" + repairedCommitID + "\t" + functionName;
		
		for(String statementType : this.insertedStatementMap.keySet()) {
			vector += "\t" + this.insertedStatementMap.get(statementType);
			vector += "\t" + this.removedStatementMap.get(statementType);
			vector += "\t" + this.updatedStatementMap.get(statementType);
		}
		
		for(String keyword : KEYWORDS) {
			vector += "\t" + this.insertedKeywordMap.get(keyword);
			vector += "\t" + this.removedKeywordMap.get(keyword);
			vector += "\t" + this.updatedKeywordMap.get(keyword);
		}
		
		return vector;

	}

	/**
	 * @return The next unique ID for a feature vector alert.
	 */
	private static int getNextID() {
		idCounter++;
		return idCounter;
	}

	/**
	 * @return a map initialized with the keywords we want to track.
	 */
	private static Map<String, Integer> buildKeywordMap() {

		/* Initialize the keyword map. */
		Map<String, Integer> keywordMap = new HashMap<String, Integer>();
		for(String keyword : KEYWORDS) { 
			keywordMap.put(keyword, 0);
		}
		
		return keywordMap;
		
	}
	
}