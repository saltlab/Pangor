package ca.ubc.ece.salt.sdjsb.test.learning;

import org.junit.Assert;
import org.junit.Test;
import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.Assignment;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.Block;
import org.mozilla.javascript.ast.CatchClause;
import org.mozilla.javascript.ast.EmptyStatement;
import org.mozilla.javascript.ast.ExpressionStatement;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.IfStatement;
import org.mozilla.javascript.ast.InfixExpression;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.NumberLiteral;
import org.mozilla.javascript.ast.PropertyGet;
import org.mozilla.javascript.ast.StringLiteral;
import org.mozilla.javascript.ast.TryStatement;
import org.mozilla.javascript.ast.UnaryExpression;
import org.mozilla.javascript.ast.VariableDeclaration;
import org.mozilla.javascript.ast.VariableInitializer;

import ca.ubc.ece.salt.sdjsb.analysis.learning.apis.KeywordDefinition.KeywordType;
import ca.ubc.ece.salt.sdjsb.analysis.learning.ast.LearningUtilities;

public class TestLearningUtilitiesType {

	public void runTest(AstNode token, KeywordType expected) {
		KeywordType type = LearningUtilities.getTokenType(token);
		Assert.assertEquals("getTokenType returned an incorrect value.", expected, type);
	}

	@Test
	public void testClassTokenType() {

		Name name = new Name(0, "Bear");
		FunctionNode node = new FunctionNode(0, name);

		AstRoot root = new AstRoot();
		root.addChild(node);

		runTest(name, KeywordType.CLASS);

	}

	@Test
	public void testMethodNameTokenType() {

		Name name = new Name(0, "getName");
		FunctionNode node = new FunctionNode(0, name);

		AstRoot root = new AstRoot();
		root.addChild(node);

		runTest(name, KeywordType.METHOD);

	}

	@Test
	public void testKeywordTokenType() {

		Name right = new Name(0, "null");
		Name left = new Name(0, "a");

		Assignment assignment = new Assignment(left, right);

		AstRoot root = new AstRoot();
		root.addChild(assignment);

		runTest(right, KeywordType.RESERVED);

	}

	@Test
	public void testPackageTokenType() {

		StringLiteral pack = new StringLiteral();
		pack.setQuoteCharacter('"');
		pack.setValue("fs");

		FunctionCall call = new FunctionCall();
		call.setTarget(new Name(0, "require"));
		call.addArgument(pack);

		VariableInitializer initializer = new VariableInitializer();
		initializer.setTarget(new Name(0, "fs"));
		initializer.setInitializer(call);

		VariableDeclaration declaration = new VariableDeclaration();
		declaration.addVariable(initializer);

		ExpressionStatement statement = new ExpressionStatement(declaration);

		AstRoot root = new AstRoot();
		root.addChild(statement);

		runTest(pack, KeywordType.PACKAGE);

	}

	@Test
	public void testTypeOfType() {

		Name target = new Name(0, "user");
		UnaryExpression lhs = new UnaryExpression(Token.TYPEOF, 0, target);
		Name rhs = new Name(0, "undefined");

		InfixExpression condition = new InfixExpression(Token.SHEQ, lhs, rhs, 0);

		IfStatement ifs = new IfStatement();
		ifs.setCondition(condition);
		ifs.setThenPart(new EmptyStatement());

		AstRoot root = new AstRoot();
		root.addChild(ifs);

		System.out.println(root.toSource());

		runTest(lhs, KeywordType.RESERVED);

	}

	@Test
	public void testTypeOfInfixType() {

		Name target = new Name(0, "user");
		UnaryExpression lhs = new UnaryExpression(Token.TYPEOF, 0, target);
		Name rhs = new Name(0, "undefined");

		InfixExpression condition = new InfixExpression(Token.SHEQ, lhs, rhs, 0);

		IfStatement ifs = new IfStatement();
		ifs.setCondition(condition);
		ifs.setThenPart(new EmptyStatement());

		AstRoot root = new AstRoot();
		root.addChild(ifs);

		System.out.println(root.toSource());

		runTest(condition, KeywordType.RESERVED);

	}

