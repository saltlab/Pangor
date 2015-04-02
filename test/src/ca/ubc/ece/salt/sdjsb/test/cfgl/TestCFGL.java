package ca.ubc.ece.salt.sdjsb.test.cfgl;

import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.ast.AstRoot;

import ca.ubc.ece.salt.sdjsb.cfgl.CFG;
import ca.ubc.ece.salt.sdjsb.cfgl.CFGFactory;
import ca.ubc.ece.salt.sdjsb.cfgl.CFGNode;
import ca.ubc.ece.salt.sdjsb.cfgl.CFGPrinter;
import junit.framework.TestCase;

public class TestCFGL extends TestCase {
	
	protected void runTest(String file, List<String> expectedCFGs, boolean printCFGs) throws IOException {

		/* Parse the artifact with Rhino. */
		Parser parser = new Parser();
		AstRoot root = parser.parse(new FileReader(file), file, 1);
		
		/* Create the CFG. */
		List<CFG> cfgs = CFGFactory.createCFGs(root);

		/* Reset the CFGNode id generator value (needed for consistent test cases). */
		CFGNode.resetIdGen();

		/* Get the serialized CFGs. */
		List<String> actualCFGs = new LinkedList<String>();
        int n = 1;
        for(CFG cfg : cfgs) {
            String serialized = CFGPrinter.adjacencyList(cfg);
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
	public void testIf() throws IOException {
		
		String file = "./test/input/cfg/if.js";
		
		List<String> expectedCFGs = new LinkedList<String>();
		expectedCFGs.add("SCRIPT_ENTRY(0){2},VAR(2){3},EXPR_RESULT(3){1},SCRIPT_EXIT(1){}");
		expectedCFGs.add("FUNCTION_ENTRY(4){6},EXPR_VOID(6){7},IF(7){true:8,false:9},EXPR_VOID(8){5},EXPR_VOID(9){10},FUNCTION_EXIT(5){},EXPR_VOID(10){5}");
		
		this.runTest(file, expectedCFGs, true);

	}

	@Test
	public void testNestedIf() throws IOException {
		
		String file = "./test/input/cfg/basic.js";
		
		List<String> expectedCFGs = new LinkedList<String>();
		expectedCFGs.add("SCRIPT_ENTRY(0){2},VAR(2){3},EXPR_RESULT(3){1},SCRIPT_EXIT(1){}");
		expectedCFGs.add("FUNCTION_ENTRY(4){6},EXPR_VOID(6){7},IF(7){true:8,false:9},EXPR_VOID(8){5},EXPR_VOID(9){10},FUNCTION_EXIT(5){},IF(10){true:11,false:12},EXPR_VOID(11){5},EMPTY(12){5}");
		expectedCFGs.add("FUNCTION_ENTRY(13){15},EXPR_VOID(15){14},FUNCTION_EXIT(14){}");
		
		this.runTest(file, expectedCFGs, true);

	}

	@Test
	public void testWhile() throws IOException {
		
		String file = "./test/input/cfg/whileloop.js";
		
		List<String> expectedCFGs = new LinkedList<String>();
		expectedCFGs.add("SCRIPT_ENTRY(0){2},VAR(2){3},EXPR_RESULT(3){1},SCRIPT_EXIT(1){}");
		expectedCFGs.add("FUNCTION_ENTRY(4){6},EXPR_VOID(6){7},VAR(7){8},WHILE(8){true:9,false:12},EXPR_VOID(9){10},EMPTY(12){5},EXPR_VOID(10){11},FUNCTION_EXIT(5){},EXPR_VOID(11){8}");
		expectedCFGs.add("FUNCTION_ENTRY(13){15},EXPR_VOID(15){14},FUNCTION_EXIT(14){}");
		
		this.runTest(file, expectedCFGs, true);

	}

	@Test
	public void testWhileBreak() throws IOException {
		
		String file = "./test/input/cfg/whilebreak.js";
		
		List<String> expectedCFGs = new LinkedList<String>();
		expectedCFGs.add("SCRIPT_ENTRY(0){2},VAR(2){3},EXPR_RESULT(3){1},SCRIPT_EXIT(1){}");
		expectedCFGs.add("FUNCTION_ENTRY(4){6},EXPR_VOID(6){7},VAR(7){8},WHILE(8){true:9,false:15},EXPR_VOID(9){10},EMPTY(15){5},IF(10){true:11,false:12},FUNCTION_EXIT(5){},BREAK(11){5},EMPTY(12){13},EXPR_VOID(13){14},EXPR_VOID(14){8}");
		expectedCFGs.add("FUNCTION_ENTRY(16){18},EXPR_VOID(18){17},FUNCTION_EXIT(17){}");
		
		this.runTest(file, expectedCFGs, true);

	}

	@Test
	public void testWhileContinue() throws IOException {
		
		String file = "./test/input/cfg/whilecontinue.js";
		
		List<String> expectedCFGs = new LinkedList<String>();
		expectedCFGs.add("SCRIPT_ENTRY(0){2},VAR(2){3},EXPR_RESULT(3){1},SCRIPT_EXIT(1){}");
		expectedCFGs.add("FUNCTION_ENTRY(4){6},EXPR_VOID(6){7},VAR(7){8},WHILE(8){true:9,false:16},EXPR_VOID(9){10},EMPTY(16){5},IF(10){true:11,false:13},FUNCTION_EXIT(5){},EXPR_VOID(11){12},EMPTY(13){14},CONTINUE(12){8},EXPR_VOID(14){15},EXPR_VOID(15){8}");
		expectedCFGs.add("FUNCTION_ENTRY(17){19},EXPR_VOID(19){18},FUNCTION_EXIT(18){}");
		
		this.runTest(file, expectedCFGs, true);

	}

	@Test
	public void testReturn() throws IOException {
		
		String file = "./test/input/cfg/return.js";
		
		List<String> expectedCFGs = new LinkedList<String>();
		expectedCFGs.add("SCRIPT_ENTRY(0){2},VAR(2){3},EXPR_RESULT(3){1},SCRIPT_EXIT(1){}");
		expectedCFGs.add("FUNCTION_ENTRY(4){6},EXPR_VOID(6){7},VAR(7){8},WHILE(8){true:9,false:15},EXPR_VOID(9){10},EMPTY(15){5},IF(10){true:11,false:12},FUNCTION_EXIT(5){},RETURN(11){5},EMPTY(12){13},EXPR_VOID(13){14},EXPR_VOID(14){8}");
		expectedCFGs.add("FUNCTION_ENTRY(16){18},EXPR_VOID(18){17},FUNCTION_EXIT(17){}");
		
		this.runTest(file, expectedCFGs, true);

	}

}
