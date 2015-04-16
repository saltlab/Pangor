package ca.ubc.ece.salt.sdjsb.checker.doesnotexist;

import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.ArrayUtils;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.ConditionalExpression;
import org.mozilla.javascript.ast.DoLoop;
import org.mozilla.javascript.ast.ForLoop;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.IfStatement;
import org.mozilla.javascript.ast.InfixExpression;
import org.mozilla.javascript.ast.KeywordLiteral;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.NumberLiteral;
import org.mozilla.javascript.ast.StringLiteral;
import org.mozilla.javascript.ast.WhileLoop;

import ca.ubc.ece.salt.sdjsb.checker.CheckerContext;
import ca.ubc.ece.salt.sdjsb.checker.CheckerUtilities;

public class DoesNotExistCheckerUtilities {

	/**
	 * Determines the type of the simple name node. A simple name could be a
	 * variable, field, method or keyword.
	 * @param name
	 * @return
	 */
	public static NameType getNameType(Name name) {

		if(name.getParent() instanceof FunctionCall) {
			FunctionCall parent = (FunctionCall) name.getParent();

			if(parent.getParent() instanceof InfixExpression && CheckerUtilities.isIdentifierOperator(((InfixExpression) parent.getParent()).getOperator())) {
				return NameType.METHOD;
			}

			return NameType.FUNCTION;
		}

		if(name.getParent() instanceof InfixExpression && CheckerUtilities.isIdentifierOperator(((InfixExpression) name.getParent()).getOperator())) {
			return NameType.FIELD;
		}

		return NameType.VARIABLE;

	}

	public enum NameType {
		UNKNOWN,
		VARIABLE,
		FIELD,
		FUNCTION,
		METHOD
	}
    
}
