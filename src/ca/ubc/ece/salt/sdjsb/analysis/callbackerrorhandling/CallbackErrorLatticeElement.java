package ca.ubc.ece.salt.sdjsb.analysis.callbackerrorhandling;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ca.ubc.ece.salt.sdjsb.analysis.flow.AbstractLatticeElement;

public class CallbackErrorLatticeElement extends AbstractLatticeElement {

	/** Keeps track of the unchanged parameters. */
	public Set<String> parameters;

	public CallbackErrorLatticeElement() {
		super();
		this.parameters = new HashSet<String>();
	}

	public CallbackErrorLatticeElement(Set<String> parameters, Map<Long, Integer> visitedEdges) {
		super(visitedEdges);
		this.parameters = parameters;
	}

	/**
	 * @param le The LE to join.
	 * @return This LE joined with the given LE.
	 */
	public CallbackErrorLatticeElement join(CallbackErrorLatticeElement le) {
		CallbackErrorLatticeElement joined = this.copy();
		joined.parameters.addAll(le.parameters);
		joined.visitedEdges.putAll(le.visitedEdges);
		return joined;
	}

	/**
	 * @return a copy of the LatticeElement.
	 */
	public CallbackErrorLatticeElement copy() {
		return new CallbackErrorLatticeElement(new HashSet<String>(this.parameters), new HashMap<Long, Integer>(this.visitedEdges));
	}

}