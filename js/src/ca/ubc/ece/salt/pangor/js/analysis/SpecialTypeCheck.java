package ca.ubc.ece.salt.pangor.js.analysis;

import ca.ubc.ece.salt.pangor.js.analysis.SpecialTypeAnalysisUtilities.SpecialType;

/**
 * Stores the details of a special typ check in a conditional.
 */
public class SpecialTypeCheck {

	public String identifier;
	public SpecialType specialType;
	public boolean isSpecialType;

	public SpecialTypeCheck(String identifier, SpecialType specialType, boolean isSpecialType) {
		this.identifier = identifier;
		this.specialType = specialType;
		this.isSpecialType = isSpecialType;
	}

}