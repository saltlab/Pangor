package ca.ubc.ece.salt.pangor.test.ast;

import java.util.LinkedList;
import java.util.List;

import org.mozilla.javascript.Node;
import org.mozilla.javascript.ast.ArrayLiteral;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.Block;
import org.mozilla.javascript.ast.CatchClause;
import org.mozilla.javascript.ast.ConditionalExpression;
import org.mozilla.javascript.ast.DoLoop;
import org.mozilla.javascript.ast.ElementGet;
import org.mozilla.javascript.ast.ExpressionStatement;
import org.mozilla.javascript.ast.ForInLoop;
import org.mozilla.javascript.ast.ForLoop;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.IfStatement;
import org.mozilla.javascript.ast.InfixExpression;
import org.mozilla.javascript.ast.ObjectLiteral;
import org.mozilla.javascript.ast.ParenthesizedExpression;
import org.mozilla.javascript.ast.ReturnStatement;
import org.mozilla.javascript.ast.SwitchCase;
import org.mozilla.javascript.ast.SwitchStatement;
import org.mozilla.javascript.ast.ThrowStatement;
import org.mozilla.javascript.ast.TryStatement;
import org.mozilla.javascript.ast.UnaryExpression;
import org.mozilla.javascript.ast.VariableDeclaration;
import org.mozilla.javascript.ast.VariableInitializer;
import org.mozilla.javascript.ast.WhileLoop;
import org.mozilla.javascript.ast.WithStatement;

public class AstNodeTraverse {

	private static List<AstNode> childrenOf(AstRoot node) {

		List<AstNode> children = new LinkedList<AstNode>();
		for(Node child : node) children.add((AstNode) child);
		return children;

	}

	private static List<AstNode> childrenOf(FunctionNode node) {

		List<AstNode> children = new LinkedList<AstNode>();
		children.add(node.getFunctionName());
		for(AstNode param : node.getParams()) children.add(param);
		children.add(node.getBody());
		return children;

	}

	private static List<AstNode> childrenOf(Block node) {

		List<AstNode> children = new LinkedList<AstNode>();
		for(Node child : node) children.add((AstNode) child);
		return children;

	}

	private static List<AstNode> childrenOf(IfStatement node) {

		List<AstNode> children = new LinkedList<AstNode>();
		children.add(node.getCondition());
		children.add(node.getThenPart());
		children.add(node.getElsePart());
		return children;

	}

	private static List<AstNode> childrenOf(WhileLoop node) {

		List<AstNode> children = new LinkedList<AstNode>();
		children.add(node.getCondition());
		children.add(node.getBody());
		return children;

	}

	private static List<AstNode> childrenOf(DoLoop node) {

		List<AstNode> children = new LinkedList<AstNode>();
		children.add(node.getCondition());
		children.add(node.getBody());
		return children;

	}

	private static List<AstNode> childrenOf(ForLoop node) {

		List<AstNode> children = new LinkedList<AstNode>();
		children.add(node.getInitializer());
		children.add(node.getIncrement());
		children.add(node.getCondition());
		children.add(node.getBody());
		return children;

	}

	private static List<AstNode> childrenOf(ForInLoop node) {

		List<AstNode> children = new LinkedList<AstNode>();
		children.add(node.getIterator());
		children.add(node.getIteratedObject());
		children.add(node.getBody());
		return children;

	}

	private static List<AstNode> childrenOf(SwitchStatement node) {

		List<AstNode> children = new LinkedList<AstNode>();
		children.add(node.getExpression());
		for(AstNode switchCase : node.getCases()) children.add(switchCase);
		return children;

	}

	private static List<AstNode> childrenOf(WithStatement node) {

		List<AstNode> children = new LinkedList<AstNode>();
		children.add(node.getExpression());
		node.getStatement();
		return children;

	}

	private static List<AstNode> childrenOf(TryStatement node) {

		List<AstNode> children = new LinkedList<AstNode>();
		children.add(node.getTryBlock());
		children.add(node.getFinallyBlock());
		for(AstNode catchClause : node.getCatchClauses()) children.add(catchClause);
		return children;

	}

	private static List<AstNode> childrenOf(CatchClause node) {

		List<AstNode> children = new LinkedList<AstNode>();
		children.add(node.getCatchCondition());
		children.add(node.getBody());
		children.add(node.getVarName());
		return children;

	}

	private static List<AstNode> childrenOf(ReturnStatement node) {

		List<AstNode> children = new LinkedList<AstNode>();
		children.add(node.getReturnValue());
		return children;

	}

	private static List<AstNode> childrenOf(ThrowStatement node) {

		List<AstNode> children = new LinkedList<AstNode>();
		children.add(node.getExpression());
		return children;

	}

	private static List<AstNode> childrenOf(ArrayLiteral node) {

		List<AstNode> children = new LinkedList<AstNode>();
		for(AstNode element : node.getElements()) children.add(element);
		return children;

	}

	private static List<AstNode> childrenOf(InfixExpression node) {

		List<AstNode> children = new LinkedList<AstNode>();
		children.add(node.getLeft());
		children.add(node.getRight());
		return children;

	}

	private static List<AstNode> childrenOf(ConditionalExpression node) {

		List<AstNode> children = new LinkedList<AstNode>();
		children.add(node.getTestExpression());
		children.add(node.getTrueExpression());
		children.add(node.getFalseExpression());
		return children;

	}
	
