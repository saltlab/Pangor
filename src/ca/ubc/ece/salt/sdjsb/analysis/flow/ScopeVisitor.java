package ca.ubc.ece.salt.sdjsb.analysis.flow;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.NodeVisitor;
import org.mozilla.javascript.ast.ScriptNode;
import org.mozilla.javascript.ast.VariableDeclaration;
import org.mozilla.javascript.ast.VariableInitializer;

/**
 * Generates the scope for a function.
 */
public class ScopeVisitor implements NodeVisitor {
	
	private Map<String, AstNode> localScope;
	
	/**
	 * Gets the variables in the local scope of the script or function.
	 * @param script The script or function.
	 * @return A map containing the variable identifiers and their AstNode (Name).
	 */
	public static Map<String, AstNode> getLocalScope(ScriptNode script) {
		
		ScopeVisitor scopeVisitor;
		
		if(script instanceof FunctionNode) {
			FunctionNode function = (FunctionNode) script;
			scopeVisitor = new ScopeVisitor(function.getParams());
		}
		else {
			scopeVisitor = new ScopeVisitor();
		}
		
		script.visit(scopeVisitor);
		
		return scopeVisitor.localScope;
		
	}
	
	public ScopeVisitor() {
		this.localScope = new HashMap<String, AstNode>();
	}
	
	public ScopeVisitor(List<AstNode> parameters) {
		this.localScope = new HashMap<String, AstNode>();

        /* Include the parameters in the local scope. */
        for(AstNode parameter : parameters) {
        	
        	if(parameter instanceof Name) {
        		Name name = (Name) parameter;
        		this.localScope.put(name.getIdentifier(), name);
        	}
            
        }
	}

	@Override
	public boolean visit(AstNode node) {
		
		if(node instanceof FunctionNode) {
			
			FunctionNode fn = (FunctionNode) node;
		
			if(!fn.getName().equals(""))
                this.localScope.put(fn.getName(), fn);
			
			return false;
		}
		else if (node instanceof VariableDeclaration) {
			
			VariableDeclaration vd = (VariableDeclaration) node;
			
			for(VariableInitializer vi : vd.getVariables()) {
				if(vi.getTarget() instanceof Name) {
                    Name variable = (Name) vi.getTarget();
                    this.localScope.put(variable.getIdentifier(), variable);
					
				}
			}
			
		}
		
		return false;
	}

}
