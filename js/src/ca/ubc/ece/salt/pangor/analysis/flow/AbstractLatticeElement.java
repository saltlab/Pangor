package ca.ubc.ece.salt.pangor.analysis.flow;

import java.util.HashMap;
import java.util.Map;

import ca.ubc.ece.salt.pangor.cfg.CFGEdge;

public abstract class AbstractLatticeElement {
	
	/**
	 * Keeps track of the edges this LatticeElement has already visited. This
	 * is so that we can break from loops after a defined number of iterations.
	 */
	protected Map<Long, Integer> visitedEdges;
	
	public AbstractLatticeElement(Map<Long, Integer> visitedEdges) {
		this.visitedEdges = visitedEdges;
	}
	
	public AbstractLatticeElement() {
		this.visitedEdges = new HashMap<Long, Integer>();
	}
	
	/**
	 * Increment the number of times this edge has been visited by the LE.
	 * @param edge The edge that was visited.
	 */
	public void visit(CFGEdge edge) {
		Integer count = this.visitedEdges.get(edge.getId());

		if(count == null) count = 1;
		else count = count + 1;
			
        this.visitedEdges.put(edge.getId(), count);
	}
	
	/**
	 * @param edge the edge that was visited.
	 * @return The number of times the given edge has been visited by the
	 * 		   lattice element.
	 */
	public Integer getVisitedCount(CFGEdge edge) {
		Integer count = this.visitedEdges.get(edge.getId());

		if(count == null) return 0;
		else return count;
	}
	
}
