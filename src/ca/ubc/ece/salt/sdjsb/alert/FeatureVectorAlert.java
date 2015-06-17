package ca.ubc.ece.salt.sdjsb.alert;

import ca.ubc.ece.salt.sdjsb.analysis.learning.FeatureVector;

public class FeatureVectorAlert extends Alert {
	
	/** Feature vector from the function analysis. **/
	public FeatureVector featureVector;

	public FeatureVectorAlert(FeatureVector featureVector) {
		super("FEATURE_VECTOR", "BoW");
		this.featureVector = featureVector;
	}
	
	@Override 
	public String getCustomDescription() {
		return this.featureVector.toString();
	}

	@Override
	protected String getAlertDescription() {
		return this.featureVector.functionName;
	}

	@Override
	protected String getAlertExplanation() {
		return "This alert stores a feature vector from the analysis of one function. These feature vectors can be used to mine repair patterns.";
	}

}
