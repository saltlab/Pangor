package ca.ubc.ece.salt.sdjsb.test.learning;

import static org.junit.Assert.*;

import java.util.Set;

import org.junit.Test;

import ca.ubc.ece.salt.sdjsb.ControlFlowDifferencing;
import ca.ubc.ece.salt.sdjsb.alert.Alert;
import ca.ubc.ece.salt.sdjsb.analysis.learning.ast.FeatureVector;
import ca.ubc.ece.salt.sdjsb.analysis.learning.ast.LearningAnalysis;

/**
 * TODO
 */
public class TestASTLearning {

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
        Set<Alert> alerts = cfd.analyze(analysis);
        
        /* Print the alerts. */
        System.out.println(FeatureVector.getHeader());
        for(Alert alert : alerts) {
        	System.out.println(alert.getFeatureVector("tst", null, null, null, null));
        }

        for(Alert alert : alerts) {
        	System.out.println(alert.getSourceCode());
        	System.out.println(alert.getDestinationCode());
        }
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
