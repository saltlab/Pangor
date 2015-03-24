package ca.ubc.ece.salt.sdjsb.checker.callbackerror;

import java.util.HashSet;
import java.util.Set;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.IfStatement;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.NodeVisitor;

import ca.ubc.ece.salt.sdjsb.checker.AbstractChecker;
import ca.ubc.ece.salt.sdjsb.checker.CheckerContext;
import ca.ubc.ece.salt.sdjsb.checker.CheckerUtilities;
import ca.ubc.ece.salt.sdjsb.checker.CheckerContext.ChangeType;

/**
 * Detects repairs that fix undefined variables.
 * 
 * For example, if a repair declares a variable (i.e. "var [variable]") that is
 * used in the source file but is undeclared in the source file.
 * 
 * TODO: Handle cases like PM2 d59200f2c40691a8b87674e2cd707c9a73175bee
 * 
 * @author qhanam
 */
public class CallbackErrorChecker extends AbstractChecker {
	
	private static final String TYPE = "CB";
	
	private Set<FunctionNode> deleted;
	
	public CallbackErrorChecker(CheckerContext context) {
		super(context);
		this.deleted = new HashSet<FunctionNode>();
	}

	@Override
	public void sourceDelete(AstNode node) { 

		/* Strategy: Look for parameters that look like 'err' or 'error' that
		 * are checked inside branch conditionals. */
		
		AstNode parameter = isErrorCheck(node);

		if(parameter != null) {

            /* Was the parameter inserted? */
            ChangeType changeType = this.context.getDstChangeOp(parameter);
            if(changeType != ChangeType.INSERT /*&& changeType != ChangeType.MOVE && changeType != ChangeType.UPDATE */) {

                FunctionNode function = (FunctionNode) parameter.getParent();
            	
                /* This should be part of a conditional. */
            	AstNode parent = CheckerUtilities.getBranchStatement(node);
                
            	if(parent != null) {
                    /* Add the alert to the deleted set. */
                    FunctionNode sourceFunction = (FunctionNode) this.context.getDstNodeMapping(function);
                    if(sourceFunction != null) this.deleted.add(sourceFunction);
            	}
                
            }
			
		}
	}

	@Override
	public void sourceUpdate(AstNode node) { }

	@Override
	public void sourceMove(AstNode node) { }

	@Override
	public void destinationUpdate(AstNode node) { }

	@Override
	public void destinationMove(AstNode node) { }

	@Override
	public void destinationInsert(AstNode node) { 
		
		/* Strategy: Look for parameters that look like 'err' or 'error' that
		 * are checked inside branch conditionals. */
		
		AstNode parameter = isErrorCheck(node);

		if(parameter != null) {

            /* Was the parameter inserted? */
            ChangeType changeType = this.context.getDstChangeOp(parameter);
            if(changeType != ChangeType.INSERT /*&& changeType != ChangeType.MOVE && changeType != ChangeType.UPDATE */) {

                FunctionNode function = (FunctionNode) parameter.getParent();
            	
                /* This should be part of a conditional. */
            	AstNode parent = CheckerUtilities.getBranchStatement(node);
            	if(parent != null) {
            		
            		/* Was a check deleted that this is replacing? */
            		if(!this.deleted.contains(function)) {

                        /* Register the alert. */
                        String signature = CheckerUtilities.getFunctionSignature(function);
                        this.registerAlert(new CallbackErrorAlert(this.getCheckerType(), function.getName(), signature, ((Name)node).getIdentifier()));
            			
            		}
            		
            	}
                
            }
			
		}
		
	}
	
	private AstNode isErrorCheck(AstNode node) {

		/* Is this possibly a variable? */
		if(node instanceof Name) {

			Name name = (Name) node;

            /* Is the variable being checked to see if it is falsey? */
            if(CheckerUtilities.isFalsey(node)) {

                /* Does this variable have an error-like name? */
                if(name.getIdentifier().matches("(?i)e(rr(or)?)?")) {
                    
                    /* Is this a parameter? */
                    AstNode parameter = CheckerUtilities.isParameter(name);
                    if(parameter != null) {
                        return parameter;
                    }
                    
                }
                
            }
	
		}
		
		return null;
		
	}

	@Override
	public void pre() { }
	
	@Override
	public void post() { }

	@Override
	public String getCheckerType() {
		return TYPE;
	}
	
}
