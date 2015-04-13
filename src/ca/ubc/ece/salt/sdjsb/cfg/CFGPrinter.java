package ca.ubc.ece.salt.sdjsb.cfg;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode.ChangeType;

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
		serial += "to [ style = filled fillcolor = \"white\" ];\n";
		
		while(true) {
			
			CFGNode current = queue.poll();
			
			if(current == null) {
				break;
			}
			
			/* Print the display settings for this to. */
			String label = current.getStatement().getCFGLabel();

			if(label.equals(";")) label = "";

			serial += "\t" + current.getId() + " [ fillcolor = \"" + getFillColor(current.getStatement().getChangeType()) + "\" label = \"" + label + "\" ];\n";
			
			for(CFGEdge edge : current.getEdges()) {

                serial += "\t" + current.getId() + " -> " + edge.getTo().getId();

				if(edge.getCondition() != null) {
					serial += " [ color = \"" + getFillColor(edge.changeType) + "\" fontcolor = \"" + getFillColor(edge.getCondition().getChangeType()) + "\" label = \"" + edge.getCondition().getCFGLabel() + "\" ];\n";
				}
				else {
                    serial += " [ color = \"" + getFillColor(edge.changeType) + "\" ];\n";
				}
				
				if(!visited.contains(edge.getTo())) { 
					queue.add(edge.getTo());
					visited.add(edge.getTo());
				}
			}

		}
		
		serial += "}";
		
		return serial;
		
	}
	
	/**
	 * @param changeType The change type of the underlying AstNode.
	 * @return The color to fill the GraphViz CFGNode.
	 */
	private static String getFillColor(ChangeType changeType) {

		switch(changeType) {
		case INSERTED:
			return "green";
		case REMOVED:
			return "red";
		case MOVED:
			return "yellow";
		case UPDATED:
			return "blue";
		case UNCHANGED:
			return "grey";
		case UNKNOWN:
		default:
			return "black";
		}

	}
	
	/**
	 * Prints a CFG as a directed adjacency list. That is, only tos from
	 * outgoing edges are listed as adjacent to a to. 
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
			
			for(CFGEdge edge : current.getEdges()) {

				if(edge.getCondition() != null) {
					serial += edge.getCondition().getCFGLabel() + ":" + edge.getTo().getId() + ",";
				}
				else {
                    serial += edge.getTo().getId() + ",";
				}
				
				if(!visited.contains(edge.getTo())) { 
					queue.add(edge.getTo());
					visited.add(edge.getTo());
				}
			}
			
			if(serial.charAt(serial.length() - 1) == ',') serial = serial.substring(0, serial.length() - 1);
			serial += "}";
			
			if(queue.peek() != null) serial += ",";
			
		}
		
		return serial;
	}

}
