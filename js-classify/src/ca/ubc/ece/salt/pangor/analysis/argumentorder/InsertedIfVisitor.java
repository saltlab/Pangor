package ca.ubc.ece.salt.pangor.analysis.argumentorder;

import java.util.HashSet;
import java.util.Set;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.IfStatement;
import org.mozilla.javascript.ast.NodeVisitor;

import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode.ChangeType;

/**
 * Visitor for storing inserted if statements. The store nodes serve as start
 * point for ArgumentOrderAnalysis.
 */
public class InsertedIfVisitor implements NodeVisitor {
	public Set<IfStatement> storedNodes = new HashSet<>();

	@Override
	public boolean visit(AstNode node) {
		/*
		 * If it is not an if statement, continue
		 */
		if (!(node instanceof IfStatement))
			return true;

		IfStatement statement = (IfStatement) node;

		/*
		 * If the entire block was inserted, we store it
		 */
		if (statement.getChangeType() == ChangeType.INSERTED) {
			storedNodes.add(statement);
		}

		return true;
	}

}