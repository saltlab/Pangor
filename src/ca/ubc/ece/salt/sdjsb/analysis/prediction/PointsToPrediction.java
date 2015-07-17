package ca.ubc.ece.salt.sdjsb.analysis.prediction;

import java.util.List;
import java.util.Map;

import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode.ChangeType;
import ca.ubc.ece.salt.sdjsb.analysis.learning.apis.AbstractAPI;
import ca.ubc.ece.salt.sdjsb.analysis.learning.apis.KeywordDefinition;
import ca.ubc.ece.salt.sdjsb.analysis.learning.apis.KeywordDefinition.KeywordType;
import ca.ubc.ece.salt.sdjsb.analysis.learning.apis.KeywordUse;
import ca.ubc.ece.salt.sdjsb.analysis.learning.apis.KeywordUse.KeywordContext;
import ca.ubc.ece.salt.sdjsb.analysis.learning.apis.PackageAPI;
import ca.ubc.ece.salt.sdjsb.analysis.learning.apis.TopLevelAPI;

/**
 * Predicts the points-to relationships for all keywords (methods, fields,
 * events, etc.) based on the use patterns of all the keywords.
 */
public class PointsToPrediction {
	protected TopLevelAPI api;

	/**
	 * Build the model for predicting points-to relationships.
	 * @param insertedKeywords
	 * @param removedKeywords
	 * @param updatedKeywords
	 * @param unchangedKeywords
	 */
	public PointsToPrediction(TopLevelAPI api, Map<KeywordUse, Integer> keywords) {
		
		this.api = api;
	}

	/** Returns the most likely API that the keyword points to. **/
	public KeywordUse getKeyword(KeywordType type, KeywordContext context, 
			String token, ChangeType changeType) {

		/*
		 * On this draft implementation, return the first API which 
		 * has the keyword
		 */
		
		KeywordUse keyword = new KeywordUse(type, context, token, changeType);
		
		for (PackageAPI pack : api.getPackages()) {
			if (pack.isMemberOf(keyword)) {
				keyword.setPointsTo(pack);
				return keyword;
			}
		}
		
		return null;
	}

	/** Returns a list of APIs that are likely used in this method. **/
	public List<AbstractAPI> getAPIsUsed(Map<KeywordDefinition, Integer> insertedKeywords,
			   Map<KeywordDefinition, Integer> removedKeywords,
			   Map<KeywordDefinition, Integer> updatedKeywords,
			   Map<KeywordDefinition, Integer> unchangedKeywords) {
		return null;
	}

	/**
	 * Returns a list of APIs that are likely involved in a method's repair.
	 **/
	public List<AbstractAPI> getAPIsInRepair(Map<KeywordDefinition, Integer> insertedKeywords,
			   Map<KeywordDefinition, Integer> removedKeywords,
			   Map<KeywordDefinition, Integer> updatedKeywords,
			   Map<KeywordDefinition, Integer> unchangedKeywords) {
		return null;
	}

}