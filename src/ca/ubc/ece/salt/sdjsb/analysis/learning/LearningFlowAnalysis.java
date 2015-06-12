package ca.ubc.ece.salt.sdjsb.analysis.learning;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.ScriptNode;

import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode.ChangeType;
import ca.ubc.ece.salt.sdjsb.analysis.flow.PathInsensitiveFlowAnalysis;
import ca.ubc.ece.salt.sdjsb.analysis.scope.Scope;
import ca.ubc.ece.salt.sdjsb.cfg.CFGEdge;
import ca.ubc.ece.salt.sdjsb.cfg.CFGNode;

public class LearningFlowAnalysis extends PathInsensitiveFlowAnalysis<LearningLatticeElement> {

	/** 
	 * Stores a set of path fragments discovered by this analysis. Path
	 * fragments are stored by their entry points, which should make it easier
	 * to reconstruct full paths later. */
	private Map<String, List<PathFragment>> pathFragments;
	
	/**
	 * Stores a set of interesting conditions. These are conditions that have
	 * been inserted, removed or updated and occur on unchanged edges.
	 */
	private Map<String, List<AstNode>> interestingConditions;
	
	public LearningFlowAnalysis() { 
		this.pathFragments = new HashMap<String, List<PathFragment>>();
		this.interestingConditions = new HashMap<String, List<AstNode>>();
	}

	/**
	 * @return The list of path fragments found by the analysis.
	 */
	public Map<String, List<PathFragment>> getPathFragments() {
		return this.pathFragments;
	}
	
	/**
	 * @return The list of interesting conditions found by the analysis.
	 */
	public Map<String, List<AstNode>> getInterestingConditions() {
		return this.interestingConditions;
	}
	
	@Override
	protected LearningLatticeElement join(
			LearningLatticeElement left, LearningLatticeElement right) {
		
		if(left != null && right == null) return left;
		else if(left == null && right != null) return right;

		/* Terminate the current path fragments. */
		if(left != null && left.currentPathFragment != null) {
			
			/* The last node in the path will be the first node of our new path fragment. */
			left.currentPathFragment.continuesAt = left.currentPathFragment.path.removeLast();
			
			/* Store the current path fragment in the fragment map. */
			this.storePathFragment(left.currentPathFragment);
			
			/* Reset the current path fragment. */
			left.currentPathFragment = null;
			
			return left;

		}
		else if(right != null && right.currentPathFragment != null) {
			
			/* The last node in the path will be the first node of our new path fragment. */
			right.currentPathFragment.continuesAt = right.currentPathFragment.path.removeLast();
			
			/* Store the current path fragment in the fragment map. */
			this.storePathFragment(right.currentPathFragment);
			
			/* Reset the current path fragment. */
			right.currentPathFragment = null;
			
			return right;

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
				this.storePathFragment(sourceLE.currentPathFragment);
				sourceLE.currentPathFragment = null;
			}

			/* Add the next node in the path to the path fragment. */
			if(sourceLE.currentPathFragment == null) {
				sourceLE.currentPathFragment = new PathFragment(edge.getFrom());
				sourceLE.currentPathFragment.condition = condition;
			}

			sourceLE.currentPathFragment.path.add(edge.getTo());
			
		}
		
		/* If there is a new or updated edge condition and the edge is
		 * unchanged, add it to our set of interesting conditions. */
		if(edge.changeType == ChangeType.UNCHANGED && condition != null && condition.getChangeType() == ChangeType.INSERTED) {
			this.storeInterestingCondition(condition);
		}
		
		/* If this is the exit node, add the current path fragment to the path
		 * fragment list. */
		if(edge.getTo().getName().equals("FUNCTION_EXIT") && sourceLE.currentPathFragment != null) {
			this.storePathFragment(sourceLE.currentPathFragment);
			sourceLE.currentPathFragment = null;
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
	
	/**
	 * Stores the given path fragment in the path fragment list for the current
	 * function being analyzed.
	 * @param pathFragment The path fragment to store.
	 */
	private void storePathFragment(PathFragment pathFragment) {
		
		/* Initialize the fragment list for this function if needed. */
		if(!this.pathFragments.containsKey(this.getCurrentCFGIdentity())) {
			this.pathFragments.put(this.getCurrentCFGIdentity(), new LinkedList<PathFragment>());
		}
		
		/* Store the path fragment for this function. */
		this.pathFragments.get(this.getCurrentCFGIdentity()).add(pathFragment);

	}
	
	private void storeInterestingCondition(AstNode condition) {
		
		/* Initialize the condition list for this function if needed. */
		if(!this.interestingConditions.containsKey(this.getCurrentCFGIdentity())) {
			this.interestingConditions.put(this.getCurrentCFGIdentity(), new LinkedList<AstNode>());
		}
		
		/* Store the interesting condition for this function. */
		this.interestingConditions.get(this.getCurrentCFGIdentity()).add(condition);
		
	}

}