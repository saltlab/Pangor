package ca.ubc.ece.salt.sdjsb.cfg2;

import java.util.LinkedList;
import java.util.List;

import org.mozilla.javascript.Node;
import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.Assignment;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.Block;
import org.mozilla.javascript.ast.BreakStatement;
import org.mozilla.javascript.ast.CatchClause;
import org.mozilla.javascript.ast.ContinueStatement;
import org.mozilla.javascript.ast.DoLoop;
import org.mozilla.javascript.ast.EmptyStatement;
import org.mozilla.javascript.ast.ExpressionStatement;
import org.mozilla.javascript.ast.ForInLoop;
import org.mozilla.javascript.ast.ForLoop;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.IfStatement;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.PropertyGet;
import org.mozilla.javascript.ast.ReturnStatement;
import org.mozilla.javascript.ast.Scope;
import org.mozilla.javascript.ast.ScriptNode;
import org.mozilla.javascript.ast.SwitchCase;
import org.mozilla.javascript.ast.SwitchStatement;
import org.mozilla.javascript.ast.TryStatement;
import org.mozilla.javascript.ast.UnaryExpression;
import org.mozilla.javascript.ast.VariableDeclaration;
import org.mozilla.javascript.ast.WhileLoop;
import org.mozilla.javascript.ast.WithStatement;

