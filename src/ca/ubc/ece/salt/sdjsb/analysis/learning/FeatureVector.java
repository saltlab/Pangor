package ca.ubc.ece.salt.sdjsb.analysis.learning;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.mozilla.javascript.ast.AstNode;

public class FeatureVector {

	public static final List<String> RESERVED = Arrays.asList("arguments", "boolean",
			"byte", "char", "class", "debugger", "default", "delete", "double",
			"enum", "eval", "export", "false", "float", "instanceof", "int",
			"long", "native", "null", "short", "super", "this", "transient",
			"true", "typeof", "volatile");
	
	public static final List<String> OBJECTS = Arrays.asList("Array", "Date", "Math", 
			"NaN", "Number", "Object", "String", "undefined");
	
	public static final List<String> METHODS = Arrays.asList("eval", "hasOwnProperty",
			"isFinite", "isNaN", "isPrototypeOf", "toString", "valueOf");
	
	public static final List<String> PROPERTIES = Arrays.asList("length", "name",
			"prototype");
	
	public static final List<String> CUSTOM = Arrays.asList("zero", "blank", 
			"empty_object", "empty_array", "error", "callback", "~getNextKey",
			"~hasNextKey", "~error~");
	
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
	
	/** The number of interesting path fragments in the function. **/
	public int numberOfInterestingFragments;
	
	/** The number of interesting conditions in the function. **/
	public int numberOfInterestingConditions;
	
	/** The average size of the interesting fragments in the function. **/
	public double avgInterestingFragmentSize;
	
	/** The maximum size of the interesting fragments in the function. **/
	public int maxInterestingFragmentSize;
	
	/** The statement types in each fragment. **/
	public Map<String, Integer> statementMap;
	
	/** The keyword counts in each fragment. **/
	public Map<String, Integer> keywordMap;

	public FeatureVector() {
		this.id = FeatureVector.getNextID();
		this.statementMap = FeatureVector.buildStatementMap();
		this.keywordMap = FeatureVector.buildKeywordMap();
	}
	
	/**
	 * If the given token is a statement, that statement's count is incremented
	 * by one.
	 * @param token The string to check against the statement list.
	 */
	public void addStatement(String token) {
		if(this.statementMap.containsKey(token)) {
			this.statementMap.put(token,  this.statementMap.get(token) + 1);
		}
	}
	
	/**
	 * If the given token is a keyword, that keyword's count is incremented by
	 * one.
	 * @param token The string to check against the keyword list.
	 */
	public void addKeyword(String token) {
		if(!token.isEmpty() && this.keywordMap.containsKey(token)){
			this.keywordMap.put(token, this.keywordMap.get(token) + 1);
		}
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

		String header = "ID\tFunction\t# Frag\t# Cond\tAvg Frag\tMax Frag";

		for(String statementType : FeatureVector.buildStatementMap().keySet()) {
			header += "\t" + statementType;
		}
		
		for(String keyword : FeatureVector.getKewordList()) {
			header += "\t" + keyword;
		}

		return header;

	}
	
	/**
	 * @return the CSV row (the feature vector) as a string.
	 */
	@Override
	public String toString() {

		String vector = id + "\t" + functionName + "\t" + numberOfInterestingFragments + "\t" + numberOfInterestingConditions + "\t" + avgInterestingFragmentSize + "\t" + maxInterestingFragmentSize;
		
		for(String statementType : this.statementMap.keySet()) {
			vector += "\t" + this.statementMap.get(statementType);
		}
		
		for(String keyword : KeywordVisitor.getKewordList()) {
			vector += "\t" + this.keywordMap.get(keyword);
		}
		
		return vector;

	}

	/**
	 * Calls the appropriate build method for the node type.
	 */
	public void addInterestingStatement(AstNode node) {
		
		if(node == null) return;
		
		String statementType = node.getClass().getSimpleName();
		
		if(this.statementMap.containsKey(statementType)) {
			Integer count = this.statementMap.get(statementType) + 1;
			this.statementMap.put(statementType, count);
		}
		else {
			System.err.println("Statement type not in fragment list: " + statementType);
		}

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

		/* Combine all the keywords into one list. */
		List<String> keywords = KeywordVisitor.getKewordList();
		
		/* Initialize the keyword map. */
		Map<String, Integer> keywordMap = new HashMap<String, Integer>();
		for(String keyword : keywords) { 
			keywordMap.put(keyword, 0);
		}
		
		return keywordMap;
		
	}
	
	/**
	 * @return a combined list of all keywords.
	 */
	public static List<String> getKewordList() {

		List<String> keywords = new LinkedList<String>();

		keywords.addAll(RESERVED);
		keywords.addAll(OBJECTS);
		keywords.addAll(METHODS);
		keywords.addAll(PROPERTIES);
		keywords.addAll(CUSTOM);
		
		return keywords;

	}
	
}