package ca.ubc.ece.salt.sdjsb.analysis.specialtype;

import java.util.HashMap;
import java.util.Map;

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
	 * Keeps track of identifiers that are special types on the path. 
	 */
	public Map<String, SpecialType> specialTypes;

	/**
	 * Keeps track of identifiers that are not special types on the path. 
	 */
	public Map<String, SpecialType> nonSpecialTypes;
	
	/**
	 * Keeps track of new special type assignments.
	 */
	public Map<String, SpecialType> assignments;
	
	public SpecialTypeLatticeElement() {
		this.specialTypes = new HashMap<String, SpecialType>();
		this.nonSpecialTypes = new HashMap<String, SpecialType>();
		this.assignments = new HashMap<String, SpecialType>();
		this.visitedEdges = new HashMap<Long, Integer>();
	}

	public SpecialTypeLatticeElement(Map<String, SpecialType> specialTypes, Map<String, SpecialType> nonSpecialTypes, Map<String, SpecialType> assignments, Map<Long, Integer> visitedEdges) {
		this.specialTypes = specialTypes;
		this.nonSpecialTypes = nonSpecialTypes;
		this.assignments = assignments;
		this.visitedEdges = visitedEdges;
	}
	
	/**
	 * @return a copy of the LatticeElement.
	 */
	public static SpecialTypeLatticeElement copy(SpecialTypeLatticeElement le) {
		return new SpecialTypeLatticeElement(new HashMap<String, SpecialType>(le.specialTypes), 
											 new HashMap<String, SpecialType>(le.nonSpecialTypes), 
											 new HashMap<String, SpecialType>(le.assignments), 
											 new HashMap<Long, Integer>(le.visitedEdges));
	}
	
}
