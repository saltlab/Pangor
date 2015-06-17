package ca.ubc.ece.salt.sdjsb.analysis.learning;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mozilla.javascript.ast.AstNode;

import ca.ubc.ece.salt.sdjsb.alert.FeatureVectorAlert;
import ca.ubc.ece.salt.sdjsb.analysis.meta.MetaAnalysis;
import ca.ubc.ece.salt.sdjsb.cfg.CFGNode;

public class LearningAnalysis extends MetaAnalysis<LearningFlowAnalysis, LearningFlowAnalysis> {

	public LearningAnalysis() {
		super(new LearningFlowAnalysis(), new LearningFlowAnalysis());
	}

	@Override
	protected void synthesizeAlerts() {
		
		/* Anti-patterns. */
		Map<String, List<PathFragment>> srcPathFragments = this.srcAnalysis.getPathFragments();
		Map<String, List<AstNode>> srcInterestingConditions = this.srcAnalysis.getInterestingConditions();
		
		/* Possible repair that adds callback error handling. */
		Map<String, List<PathFragment>> dstPathFragments = this.dstAnalysis.getPathFragments();
		Map<String, List<AstNode>> dstInterestingConditions = this.dstAnalysis.getInterestingConditions();
		
		List<String> identities = new LinkedList<String>(dstPathFragments.keySet());
		identities.addAll(dstInterestingConditions.keySet());
		
		/* Make a feature set by extracting features for each function. */
		for(String identity : identities) {
			
			FeatureVector fv = new FeatureVector();
			
			fv.functionName = identity;
			
			/* Get the features from the interesting fragments. */
			if(dstPathFragments.containsKey(identity)) {

				List<PathFragment> fragments = dstPathFragments.get(identity);

				double avgFragmentLength = 0;
				int maxFragmentLength = 0;

				fv.numberOfInterestingFragments = fragments.size();
				
				/* Get features from the individual fragments. */
				for(PathFragment fragment : fragments) {

					avgFragmentLength += fragment.path.size();
					if(fragment.path.size() > maxFragmentLength) maxFragmentLength = fragment.path.size();
					
					if(fragment.condition != null) KeywordVisitor.getKeywords(fragment.condition, fv.keywordsInFragments);
					
					/* Get features from the nodes in the fragment path. */
					for(CFGNode node : fragment.path) {
						AstNode statement = (AstNode) node.getStatement();
						
						/* Get the statement type. */
						fv.addInterestingStatement(statement);
						
						/* Get the keywords for the statement. */
						KeywordVisitor.getKeywords(statement, fv.keywordsInFragments);
					}
					
				}
				
				fv.maxInterestingFragmentSize = maxFragmentLength;
				fv.avgInterestingFragmentSize = avgFragmentLength / fragments.size();

			}
			
			/* Get the features from the interesting conditions. */
			if(dstInterestingConditions.containsKey(identity)) {

				List<AstNode> conditions = dstInterestingConditions.get(identity);
				fv.numberOfInterestingConditions = conditions.size();

			}
			
			this.registerAlert(new FeatureVectorAlert(fv));
			
		}
		
	}

}
