package ca.ubc.ece.salt.sdjsb.analysis.specialtype;

import java.util.Set;

import ca.ubc.ece.salt.sdjsb.alert.SpecialTypeAlert;
import ca.ubc.ece.salt.sdjsb.analysis.meta.MetaAnalysis;
import ca.ubc.ece.salt.sdjsb.analysis.specialtype.SpecialTypeFlowAnalysis.SpecialTypeCheckResult;

public class SpecialTypeAnalysis extends MetaAnalysis<SpecialTypeFlowAnalysis, SpecialTypeFlowAnalysis> {

	public SpecialTypeAnalysis() {
		super(new SpecialTypeFlowAnalysis(), new SpecialTypeFlowAnalysis());
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
				this.registerAlert(new SpecialTypeAlert("STH", repair.identifier, repair.specialType));
				
			}
			
		}
		
	}

}
