package ca.ubc.ece.salt.sdjsb.analysis.notdefined;

import java.util.LinkedList;

import org.mozilla.javascript.ast.AstNode;

import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode.ChangeType;
import ca.ubc.ece.salt.sdjsb.alert.NotDefinedAlert;
import ca.ubc.ece.salt.sdjsb.analysis.meta.MetaAnalysis;
import ca.ubc.ece.salt.sdjsb.analysis.notdefined.NotDefinedDestinationAnalysis.GlobalToLocal;
import ca.ubc.ece.salt.sdjsb.analysis.scope.Scope;
import ca.ubc.ece.salt.sdjsb.analysis.scope.ScopeAnalysis;

public class NotDefinedAnalysis extends MetaAnalysis<ScopeAnalysis, NotDefinedDestinationAnalysis> {

	public NotDefinedAnalysis() {
		super(new ScopeAnalysis(), new NotDefinedDestinationAnalysis());
	}

	/**
	 * Synthesized alerts by inspecting the results of the scope analysis on
	 * the source file and the not defined analysis on the destination file.
	 */
	@Override
	protected void synthesizeAlerts() {

		for(GlobalToLocal gtl : new LinkedList<GlobalToLocal>(this.dstAnalysis.notDefinedRepairs)) {
			
			/* Remove identifiers that were deleted in the source scope.
			 * Check the entire scope tree. this reduces false positives from
			 * methods which are renamed. */
			
			Scope srcScope = this.srcAnalysis.getDstScope();
			
			if(this.deletedInScope(srcScope, gtl.identifier)) {
				this.dstAnalysis.notDefinedRepairs.remove(gtl);
			}
			
		}
	
		/* Generate alerts for the remaining GlobalToLocal elements. */
		for(GlobalToLocal gtl : this.dstAnalysis.notDefinedRepairs) {
			this.registerAlert(new NotDefinedAlert("ND", gtl.identifier));
		}

	}
	
	/**
	 * Determines if the identifier was deleted in some source scope.
	 * @param scope The source scope to check.
	 * @param notDefinedRepairs The list of potential not defined repairs.
	 * @return true if the identifier was deleted in the scope or a method scope.
	 */
	private boolean deletedInScope(Scope scope, String identifier) {
		
		AstNode node = scope.variables.get(identifier);
		if(node != null && node.getChangeType() == ChangeType.REMOVED) return true;
		
		for(Scope child : scope.children) {
			if(deletedInScope(child, identifier)) return true;
		}
		
		return false;
		
	}

}
