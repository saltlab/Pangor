package ca.ubc.ece.salt.pangor.analysis.specialtype;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ca.ubc.ece.salt.pangor.analysis.flow.AbstractLatticeElement;
import ca.ubc.ece.salt.pangor.js.analysis.SpecialTypeAnalysisUtilities.SpecialType;

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
	public Map<String, List<SpecialType>> specialTypes;

	/**
	 * Keeps track of identifiers that are not special types on the path.
	 */
	public Map<String, List<SpecialType>> nonSpecialTypes;

	/**
	 * Keeps track of new special type assignments.
	 */
	public Map<String, SpecialType> assignments;

	public SpecialTypeLatticeElement() {
		super();
		this.specialTypes = new HashMap<String, List<SpecialType>>();
		this.nonSpecialTypes = new HashMap<String, List<SpecialType>>();
		this.assignments = new HashMap<String, SpecialType>();
	}

	public SpecialTypeLatticeElement(Map<String, List<SpecialType>> specialTypes, Map<String, List<SpecialType>> nonSpecialTypes, Map<String, SpecialType> assignments, Map<Long, Integer> visitedEdges) {
		super(visitedEdges);
		this.specialTypes = specialTypes;
		this.nonSpecialTypes = nonSpecialTypes;
		this.assignments = assignments;
	}

	/**
	 * @return a copy of the LatticeElement.
	 */
	public static SpecialTypeLatticeElement copy(SpecialTypeLatticeElement le) {

		/* Make copies of the special type maps. We also need to make a copy
		 * of all the lists inside each mapping. */
		HashMap<String, List<SpecialType>> specialTypes = new HashMap<String, List<SpecialType>>();
		for(String key : le.specialTypes.keySet()) {
			specialTypes.put(key, new LinkedList<SpecialType>(le.specialTypes.get(key)));
		}

		HashMap<String, List<SpecialType>> nonSpecialTypes = new HashMap<String, List<SpecialType>>();
		for(String key : le.nonSpecialTypes.keySet()) {
			nonSpecialTypes.put(key, new LinkedList<SpecialType>(le.nonSpecialTypes.get(key)));
		}

		/* Return a copy of this lattice element. */
		return new SpecialTypeLatticeElement(specialTypes,
											 nonSpecialTypes,
											 new HashMap<String, SpecialType>(le.assignments),
											 new HashMap<Long, Integer>(le.visitedEdges));
	}

}
