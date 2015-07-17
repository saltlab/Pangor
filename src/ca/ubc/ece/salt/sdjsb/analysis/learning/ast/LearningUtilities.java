package ca.ubc.ece.salt.sdjsb.analysis.learning.ast;

import java.util.Arrays;
import java.util.List;

import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.Assignment;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.CatchClause;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.InfixExpression;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.ObjectProperty;
import org.mozilla.javascript.ast.PropertyGet;
import org.mozilla.javascript.ast.StringLiteral;
import org.mozilla.javascript.ast.VariableInitializer;

import ca.ubc.ece.salt.sdjsb.analysis.learning.apis.Keyword.KeywordContext;
import ca.ubc.ece.salt.sdjsb.analysis.learning.apis.Keyword.KeywordType;

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
	private static boolean isJavaScriptReserved(AstNode token) {
		
		List<String> JAVASCRIPT_RESERVED_WORDS = Arrays.asList( "abstract",
				"arguments", "boolean", "break", "byte", "case", "catch",
				"char", "class", "const", "continue", "debugger", "default",
				"delete", "do", "double", "else", "enum", "eval", "export",
				"extends", "false", "final", "finally", "float", "for", 
				"function", "goto", "if", "implements", "import", "in",
				"instanceof", "int", "interface", "let", "long", "native",
				"new", "null", "package", "private", "protected", "public",
				"return", "short", "static", "super", "switch", "synchronized",
				"this", "throw", "throws", "transient", "true", "try", "typeof",
				"var", "void", "volatile", "while", "with", "yield");
		
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
		
		AstNode parent = token.getParent();
		
		if(parent == null || token == null) return KeywordType.UNKNOWN;
		
		if (parent instanceof FunctionNode) {

			FunctionNode function = (FunctionNode) parent;
			
			/* Is this a class declaration? */
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
		else if(parent instanceof FunctionCall) {
			
			FunctionCall call = (FunctionCall) parent;

			if(call.getTarget() instanceof Name) {

				Name target = (Name)call.getTarget();
				if(target == token) {
					return KeywordType.METHOD;
				}
				else if(target.getIdentifier().equals("require") && call.getArguments().size() == 1) {
					AstNode pack = call.getArguments().get(0);
					if(pack instanceof StringLiteral && pack == token) {
						return KeywordType.PACKAGE;
					}
				}
				
			}
			else if(call.getTarget() instanceof PropertyGet) {
				
				/* Should we differentiate between event registrations and 
				 * event removals? Not needed for determining which API it's
				 * from but could be useful for clustering. */
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

			if(call.getArguments().contains(token)) {
				return KeywordType.VARIABLE;
			}

		}
		else if(parent instanceof PropertyGet) {
			
			/* Should we differentiate between field accesses and field 
			 * assignments? Currently we do not. */
			PropertyGet access = (PropertyGet) parent;
			if(token instanceof Name && access.getProperty() == token) {
				Name name = (Name)token;
				
				if(name.getIdentifier().equals(name.getIdentifier().toUpperCase())) {
					return KeywordType.CONSTANT;
				}
				else {
					return KeywordType.FIELD;
				}
			}
			
		}
		else if(parent instanceof CatchClause) {
			
			CatchClause catchClause = (CatchClause) parent;
			if(catchClause.getVarName() == token) {
				return KeywordType.EXCEPTION;
			}
			
		}
		
		return KeywordType.UNKNOWN;

	}

	/**
	 * Switches on the ancestor of an AstNode to determine the keyword context.
	 */
	private static KeywordContext contextSwitch(AstNode token) {
		
		AstNode parent = token.getParent();
		
		if(parent == null || token == null) return KeywordContext.UNKNOWN;
		
		/* Check for class, method and parameter declarations. */
		if (parent instanceof FunctionNode) {

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
				
				/* Should we differentiate between event registrations and 
				 * event removals? Not needed for determining which API it's
				 * from but could be useful for clustering. */
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
	 * Finds the context in which a field is being used.
	 * @param vfr The variable, field or reserved word.
	 * @return The context in which the field is used.
	 */
	private static KeywordContext getVariableOrFieldContext(AstNode vfr) {

		/* This access is part of another access. Recursively find the context. */

		if(vfr.getParent() instanceof PropertyGet) {
			return getVariableOrFieldContext((PropertyGet) vfr.getParent());
		}
		
		/* We can now determine how the field is being used. */

		if(vfr.getParent() instanceof FunctionCall) {
			FunctionCall call = (FunctionCall) vfr.getParent();
			if(call.getTarget() == vfr) {
				return KeywordContext.METHOD_CALL;
			}
			else {
				return KeywordContext.ARGUMENT;
			}

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