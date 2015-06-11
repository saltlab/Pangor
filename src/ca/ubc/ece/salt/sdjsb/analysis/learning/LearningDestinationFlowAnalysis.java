package ca.ubc.ece.salt.sdjsb.analysis.learning;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.ScriptNode;

import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode.ChangeType;
import ca.ubc.ece.salt.sdjsb.analysis.flow.PathInsensitiveFlowAnalysis;
import ca.ubc.ece.salt.sdjsb.analysis.scope.Scope;
import ca.ubc.ece.salt.sdjsb.cfg.CFGEdge;
import ca.ubc.ece.salt.sdjsb.cfg.CFGNode;

public class LearningDestinationFlowAnalysis extends PathInsensitiveFlowAnalysis<LearningLatticeElement> {

	/** 
	 * Stores a set of path fragments discovered by this analysis. Path
	 * fragments are stored by their entry points, which should make it easier
	 * to reconstruct full paths later. */
	public Map<CFGNode, PathFragment> pathFragments;
	
	/**
	 * Stores a set of interesting conditions. These are conditions that have
	 * been inserted, removed or updated and occur on unchanged edges.
	 */
	public List<AstNode> interestingConditions;
	
	public LearningDestinationFlowAnalysis() { 
		this.pathFragments = new HashMap<CFGNode, PathFragment>();
	}
	
	@Override
	protected LearningLatticeElement join(
			LearningLatticeElement left, LearningLatticeElement right) {

		/* Terminate the current path fragments. */
		if(left.currentPathFragment != null) {
			
			/* The last node in the path will be the first node of our new path fragment. */
			left.currentPathFragment.continuesAt = left.currentPathFragment.path.removeLast();
			
			/* Store the current path fragment in the fragment map. */
			this.pathFragments.put(left.currentPathFragment.path.get(0), left.currentPathFragment);

		}
		else if(right.currentPathFragment != null) {
			
			/* The last node in the path will be the first node of our new path fragment. */
			right.currentPathFragment.continuesAt = right.currentPathFragment.path.removeLast();
			
			/* Store the current path fragment in the fragment map. */
			this.pathFragments.put(right.currentPathFragment.path.get(0), right.currentPathFragment);

		}
		
		return new LearningLatticeElement();

	}

	@Override
	public LearningLatticeElement entryValue(ScriptNode node) {
		return new LearningLatticeElement();
	}

	@Override
	public void transfer(CFGEdge edge, LearningLatticeElement sourceLE,
			Scope scope) {
		
		AstNode condition = (AstNode) edge.getCondition();
		
		/* There is a new or removed path element, add it to a path fragment. */
		if(edge.changeType == ChangeType.INSERTED || edge.changeType == ChangeType.REMOVED) {
			
			/* If there is a condition on the edge, we need to begin a new 
			 * path fragment. Store the current path fragment in the map. */
			if(condition != null && sourceLE.currentPathFragment != null) {
				sourceLE.currentPathFragment.continuesAt = edge.getTo();
				this.pathFragments.put(sourceLE.currentPathFragment.path.getFirst(), sourceLE.currentPathFragment);
				sourceLE.currentPathFragment = null;
			}

			/* Add the next node in the path to the path fragment. */
			if(sourceLE.currentPathFragment == null) {
				sourceLE.currentPathFragment = new PathFragment();
				sourceLE.currentPathFragment.condition = condition;
			}

			sourceLE.currentPathFragment.path.add(edge.getTo()); 
			
			/* Set the condition if applicable. */
			if(condition != null) {
				sourceLE.currentPathFragment.condition = condition;
			}
			
		}
		
		/* If there is a new or updated edge condition and the edge is
		 * unchanged, add it to our set of interesting conditions. */
		if(edge.changeType == ChangeType.UNCHANGED && condition != null && condition.getChangeType() == ChangeType.INSERTED) {
			this.interestingConditions.add(condition);
		}
		
	}

	@Override
	public void transfer(CFGNode node, LearningLatticeElement sourceLE,
			Scope scope) {
		/* Nothing to do. */
	}

	@Override
	public LearningLatticeElement copy(LearningLatticeElement le) {
		/* It is OK for us to only use one lattice element. If an edge has a 
		 * condition (i.e., it is a branching edge), any path fragment is 
		 * terminated. */
		return le;
	}

}
