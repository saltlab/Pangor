package ca.ubc.ece.salt.sdjsb.analysis.learning.ast;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.KeywordLiteral;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.NodeVisitor;
import org.mozilla.javascript.ast.NumberLiteral;
import org.mozilla.javascript.ast.ScriptNode;
import org.mozilla.javascript.ast.StringLiteral;

import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode.ChangeType;

public class LearningAnalysisVisitor implements NodeVisitor {

	/** The feature vector for the visited function. **/
	private FeatureVector featureVector;

	/**
	 * Visits the script or function and returns a feature vector for it.
	 * @param function the script or function to visit.
	 * @return the feature vector for the function.
	 */
	public static FeatureVector getFeatureVector(ScriptNode function) {
		
		/* Create the feature vector by visiting the function. */
		LearningAnalysisVisitor visitor = new LearningAnalysisVisitor();
		function.visit(visitor);
		
		/* Set the function name before we return */
		if(function instanceof FunctionNode) {
			String name = ((FunctionNode)function).getName();
			if(name.isEmpty()) {
				visitor.featureVector.functionName = "~anonymous~";
			}
			else {
				visitor.featureVector.functionName = ((FunctionNode)function).getName();
			}
		}
		else {
			visitor.featureVector.functionName = "~script~";
		}

		return visitor.featureVector;
	}
	
	private LearningAnalysisVisitor() {
		this.featureVector = new FeatureVector();
	}

	@Override
	public boolean visit(AstNode node) {
		
		/* Check for statement types. */
		this.registerStatement(node, node.getChangeType());
		
		/* Check for keywords. */
		this.registerKeyword(node, node.getChangeType());
		
		/* Stop if this is a function declaration. */
		if(node instanceof FunctionNode) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * Checks if the node is a statement that we want to track and increments
	 * its count in the feature vector if it is.
	 * @param node The node to check.
	 */
	private void registerStatement(AstNode node, ChangeType changeType) {

		if(node == null) return;
		
		String statementType = node.getClass().getSimpleName();
		
		this.featureVector.addStatement(statementType, changeType);
		
	} 
	
	/**
	 * Checks if the node contains a keyword that we want to track and
	 * increments its count in the feature vector if it is.
	 * @param node The node to check.
	 */
	private void registerKeyword(AstNode node, ChangeType changeType) {

		String token = "";
		
		if(node instanceof Name) {
			Name name = (Name) node;
			token = name.getIdentifier();
			if(token.matches("e|err")) token = "error";
			else if(token.matches("cb|callb")) token = "callback";
		}
		else if(node instanceof KeywordLiteral) {
			KeywordLiteral kl = (KeywordLiteral) node;
			token = kl.toSource();
		}
		else if(node instanceof NumberLiteral) {
			NumberLiteral nl = (NumberLiteral) node;
			if(Double.parseDouble(nl.getValue()) == 0.0) {
				token = "zero";
			}
		}
		else if(node instanceof StringLiteral) {
			StringLiteral sl = (StringLiteral) node;
			if(sl.getValue().isEmpty()) {
				token = "blank";
			}
		}
		
		this.featureVector.addKeyword(token, changeType);
		
	}

}
