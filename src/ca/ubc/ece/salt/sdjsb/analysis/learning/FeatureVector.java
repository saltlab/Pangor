package ca.ubc.ece.salt.sdjsb.analysis.learning;

import java.util.HashMap;
import java.util.Map;

import org.mozilla.javascript.ast.AstNode;

public class FeatureVector {
	
	/** A counter to produce unique IDs for each feature vector. **/
	private static int idCounter;
	
	/** The unique ID for the feature vector. **/
	public int id;
	
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
	public Map<String, Integer> statementsInFragments;
	
	/** The keyword counts in each fragment. **/
	public Map<String, Integer> keywordsInFragments;
	
	public FeatureVector() {
		this.id = FeatureVector.getNextID();
		this.statementsInFragments = FeatureVector.buildStatementMap();
		this.keywordsInFragments = KeywordVisitor.buildKeywordMap();
	}
	
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
	
	public static String getHeader() {

		String header = "ID\tFunction\t# Frag\t# Cond\tAvg Frag\tMax Frag";

		for(String statementType : FeatureVector.buildStatementMap().keySet()) {
			header += "\t" + statementType;
		}
		
		for(String keyword : KeywordVisitor.getKewordList()) {
			header += "\t" + keyword;
		}

		return header;

	}
	
	@Override
	public String toString() {

		String vector = id + "\t" + functionName + "\t" + numberOfInterestingFragments + "\t" + numberOfInterestingConditions + "\t" + avgInterestingFragmentSize + "\t" + maxInterestingFragmentSize;
		
		for(String statementType : this.statementsInFragments.keySet()) {
			vector += "\t" + this.statementsInFragments.get(statementType);
		}
		
		for(String keyword : KeywordVisitor.getKewordList()) {
			vector += "\t" + this.keywordsInFragments.get(keyword);
		}
		
		return vector;

	}

	/**
	 * Calls the appropriate build method for the node type.
	 */
	public void addInterestingStatement(AstNode node) {
		
		if(node == null) return;
		
		String statementType = node.getClass().getSimpleName();
		
		if(this.statementsInFragments.containsKey(statementType)) {
			Integer count = this.statementsInFragments.get(statementType) + 1;
			this.statementsInFragments.put(statementType, count);
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
	
}