/**
 * Builds a control flow graph.
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
        cfgs.add(CFGFactory.buildScriptCFG(root));
		
		/* Get the list of functions in the script. */
		List<FunctionNode> functions = FunctionNodeVisitor.getFunctions(root);
		
		/* For each function, generate its CFG. */
		for (FunctionNode function : functions) {
			cfgs.add(CFGFactory.buildScriptCFG(function));
		}
		
		return cfgs;
	}
	
	/**
	 * Builds a CFG for a function or script.
	 * @param scriptNode An ASTRoot node or FunctionNode.
	 * @return The complete CFG.
	 */
	private static CFG buildScriptCFG(ScriptNode scriptNode) {

		/* Start by getting the CFG for the script. There is one entry point
		 * and one exit point for a script and function. */

		CFGNode scriptEntry = new CFGNode(new EmptyStatement());
		CFGNode scriptExit = new CFGNode(new EmptyStatement());
		
        /* Build the CFG for the script. */
        CFG cfg = new CFG(scriptEntry);
        cfg.addExitNode(scriptExit);
        
        /* Build the CFG subgraph for the script body. */
        CFG subGraph = CFGFactory.build(scriptNode);

        /* The next node in the graph is first node of the subgraph. */
        scriptEntry.addEdge(null, subGraph.getEntryNode());
        
        /* Merge the subgraph's exit nodes into the script exit node. */
        for(CFGNode exitNode : subGraph.getExitNodes()) {
        	exitNode.addEdge(null, scriptExit);
        }
        
        return cfg;
		
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
	 * Builds a CFG for a script or function.
	 * @param script The block statement ({@code AstRoot} or {@code FunctionNode}).
	 */
	private static CFG build(ScriptNode script) {
		if(script instanceof AstRoot) {
            return CFGFactory.buildBlock(script);
		}
		return CFGFactory.buildSwitch(((FunctionNode)script).getBody());
	}

	/**
	 * Builds a CFG for a block, function or script.
	 * @param block
	 * @return The CFG for the block.
	 */
	private static CFG buildBlock(Iterable<Node> block) {
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
					assert(previous.getExitNodes().size() == 1);
					for(CFGNode exitNode : previous.getExitNodes()) {
						exitNode.addEdge(null, subGraph.getEntryNode());
						
					}
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
		
		CFGNode ifNode = new CFGNode(new EmptyStatement());
		CFG cfg = new CFG(ifNode);
		
		/* Build the true branch. */
		
		CFG trueBranch = CFGFactory.buildSwitch(ifStatement.getThenPart());
		
		if(trueBranch == null) {
			CFGNode empty = new CFGNode(new EmptyStatement());
			trueBranch = new CFG(empty);
			trueBranch.addExitNode(empty);
		}
		
		ifNode.addEdge(ifStatement.getCondition(), trueBranch.getEntryNode());

        /* Propagate exit, return, continue and break nodes. */
        cfg.addAllExitNodes(trueBranch.getExitNodes());
        cfg.addAllReturnNodes(trueBranch.getReturnNodes());
        cfg.addAllBreakNodes(trueBranch.getBreakNodes());
        cfg.addAllContinueNodes(trueBranch.getContinueNodes());
        
        /* Build the false branch. */

		CFG falseBranch = CFGFactory.buildSwitch(ifStatement.getElsePart());

		if(falseBranch == null) {
			CFGNode empty = new CFGNode(new EmptyStatement());
			falseBranch = new CFG(empty);
			falseBranch.addExitNode(empty);
		}

		ifNode.addEdge(new UnaryExpression(Token.NOT, 0, ifStatement.getElsePart()), trueBranch.getEntryNode());

        /* Propagate exit, return, continue and break nodes. */
        cfg.addAllExitNodes(falseBranch.getExitNodes());
        cfg.addAllReturnNodes(falseBranch.getReturnNodes());
        cfg.addAllBreakNodes(falseBranch.getBreakNodes());
        cfg.addAllContinueNodes(falseBranch.getContinueNodes());
		
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
            for(Node exitNode : trueBranch.getExitNodes()) {
                exitNode.mergeInto(node);
            }
            
            /* We merge continue nodes back into the while loop. */
            for(Node continueNode : trueBranch.getContinueNodes()) {
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
            for(Node exitNode : trueBranch.getExitNodes()) {
                exitNode.mergeInto(loop);
            }
            
            /* We merge continue nodes back into the while loop. */
            for(Node continueNode : trueBranch.getContinueNodes()) {
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
            for(Node exitNode : trueBranch.getExitNodes()) {
                exitNode.mergeInto(increment);
            }
            
            /* We merge continue nodes back into the while loop. */
            for(Node continueNode : trueBranch.getContinueNodes()) {
            	continueNode.mergeInto(increment);
            }

		} 		
		
		return cfg;
		
	}

	/**
	 * Builds a control flow subgraph for a for in statement. A for in statement
	 * is a loop that iterates over the keys of an object. The Rhino IR 
	 * represents this using the Node labeled "ENUM_INIT_KEYS". Here, we make
	 * a fake function that returns an object's keys.
	 * @param forInLoop
	 * @return The CFG for the for-in loop.
	 */
	private static CFG build(ForInLoop forInLoop) {
		
		AstNode iterator = forInLoop.getIterator();
		StatementNode initializer = null;
		CFG cfg;

        /* We need to add a node that declares a variable. Interestingly,
         * if a variable is declared it can also be initialized to a value
         * here (although it will have no effect). */
        initializer = new StatementNode(iterator);
        cfg = new CFG(initializer);
        
        /* To represent key iteration, we make up a function that iterates 
         * through each key in an object. The function name is invalid in JS
         * to ensure that there isn't another function with the same name.
         * Since we're not producing code, this is ok. */
        AstNode target = ((VariableDeclaration) iterator).getVariables().get(0).getTarget();
        PropertyGet keyIteratorMethod = new PropertyGet(forInLoop.getIteratedObject(), new Name(0, "~getNextkey"));
        FunctionCall keyIteratorFunction = new FunctionCall();
        keyIteratorFunction.setTarget(keyIteratorMethod);
        Assignment targetAssignment = new Assignment(target, keyIteratorFunction);
        targetAssignment.setType(Token.ASSIGN);

        StatementNode assignment = new StatementNode(targetAssignment);

        /* Do the same thing for the loop condition (if there is another key then loop). */
        PropertyGet keyConditionMethod = new PropertyGet(forInLoop.getIteratedObject(), new Name(0, "~hasNextKey"));
        FunctionCall keyConditionFunction = new FunctionCall();
        keyConditionFunction.setTarget(keyConditionMethod);

		WhileNode loop = new WhileNode(keyConditionFunction);
		
        /* Set up the initializer and increment with respect to the loop. */
		initializer.mergeInto(loop);
		loop.setTrueBranch(assignment);
        cfg.addExitNode(loop);
		
		CFG trueBranch = CFGFactory.buildSwitch(forInLoop.getBody());
		
		if(trueBranch != null) {
			assignment.mergeInto(trueBranch.getEntryNode());
			
			/* Propagate return nodes. */
			cfg.addAllReturnNodes(trueBranch.getReturnNodes());

			/* The break nodes are exit nodes for this loop. */
			cfg.addAllExitNodes(trueBranch.getBreakNodes());

			/* We merge the exit nodes back into the while loop. */
            for(Node exitNode : trueBranch.getExitNodes()) {
                exitNode.mergeInto(loop);
            }
            
            /* We merge continue nodes back into the while loop. */
            for(Node continueNode : trueBranch.getContinueNodes()) {
            	continueNode.mergeInto(loop);
            }

		} 		
		
		return cfg;
		
	}

	/**
	 * Builds a control flow subgraph for a switch statement. A for statement is
	 * simply a while statement with an expression before and after the loop
	 * body.
	 * @param forLoop
	 * @return The CFG for the for loop.
	 */
	private static CFG build(SwitchStatement switchStatement) {
		
		/* Create the switch node. The expression is the value to switch on. */
		SwitchNode switchNode = new SwitchNode(switchStatement.getExpression());

		CFG cfg = new CFG(switchNode);
		cfg.addExitNode(switchNode);
		
		/* Build the subgraphs for the cases. */
		CFG previousSubGraph = null;
		List<SwitchCase> switchCases = switchStatement.getCases();
	 
		for(SwitchCase switchCase : switchCases) {
			
			/* Build the subgraph for the case. */
            CFG subGraph = null;
			if(switchCase.getStatements() != null) {
                List<Node> statements = new LinkedList<Node>(switchCase.getStatements());
                subGraph = CFGFactory.buildBlock(statements);
			}
			
			/* If it is an empty case, make our lives easier by adding an
			 * empty statement as the entry and exit node. */
			if(subGraph == null) {

				StatementNode emptyCase = new StatementNode(new EmptyStatement());
				subGraph = new CFG(emptyCase);
				subGraph.addExitNode(emptyCase);
				
			}
            
            /* Add the node to the switch statement. */
            switchNode.setCase(switchCase.getExpression(), subGraph.getEntryNode());
			
			/* Propagate return nodes. */
			cfg.addAllReturnNodes(subGraph.getReturnNodes());

            /* Propagate continue nodes. */
            cfg.addAllContinueNodes(subGraph.getContinueNodes());

			/* The break nodes are exit nodes for this loop. */
			cfg.addAllExitNodes(subGraph.getBreakNodes());

			if(previousSubGraph != null) {

                /* Merge the exit nodes into the next case. */
                for(Node exitNode : previousSubGraph.getExitNodes()) {
                    exitNode.mergeInto(subGraph.getEntryNode());
                }

			}
			
			previousSubGraph = subGraph;
			
		}

        /* The rest of the exit nodes are exit nodes for the statement. */
        cfg.addAllExitNodes(previousSubGraph.getExitNodes());
		
		return cfg;
		
	}

	/**
	 * Builds a control flow subgraph for a with statement.
	 * @param withStatement
	 * @return The CFG for the while loop.
	 */
	private static CFG build(WithStatement withStatement) {
		
		WithNode node = new WithNode(withStatement.getExpression());
		CFG cfg = new CFG(node);
        cfg.addExitNode(node);
		
		CFG scopeBlock = CFGFactory.buildSwitch(withStatement.getStatement());
		
		if(scopeBlock != null) {

			node.setScopeBlock(scopeBlock.getEntryNode());
			
			/* Propagate return nodes. */
			cfg.addAllReturnNodes(scopeBlock.getReturnNodes());

			/* Propagate break nodes. */
			cfg.addAllBreakNodes(scopeBlock.getBreakNodes());
			
			/* Propagate the exit nodes. */
			cfg.addAllExitNodes(scopeBlock.getExitNodes());
			
			/* Propagate continue nodes. */
			cfg.addAllContinueNodes(scopeBlock.getContinueNodes());
			
		} 		
		
		return cfg;
		
	}

	/**
	 * Builds a control flow subgraph for a try/catch statement.
	 * 
	 * NOTE: The resulting graph will not be accurate when there are jump
	 * 		 statements in the try or catch blocks and a finally block.
	 * 
	 * @param tryStatement
	 * @return The CFG for the while loop.
	 */
	private static CFG build(TryStatement tryStatement) {
		
		TryNode node = new TryNode(tryStatement);
		CFG cfg = new CFG(node);
		cfg.addExitNode(node);
		
		/* Start by setting up the finally block. */

		CFG finallyBlock = CFGFactory.buildSwitch(tryStatement.getFinallyBlock());

		if(finallyBlock == null) { 
			Node empty = new StatementNode(new EmptyStatement());
			finallyBlock = new CFG(empty);
			finallyBlock.addExitNode(empty);
		}
		else {
            /* Propagate all nodes. */
            cfg.addAllReturnNodes(finallyBlock.getReturnNodes());
            cfg.addAllBreakNodes(finallyBlock.getBreakNodes());
            cfg.addAllContinueNodes(finallyBlock.getContinueNodes());
            cfg.addAllExitNodes(finallyBlock.getExitNodes());
		}
		
		node.setFinallyBranch(finallyBlock.getEntryNode());
		
		/* Set up the try block. */
		
		CFG tryBlock = CFGFactory.buildSwitch(tryStatement.getTryBlock());
		
		if(tryBlock == null) {
			Node empty = new StatementNode(new EmptyStatement());
			tryBlock = new CFG(empty);
			tryBlock.addExitNode(empty);
		}
		else {
            /* Propagate all nodes. */
            cfg.addAllReturnNodes(tryBlock.getReturnNodes());
            cfg.addAllBreakNodes(tryBlock.getBreakNodes());
            cfg.addAllContinueNodes(tryBlock.getContinueNodes());
            
            /* Exit nodes exit to the finally block. */
            for(Node exitNode : tryBlock.getExitNodes()) {
            	exitNode.mergeInto(finallyBlock.getEntryNode());
            }
		}
		
		node.setTryBranch(tryBlock.getEntryNode());
		
		/* Set up the catch clauses. */

		List<CatchClause> catchClauses = tryStatement.getCatchClauses();
		for(CatchClause catchClause : catchClauses) {

			CFG catchBlock = CFGFactory.buildSwitch(catchClause.getBody());
			
			if(catchBlock == null) {
				Node empty = new StatementNode(new EmptyStatement());
				catchBlock = new CFG(empty);
				catchBlock.addExitNode(empty);
			}
			else {
                /* Propagate all nodes. */
                cfg.addAllReturnNodes(catchBlock.getReturnNodes());
                cfg.addAllBreakNodes(catchBlock.getBreakNodes());
                cfg.addAllContinueNodes(catchBlock.getContinueNodes());
                
                /* Exit nodes exit to the finally block. */
                for(Node exitNode : catchBlock.getExitNodes()) {
                    exitNode.mergeInto(finallyBlock.getEntryNode());
                }
				
			}
			
			node.addCatchClause(catchClause.getCatchCondition(), catchBlock.getEntryNode());
			
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
		
		Node node = new JumpNode(breakStatement);
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
		
		Node node = new JumpNode(continueStatement);
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
		
		Node node = new JumpNode(returnStatement);
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
		
		Node node = new StatementNode(statement);
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
		} else if (node instanceof ForInLoop) {
			return CFGFactory.build((ForInLoop) node);
		} else if (node instanceof SwitchStatement) {
			return CFGFactory.build((SwitchStatement) node);
		} else if (node instanceof WithStatement) {
			return CFGFactory.build((WithStatement) node);
		} else if (node instanceof TryStatement) {
			return CFGFactory.build((TryStatement) node);
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
