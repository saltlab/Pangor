package ca.ubc.ece.salt.sdjsb.analysis.learning;

import ca.ubc.ece.salt.sdjsb.analysis.flow.AbstractLatticeElement;

/**
 * The LearningLatticeElement stores new or removed path fragments through the
 * source or destination function.
 */
public class LearningLatticeElement extends AbstractLatticeElement {
	
	/** Stores the current new or removed path fragment. **/
	public PathFragment currentPathFragment;
	
	/**
	 * Creates a new lattice element.
	 */
	public LearningLatticeElement() { }
	
}
