package ca.ubc.ece.salt.sdjsb.analysis.learning;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode.ChangeType;
import ca.ubc.ece.salt.sdjsb.analysis.Alert;
import ca.ubc.ece.salt.sdjsb.batch.AnalysisMetaInformation;
import ca.ubc.ece.salt.sdjsb.learning.apis.KeywordDefinition;
import ca.ubc.ece.salt.sdjsb.learning.apis.KeywordDefinition.KeywordType;
import ca.ubc.ece.salt.sdjsb.learning.apis.KeywordUse;
import ca.ubc.ece.salt.sdjsb.learning.apis.KeywordUse.KeywordContext;

/**
 * Stores a feature vector (a row) of the repair pattern learning data set.
 *
 * The feature vector includes meta information and keyword counts. The meta
 * information (e.g., project, commit #, file names, etc.) are used to explore
 * the repair patterns once they are discovered through data mining. The
 * keyword counts are used to discover the repair patterns.
 */
public class FeatureVector extends Alert {

	/** The keyword counts in each fragment. **/
	public Map<KeywordUse, Integer> keywordMap;

	public FeatureVector(AnalysisMetaInformation ami, String functionName) {
		super(ami, functionName);
		this.keywordMap = new HashMap<KeywordUse, Integer>();
	}

	/**
	 * Joins a source feature vector with this (the destination) feature vector.
	 * @param source The source feature vector.
	 */
	public void join(FeatureVector source) {
		this.ami.buggyCode = source.ami.buggyCode;

		/* Insert the keywords form the source feature vector with change type
		 * REMOVED into this feature vector. */
		for(KeywordUse keyword : source.keywordMap.keySet()) {
			if(keyword.changeType == ChangeType.REMOVED) {
				this.keywordMap.put(keyword, source.keywordMap.get(keyword));
			}
		}
	}

	/**
	 * If the given token is a keyword, that keyword's count is incremented by
	 * one.
	 * @param token The string to check against the keyword list.
	 */
	public void addKeyword(KeywordUse keyword) {

		Integer count = this.keywordMap.containsKey(keyword) ? this.keywordMap.get(keyword) + 1 : 1;
		this.keywordMap.put(keyword,  count);

	}

	/**
	 * Add the keyword to the feature vector and set its count.
	 * @param token The string to check against the keyword list.
	 */
	public void addKeyword(KeywordUse keyword, Integer count) {

		this.keywordMap.put(keyword,  count);

	}

	/**
	 * This method serializes the feature vector. This is useful when writing
	 * a data set to the disk.
	 * @return The serialized version of the feature vector.
	 */
	public String serialize() {

		String serialized = id + "," + this.ami.projectID + "," + this.ami.projectHomepage
				+ "," + this.ami.buggyFile + "," + this.ami.repairedFile
				+ "," + this.ami.buggyCommitID + "," + this.ami.repairedCommitID
				+ "," + this.functionName;

		for(KeywordUse keyword : this.keywordMap.keySet()) {
			Integer uses = this.keywordMap.get(keyword);
			serialized += "," + keyword.type + ":" + keyword.context + ":" + keyword.changeType + ":" + keyword.getPackageName() + ":" + keyword.keyword + ":" + uses;
		}

		return serialized;

	}

	/**
	 * This method de-serializes a feature vector. This is useful when reading
	 * a data set from the disk.
	 * @param serialized The serialized version of a feature vector.
	 * @return The feature vector represented by {@code serialized}.
	 */
	public static FeatureVector deSerialize(String serialized) throws Exception {

		String[] features = serialized.split(",");

		if(features.length < 7) throw new Exception("De-serialization exception. Serial format not recognized.");

		AnalysisMetaInformation ami = new AnalysisMetaInformation(-1, -1,
				features[1], features[2], features[3], features[4], features[5],
				features[6], null, null);

		FeatureVector featureVector = new FeatureVector(ami, features[7]);

		for(int i = 8; i < features.length; i++) {
			String[] feature = features[i].split(":");
			if(feature.length < 6) throw new Exception("De-serialization exception. Serial format not recognized.");
			KeywordUse keyword = new KeywordUse(KeywordType.valueOf(feature[0]),
												KeywordContext.valueOf(feature[1]),
												feature[4],
												ChangeType.valueOf(feature[2]), feature[3]);
			featureVector.addKeyword(keyword, Integer.parseInt(feature[5]));
		}

		return featureVector;

	}

	/**
	 * Prints the meta features and the specified keyword values in the order they are provided.
	 * @param keywords An ordered list of the keywords to print in the feature vector.
	 * @return the CSV row (the feature vector) as a string.
	 */
	public String getFeatureVector(Set<KeywordDefinition> keywords) {

		String vector = id + "," + this.ami.projectID + "," + this.ami.projectHomepage + ","
				+ this.ami.buggyFile + "," + this.ami.repairedFile
				+ "," + this.ami.buggyCommitID + "," + this.ami.repairedCommitID + "," + this.functionName;

		for(KeywordDefinition keyword : keywords) {
			if(this.keywordMap.containsKey(keyword)) vector += "," + this.keywordMap.get(keyword).toString();
			else vector += ",0";
		}

		return vector;

	}

	/**
	 * @return The source code for the alert.
	 */
	public String getSource() {
		return this.ami.buggyCode;
	}

	/**
	 * @return The destination code for the alert.
	 */
	public String getDestination() {
		return this.ami.repairedCode;
	}

}