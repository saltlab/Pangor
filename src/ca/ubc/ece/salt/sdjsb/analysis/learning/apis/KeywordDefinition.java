package ca.ubc.ece.salt.sdjsb.analysis.learning.apis;

/**
 * Stores a keyword and the context under which it is used (which we call its type).
 */
public class KeywordDefinition {

	/** The type of the keyword (i.e., package, method, field, constant or event). **/
	public KeywordType type;

	/** The keyword text. **/
	public String keyword;

	/** The AbstractAPI which contains this keyword. **/
	public AbstractAPI api;

	/**
	 * @param type
	 * @param keyword
	 */
	public KeywordDefinition(KeywordType type, String keyword) {
		this.type = type;
		this.keyword = keyword;
	}

	/**
	 * @param type
	 * @param keyword
	 */
	public KeywordDefinition(KeywordType type, String keyword, AbstractAPI api) {
		this(type, keyword);
		this.api = api;
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
		if(obj instanceof KeywordDefinition) {
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
		PARAMETER,
		EXCEPTION,
		EVENT
	}

}