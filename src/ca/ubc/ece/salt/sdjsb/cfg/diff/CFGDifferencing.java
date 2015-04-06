package ca.ubc.ece.salt.sdjsb.cfg.diff;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

import org.mozilla.javascript.ast.EmptyStatement;

import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode;
import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode.ChangeType;
import ca.ubc.ece.salt.sdjsb.cfg.CFG;
import ca.ubc.ece.salt.sdjsb.cfg.CFGNode;
import ca.ubc.ece.salt.sdjsb.cfg.Edge;

/**
 * Computes edge changes between CFGs using the node changes from the AST.
 * 
 * Edges can be labeled as unchanged, inserted or removed. An empty node is
 * a node that contains an EmptyStatement AST node. A non-empty node is a
 * node that contains any other AST node type.
 * 
 * Unchanged: Node A has a path to non empty node B through zero or more
 * 			  empty nodes in both the source CFG and the destination CFG. 
 * 
 * Inserted: Node A has a path to non-empty node B through zero or more
 * 			 empty nodes in the destination CFG, but not in the source CFG.
 * 
 * Deleted: Node A has a path to non-empty node B through zero or more
 * 			 empty nodes in the source CFG, but not in the destination CFG.
 */
public class CFGDifferencing {
	
	/**
	 * Computes edge changes between CFGs using the node changes from the AST.
	 * 
	 * @param srcCFG the labeled source CFG.
	 * @param dstCFG the labeled destination CFG.
	 */
	public static void computeEdgeChanges(CFG srcCFG, CFG dstCFG) {

		Map<ClassifiedASTNode, CFGNode> srcASTMap = new HashMap<ClassifiedASTNode, CFGNode>();
		Map<ClassifiedASTNode, CFGNode> dstASTMap = new HashMap<ClassifiedASTNode, CFGNode>();
		
		/* Map AST nodes to CFG nodes for reverse lookup. */
		buildASTMap(srcCFG, srcASTMap);
		buildASTMap(dstCFG, dstASTMap);
		
		/* Map CFG nodes in the two CFGs. */
		srcCFG.getEntryNode().setMappedNode(dstCFG.getEntryNode());
		dstCFG.getEntryNode().setMappedNode(srcCFG.getEntryNode());
		CFGNode srcExit = mapCFGNodes(srcCFG, dstASTMap);
		CFGNode dstExit = mapCFGNodes(dstCFG, srcASTMap);
		
		/* Map the function and script exit nodes. */
		srcExit.setMappedNode(dstExit);
		dstExit.setMappedNode(srcExit);
		
		/* Classify the edges as inserted, deleted or unchanged. */
		classifyEdges(srcCFG, ChangeType.REMOVED);
		classifyEdges(dstCFG, ChangeType.INSERTED);

	}

	/** 
	 * Determines the change operation applied to the edges from source to
	 * destination.
	 * @param cfg The control flow graph to classify.
	 */
	private static void classifyEdges (CFG cfg, ChangeType changeType) {
		
		Set<CFGNode> visited = new HashSet<CFGNode>();
		Queue<CFGNode> queue = new LinkedList<CFGNode>();
		queue.add(cfg.getEntryNode());
		visited.add(cfg.getEntryNode());
		
		while(!queue.isEmpty()) {

			CFGNode cfgNode = queue.remove();
			
			/* We only classify edges from non-empty nodes or the entry node. */
			if(!cfgNode.getStatement().isEmpty() || cfgNode.getName().equals("FUNCTION_ENTRY") || cfgNode.getName().equals("SCRIPT_ENTRY")) {
            
                /* Get the next non-empty node. */
                /* Label all edges in between and return the non-empty node. This should be recursive. */
                
                CFGNode mappedCFGNode = cfgNode.getMappedNode();
                
                /* Get the non-empty edges. */
                Map<CFGNode, Stack<Edge>> nodeNonEmpty = getPathsToNext(cfgNode, new Stack<Edge>());
                
                if(mappedCFGNode == null) {
                    
                    /* If there is no mapped CFGNode, all edges are changed. */
                    for(CFGNode key : nodeNonEmpty.keySet()) {
                        labelEdgesOnPath(nodeNonEmpty.get(key), changeType);
                    }

                }
                else {

                    /* Get the non-empty edges for the mapped node. */
                    Map<CFGNode, Stack<Edge>> mappedNonEmpty = getPathsToNext(mappedCFGNode, new Stack<Edge>());
                    Set<CFGNode> mappedKeySet = mappedNonEmpty.keySet();
                    
                    /* Iterate through the non-empty nodes on the path. */
                    for(CFGNode key : nodeNonEmpty.keySet()) {
                    	
                        /* If this node and the mapped node have empty paths to
                         * the same non-empty node, the path is unchanged. */
                        if(mappedKeySet.contains(key.getMappedNode())) {
                            labelEdgesOnPath(nodeNonEmpty.get(key), ChangeType.UNCHANGED);
                        }
                        /* Otherwise, the path is changed. */
                        else {
                            labelEdgesOnPath(nodeNonEmpty.get(key), changeType);
                        }
                    }
                    
                }
                
			}

            for(Edge edge : cfgNode.getEdges()) {
            	if(!visited.contains(edge.node)) {
            		queue.add(edge.node);
            		visited.add(edge.node);
            	}
            }
			
		}
		
	}
	
