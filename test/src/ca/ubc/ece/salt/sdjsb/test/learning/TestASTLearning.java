package ca.ubc.ece.salt.sdjsb.test.learning;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Test;

import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode.ChangeType;
import ca.ubc.ece.salt.sdjsb.ControlFlowDifferencing;
import ca.ubc.ece.salt.sdjsb.analysis.learning.apis.APIFactory;
import ca.ubc.ece.salt.sdjsb.analysis.learning.apis.AbstractAPI;
import ca.ubc.ece.salt.sdjsb.analysis.learning.apis.KeywordDefinition.KeywordType;
import ca.ubc.ece.salt.sdjsb.analysis.learning.apis.KeywordUse;
import ca.ubc.ece.salt.sdjsb.analysis.learning.apis.KeywordUse.KeywordContext;
import ca.ubc.ece.salt.sdjsb.analysis.learning.apis.PackageAPI;
import ca.ubc.ece.salt.sdjsb.analysis.learning.apis.TopLevelAPI;
import ca.ubc.ece.salt.sdjsb.analysis.learning.ast.LearningAnalysis;
import ca.ubc.ece.salt.sdjsb.analysis.learning.ast.LearningDataSet;

public class TestASTLearning {

	/**
	 * Tests data mining data set construction.
	 * @param args The command line arguments (i.e., old and new file names).
	 * @throws Exception
	 */
	protected void runTest(String[] args, List<MockFeatureVector> expected) throws Exception {

		/* Set up the FeatureVectorManager, which will store all the feature
		 * vectors produced by our analysis and perform pre-processing tasks
		 * for data mining. */
		List<String> packagesToExtract = Arrays.asList("fs", "path", "Date");
		LearningDataSet featureVectorManager = new LearningDataSet(packagesToExtract);

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

        /* Verify the expected feature vectors match the actual feature vectors. */
        for(MockFeatureVector fv : expected) {
        	Assert.assertTrue(featureVectorManager.contains(fv.functionName, fv.expectedKeywords));
        }
	}

	/*
	 * Tests if predictor actually realize that parse() belongs to "Date" which
	 * is globally available. To make things more interesting, "path" is also
	 * imported (and also has method parse()), but we use other keywords from
	 * Date to help the predictor
	 */
	@Test
	public void testDate() throws Exception {
		String src = "./test/input/learning/date_old.js";
		String dst = "./test/input/learning/date_new.js";

		TopLevelAPI topLevelAPI = APIFactory.buildTopLevelAPI();
		AbstractAPI DateAPI = topLevelAPI.getFirstKeyword(KeywordType.CLASS, "Date").api;

		KeywordUse parse = new KeywordUse(KeywordType.METHOD, KeywordContext.METHOD_CALL, "parse", ChangeType.INSERTED,
				DateAPI);

		MockFeatureVector function = new MockFeatureVector("testFunction");
		function.expectedKeywords.add(Pair.of(parse, 1));

		this.runTest(new String[] { src, dst }, Arrays.asList(function));
	}

	/*
	 * Tests if predictor actually realize that parse() belongs to "path" (which
	 * was imported), and not to "Date", which is globally available
	 */
	@Test
	public void testPath() throws Exception {
		String src = "./test/input/learning/path_old.js";
		String dst = "./test/input/learning/path_new.js";

		PackageAPI path = APIFactory.buildPathPackage();

		KeywordUse parse = new KeywordUse(KeywordType.METHOD, KeywordContext.METHOD_CALL, "parse", ChangeType.INSERTED,
				path);
		KeywordUse pkg = new KeywordUse(KeywordType.PACKAGE, KeywordContext.REQUIRE, "path", ChangeType.INSERTED, path);

		MockFeatureVector function = new MockFeatureVector("testFunction");
		function.expectedKeywords.add(Pair.of(parse, 1));

		MockFeatureVector script = new MockFeatureVector("~script~");
		script.expectedKeywords.add(Pair.of(pkg, 1));

		this.runTest(new String[] { src, dst }, Arrays.asList(function, script));
	}

	@Test
	public void testFileSystem() throws Exception {
		String src = "./test/input/learning/lrn_fs_old.js";
		String dst = "./test/input/learning/lrn_fs_new.js";

		PackageAPI fs = APIFactory.buildFileSystemPackage();

		/* Create the expected keywords. */
		KeywordUse openSync = new KeywordUse(KeywordType.METHOD, KeywordContext.METHOD_CALL, "openSync",
				ChangeType.UNCHANGED, fs);
		KeywordUse closeSync = new KeywordUse(KeywordType.METHOD, KeywordContext.METHOD_CALL, "closeSync",
				ChangeType.INSERTED, fs);
		KeywordUse existsSync = new KeywordUse(KeywordType.METHOD, KeywordContext.METHOD_CALL, "existsSync",
				ChangeType.UNCHANGED, fs);
		KeywordUse writeSync = new KeywordUse(KeywordType.METHOD, KeywordContext.METHOD_CALL, "writeSync",
				ChangeType.UNCHANGED, fs);
		KeywordUse fsU = new KeywordUse(KeywordType.PACKAGE, KeywordContext.REQUIRE, "fs", ChangeType.UNCHANGED, fs);
		KeywordUse fsI = new KeywordUse(KeywordType.PACKAGE, KeywordContext.REQUIRE, "fs", ChangeType.INSERTED, fs);

		/* Create the expected feature vectors. */
		MockFeatureVector fv1 = new MockFeatureVector("writeHello");
		fv1.expectedKeywords.add(Pair.of(openSync, 1));
		fv1.expectedKeywords.add(Pair.of(closeSync, 1));
		fv1.expectedKeywords.add(Pair.of(existsSync, 0));
		fv1.expectedKeywords.add(Pair.of(writeSync, 1));
		fv1.expectedKeywords.add(Pair.of(fsU, 0));
		fv1.expectedKeywords.add(Pair.of(fsI, 0));

		MockFeatureVector fv2 = new MockFeatureVector("writeHello");
		fv2.expectedKeywords.add(Pair.of(openSync, 0));
		fv2.expectedKeywords.add(Pair.of(closeSync, 0));
		fv2.expectedKeywords.add(Pair.of(existsSync, 1));
		fv2.expectedKeywords.add(Pair.of(writeSync, 0));
		fv2.expectedKeywords.add(Pair.of(fsU, 2));
		fv2.expectedKeywords.add(Pair.of(fsI, 1));

		List<MockFeatureVector> expected = Arrays.asList(fv1, fv2);

		this.runTest(new String[] {src, dst}, expected);
	}

	/**
	 * A mock FeatureVector for verifying test cases.
	 */
	private class MockFeatureVector {

		public String functionName;
		public List<Pair<KeywordUse, Integer>> expectedKeywords;

		public MockFeatureVector(String functionName) {
			this.functionName = functionName;
			this.expectedKeywords = new LinkedList<Pair<KeywordUse, Integer>>();
		}

	}

}
