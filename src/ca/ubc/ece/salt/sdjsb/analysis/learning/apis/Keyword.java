package ca.ubc.ece.salt.sdjsb.analysis.learning.apis;

/**
 * Stores a keyword and the context under which it is used (which we call its type).
 */
public class Keyword {
	
	/** The context under which the keyword is used. **/
	public KeywordType type;
	
	/** The keyword text. **/
	public String keyword;
	
	public Keyword(KeywordType type, String keyword) {
		this.type = type;
		this.keyword = keyword;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Keyword) {
			Keyword that = (Keyword) obj;
			if(this.type == that.type && this.keyword == that.keyword) return true;
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
	 * The possible types for a keyword.
	 */
	public enum KeywordType {
		KEYWORD,
		PACKAGE,
		CLASS,
		METHOD_CALL,
		METHOD_NAME,
		FIELD,
		CONSTANT,
		ARGUMENT,
		PARAMETER,
		EXCEPTION,
		EVENT
	}

}