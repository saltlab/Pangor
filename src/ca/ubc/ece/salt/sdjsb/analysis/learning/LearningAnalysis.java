package ca.ubc.ece.salt.sdjsb.analysis.learning;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mozilla.javascript.ast.AstNode;

import ca.ubc.ece.salt.sdjsb.analysis.meta.MetaAnalysis;

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
		
		Set<String> identities = dstPathFragments.keySet();
		identities.addAll(dstInterestingConditions.keySet());
		
		System.out.println("\nInteresting Functions:");
		for(String identity : identities) { 
			System.out.println("\t" + identity);
		}
		
	}

}
