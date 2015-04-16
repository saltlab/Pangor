package ca.ubc.ece.salt.sdjsb.checker.specialtype;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.EnumSet;

import ca.ubc.ece.salt.sdjsb.alert.SpecialTypeAlert.SpecialType;

/**
 * Keeps track of possible special type values for variables.
 * @author qhanam
 */
public class SpecialTypeMap {
	
	/* Keep track of variable names and special types. The key is the name of
	 * the variable while the value the set of values that the variable has
	 * been compared to. */
	Map<String, EnumSet<SpecialType>> map;
	
	public SpecialTypeMap() {
		this.map = new HashMap<String, EnumSet<SpecialType>>();
	}
	
	/**
	 * Add or update the possible values for a variable.
	 * @param name The variable name.
	 * @param type The type that the variable was assigned or compared to.
	 */
	public void add(String name, SpecialType type){
		if (map.containsKey(name)) {
			map.get(name).add(type);
		}
		else {
			map.put(name, EnumSet.of(type));
		}
	}
	
	/**
	 * Checks if the set for {@value name} contains type {@value type}.
	 * @param name The name of the variable.
	 * @param type The special type.
	 * @return True if the set for {@value name} contains type {@value type}, 
	 * 		   false if name is not found or the set does not contain
	 * 		   {@value type}.
	 */
	public boolean setContains(String name, SpecialType type) {
		if (map.containsKey(name)) {
			return map.get(name).contains(type);
		}
		return false;
	}
	
	/**
	 * Returns the list of variable names in the map.
	 * @return The list of variable names in the map.
	 */
	public Set<String> getNames() {
		return map.keySet();
	}
	
	/**
	 * Returns the set for the given name.
	 * @param name The variable name.
	 * @return The set of special types for the variable.
	 */
	public EnumSet<SpecialType> getSet(String name) {
		if (map.containsKey(name)) {
			return map.get(name);
		}
		throw new IllegalArgumentException("Name not found in map.");
	}

}
