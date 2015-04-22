package ca.ubc.ece.salt.sdjsb.test.ast;

import java.io.FileReader;
import java.io.IOException;

import org.junit.Test;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.ast.AstRoot;

import ca.ubc.ece.salt.sdjsb.ast.ConditionalPreProcessor;
import junit.framework.TestCase;

public class TestASTPreProcessing extends TestCase {
	
	protected void runTest(String file) throws IOException, CloneNotSupportedException {

		/* Parse the artifact with Rhino. */
		Parser parser = new Parser();
		AstRoot original = parser.parse(new FileReader(file), file, 1);
		
		/* Pre-process the AST. */
		ConditionalPreProcessor preProc = new ConditionalPreProcessor();
		preProc.process(original);

		/* Print the new AST. */
		System.out.println(original.toSource());
        
	}
	
	@Test
	public void testAstRoot() throws IOException, CloneNotSupportedException {
		
		String file = "./test/input/ast_preproc/var.js";
		this.runTest(file);

	}

	@Test
	public void testFunctionNode() throws IOException, CloneNotSupportedException {
		
		String file = "./test/input/ast/function.js";
		this.runTest(file);

	}

	@Test
	public void testInfixExpression() throws IOException, CloneNotSupportedException {
		
		String file = "./test/input/ast/infix.js";
		this.runTest(file);

	}

	@Test
	public void testReturnStatement() throws IOException, CloneNotSupportedException {
		
		String file = "./test/input/ast/return.js";
		this.runTest(file);

	}

	@Test
	public void testIfStatement() throws IOException, CloneNotSupportedException {
		
		String file = "./test/input/ast/if.js";
		this.runTest(file);

	}

	@Test
	public void testWhileLoop() throws IOException, CloneNotSupportedException {
		
		String file = "./test/input/ast/while.js";
		this.runTest(file);

	}

	@Test
	public void testDoLoop() throws IOException, CloneNotSupportedException {
		
		String file = "./test/input/ast/do.js";
		this.runTest(file);

	}

	@Test
	public void testForLoop() throws IOException, CloneNotSupportedException {
		
		String file = "./test/input/ast/for.js";
		this.runTest(file);

	}

	@Test
	public void testForInLoop() throws IOException, CloneNotSupportedException {
		
		String file = "./test/input/ast/forin.js";
		this.runTest(file);

	}

	@Test
	public void testFunctionCall() throws IOException, CloneNotSupportedException {
		
		String file = "./test/input/ast/call.js";
		this.runTest(file);

	}

	@Test
	public void testVariableDeclaration() throws IOException, CloneNotSupportedException {
		
		String file = "./test/input/ast/var.js";
		this.runTest(file);

	}

	@Test
	public void testUnaryExpression() throws IOException, CloneNotSupportedException {
		
		String file = "./test/input/ast/unary.js";
		this.runTest(file);

	}

	@Test
	public void testSwitchStatement() throws IOException, CloneNotSupportedException {
		
		String file = "./test/input/ast/switch.js";
		this.runTest(file);

	}

	@Test
	public void testParenthesizedExpression() throws IOException, CloneNotSupportedException {
		
		String file = "./test/input/ast/paren.js";
		this.runTest(file);

	}

	@Test
	public void testObjectLiteral() throws IOException, CloneNotSupportedException {
		
		String file = "./test/input/ast/obj.js";
		this.runTest(file);

	}

	@Test
	public void testElementGet() throws IOException, CloneNotSupportedException {
		
		String file = "./test/input/ast/elementget.js";
		this.runTest(file);

	}

	@Test
	public void testConditionalExpression() throws IOException, CloneNotSupportedException {
		
		String file = "./test/input/ast/conditional.js";
		this.runTest(file);

	}

	@Test
	public void testArrayLiteral() throws IOException, CloneNotSupportedException {
		
		String file = "./test/input/ast/array.js";
		this.runTest(file);

	}

	@Test
	public void testThrowStatement() throws IOException, CloneNotSupportedException {
		
		String file = "./test/input/ast/throw.js";
		this.runTest(file);

	}

	@Test
	public void testWithStatement() throws IOException, CloneNotSupportedException {
		
		String file = "./test/input/ast/with.js";
		this.runTest(file);

	}

	@Test
	public void testTryStatement() throws IOException, CloneNotSupportedException {
		
		String file = "./test/input/ast/try.js";
		this.runTest(file);

	}

}
