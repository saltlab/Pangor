package ca.ubc.ece.salt.pangor.test.learning;

import org.junit.Assert;
import org.junit.Test;
import org.mozilla.javascript.ast.AstRoot;

import ca.ubc.ece.salt.pangor.analysis.learning.ChangeComplexityVisitor;
import ca.ubc.ece.salt.pangor.cfd.CFDContext;
import ca.ubc.ece.salt.pangor.cfd.ControlFlowDifferencing;

public class TestChangeComplexityVisitor {

	/**
	 * Tests the change complexity visitor.
	 * @param args The files to test.
	 * @throws Exception
	 */
	protected void runTest(String[] args, int expectedSrcScore, int expectedDstScore) throws Exception {

		/* Compute the CFG diff. */
		CFDContext context = ControlFlowDifferencing.setup(args);

		/* Compute the change complexity score. */
		int actualSrcScore = ChangeComplexityVisitor.getChangeComplexity((AstRoot)context.srcScript);
		int actualDstScore = ChangeComplexityVisitor.getChangeComplexity((AstRoot)context.dstScript);

		Assert.assertEquals(expectedSrcScore, actualSrcScore);
		Assert.assertEquals(expectedDstScore, actualDstScore);

	}

	@Test
	public void testTVFunctions() throws Exception {

		String src = "./test/input/special_type_handling/tv-functions-old.js";
		String dst = "./test/input/special_type_handling/tv-functions-new.js";

		this.runTest(new String[] {src, dst}, 1, 3);

	}

}
