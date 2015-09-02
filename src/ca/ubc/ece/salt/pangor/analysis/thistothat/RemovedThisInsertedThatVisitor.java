package ca.ubc.ece.salt.pangor.analysis.thistothat;

import java.util.ArrayList;
import java.util.List;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.KeywordLiteral;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.NodeVisitor;
import org.mozilla.javascript.ast.PropertyGet;

import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode.ChangeType;

/**
 * Visitor for storing "this.something" replaced with "that.something".
 * Actually, we look for that, self and other keywords on the destination
 */

public class RemovedThisInsertedThatVisitor implements NodeVisitor {
	/**
	 * For our convenience on generating the alerts, we store both the node and
	 * the keyword that replaced 'this'
	 */
	public List<PropertyGet> removedThis = new ArrayList<>();
	public List<PropertyGet> insertedThat = new ArrayList<>();

	/**
	 * By default, functions are also visited, but sometimes we may want to
	 * change it (see Popcorn false positive on test case)
	 */
	private boolean visitFunctions = true;

	/**
	 * Default constructor
	 */
	public RemovedThisInsertedThatVisitor() {
		super();
	}

	public void setVisitFunctions(boolean visitFunctions) {
		this.visitFunctions = visitFunctions;
	}

	@Override
	public boolean visit(AstNode node) {
		if (node instanceof FunctionNode && !visitFunctions)
			return false;

		/* If it is not an PropertyGet, continue */
		if (!(node instanceof PropertyGet))
			return true;

		PropertyGet propertyGet = (PropertyGet) node;

		/*
		 * If it is literal, check for 'this'
		 */
		if (propertyGet.getLeft() instanceof KeywordLiteral) {
			KeywordLiteral literal = (KeywordLiteral) propertyGet.getLeft();

			if (literal.toSource().equals("this")
					&& (literal.getChangeType() == ChangeType.REMOVED || literal.getChangeType() == ChangeType.UPDATED))
				removedThis.add(propertyGet);

			return true;
		}

		/*
		 * If it is name, check for 'that'
		 */
		if (propertyGet.getLeft() instanceof Name) {
			Name name = (Name) propertyGet.getLeft();

			if (name.getIdentifier().matches("(that)|(self)|(me)|(my)|(context)|(_this)")
					&& (name.getChangeType() == ChangeType.INSERTED || name.getChangeType() == ChangeType.UPDATED))
				insertedThat.add(propertyGet);

			return true;
		}

		return true;
	}

}
