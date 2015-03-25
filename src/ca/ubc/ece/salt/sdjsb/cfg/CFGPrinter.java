package ca.ubc.ece.salt.sdjsb.cfg;

import java.util.Stack;

/**
 * Prints a control flow graph. Not thread safe.
 */
public class CFGPrinter {
	
	private CFG cfg;
	private Stack<CFGNode> mergeStack;
	
	public CFGPrinter(CFG cfg) {
		this.cfg = cfg;
	}
	
	/**
	 * Prints a serial representation of the CFG.
	 */
	public String print() {
		this.mergeStack = new Stack<CFGNode>();
		return printCFG(this.cfg.getEntryNode());
	}

	/**
	 * Recursively prints a serial representation of a CFGNode.
	 */
	private String printCFG(CFGNode node) {
		
		if(node instanceof FunctionExitCFGNode || node instanceof ScriptExitCFGNode) {
			return node.toString();
		}

		else if(node instanceof LinearCFGNode) {
			LinearCFGNode linearNode = (LinearCFGNode) node;

            if(!this.mergeStack.isEmpty() && this.mergeStack.peek() == linearNode.getNext()) {
                /* We are not at the bottom level of the merge. */
                return node.toString();
            } 

			return node.toString() + "->" + printCFG(((LinearCFGNode) node).getNext());
		}

		else if(node instanceof IfCFGNode) {
			IfCFGNode ifNode = (IfCFGNode) node;
			String s;
			
			this.mergeStack.push(ifNode.mergeNode);
			if(ifNode.getFalseBranch() != ifNode.mergeNode && ifNode.getTrueBranch() != ifNode.mergeNode) {
				/* There is an else branch. */
                s = node.toString() + "?{" + printCFG(ifNode.getTrueBranch()) + ":" + printCFG(ifNode.getFalseBranch()) + "}";
			}
			else if(ifNode.getFalseBranch() == ifNode.mergeNode && ifNode.getTrueBranch() != ifNode.mergeNode) {
				/* There is no else branch. */
                s = node.toString() + "?{" + printCFG(ifNode.getTrueBranch()) + "}";
			}
			else if(ifNode.getFalseBranch() != ifNode.mergeNode && ifNode.getTrueBranch() == ifNode.mergeNode) {
				/* There is no else branch. */
                s = node.toString() + "?{" + printCFG(ifNode.getTrueBranch()) + "}";
			}
			else {
				s = "ERROR";
			}
            this.mergeStack.pop();

            if(!this.mergeStack.isEmpty() && this.mergeStack.peek() == ifNode.mergeNode) {
                /* We are not at the bottom level of the merge. */
                return s;
            } 

            /* We are at the bottom level of the merge. */
			return s + "->" + printCFG(((IfCFGNode) node).mergeNode);
		}
		
		return "UNKNOWN";
		
	}

}
