package ca.ubc.ece.salt.sdjsb.analysis.notdefined;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ca.ubc.ece.salt.sdjsb.analysis.flow.AbstractLatticeElement;

public class NotDefinedLatticeElement extends AbstractLatticeElement {

	/**
	 * Keeps track of inserted variable declarations.
	 */
	public Set<String> inserted;

	public NotDefinedLatticeElement() {
		super();
		this.inserted = new HashSet<String>();
	}

	public NotDefinedLatticeElement(Set<String> inserted, Map<Long, Integer> visitedEdges) {
		super(visitedEdges);
		this.inserted = inserted;
	}

	/**
	 * @return a copy of the LatticeElement.
	 */
	public static NotDefinedLatticeElement copy(NotDefinedLatticeElement le) {
		return new NotDefinedLatticeElement(new HashSet<String>(le.inserted), new HashMap<Long, Integer>(le.visitedEdges));
	}

}