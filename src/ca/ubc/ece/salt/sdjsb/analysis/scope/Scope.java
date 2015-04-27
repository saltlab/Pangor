package ca.ubc.ece.salt.sdjsb.analysis.scope;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.ScriptNode;

public final class Scope {
	
	public Scope parent;
	public ScriptNode scope;
	public Map<String, AstNode> variables;
	public List<Scope> children;
	
	public Scope(Scope parent, ScriptNode scope) {
		this.parent = parent;
		this.scope = scope;
		this.variables = new HashMap<String, AstNode>();
		this.children = new LinkedList<Scope>();
	}

}
