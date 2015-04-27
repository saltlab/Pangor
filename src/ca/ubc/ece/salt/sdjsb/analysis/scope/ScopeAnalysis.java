package ca.ubc.ece.salt.sdjsb.analysis.scope;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.ScriptNode;

import ca.ubc.ece.salt.sdjsb.alert.Alert;
import ca.ubc.ece.salt.sdjsb.analysis.Analysis;
import ca.ubc.ece.salt.sdjsb.analysis.flow.FunctionTreeVisitor;
import ca.ubc.ece.salt.sdjsb.analysis.flow.ScopeVisitor;
import ca.ubc.ece.salt.sdjsb.cfg.CFG;

/**
 * Builds a scope tree for the source and destination ASTs. This is used
 * as the basis for performing flow analysis, but can also be used stand-alone
 * if only scope is needed.
 */
public class ScopeAnalysis implements Analysis {
	
	protected List<CFG> srcCFGs;
	protected List<CFG> dstCFGs;
	
	protected Scope srcScope;
	protected Scope dstScope;
	
	/** Maps function nodes to their scopes. */
	protected Map<ScriptNode, Scope> srcScopeMap;

	/** Maps function nodes to their scopes. */
	protected Map<ScriptNode, Scope> dstScopeMap;
	
	/**
	 * @return the source scope tree.
	 */
	public Scope getSrcScope() {
		return this.srcScope;
	}
	
	/**
	 * @return the destination scope tree.
	 */
	public Scope getDstScope() {
		return this.dstScope;
	}
	
	/**
	 * @param node the script or function
	 * @return the source scope tree.
	 */
	public Scope getSrcScope(ScriptNode node) {
		return this.srcScopeMap.get(node);
	}

	/**
	 * @param node the script or function
	 * @return the destination scope tree.
	 */
	public Scope getDstScope(ScriptNode node) {
		return this.dstScopeMap.get(node);
	}

	@Override
	public void analyze(AstRoot root, List<CFG> cfgs) throws Exception {

		this.dstScopeMap = new HashMap<ScriptNode, Scope>();
		this.dstCFGs = cfgs;
		this.dstScope = this.buildScopeTree(root, null, this.dstScopeMap);

	}

	@Override
	public void analyze(AstRoot srcRoot, List<CFG> srcCFGs, AstRoot dstRoot,
			List<CFG> dstCFGs) throws Exception {

		this.srcScopeMap = new HashMap<ScriptNode, Scope>();
		this.dstScopeMap = new HashMap<ScriptNode, Scope>();
		this.srcCFGs = srcCFGs;
		this.dstCFGs = dstCFGs;
		this.srcScope = this.buildScopeTree(srcRoot, null, this.srcScopeMap);
		this.dstScope = this.buildScopeTree(dstRoot, null, this.dstScopeMap);

	}

	/**
	 * Builds the scope tree. 
	 * @return the root of the scope tree.
	 * @throws Exception
	 */
	private Scope buildScopeTree(ScriptNode function, Scope parent, Map<ScriptNode, Scope> scopeMap) throws Exception {
		
        /* Create a new scope for this script or function and add it to the 
         * scope tree. */
        
		Scope scope = new Scope(parent, function);
		scope.variables = ScopeVisitor.getLocalScope(function);

		if(parent != null) parent.children.add(scope);
		
		/* Put the scope in the scope map. */
		
		scopeMap.put(function, scope);
        
        /* Analyze the methods of the function. */

        List<FunctionNode> methods = FunctionTreeVisitor.getFunctions(function);
        for(FunctionNode method : methods) {
        	buildScopeTree(method, scope, scopeMap);
        }
        
        return scope;
        
	}

	@Override
	public Set<Alert> getAlerts() {
		return null;
	}

}
