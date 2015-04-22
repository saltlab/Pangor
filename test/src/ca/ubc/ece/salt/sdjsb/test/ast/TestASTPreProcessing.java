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
	public void testVar() throws IOException, CloneNotSupportedException {
		
		String file = "./test/input/ast_preproc/var.js";
		this.runTest(file);

	}

	@Test
	public void testNestedVar() throws IOException, CloneNotSupportedException {
		
		String file = "./test/input/ast_preproc/var_nested.js";
		this.runTest(file);

	}

	@Test
	public void testInfixExpression() throws IOException, CloneNotSupportedException {
		
		String file = "./test/input/ast_preproc/infix.js";
		this.runTest(file);

	}

	@Test
	public void testNestedInfixExpression() throws IOException, CloneNotSupportedException {
		
		String file = "./test/input/ast_preproc/infix_nested.js";
		this.runTest(file);

	}

	@Test
	public void testStatement() throws IOException, CloneNotSupportedException {
		
		String file = "./test/input/ast_preproc/statement.js";
		this.runTest(file);

	}

	@Test
	public void testFunction() throws IOException, CloneNotSupportedException {
		
		String file = "./test/input/ast_preproc/function.js";
		this.runTest(file);

	}

	@Test
	public void testIfThenElse() throws IOException, CloneNotSupportedException {
		
		String file = "./test/input/ast_preproc/ifthen.js";
		this.runTest(file);

	}

	@Test
	public void testWhileBody() throws IOException, CloneNotSupportedException {
		
		String file = "./test/input/ast_preproc/whilebody.js";
		this.runTest(file);

	}

}