	private static List<AstNode> childrenOf(ElementGet node) {

		List<AstNode> children = new LinkedList<AstNode>();
		children.add(node.getTarget());
		children.add(node.getElement());
		return children;

	}

	private static List<AstNode> childrenOf(ExpressionStatement node) {

		List<AstNode> children = new LinkedList<AstNode>();
		children.add(node.getExpression());
		return children;

	}

	private static List<AstNode> childrenOf(FunctionCall node) {

		List<AstNode> children = new LinkedList<AstNode>();
		children.add(node.getTarget());
		for(AstNode arg : node.getArguments()) children.add(arg);
		return children;

	}

	private static List<AstNode> childrenOf(ObjectLiteral node) {

		List<AstNode> children = new LinkedList<AstNode>();
		for(AstNode element : node.getElements()) children.add(element);
		return children;

	}

	private static List<AstNode> childrenOf(ParenthesizedExpression node) {

		List<AstNode> children = new LinkedList<AstNode>();
		children.add(node.getExpression());
		return children;

	}

	private static List<AstNode> childrenOf(SwitchCase node) {

		List<AstNode> children = new LinkedList<AstNode>();
		children.add(node.getExpression());
		for(AstNode statement : node.getStatements()) children.add(statement);
		return children;

	}

	private static List<AstNode> childrenOf(UnaryExpression node) {

		List<AstNode> children = new LinkedList<AstNode>();
		children.add(node.getOperand());
		return children;

	}

	private static List<AstNode> childrenOf(VariableDeclaration node) {

		List<AstNode> children = new LinkedList<AstNode>();
		for(AstNode initializer : node.getVariables()) children.add(initializer);
		return children;

	}

	private static List<AstNode> childrenOf(VariableInitializer node) {

		List<AstNode> children = new LinkedList<AstNode>();
		children.add(node.getTarget());
		children.add(node.getInitializer());
		return children;

	}

	/**
	 * The default is to return no children for the node. If we are doing a
	 * clone, the children will be the same objects as the the original.
	 * @param node
	 * @return the children of the node that should be cloned.
	 * 
	 */
	private static List<AstNode> childrenOf(AstNode node) {
		List<AstNode> children = new LinkedList<AstNode>();
		return children;
	}
	
	/**
	 * Calls the appropriate build method for the node type.
	 */
	public static List<AstNode> getChildrenOf(AstNode node) {
		
		if (node instanceof AstRoot) {
			return AstNodeTraverse.childrenOf((AstRoot)node);
		} else if (node instanceof FunctionNode) {
			return AstNodeTraverse.childrenOf((FunctionNode) node);
		} else if (node instanceof Block) {
			return AstNodeTraverse.childrenOf((Block) node);
		} else if (node instanceof IfStatement) {
			return AstNodeTraverse.childrenOf((IfStatement) node);
		} else if (node instanceof WhileLoop) {
			return AstNodeTraverse.childrenOf((WhileLoop) node);
		} else if (node instanceof DoLoop) {
			return AstNodeTraverse.childrenOf((DoLoop) node);
		} else if (node instanceof ForLoop) {
			return AstNodeTraverse.childrenOf((ForLoop) node);
		} else if (node instanceof ForInLoop) {
			return AstNodeTraverse.childrenOf((ForInLoop) node);
		} else if (node instanceof SwitchStatement) {
			return AstNodeTraverse.childrenOf((SwitchStatement) node);
		} else if (node instanceof WithStatement) {
			return AstNodeTraverse.childrenOf((WithStatement) node);
		} else if (node instanceof TryStatement) {
			return AstNodeTraverse.childrenOf((TryStatement) node);
		} else if (node instanceof CatchClause) {
			return AstNodeTraverse.childrenOf((CatchClause) node);
		} else if (node instanceof ReturnStatement) {
			return AstNodeTraverse.childrenOf((ReturnStatement) node);
		} else if (node instanceof ThrowStatement) {
			return AstNodeTraverse.childrenOf((ThrowStatement) node);
		} else if (node instanceof ArrayLiteral) {
			return AstNodeTraverse.childrenOf((ArrayLiteral) node);
		} else if (node instanceof InfixExpression) {
			return AstNodeTraverse.childrenOf((InfixExpression) node);
		} else if (node instanceof ConditionalExpression) {
			return AstNodeTraverse.childrenOf((ConditionalExpression) node);
		} else if (node instanceof ElementGet) {
			return AstNodeTraverse.childrenOf((ElementGet) node);
		} else if (node instanceof ExpressionStatement) {
			return AstNodeTraverse.childrenOf((ExpressionStatement) node);
		} else if (node instanceof FunctionCall) {
			return AstNodeTraverse.childrenOf((FunctionCall) node);
		} else if (node instanceof ObjectLiteral) {
			return AstNodeTraverse.childrenOf((ObjectLiteral) node);
		} else if (node instanceof ParenthesizedExpression) {
			return AstNodeTraverse.childrenOf((ParenthesizedExpression) node);
		} else if (node instanceof SwitchCase) {
			return AstNodeTraverse.childrenOf((SwitchCase) node);
		} else if (node instanceof UnaryExpression) {
			return AstNodeTraverse.childrenOf((UnaryExpression) node);
		} else if (node instanceof VariableDeclaration) {
			return AstNodeTraverse.childrenOf((VariableDeclaration) node);
		} else if (node instanceof VariableInitializer) {
			return AstNodeTraverse.childrenOf((VariableInitializer) node);
		} else {
			return AstNodeTraverse.childrenOf(node);
		}

	}

}