	/*
	 * existsSync("/etc/init.d/httpd")
	 *
	 * expected: existsSync = METHOD
	 */
	@Test
	public void testMethodCallTokenType() {

		StringLiteral file = new StringLiteral();
		file.setQuoteCharacter('"');
		file.setValue("/etc/init.d/httpd");

		Name target = new Name(0, "existsSync");

		FunctionCall call = new FunctionCall();
		call.setTarget(target);
		call.addArgument(file);

		ExpressionStatement statement = new ExpressionStatement(call);

		AstRoot root = new AstRoot();
		root.addChild(statement);

		runTest(target, KeywordType.METHOD);

	}

	/*
	 * fs.existsSync("/etc/init.d/httpd")
	 *
	 * expected: fs = VARIABLE
	 * expected: existsSync = METHOD
	 */
	@Test
	public void testMethodCallFromObjectTokenType() {
		StringLiteral argument = new StringLiteral();
		argument.setQuoteCharacter('"');
		argument.setValue("/etc/init.d/httpd");

		Name object = new Name(0, "fs");
		Name method = new Name(0, "existsSync");

		/*
		 * fs.existsSync
		 */
		PropertyGet propertyGet = new PropertyGet();
		propertyGet.setTarget(object);
		propertyGet.setProperty(method);

		/*
		 * fs.existsSync("/etc/init.d/httpd");
		 */
		FunctionCall call = new FunctionCall();
		call.setTarget(propertyGet);
		call.addArgument(argument);

		ExpressionStatement statement = new ExpressionStatement(call);

		AstRoot root = new AstRoot();
		root.addChild(statement);

		runTest(object, KeywordType.VARIABLE);
		runTest(method, KeywordType.METHOD);
	}

	/*
	 * forever.config.set("foo")
	 *
	 * expected: forever = VARIABLE
	 * expected: config = FIELD
	 * expected: set = METHOD
	 */
	@Test
	public void testMethodCallFromFieldFromObjectTokenType() {
		StringLiteral argument = new StringLiteral();
		argument.setQuoteCharacter('"');
		argument.setValue("foo");

		Name object = new Name(0, "forever");
		Name field = new Name(0, "config");
		Name method = new Name(0, "set");

		/*
		 * forever.config
		 */
		PropertyGet innerPropertyGet = new PropertyGet();
		innerPropertyGet.setTarget(object);
		innerPropertyGet.setProperty(field);

		/*
		 * forever.config.set
		 */
		PropertyGet outterPropertyGet = new PropertyGet();
		outterPropertyGet.setTarget(innerPropertyGet);
		outterPropertyGet.setProperty(method);

		/*
		 * forever.config.set("foo")
		 */
		FunctionCall call = new FunctionCall();
		call.setTarget(outterPropertyGet);
		call.addArgument(argument);

		ExpressionStatement statement = new ExpressionStatement(call);

		AstRoot root = new AstRoot();
		root.addChild(statement);

		runTest(object, KeywordType.VARIABLE);
		runTest(field, KeywordType.FIELD);
		runTest(method, KeywordType.METHOD);
	}

	@Test
	public void testFieldTokenType() {

		Name target = new Name(0, "path");
		Name field = new Name(0, "delimiter");

		PropertyGet access = new PropertyGet(target, field);

		VariableInitializer initializer = new VariableInitializer();
		initializer.setTarget(new Name(0, "delim"));
		initializer.setInitializer(access);

		VariableDeclaration declaration = new VariableDeclaration();
		declaration.addVariable(initializer);

		ExpressionStatement statement = new ExpressionStatement(declaration);

		AstRoot root = new AstRoot();
		root.addChild(statement);

		runTest(field, KeywordType.FIELD);

	}

	@Test
	public void testConstantTokenType() {

		Name target = new Name(0, "buffer");
		Name field = new Name(0, "INSPECT_MAX_BYTES");

		PropertyGet access = new PropertyGet(target, field);

		VariableInitializer initializer = new VariableInitializer();
		initializer.setTarget(new Name(0, "max"));
		initializer.setInitializer(access);

		VariableDeclaration declaration = new VariableDeclaration();
		declaration.addVariable(initializer);

		ExpressionStatement statement = new ExpressionStatement(declaration);

		AstRoot root = new AstRoot();
		root.addChild(statement);

		runTest(field, KeywordType.CONSTANT);

	}

	@Test
	public void testArgumentTokenType() {

		StringLiteral file = new StringLiteral();
		file.setQuoteCharacter('"');
		file.setValue("/etc/init.d/httpd");

		Name target = new Name(0, "existsSync");

		FunctionCall call = new FunctionCall();
		call.setTarget(target);
		call.addArgument(file);

		ExpressionStatement statement = new ExpressionStatement(call);

		AstRoot root = new AstRoot();
		root.addChild(statement);

		runTest(file, KeywordType.UNKNOWN);

	}

