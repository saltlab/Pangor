package ca.ubc.ece.salt.sdjsb.analysis.flow;

import java.util.Stack;

import org.mozilla.javascript.ast.ScriptNode;

import ca.ubc.ece.salt.sdjsb.analysis.scope.Scope;
import ca.ubc.ece.salt.sdjsb.cfg.CFG;
import ca.ubc.ece.salt.sdjsb.cfg.CFGEdge;

/**
 * A path sensitive flow analysis.
 * 
 * @author qhanam
 *
 * @param <LE> The type that stores the analysis information.
 */
public abstract class PathSensitiveFlowAnalysis<LE extends AbstractLatticeElement> extends FlowAnalysis<LE> {

	public PathSensitiveFlowAnalysis() {
		super();
	}

	/**
	 * Perform a path-sensitive analysis.
	 * 
	 * As we discover variable declarations, we add them to the scope.
	 * 
	 * @param cfg the control flow graph for the function or script we are analyzing.
	 * @param scopeStack the scope for the function.
	 */
	@Override
	protected void analyze(CFG cfg, Scope scope) {
		
		long pathsComplete = 0;
		long edgesVisited = 0;

		/* Initialize the stack for a depth-first traversal. */
		Stack<PathState> stack = new Stack<PathState>();
		for(CFGEdge edge : cfg.getEntryNode().getEdges()) stack.add(new PathState(edge, this.entryValue((ScriptNode)cfg.getEntryNode().getStatement())));
		
		/* Break when the number of edges visited reaches some limit. */
		while(!stack.isEmpty() && edgesVisited < 100000) {
			
			PathState state = stack.pop();
			edgesVisited++;
			
			/* Transfer over the edge. */
			this.transfer(state.edge, state.le, scope);
			
			/* Transfer over the node. */
			this.transfer(state.edge.getTo(), state.le, scope);
			
			/* Push the new edges onto the stack. */
			for(CFGEdge edge : state.edge.getTo().getEdges()) {
				
				/* Increment the # of paths visited by one if we're at an exit node. */
				if(edge.getTo().getEdges().size() == 0) {
					pathsComplete++;
					System.out.println("Paths Complete: " + pathsComplete);
					System.out.println("Edges Visited: " + edgesVisited);
				}
				
                /* If an edge has been visited on this path, don't visit it
                 * again (only loop once). */
				if(edge.getCondition() == null || state.le.getVisitedCount(edge) == 0) {
					LE copy = this.copy(state.le);
					copy.visit(edge);
                    stack.add(new PathState(edge, copy));
				}
				
			}
			
		}
		
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
		public LE le;
		
		public PathState (CFGEdge edge, LE le) {
			this.edge = edge;
			this.le = le;
		}
		
	}

}
