package ca.ubc.ece.salt.pangor.test.cfg;

import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Ignore;
import org.junit.Test;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.ast.AstRoot;

import ca.ubc.ece.salt.pangor.cfg.CFG;
import ca.ubc.ece.salt.pangor.cfg.CFGNode;
import ca.ubc.ece.salt.pangor.cfg.CFGPrinter;
import ca.ubc.ece.salt.pangor.cfg.CFGPrinter.Output;
import ca.ubc.ece.salt.pangor.js.cfg.JavaScriptCFGFactory;

@Ignore
public class TestCFG extends TestCase {

	protected void runTest(String file, List<String> expectedCFGs, Output output) throws IOException {

		/* Parse the artifact with Rhino. */
		Parser parser = new Parser();
		AstRoot root = parser.parse(new FileReader(file), file, 1);

		/* Create the CFG. */
		List<CFG> cfgs = new JavaScriptCFGFactory().createCFGs(root);

		/* Reset the CFGNode id generator value (needed for consistent test cases). */
		CFGNode.resetIdGen();

		/* Get the serialized CFGs. */
		List<String> actualCFGs = new LinkedList<String>();
        int n = 1;
        for(CFG cfg : cfgs) {
            String serialized = CFGPrinter.adjacencyList(cfg);
            actualCFGs.add(serialized);
            if(output != Output.NONE) System.out.println("CFG" + n + ": " + CFGPrinter.print(output, cfg));
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

	@Ignore @Test
	public void testIf() throws IOException {

		String file = "./test/input/cfg/if.js";

		List<String> expectedCFGs = new LinkedList<String>();
		expectedCFGs.add("SCRIPT_ENTRY(0){2},VAR(2){3},EXPR_RESULT(3){1},SCRIPT_EXIT(1){}");
		expectedCFGs.add("FUNCTION_ENTRY(4){6},EXPR_VOID(6){7},IF(7){name:8,!(name):9},EXPR_VOID(8){5},EXPR_VOID(9){10},FUNCTION_EXIT(5){},EXPR_VOID(10){5}");

		this.runTest(file, expectedCFGs, Output.NONE);

	}

	@Ignore @Test
	public void testNestedIf() throws IOException {

		String file = "./test/input/cfg/basic.js";

		List<String> expectedCFGs = new LinkedList<String>();
		expectedCFGs.add("SCRIPT_ENTRY(0){2},VAR(2){3},EXPR_RESULT(3){1},SCRIPT_EXIT(1){}");
		expectedCFGs.add("FUNCTION_ENTRY(4){6},EXPR_VOID(6){7},IF(7){name:8,!(name):9},EXPR_VOID(8){5},EXPR_VOID(9){10},FUNCTION_EXIT(5){},IF(10){true:11,!(true):12},EXPR_VOID(11){5},EMPTY(12){5}");
		expectedCFGs.add("FUNCTION_ENTRY(13){15},EXPR_VOID(15){14},FUNCTION_EXIT(14){}");

		this.runTest(file, expectedCFGs, Output.NONE);

	}

	@Ignore @Test
	public void testWhile() throws IOException {

		String file = "./test/input/cfg/whileloop.js";

		List<String> expectedCFGs = new LinkedList<String>();
		expectedCFGs.add("SCRIPT_ENTRY(0){2},VAR(2){3},EXPR_RESULT(3){1},SCRIPT_EXIT(1){}");
		expectedCFGs.add("FUNCTION_ENTRY(4){6},EXPR_VOID(6){7},VAR(7){8},WHILE(8){i < 10:9,!(i < 10):12},EXPR_VOID(9){10},EMPTY(12){5},EXPR_VOID(10){11},FUNCTION_EXIT(5){},EXPR_VOID(11){8}");
		expectedCFGs.add("FUNCTION_ENTRY(13){15},EXPR_VOID(15){14},FUNCTION_EXIT(14){}");

		this.runTest(file, expectedCFGs, Output.NONE);

	}

	@Ignore @Test
	public void testWhileBreak() throws IOException {

		String file = "./test/input/cfg/whilebreak.js";

		List<String> expectedCFGs = new LinkedList<String>();
		expectedCFGs.add("SCRIPT_ENTRY(0){2},VAR(2){3},EXPR_RESULT(3){1},SCRIPT_EXIT(1){}");
		expectedCFGs.add("FUNCTION_ENTRY(4){6},EXPR_VOID(6){7},VAR(7){8},WHILE(8){i < 10:9,!(i < 10):15},EXPR_VOID(9){10},EMPTY(15){5},IF(10){i === 5:11,!(i === 5):12},FUNCTION_EXIT(5){},BREAK(11){5},EMPTY(12){13},EXPR_VOID(13){14},EXPR_VOID(14){8}");
		expectedCFGs.add("FUNCTION_ENTRY(16){18},EXPR_VOID(18){17},FUNCTION_EXIT(17){}");

		this.runTest(file, expectedCFGs, Output.NONE);

	}

	@Ignore @Test
	public void testWhileContinue() throws IOException {

		String file = "./test/input/cfg/whilecontinue.js";

		List<String> expectedCFGs = new LinkedList<String>();
		expectedCFGs.add("SCRIPT_ENTRY(0){2},VAR(2){3},EXPR_RESULT(3){1},SCRIPT_EXIT(1){}");
		expectedCFGs.add("FUNCTION_ENTRY(4){6},EXPR_VOID(6){7},VAR(7){8},WHILE(8){i < 10:9,!(i < 10):16},EXPR_VOID(9){10},EMPTY(16){5},IF(10){i === 5:11,!(i === 5):13},FUNCTION_EXIT(5){},EXPR_VOID(11){12},EMPTY(13){14},CONTINUE(12){8},EXPR_VOID(14){15},EXPR_VOID(15){8}");
		expectedCFGs.add("FUNCTION_ENTRY(17){19},EXPR_VOID(19){18},FUNCTION_EXIT(18){}");

		this.runTest(file, expectedCFGs, Output.NONE);

	}

	@Ignore @Test
	public void testReturn() throws IOException {

		String file = "./test/input/cfg/return.js";

		List<String> expectedCFGs = new LinkedList<String>();
		expectedCFGs.add("SCRIPT_ENTRY(0){2},VAR(2){3},EXPR_RESULT(3){1},SCRIPT_EXIT(1){}");
		expectedCFGs.add("FUNCTION_ENTRY(4){6},EXPR_VOID(6){7},VAR(7){8},WHILE(8){i < 10:9,!(i < 10):15},EXPR_VOID(9){10},EMPTY(15){5},IF(10){i === 4:11,!(i === 4):12},FUNCTION_EXIT(5){},RETURN(11){5},EMPTY(12){13},EXPR_VOID(13){14},EXPR_VOID(14){8}");
		expectedCFGs.add("FUNCTION_ENTRY(16){18},EXPR_VOID(18){17},FUNCTION_EXIT(17){}");

		this.runTest(file, expectedCFGs, Output.NONE);

	}

	@Ignore @Test
	public void testFor() throws IOException {

		String file = "./test/input/cfg/forloop.js";

		List<String> expectedCFGs = new LinkedList<String>();
		expectedCFGs.add("SCRIPT_ENTRY(0){2},VAR(2){3},EXPR_RESULT(3){1},SCRIPT_EXIT(1){}");
		expectedCFGs.add("FUNCTION_ENTRY(4){6},EXPR_VOID(6){7},VAR(7){8},VAR(8){9},FOR(9){i < 10:11,!(i < 10):14},EXPR_VOID(11){12},EMPTY(14){5},EXPR_VOID(12){13},FUNCTION_EXIT(5){},EXPR_VOID(13){10},INC(10){9}");
		expectedCFGs.add("FUNCTION_ENTRY(15){17},EXPR_VOID(17){16},FUNCTION_EXIT(16){}");

		this.runTest(file, expectedCFGs, Output.ADJACENCY_LIST);

	}

	@Ignore @Test
	public void testDo() throws IOException {

		String file = "./test/input/cfg/doloop.js";

		List<String> expectedCFGs = new LinkedList<String>();
		expectedCFGs.add("SCRIPT_ENTRY(0){2},VAR(2){3},EXPR_RESULT(3){1},SCRIPT_EXIT(1){}");
		expectedCFGs.add("FUNCTION_ENTRY(4){6},EXPR_VOID(6){7},VAR(7){8},DO(8){10},EXPR_VOID(10){11},EXPR_VOID(11){12},EXPR_VOID(12){9},WHILE(9){i < 10:8,!(i < 10):13},EMPTY(13){5},FUNCTION_EXIT(5){}");
		expectedCFGs.add("FUNCTION_ENTRY(14){16},EXPR_VOID(16){15},FUNCTION_EXIT(15){}");

		this.runTest(file, expectedCFGs, Output.NONE);

	}

	@Ignore @Test
	public void testForIn() throws IOException {

		String file = "./test/input/cfg/forinloop.js";

		List<String> expectedCFGs = new LinkedList<String>();
		expectedCFGs.add("SCRIPT_ENTRY(0){2},VAR(2){3},EXPR_RESULT(3){1},SCRIPT_EXIT(1){}");
		expectedCFGs.add("FUNCTION_ENTRY(4){6},EXPR_VOID(6){7},VAR(7){8},VAR(8){10},FORIN(10){noises.~hasNextKey():9,!(noises.~hasNextKey()):13},ASSIGN(9){11},EMPTY(13){5},EXPR_VOID(11){12},FUNCTION_EXIT(5){},EXPR_VOID(12){10}");
		expectedCFGs.add("FUNCTION_ENTRY(14){16},EXPR_VOID(16){15},FUNCTION_EXIT(15){}");

		this.runTest(file, expectedCFGs, Output.NONE);

	}

	@Ignore @Test
	public void testSwitch() throws IOException {

		String file = "./test/input/cfg/switch.js";

		List<String> expectedCFGs = new LinkedList<String>();
		expectedCFGs.add("SCRIPT_ENTRY(0){2},VAR(2){3},EXPR_RESULT(3){1},SCRIPT_EXIT(1){}");
		expectedCFGs.add("FUNCTION_ENTRY(4){6},EXPR_VOID(6){7},VAR(7){9},FORIN(9){animals.~hasNextKey():8,!(animals.~hasNextKey()):11},ASSIGN(8){10},EMPTY(11){5},EXPR_VOID(10){9},FUNCTION_EXIT(5){}");
		expectedCFGs.add("FUNCTION_ENTRY(12){14},VAR(14){15},SWITCH(15){animal === \\\"cow\\\":16,animal === \\\"moose\\\":18,animal === \\\"horse\\\":20,animal === \\\"buffalo\\\":22,!(animal === \\\"buffalo\\\" || animal === \\\"horse\\\" || animal === \\\"moose\\\" || animal === \\\"cow\\\"):23},EXPR_VOID(16){17},EXPR_VOID(18){19},EXPR_VOID(20){21},EMPTY(22){23},EXPR_VOID(23){24},BREAK(17){24},BREAK(19){24},BREAK(21){24},EXPR_VOID(24){13},FUNCTION_EXIT(13){}");

		this.runTest(file, expectedCFGs, Output.NONE);

	}

	@Ignore @Test
	public void testWith() throws IOException {

		String file = "./test/input/cfg/with.js";

		List<String> expectedCFGs = new LinkedList<String>();
		expectedCFGs.add("SCRIPT_ENTRY(0){2},EXPR_RESULT(2){1},SCRIPT_EXIT(1){}");
		expectedCFGs.add("FUNCTION_ENTRY(3){5},BEGIN_SCOPE(5){7},EXPR_VOID(7){6},END_SCOPE(6){4},FUNCTION_EXIT(4){}");

		this.runTest(file, expectedCFGs, Output.NONE);

	}

	@Ignore @Test
	public void testTryCatch() throws IOException {

		String file = "./test/input/cfg/trycatch.js";

		List<String> expectedCFGs = new LinkedList<String>();
		expectedCFGs.add("SCRIPT_ENTRY(0){2},EXPR_RESULT(2){1},SCRIPT_EXIT(1){}");
		expectedCFGs.add("FUNCTION_ENTRY(3){5},VAR(5){6},TRY(6){err:9,11},EXPR_VOID(9){8},EXPR_VOID(11){12},EXPR_VOID(8){18,err:10},IF(12){x === Infinity:13,!(x === Infinity):14},RETURN(18){4},EMPTY(10){4},THROW(13){8},EMPTY(14){15},FUNCTION_EXIT(4){},EXPR_VOID(15){16},EMPTY(16){8}");

		this.runTest(file, expectedCFGs, Output.DOT);

	}

	@Ignore @Test
	public void testTryCatchWithoutFinally() throws IOException {

		String file = "./test/input/cfg/trycatchnofinally.js";

		List<String> expectedCFGs = new LinkedList<String>();
		expectedCFGs.add("SCRIPT_ENTRY(0){2},EXPR_RESULT(2){1},SCRIPT_EXIT(1){}");
		expectedCFGs.add("FUNCTION_ENTRY(3){5},TRY(5){err:8,10},EXPR_VOID(8){7},EXPR_VOID(10){7},EMPTY(7){err:9,11},EMPTY(9){4},EMPTY(11){4},FUNCTION_EXIT(4){}");

		this.runTest(file, expectedCFGs, Output.NONE);

	}

	@Ignore @Test
	public void testConditional() throws IOException {

		String file = "./test/input/cfg/conditional.js";

		List<String> expectedCFGs = new LinkedList<String>();
		expectedCFGs.add("SCRIPT_ENTRY(0){2},VAR(2){1},SCRIPT_EXIT(1){}");

		this.runTest(file, expectedCFGs, Output.NONE);

	}

	@Ignore @Test
	public void testEmptyStatementLoop() throws IOException {

		String file = "./test/input/cfg/Common_old.js";

		List<String> expectedCFGs = new LinkedList<String>();
		expectedCFGs.add("SCRIPT_ENTRY(0){2},VAR(2){1},SCRIPT_EXIT(1){}");

		this.runTest(file, expectedCFGs, Output.DOT);

	}

}
