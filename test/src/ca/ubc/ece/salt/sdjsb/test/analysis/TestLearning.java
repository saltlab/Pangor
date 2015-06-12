package ca.ubc.ece.salt.sdjsb.test.analysis;

import static org.junit.Assert.*;

import java.util.Set;

import org.junit.Test;
import org.mozilla.javascript.ast.AstNode;

import ca.ubc.ece.salt.sdjsb.ControlFlowDifferencing;
import ca.ubc.ece.salt.sdjsb.alert.Alert;
import ca.ubc.ece.salt.sdjsb.analysis.learning.LearningAnalysis;
import ca.ubc.ece.salt.sdjsb.analysis.learning.LearningFlowAnalysis;
import ca.ubc.ece.salt.sdjsb.analysis.learning.PathFragment;
import ca.ubc.ece.salt.sdjsb.cfg.CFGNode;

/**
 * TODO
 */
public class TestLearning {

	/**
	 * Tests flow analysis repair classifiers.
	 * @param args The command line arguments (i.e., old and new file names).
	 * @param expectedAlerts The list of alerts that should be produced.
	 * @param printAlerts If true, print the alerts to standard output.
	 * @throws Exception 
	 */
	protected void runTest(String[] args) throws Exception {
		
		/* Set up the analysis. */
		LearningAnalysis analysis = new LearningAnalysis();
		
		/* Control flow difference the files. */
		ControlFlowDifferencing cfd = new ControlFlowDifferencing(args);
        
		/* Run the analysis. */
        cfd.analyze(analysis);

	}

	@Test
	public void testCallback() throws Exception {
		String src = "./test/input/learning/lrn_old.js";
		String dst = "./test/input/learning/lrn_new.js";
		this.runTest(new String[] {src, dst});
	}

	@Test
	public void testUndefined() throws Exception{
		String src = "./test/input/special_type_handling/sth_undefined_old.js";
		String dst = "./test/input/special_type_handling/sth_undefined_new.js";
		this.runTest(new String[] {src, dst});
	}

	@Test
	public void testTypeOf() throws Exception {
		String src = "./test/input/special_type_handling/sth_undefined_typeof_old.js";
		String dst = "./test/input/special_type_handling/sth_undefined_typeof_new.js";
		this.runTest(new String[] {src, dst});
	}

	@Test
	public void testUndefinedReturn() throws Exception{
		String src = "./test/input/special_type_handling/sth_undefined_return_old.js";
		String dst = "./test/input/special_type_handling/sth_undefined_return_new.js";
		this.runTest(new String[] {src, dst});
	}

	@Test
	public void testUndefinedRealWorld() throws Exception{
		String src = "./test/input/special_type_handling/tv-functions-old.js";
		String dst = "./test/input/special_type_handling/tv-functions-new.js";
		this.runTest(new String[] {src, dst});
	}

}
