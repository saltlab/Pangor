package ca.ubc.ece.salt.sdjsb.classify.alert;

import ca.ubc.ece.salt.sdjsb.analysis.learning.FeatureVector;
import ca.ubc.ece.salt.sdjsb.batch.AnalysisMetaInformation;

public class FeatureVectorAlert extends ClassifierAlert {

	/** Feature vector from the function analysis. **/
	public FeatureVector featureVector;

	public FeatureVectorAlert(AnalysisMetaInformation ami, String functionName, FeatureVector featureVector) {
		super(ami, functionName, "FEATURE_VECTOR", "BoW");
		this.featureVector = featureVector;
	}

	@Override
	public String getID() {
		return String.valueOf(this.featureVector.id);
	}

	@Override
	public String getSourceCode() {
		return this.featureVector.ami.buggyCode;
	}

	@Override
	public String getDestinationCode() {
		return this.featureVector.ami.repairedCode;
	}

	@Override
	public String getFeatureVector(String project, String sourceFile, String destinationFile, String buggyCommit, String repairedCommit) {
		/* TODO: We need to go through the FeatureVectorManager to print alerts. */
		throw new UnsupportedOperationException();
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
