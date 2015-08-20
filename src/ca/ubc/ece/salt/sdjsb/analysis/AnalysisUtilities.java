package ca.ubc.ece.salt.sdjsb.analysis;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.InfixExpression;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.ObjectLiteral;
import org.mozilla.javascript.ast.ParenthesizedExpression;
import org.mozilla.javascript.ast.PropertyGet;
import org.mozilla.javascript.ast.ScriptNode;

import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode.ChangeType;
import ca.ubc.ece.salt.sdjsb.analysis.boundedcontext.ChangeTypeFilterVisitor;
import ca.ubc.ece.salt.sdjsb.analysis.flow.IdentifiersTreeVisitor;

public class AnalysisUtilities {

	/**
	 * Returns the function signature.
	 * @param function
	 * @return The string representation of the signature.
	 */
	public static String getFunctionSignature(FunctionNode function) {

		List<AstNode> params = function.getParams();
        String signature = "(";

        for(AstNode param : params) {
            if(param instanceof Name) {
                if(!signature.equals("(")) signature += ",";
                signature += ((Name)param).getIdentifier();
            }
        }

        signature += ")";
        return signature;

	}

	/**
	 * @param function The function or script.
	 * @return The name of the function. "~script~" if it the script node and
	 * 		   "~anonymous~" if it is an anonymous function.
	 */
	public static String getFunctionName(ScriptNode function) {

		if(function instanceof FunctionNode) {
			String name = ((FunctionNode)function).getName();
			if(name.isEmpty()) {
				return "~anonymous~";
			}
			else {
				return name;
			}
		}
		else {
			return "~script~";
		}

	}

	/**
	 * Returns true if the AstNode is part of the parameter list of any of the
	 * functions in the variables scope.
	 * @param name The variable to check.
	 * @return The parameter declaration if a function in the variable's scope
	 * 		   contains the variable as a parameter (well, a variable with the
	 * 		   same name anyways, which won't always be correct but close
	 * 		   enough for us).
	 */
	public static AstNode isParameter(Name name) {

		AstNode parent = name.getParent();

		while(!(parent instanceof AstRoot)) {

			if(parent instanceof FunctionNode) {
				List<AstNode> parameters = ((FunctionNode)parent).getParams();
				for(AstNode parameter : parameters) {
					if(parameter instanceof Name) {
						if(((Name)parameter).getIdentifier().equals(name.getIdentifier())) return parameter;
					}
				}
			}

			parent = parent.getParent();
		}

		return null;

	}

	/**
	 * Gets the other side of an equivalence binary operator.
	 * @param node The node that may be part of an equivalence check.
	 * @return The AstNode on the other side of the equivalence operator
	 * 		   or null if the parent is not an equivalence operator.
	 */
	public static AstNode getComparison(AstNode node) {
		AstNode parent = node.getParent();

		/* Is this part of some binary operation? */
		if(parent instanceof InfixExpression) {
			InfixExpression ie = (InfixExpression) parent;

			/* Is the operator an equivalence operator? */
			if(AnalysisUtilities.isEquivalenceOperator(ie.getOperator())) {

				if(ie.getRight() == node) {
                    return ie.getLeft(); //CheckerUtilities.getIdentifier(ie.getLeft());
				}
				else {
                    return ie.getRight(); //CheckerUtilities.getIdentifier(ie.getRight());
				}
			}
		}

		return null;
	}

	/**
	 * Returns the top level identifier for a node. If the node is a name, it will
	 * check the parent nodes until it gets to the top of the identifier.
	 * @param node A subnode of an identifier.
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static AstNode getTopLevelFieldIdentifier(AstNode node) throws IllegalArgumentException {

		/* If this node is a field or method, get the parent. */
		if(node.getParent() instanceof InfixExpression) {

			InfixExpression ie = (InfixExpression) node.getParent();

			if(AnalysisUtilities.isIdentifierOperator(ie.getOperator()) &&
			   !(ie.getLeft() instanceof FunctionCall || ie.getRight() instanceof FunctionCall)) {
				return AnalysisUtilities.getTopLevelIdentifier(ie);
			}

		}

