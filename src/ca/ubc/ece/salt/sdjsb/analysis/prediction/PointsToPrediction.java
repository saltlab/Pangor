package ca.ubc.ece.salt.sdjsb.analysis.prediction;

import java.util.List;
import java.util.Map;

import org.mozilla.javascript.ast.AstRoot;

import ca.ubc.ece.salt.sdjsb.analysis.learning.apis.AbstractAPI;
import ca.ubc.ece.salt.sdjsb.analysis.learning.apis.Keyword;
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
	 */
	public PointsToPrediction(TopLevelAPI api, Map<Keyword, Integer> insertedKeywords,
			   Map<Keyword, Integer> removedKeywords,
			   Map<Keyword, Integer> updatedKeywords,
			   Map<Keyword, Integer> unchangedKeywords) {
		this.api = api;
	}

	/** Returns the most likely API that the keyword points to. **/
	public PackageAPI getLikelyAPI(Keyword keyword) {
		/*
		 * On this draft implementation, return the first API which has the keyword
		 */
		
		for (PackageAPI pack : api.getPackages()) {
			if (pack.isMemberOf(keyword))
				return pack;
		}
		
		return null;
	}

	/** Returns a list of APIs that are likely used in this method. **/
	public List<AbstractAPI> getAPIsUsed(AstRoot methodRoot) {
		return null;
	}

	/**
	 * Returns a list of APIs that are likely involved in a method's repair.
	 **/
	public List<AbstractAPI> getAPIsInRepair(AstRoot methodRoot) {
		return null;
	}

}