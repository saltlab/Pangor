package ca.ubc.ece.salt.pangor.analysis.learning;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.BreakStatement;
import org.mozilla.javascript.ast.CatchClause;
import org.mozilla.javascript.ast.ContinueStatement;
import org.mozilla.javascript.ast.DoLoop;
import org.mozilla.javascript.ast.ExpressionStatement;
import org.mozilla.javascript.ast.ForInLoop;
import org.mozilla.javascript.ast.ForLoop;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.IfStatement;
import org.mozilla.javascript.ast.NodeVisitor;
import org.mozilla.javascript.ast.ReturnStatement;
import org.mozilla.javascript.ast.SwitchCase;
import org.mozilla.javascript.ast.SwitchStatement;
import org.mozilla.javascript.ast.ThrowStatement;
import org.mozilla.javascript.ast.TryStatement;
import org.mozilla.javascript.ast.VariableDeclaration;
import org.mozilla.javascript.ast.WhileLoop;
import org.mozilla.javascript.ast.WithStatement;

/**
 * Computes the change complexity score for a script. The change complexity
 * score is the number of statements that have been modified (i.e., inserted,
 * removed or updated). Moved statements are not considered modified.
 */
public class ChangeComplexityVisitor implements NodeVisitor {

	private int score;

	public static int getChangeComplexity(AstRoot root) {

		ChangeComplexityVisitor visitor = new ChangeComplexityVisitor();
		root.visit(visitor);
		return visitor.score;

	}

	public ChangeComplexityVisitor() {
		this.score = 0;
	}

	@Override
	public boolean visit(AstNode node) {

		if((node instanceof VariableDeclaration && ((VariableDeclaration)node).isStatement())
				|| node instanceof ExpressionStatement
				|| node instanceof ReturnStatement
				|| node instanceof BreakStatement
				|| node instanceof ContinueStatement
				|| node instanceof ThrowStatement) {
			if(this.checkSubExpression(node)) this.incrementScore(node);
		}
		else if(node instanceof IfStatement) {
			IfStatement ifStatement = (IfStatement) node;
			if(changeTypeModified(ifStatement)) this.incrementScore(node);
			else if(this.checkSubExpression(ifStatement.getCondition())) this.incrementScore(node);
		}
		else if(node instanceof WithStatement) {
			WithStatement withStatement = (WithStatement) node;
			if(changeTypeModified(withStatement)) this.incrementScore(node);
			else if(this.checkSubExpression(withStatement.getExpression())) this.incrementScore(node);
		}
		else if(node instanceof TryStatement) {
			if(changeTypeModified(node)) this.incrementScore(node);
		}
		else if(node instanceof CatchClause) {
			CatchClause clause = (CatchClause) node;
			if(changeTypeModified(clause)) this.incrementScore(node);
			else if(this.checkSubExpression(clause.getCatchCondition())) this.incrementScore(node);
		}
		else if(node instanceof SwitchStatement) {
			SwitchStatement switchStatement = (SwitchStatement) node;
			if(changeTypeModified(switchStatement)) this.incrementScore(node);
			else if(this.checkSubExpression(switchStatement.getExpression())) this.incrementScore(node);
		}
		else if(node instanceof SwitchCase) {
			if(changeTypeModified(node)) this.incrementScore(node);
		}
		else if(node instanceof DoLoop) {
			DoLoop doLoop = (DoLoop) node;
			if(changeTypeModified(doLoop))  this.incrementScore(node);
			else if(this.checkSubExpression(doLoop.getCondition())) this.incrementScore(node);
		}
		else if(node instanceof ForInLoop) {
			ForInLoop loop = (ForInLoop) node;
			if(changeTypeModified(loop)) this.incrementScore(node);
			else if(this.checkSubExpression(loop)) this.incrementScore(node);
		}
		else if(node instanceof ForLoop) {
			ForLoop loop = (ForLoop) node;
			if(changeTypeModified(loop)) this.incrementScore(node);
			else if (this.checkSubExpression(loop)) this.incrementScore(node);
		}
		else if(node instanceof WhileLoop) {
			WhileLoop loop = (WhileLoop) node;
			if(changeTypeModified(loop)) this.incrementScore(node);
			else if (this.checkSubExpression(loop)) this.incrementScore(node);
		}
		else if(node instanceof FunctionNode) {
			FunctionNode function = (FunctionNode) node;
			if(changeTypeModified(function)) this.incrementScore(node);
			else {
				for(AstNode param : function.getParams()) {
					if(this.checkSubExpression(param)) {
						this.incrementScore(node);
						break;
					}
				}
			}
		}


		return true;
	}

	/**
	 * Increment the score. Good for debugging.
	 */
	private void incrementScore(AstNode node) {
		this.score++;
	}

	/**
	 * Checks if a statement has changes (i.e., is inserted, removed or updated).
	 * @param node The statement
	 * @return
	 */
	private static boolean changeTypeModified(AstNode node) {
		switch(node.getChangeType()) {
		case INSERTED:
		case REMOVED:
		case UPDATED:
			return true;
		case MOVED:
		case UNCHANGED:
		case UNKNOWN:
		default:
			return false;
		}

	}

	/**
	 * Checks if a statement has changes (i.e., is inserted, removed or updated).
	 * @param node The statement
	 * @return true if the statement was modified.
	 */
	public boolean checkSubExpression(AstNode node) {
		if(node == null) return false;
		ExpressionChangeVisitor visitor = new ExpressionChangeVisitor();
		node.visit(visitor);
		if(visitor.isModified) return true;
		return false;
	}

	/**
	 * Checks if a statement has changes (i.e., is inserted, removed or updated).
	 */
	private class ExpressionChangeVisitor implements NodeVisitor {

		private boolean isModified;

		public ExpressionChangeVisitor() {
			this.isModified = false;
		}

		@Override
		public boolean visit(AstNode node) {

			/* Do not visit FunctionNodes. */
			if(node instanceof FunctionNode) return false;

			if(changeTypeModified(node)) {
				this.isModified = true;
				return false;
			}

			return true;

		}

	}

}
