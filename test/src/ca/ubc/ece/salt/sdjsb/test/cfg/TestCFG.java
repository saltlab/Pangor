package ca.ubc.ece.salt.sdjsb.test.cfg;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.mozilla.javascript.IRFactory;
import org.mozilla.javascript.Node;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.NodeVisitor;
import org.mozilla.javascript.ast.ScriptNode;

import ca.ubc.ece.salt.sdjsb.cfg.CFG;
import ca.ubc.ece.salt.sdjsb.cfg.CFGFactory;
import junit.framework.TestCase;

public class TestCFG extends TestCase {
	
	protected void runTest(String file, List<String> expectedCFGs, boolean printCFGs) throws IOException {

		/* Parse the artifact with Rhino. */
		Parser parser = new Parser();
		AstRoot root = parser.parse(new FileReader(file), file, 1);
		
		/* Create the CFG. */
		List<CFG> cfgs = CFGFactory.createCFGs(root);

		/* Get the serialized CFGs. */
		List<String> actualCFGs = new LinkedList<String>();
        int n = 1;
        for(CFG cfg : cfgs) {
            String serialized = cfg.getEntryNode().printSubGraph(null);
            actualCFGs.add(serialized);
            if(printCFGs) System.out.println("CFG" + n + ": " + serialized);
            n++;
        }
        
        /* Check the CFGs. */
        check(actualCFGs, expectedCFGs);

	}
	
	protected void check(List<String> actualCFGs, List<String> expectedCFGs) {
		
		/* Check the CFGs. */
		for(String expectedCFG : expectedCFGs) {
			int index = actualCFGs.indexOf(expectedCFG);
            TestCase.assertTrue("a CFG was not produced correctly", index >= 0);
		}
		
		/* Check that no additional CFGs were produced. */
		TestCase.assertEquals("an unexpected CFG was produced", actualCFGs.size(), expectedCFGs.size());

	}

	@Test
	public void testNestedIf() throws IOException {
		
		String file = "./test/input/cfg/basic.js";
		
		List<String> expectedCFGs = new LinkedList<String>();
		expectedCFGs.add("SCRIPT ENTRY->VAR->EXPR_RESULT->SCRIPT EXIT");
		expectedCFGs.add("FUNCTION ENTRY->EXPR_VOID->NAME?{EXPR_VOID:EXPR_VOID->TRUE?{EXPR_VOID}}->FUNCTION EXIT");
		expectedCFGs.add("FUNCTION ENTRY->EXPR_VOID->FUNCTION EXIT");
		
		this.runTest(file, expectedCFGs, true);

	}

	@Test
	public void testWhile() throws IOException {
		
		String file = "./test/input/cfg/whileloop.js";
		
		List<String> expectedCFGs = new LinkedList<String>();
		expectedCFGs.add("SCRIPT ENTRY->VAR->EXPR_RESULT->SCRIPT EXIT");
		expectedCFGs.add("FUNCTION ENTRY->EXPR_VOID->VAR->LT?{EXPR_VOID->EXPR_VOID->EXPR_VOID}->FUNCTION EXIT");
		expectedCFGs.add("FUNCTION ENTRY->EXPR_VOID->FUNCTION EXIT");
		
		this.runTest(file, expectedCFGs, true);

	}

	@Test
	public void testWhileBreak() throws IOException {
		
		String file = "./test/input/cfg/whilebreak.js";
		
		List<String> expectedCFGs = new LinkedList<String>();
		expectedCFGs.add("SCRIPT ENTRY->VAR->EXPR_RESULT->SCRIPT EXIT");
		expectedCFGs.add("FUNCTION ENTRY->EXPR_VOID->VAR->LT?{EXPR_VOID->SHEQ?{BREAK(FUNCTION EXIT)}->EXPR_VOID->EXPR_VOID}->FUNCTION EXIT");
		expectedCFGs.add("FUNCTION ENTRY->EXPR_VOID->FUNCTION EXIT");
		
		this.runTest(file, expectedCFGs, true);

	}

	@Test
	public void testWhileContinue() throws IOException {
		
		String file = "./test/input/cfg/whilecontinue.js";
		
		List<String> expectedCFGs = new LinkedList<String>();
		expectedCFGs.add("SCRIPT ENTRY->VAR->EXPR_RESULT->SCRIPT EXIT");
		expectedCFGs.add("FUNCTION ENTRY->EXPR_VOID->VAR->LT?{EXPR_VOID->SHEQ?{EXPR_VOID->CONTINUE(LT)}->EXPR_VOID->EXPR_VOID}->FUNCTION EXIT");
		expectedCFGs.add("FUNCTION ENTRY->EXPR_VOID->FUNCTION EXIT");
		
		this.runTest(file, expectedCFGs, true);

	}

