package ca.ubc.ece.salt.sdjsb.analysis.learning.ast;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.KeywordLiteral;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.NodeVisitor;
import org.mozilla.javascript.ast.NumberLiteral;
import org.mozilla.javascript.ast.ScriptNode;
import org.mozilla.javascript.ast.StringLiteral;

import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode.ChangeType;
import ca.ubc.ece.salt.sdjsb.analysis.learning.apis.Keyword;
import ca.ubc.ece.salt.sdjsb.analysis.learning.apis.Keyword.KeywordType;
import ca.ubc.ece.salt.sdjsb.analysis.prediction.PointsToPrediction;
import ca.ubc.ece.salt.sdjsb.analysis.specialtype.SpecialTypeAnalysisUtilities;

/**
 * Inspects scripts and functions for API keywords. 
 */
public class LearningAnalysisVisitor implements NodeVisitor {

	/** The feature vector for the visited function. **/
	private FeatureVector featureVector;
	
	/** The root of the function or script we are visiting. **/
	private ScriptNode root;

	/** 
	 * Utility for predicting points-to relationships between keywords and
	 * methods/fields/events in packages.
	 */
	private PointsToPrediction packageModel;
	
	/** 
	 * If true, will visit function signatures and bodies. This should be true
	 * when doing the initial keyword extraction (at the file level) and false
	 * when collecting features at the function level.
	 */
	private boolean visitFunctions;
	
	/**
	 * Visits the script and returns a feature vector containing only the
	 * keywords in the script. The feature vector will not contain package
	 * associations for the keywords. Unlike {@code getFunctionFeatureVector}, 
	 * this method extracts keywords from the entire script (i.e., it visits 
	 * function declarations and bodies).
	 * @param script The script to extract keywords from.
	 * @return A feature vector containing the keywords extracted from the 
	 * 		   script.
	 */
	public static FeatureVector getScriptFeatureVector(AstRoot script) {
		
		/* Create the feature vector by visiting the function. */
		LearningAnalysisVisitor visitor = new LearningAnalysisVisitor(script, null, true);
		script.visit(visitor);
		
		return visitor.featureVector;
		
	}

	/**
	 * Visits the script or function and returns a feature vector for it.
	 * @param function the script or function to visit.
	 * @return the feature vector for the function.
	 */
	public static FeatureVector getFunctionFeatureVector(ScriptNode function, PointsToPrediction packageModel) {
		
		/* Create the feature vector by visiting the function. */
		LearningAnalysisVisitor visitor = new LearningAnalysisVisitor(function, packageModel, false);
		function.visit(visitor);
		
		/* Store the source code for the function. Since we don't know if we
		 * are analyzing the source function or destination function, store
		 * it as both. When the source and destination feature vectors are 
		 * merged. */
		visitor.featureVector.sourceCode = function.toSource();
		visitor.featureVector.destinationCode = function.toSource();
		
		/* Set the function name before we return */
		if(function instanceof FunctionNode) {
			String name = ((FunctionNode)function).getName();
			if(name.isEmpty()) {
				visitor.featureVector.functionName = "~anonymous~";
			}
			else {
				visitor.featureVector.functionName = name;
			}
		}
		else {
			visitor.featureVector.functionName = "~script~";
		}

		return visitor.featureVector;
	}
	
	private LearningAnalysisVisitor(ScriptNode root, PointsToPrediction packageModel, boolean visitFunctions) {
		this.packageModel = packageModel;
		this.featureVector = new FeatureVector();
		this.root = root;
		this.visitFunctions = visitFunctions;
	}

	@Override
	public boolean visit(AstNode node) {
		
		/* Check for keywords. */
		this.registerKeyword(node, node.getChangeType());
		
		/* Stop if this is a function declaration. */
		if(!this.visitFunctions && node instanceof FunctionNode && node != this.root) {
			return false;
		}
		
		return true;

	}
	
	/**
	 * If the node is a potential keyword (Name, StringLiteral or NumberLiteral),
	 * get the node's context and look up the most likely artifact in a package
	 * that the keyword points to.
	 * 
	 * @param node The node to check.
	 * @param changeType How the node has been modified (inserted, removed, 
	 * 					 updated, etc.)
	 */
	private void registerKeyword(AstNode node, ChangeType changeType) {

		String token = "";
		
		KeywordType context = LearningUtilities.getTokenContext(node);
		
		if(context == KeywordType.UNKNOWN) return;

		/* Add a falsey keyword if we're checking if this node is truthy or 
		 * falsey. */
		if(SpecialTypeAnalysisUtilities.isFalsey(node)) {

			Keyword keyword = null;
			if(this.packageModel != null) {
				keyword = this.packageModel.getKeyword(context, "~falsey~");
			}
			else {
				keyword = new Keyword(context, "~falsey");
			}

			if(keyword != null) this.featureVector.addKeyword(keyword, changeType);

		}
		
		/* Get the relevant keyword from the node. */
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
			try { 
				if(Double.parseDouble(nl.getValue()) == 0.0) {
					token = "zero";
				}
			}
			catch (NumberFormatException ignore) { }
		}
		else if(node instanceof StringLiteral) {
			StringLiteral sl = (StringLiteral) node;
			if(sl.getValue().isEmpty()) {
				token = "blank";
			}
			else {
				token = sl.getValue();
			}
		}
		
		/* Insert the token into the feature vector if it is a keyword. */
		Keyword keyword = null;

		if(this.packageModel != null) {
			keyword = this.packageModel.getKeyword(context, token);
		}
		else {
			keyword = new Keyword(context, token);
		}

		if(keyword != null) this.featureVector.addKeyword(keyword, changeType);
		
	}

}
