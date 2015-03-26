package ca.ubc.ece.salt.sdjsb.cfg;

import java.util.LinkedList;
import java.util.List;

import org.mozilla.javascript.Node;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.Block;
import org.mozilla.javascript.ast.BreakStatement;
import org.mozilla.javascript.ast.ContinueStatement;
import org.mozilla.javascript.ast.DoLoop;
import org.mozilla.javascript.ast.ExpressionStatement;
import org.mozilla.javascript.ast.ForLoop;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.IfStatement;
import org.mozilla.javascript.ast.ReturnStatement;
import org.mozilla.javascript.ast.Scope;
import org.mozilla.javascript.ast.SwitchStatement;
import org.mozilla.javascript.ast.TryStatement;
import org.mozilla.javascript.ast.VariableDeclaration;
import org.mozilla.javascript.ast.WhileLoop;
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
	public static List<CFG> createCFGs(AstRoot root) {
		
		/* Store the CFGs from all the functions. */
		List<CFG> cfgs = new LinkedList<CFG>();
		
		/* Start by getting the CFG for the script. */
		CFGNode entry = new ScriptEntryCFGNode(root);
		CFGNode exit = new ScriptExitCFGNode(root);
		
        /* There is one entry point and one exit point for a script and function. */
        CFG cfg = new CFG(entry);
        cfg.addExitNode(exit);
        
        /* Build the CFG for the script. */
        CFG subGraph = CFGFactory.build(root);
        entry.mergeInto(subGraph.getEntryNode());
        subGraph.mergeInto(exit);
        cfgs.add(cfg);
		
		/* Get the list of functions in the script. */
		List<FunctionNode> functions = FunctionNodeVisitor.getFunctions(root);
		
		/* For each function, generate its CFG. */
		for (FunctionNode function : functions) {
			
            /* Start by getting the CFG for the script. */
            entry = new FunctionEntryCFGNode(function);
            exit = new FunctionExitCFGNode(function);
            
            /* There is one entry point and one exit point for a script and function. */
            cfg = new CFG(entry);
            cfg.addExitNode(exit);
            
            /* Build the CFG for the script. */
            subGraph = CFGFactory.build(function);
            entry.mergeInto(subGraph.getEntryNode());
            subGraph.mergeInto(exit);
            
            /* Merge return nodes into function exit node. */
            for(CFGNode node : subGraph.getReturnNodes()) {
            	node.mergeInto(exit);
            }
            
            cfgs.add(cfg);

		}
		
		return cfgs;
	}
	
	/**
	 * Builds a CFG for a block.
	 * @param block The block statement.
	 */
	private static CFG build(Block block) {
		return CFGFactory.buildBlock(block);
	}

	/**
	 * Builds a CFG for a block.
	 * @param block The block statement.
	 */
	private static CFG build(Scope scope) {
		return CFGFactory.buildBlock(scope);
	}
	
	/**
	 * Builds a CFG for a script.
	 * @param block The block statement.
	 */
	private static CFG build(AstRoot script) {
		return CFGFactory.buildBlock(script);
	}

	/**
	 * Builds a CFG for a function or script.
	 * @param block The block statement.
	 */
	private static CFG build(FunctionNode function) {
		return CFGFactory.buildSwitch(function.getBody());
	}

	/**
	 * Builds a CFG for a block, function or script.
	 * @param block
	 * @return The CFG for the block.
	 */
	private static CFG buildBlock(AstNode block) {
		/* Special cases:
		 * 	- First statement in block (set entry point for the CFG and won't need to merge previous into it).
		 * 	- Last statement: The exit nodes for the block will be the same as the exit nodes for this statement.
		 */
		
		CFG cfg = null;
		CFG previous = null;
		
		for(Node statement : block) {
			
			assert(statement instanceof AstNode);

			CFG subGraph = CFGFactory.buildSwitch((AstNode)statement);
			
			if(subGraph != null) {

				if(previous == null) {
					/* The first subgraph we find is the entry point to this graph. */
                    cfg = new CFG(subGraph.getEntryNode());
				}
				else {
					/* Merge the previous subgraph into the entry point of this subgraph. */
                    previous.mergeInto(subGraph.getEntryNode());
				}

                /* Propagate return, continue and break nodes. */
                cfg.addAllReturnNodes(subGraph.getReturnNodes());
                cfg.addAllBreakNodes(subGraph.getBreakNodes());
                cfg.addAllContinueNodes(subGraph.getContinueNodes());

                previous = subGraph;
			}
			
		}
		
		if(previous != null) {

            /* Propagate exit nodes from the last node in the block. */
            cfg.addAllExitNodes(previous.getExitNodes());
		}
		else {
			assert(cfg == null);
		}
		
		return cfg;
	}
	
	/**
	 * Builds a control flow subgraph for an if statement.
	 * @param ifStatement
	 * @return
	 */
	private static CFG build(IfStatement ifStatement) {
		
		IfNode node = new IfNode(ifStatement.getCondition());
		CFG cfg = new CFG(node);
        cfg.addExitNode(node);
		
		CFG trueBranch = CFGFactory.buildSwitch(ifStatement.getThenPart());
		CFG falseBranch = CFGFactory.buildSwitch(ifStatement.getElsePart());
		
		if(trueBranch != null) {
			node.setTrueBranch(trueBranch.getEntryNode());

            /* Propagate exit, return, continue and break nodes. */
			cfg.addAllExitNodes(trueBranch.getExitNodes());
            cfg.addAllReturnNodes(trueBranch.getReturnNodes());
            cfg.addAllBreakNodes(trueBranch.getBreakNodes());
            cfg.addAllContinueNodes(trueBranch.getContinueNodes());
		} 		

		if(falseBranch != null) {
			node.setFalseBranch(falseBranch.getEntryNode());

            /* Propagate exit, return, continue and break nodes. */
			cfg.addAllExitNodes(falseBranch.getExitNodes());
            cfg.addAllReturnNodes(falseBranch.getReturnNodes());
            cfg.addAllBreakNodes(falseBranch.getBreakNodes());
            cfg.addAllContinueNodes(falseBranch.getContinueNodes());
		}
		
		return cfg;
		
	}

	/**
	 * Builds a control flow subgraph for a while statement.
	 * @param whileLoop
	 * @return The CFG for the while loop.
	 */
	private static CFG build(WhileLoop whileLoop) {
		
		WhileNode node = new WhileNode(whileLoop.getCondition());
		CFG cfg = new CFG(node);
        cfg.addExitNode(node);
		
		CFG trueBranch = CFGFactory.buildSwitch(whileLoop.getBody());
		
		if(trueBranch != null) {
			node.setTrueBranch(trueBranch.getEntryNode());
			
			/* Propagate return nodes. */
			cfg.addAllReturnNodes(trueBranch.getReturnNodes());

			/* The break nodes are exit nodes for this loop. */
			cfg.addAllExitNodes(trueBranch.getBreakNodes());

			/* We merge the exit nodes back into the while loop. */
            for(CFGNode exitNode : trueBranch.getExitNodes()) {
                exitNode.mergeInto(node);
            }
            
            /* We merge continue nodes back into the while loop. */
            for(CFGNode continueNode : trueBranch.getContinueNodes()) {
            	continueNode.mergeInto(node);
            }

		} 		
		
		return cfg;
		
	}

	/**
	 * Builds a control flow subgraph for a do loop.
	 * @param doLoop
	 * @return The CFG for the do loop.
	 */
	private static CFG build(DoLoop doLoop) {
		
		DoNode entry = new DoNode();
		WhileNode loop = new WhileNode(doLoop.getCondition());
		loop.setTrueBranch(entry);

		CFG cfg = new CFG(entry);
        cfg.addExitNode(loop);
		
		CFG trueBranch = CFGFactory.buildSwitch(doLoop.getBody());
		
		if(trueBranch != null) {
			
			/* The body is executed at least once. */
			entry.mergeInto(trueBranch.getEntryNode());
			
			/* Propagate return nodes. */
			cfg.addAllReturnNodes(trueBranch.getReturnNodes());

			/* The break nodes are exit nodes for this loop. */
			cfg.addAllExitNodes(trueBranch.getBreakNodes());

			/* We merge the exit nodes back into the while loop. */
            for(CFGNode exitNode : trueBranch.getExitNodes()) {
                exitNode.mergeInto(loop);
            }
            
            /* We merge continue nodes back into the while loop. */
            for(CFGNode continueNode : trueBranch.getContinueNodes()) {
            	continueNode.mergeInto(loop);
            }

		} 		
		else {
			
			/* Infinite loop. */
			entry.mergeInto(loop);
		}
		
		return cfg;
		
	}

	/**
	 * Builds a control flow subgraph for a for statement. A for statement is
	 * simply a while statement with an expression before and after the loop
	 * body.
	 * @param forLoop
	 * @return The CFG for the for loop.
	 */
	private static CFG build(ForLoop forLoop) {
		
		StatementNode initializer = new StatementNode(forLoop.getInitializer());
		WhileNode loop = new WhileNode(forLoop.getCondition());
		StatementNode increment = new StatementNode(forLoop.getIncrement());
		
        /* Set up the initializer and increment with respect to the loop. */
		initializer.mergeInto(loop);
		increment.mergeInto(loop);

		CFG cfg = new CFG(initializer);
        cfg.addExitNode(loop);
		
		CFG trueBranch = CFGFactory.buildSwitch(forLoop.getBody());
		
		if(trueBranch != null) {
			loop.setTrueBranch(trueBranch.getEntryNode());
			
			/* Propagate return nodes. */
			cfg.addAllReturnNodes(trueBranch.getReturnNodes());

			/* The break nodes are exit nodes for this loop. */
			cfg.addAllExitNodes(trueBranch.getBreakNodes());

			/* We merge the exit nodes back into the while loop. */
            for(CFGNode exitNode : trueBranch.getExitNodes()) {
                exitNode.mergeInto(increment);
            }
            
            /* We merge continue nodes back into the while loop. */
            for(CFGNode continueNode : trueBranch.getContinueNodes()) {
            	continueNode.mergeInto(increment);
            }

		} 		
		
		return cfg;
		
	}

	/**
	 * Builds a control flow subgraph for a break statement.
	 * @param entry The entry point for the subgraph.
	 * @param exit The exit point for the subgraph.
	 * @return A list of exit nodes for the subgraph.
	 */
	private static CFG build(BreakStatement breakStatement) {
		
		CFGNode node = new JumpNode(breakStatement);
		CFG cfg = new CFG(node);
		cfg.addBreakNode(node);
		return cfg;

	}

	/**
	 * Builds a control flow subgraph for a continue statement.
	 * @param entry The entry point for the subgraph.
	 * @param exit The exit point for the subgraph.
	 * @return A list of exit nodes for the subgraph.
	 */
	private static CFG build(ContinueStatement continueStatement) {
		
		CFGNode node = new JumpNode(continueStatement);
		CFG cfg = new CFG(node);
		cfg.addContinueNode(node);
		return cfg;

	}

	/**
	 * Builds a control flow subgraph for a return statement.
	 * @param entry The entry point for the subgraph.
	 * @param exit The exit point for the subgraph.
	 * @return A list of exit nodes for the subgraph.
	 */
	private static CFG build(ReturnStatement returnStatement) {
		
		CFGNode node = new JumpNode(returnStatement);
		CFG cfg = new CFG(node);
		cfg.addReturnNode(node);
		return cfg;

	}
	
	/**
	 * Builds a control flow subgraph for a statement.
	 * @param entry The entry point for the subgraph.
	 * @param exit The exit point for the subgraph.
	 * @return A list of exit nodes for the subgraph.
	 */
	private static CFG build(AstNode statement) {
		
		CFGNode node = new StatementNode(statement);
		CFG cfg = new CFG(node);
		cfg.addExitNode(node);
		return cfg;

	}
	
	/**
	 * Calls the appropriate build method for the node type.
	 */
	private static CFG buildSwitch(AstNode node) {
		
		if(node == null) return null;

		if (node instanceof Block) {
			return CFGFactory.build((Block) node);
		} else if (node instanceof IfStatement) {
			return CFGFactory.build((IfStatement) node);
		} else if (node instanceof WhileLoop) {
			return CFGFactory.build((WhileLoop) node);
		} else if (node instanceof DoLoop) {
			return CFGFactory.build((DoLoop) node);
		} else if (node instanceof ForLoop) {
			return CFGFactory.build((ForLoop) node);
		} else if (node instanceof BreakStatement) {
			return CFGFactory.build((BreakStatement) node);
		} else if (node instanceof ContinueStatement) {
			return CFGFactory.build((ContinueStatement) node);
		} else if (node instanceof ReturnStatement) {
			return CFGFactory.build((ReturnStatement) node);
		} else if (node instanceof FunctionNode) {
			return null; // Function declarations shouldn't be part of the CFG.
		} else if (node instanceof Scope) {
			return CFGFactory.build((Scope) node);
		} else {
			return CFGFactory.build(node);
		}

	}
	
	/**
	 * Check if an AstNode is a statement.
	 * @param node
	 * @return
	 */
	private static boolean isStatement(Node node) {

		/*
			TryStatement 
			WithStatement
			SwitchStatement
			ForInLoop
			DoLoop
		*/

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
