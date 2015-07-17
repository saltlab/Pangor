package ca.ubc.ece.salt.sdjsb.analysis.learning.apis;

import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode.ChangeType;

public class KeywordUse extends KeywordDefinition {

	/** The context under which the keyword is used. **/
	public KeywordContext context;

	/** How this keyword was modified from the source to the destination file. **/
	public ChangeType changeType;
	
	/** The AbstractAPI which contains this keyword. **/
	public AbstractAPI pointsto;
	
	/**
	 * @param type
	 * @param context
	 * @param keyword
	 * @param changeType
	 */
	public KeywordUse(KeywordType type, KeywordContext context, String keyword,
			ChangeType changeType) {
		super(type, keyword);

		this.pointsto = null;
		this.context = context;
		this.changeType = changeType;
	}
	
	/**
	 * @param type
	 * @param keyword
	 */
	public KeywordUse(KeywordType type, String keyword) {
		super(type, keyword);

		this.pointsto = null;
		this.context = KeywordContext.UNKNOWN;
		this.changeType = ChangeType.UNKNOWN;
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
		return this.type.toString() + "_" + this.context.toString() + "_" + 
			   this.changeType.toString() + "_" + this.keyword;
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

}
