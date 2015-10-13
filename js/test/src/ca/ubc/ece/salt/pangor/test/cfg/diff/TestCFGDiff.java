package ca.ubc.ece.salt.pangor.test.cfg.diff;

import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

import ca.ubc.ece.salt.pangor.cfd.CFDContext;
import ca.ubc.ece.salt.pangor.cfd.ControlFlowDifferencing;
import ca.ubc.ece.salt.pangor.cfg.CFG;
import ca.ubc.ece.salt.pangor.cfg.CFGNode;
import ca.ubc.ece.salt.pangor.cfg.CFGPrinter;
import ca.ubc.ece.salt.pangor.cfg.CFGPrinter.Output;
import ca.ubc.ece.salt.pangor.js.cfg.JavaScriptCFGFactory;

public class TestCFGDiff extends TestCase {

	/**
	 * Integration test for CFG building and GumTree differencing.
	 * @param file
	 * @param expectedCFGs
	 * @param output
	 * @throws Exception
	 */
	protected void runTest(String[] args, List<String> expectedSrcCFGs, List<String> expectedDstCFGs, Output output) throws Exception {

		/* Compute the CFG diff. */
		CFDContext context = ControlFlowDifferencing.setup(new JavaScriptCFGFactory(), args);

		/* Print the CFGs. */
//		System.out.println("Source CFGs: *************");
//		this.getCFGs(context.srcCFGs, output);
//		System.out.println("Destination CFGs: *************");
//		this.getCFGs(context.dstCFGs, output);

        /* Check the CFGs. */
		List<String> actualSrcCFGs = new LinkedList<String>();
		for(CFG srcCFG : context.srcCFGs) {
			actualSrcCFGs.add(CFGPrinter.print(Output.DOT_TEST, srcCFG));
		}

		if(expectedSrcCFGs != null) check(actualSrcCFGs, expectedSrcCFGs);

		List<String> actualDstCFGs = new LinkedList<String>();
		for(CFG dstCFG : context.dstCFGs) {
			actualDstCFGs.add(CFGPrinter.print(Output.DOT_TEST, dstCFG));
		}

        if(expectedDstCFGs != null) check(actualDstCFGs, expectedDstCFGs);

	}

	protected void check(List<String> actualCFGs, List<String> expectedCFGs) {

		/* Check the CFGs. */
		for(String expectedCFG : expectedCFGs) {
			int index = actualCFGs.indexOf(expectedCFG.replace("\\", "\\\\").replace("\"", "\\\""));
            TestCase.assertTrue("a CFG was not produced correctly", index >= 0);
		}

		/* Check that no additional CFGs were produced. */
		TestCase.assertEquals("an unexpected CFG was produced", actualCFGs.size(), expectedCFGs.size());

	}

