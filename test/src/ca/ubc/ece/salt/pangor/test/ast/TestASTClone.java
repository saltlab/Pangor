package ca.ubc.ece.salt.pangor.test.ast;

import java.util.Iterator;
import java.util.List;
import java.io.FileReader;
import java.io.IOException;

import org.junit.Test;
import org.mozilla.javascript.Node;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;

import junit.framework.TestCase;

public class TestASTClone extends TestCase {
	
	protected void runTest(String file) throws IOException, CloneNotSupportedException {

		/* Parse the artifact with Rhino. */
		Parser parser = new Parser();
		AstRoot original = parser.parse(new FileReader(file), file, 1);
		
		/* Clone the AST. */
		AstRoot clone = (AstRoot)original.clone(original.getParent());

		/* Get the serialized ASTs. */
//		String expected = TestASTClone.serialize(original);
//		String actual = TestASTClone.serialize(clone);
		
//		System.out.println(expected);
//		System.out.println(actual);
        
        /* Check the AST. */
        check(clone, original);

	}
	
	protected static String serialize(AstNode node) {
		
		String s = "{" + node.getTypeName() + ":";
		
		for(Node nodeChild : node) {

			AstNode child = (AstNode)nodeChild;
			s += child.getTypeName() + ",";
			
		}
		
		return s + "}";
		
	}
	
	/** 
	 * Recursively check the AST. 
	 */
	protected void check(AstNode clone, AstNode original) {
		
		if(original == null && clone == null) return;
		
		String expected = original.toSource();
		String actual = clone.toSource();
		
//		System.out.println("Original: " + original.toSource());
		
		TestCase.assertEquals("the source code does not match", expected.replace("\n", ""), actual.replace("\n", ""));

		TestCase.assertFalse("\"" + clone.toSource() + "\" points to the same object in the original and clone", clone == original);
		
		/* Check the child nodes. */
		List<AstNode> originalChildren = AstNodeTraverse.getChildrenOf(original);
		List<AstNode> cloneChildren = AstNodeTraverse.getChildrenOf(clone);
		
		TestCase.assertEquals("the nodes should have the same number of children", cloneChildren.size(), originalChildren.size());
		
		Iterator<AstNode> oIterator = originalChildren.iterator();
		Iterator<AstNode> cIterator = cloneChildren.iterator();
		
		while(oIterator.hasNext()) {

			assert(cIterator.hasNext());
			
			AstNode oChild = oIterator.next();
			AstNode cChild = cIterator.next();
			
			this.check(cChild, oChild);
			
		}

	}

	@Test
	public void testAstRoot() throws IOException, CloneNotSupportedException {
		
		String file = "./test/input/ast/root.js";
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

	@Test
	public void testUndefinedConditional() throws IOException, CloneNotSupportedException {
		
		String file = "./test/input/ast/sth_undefined_conditional_new.js";
		this.runTest(file);

	}

}
