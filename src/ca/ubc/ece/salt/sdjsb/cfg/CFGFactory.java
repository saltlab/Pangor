package ca.ubc.ece.salt.sdjsb.cfg;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.mozilla.javascript.Node;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.Block;
import org.mozilla.javascript.ast.BreakStatement;
import org.mozilla.javascript.ast.ContinueStatement;
import org.mozilla.javascript.ast.ExpressionStatement;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.IfStatement;
import org.mozilla.javascript.ast.ScriptNode;
import org.mozilla.javascript.ast.SwitchStatement;
import org.mozilla.javascript.ast.TryStatement;
import org.mozilla.javascript.ast.VariableDeclaration;
import org.mozilla.javascript.ast.WithStatement;

/**
 * Builds a control flow graph.
 * @author qhanam
 */
public class CFGFactory {
	
	/**
	 * Builds intra-procedural control flow graphs for the given artifact.
	 * @param root
	 * @return
	 */
	public static List<CFGNode> createCFGs(AstRoot root) {
		
		List<CFGNode> cfgs = new LinkedList<CFGNode>();
        List<LinearCFGNode> exitNodes;
		
		/* First analyze the script. */
        LinearCFGNode scriptEntry = new ScriptEntryCFGNode(root);
        cfgs.add(scriptEntry);
        
        /* Build the CFG for the function get back a list of exit nodes to link. */
        exitNodes = CFGFactory.buildCFG(scriptEntry, root);
        
        /* For now, all exit nodes should point to the FunctionExitCFGNode. */
        ScriptExitCFGNode scriptExitNode = new ScriptExitCFGNode(root);
        for(LinearCFGNode exitNode : exitNodes) {
            exitNode.setNext(scriptExitNode);
        }
		
		/* Get the list of functions in the script. */
		List<FunctionNode> functions = FunctionNodeVisitor.getFunctions(root);
		
		/* For each function, generate its CFG. */
		for (FunctionNode function : functions) {
			
			/* Create an entry node for this function and add it to our CFG list. */
			LinearCFGNode entry = new FunctionEntryCFGNode(function);
			cfgs.add(entry);

			/* Build the CFG for the function get back a list of exit nodes to link. */
			exitNodes = CFGFactory.buildCFG(entry, function.getBody());
			
			/* For now, all exit nodes should point to the FunctionExitCFGNode. */
			FunctionExitCFGNode functionExitNode = new FunctionExitCFGNode(function);
			for(LinearCFGNode exitNode : exitNodes) {
				exitNode.setNext(functionExitNode);
			}
		}
		
		return cfgs;
	}
	
	/**
	 * Prints a serial representation of the CFG.
	 * @param node
	 */
	public static String printCFG(CFGNode node) {
		
		if(node instanceof FunctionExitCFGNode || node instanceof ScriptExitCFGNode) {
			return node.toString();
		}
		else if(node instanceof LinearCFGNode) {
			return node.toString() + "->" + CFGFactory.printCFG(((LinearCFGNode) node).getNext());
		}
		
		return "UNKNOWN";
		
	}

	/**
	 * Builds a control flow subgraph.
	 * @param entry The entry point for the subgraph.
	 * @param exit The exit point for the subgraph.
	 * @return A list of exit nodes for the subgraph.
	 */
	private static List<LinearCFGNode> buildCFG(LinearCFGNode entry, AstNode block) {
		List<LinearCFGNode> exitNodes = new LinkedList<LinearCFGNode>();
		Queue<AstNode> statements = CFGFactory.getStatementList(block);
		LinearCFGNode current = entry;
		
		/* Iterate through the statements and build the graph. */
		while(!statements.isEmpty()) {
			AstNode statement = statements.remove();
			LinearCFGNode node = new LinearCFGNode(statement);
			current.setNext(node);
			current = node;
		}
		
		/* We only have one exit node right now. */
		exitNodes.add(current);

		return exitNodes;
	}

	/**
	 * Get a list of statements for a block without exploring the subtrees of
	 * branch statements.
	 * @param astNode A statement or block.
	 * @return A queue of statements in the block or a queue containing the
	 * 		   single statement if astNode is not a block.
	 */
	private static Queue<AstNode> getStatementList(AstNode astNode) {
		Queue<AstNode> statements = new LinkedList<AstNode>();
		
		if(astNode instanceof Block || astNode instanceof AstRoot) {
			/* Get the block's children. They should all be statements. */
			for(Node node : astNode) {
				
                assert(CFGFactory.isStatement(node));
                statements.add((AstNode) node);
				
			}
		}
		else {
            assert(CFGFactory.isStatement(astNode));
            statements.add(astNode);
		}
		
		return statements;
	}
	
	/**
	 * Check if an AstNode is a statement.
	 * @param node
	 * @return
	 */
	private static boolean isStatement(Node node) {

		if(node instanceof VariableDeclaration ||
			node instanceof TryStatement || 
			node instanceof IfStatement ||
			node instanceof WithStatement ||
			node instanceof BreakStatement ||
			node instanceof ContinueStatement ||
			node instanceof SwitchStatement ||
			node instanceof ExpressionStatement) {
			return true;
		}

		return false;
	}

}