	/**
	 * Prints a list of CFGs from a file.
	 * @param cfgs The CFGs from either the source or destination file.
	 * @param output How to display the output (adjacency list or DOT).
	 */
	@SuppressWarnings("unused")
	private void getCFGs(List<CFG> cfgs, Output output) {

		/* Reset the CFGNode id generator value (needed for consistent test cases). */
		CFGNode.resetIdGen();

		/* Get the serialized CFGs. */
		List<String> actualCFGs = new LinkedList<String>();
        int n = 1;
        for(CFG cfg : cfgs) {
            String serialized = CFGPrinter.adjacencyList(cfg);
            actualCFGs.add(serialized);
            System.out.println("CFG" + n + ": " + CFGPrinter.print(output, cfg));
            n++;
        }

	}

//	@Test
//	public void testIf() throws Exception {
//
//		String src = "./test/input/special_type_handling/sth_undefined_old.js";
//		String dst = "./test/input/special_type_handling/sth_undefined_new.js";
//
//		List<String> expectedSrcCFGs = new LinkedList<String>();
//		expectedSrcCFGs.add("digraph control_flow_graph { node [ style = filled fillcolor = \"white\" ]; 0 [ fillcolor = \"grey\" label = \"script entry\" ]; 0 -> 2 [ color = \"grey\" ]; 2 [ fillcolor = \"grey\" label = \"var a;\" ]; 2 -> 3 [ color = \"grey\" ]; 3 [ fillcolor = \"yellow\" label = \"console.log(a);\" ]; 3 -> 1 [ color = \"grey\" ]; 1 [ fillcolor = \"grey\" label = \"\" ]; }");
//
//		List<String> expectedDstCFGs = new LinkedList<String>();
//		expectedDstCFGs.add("digraph control_flow_graph { node [ style = filled fillcolor = \"white\" ]; 4 [ fillcolor = \"grey\" label = \"script entry\" ]; 4 -> 6 [ color = \"grey\" ]; 6 [ fillcolor = \"grey\" label = \"var a;\" ]; 6 -> 7 [ color = \"grey\" ]; 7 [ fillcolor = \"grey\" label = \"\" ]; 7 -> 8 [ color = \"grey\" fontcolor = \"green\" label = \"a !== undefined\" ]; 7 -> 9 [ color = \"green\" fontcolor = \"green\" label = \"!(a !== undefined)\" ]; 8 [ fillcolor = \"yellow\" label = \"console.log(a);\" ]; 8 -> 5 [ color = \"grey\" ]; 9 [ fillcolor = \"grey\" label = \"\" ]; 9 -> 5 [ color = \"green\" ]; 5 [ fillcolor = \"grey\" label = \"\" ]; }");
//
//		this.runTest(new String[] {src, dst}, expectedSrcCFGs, expectedDstCFGs, Output.DOT);
//
//	}

//	@Test
//	public void testReverseIf() throws Exception {
//
//		String src = "./test/input/special_type_handling/sth_undefined_new.js";
//		String dst = "./test/input/special_type_handling/sth_undefined_old.js";
//
//		List<String> expectedSrcCFGs = new LinkedList<String>();
//		expectedSrcCFGs.add("digraph control_flow_graph { node [ style = filled fillcolor = \"white\" ]; 0 [ fillcolor = \"grey\" label = \"script entry\" ]; 0 -> 2 [ color = \"grey\" ]; 2 [ fillcolor = \"grey\" label = \"var a;\" ]; 2 -> 3 [ color = \"grey\" ]; 3 [ fillcolor = \"grey\" label = \"\" ]; 3 -> 4 [ color = \"grey\" fontcolor = \"red\" label = \"a !== undefined\" ]; 3 -> 5 [ color = \"red\" fontcolor = \"red\" label = \"!(a !== undefined)\" ]; 4 [ fillcolor = \"yellow\" label = \"console.log(a);\" ]; 4 -> 1 [ color = \"grey\" ]; 5 [ fillcolor = \"grey\" label = \"\" ]; 5 -> 1 [ color = \"red\" ]; 1 [ fillcolor = \"grey\" label = \"\" ]; }");
//
//		List<String> expectedDstCFGs = new LinkedList<String>();
//		expectedDstCFGs.add("digraph control_flow_graph { node [ style = filled fillcolor = \"white\" ]; 6 [ fillcolor = \"grey\" label = \"script entry\" ]; 6 -> 8 [ color = \"grey\" ]; 8 [ fillcolor = \"grey\" label = \"var a;\" ]; 8 -> 9 [ color = \"grey\" ]; 9 [ fillcolor = \"yellow\" label = \"console.log(a);\" ]; 9 -> 7 [ color = \"grey\" ]; 7 [ fillcolor = \"grey\" label = \"\" ]; }");
//
//		this.runTest(new String[] {src, dst}, expectedSrcCFGs, expectedDstCFGs, Output.DOT);
//
//	}

//	@Test
//	public void testMultipleCFGs() throws Exception {
//
//		String src = "./test/input/callback_parameter/cbp_old.js";
//		String dst = "./test/input/callback_parameter/cbp_new.js";
//
//		List<String> expectedSrcCFGs = new LinkedList<String>();
//
//		expectedSrcCFGs.add("digraph control_flow_graph { node [ style = filled fillcolor = \"white\" ]; 0 [ fillcolor = \"grey\" label = \"script entry\" ]; 0 -> 2 [ color = \"grey\" ]; 2 [ fillcolor = \"grey\" label = \"printMessage(\\\"Hello World!\\\", donePrinting);\" ]; 2 -> 1 [ color = \"grey\" ]; 1 [ fillcolor = \"grey\" label = \"\" ]; }");
//		expectedSrcCFGs.add("digraph control_flow_graph { node [ style = filled fillcolor = \"white\" ]; 3 [ fillcolor = \"grey\" label = \"printMessage(message,callback)\" ]; 3 -> 5 [ color = \"grey\" ]; 5 [ fillcolor = \"grey\" label = \"console.log(message);\" ]; 5 -> 4 [ color = \"grey\" ]; 4 [ fillcolor = \"grey\" label = \"\" ]; }");
//		expectedSrcCFGs.add("digraph control_flow_graph { node [ style = filled fillcolor = \"white\" ]; 6 [ fillcolor = \"grey\" label = \"donePrinting()\" ]; 6 -> 8 [ color = \"grey\" ]; 8 [ fillcolor = \"yellow\" label = \"console.log(\\\"Finished!\\\");\" ]; 8 -> 7 [ color = \"grey\" ]; 7 [ fillcolor = \"grey\" label = \"\" ]; }");
//
//		List<String> expectedDstCFGs = new LinkedList<String>();
//
//		expectedDstCFGs.add("digraph control_flow_graph { node [ style = filled fillcolor = \"white\" ]; 9 [ fillcolor = \"grey\" label = \"script entry\" ]; 9 -> 11 [ color = \"grey\" ]; 11 [ fillcolor = \"grey\" label = \"printMessage(\\\"Hello World!\\\", donePrinting);\" ]; 11 -> 10 [ color = \"grey\" ]; 10 [ fillcolor = \"grey\" label = \"\" ]; }");
//		expectedDstCFGs.add("digraph control_flow_graph { node [ style = filled fillcolor = \"white\" ]; 12 [ fillcolor = \"grey\" label = \"printMessage(message,callback)\" ]; 12 -> 14 [ color = \"grey\" ]; 14 [ fillcolor = \"grey\" label = \"console.log(null, message);\" ]; 14 -> 13 [ color = \"grey\" ]; 13 [ fillcolor = \"grey\" label = \"\" ]; }");
//		expectedDstCFGs.add("digraph control_flow_graph { node [ style = filled fillcolor = \"white\" ]; 15 [ fillcolor = \"grey\" label = \"donePrinting(err)\" ]; 15 -> 17 [ color = \"grey\" ]; 17 [ fillcolor = \"grey\" label = \"\" ]; 17 -> 18 [ color = \"green\" fontcolor = \"green\" label = \"err\" ]; 17 -> 19 [ color = \"grey\" fontcolor = \"green\" label = \"!(err)\" ]; 18 [ fillcolor = \"green\" label = \"console.log(\\\"Error!\\\");\" ]; 18 -> 20 [ color = \"green\" ]; 19 [ fillcolor = \"grey\" label = \"\" ]; 19 -> 20 [ color = \"grey\" ]; 20 [ fillcolor = \"yellow\" label = \"console.log(\\\"Finished!\\\");\" ]; 20 -> 16 [ color = \"grey\" ]; 16 [ fillcolor = \"grey\" label = \"\" ]; }");
//
//		this.runTest(new String[] {src, dst}, expectedSrcCFGs, expectedDstCFGs, Output.DOT);
//
//	}

