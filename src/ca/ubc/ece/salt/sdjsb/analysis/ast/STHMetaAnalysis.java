package ca.ubc.ece.salt.sdjsb.analysis.ast;

import java.util.Set;

import ca.ubc.ece.salt.sdjsb.alert.SpecialTypeAlert;
import ca.ubc.ece.salt.sdjsb.analysis.ast.STHScopeAnalysis.SpecialTypeCheckResult;
import ca.ubc.ece.salt.sdjsb.analysis.meta.MetaAnalysis;

public class STHMetaAnalysis extends MetaAnalysis<STHScopeAnalysis, STHScopeAnalysis> {

	public STHMetaAnalysis() {
		super(new STHScopeAnalysis(), new STHScopeAnalysis());
	}

	@Override
	protected void synthesizeAlerts() {

		/* Anti-patterns. */
		Set<SpecialTypeCheckResult> antiPatterns = this.srcAnalysis.getSpecialTypeCheckResults();
		
		/* Possible repair that adds callback error handling. */
		Set<SpecialTypeCheckResult> repairs = this.dstAnalysis.getSpecialTypeCheckResults();
		
		for(SpecialTypeCheckResult repair : repairs) {
			
			if(!antiPatterns.contains(repair)) {
				
				/* Register an alert. */
				this.registerAlert(new SpecialTypeAlert("AST_STH", repair.identifier, repair.specialType));
				
			}
			
		}
		
	}

}
