package ca.ubc.ece.salt.sdjsb.analysis;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.ScriptNode;

import ca.ubc.ece.salt.sdjsb.cfg.CFG;
import ca.ubc.ece.salt.sdjsb.cfg.CFGEdge;

/**
 * Performs a change-sensitive, intra-procedural analysis.
 * 
 * This framework provides the analysis framework and the current scope.
 * 
 * Loops are executed once.
 */
public class Analysis {
	
	private AstRoot root;
	private List<CFG> cfgs;
	
	/**
	 * Set up the analysis. For JavaScript, we need to get a function-hierarchy
	 * so we can analyze them in-order and build the scope for each function.
	 * 
	 * @param root the root AST node fort he script.
	 * @param cfgs the control flow graphs for the functions (and script).
	 */
	public Analysis(AstRoot root, List<CFG> cfgs) {
		this.root = root;
		this.cfgs = cfgs;
	}
	
	/**
	 * Perform's a path-sensitive analysis of the script.
	 * @throws Exception
	 */
	public void analyze() throws Exception {

		Stack<Set<Name>> scopeStack = new Stack<Set<Name>>();
		this.analyze(this.root, scopeStack);
		
	}
	
	/**
	 * Perform's a path-sensitive analysis of the function.
	 * @throws Exception
	 */
	public void analyze(ScriptNode function, Stack<Set<Name>> scopeStack) throws Exception {
		
        /* Create a new scope for this script or function and push it onto
         * the stack. */
        
        Set<Name> scope = new HashSet<Name>();
        scopeStack.push(scope);
        
        /* Analyze node. */
        
        CFG cfg = this.getFunctionCFG(function);
        if(cfg == null) throw new Exception("CFG not found for function.");
        this.pathSensitiveAnalysis(cfg, scopeStack);
        
        /* Analyze the methods of the function. */

        for(FunctionNode method : function.getFunctions()) {
        	analyze(method, scopeStack);
        }
        
        /* We are done with this function. Pop it's scope from the stack. */
        scopeStack.pop();

	}
	
	/**
	 * Perform a path-sensitive analysis.
	 * 
	 * As we discover variable declarations, we add them to the scope.
	 * 
	 * @param cfg the control flow graph for the function or script we are analyzing.
	 * @param scopeStack the scope for the function.
	 */
	private void pathSensitiveAnalysis(CFG cfg, Stack<Set<Name>> scopeStack) {
		
		/* Initialize the transfer function we will use for the LatticeElement. */
		TransferFunction tf = new TransferFunction();
		
		/* Initialize the stack for a depth-first traversal. */
		Stack<PathState> stack = new Stack<PathState>();
		for(CFGEdge edge : cfg.getEntryNode().getEdges()) stack.add(new PathState(edge, new LatticeElement()));
		
		while(!stack.isEmpty()) {
			
			PathState state = stack.pop();
			
			/* Transfer over the edge. */
			tf.transfer(state.edge, state.le);
			
			/* Transfer over the node. */
			tf.transfer(state.edge.getTo(), state.le);
			
			/* Push the new edges onto the stack. */
			for(CFGEdge edge : state.edge.getTo().getEdges()) {

                /* If an edge has been visited on this path, don't visit it
                 * again (only loop once). */
				if(!state.le.visitedEdges.contains(edge.getId())) {
					LatticeElement copy = LatticeElement.copy(state.le);
					copy.visitedEdges.add(edge.getId());
                    stack.add(new PathState(edge, copy));
				}

			}
			
		}

	}
	
	/**
	 * Find the CFG for the script or function.
	 * @param node a AstRoot or FunctionNode
	 * @return the CFG for the script or function.
	 */
	private CFG getFunctionCFG(ScriptNode node) {
		
		for(CFG cfg : this.cfgs) {
			if(cfg.getEntryNode().getStatement() == node) return cfg;
		}
		
		return null;
		
	}
	
	/**
	 * Stores the state of the analysis.
	 * 
	 * This is needed for a path-sensitive analysis. When we traverse the graph
	 * we keep track of the lattice element for a given path we are traversing
	 * and the next edge to transfer over using this clas.
	 */
	private class PathState {
		
		public CFGEdge edge;
		public LatticeElement le;
		
		public PathState (CFGEdge edge, LatticeElement le) {
			this.edge = edge;
			this.le = le;
		}
		
	}
	
}
