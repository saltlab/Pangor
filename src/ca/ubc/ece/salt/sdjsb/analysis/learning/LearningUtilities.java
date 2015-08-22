package ca.ubc.ece.salt.sdjsb.analysis.learning;

import java.util.Arrays;
import java.util.List;

import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.Assignment;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.BreakStatement;
import org.mozilla.javascript.ast.CatchClause;
import org.mozilla.javascript.ast.ContinueStatement;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.InfixExpression;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.NewExpression;
import org.mozilla.javascript.ast.NumberLiteral;
import org.mozilla.javascript.ast.ObjectProperty;
import org.mozilla.javascript.ast.PropertyGet;
import org.mozilla.javascript.ast.ReturnStatement;
import org.mozilla.javascript.ast.StringLiteral;
import org.mozilla.javascript.ast.TryStatement;
import org.mozilla.javascript.ast.VariableDeclaration;
import org.mozilla.javascript.ast.VariableInitializer;

import ca.ubc.ece.salt.sdjsb.analysis.specialtype.SpecialTypeAnalysisUtilities;
import ca.ubc.ece.salt.sdjsb.learning.apis.KeywordDefinition.KeywordType;
import ca.ubc.ece.salt.sdjsb.learning.apis.KeywordUse.KeywordContext;

/**
 * Provides AST utilities to help collect feature information (i.e., keyword
 * info) from the source code analysis.
 */
public class LearningUtilities {

	/**
	 * Gets the type of artifact the keyword refers to.
	 * @param token The AST node that may be a keyword.
	 * @return The artifact type.
	 */
	public static KeywordType getTokenType(AstNode token) {

		/* If the token is a reserved word, we can already infer it's type. */
		if(isJavaScriptReserved(token)) return KeywordType.RESERVED;

		KeywordType type = KeywordType.UNKNOWN;
		type = typeSwitch(token);

		return type;
	}

	/**
	 * Gets the context under which the artifact is being used (e.g., as an
	 * argument, parameter, exception, etc. see @code{Keyword}.
	 * @param token the AST node that may be a keyword.
	 * @return The context in which the artifact is being used.
	 */
	public static KeywordContext getTokenContext(AstNode token) {

		/* Look at the token's parent to determine the context in which it is
		 * used. */

		KeywordContext context = KeywordContext.UNKNOWN;
		context = contextSwitch(token);

		return context;

	}

