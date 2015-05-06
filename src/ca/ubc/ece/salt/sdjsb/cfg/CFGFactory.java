package ca.ubc.ece.salt.sdjsb.cfg;

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
import org.mozilla.javascript.ast.ForInLoop;
import org.mozilla.javascript.ast.ForLoop;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.IfStatement;
import org.mozilla.javascript.ast.InfixExpression;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.ParenthesizedExpression;
import org.mozilla.javascript.ast.PropertyGet;
import org.mozilla.javascript.ast.ReturnStatement;
import org.mozilla.javascript.ast.Scope;
import org.mozilla.javascript.ast.ScriptNode;
import org.mozilla.javascript.ast.SwitchCase;
import org.mozilla.javascript.ast.SwitchStatement;
import org.mozilla.javascript.ast.ThrowStatement;
import org.mozilla.javascript.ast.TryStatement;
import org.mozilla.javascript.ast.UnaryExpression;
import org.mozilla.javascript.ast.VariableDeclaration;
import org.mozilla.javascript.ast.WhileLoop;
import org.mozilla.javascript.ast.WithStatement;

import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode.ChangeType;

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
		
		String name = "FUNCTION";
		if(scriptNode instanceof AstRoot) name = "SCRIPT";

		/* Start by getting the CFG for the script. There is one entry point
		 * and one exit point for a script and function. */

		CFGNode scriptEntry = new CFGNode(scriptNode, name + "_ENTRY");
		CFGNode scriptExit = new CFGNode(new EmptyStatement(), name + "_EXIT");
		
        /* Build the CFG for the script. */
        CFG cfg = new CFG(scriptEntry);
        cfg.addExitNode(scriptExit);
        
        /* Build the CFG subgraph for the script body. */
        CFG subGraph = CFGFactory.build(scriptNode);
        
        if(subGraph == null) {
        	CFGNode empty = new CFGNode(new EmptyStatement());
        	subGraph = new CFG(empty);
        	subGraph.addExitNode(empty);
        }

        /* The next node in the graph is first node of the subgraph. */
        scriptEntry.addEdge(null, subGraph.getEntryNode());
        
        /* Merge the subgraph's exit nodes into the script exit node. */
        for(CFGNode exitNode : subGraph.getExitNodes()) {
        	exitNode.addEdge(null, scriptExit);
        }
        
        /* The return nodes should point to the function exit. */
        for(CFGNode returnNode : subGraph.getReturnNodes()) {
        	returnNode.addEdge(null, scriptExit);
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

                /* Propagate return, continue, break and throw nodes. */
                cfg.addAllReturnNodes(subGraph.getReturnNodes());
                cfg.addAllBreakNodes(subGraph.getBreakNodes());
                cfg.addAllContinueNodes(subGraph.getContinueNodes());
                cfg.addAllThrowNodes(subGraph.getThrowNodes());

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
		
		CFGNode ifNode = new CFGNode(new EmptyStatement(), "IF");
		CFG cfg = new CFG(ifNode);
		
		/* Build the true branch. */
		
		CFG trueBranch = CFGFactory.buildSwitch(ifStatement.getThenPart());
		
		if(trueBranch == null) {
			CFGNode empty = new CFGNode(new EmptyStatement());
			trueBranch = new CFG(empty);
			trueBranch.addExitNode(empty);
		}
		
		ifNode.addEdge(new CFGEdge(ifStatement.getCondition(), ifNode, trueBranch.getEntryNode()));

        /* Propagate exit, return, continue, break and throw nodes. */
        cfg.addAllExitNodes(trueBranch.getExitNodes());
        cfg.addAllReturnNodes(trueBranch.getReturnNodes());
        cfg.addAllBreakNodes(trueBranch.getBreakNodes());
        cfg.addAllContinueNodes(trueBranch.getContinueNodes());
        cfg.addAllThrowNodes(trueBranch.getThrowNodes());
        
        /* Build the false branch. */

		CFG falseBranch = CFGFactory.buildSwitch(ifStatement.getElsePart());

		if(falseBranch == null) {
			CFGNode empty = new CFGNode(new EmptyStatement());
			falseBranch = new CFG(empty);
			falseBranch.addExitNode(empty);
		}

		/* The false branch condition is the negation of the true branch 
		 * condition. We give it the same change type label as the true
		 * branch condition. */
		AstNode falseBranchCondition = new UnaryExpression(Token.NOT, 0, new ParenthesizedExpression(ifStatement.getCondition()));
		falseBranchCondition.setChangeType(ifStatement.getCondition().getChangeType());

		ifNode.addEdge(new CFGEdge(falseBranchCondition, ifNode, falseBranch.getEntryNode()));

        /* Propagate exit, return, continue and break nodes. */
        cfg.addAllExitNodes(falseBranch.getExitNodes());
        cfg.addAllReturnNodes(falseBranch.getReturnNodes());
        cfg.addAllBreakNodes(falseBranch.getBreakNodes());
        cfg.addAllContinueNodes(falseBranch.getContinueNodes());
        cfg.addAllThrowNodes(falseBranch.getThrowNodes());
		
		return cfg;
		
	}

	/**
	 * Builds a control flow subgraph for a while statement.
	 * @param whileLoop
	 * @return The CFG for the while loop.
	 */
	private static CFG build(WhileLoop whileLoop) {
		
		CFGNode whileNode = new CFGNode(new EmptyStatement(), "WHILE");
		CFG cfg = new CFG(whileNode);

		/* Build the true branch. */
		
		CFG trueBranch = CFGFactory.buildSwitch(whileLoop.getBody());

		if(trueBranch == null) {
			CFGNode empty = new CFGNode(new EmptyStatement());
			trueBranch = new CFG(empty);
			trueBranch.addExitNode(empty);
		}
		
		whileNode.addEdge(new CFGEdge(whileLoop.getCondition(), whileNode, trueBranch.getEntryNode(), true));

        /* Propagate return and throw nodes. */
        cfg.addAllReturnNodes(trueBranch.getReturnNodes());
        cfg.addAllThrowNodes(trueBranch.getThrowNodes());
        
        /* The break nodes are exit nodes for this loop. */
        cfg.addAllExitNodes(trueBranch.getBreakNodes());
        
        /* Exit nodes point back to the start of the loop. */
        for(CFGNode exitNode : trueBranch.getExitNodes()) {
        	exitNode.addEdge(null, whileNode);
        }
        
        /* Continue nodes point back to the start of the loop. */
        for(CFGNode continueNode : trueBranch.getContinueNodes()) {
        	continueNode.addEdge(null, whileNode);
        }
        
        /* Build the false branch. */

		/* The false branch condition is the negation of the true branch 
		 * condition. We give it the same change type label as the true
		 * branch condition. */
		AstNode falseBranchCondition = new UnaryExpression(Token.NOT, 0, new ParenthesizedExpression(whileLoop.getCondition()));
		falseBranchCondition.setChangeType(whileLoop.getCondition().getChangeType());
        
        CFGNode empty = new CFGNode(new EmptyStatement());
		whileNode.addEdge(new CFGEdge(falseBranchCondition, whileNode, empty));
		cfg.addExitNode(empty);
		
		return cfg;
		
	}

	/**
	 * Builds a control flow subgraph for a do loop.
	 * @param doLoop
	 * @return The CFG for the do loop.
	 */
	private static CFG build(DoLoop doLoop) {
		
		CFGNode doNode = new CFGNode(new EmptyStatement(), "DO");
		CFGNode whileNode = new CFGNode(new EmptyStatement(), "WHILE");
		CFG cfg = new CFG(doNode);
		
		/* Build the loop branch. */
		
		CFG loopBranch = CFGFactory.buildSwitch(doLoop.getBody());
		
		if(loopBranch == null) {
			CFGNode empty = new CFGNode(new EmptyStatement());
			loopBranch = new CFG(empty);
			loopBranch.addExitNode(empty);
		}
		
		/* We always execute the do block at least once. */
		doNode.addEdge(null, loopBranch.getEntryNode());

		/* Add edges from exit nodes from the loop to the while node. */
		for(CFGNode exitNode : loopBranch.getExitNodes()) {
			exitNode.addEdge(null, whileNode);
		}

        /* Propagate return and throw nodes. */
        cfg.addAllReturnNodes(loopBranch.getReturnNodes());
        cfg.addAllThrowNodes(loopBranch.getThrowNodes());

        /* The break nodes are exit nodes for this loop. */
        cfg.addAllExitNodes(loopBranch.getBreakNodes());

        /* Continue nodes have edges to the while condition. */
        for(CFGNode continueNode : loopBranch.getContinueNodes()) {
        	continueNode.addEdge(null, whileNode);
        }
		
		/* Add edge for true condition back to the start of the loop. */
		whileNode.addEdge(doLoop.getCondition(), doNode, true);
		
		/* Add edge for false condition. */

		/* The false branch condition is the negation of the true branch 
		 * condition. We give it the same change type label as the true
		 * branch condition. */
		AstNode falseBranchCondition = new UnaryExpression(Token.NOT, 0, new ParenthesizedExpression(doLoop.getCondition()));
		falseBranchCondition.setChangeType(doLoop.getCondition().getChangeType());

        CFGNode empty = new CFGNode(new EmptyStatement());
		whileNode.addEdge(falseBranchCondition, empty);
		cfg.addExitNode(empty);

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
		
		CFGNode forNode = new CFGNode(forLoop.getInitializer());
		CFG cfg = new CFG(forNode);

		/* After variables are declared, add an empty node with two edges. */
		CFGNode condition = new CFGNode(new EmptyStatement(), "FOR");
		forNode.addEdge(null, condition);

		/* After the body of the loop executes, add the node to perform the increment. */
		CFGNode increment = new CFGNode(forLoop.getIncrement());
		increment.addEdge(null, condition);
		
		/* Build the true branch. */
		
		CFG trueBranch = CFGFactory.buildSwitch(forLoop.getBody());

		if(trueBranch == null) {
			CFGNode empty = new CFGNode(new EmptyStatement());
			trueBranch = new CFG(empty);
			trueBranch.addExitNode(empty);
		}
		
		condition.addEdge(forLoop.getCondition(), trueBranch.getEntryNode(), true);

        /* Propagate return and throw nodes. */
        cfg.addAllReturnNodes(trueBranch.getReturnNodes());
        cfg.addAllThrowNodes(trueBranch.getThrowNodes());
        
        /* The break nodes are exit nodes for this loop. */
        cfg.addAllExitNodes(trueBranch.getBreakNodes());
        
        /* Exit nodes point to the increment node. */
        for(CFGNode exitNode : trueBranch.getExitNodes()) {
        	exitNode.addEdge(null, increment);
        }
        
        /* Continue nodes point to the increment. */
        for(CFGNode continueNode : trueBranch.getContinueNodes()) {
        	continueNode.addEdge(null, increment);
        }
        
        /* Build the false branch. */

		/* The false branch condition is the negation of the true branch 
		 * condition. We give it the same change type label as the true
		 * branch condition. */
		AstNode falseBranchCondition = new UnaryExpression(Token.NOT, 0, new ParenthesizedExpression(forLoop.getCondition()));
		falseBranchCondition.setChangeType(forLoop.getCondition().getChangeType());
        
        CFGNode empty = new CFGNode(new EmptyStatement());
		condition.addEdge(falseBranchCondition, empty);
		cfg.addExitNode(empty);
		
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

		/* To represent key iteration, we make up two functions:
		 *  
		 * 	~getNextKey() - iterates through each key in an object. 
		 *	~hasNextKey() - true if there is another key to iterate. 
		 *
         * These names are invalid in JavaScript to ensure that there isn't
         * another function with the same name. Since we're not producing code,
         * this is ok. */
		
		/* Start with the variable declaration. */
		AstNode iterator = forInLoop.getIterator();
		CFGNode forInNode = new CFGNode(iterator);
		CFG cfg = new CFG(forInNode);
		
		/* Get the variable being assigned. */
		AstNode target;
		if(iterator instanceof VariableDeclaration) {
            target = ((VariableDeclaration) iterator).getVariables().get(0).getTarget();
		}
		else if (iterator instanceof Name) {
			target = iterator;
		}
		else {
			target = new Name(0, "~error~");
		}

		/* Create the node that gets the next key in an object and assigns the
		 * value to the iterator variable. */

		Name getNextKey = new Name(0, "~getNextKey");
		getNextKey.setChangeType(iterator.getChangeType());
        PropertyGet keyIteratorMethod = new PropertyGet(forInLoop.getIteratedObject(), getNextKey);
        keyIteratorMethod.setChangeType(iterator.getChangeType());
        FunctionCall keyIteratorFunction = new FunctionCall();
        keyIteratorFunction.setTarget(keyIteratorMethod);
        keyIteratorFunction.setChangeType(iterator.getChangeType());
        Assignment targetAssignment = new Assignment(target, keyIteratorFunction);
        targetAssignment.setType(Token.ASSIGN);
        targetAssignment.setChangeType(target.getChangeType());
		
        CFGNode assignment = new CFGNode(targetAssignment);

        /* Create the the condition that checks if an object still has keys.
         * The condition is assigned to the true/false loop branches. */

        PropertyGet keyConditionMethod = new PropertyGet(forInLoop.getIteratedObject(), new Name(0, "~hasNextKey"));
        keyConditionMethod.setChangeType(iterator.getChangeType());
        FunctionCall keyConditionFunction = new FunctionCall();
        keyConditionFunction.setTarget(keyConditionMethod);
        keyConditionFunction.setChangeType(iterator.getChangeType());

		CFGNode condition = new CFGNode(new EmptyStatement(), "FORIN");
		
		/* Add the edges connecting the entry point to the assignment and
		 * assignment to condition. */
        forInNode.addEdge(null, condition);
        condition.addEdge(new CFGEdge(keyConditionFunction, condition, assignment, true));
		
        /* Create the CFG for the loop body. */
		
		CFG trueBranch = CFGFactory.buildSwitch(forInLoop.getBody());

		if(trueBranch == null) {
			CFGNode empty = new CFGNode(new EmptyStatement());
			trueBranch = new CFG(empty);
			trueBranch.addExitNode(empty);
		}
		
        /* Propagate return and throw nodes. */
        cfg.addAllReturnNodes(trueBranch.getReturnNodes());
        cfg.addAllThrowNodes(trueBranch.getThrowNodes());

        /* The break nodes are exit nodes for this loop. */
        cfg.addAllExitNodes(trueBranch.getBreakNodes());

        /* The exit nodes point back to the assignment node. */
        for(CFGNode exitNode : trueBranch.getExitNodes()) {
            exitNode.addEdge(null, condition);
        }
        
        /* The continue nodes point back to the assignment node. */
        for(CFGNode continueNode : trueBranch.getContinueNodes()) {
        	continueNode.addEdge(null, condition);
        }
        
        /* Create a node for the false branch to exit the loop. */
        CFGNode falseBranch = new CFGNode(new EmptyStatement());
        cfg.addExitNode(falseBranch);

		/* The false branch condition is the negation of the true branch 
		 * condition. We give it the same change type label as the true
		 * branch condition. */
		AstNode falseBranchCondition = new UnaryExpression(Token.NOT, 0, new ParenthesizedExpression(keyConditionFunction));
		falseBranchCondition.setChangeType(keyConditionFunction.getChangeType());

        /* Add the edges from the assignment node to the start of the loop. */
        assignment.addEdge(null, trueBranch.getEntryNode());
		condition.addEdge(new CFGEdge(new UnaryExpression(Token.NOT, 0, new ParenthesizedExpression(keyConditionFunction)), condition, falseBranch));
		
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
		
		CFGNode switchNode = new CFGNode(new EmptyStatement(), "SWITCH");
		CFG cfg = new CFG(switchNode);
		
		/* Keep track of the default edge so we can update the condition later. */
		CFGEdge defaultEdge = null;
		AstNode defaultCondition = null;
		
		/* Add edges for each case. */
		List<SwitchCase> switchCases = switchStatement.getCases();
		CFG previousSubGraph = null;
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
                CFGNode empty = new CFGNode(new EmptyStatement());
                subGraph = new CFG(empty);
                subGraph.addExitNode(empty);
            }

            /* Add an edge from the switch to the entry node for the case. We
             * build a comparison expression for the edge by comparing the
             * switch expression to the case expression. */
            if(switchCase.getExpression() != null) {
                InfixExpression compare = new InfixExpression(switchStatement.getExpression(), switchCase.getExpression());
                compare.setType(Token.SHEQ);
                switchNode.addEdge(new CFGEdge(compare, switchNode, subGraph.getEntryNode()));
                
                if(defaultCondition == null) {
                	defaultCondition = compare;
                	defaultCondition.setChangeType(compare.getChangeType());
                }
                else {
                    AstNode infix = new InfixExpression(compare, defaultCondition);
                    infix.setType(Token.OR);
                    if(compare.getChangeType() == defaultCondition.getChangeType()) infix.setChangeType(compare.getChangeType());
                    else infix.setChangeType(ChangeType.UPDATED);
                    defaultCondition = infix;
                }
                
            }
            else {
            	defaultEdge = new CFGEdge(null, switchNode, subGraph.getEntryNode());
            	switchNode.addEdge(defaultEdge);
            }
			
			/* Propagate return and throw nodes. */
			cfg.addAllReturnNodes(subGraph.getReturnNodes());
			cfg.addAllThrowNodes(subGraph.getThrowNodes());

            /* Propagate continue nodes. */
            cfg.addAllContinueNodes(subGraph.getContinueNodes());

			/* The break nodes are exit nodes for the switch. */
			cfg.addAllExitNodes(subGraph.getBreakNodes());

			if(previousSubGraph != null) {

                /* Add an edge from the exit nodes of the previous case to the
                 * entry node for this case. */
                for(CFGNode exitNode : previousSubGraph.getExitNodes()) {
                	exitNode.addEdge(null, subGraph.getEntryNode());
                }

			}
			
			previousSubGraph = subGraph;
            
		}
		
		/* Setup the default path if wasn't explicitly given in the switch statement. */
		if(defaultEdge == null) {
			CFGNode defaultPath = new CFGNode(new EmptyStatement());
            defaultEdge = new CFGEdge(null, switchNode, new CFGNode(new EmptyStatement()));
            cfg.addExitNode(defaultPath);
		}

		/* The false branch condition is the negation of the true branch 
		 * condition. We give it the same change type label as the true
		 * branch condition. */
		if(defaultCondition != null)  {

            AstNode falseBranchCondition = new UnaryExpression(Token.NOT, 0, new ParenthesizedExpression(defaultCondition));
            falseBranchCondition.setChangeType(defaultCondition.getChangeType());
            defaultCondition = falseBranchCondition;

		}
		
		/* Add the final default condition. */
		defaultEdge.setCondition(defaultCondition);

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

		/* Create two functions to represent adding a scope:
		 * 	- One that loads the expression's fields and functions into the current scope.
		 *  - One that unloads the expression's fields and functions from the current scope.
		 */

        FunctionCall createScopeFunction = new FunctionCall();
        createScopeFunction.setTarget(new Name(0, "~createScope"));
        createScopeFunction.addArgument(withStatement.getExpression());
        
        FunctionCall destroyScopeFunction = new FunctionCall();
        destroyScopeFunction.setTarget(new Name(0, "~destroySceop"));
        destroyScopeFunction.addArgument(withStatement.getExpression());
		
		CFGNode withNode = new CFGNode(createScopeFunction, "BEGIN_SCOPE");
		CFGNode endWithNode = new CFGNode(destroyScopeFunction, "END_SCOPE");

		CFG cfg = new CFG(withNode);
		cfg.addExitNode(endWithNode);
		
		CFG scopeBlock = CFGFactory.buildSwitch(withStatement.getStatement());

        if(scopeBlock == null) {
            CFGNode empty = new CFGNode(new EmptyStatement());
            scopeBlock = new CFG(empty);
            scopeBlock.addExitNode(empty);
        }
		
        withNode.addEdge(null, scopeBlock.getEntryNode());

        /* Exit nodes point to the scope destroy method. */
        for(CFGNode exitNode : scopeBlock.getExitNodes()) {
        	exitNode.addEdge(null, endWithNode);
        }
        
        /* Propagate return and throw nodes. */
        cfg.addAllReturnNodes(scopeBlock.getReturnNodes());
        cfg.addAllThrowNodes(scopeBlock.getThrowNodes());

        /* Propagate break nodes. */
        cfg.addAllBreakNodes(scopeBlock.getBreakNodes());
        
        /* Propagate continue nodes. */
        cfg.addAllContinueNodes(scopeBlock.getContinueNodes());
		
		return cfg;
		
	}

	/**
	 * Builds a control flow subgraph for a try/catch statement.
	 * 
	 * @param tryStatement
	 * @return The CFG for the while loop.
	 */
	private static CFG build(TryStatement tryStatement) {
		
		CFGNode tryNode = new CFGNode(new EmptyStatement(), "TRY");
		CFG cfg = new CFG(tryNode);
		
		/* To make life easier, add a node that represents the exit of the try. */
		CFGNode exit = new CFGNode(new EmptyStatement());
		cfg.addExitNode(exit);
		
		/* Set up the finally block. */

		CFG finallyBlock = CFGFactory.buildSwitch(tryStatement.getFinallyBlock());

		if(finallyBlock == null) { 
			CFGNode empty = new CFGNode(new EmptyStatement());
			finallyBlock = new CFG(empty);
			finallyBlock.addExitNode(empty);
		}
		else {
            /* Propagate return, break, continue and throw nodes. */
            cfg.addAllReturnNodes(finallyBlock.getReturnNodes());
            cfg.addAllBreakNodes(finallyBlock.getBreakNodes());
            cfg.addAllContinueNodes(finallyBlock.getContinueNodes());
            cfg.addAllThrowNodes(finallyBlock.getThrowNodes());

            for(CFGNode exitNode : finallyBlock.getExitNodes()) {
            	exitNode.addEdge(null, exit);
            }
		}

		/* Set up the catch clauses. */

		List<CatchClause> catchClauses = tryStatement.getCatchClauses();
		for(CatchClause catchClause : catchClauses) {

			CFG catchBlock = CFGFactory.buildSwitch(catchClause.getBody());

            /* Create the clause for branching to the catch. */
            AstNode catchCondition = catchClause.getCatchCondition();
            if(catchCondition == null) {
            	
            	/* Create a special method that contains the exception. */
            	FunctionCall exception = new FunctionCall();
            	List<AstNode> args = new LinkedList<AstNode>();
            	args.add(catchClause.getVarName());
            	exception.setArguments(args);
            	exception.setTarget(new Name(0, "~exception"));
                catchCondition = exception;

            }
			
			if(catchBlock == null) {
				CFGNode empty = new CFGNode(new EmptyStatement());
				catchBlock = new CFG(empty);
				catchBlock.addExitNode(empty);
			}
			else {
				
				/* Create empty exit nodes so there is an edge from each exit
				 * node in the finally block for each clause. */
				CFGNode empty = new CFGNode(new EmptyStatement());
				cfg.addExitNode(empty);
				
				for(CFGNode exitNode : finallyBlock.getExitNodes()) {
					exitNode.addEdge(catchCondition, empty);
				}
				
                /* Move the jump nodes after the finally block and propagate them
                 * through the CFG. */
                cfg.addAllBreakNodes(moveJumpAfterFinally(finallyBlock.copy(), catchBlock.getBreakNodes(), catchCondition));
                cfg.addAllContinueNodes(moveJumpAfterFinally(finallyBlock.copy(), catchBlock.getContinueNodes(), catchCondition));
                cfg.addAllReturnNodes(moveJumpAfterFinally(finallyBlock.copy(), catchBlock.getReturnNodes(), catchCondition));
                cfg.addAllThrowNodes(moveJumpAfterFinally(finallyBlock.copy(), catchBlock.getThrowNodes(), catchCondition));

                /* Exit nodes exit to the finally block. */
                for(CFGNode exitNode : catchBlock.getExitNodes()) {
                    exitNode.addEdge(null, finallyBlock.getEntryNode());
                }
				
			}
			
			tryNode.addEdge(catchCondition, catchBlock.getEntryNode());
			
		}
		
		/* Set up the try block. */
		
		CFG tryBlock = CFGFactory.buildSwitch(tryStatement.getTryBlock());
		
		if(tryBlock == null) {
			CFGNode empty = new CFGNode(new EmptyStatement());
			tryBlock = new CFG(empty);
			tryBlock.addExitNode(finallyBlock.getEntryNode());
		}
		else {
            /* Create empty exit nodes so there is an edge from each exit
             * node in the finally block for the catch block. */
            CFGNode empty = new CFGNode(new EmptyStatement());
            cfg.addExitNode(empty);
            
            for(CFGNode exitNode : finallyBlock.getExitNodes()) {
                exitNode.addEdge(null, empty);
            }
            
            /* Move the jump nodes after the finally block and propagate them
             * through the CFG. */
            cfg.addAllBreakNodes(moveJumpAfterFinally(finallyBlock.copy(), tryBlock.getBreakNodes(), null));
            cfg.addAllContinueNodes(moveJumpAfterFinally(finallyBlock.copy(), tryBlock.getContinueNodes(), null));
            cfg.addAllReturnNodes(moveJumpAfterFinally(finallyBlock.copy(), tryBlock.getReturnNodes(), null));

            /* Throw nodes point to a catch block. We assume the first because
             * to get the correct one we need to do data flow analysis. */
            for(CFGNode throwNode : tryBlock.getThrowNodes()) {
            	throwNode.addEdge(null, finallyBlock.getEntryNode());
            }

            /* Exit nodes exit to the finally block. */
            for(CFGNode exitNode : tryBlock.getExitNodes()) {
            	exitNode.addEdge(null, finallyBlock.getEntryNode());
            }
		}
		
		tryNode.addEdge(null, tryBlock.getEntryNode());
		

		return cfg;
		
	}

	/**
	 * Move the jump nodes from a try or catch block to after the finally block.
	 * @param finallyBlock The finally block in the try statement.
	 * @param jumpNodes The set of break, continue or return nodes.
	 * @return The set of newly created jump nodes (to be propagated to the CFG).
	 */
	private static List<CFGNode> moveJumpAfterFinally(CFG finallyBlock, List<CFGNode> jumpNodes, AstNode condition) {
		
		/* The list of newly created jump nodes to propagate to the cfg. */
		List<CFGNode> newJumpNodes = new LinkedList<CFGNode>();

		for(CFGNode jumpNode : jumpNodes) {
			
			/* Make a shallow copy of the node. */
			CFGNode newJumpNode = CFGNode.copy(jumpNode);
			newJumpNodes.add(newJumpNode);
			
			/* Add an edge from the finally block to the return node. */
			for(CFGNode exitNode : finallyBlock.getExitNodes()) {
				exitNode.addEdge(condition, newJumpNode);
			}
			
			/* Change the original jump node to do nothing. */
			jumpNode.setStatement(new EmptyStatement());
			
			/* Remove any previous edges from the jump node. */
			jumpNode.getEdges().clear();
			
			/* Add an edge from the original jump node to the start of the
			 * finally block. */
			jumpNode.addEdge(null, finallyBlock.getEntryNode());
			
		}
		
		return newJumpNodes;
	}

	/**
	 * Builds a control flow subgraph for a break statement.
	 * @param entry The entry point for the subgraph.
	 * @param exit The exit point for the subgraph.
	 * @return A list of exit nodes for the subgraph.
	 */
	private static CFG build(BreakStatement breakStatement) {
		
		CFGNode breakNode = new CFGNode(breakStatement);
		CFG cfg = new CFG(breakNode);
		cfg.addBreakNode(breakNode);
		return cfg;

	}

	/**
	 * Builds a control flow subgraph for a continue statement.
	 * @param entry The entry point for the subgraph.
	 * @param exit The exit point for the subgraph.
	 * @return A list of exit nodes for the subgraph.
	 */
	private static CFG build(ContinueStatement continueStatement) {
		
		CFGNode continueNode = new CFGNode(continueStatement);
		CFG cfg = new CFG(continueNode);
		cfg.addContinueNode(continueNode);
		return cfg;

	}

	/**
	 * Builds a control flow subgraph for a return statement.
	 * @param entry The entry point for the subgraph.
	 * @param exit The exit point for the subgraph.
	 * @return A list of exit nodes for the subgraph.
	 */
	private static CFG build(ReturnStatement returnStatement) {
		
		CFGNode returnNode = new CFGNode(returnStatement);
		CFG cfg = new CFG(returnNode);
		cfg.addReturnNode(returnNode);
		return cfg;

	}

	/**
	 * Builds a control flow subgraph for a throw statement.
	 * @param entry The entry point for the subgraph.
	 * @param exit The exit point for the subgraph.
	 * @return A list of exit nodes for the subgraph.
	 */
	private static CFG build(ThrowStatement throwStatement) {
		
		CFGNode throwNode = new CFGNode(throwStatement);
		CFG cfg = new CFG(throwNode);
		cfg.addThrowNode(throwNode);
		return cfg;

	}

	/**
	 * Builds a control flow subgraph for a statement. If other statement types
	 * are handled properly then this should only be an expression.
	 * @param entry The entry point for the subgraph.
	 * @param exit The exit point for the subgraph.
	 * @return A list of exit nodes for the subgraph.
	 */
	private static CFG build(AstNode statement) {
		
		CFGNode expressionNode = new CFGNode(statement);
		CFG cfg = new CFG(expressionNode);
		cfg.addExitNode(expressionNode);
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
		} else if (node instanceof ThrowStatement) {
			return CFGFactory.build((ThrowStatement) node);
		} else if (node instanceof FunctionNode) {
			return null; // Function declarations shouldn't be part of the CFG.
		} else if (node instanceof Scope) {
			return CFGFactory.build((Scope) node);
		} else {
			return CFGFactory.build(node);
		}

	}
	
}
