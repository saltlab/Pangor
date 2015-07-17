package ca.ubc.ece.salt.sdjsb.analysis.learning.apis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode.ChangeType;

/**
 * Stores a keyword and the context under which it is used (which we call its type).
 */
public class Keyword {

	/** The type of the keyword (i.e., package, method, field, constant or event). **/
	public KeywordType type;

	/** The context under which the keyword is used. **/
	public KeywordContext context;

	/** The keyword text. **/
	public String keyword;

	/** How this keyword was modified from the source to the destination file. **/
	public ChangeType changeType;

	/** The AbstractAPI which contains this keyword. **/
	public AbstractAPI api;

	public Keyword(KeywordType type, KeywordContext context, String keyword, ChangeType changeType) {
		this.type = type;
		this.context = context;
		this.keyword = keyword;
		this.changeType = changeType;
		this.api = null;
	}

	public Keyword(KeywordType type, String keyword, AbstractAPI api) {
		this.type = type;
		this.keyword = keyword;
		this.api = api;
	}

	public Keyword(KeywordType type, String keyword) {
		this.type = type;
		this.keyword = keyword;
	}

	/**
	 * Return the package name of the API this Keywords belongs to. Just
	 * delegates the call method to the API.
	 *
	 * @return the package name, "global", or null
	 */
	public String getPackageName() {
		if (api != null)
			return api.getPackageName();

		return null;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Keyword) {
			Keyword that = (Keyword) obj;

			if(this.type == that.type && this.keyword.equals(that.keyword))
				return true;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}

	@Override
	public String toString() {
		return this.type.toString() + "_" + this.keyword;
	}

	/**
	 * The possible types for a keyword. Reads like "The keyword is a ... ".
	 */
	public enum KeywordType {
		UNKNOWN,
		RESERVED,
		PACKAGE,
		CLASS,
		METHOD,
		FIELD,
		CONSTANT,
		VARIABLE,
		EXCEPTION,
		EVENT
	}

	/**
	 * The possible contexts for a keyword. Reads like "The keyword is being
	 * used in a ... ".
	 */
	public enum KeywordContext {
		UNKNOWN,
		CONDITION,
		REQUIRE,
		METHOD_CALL,
		FIELD_ACCESS,
		CONSTANT_ACCESS,
		ARGUMENT,
		PARAMETER,
		EXCEPTION,
		EVENT
	}

	/**
	 * Helper method to filter a list of keywords by ChangeType. It returns a
	 * new list with the filtered elements. Original list is kept untouched.
	 * TODO: Should this be on this class or should we create a
	 * KeywordListHelper or something?
	 *
	 * @param keywords list of keywords
	 * @param changeTypes types of keywords that should stay
	 * @return a new list
	 */
	public static List<Keyword> filterListByChangeType(List<Keyword> keywords, ChangeType... changeTypes) {
		List<Keyword> newList = new ArrayList<Keyword>(keywords);

		for (Iterator<Keyword> iterator = newList.iterator(); iterator.hasNext();) {
			Keyword keyword = iterator.next();

			if (!Arrays.asList(changeTypes).contains(keyword.changeType))
				iterator.remove();
		}

		return newList;
	}

}