	/**
	 * @return True if the token is a JavaScript reserved word.
	 */
	private static boolean isJavaScriptReservedLiteral(AstNode token) {

		List<String> JAVASCRIPT_RESERVED_WORDS = Arrays.asList( "abstract",
				"arguments", "boolean", "break", "byte", "case", "catch",
				"char", "class", "const", "continue", "debugger", "default",
				"delete", "do", "double", "else", "enum", "eval", "export",
				"extends", "false", "final", "finally", "float", "for",
				"function", "goto", "if", "implements", "import", "in",
				"instanceof", "int", "interface", "let", "long", "native",
				"new", "null", "package", "private", "protected", "public",
				"return", "short", "static", "super", "switch", "synchronized",
				"this", "undefined", "throw", "throws", "transient", "true",
				"try", "typeof", "var", "void", "volatile", "while", "with",
				"yield");

		if(token instanceof Name) {
			Name name = (Name) token;
			if(JAVASCRIPT_RESERVED_WORDS.contains(name.getIdentifier())) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Switches on the ancestor of an AstNode to determine the keyword type.
	 */
	private static KeywordType typeSwitch(AstNode token) {

		if(token == null || token.getParent() == null) return KeywordType.UNKNOWN;

		AstNode parent = token.getParent();

		/* Special case: falsey */
		if(SpecialTypeAnalysisUtilities.isFalsey(token)) {
			return KeywordType.RESERVED;
		}
		/* Check for special types. */
		if(token instanceof ReturnStatement ||
				token instanceof BreakStatement ||
				token instanceof ContinueStatement ||
				token instanceof VariableDeclaration ||
				token instanceof NewExpression ||
				token instanceof TryStatement) {
			return KeywordType.RESERVED;
		}
		else if(token instanceof StringLiteral) {

			if(((StringLiteral)token).toSource().equals("\"\"")) {
				return KeywordType.RESERVED;
			}
		}
		else if((token instanceof NumberLiteral) && ((NumberLiteral)token).getNumber() == 0) {
			return KeywordType.RESERVED;
		}

		/* Check for class, method and parameter declarations. */
		if (parent instanceof FunctionNode) {

			FunctionNode function = (FunctionNode) parent;

			if(function.getFunctionName() == token && Character.isUpperCase(function.getName().charAt(0))) {
				return KeywordType.CLASS;
			}
			else if(function.getFunctionName() == token) {
				return KeywordType.METHOD;
			}
			else if(function.getParams().contains(token)) {
				return KeywordType.PARAMETER;
			}

		}
		/* Check for variable declarations. */
		else if(parent instanceof VariableInitializer) {

			VariableInitializer initializer = (VariableInitializer) parent;
			if(initializer.getTarget() == token) return KeywordType.VARIABLE;

		}

		/*
		 * Check for direct function call related keywords, like
		 *   require("package");
		 *   method("foo");
		 */
		else if(parent instanceof FunctionCall) {

			FunctionCall call = (FunctionCall) parent;

			/* Check for packages. */
			if(call.getTarget() instanceof Name) {

				Name target = (Name)call.getTarget();

				if(target.getIdentifier().equals("require") && call.getArguments().size() == 1) {
					AstNode pack = call.getArguments().get(0);
					if(pack instanceof StringLiteral && pack == token) {
						return KeywordType.PACKAGE;
					}
				}
				else if(target.getIdentifier().equals("~exception")) {
					return KeywordType.EXCEPTION;
				}
				else if (target == token) {
					/*
					 * If is not require("package"), but this is exactly the
					 * target from the function call, it is a method
					 *
					 * Ex: method("foo");
					 */
					return KeywordType.METHOD;
				}

			}

			/* Check for events. */
			else if(call.getTarget() instanceof PropertyGet) {

				PropertyGet target = (PropertyGet) call.getTarget();

				if(target.getProperty().getIdentifier().equals("on") && call.getArguments().size() == 2) {
					AstNode event = call.getArguments().get(0);
					if(event instanceof StringLiteral && event == token) {
						return KeywordType.EVENT;
					}
				}
				else if(target.getProperty().getIdentifier().equals("removeListener") && call.getArguments().size() == 2) {
					AstNode event = call.getArguments().get(0);
					if(event instanceof StringLiteral && event == token) {
						return KeywordType.EVENT;
					}
				}
				else if(target.getProperty().getIdentifier().equals("removeAllListeners") && call.getArguments().size() == 1) {
					AstNode event = call.getArguments().get(0);
					if(event instanceof StringLiteral && event == token) {
						return KeywordType.EVENT;
					}
				}

			}

		}
		/*
		 * Check for object property function calls, like
		 *   variable.method();
		 *
		 * If parent is PropertyGet and parent of parent is FunctionCall, this is
		 * either the variable, or the method, so we check for each one of these
		 * cases
		 *
		 * Special cases of variable.field.method() and variable.field.field.method()
		 * are handled in getVariableOrFieldType()
		 */
		else if (parent instanceof PropertyGet
				&& parent.getParent() instanceof FunctionCall) {

			PropertyGet propertyGet = (PropertyGet) token.getParent();

			if (propertyGet.getTarget() == token) {

				/* If the first character is upper case, consider it a class. */
				if(Character.isUpperCase(token.toSource().charAt(0))) {
					return KeywordType.CLASS;
				}

				/* Otherwise it is a variable. */
				return KeywordType.VARIABLE;

			}
			else if (propertyGet.getProperty() == token) {
				return KeywordType.METHOD;
			}

		}

		/*
		 * Catch clauses
		 */
		else if(parent instanceof CatchClause) {

			CatchClause catchClause = (CatchClause) parent;
			if(catchClause.getVarName().toSource().equals(token.toSource())) {
				return KeywordType.EXCEPTION;
			}

		}

		/*
		 * Check for variable or field
		 */
		if(token instanceof Name) {
			return getVariableOrFieldType(token, (Name)token);
		}

		return KeywordType.UNKNOWN;

	}

	/**
	 * Switches on the ancestor of an AstNode to determine the keyword context.
	 */
	private static KeywordContext contextSwitch(AstNode token) {

		if(token == null || token.getParent() == null) return KeywordContext.UNKNOWN;

		AstNode parent = token.getParent();

		/* Special case: falsey */
		if(SpecialTypeAnalysisUtilities.isFalsey(token)) {
			return KeywordContext.CONDITION;
		}
		/* Special case: statements */
		else if(token instanceof ReturnStatement ||
				token instanceof BreakStatement ||
				token instanceof ContinueStatement ||
				token instanceof VariableDeclaration ||
				token instanceof NewExpression ||
				token instanceof TryStatement) {
			return KeywordContext.STATEMENT;
		}
		/* Special case: infix expression that compares a falsey keyword? */
		else if(token instanceof InfixExpression) {
			InfixExpression ie = (InfixExpression) token;
			if(ie.getType() == Token.SHEQ || ie.getType() == Token.SHNE) {
				if(SpecialTypeAnalysisUtilities.getSpecialType(ie.getLeft()) != null ||
				   SpecialTypeAnalysisUtilities.getSpecialType(ie.getRight()) != null) {
					/* Then we consider it a 'typeof' keyword. */
					return KeywordContext.CONDITION;
				}
			}
		}
		/* Check for class, method and parameter declarations. */
		else if (parent instanceof FunctionNode) {

			FunctionNode function = (FunctionNode) parent;

			/* Is this a class declaration? */
			if(function.getFunctionName() == token && Character.isUpperCase(function.getName().charAt(0))) {
				return KeywordContext.CLASS_DECLARATION;
			}
			else if(function.getFunctionName() == token) {
				return KeywordContext.METHOD_DECLARATION;
			}
			else if(function.getParams().contains(token)) {
				return KeywordContext.PARAMETER_DECLARATION;
			}

		}
		/* Check for variable declarations. */
		else if(parent instanceof VariableInitializer) {

			VariableInitializer initializer = (VariableInitializer) parent;
			if(initializer.getTarget() == token) return KeywordContext.VARIABLE_DECLARATION;

		}
		/* Check for function call related uses. */
		else if(parent instanceof FunctionCall) {

			FunctionCall call = (FunctionCall) parent;

			/* Check for the require use. */
			if(call.getTarget() instanceof Name) {

				Name target = (Name)call.getTarget();
				if(target.getIdentifier().equals("require") && call.getArguments().size() == 1) {
					AstNode pack = call.getArguments().get(0);
					if(pack instanceof StringLiteral && pack == token) {
						return KeywordContext.REQUIRE;
					}
				}

			}
			/* Check for event uses. */
			else if(call.getTarget() instanceof PropertyGet) {

				PropertyGet target = (PropertyGet) call.getTarget();

				if(target.getProperty().getIdentifier().equals("on") && call.getArguments().size() == 2) {
					AstNode event = call.getArguments().get(0);
					if(event instanceof StringLiteral && event == token) {
						return KeywordContext.EVENT_REGISTER;
					}
				}
				else if(target.getProperty().getIdentifier().equals("removeListener") && call.getArguments().size() == 2) {
					AstNode event = call.getArguments().get(0);
					if(event instanceof StringLiteral && event == token) {
						return KeywordContext.EVENT_REMOVE;
					}
				}
				else if(target.getProperty().getIdentifier().equals("removeAllListeners") && call.getArguments().size() == 1) {
					AstNode event = call.getArguments().get(0);
					if(event instanceof StringLiteral && event == token) {
						return KeywordContext.EVENT_REMOVE;
					}
				}

			}

		}
		/* Check for exception catching. */
		else if(parent instanceof CatchClause) {

			CatchClause catchClause = (CatchClause) parent;
			if(catchClause.getVarName() == token) {
				return KeywordContext.EXCEPTION_CATCH;
			}

		}

		/* Since it's not any of the above contexts, it is likely a variable,
		 * field or reserved word use. */
		return getVariableOrFieldContext(token);

	}

	/**
	 * Finds the type of a variable or field (field or method).
	 * @param vf The variable or field.
	 * @return The type of the keyword.
	 */
	private static KeywordType getVariableOrFieldType(AstNode vf, Name node) {

		/* This access is part of another access. Recursively find the context. */

		if(vf.getParent() instanceof PropertyGet) {
			return getVariableOrFieldType(vf.getParent(), node);
		}

		/*
		 * Handles variable.field.field.method()
		 *
		 * If we went up the tree, we are a direct child of a property get and
		 * we found a function in our way up
		 */
		if (vf != node
				&& node.getParent() instanceof PropertyGet
				&& vf.getParent() instanceof FunctionCall) {
			PropertyGet property = (PropertyGet) node.getParent();

			/*
			 * And we are the target of our parent property get, this must be a
			 * variable
			 */
			if (property.getTarget() == node) {
				return KeywordType.VARIABLE;
			}
		}

		if(vf instanceof PropertyGet) {

			if(node.getIdentifier().equals(node.getIdentifier().toUpperCase())) {
				return KeywordType.CONSTANT;
			}
			else {
				return KeywordType.FIELD;
			}

		}
		else {
			return KeywordType.VARIABLE;
		}

	}

	/**
	 * Finds the context in which a field is being used.
	 * @param vfr The variable, field or reserved word.
	 * @return The context in which the field is used.
	 */
	private static KeywordContext getVariableOrFieldContext(AstNode vfr) {

		/* This access is part of another access. Recursively find the context. */

		if(vfr.getParent() instanceof PropertyGet) {
			return getVariableOrFieldContext(vfr.getParent());
		}

		/* We can now determine how the field is being used. */

		if(vfr.getParent() instanceof FunctionCall) {
			FunctionCall call = (FunctionCall) vfr.getParent();
			if(call.getTarget() == vfr) {
				return KeywordContext.METHOD_CALL;
			}
			else if(call.getTarget() instanceof Name && ((Name)call.getTarget()).getIdentifier().equals("~exception")) {
				return KeywordContext.EXCEPTION_CATCH;
			}
			else {
				return KeywordContext.ARGUMENT;
			}
		}
		else if(vfr.getParent() instanceof VariableInitializer) {
			VariableInitializer initializer = (VariableInitializer) vfr.getParent();
			if(initializer.getInitializer() == vfr) return KeywordContext.ASSIGNMENT_RHS;
		}
		else if(vfr.getParent() instanceof Assignment ||
				vfr.getParent() instanceof ObjectProperty) {
			InfixExpression assignment = (InfixExpression) vfr.getParent();
			if(assignment.getLeft() == vfr) return KeywordContext.ASSIGNMENT_LHS;
			else return KeywordContext.ASSIGNMENT_RHS;
		}
		else if(vfr.getParent() instanceof InfixExpression) {
			InfixExpression ie = (InfixExpression) vfr.getParent();
			if(isConditionalComparator(ie.getOperator())) return KeywordContext.CONDITION;
			return KeywordContext.EXPRESSION;
		}

		return KeywordContext.UNKNOWN;

	}

	/**
	 * Gets the type of artifact the keyword refers to.
	 * @param token The AST node that may be a keyword.
	 * @return The artifact type.
	 */
	public static boolean isJavaScriptReserved(AstNode token) {

		/* If the token is a reserved word, we can already infer it's type. */
		if(isJavaScriptReservedLiteral(token)) return true;

		/* UnaryExpression or KeywordLiteral? */
		switch(token.getType()) {
		case Token.TYPEOF:
		case Token.DELPROP:
		case Token.DEL_REF:
		case Token.INC:
		case Token.DEC:
		case Token.NULL:
		case Token.THIS:
		case Token.TRUE:
		case Token.FALSE:
			return true;
		}

		/* Infix expression that compares a falsey keyword? */
		if(token instanceof InfixExpression) {
			InfixExpression ie = (InfixExpression) token;
			if(ie.getType() == Token.SHEQ || ie.getType() == Token.SHNE) {
				if(SpecialTypeAnalysisUtilities.getSpecialType(ie.getLeft()) != null ||
				   SpecialTypeAnalysisUtilities.getSpecialType(ie.getRight()) != null) {
					/* Then we consider it a 'typeof' keyword. */
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Determines whether or not the boolean operator is a conditional
	 * comparator.
	 * @param tokenType The Token type.
	 * @return True if the token is a conditional comparator (e.g., === or >).
	 */
	private static boolean isConditionalComparator(int tokenType) {
		switch(tokenType) {
		case Token.EQ:
		case Token.NE:
		case Token.LT:
		case Token.GT:
		case Token.GE:
		case Token.OR:
		case Token.AND:
		case Token.SHEQ:
		case Token.SHNE:
			return true;
		default:
			return false;
		}
	}

}