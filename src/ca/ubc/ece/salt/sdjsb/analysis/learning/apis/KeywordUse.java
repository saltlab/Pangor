package ca.ubc.ece.salt.sdjsb.analysis.learning.apis;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode.ChangeType;

/**
 * Stores the use of a keyword in a source code file. This includes the context
 * under which it is used and the type of change that occurred on the keyword
 * from the source to the destination code.
 */
public class KeywordUse extends KeywordDefinition {

	/** The context under which the keyword is used. **/
	public KeywordContext context;

	/** How this keyword was modified from the source to the destination file. **/
	public ChangeType changeType;

	/**
	 * To be used when investigating a single function.
	 * @param type
	 * @param context
	 * @param keyword
	 * @param changeType
	 */
	public KeywordUse(KeywordType type, KeywordContext context, String keyword,
			ChangeType changeType) {
		super(type, keyword);

		this.context = context;
		this.changeType = changeType;
	}

	/**
	 * To be used in the initial scan of the entire script for the purpose of
	 * building the API model for the class.
	 * @param type
	 * @param keyword
	 */
	public KeywordUse(KeywordType type, String keyword) {
		super(type, keyword);

		this.context = KeywordContext.UNKNOWN;
		this.changeType = ChangeType.UNKNOWN;
	}

	/**
	 * Set the package artifact that this keyword points to.
	 * 
	 * @param pointsto The package this keyword points to.
	 */
	public void setAPI(AbstractAPI api) {
		this.api = api;
	}

	@Override
	public boolean equals(Object obj) {

		if(obj instanceof KeywordUse) {
			KeywordUse that = (KeywordUse) obj;

			if(this.type == that.type && this.context == that.context &&
					this.keyword.equals(that.keyword) &&
					this.changeType == that.changeType) {
				return true;
			}
		}
		else if(obj instanceof KeywordDefinition) {
			KeywordDefinition that = (KeywordDefinition) obj;

			if(this.type == that.type && this.keyword.equals(that.keyword))
				return true;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return (this.type.toString() + "_" + this.keyword).hashCode();
	}

	@Override
	public String toString() {

		if(this.type == KeywordType.PACKAGE) {
			/* Don't print the package twice if the keyword type is a package. */
			return this.type.toString() + "_" + this.context.toString() + "_" +
				   this.changeType.toString() + "_" + this.keyword;
		}
		return this.type.toString() + "_" + this.context.toString() + "_" +
			   this.changeType.toString() + "_" + this.api.getName() + "_" + this.keyword;

	}

	/**
	 * The possible contexts for a keyword. Reads like "The keyword is being
	 * used in a ... ".
	 */
	public enum KeywordContext {
		UNKNOWN,
		CONDITION,
		EXPRESSION,
		ASSIGNMENT_LHS,
		ASSIGNMENT_RHS,
		REQUIRE,
		CLASS_DECLARATION,
		METHOD_DECLARATION,
		PARAMETER_DECLARATION,
		VARIABLE_DECLARATION,
		METHOD_CALL,
		ARGUMENT,
		PARAMETER,
		EXCEPTION_CATCH,
		EVENT_REGISTER,
		EVENT_REMOVE
	}

	/**
	 * Helper method to filter a list of keywords by ChangeType. It returns a
	 * new list with the filtered elements. Original list is kept untouched.
	 * TODO: Should this be on this class or should we create a
	 * KeywordListHelper or something?
	 *
	 * @param keywordsMap list of keywords
	 * @param changeTypes types of keywords that should stay
	 * @return a new list
	 */
	public static Map<KeywordUse, Integer> filterMapByChangeType(Map<KeywordUse, Integer> keywordsMap,
			ChangeType... changeTypes) {
		Map<KeywordUse, Integer> newList = new HashMap<KeywordUse, Integer>();

		for (Entry<KeywordUse, Integer> keywordEntry : keywordsMap.entrySet()) {
			if (Arrays.asList(changeTypes).contains(keywordEntry.getKey().changeType))
				newList.put(keywordEntry.getKey(), keywordEntry.getValue());
		}

		return newList;
	}
}
