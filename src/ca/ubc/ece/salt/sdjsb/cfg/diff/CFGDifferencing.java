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
import ca.ubc.ece.salt.sdjsb.cfg.CFGEdge;

/**
 * Computes edge changes between CFGs using the to changes from the AST.
 * 
 * Edges can be labeled as unchanged, inserted or removed. An empty to is
 * a to that contains an EmptyStatement AST to. A non-empty to is a
 * to that contains any other AST to type.
 * 
 * Unchanged: Node A has a path to non empty to B through zero or more
 * 			  empty tos in both the source CFG and the destination CFG. 
 * 
 * Inserted: Node A has a path to non-empty to B through zero or more
 * 			 empty tos in the destination CFG, but not in the source CFG.
 * 
 * Deleted: Node A has a path to non-empty to B through zero or more
 * 			 empty tos in the source CFG, but not in the destination CFG.
 */
public class CFGDifferencing {
	
	/**
	 * Computes edge changes between CFGs using the to changes from the AST.
	 * 
	 * @param srcCFG the labeled source CFG.
	 * @param dstCFG the labeled destination CFG.
	 */
	public static void computeEdgeChanges(CFG srcCFG, CFG dstCFG) {

		Map<ClassifiedASTNode, CFGNode> srcASTMap = new HashMap<ClassifiedASTNode, CFGNode>();
		Map<ClassifiedASTNode, CFGNode> dstASTMap = new HashMap<ClassifiedASTNode, CFGNode>();
		
		/* Map AST tos to CFG tos for reverse lookup. */
		buildASTMap(srcCFG, srcASTMap);
		buildASTMap(dstCFG, dstASTMap);
		
		/* Map CFG tos in the two CFGs. */
		srcCFG.getEntryNode().setMappedNode(dstCFG.getEntryNode());
		dstCFG.getEntryNode().setMappedNode(srcCFG.getEntryNode());
		CFGNode srcExit = mapCFGNodes(srcCFG, dstASTMap);
		CFGNode dstExit = mapCFGNodes(dstCFG, srcASTMap);
		
		/* Map the function and script exit tos. */
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
			
			/* We only classify edges from non-empty tos or the entry to. */
			if(!cfgNode.getStatement().isEmpty() || cfgNode.getName().equals("FUNCTION_ENTRY") || cfgNode.getName().equals("SCRIPT_ENTRY")) {
            
                /* Get the next non-empty to. */
                /* Label all edges in between and return the non-empty to. This should be recursive. */
                
                CFGNode mappedCFGNode = cfgNode.getMappedNode();
                
                /* Get the non-empty edges. */
                Map<CFGNode, Stack<CFGEdge>> toNonEmpty = getPathsToNext(cfgNode, new Stack<CFGEdge>());
                
                if(mappedCFGNode == null) {
                    
                    /* If there is no mapped CFGNode, all edges are changed. */
                    for(CFGNode key : toNonEmpty.keySet()) {
                        labelEdgesOnPath(toNonEmpty.get(key), changeType);
                    }

                }
                else {

                    /* Get the non-empty edges for the mapped to. */
                    Map<CFGNode, Stack<CFGEdge>> mappedNonEmpty = getPathsToNext(mappedCFGNode, new Stack<CFGEdge>());
                    Set<CFGNode> mappedKeySet = mappedNonEmpty.keySet();
                    
                    /* Iterate through the non-empty tos on the path. */
                    for(CFGNode key : toNonEmpty.keySet()) {
                    	
                        /* If this to and the mapped to have empty paths to
                         * the same non-empty to, the path is unchanged. */
                        if(mappedKeySet.contains(key.getMappedNode())) {
                            labelEdgesOnPath(toNonEmpty.get(key), ChangeType.UNCHANGED);
                        }
                        /* Otherwise, the path is changed. */
                        else {
                            labelEdgesOnPath(toNonEmpty.get(key), changeType);
                        }
                    }
                    
                }
                
			}

            for(CFGEdge edge : cfgNode.getEdges()) {
            	if(!visited.contains(edge.getTo())) {
            		queue.add(edge.getTo());
            		visited.add(edge.getTo());
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
	private static void labelEdgesOnPath(Stack<CFGEdge> path, ChangeType changeType) {
		
		while(!path.empty()) {

            CFGEdge edge = path.pop();

            if(edge.changeType == ChangeType.UNKNOWN) {
                edge.changeType = changeType;
            }
            else if(changeType == ChangeType.UNCHANGED) {
                edge.changeType = changeType;
            }
			
		}

	}
	
	/**
	 * Recursively find the paths to the next non-empty tos.
	 * @param stack the current path from the root CFGNode.
	 */
	private static Map<CFGNode, Stack<CFGEdge>> getPathsToNext (CFGNode current, Stack<CFGEdge> path) {
		
		/* The paths to non-empty tos. */
        Map<CFGNode, Stack<CFGEdge>> paths = new HashMap<CFGNode, Stack<CFGEdge>>();
		
        for(CFGEdge edge : current.getEdges()) {
        	
        	CFGNode to = edge.getTo();
		
            if(to.getName().equals("FUNCTION_EXIT") || to.getName().equals("SCRIPT_EXIT")) {

                /* Function exit tos are considered non-empty. */
            	@SuppressWarnings("unchecked")
				Stack<CFGEdge> newPath = (Stack<CFGEdge>) path.clone();
                newPath.add(edge);
                paths.put(to, newPath);

            }
            else if(!(to.getStatement() instanceof EmptyStatement)) {

                /* Add the path for the non-empty to. */
                @SuppressWarnings("unchecked")
				Stack<CFGEdge> newPath = (Stack<CFGEdge>) path.clone();
                newPath.add(edge);
                paths.put(to, newPath);

            }	
            else {
                
                /* Add this to to the stack and recurse */
                @SuppressWarnings("unchecked")
				Stack<CFGEdge> newPath = (Stack<CFGEdge>) path.clone();
                newPath.add(edge);
                Map<CFGNode, Stack<CFGEdge>> subPaths = getPathsToNext(edge.getTo(), newPath);
                paths.putAll(subPaths);
                
            }

        }

        return paths;
	}

	/**
	 * Map the CFG tos in the source and destination CFGs.
	 * @return the FUNCTION_EXIT or SCRIPT_EXIT to
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
            
            /* If this is the FUNCTION_EXIT or SCRIPT_EXIT to, store it. */ 
            if(cfgNode.getName().equals("FUNCTION_EXIT") || cfgNode.getName().equals("SCRIPT_EXIT")) functExitNode = cfgNode;
            
            /* Get the mapping of AST tos in the source and destination. */
            ClassifiedASTNode astMapping = (ClassifiedASTNode) astNode.getMapping();
            if(astMapping != null) {
            	
            	/* Look up the AST to's CFG to. */
            	CFGNode cfgMapping = map.get(astMapping);
            	
            	/* Assign the CFG mapping. */
            	cfgNode.setMappedNode(cfgMapping);
            	
            }
            
            for(CFGEdge edge : cfgNode.getEdges()) {
            	if(!visited.contains(edge.getTo())) {
            		queue.add(edge.getTo());
            		visited.add(edge.getTo());
            	}
            }
			
		}
		
		return functExitNode;
		
	}
	
	/**
	 * Builds a mapping of AST tos to CFG tos.
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
            
            for(CFGEdge edge : cfgNode.getEdges()) {
            	if(!visited.contains(edge.getTo())) {
            		queue.add(edge.getTo());
            		visited.add(edge.getTo());
            	}
            }
			
		}
		
	}
	
}