	/**
	 * Apply labels to all edges on the path.
	 * 
	 * The type of the edge will be rewritten if the edge change type is
	 * unknown or if the change type being applied is unchanged.
	 * @param path
	 * @param changeType The change type to apply to the edge.
	 */
	private static void labelEdgesOnPath(Stack<Edge> path, ChangeType changeType) {
		
		while(!path.empty()) {

            Edge edge = path.pop();

            if(edge.changeType == ChangeType.UNKNOWN) {
                edge.changeType = changeType;
            }
            else if(changeType == ChangeType.UNCHANGED) {
                edge.changeType = changeType;
            }
			
		}

	}
	
	/**
	 * Recursively find the paths to the next non-empty nodes.
	 * @param stack the current path from the root CFGNode.
	 */
	private static Map<CFGNode, Stack<Edge>> getPathsToNext (CFGNode current, Stack<Edge> path) {
		
		/* The paths to non-empty nodes. */
        Map<CFGNode, Stack<Edge>> paths = new HashMap<CFGNode, Stack<Edge>>();
		
        for(Edge edge : current.getEdges()) {
        	
        	CFGNode node = edge.node;
		
            if(node.getName().equals("FUNCTION_EXIT") || node.getName().equals("SCRIPT_EXIT")) {

                /* Function exit nodes are considered non-empty. */
            	@SuppressWarnings("unchecked")
				Stack<Edge> newPath = (Stack<Edge>) path.clone();
                newPath.add(edge);
                paths.put(node, newPath);

            }
            else if(!(node.getStatement() instanceof EmptyStatement)) {

                /* Add the path for the non-empty node. */
                @SuppressWarnings("unchecked")
				Stack<Edge> newPath = (Stack<Edge>) path.clone();
                newPath.add(edge);
                paths.put(node, newPath);

            }	
            else {
                
                /* Add this node to the stack and recurse */
                @SuppressWarnings("unchecked")
				Stack<Edge> newPath = (Stack<Edge>) path.clone();
                newPath.add(edge);
                Map<CFGNode, Stack<Edge>> subPaths = getPathsToNext(edge.node, newPath);
                paths.putAll(subPaths);
                
            }

        }

        return paths;
	}

	/**
	 * Map the CFG nodes in the source and destination CFGs.
	 * @return the FUNCTION_EXIT or SCRIPT_EXIT node
	 */
	private static CFGNode mapCFGNodes(CFG cfg, Map<ClassifiedASTNode, CFGNode> map) {
		
		CFGNode functExitNode = null;
		Set<CFGNode> visited = new HashSet<CFGNode>();
		Queue<CFGNode> queue = new LinkedList<CFGNode>();
		queue.add(cfg.getEntryNode());
		visited.add(cfg.getEntryNode());
		
		/* Do a breadth-first traversal of the graph. */
		
		while(!queue.isEmpty()) {

			CFGNode cfgNode = queue.remove();
            ClassifiedASTNode astNode = cfgNode.getStatement();
            
            /* If this is the FUNCTION_EXIT or SCRIPT_EXIT node, store it. */ 
            if(cfgNode.getName().equals("FUNCTION_EXIT") || cfgNode.getName().equals("SCRIPT_EXIT")) functExitNode = cfgNode;
            
            /* Get the mapping of AST nodes in the source and destination. */
            ClassifiedASTNode astMapping = (ClassifiedASTNode) astNode.getMapping();
            if(astMapping != null) {
            	
            	/* Look up the AST node's CFG node. */
            	CFGNode cfgMapping = map.get(astMapping);
            	
            	/* Assign the CFG mapping. */
            	cfgNode.setMappedNode(cfgMapping);
            	
            }
            
            for(Edge edge : cfgNode.getEdges()) {
            	if(!visited.contains(edge.node)) {
            		queue.add(edge.node);
            		visited.add(edge.node);
            	}
            }
			
		}
		
		return functExitNode;
		
	}
	
	/**
	 * Builds a mapping of AST nodes to CFG nodes.
	 * @param cfg the CFG to map.
	 */
	private static void buildASTMap(CFG cfg, Map<ClassifiedASTNode, CFGNode> map) {

		Set<CFGNode> visited = new HashSet<CFGNode>();
		Queue<CFGNode> queue = new LinkedList<CFGNode>();
		queue.add(cfg.getEntryNode());
		visited.add(cfg.getEntryNode());
		
		while(!queue.isEmpty()) {

			CFGNode cfgNode = queue.remove();
            ClassifiedASTNode astNode = cfgNode.getStatement();
            map.put(astNode, cfgNode);
            
            for(Edge edge : cfgNode.getEdges()) {
            	if(!visited.contains(edge.node)) {
            		queue.add(edge.node);
            		visited.add(edge.node);
            	}
            }
			
		}
		
	}
	
}
