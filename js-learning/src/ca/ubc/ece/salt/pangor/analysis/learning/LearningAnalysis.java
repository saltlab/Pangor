package ca.ubc.ece.salt.pangor.analysis.learning;

import java.util.Map;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.ScriptNode;

import ca.ubc.ece.salt.pangor.analysis.MetaAnalysis;
import ca.ubc.ece.salt.pangor.analysis.scope.Scope;
import ca.ubc.ece.salt.pangor.batch.AnalysisMetaInformation;
import ca.ubc.ece.salt.pangor.js.analysis.scope.JavaScriptScope;

/**
 * Creates a data set for learning bug and repair patterns related to the use
 * of Node.js package APIs. This class will produce one feature vector for each
 * function in the analyzed script with a source and destination function.
 */
public class LearningAnalysis extends MetaAnalysis<FeatureVector, LearningDataSet,
	LearningASTAnalysis, LearningASTAnalysis> {

	/**
	 * The FeatureVectorManager performs pre-processing tasks for data
	 * mining (i.e., row and column filtering).
	 */
	private LearningDataSet featureVectorManager;

	/**
	 * The maximum change complexity for the file. If the change complexity for
	 * a file is greater than the maximum change complexity, the file is not
	 * analyzed and no feature vectors are generated.
	 */
	private int maxChangeComplexity;

	/**
	 * @param featureVectorManager the manager that stores the feature vectors produced by this analysis.
	 * @param ami The meta info for the analysis (i.e., project id, file paths, commit IDs, etc.).
	 */
	public LearningAnalysis(LearningDataSet featureVectorManager, AnalysisMetaInformation ami, int maxChangeComplexity) {
		super(featureVectorManager, ami,
				new LearningASTAnalysis(featureVectorManager, ami, maxChangeComplexity),
				new LearningASTAnalysis(featureVectorManager, ami, maxChangeComplexity));
		this.featureVectorManager = featureVectorManager;
		this.maxChangeComplexity = maxChangeComplexity;
	}

	@Override
	protected void synthesizeAlerts() throws Exception {

		/* Source analysis. */
		Map<Scope<AstNode>, FeatureVector> srcFeatureVectors = this.srcAnalysis.getFeatureVectors();

		/* Destination analysis. */
		Map<Scope<AstNode>, FeatureVector> dstFeatureVectors = this.dstAnalysis.getFeatureVectors();

		/* Check that the change complexity falls within the max. */
		if(this.srcAnalysis.getChangeComplexity() <= this.maxChangeComplexity
				&& this.dstAnalysis.getChangeComplexity() <= this.maxChangeComplexity) {

			/* Synthesize the alerts. */
			for(Scope<AstNode> dstScope : dstFeatureVectors.keySet()) {

				/* Get the source scope that maps to the destination scope. */
				JavaScriptScope srcScope;
				if(dstScope.getScope().getMapping() != null) {
					srcScope = this.srcAnalysis.getDstScope((ScriptNode)dstScope.getScope().getMapping());
				}
				else {
					srcScope = this.srcAnalysis.getDstScope();
				}

				FeatureVector srcFeatureVector = srcFeatureVectors.get(srcScope);
				FeatureVector dstFeatureVector = dstFeatureVectors.get(dstScope);

				/* Get the 'removed' statements and keywords from the source feature vector. */
				dstFeatureVector.join(srcFeatureVector);

				/* Add the feature vector to the FeatureVectorManager. */
				this.featureVectorManager.registerAlert(dstFeatureVector);

			}

		}

	}

}
