package ca.ubc.ece.salt.sdjsb.analysis.learning.ast;

import java.util.Map;

import org.mozilla.javascript.ast.ScriptNode;

import ca.ubc.ece.salt.sdjsb.analysis.meta.MetaAnalysis;
import ca.ubc.ece.salt.sdjsb.analysis.scope.Scope;
import ca.ubc.ece.salt.sdjsb.batch.AnalysisMetaInformation;

/**
 * Creates a data set for learning bug and repair patterns related to the use
 * of Node.js package APIs. This class will produce one feature vector for each
 * function in the analyzed script with a source and destination function.
 */
public class LearningAnalysis extends MetaAnalysis<LearningASTAnalysis, LearningASTAnalysis> {
	
	/** 
	 * The FeatureVectorManager performs pre-processing tasks for data
	 * mining (i.e., row and column filtering). 
	 */
	private LearningDataSet featureVectorManager; 
	
	/**
	 * The meta info for the analysis (i.e., project id, file paths, commit IDs, etc.).
	 */
	private AnalysisMetaInformation ami;
	

	/**
	 * @param featureVectorManager the manager that stores the feature vectors produced by this analysis.
	 * @param ami The meta info for the analysis (i.e., project id, file paths, commit IDs, etc.).
	 */
	public LearningAnalysis(LearningDataSet featureVectorManager, AnalysisMetaInformation ami) {
		super(new LearningASTAnalysis(), new LearningASTAnalysis());
		this.featureVectorManager = featureVectorManager;
		this.ami = ami;
	}

	@Override
	protected void synthesizeAlerts() {
		
		/* Source analysis. */
		Map<Scope, FeatureVector> srcFeatureVectors = this.srcAnalysis.getFeatureVectors();
		
		/* Destination analysis. */
		Map<Scope, FeatureVector> dstFeatureVectors = this.dstAnalysis.getFeatureVectors();
		
		/* Combine the source and destination analyses. */
		
		/* Synthesize the alerts. */
		for(Scope dstScope : dstFeatureVectors.keySet()) {
			
			/* Get the source scope that maps to the destination scope. */
			Scope srcScope;
			if(dstScope.scope.getMapping() != null) {
				srcScope = this.srcAnalysis.getDstScope((ScriptNode)dstScope.scope.getMapping());
			}
			else {
				srcScope = this.srcAnalysis.getDstScope();
			}

			FeatureVector srcFeatureVector = srcFeatureVectors.get(srcScope);
			FeatureVector dstFeatureVector = dstFeatureVectors.get(dstScope);
			
			/* Get the 'removed' statements and keywords from the source feature vector. */
			dstFeatureVector.join(srcFeatureVector);
			
			/* Set the meta info for the feature vector. */
			dstFeatureVector.projectID = ami.projectID;
			dstFeatureVector.buggyFile = ami.buggyFile;
			dstFeatureVector.repairedFile = ami.repairedFile;
			dstFeatureVector.buggyCommitID = ami.buggyCommitID;
			dstFeatureVector.repairedCommitID = ami.repairedCommitID;
			dstFeatureVector.sourceCode = ami.buggyCode;
			dstFeatureVector.destinationCode = ami.repairedCode;
			
			/* Add the feature vector to the FeatureVectorManager. */
			this.featureVectorManager.registerFeatureVector(dstFeatureVector);

		}
		
	}

}
