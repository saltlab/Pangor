package ca.ubc.ece.salt.sdjsb.analysis.notdefined;

import java.util.HashSet;
import java.util.Set;

import ca.ubc.ece.salt.sdjsb.analysis.AbstractLatticeElement;

public class NotDefinedLatticeElement extends AbstractLatticeElement {

	/**
	 * Keeps track of deleted variable declarations.
	 */
	public Set<String> deleted;
	
	/**
	 * Keeps track of inserted variable declarations.
	 */
	public Set<String> inserted;

	public NotDefinedLatticeElement() {
		this.deleted = new HashSet<String>();
		this.inserted = new HashSet<String>();
	}

	public NotDefinedLatticeElement(Set<String> deleted, Set<String> inserted) {
		this.deleted = deleted;
		this.inserted = inserted;
	}

	/**
	 * @return a copy of the LatticeElement.
	 */
	public static NotDefinedLatticeElement copy(NotDefinedLatticeElement le) {
		return new NotDefinedLatticeElement(new HashSet<String>(le.deleted), new HashSet<String>(le.inserted));
	}

}