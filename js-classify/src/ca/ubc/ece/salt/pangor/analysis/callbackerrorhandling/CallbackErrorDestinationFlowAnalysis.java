package ca.ubc.ece.salt.pangor.analysis.callbackerrorhandling;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.ScriptNode;

import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode.ChangeType;
import ca.ubc.ece.salt.pangor.analysis.classify.ClassifierDataSet;
import ca.ubc.ece.salt.pangor.analysis.flow.PathInsensitiveFlowAnalysis;
import ca.ubc.ece.salt.pangor.analysis.scope.Scope;
import ca.ubc.ece.salt.pangor.analysis.specialtype.SpecialTypeVisitor;
import ca.ubc.ece.salt.pangor.batch.AnalysisMetaInformation;
import ca.ubc.ece.salt.pangor.cfg.CFGEdge;
import ca.ubc.ece.salt.pangor.cfg.CFGNode;
import ca.ubc.ece.salt.pangor.classify.alert.ClassifierAlert;
import ca.ubc.ece.salt.pangor.js.analysis.AnalysisUtilities;
import ca.ubc.ece.salt.pangor.js.analysis.SpecialTypeCheck;

public class CallbackErrorDestinationFlowAnalysis extends PathInsensitiveFlowAnalysis<ClassifierAlert, ClassifierDataSet, CallbackErrorLatticeElement> {

	/** Stores the possible callback error check repairs. */
	private Set<CallbackErrorCheck> callbackErrorChecks;

	public CallbackErrorDestinationFlowAnalysis(ClassifierDataSet dataSet, AnalysisMetaInformation ami) {
		super(dataSet, ami);
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
	protected CallbackErrorLatticeElement join(
			CallbackErrorLatticeElement left, CallbackErrorLatticeElement right) {

		if(left == null) return right;
		else if(right == null) return left;

		return left.join(right);

	}

	@Override
	public CallbackErrorLatticeElement entryValue(ScriptNode node) {

		/* Add any parameters that are not new and htat look like an error
		 * parameter. */

		CallbackErrorLatticeElement cele = new CallbackErrorLatticeElement();

		/* If this is the script (no parameters) or the function was inserted, there is nothing to do. */
		if(!(node instanceof FunctionNode) || node.getChangeType() == ChangeType.INSERTED) return cele;

		/* Look through the parameters to see if there is an unchanged error parameter. */
        FunctionNode function = (FunctionNode) node;
        for(AstNode parameter : function.getParams()) {
            if(parameter instanceof Name) {

                /* Match only error parameters for now. */
                Name name = (Name) parameter;
                if(name.getIdentifier().matches("(?i)e(rr(or)?)?") && name.getChangeType() != ChangeType.INSERTED && name.getChangeType() != ChangeType.UPDATED) {

                    /* Add the parameter to the lattice element. */
                    cele.parameters.add(name.getIdentifier());

                }

            }
        }

		return cele;

	}

	@Override
	public void transfer(CFGEdge edge, CallbackErrorLatticeElement sourceLE,
			Scope<AstNode> scope) {

		AstNode condition = (AstNode) edge.getCondition();

		if(condition == null || !(scope.getScope() instanceof FunctionNode)) return;

		/* Look for inserted parameter checks. */
        List<SpecialTypeCheck> specialTypeChecks = SpecialTypeVisitor.getSpecialTypeChecks(condition);

        for(SpecialTypeCheck specialTypeCheck : specialTypeChecks) {
        	if(sourceLE.parameters.contains(specialTypeCheck.identifier)) {

        		FunctionNode function = (FunctionNode) scope.getScope();

                /* Register an alert. */
                String signature = AnalysisUtilities.getFunctionSignature(function);
                this.callbackErrorChecks.add(new CallbackErrorCheck(scope, function.getName(), signature, specialTypeCheck.identifier, specialTypeCheck.specialType));

        	}
        }

	}

	@Override
	public void transfer(CFGNode node, CallbackErrorLatticeElement sourceLE,
			Scope<AstNode> scope) {
		/* Nothing to do. */
	}

	@Override
	public CallbackErrorLatticeElement copy(CallbackErrorLatticeElement le) {
		return le.copy();
	}

}