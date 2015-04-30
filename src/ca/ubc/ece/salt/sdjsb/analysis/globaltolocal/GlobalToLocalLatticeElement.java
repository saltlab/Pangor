package ca.ubc.ece.salt.sdjsb.analysis.globaltolocal;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ca.ubc.ece.salt.sdjsb.analysis.flow.AbstractLatticeElement;

public class GlobalToLocalLatticeElement extends AbstractLatticeElement {

	/**
	 * Keeps track of inserted variable declarations.
	 */
	public Set<String> inserted;

	public GlobalToLocalLatticeElement() {
		super();
		this.inserted = new HashSet<String>();
	}

	public GlobalToLocalLatticeElement(Set<String> inserted, Map<Long, Integer> visitedEdges) {
		super(visitedEdges);
		this.inserted = inserted;
	}

	/**
	 * @return a copy of the LatticeElement.
	 */
	public static GlobalToLocalLatticeElement copy(GlobalToLocalLatticeElement le) {
		return new GlobalToLocalLatticeElement(new HashSet<String>(le.inserted), new HashMap<Long, Integer>(le.visitedEdges));
	}

}