	/**
	 * Too complex to add expected CFGs. We are using it to debug exceptions.
	 * @throws Exception
	 */
	@Test
	public void testPM2_1() throws Exception {

		String src = "./test/input/cfg_diff/CLI_0_old.js";
		String dst = "./test/input/cfg_diff/CLI_0_new.js";

		this.runTest(new String[] {src, dst}, null, null, Output.DOT);

	}

	/**
	 * Too complex to add expected CFGs. We are using it to debug exceptions.
	 * @throws Exception
	 */
	@Test
	public void testPM2_2() throws Exception {

		String src = "./test/input/cfg_diff/Common_old.js";
		String dst = "./test/input/cfg_diff/Common_new.js";

		this.runTest(new String[] {src, dst}, null, null, Output.DOT);

	}

	/**
	 * Too complex to add expected CFGs. We are using it to debug exceptions.
	 * @throws Exception
	 */
	@Test
	public void testPM2_3() throws Exception {

		String src = "./test/input/cfg_diff/CliUx_old.js";
		String dst = "./test/input/cfg_diff/CliUx_new.js";

		this.runTest(new String[] {src, dst}, null, null, Output.DOT);

	}

	/**
	 * Too complex to add expected CFGs. We are using it to debug exceptions.
	 * @throws Exception
	 */
	@Test
	public void testPM2_4() throws Exception {

		String src = "./test/input/cfg_diff/custom_action_old.js";
		String dst = "./test/input/cfg_diff/custom_action_new.js";

		this.runTest(new String[] {src, dst}, null, null, Output.DOT);

	}

