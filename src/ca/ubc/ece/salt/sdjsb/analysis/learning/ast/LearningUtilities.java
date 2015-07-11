package ca.ubc.ece.salt.sdjsb.analysis.learning.ast;

import java.util.Arrays;
import java.util.List;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.CatchClause;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.PropertyGet;
import org.mozilla.javascript.ast.StringLiteral;

import ca.ubc.ece.salt.sdjsb.analysis.learning.apis.Keyword.KeywordType;

/**
 * Provides AST utilities to help collect feature information (i.e., keyword 
 * info) from the source code analysis.
 */
public class LearningUtilities {
	
	/**
	 * Gets the context under which the token is being used (e.g., as an 
	 * argument, parameter, exception, etc. see @code{Keyword}.
	 */
	public static KeywordType getTokenContext(AstNode token) {
		
		/* If the token is a reserved word, we can return that context. */
		if(isJavaScriptReserved(token)) return KeywordType.RESERVED;
		
		/* Look at the token's parent to determine the context in which it is 
		 * used. */
		
		KeywordType type = KeywordType.UNKNOWN;
		type = contextSwitch(token.getParent(), token);
		
		return type;

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
	 * Switches on the ancestor of an AstNode to determine the keyword context.
	 */
	private static KeywordType contextSwitch(AstNode ancestor, AstNode token) {
		
		if(ancestor == null || token == null) return KeywordType.UNKNOWN;
		
		if (ancestor instanceof FunctionNode) {

			FunctionNode function = (FunctionNode) ancestor;
			
			/* Is this a class declaration? */
			if(function.getFunctionName() == token && Character.isUpperCase(function.getName().charAt(0))) {
				return KeywordType.CLASS;
			}
			else if(function.getFunctionName() == token) {
				return KeywordType.METHOD_NAME;
			}
			else if(function.getParams().contains(token)) {
				return KeywordType.PARAMETER;
			}

		} 
		else if(ancestor instanceof FunctionCall) {
			
			FunctionCall call = (FunctionCall) ancestor;

			if(call.getTarget() instanceof Name) {

				Name target = (Name)call.getTarget();
				if(target == token) {
					return KeywordType.METHOD_CALL;
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
				return KeywordType.ARGUMENT;
			}

		}
		else if(ancestor instanceof PropertyGet) {
			
			/* Should we differentiate between field accesses and field 
			 * assignments? Currently we do not. */
			PropertyGet access = (PropertyGet) ancestor;
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
		else if(ancestor instanceof CatchClause) {
			
			CatchClause catchClause = (CatchClause) ancestor;
			if(catchClause.getVarName() == token) {
				return KeywordType.EXCEPTION;
			}
			
		}
		
		return KeywordType.UNKNOWN;

	}

}