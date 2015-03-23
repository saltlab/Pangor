package ca.ubc.ece.salt.sdjsb.checker.callbackparam;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.NodeVisitor;
import org.mozilla.javascript.ast.VariableDeclaration;
import org.mozilla.javascript.ast.VariableInitializer;

import ca.ubc.ece.salt.sdjsb.checker.AbstractChecker;
import ca.ubc.ece.salt.sdjsb.checker.CheckerContext;
import ca.ubc.ece.salt.sdjsb.checker.CheckerContext.ChangeType;
import fr.labri.gumtree.io.ParserASTNode;

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
public class CallbackParameterChecker extends AbstractChecker {
	
	private static final String TYPE = "CBP";
	
	public CallbackParameterChecker(CheckerContext context) {
		super(context);
	}

	@Override
	public void sourceDelete(AstNode node) { }

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
		
		/* Strategy: Look for parameters added to functions that look like 'err' or 'error'. */
		
		/* Is this part of a function? */
		if(node instanceof Name && node.getParent() instanceof FunctionNode) {
			
			Name name = (Name) node;
            FunctionNode function = (FunctionNode) name.getParent();
            List<AstNode> params = function.getParams();

            /* Is this the first parameter? */
            if(!params.isEmpty() && params.get(0) == name) {

                /* Does this parameter have an error-like name? */
                if(name.getIdentifier().matches("(?i)e(rr(or)?)?")) {
                    
                    /* Is the function declaration unchanged? */
                    if(this.context.getDstChangeOp(function) != ChangeType.INSERT) {
                    	
                    	/* Build the signature. */
                    	String signature = "(";
                    	for(AstNode param : params) {
                    		if(param instanceof Name) {
                    			if(!signature.equals("(")) signature += ",";
                    			signature += ((Name)param).getIdentifier();
                    		}
                    		signature += ")";
                    	}

                    	/* Register the alert. */
                        this.registerAlert(new CallbackParameterAlert(this.getCheckerType(), function.getName(), signature, name.getIdentifier()));
                        
                    }

                }
                
            }
				
		}
		
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
