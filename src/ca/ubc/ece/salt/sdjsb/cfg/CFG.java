package ca.ubc.ece.salt.sdjsb.cfg;

import java.util.LinkedList;
import java.util.List;

public class CFG {
	
	private CFGNode entryNode;
	private List<CFGNode> exitNodes;
	
	public CFG(CFGNode entryNode) { 
		this.entryNode = entryNode;
		this.exitNodes = new LinkedList<CFGNode>();
	}
	
	/**
	 * Returns the entry node for this CFG.
	 * @return The entry CFGNode.
	 */
	public CFGNode getEntryNode() {
		return entryNode;
	}
	
	/**
	 * Add an exit node to this CFG.
	 * @param node The last node before exiting an execution branch.
	 */
	public void addExitNode(CFGNode node) {
		this.exitNodes.add(node);
	}
	
	/**
	 * Adds all the exit nodes in the list.
	 * @param nodes
	 */
	public void addAllExitNodes(List<CFGNode> nodes) {
		this.exitNodes.addAll(nodes);
	}
	
	/**
	 * Get the exit nodes for this graph.
	 * @return The list of exit points.
	 */
	public List<CFGNode> getExitNodes() {
		return this.exitNodes;
	}
	
	/**
	 * All exit nodes should merge into the given node.
	 */
	public void mergeInto(CFGNode node) {
		
		for(CFGNode exitNode : this.exitNodes) {
			exitNode.mergeInto(node);
		}
		
	}

}
