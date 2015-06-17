package ca.ubc.ece.salt.sdjsb.analysis.learning;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.KeywordLiteral;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.NodeVisitor;
import org.mozilla.javascript.ast.NumberLiteral;
import org.mozilla.javascript.ast.StringLiteral;

public class KeywordVisitor implements NodeVisitor {
	
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
	
	/** Keeps track of the keywords found by the visitor. **/
	private Map<String, Integer> keywordMap;

	/**
	 * Visits the node and returns a map containing the counts for each keyword
	 * (a bag of words approach).
	 * @param node the node to visit.
	 * @return a bag of words containing the keyword counts.
	 */
	public static Map<String, Integer> getKeywords(AstNode node) {
		KeywordVisitor visitor = new KeywordVisitor();
		node.visit(visitor);
		return visitor.keywordMap;
	}
	
	/**
	 * Visits the node and returns a map containing the counts for each keyword
	 * (a bag of words approach).
	 * @param node the node to visit.
	 * @param keywordMap the map to populate.
	 */
	public static void getKeywords(AstNode node, Map<String, Integer> keywordMap) {
		KeywordVisitor visitor = new KeywordVisitor(keywordMap);
		node.visit(visitor);
	}
	
	public static Map<String, Integer> buildKeywordMap() {

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
	
	/**
	 * Creates a new keyword map.
	 */
	public KeywordVisitor() {
		this.keywordMap = KeywordVisitor.buildKeywordMap();
	}
	
	/**
	 * Initialize the keyword map from the existing map.
	 * @param keywordMap
	 */
	public KeywordVisitor(Map<String, Integer> keywordMap) {
		this.keywordMap = keywordMap;
	}
	
	@Override
	public boolean visit(AstNode node) {
		
		String token = "";
		
		if(node instanceof Name) {
			Name name = (Name) node;
			token = name.getIdentifier();

			if(token.matches("e|err")) token = "error";
			else if(token.matches("cb|callb")) token = "callback";
		}
		else if(node instanceof KeywordLiteral) {
			KeywordLiteral kl = (KeywordLiteral) node;
			token = kl.toSource();
		}
		else if(node instanceof NumberLiteral) {
			NumberLiteral nl = (NumberLiteral) node;
			if(Double.parseDouble(nl.getValue()) == 0.0) {
				token = "zero";
			}
		}
		else if(node instanceof StringLiteral) {
			StringLiteral sl = (StringLiteral) node;
			if(sl.getValue().isEmpty()) {
				token = "blank";
			}
		}

		if(!token.isEmpty() && this.keywordMap.containsKey(token)){
			this.keywordMap.put(token, this.keywordMap.get(token) + 1);
		}
		
		return true;
	}

}
