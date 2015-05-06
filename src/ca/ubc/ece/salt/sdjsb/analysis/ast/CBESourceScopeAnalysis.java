package ca.ubc.ece.salt.sdjsb.analysis.ast;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.IfStatement;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.NodeVisitor;

import ca.ubc.ece.salt.sdjsb.analysis.AnalysisUtilities;
import ca.ubc.ece.salt.sdjsb.analysis.callbackerror.CallbackErrorCheck;
import ca.ubc.ece.salt.sdjsb.analysis.scope.Scope;
import ca.ubc.ece.salt.sdjsb.analysis.scope.ScopeAnalysis;
import ca.ubc.ece.salt.sdjsb.analysis.specialtype.SpecialTypeCheck;
import ca.ubc.ece.salt.sdjsb.analysis.specialtype.SpecialTypeVisitor;
import ca.ubc.ece.salt.sdjsb.cfg.CFG;

/**
 * Performs an AST-only callback error handling analysis using AST visitors.
 * 
 * This classifier is used for evaluation purposes only and should not be used
 * in actual data mining. Instead, use the CallbackErrorAnalysis classifier.
 */
public class CBESourceScopeAnalysis extends ScopeAnalysis {

	/** Stores the possible callback error check repairs. */
	private Set<CallbackErrorCheck> callbackErrorChecks;
	
	public CBESourceScopeAnalysis() {
		super();
		this.callbackErrorChecks = new HashSet<CallbackErrorCheck>();
	}

	/**
	 * @return The set of possible callback error check repairs (or
	 * anti-patterns if this is the source file analysis.
	 */
	public Set<CallbackErrorCheck> getCallbackErrorChecks() {
		return this.callbackErrorChecks;
	}

	@Override
	public void analyze(AstRoot root, List<CFG> cfgs) throws Exception {
		
		super.analyze(root, cfgs);
		
		/* Look at each function. */
		this.inspectFunctions(this.dstScope);

	}

	@Override
	public void analyze(AstRoot srcRoot, List<CFG> srcCFGs, AstRoot dstRoot,
			List<CFG> dstCFGs) throws Exception {

		super.analyze(srcRoot, srcCFGs, dstRoot, dstCFGs);

		/* Look at each function. */
		this.inspectFunctions(this.dstScope);

	}

	/**
	 * 
	 * @param scope The function to inspect.
	 */
	private void inspectFunctions(Scope scope) {

		/* If this is the script (no parameters) or the function was inserted, there is nothing to do. */
		if(scope.scope instanceof FunctionNode) {
		
            /* Look through the parameters to see if there is an unchanged error parameter. */
            Set<String> parameters = new HashSet<String>();
            FunctionNode function = (FunctionNode) scope.scope;
            for(AstNode parameter : function.getParams()) {
                if(parameter instanceof Name) {
                    
                    /* Match only error parameters for now. */
                    Name name = (Name) parameter;
                    if(name.getIdentifier().matches("(?i)e(rr(or)?)?")) {
                        
                        /* Add the parameter to the set of parameters to look for. */
                        parameters.add(name.getIdentifier());

                    }

                }
            }
            
            /* Visit the function and look for CBE patterns. */
            CBEScopeAnalysisVisitor visitor = new CBEScopeAnalysisVisitor(scope, parameters, function.getName(), AnalysisUtilities.getFunctionSignature(function));
            function.getBody().visit(visitor);

		}
		
		/* Visit the child functions. */
		for(Scope child : scope.children) {
			inspectFunctions(child);
		}
		
	}
	
	/**
	 * Visits if statements and finds new special type checks.
	 */
	private class CBEScopeAnalysisVisitor implements NodeVisitor {
		
		private Scope scope;
		private Set<String> parameters;
		private String function;
		private String signature;
		
		public CBEScopeAnalysisVisitor(Scope scope, Set<String> parameters, String function, String signature) {
			this.scope = scope;
			this.parameters = parameters;
			this.function = function;
			this.signature = signature;
		}

		@Override
		public boolean visit(AstNode node) {
			
			if(node instanceof IfStatement) {
				
				IfStatement is = (IfStatement) node;
				
				/* Get the special type checks in the if statement. */
				List<SpecialTypeCheck> specialTypeChecks = SpecialTypeVisitor.getSpecialTypeChecks(is.getCondition(), false);
				
				for(SpecialTypeCheck specialTypeCheck : specialTypeChecks) {
					if(this.parameters.contains(specialTypeCheck.identifier)) {
						CBESourceScopeAnalysis.this.callbackErrorChecks.add(new CallbackErrorCheck(this.scope, this.function, this.signature, specialTypeCheck.identifier, specialTypeCheck.specialType));
					}
				}
				
			}

			else if(node instanceof FunctionNode) {
				return false;
			}
			
			return true;

		}
		
	}

}
