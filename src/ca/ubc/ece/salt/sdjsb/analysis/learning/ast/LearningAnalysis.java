package ca.ubc.ece.salt.sdjsb.analysis.learning.ast;

import java.util.Map;

import org.mozilla.javascript.ast.ScriptNode;

import ca.ubc.ece.salt.sdjsb.alert.FeatureVectorAlert;
import ca.ubc.ece.salt.sdjsb.analysis.meta.MetaAnalysis;
import ca.ubc.ece.salt.sdjsb.analysis.scope.Scope;

public class LearningAnalysis extends MetaAnalysis<LearningASTAnalysis, LearningASTAnalysis> {

	public LearningAnalysis() {
		super(new LearningASTAnalysis(), new LearningASTAnalysis());
	}

	@Override
	protected void synthesizeAlerts() {
		
		/* TODO: Here would be a good place to initialize the points-to analysis. */
		
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
			
			if(!dstFeatureVector.isEmpty()) {
				this.registerAlert(new FeatureVectorAlert(dstFeatureVector));
			}
		}
		
	}

}