		return node;

	}

	/**
	 * Returns the top level identifier for a node. If the node is a name, it will
	 * check the parent nodes until it gets to the top of the identifier.
	 * @param node A subnode of an identifier.
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static AstNode getTopLevelIdentifier(AstNode node) throws IllegalArgumentException {

		/* If this node is a field or method, get the parent. */
		if(node.getParent() instanceof InfixExpression) {

			InfixExpression ie = (InfixExpression) node.getParent();

			if(AnalysisUtilities.isIdentifierOperator(ie.getOperator())) {
				return AnalysisUtilities.getTopLevelIdentifier(ie);
			}

		}

		return node;

	}

	/**
	 * Returns the variable, field or function identifier for the AstNode. If
	 * the node is not a Name, InfixEpxression or FunctionCall, or if the
	 * operator of an InfixExpression is not a field access (GETPROP), then it
	 * can't build an identifier and it returns null.
	 * @param node The node that represents an identifier.
	 * @return The variable, field or function identifier or null if an
	 * 		   identifier can't be built.
	 */
	public static String getIdentifier(AstNode node) throws IllegalArgumentException {
        if (node instanceof Name) {
            return ((Name)node).getIdentifier();
        }
        else if (node instanceof InfixExpression) {
            InfixExpression ie = (InfixExpression) node;
            if(AnalysisUtilities.isIdentifierOperator(ie.getOperator())) {
            	String left = getIdentifier(ie.getLeft());
            	String right = getIdentifier(ie.getRight());
            	if(left == null || right == null) return null;
                return left + "." + right;
            }
            return null; // Indicates we can't build an identifier for this expression.
        }
        else if (node instanceof FunctionCall) {
            FunctionCall fc = (FunctionCall) node;
            String identifier = getIdentifier(fc.getTarget());
            if(identifier == null) return null;
            return identifier + "()";
        }
        else if (node instanceof ParenthesizedExpression) {
        	ParenthesizedExpression pe = (ParenthesizedExpression) node;
        	String identifier = getIdentifier(pe.getExpression());
            if(identifier == null) return null;
            return identifier;
        }

        return null;
	}

	/**
	 * Returns the list of variable, field or function identifiers contained
	 * in an OR separated list. Use for getting all the identifiers on the
	 * right hand side of an assignment.
	 * @param node The node that represents the right hand side of an assignment.
	 * @return The list of variable, field or function identifiers.
	 */
	public static List<String> getRHSIdentifiers(AstNode node) {

		IdentifiersTreeVisitor visitor = new IdentifiersTreeVisitor();
		node.visit(visitor);
		return visitor.variableIdentifiers;

	}

	/**
	 * Returns true if the operator for the binary expression is an identifier
	 * operator (i.e. whatever is to the left should be included in the
	 * identity of the expression).
	 * @param tokenType The operator type.
	 * @return
	 */
    public static boolean isIdentifierOperator(int tokenType) {
        if(tokenType == Token.GETPROP || tokenType == Token.GETPROPNOWARN) {
            return true;
        }
        return false;
    }

    /**
     * Returns true if the operatory for the binary expression is an assignment
     * operator (i.e., an assignment or object
     * @param tokenType
     * @return
     */
    public static boolean isAssignmentOperator(int tokenType) {
    	if(tokenType == Token.ASSIGN || tokenType == Token.COLON) {
    		return true;
    	}
    	return false;
    }

	/**
	 * Returns true if the operator for the binary expression is an equivalence
	 * operator (i.e. ==, !=, ===, !==).
	 * @param tokenType The operator type.
	 * @return
	 */
    public static boolean isEquivalenceOperator(int tokenType) {
        if(tokenType == Token.SHEQ || tokenType == Token.SHNE
            || tokenType == Token.EQ || tokenType == Token.NE) {
            return true;
        }
        return false;
    }

	/**
	 * Generates a list of identifiers that were used in the tree.
	 * @param node the tree to look for uses in.
	 * @return the list of identifiers that were used in the tree.
	 */
	public static Set<String> getUsedIdentifiers(AstNode node) {

        UseTreeVisitor useVisitor = new UseTreeVisitor();
        node.visit(useVisitor);
        return useVisitor.getUsedIdentifiers();

	}

	/**
	 * Returns the last name of a function call. Ex: for function call
	 * object.method.call(...), it returns call. It handles anonymous functions
	 * calls. There is also a full name version of this function.
	 *
	 * @param node The script or function.
	 * @return The name of the function or script.
	 */
	public static String getFunctionCallName(FunctionCall call) {
		AstNode target = call.getTarget();

		if (target instanceof Name)
			return ((Name) target).getIdentifier();

		if (target instanceof PropertyGet)
			return ((PropertyGet) target).getRight().toSource();

		if (target instanceof FunctionNode) {
			Name targetFunctionName = ((FunctionNode) target).getFunctionName();

			if (targetFunctionName == null)
				return "~anonymous~";
			else
				return targetFunctionName.toString();
		}

		return "?";
	}

	/**
	 * Returns the full name of a function call. Ex: for function call
	 * object.method.call(...), it returns object.method.call. It handles
	 * anonymous functions calls. CAREFUL: if you have a chain call, it will
	 * return everything!
	 *
	 * @param node The script or function.
	 * @return The name of the function or script.
	 */
	public static String getFunctionFullCallName(FunctionCall call) {
		AstNode target = call.getTarget();

		if (target instanceof Name || target instanceof PropertyGet)
			return target.toSource();

		if (target instanceof FunctionNode) {
			Name targetFunctionName = ((FunctionNode) target).getFunctionName();

			if (targetFunctionName == null)
				return "~anonymous~";
			else
				return targetFunctionName.toString();
		}

		return "?";
	}


	/**
	 * Returns the name of a bounded context function call, without the bounded
	 * context method. Ex: for function call object.method.call(...), it returns
	 * object.method
	 *
	 * @param node The script or function.
	 * @return The name of the function or script.
	 */
	public static String getBoundedContextFunctionName(FunctionCall call) {
		PropertyGet target = (PropertyGet) call.getTarget();

		if (target.getLeft() instanceof Name || target.getLeft() instanceof PropertyGet) {
			return target.getLeft().toSource();
		}

		if (target.getLeft() instanceof FunctionNode) {
			Name targetFunctionName = ((FunctionNode) target.getLeft()).getFunctionName();

			if (targetFunctionName == null)
				return "~anonymous~";
			else
				return targetFunctionName.toString();
		}

		return "?";
	}

	/**
	 * Returns the changed arguments for a given function call We care for
	 * INSERTED or REMOVED parameters, not UPDATED. We use the mapped function
	 * to see if number of parameters on call has changed.
	 *
	 * @param call The function call
	 * @return The arguments that has been changed
	 */
	public static List<AstNode> getChangedArguments(FunctionCall call) {
		List<AstNode> arguments = new ArrayList<>();

		/*
		 * We only care for 2 cases: 1 - number of arguments has changed; 2 -
		 * number of arguments is the same, but type of any of them has changed.
		 */

		AstNode mappedNode = (AstNode) call.getMapping();

		if (mappedNode == null || !(mappedNode instanceof FunctionCall))
			return arguments;

		FunctionCall mappedCall = (FunctionCall) mappedNode;

		if (call.getArguments().size() == mappedCall.getArguments().size()) {
			/*
			 * Case 2: Look for type changes on arguments
			 */

			for (int argumentIndex = 0; argumentIndex < call.getArguments().size(); argumentIndex++) {
				AstNode argument = call.getArguments().get(argumentIndex);

				/*
				 * False positives
				 */
				if (argument instanceof FunctionCall || argument instanceof FunctionNode)
					continue;

				if (!argument.getTypeName()
						.equals(mappedCall.getArguments().get(argumentIndex).getTypeName())) {
					arguments.add(argument);
				}
			}
		} else {
			/*
			 * Case 1: Arguments count is not the same. Look for which arguments
			 * were inserted/deleted
			 */

			/*
			 * Inserted
			 */
			for (AstNode argument : call.getArguments()) {
				if (argument instanceof FunctionCall || argument instanceof FunctionNode)
					continue;

				if (argument.getChangeType() == ChangeType.INSERTED) {
					arguments.add(argument);
				}
			}

			/*
			 * Removed
			 */
			for (AstNode argument : mappedCall.getArguments()) {
				if (argument instanceof FunctionCall || argument instanceof FunctionNode)
					continue;

				if (argument.getChangeType() == ChangeType.REMOVED) {
					arguments.add(argument);
				}
			}
		}

		/*
		 * Anyway, we have to use a Visitor to look recursively for changes on
		 * ObjectLiterals
		 */
		for (AstNode argument : call.getArguments()) {

			/*
			 * If it is a ObjectLiteral, we should visit nodes to see if anything has changed
			 */
			if (argument instanceof ObjectLiteral) {
				ChangeTypeFilterVisitor visitor = new ChangeTypeFilterVisitor(ChangeType.REMOVED, ChangeType.UPDATED,
						ChangeType.INSERTED);
				argument.visit(visitor);

				/*
				 * If something has changed, store it, and change ChangeType of
				 * ObjectLiteral so alert is printed correctly
				 */
				if (visitor.storedNodes.size() > 0) {
					argument.setChangeType(ChangeType.UPDATED);
					arguments.add(argument);

					continue;
				}
			}

		}

		return arguments;
	}

}
