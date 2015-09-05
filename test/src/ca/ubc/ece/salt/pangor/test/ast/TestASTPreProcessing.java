package ca.ubc.ece.salt.pangor.test.ast;

import java.io.FileReader;
import java.io.IOException;

import org.junit.Test;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.ast.AstRoot;

import ca.ubc.ece.salt.sdjsb.ast.ConditionalPreProcessor;
import ca.ubc.ece.salt.sdjsb.ast.ShortCircuitPreProcessor;
import ca.ubc.ece.salt.sdjsb.ast.VarPreProcessor;
import junit.framework.TestCase;

public class TestASTPreProcessing extends TestCase {
	
	protected void runTest(String file) throws IOException, CloneNotSupportedException {

		/* Parse the artifact with Rhino. */
		Parser parser = new Parser();
		AstRoot original = parser.parse(new FileReader(file), file, 1);
		
		/* Pre-process the AST. */
		ConditionalPreProcessor preProc = new ConditionalPreProcessor();
		preProc.process(original);
		
		/* Expand variable initializers. */
		VarPreProcessor varPreProcessor = new VarPreProcessor();
		varPreProcessor.process(original);

        /* Expand the ternary operators. */
        ConditionalPreProcessor conditionalPreProcessor = new ConditionalPreProcessor();
        conditionalPreProcessor.process(original);

        /* Expand short circuit operators. */
        ShortCircuitPreProcessor shortCircuitPreProcessor = new ShortCircuitPreProcessor();
        shortCircuitPreProcessor.process(original);

		/* Print the new AST. */
//		System.out.println(original.toSource());
        
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

	@Test
	public void testIfCondition() throws IOException, CloneNotSupportedException {
		
		String file = "./test/input/ast_preproc/if.js";
		this.runTest(file);

	}

	@Test
	public void testReturn() throws IOException, CloneNotSupportedException {
		
		String file = "./test/input/ast_preproc/return.js";
		this.runTest(file);

	}

	@Test
	public void testThrow() throws IOException, CloneNotSupportedException {
		
		String file = "./test/input/ast_preproc/throw.js";
		this.runTest(file);

	}

	@Test
	public void testUndefinedConditional() throws IOException, CloneNotSupportedException {
		
		String file = "./test/input/ast_preproc/sth_undefined_conditional_new.js";
		this.runTest(file);

	}

	@Test
	public void testNestedReturn() throws IOException, CloneNotSupportedException {
		
		String file = "./test/input/ast_preproc/return_nested.js";
		this.runTest(file);

	}

	@Test
	public void testShortCircuitAnd() throws IOException, CloneNotSupportedException {
		
		String file = "./test/input/ast_preproc/short_circuit_and.js";
		this.runTest(file);

	}

	@Test
	public void testShortCircuitOr() throws IOException, CloneNotSupportedException {
		
		String file = "./test/input/ast_preproc/short_circuit_or.js";
		this.runTest(file);

	}

	@Test
	public void testShortCircuitNested() throws IOException, CloneNotSupportedException {
		
		String file = "./test/input/ast_preproc/short_circuit_nested.js";
		this.runTest(file);

	}

	@Test
	public void testVariableDeclarations() throws IOException, CloneNotSupportedException {
		
		String file = "./test/input/ast_preproc/variable_declarations.js";
		this.runTest(file);

	}

}
