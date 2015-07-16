package ca.ubc.ece.salt.sdjsb.test.learning;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import ca.ubc.ece.salt.sdjsb.ControlFlowDifferencing;
import ca.ubc.ece.salt.sdjsb.analysis.learning.ast.FeatureVectorManager;
import ca.ubc.ece.salt.sdjsb.analysis.learning.ast.LearningAnalysis;

public class TestASTLearning {

	/**
	 * Tests data mining data set construction.
	 * @param args The command line arguments (i.e., old and new file names).
	 * @throws Exception 
	 */
	protected void runTest(String[] args) throws Exception {
		
		/* Set up the FeatureVectorManager, which will store all the feature
		 * vectors produced by our analysis and perform pre-processing tasks
		 * for data mining. */
		List<String> packagesToExtract = Arrays.asList("fs");
		FeatureVectorManager featureVectorManager = new FeatureVectorManager(packagesToExtract);
		
		/* Set up the analysis. */
		LearningAnalysis analysis = new LearningAnalysis(featureVectorManager);
		
		/* Control flow difference the files. */
		ControlFlowDifferencing cfd = new ControlFlowDifferencing(args);
        
		/* Run the analysis. There are no alerts produced by the 
		 * LearningAnalysis... only FeatureVectors stored in the 
		 * FeatureVectorManager. */
        cfd.analyze(analysis);
        
        /* Pre-process the alerts. */
        featureVectorManager.preProcess();
        
        /* Print the data set. */
        System.out.println(featureVectorManager.getFeatureVectorHeader());
        System.out.println(featureVectorManager.getFeatureVector());

	}
	
	@Test
	public void testFileSystem() throws Exception {
		String src = "./test/input/learning/lrn_fs_old.js";
		String dst = "./test/input/learning/lrn_fs_new.js";
		this.runTest(new String[] {src, dst});
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