	@Test
	public void testReturn() throws IOException {
		
		String file = "./test/input/cfg/return.js";
		
		List<String> expectedCFGs = new LinkedList<String>();
		expectedCFGs.add("SCRIPT ENTRY->VAR->EXPR_RESULT->SCRIPT EXIT");
		expectedCFGs.add("FUNCTION ENTRY->EXPR_VOID->VAR->LT?{EXPR_VOID->SHEQ?{RETURN(FUNCTION EXIT)}->EXPR_VOID->EXPR_VOID}->FUNCTION EXIT");
		expectedCFGs.add("FUNCTION ENTRY->EXPR_VOID->FUNCTION EXIT");
		
		this.runTest(file, expectedCFGs, true);

	}

	@Test
	public void testFor() throws IOException {
		
		String file = "./test/input/cfg/forloop.js";
		
		List<String> expectedCFGs = new LinkedList<String>();
		expectedCFGs.add("SCRIPT ENTRY->VAR->EXPR_RESULT->SCRIPT EXIT");
		expectedCFGs.add("FUNCTION ENTRY->EXPR_VOID->VAR->VAR->LT?{EXPR_VOID->EXPR_VOID->EXPR_VOID->INC}->FUNCTION EXIT");
		expectedCFGs.add("FUNCTION ENTRY->EXPR_VOID->FUNCTION EXIT");
		
		this.runTest(file, expectedCFGs, true);

	}

	@Test
	public void testDo() throws IOException {
		
		String file = "./test/input/cfg/doloop.js";
		
		List<String> expectedCFGs = new LinkedList<String>();
		expectedCFGs.add("SCRIPT ENTRY->VAR->EXPR_RESULT->SCRIPT EXIT");
		expectedCFGs.add("FUNCTION ENTRY->EXPR_VOID->VAR->{EXPR_VOID->EXPR_VOID->EXPR_VOID}?LT->FUNCTION EXIT");
		expectedCFGs.add("FUNCTION ENTRY->EXPR_VOID->FUNCTION EXIT");
		
		this.runTest(file, expectedCFGs, true);

	}

	@Test
	public void testForIn() throws IOException {
		
		String file = "./test/input/cfg/forinloop.js";
		
		List<String> expectedCFGs = new LinkedList<String>();
		expectedCFGs.add("SCRIPT ENTRY->VAR->EXPR_RESULT->SCRIPT EXIT");
		expectedCFGs.add("FUNCTION ENTRY->EXPR_VOID->VAR->VAR->CALL?{ASSIGN->EXPR_VOID->EXPR_VOID}->FUNCTION EXIT");
		expectedCFGs.add("FUNCTION ENTRY->EXPR_VOID->FUNCTION EXIT");
		
		this.runTest(file, expectedCFGs, true);

	}

	@Test
	public void testSwitch() throws IOException {
		
		String file = "./test/input/cfg/switch.js";
		
		List<String> expectedCFGs = new LinkedList<String>();
		expectedCFGs.add("SCRIPT ENTRY->VAR->EXPR_RESULT->SCRIPT EXIT");
		expectedCFGs.add("FUNCTION ENTRY->EXPR_VOID->VAR->CALL?{ASSIGN->EXPR_VOID}->FUNCTION EXIT");
		expectedCFGs.add("FUNCTION ENTRY->VAR->CASE{cow:EXPR_VOID->BREAK(EXPR_VOID)->moose:EXPR_VOID->BREAK(EXPR_VOID)->horse:EXPR_VOID->BREAK(EXPR_VOID)->buffalo:EMPTY->default:EXPR_VOID}->EXPR_VOID->FUNCTION EXIT");
		
		this.runTest(file, expectedCFGs, true);

	}

	@Test
	public void testWith() throws IOException {
		
		String file = "./test/input/cfg/with.js";
		
		List<String> expectedCFGs = new LinkedList<String>();
		expectedCFGs.add("SCRIPT ENTRY->EXPR_RESULT->SCRIPT EXIT");
		expectedCFGs.add("FUNCTION ENTRY->WITH(NAME){EXPR_VOID}->FUNCTION EXIT");
		
		this.runTest(file, expectedCFGs, true);

	}

	@Test
	public void testTryCatch() throws IOException {
		
		String file = "./test/input/cfg/trycatch.js";
		
		List<String> expectedCFGs = new LinkedList<String>();
		expectedCFGs.add("SCRIPT ENTRY->VAR->TRY?{EXPR_RESULT->SHEQ?{THROW}->EXPR_RESULT,catch:EXPR_RESULT,finally:EXPR_RESULT}->SCRIPT EXIT");
		
		this.runTest(file, expectedCFGs, true);

	}
	
}
