package ca.ubc.ece.salt.sdjsb.cfg;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * A low(er) level control flow graph or subgraph.
 * 
 * The control flow graph contains an entry node where the graph begins. It
 * also keeps track of statements that exit the CFG. These include the last
 * statements in a block that exit the graph and jump statements including
 * break, continue, throw and return. 
 */
public class CFG {
	
	private CFGNode entryNode;
	private List<CFGNode> exitNodes;
	private List<CFGNode> breakNodes;
	private List<CFGNode> continueNodes;
	private List<CFGNode> throwNodes;
	private List<CFGNode> returnNodes;
	
	public CFG(CFGNode entryNode) { 
		this.entryNode = entryNode;
		this.exitNodes = new LinkedList<CFGNode>();
		this.breakNodes = new LinkedList<CFGNode>();
		this.continueNodes = new LinkedList<CFGNode>();
		this.throwNodes = new LinkedList<CFGNode>();
		this.returnNodes = new LinkedList<CFGNode>();
	}
	
	/** 
	 * @return a copy of the CFG.
	 */
	public CFG copy() {

		CFGNode entryNodeCopy = CFGNode.copy(this.entryNode);
		
		/* Keep track of the nodes we have copied. */
		Map<CFGNode, CFGNode> newNodes = new HashMap<CFGNode, CFGNode>();
		newNodes.put(this.entryNode, entryNodeCopy);
		
		/* Depth first traversal. */
		Stack<CFGNode> stack = new Stack<CFGNode>();
		stack.push(entryNodeCopy);

		/* Traverse and copy the CFG. */
		while(!stack.isEmpty()) {
			CFGNode node = stack.pop();

			/* Copy the list of edges. */
			List<CFGEdge> copiedEdges = new LinkedList<CFGEdge>(node.getEdges());
			
			/* Re-assign the 'to' part of the edge. Copy the node if it hasn't
			 * been copied yet. */
			for(CFGEdge copiedEdge : copiedEdges) {
				CFGNode nodeCopy = newNodes.get(copiedEdge.getTo());
				if(nodeCopy == null) {
					nodeCopy = CFGNode.copy(copiedEdge.getTo());
					newNodes.put(copiedEdge.getTo(), nodeCopy);
					stack.push(nodeCopy);
				}
				copiedEdge.setTo(nodeCopy);
			}
			
			/* Assign the new edges to the current node. */
			node.setEdges(copiedEdges);
		}
		
		/* Copy the CFG and add all the jump nodes. */
		CFG cfg = new CFG(entryNodeCopy);
		
		for(CFGNode exitNode : this.exitNodes) {
			CFGNode node = newNodes.get(exitNode);
			if(node != null) cfg.addExitNode(node);
		}

		for(CFGNode node : this.breakNodes) {
			CFGNode copy = newNodes.get(node);
			if(copy != null) cfg.addExitNode(copy);
		}

		for(CFGNode node : this.continueNodes) {
			CFGNode copy = newNodes.get(node);
			if(copy != null) cfg.addExitNode(copy);
		}

		for(CFGNode node : this.returnNodes) {
			CFGNode copy = newNodes.get(node);
			if(copy != null) cfg.addExitNode(copy);
		}

		for(CFGNode node : this.throwNodes) {
			CFGNode copy = newNodes.get(node);
			if(copy != null) cfg.addExitNode(copy);
		}
		
		return cfg;
	}
	
	/**
	 * Returns the entry node for this CFG.
	 * @return The entry Node.
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
	 * Add a break node to this CFG.
	 * @param node The last node before breaking an execution branch.
	 */
	public void addBreakNode(CFGNode node) {
		this.breakNodes.add(node);
	}
	
	/**
	 * Adds all the break nodes in the list.
	 * @param nodes
	 */
	public void addAllBreakNodes(List<CFGNode> nodes) {
		this.breakNodes.addAll(nodes);
	}
	
	/**
	 * Get the break nodes for this graph.
	 * @return The list of break points.
	 */
	public List<CFGNode> getBreakNodes() {
		return this.breakNodes;
	}

	/**
	 * Add an continue node to this CFG.
	 * @param node The last node before continuing an execution branch.
	 */
	public void addContinueNode(CFGNode node) {
		this.continueNodes.add(node);
	}
	
	/**
	 * Adds all the continue nodes in the list.
	 * @param nodes
	 */
	public void addAllContinueNodes(List<CFGNode> nodes) {
		this.continueNodes.addAll(nodes);
	}
	
	/**
	 * Get the continue nodes for this graph.
	 * @return The list of continue points.
	 */
	public List<CFGNode> getContinueNodes() {
		return this.continueNodes;
	}

	/**
	 * Add an throw node to this CFG.
	 * @param node The last node before continuing an execution branch.
	 */
	public void addThrowNode(CFGNode node) {
		this.throwNodes.add(node);
	}
	
	/**
	 * Adds all the throw nodes in the list.
	 * @param nodes
	 */
	public void addAllThrowNodes(List<CFGNode> nodes) {
		this.throwNodes.addAll(nodes);
	}
	
	/**
	 * Get the throw nodes for this graph.
	 * @return The list of throw points.
	 */
	public List<CFGNode> getThrowNodes() {
		return this.throwNodes;
	}

	/**
	 * Add an return node to this CFG.
	 * @param node The last node before returning an execution branch.
	 */
	public void addReturnNode(CFGNode node) {
		this.returnNodes.add(node);
	}
	
	/**
	 * Adds all the return nodes in the list.
	 * @param nodes
	 */
	public void addAllReturnNodes(List<CFGNode> nodes) {
		this.returnNodes.addAll(nodes);
	}
	
	/**
	 * Get the return nodes for this graph.
	 * @return The list of return points.
	 */
	public List<CFGNode> getReturnNodes() {
		return this.returnNodes;
	}

}
