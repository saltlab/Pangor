package ca.ubc.ece.salt.pangor.js.analysis.scope;

import java.util.HashMap;
import java.util.List;

import org.mozilla.javascript.ast.Assignment;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.ForInLoop;
import org.mozilla.javascript.ast.ForLoop;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.NodeVisitor;
import org.mozilla.javascript.ast.ScriptNode;
import org.mozilla.javascript.ast.VariableInitializer;

/**
 * Generates the scope for a function.
 */
public class ScopeVisitor implements NodeVisitor {

	private JavaScriptScope scope;

	/**
	 * Populate the variables in the local scope of the script or function.
	 * @param script The script or function.
	 * @return A map containing the variable identifiers and their AstNode (Name).
	 */
	public static void getLocalScope(JavaScriptScope scope) {

		ScriptNode script = scope.scope;
		ScopeVisitor scopeVisitor;

		if(script instanceof FunctionNode) {
			FunctionNode function = (FunctionNode) script;
			scopeVisitor = new ScopeVisitor(scope, function.getParams());
            function.getBody().visit(scopeVisitor);
		}
		else {
			scopeVisitor = new ScopeVisitor(scope);
            script.visit(scopeVisitor);
		}

	}

	public ScopeVisitor(JavaScriptScope scope) {
		this.scope = scope;
		this.scope.variables = new HashMap<String, AstNode>();
		this.scope.globals = new HashMap<String, AstNode>();
	}

	public ScopeVisitor(JavaScriptScope scope, List<AstNode> parameters) {
		this.scope = scope;
		this.scope.variables = new HashMap<String, AstNode>();
		this.scope.globals = new HashMap<String, AstNode>();

        /* Include the parameters in the local scope. */
        for(AstNode parameter : parameters) {

        	if(parameter instanceof Name) {
        		Name name = (Name) parameter;
        		this.scope.variables.put(name.getIdentifier(), name);
        	}

        }
	}

	@Override
	public boolean visit(AstNode node) {

		if(node instanceof FunctionNode) {

			FunctionNode fn = (FunctionNode) node;

			if(!fn.getName().equals("")) {
                this.scope.variables.put(fn.getName(), fn);
			}

			return false;
		}
		else if (node instanceof VariableInitializer) {

			VariableInitializer vi = (VariableInitializer) node;

            if(vi.getTarget() instanceof Name) {
                Name variable = (Name) vi.getTarget();
                this.scope.variables.put(variable.getIdentifier(), variable);

            }

		}
		else if (node instanceof Assignment) {

			/* An assignment creates a global variable if it hasn't been
			 * defined locally. */

			Assignment assignment = (Assignment) node;

			if(assignment.getLeft() instanceof Name) {
				Name name = (Name)assignment.getLeft();
                if(!this.scope.isLocal(name.getIdentifier()) && !this.scope.globals.containsKey(name.getIdentifier())) {
                	this.scope.globals.put(name.getIdentifier(), name);
                }
			}

		}
		else if (node instanceof ForInLoop) {

			ForInLoop loop = (ForInLoop) node;
			if(loop.getIterator() instanceof Name) {

				Name name = (Name)loop.getIterator();
                if(!this.scope.isLocal(name.getIdentifier()) && !this.scope.globals.containsKey(name.getIdentifier())) {
                	this.scope.globals.put(name.getIdentifier(), name);
                }

			}

		}
		else if (node instanceof ForLoop) {

			ForLoop loop = (ForLoop) node;
			if(loop.getInitializer() instanceof Name) {

				Name name = (Name)loop.getInitializer();
                if(!this.scope.isLocal(name.getIdentifier()) && !this.scope.globals.containsKey(name.getIdentifier())) {
                	this.scope.globals.put(name.getIdentifier(), name);
                }

			}

		}

		return true;
	}

}
