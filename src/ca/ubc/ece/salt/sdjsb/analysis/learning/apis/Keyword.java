package ca.ubc.ece.salt.sdjsb.analysis.learning.apis;

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
	public AbstractAPI pointsto;
	
	public Keyword(KeywordType type, KeywordContext context, String keyword, ChangeType changeType) {
		this.pointsto = null;
		this.type = type;
		this.context = context;
		this.keyword = keyword;
		this.changeType = changeType;
	}
	
	/**
	 * Set the package artifact that this keyword points to.
	 * @param pointsto The package this keyword points to.
	 */
	public void setPointsTo(AbstractAPI pointsto) {
		this.pointsto = pointsto;
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

}