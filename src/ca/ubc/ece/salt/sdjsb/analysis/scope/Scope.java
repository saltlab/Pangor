package ca.ubc.ece.salt.sdjsb.analysis.scope;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.ScriptNode;

public final class Scope {
	
	public Scope parent;
	public ScriptNode scope;
	public Map<String, AstNode> variables;
	public Map<String, AstNode> globals;
	public List<Scope> children;
	
	public Scope(Scope parent, ScriptNode scope) {
		this.parent = parent;
		this.scope = scope;
		this.variables = new HashMap<String, AstNode>();
		this.globals = new HashMap<String, AstNode>();
		this.children = new LinkedList<Scope>();
	}
	

	/**
	 * Starting with the current scope, search the tree upwards until the
	 * identifier is found (or not found).
	 * @param variable The variable to find.
	 * @return The Name node where the variable is declared.
	 */
	public AstNode getVariableDeclaration(String variable) {
		
		if(this.variables.containsKey(variable)) return this.variables.get(variable);
		if(this.parent == null) return null;
		return parent.getVariableDeclaration(variable);
		
	}
	
	/**
	 * @param function The function to find.
	 * @return The scope for the function, or null if the function is not a
	 * 		   sub-function of the function/script for this scope.
	 */
	public Scope getFunctionScope(FunctionNode function) {
		
		if(this.scope == function) return this;
		
		for(Scope child : this.children) {
			Scope functionScope = child.getFunctionScope(function);
			if(functionScope != null) return functionScope;
		}
		
		return null;
		
	}
	
	/**
	 * Checks if a variable is in a local scope by checking the scope tree
	 * until we get to the global scope.
	 * @param variable The variable to check.
	 * @return True if the variable is defined in a local scope.
	 */
	public boolean isLocal(String variable) {
		
		if(this.parent == null) return false;
		if(this.variables.containsKey(variable)) return true;
		return this.parent.isLocal(variable);
		
	}
	
	/**
	 * Checks if a variable is in a global scope by checking parents in the
	 * scope tree until we get to the root. 
	 * @param variable The variable to check.
	 * @return True if the variable is defined in a global scope.
	 */
	public boolean isGlobal(String variable) {
		
		if(this.globals.containsKey(variable)) return true;
		if(this.parent == null) return this.variables.containsKey(variable);
		return this.parent.isGlobal(variable);
		
	}

}
