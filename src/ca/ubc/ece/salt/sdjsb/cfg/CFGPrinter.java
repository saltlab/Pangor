package ca.ubc.ece.salt.sdjsb.cfg;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class CFGPrinter {
	
	public static String print(Output type, CFG cfg) {
		switch(type) {
            case ADJACENCY_LIST:
            	return CFGPrinter.adjacencyList(cfg);
            case DOT:
            	return CFGPrinter.graphViz(cfg);
            default:
            	return null;
		}
	}

	/**
	 * Defines types of output that can be printed.
	 * @author qhanam
	 *
	 */
	public enum Output {
		ADJACENCY_LIST,
		DOT,
		NONE
	}
	
	/**
	 * Prints a CFG in the DOT language (.gv file). This can be input into 
	 * graphviz to produce an SVG.
	 * @param cfg The control flow graph.
	 * @return A .gv file (DOT language format).
	 */
	public static String graphViz(CFG cfg) {

		Queue<CFGNode> queue = new LinkedList<CFGNode>();
		Set<CFGNode> visited = new HashSet<CFGNode>();
		queue.add(cfg.getEntryNode());
		visited.add(cfg.getEntryNode());
		String serial = "digraph control_flow_graph {\n";
		
		while(true) {
			
			CFGNode current = queue.poll();
			
			if(current == null) {
				break;
			}
			
			for(Edge edge : current.getEdges()) {

                serial += current.getName() + "_" + current.getId() + " -> " + edge.node.getName() +  "_" + edge.node.getId();

				if(edge.condition != null) {
					serial += " [ label = \"" + edge.condition.toSource() + "\" ];\n";
				}
				else {
                    serial += ";\n";
				}
				
				if(!visited.contains(edge.node)) { 
					queue.add(edge.node);
					visited.add(edge.node);
				}
			}

		}
		
		serial += "}";
		
		return serial;
		
	}
	
	/**
	 * Prints a CFG as a directed adjacency list. That is, only nodes from
	 * outgoing edges are listed as adjacent to a node. 
	 * 
	 * i.e., [t1(id1){oe1-1, oe1-2, ... , oe1-n},t2(id2){oe2-1, oe-2-2, ... , oe2-n}, ... , tm(idm){oem-1, oem-2, ... , oem-n}]
	 * @param cfg The control flow graph.
	 * @return An adjacency list with only outgoing edges.
	 */
	public static String adjacencyList(CFG cfg) {
		
		Queue<CFGNode> queue = new LinkedList<CFGNode>();
		Set<CFGNode> visited = new HashSet<CFGNode>();
		queue.add(cfg.getEntryNode());
		visited.add(cfg.getEntryNode());
		String serial = "";
		
		while(true) {
			
			CFGNode current = queue.poll();
			
			if(current == null) {
				break;
			}
			
			serial += current.getName() + "(" + current.getId() + "){";
			
			for(Edge edge : current.getEdges()) {

				if(edge.condition != null) {
					serial += edge.condition.toSource() + ":" + edge.node.getId() + ",";
				}
				else {
                    serial += edge.node.getId() + ",";
				}
				
				if(!visited.contains(edge.node)) { 
					queue.add(edge.node);
					visited.add(edge.node);
				}
			}
			
			if(serial.charAt(serial.length() - 1) == ',') serial = serial.substring(0, serial.length() - 1);
			serial += "}";
			
			if(queue.peek() != null) serial += ",";
			
		}
		
		return serial;
	}

}