	/**
	 * Too complex to add expected CFGs. We are using it to debug exceptions.
	 * @throws Exception
	 */
	@Test
	public void testPM2_5() throws Exception {

		String src = "./test/input/cfg_diff/CLI_1_old.js";
		String dst = "./test/input/cfg_diff/CLI_1_new.js";

		this.runTest(new String[] {src, dst}, null, null, Output.DOT);

	}

	/**
	 * Too complex to add expected CFGs. We are using it to debug exceptions.
	 * @throws Exception
	 */
	@Test
	public void testPM2_6() throws Exception {

		String src = "./test/input/special_type_handling/ForkMode_old.js";
		String dst = "./test/input/special_type_handling/ForkMode_new.js";

		this.runTest(new String[] {src, dst}, null, null, Output.DOT);

	}

	/**
	 * Too complex to add expected CFGs. We are using it to debug exceptions.
	 *
	 * This shows a special type handling false positive (claims 'e' is falsey checked).
	 *
	 * @throws Exception
	 */
	@Test
	public void testPM2_7() throws Exception {

		String src = "./test/input/special_type_handling/ForkMode_e_old.js";
		String dst = "./test/input/special_type_handling/ForkMode_e_new.js";

		this.runTest(new String[] {src, dst}, null, null, Output.DOT);

	}

	/**
	 * Too complex to add expected CFGs. We are using it to debug exceptions.
	 *
	 * This shows a special type handling false positive (claims 'e' is falsey checked).
	 *
	 * @throws Exception
	 */
	@Test
	public void testPM2_8() throws Exception {

		String src = "./test/input/special_type_handling/ForkMode_e2_old.js";
		String dst = "./test/input/special_type_handling/ForkMode_e2_new.js";

		this.runTest(new String[] {src, dst}, null, null, Output.DOT);

	}

	/**
	 * Too complex to add expected CFGs. We are using it to debug exceptions.
	 *
	 * This shows a special type handling false positive (claims 'e' is falsey checked).
	 *
	 * @throws Exception
	 */
	@Test
	public void testPM2_9() throws Exception {

		String src = "./test/input/cfg_diff/constants_old.js";
		String dst = "./test/input/cfg_diff/constants_new.js";

		this.runTest(new String[] {src, dst}, null, null, Output.DOT);

	}

}
