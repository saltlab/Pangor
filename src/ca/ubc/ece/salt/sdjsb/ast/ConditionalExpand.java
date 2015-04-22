package ca.ubc.ece.salt.sdjsb.ast;

import org.apache.commons.lang3.tuple.Pair;
import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.ConditionalExpression;
import org.mozilla.javascript.ast.ExpressionStatement;
import org.mozilla.javascript.ast.ForInLoop;
import org.mozilla.javascript.ast.ForLoop;
import org.mozilla.javascript.ast.IfStatement;
import org.mozilla.javascript.ast.InfixExpression;
import org.mozilla.javascript.ast.NodeVisitor;
import org.mozilla.javascript.ast.ParenthesizedExpression;
import org.mozilla.javascript.ast.ReturnStatement;
import org.mozilla.javascript.ast.SwitchStatement;
import org.mozilla.javascript.ast.ThrowStatement;
import org.mozilla.javascript.ast.UnaryExpression;
import org.mozilla.javascript.ast.VariableDeclaration;
import org.mozilla.javascript.ast.VariableInitializer;
import org.mozilla.javascript.ast.WhileLoop;
import org.mozilla.javascript.ast.WithStatement;

/**
 * Visits a statement to find conditional expressions.
 */
class ConditionalExpand implements NodeVisitor {
	
	private AstNode condition;
	private boolean expanded;
	private boolean isTrueBranch;
	
	/**
	 * Expands the first ternary operator in the statement.
	 * @param statement The statement to expand.
	 * @param condition The current branching condition for the statement.
	 * @param isTrueBranch Set to true to expand the true branch of the ternary operator.
	 * @return The expanded statement and the condition for which it executes.
	 */
	public static Pair<AstNode, AstNode> expand(AstNode statement, AstNode condition, boolean isTrueBranch) {

		ConditionalExpand expander = new ConditionalExpand(condition, isTrueBranch);
		
		/* We only want to visit non-branching statements and branch-statement
		 * conditions. */
		if(statement instanceof ExpressionStatement)
            statement.visit(expander);
		else if(statement instanceof VariableDeclaration)
			statement.visit(expander);
		else if(statement instanceof ReturnStatement)
			statement.visit(expander);
		else if(statement instanceof ThrowStatement)
			statement.visit(expander);
		else if(statement instanceof IfStatement)
			((IfStatement)statement).getCondition().visit(expander);
		else if(statement instanceof WhileLoop)
			((WhileLoop)statement).getCondition().visit(expander);
		else if(statement instanceof ForLoop)
			((ForLoop)statement).getCondition().visit(expander);
		else if(statement instanceof ForInLoop)
			((ForInLoop)statement).getIteratedObject().visit(expander);
		else if(statement instanceof SwitchStatement)
			((SwitchStatement)statement).getExpression().visit(expander);
		else if(statement instanceof WithStatement)
			((WithStatement)statement).getExpression().visit(expander);
		else return null;
		
		if(expander.expanded){
			return Pair.of(statement, expander.condition);
		}
		
		return null;

	}
	
	private ConditionalExpand(AstNode condition, boolean isTrueBranch) {
		this.expanded = false;
		this.condition = condition;
		this.isTrueBranch = isTrueBranch;
	}
	
	private void setCondition(AstNode testExpression) {

        /* Make the condition false if this is a false path. */
        if(!this.isTrueBranch) {
            testExpression = new UnaryExpression(Token.NOT, 0, testExpression);
        }
        
        /* Add the condition. */
        if(this.condition == null) {
            this.condition = testExpression;
        }
        else {
            this.condition = new InfixExpression(Token.AND, this.condition, testExpression, 0);
        }
		
	}
	
	@Override
	public boolean visit(AstNode node) {

		if(this.expanded) return false; 

		/* We need to handle each node type on a case-by-case basis. */
		if(node instanceof InfixExpression) {
			
			InfixExpression ie = (InfixExpression) node;
			
			if(ie.getLeft() instanceof ConditionalExpression) {

				/* Expand the statement by pulling up. */
				ConditionalExpression ce = (ConditionalExpression) ie.getLeft();
				if(this.isTrueBranch) ie.setLeft(ce.getTrueExpression());
				else ie.setLeft(ce.getFalseExpression());
				
				/* Add the test condition. */
				this.setCondition(ce.getTestExpression());
				
				/* We have expanded the statement. */
				this.expanded = true;
				
			}
			else if(ie.getRight() instanceof ConditionalExpression) {

				/* Expand the statement by pulling up. */
				ConditionalExpression ce = (ConditionalExpression) ie.getRight();
				if(this.isTrueBranch) ie.setRight(ce.getTrueExpression());
				else ie.setRight(ce.getFalseExpression());

				/* Add the test condition. */
				this.setCondition(ce.getTestExpression());

				/* We have expanded the statement. */
				this.expanded = true;
				
			}
			
		}

		else if(node instanceof VariableInitializer) {
			
			VariableInitializer ie = (VariableInitializer) node;
			
			if(ie.getInitializer() instanceof ConditionalExpression) {

				/* Expand the statement by pulling up. */
				ConditionalExpression ce = (ConditionalExpression) ie.getInitializer();
				if(this.isTrueBranch) ie.setInitializer(ce.getTrueExpression());
				else ie.setInitializer(ce.getFalseExpression());
				
				/* Add the test condition. */
				this.setCondition(ce.getTestExpression());
				
				/* We have expanded the statement. */
				this.expanded = true;
				
			}
			
		}

		else if(node instanceof ParenthesizedExpression) {
			
			ParenthesizedExpression pe = (ParenthesizedExpression) node;
			
			if(pe.getExpression() instanceof ConditionalExpression) {

				/* Expand the statement by pulling up. */
				ConditionalExpression ce = (ConditionalExpression) pe.getExpression();
				if(this.isTrueBranch) pe.setExpression(ce.getTrueExpression());
				else pe.setExpression(ce.getFalseExpression());
				
				/* Add the test condition. */
				this.setCondition(ce.getTestExpression());
				
				/* We have expanded the statement. */
				this.expanded = true;
				
			}
			
		}

		else if(node instanceof ExpressionStatement) {
			
			ExpressionStatement es = (ExpressionStatement) node;
			
			if(es.getExpression() instanceof ConditionalExpression) {

				/* Expand the statement by pulling up. */
				ConditionalExpression ce = (ConditionalExpression) es.getExpression();
				if(this.isTrueBranch) es.setExpression(ce.getTrueExpression());
				else es.setExpression(ce.getFalseExpression());
				
				/* Add the test condition. */
				this.setCondition(ce.getTestExpression());
				
				/* We have expanded the statement. */
				this.expanded = true;
				
			}
			
		}
		
		if(this.expanded) return false; 
		return true;
	}
	
}