	@Test
	public void testParameterTokenType() {

		StringLiteral file = new StringLiteral();
		file.setQuoteCharacter('"');
		file.setValue("/etc/init.d/httpd");

		NumberLiteral num = new NumberLiteral(5.0);

		Name var = new Name(0, "x");
		Name name = new Name(0, "open");

		FunctionNode function = new FunctionNode(0, name);
		function.addParam(file);
		function.addParam(num);
		function.addParam(var);
		function.setBody(new Block());

		AstRoot root = new AstRoot();
		root.addChild(function);

		runTest(file, KeywordType.PARAMETER);
		runTest(num, KeywordType.PARAMETER);
		runTest(var, KeywordType.PARAMETER);

	}

	@Test
	public void testExceptionTokenType() {

		Name exception = new Name(0, "err");

		CatchClause catchClause = new CatchClause();
		catchClause.setBody(new Block());
		catchClause.setVarName(exception);

		TryStatement tryStatement = new TryStatement();
		tryStatement.setTryBlock(new Block());
		tryStatement.addCatchClause(catchClause);

		AstRoot root = new AstRoot();
		root.addChild(tryStatement);

		runTest(exception, KeywordType.EXCEPTION);

	}

	@Test
	public void testEventTokenType() {

		/* Register the event listener. */

		StringLiteral registerEvent = new StringLiteral();
		registerEvent.setQuoteCharacter('\'');
		registerEvent.setValue("open");

		Name registerObject = new Name(0, "frontDoor");
		Name on = new Name(0, "on");
		Name registerAction = new Name(0, "ring");
		PropertyGet registerTarget = new PropertyGet();
		registerTarget.setTarget(registerObject);
		registerTarget.setProperty(on);

		FunctionCall registerCall = new FunctionCall();
		registerCall.setTarget(registerTarget);
		registerCall.addArgument(registerEvent);
		registerCall.addArgument(registerAction);

		ExpressionStatement registerStatement = new ExpressionStatement(registerCall);

		/* Remove the event listener. */

		StringLiteral removeListenerEvent = new StringLiteral();
		removeListenerEvent.setQuoteCharacter('\'');
		removeListenerEvent.setValue("open");

		StringLiteral removeEvent = new StringLiteral();
		removeEvent.setQuoteCharacter('\'');
		removeEvent.setValue("open");

		Name removeObject = new Name(0, "frontDoor");
		Name removeListener = new Name(0, "removeListener");
		Name removeAction = new Name(0, "ring");
		PropertyGet removeTarget = new PropertyGet();
		removeTarget.setTarget(removeObject);
		removeTarget.setProperty(removeListener);

		FunctionCall removeCall = new FunctionCall();
		removeCall.setTarget(removeTarget);
		removeCall.addArgument(removeListenerEvent);
		removeCall.addArgument(removeAction);

		ExpressionStatement removeStatement = new ExpressionStatement(removeCall);

		/* Remove all event listeners. */

		StringLiteral removeAllListenerEvent = new StringLiteral();
		removeAllListenerEvent.setQuoteCharacter('\'');
		removeAllListenerEvent.setValue("open");

		StringLiteral removeAllEvent = new StringLiteral();
		removeAllEvent.setQuoteCharacter('\'');
		removeAllEvent.setValue("open");

		Name removeAllObject = new Name(0, "frontDoor");
		Name removeAllListener = new Name(0, "removeAllListeners");
		PropertyGet removeAllTarget = new PropertyGet();
		removeAllTarget.setTarget(removeAllObject);
		removeAllTarget.setProperty(removeAllListener);

		FunctionCall removeAllCall = new FunctionCall();
		removeAllCall.setTarget(removeAllTarget);
		removeAllCall.addArgument(removeAllListenerEvent);

		ExpressionStatement removeAllStatement = new ExpressionStatement(removeAllCall);

		/* Add the call to the script. */

		AstRoot root = new AstRoot();
		root.addChild(registerStatement);
		root.addChild(removeStatement);
		root.addChild(removeAllStatement);

		System.out.println(root.toSource());
		runTest(registerEvent, KeywordType.EVENT);
		runTest(removeListenerEvent, KeywordType.EVENT);
		runTest(removeAllListenerEvent, KeywordType.EVENT);

	}

}