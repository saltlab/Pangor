package ca.ubc.ece.salt.sdjsb.analysis.flow;

import java.util.Map;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.ScriptNode;

public final class Scope {
	
	public ScriptNode scope;
	public Map<String, AstNode> variables;

}
