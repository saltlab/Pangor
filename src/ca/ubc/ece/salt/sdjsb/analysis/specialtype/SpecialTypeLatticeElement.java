package ca.ubc.ece.salt.sdjsb.analysis.specialtype;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ca.ubc.ece.salt.sdjsb.alert.SpecialTypeAlert.SpecialType;
import ca.ubc.ece.salt.sdjsb.analysis.AbstractLatticeElement;

/**
 * A SpecialTypeLatticeElement
 * 
 * Not special type path:
 * 	1. New edge conditions that check that an identifier is not a special type.
 *  2. That identifier is used after the check, but before it is assigned and
 *     that use was in the original program.
 *  *. If the variable is used or assigned, we remove it from the map (after
 *     generating an alert if needed).
 *     
 * Special type path:
 * 	1. New edge conditions that check that an identifier is a special type.
 *  2. That identifier is assigned before it is used. The assignment is
 *     inserted and the use is in the original program.
 *     
 */
public class SpecialTypeLatticeElement extends AbstractLatticeElement{
	
	/**
	 * Keeps track of the edges this LatticeElement has already visited. This
	 * is so that we can break from loops.
	 */
	public Set<Long> visitedEdges;
	
	/**
	 * Keeps track of identifiers that are special types on the path. 
	 */
	public Map<String, SpecialType> specialTypes;

	/**
	 * Keeps track of identifiers that are not special types on the path. 
	 */
	public Map<String, SpecialType> nonSpecialTypes;
	
	public SpecialTypeLatticeElement() {
		this.specialTypes = new HashMap<String, SpecialType>();
		this.nonSpecialTypes = new HashMap<String, SpecialType>();
		this.visitedEdges = new HashSet<Long>();
	}

	public SpecialTypeLatticeElement(HashMap<String, SpecialType> specialTypes, HashMap<String, SpecialType> nonSpecialTypes, HashSet<Long> visitedEdges) {
		this.specialTypes = specialTypes;
		this.nonSpecialTypes = nonSpecialTypes;
		this.visitedEdges = visitedEdges;
	}
	
	/**
	 * @return a copy of the LatticeElement.
	 */
	public static SpecialTypeLatticeElement copy(SpecialTypeLatticeElement le) {
		return new SpecialTypeLatticeElement(new HashMap<String, SpecialType>(le.specialTypes), new HashMap<String, SpecialType>(le.nonSpecialTypes), new HashSet<Long>(le.visitedEdges));
	}
	
}
