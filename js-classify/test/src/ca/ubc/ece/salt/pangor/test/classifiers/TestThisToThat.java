package ca.ubc.ece.salt.pangor.test.classifiers;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import ca.ubc.ece.salt.pangor.analysis.classify.ClassifierDataSet;
import ca.ubc.ece.salt.pangor.analysis.thistothat.ThisToThatAnalysis;
import ca.ubc.ece.salt.pangor.batch.AnalysisMetaInformation;
import ca.ubc.ece.salt.pangor.classify.alert.ClassifierAlert;
import ca.ubc.ece.salt.pangor.classify.alert.ThisToThatAlert;

public class TestThisToThat extends TestAnalysis {

	private final AnalysisMetaInformation AMI = new AnalysisMetaInformation(0, 0, "test", "homepage", "src file",
			"dst file", "src commit", "dst commit", "src code", "dst code");

	private void runTest(String[] args, List<ClassifierAlert> expectedAlerts, boolean printAlerts) throws Exception {
		ClassifierDataSet dataSet = new ClassifierDataSet(null, null);
		ThisToThatAnalysis analysis = new ThisToThatAnalysis(dataSet, AMI);
		super.runTest(args, expectedAlerts, printAlerts, analysis, dataSet);
	}

	/*
	 * Initial simple scenario
	 */
	@Test
	public void testSimplestScenario() throws Exception {
		String src = "./test/input/this_to_that/simplest_old.js";
		String dst = "./test/input/this_to_that/simplest_new.js";

		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();
		expectedAlerts.add(new ThisToThatAlert(AMI, "~anonymous~", "that"));
		expectedAlerts.add(new ThisToThatAlert(AMI, "named", "that"));

		this.runTest(new String[] { src, dst }, expectedAlerts, true);
	}

	/*
	 * popcorn: used to be a false positive. moving calls from outside to inside
	 * anonymous function, and changing from this to self accordingly
	 */
	@Test
	public void testPopcorn() throws Exception {
		String src = "./test/input/this_to_that/popcorn_fp_old.js";
		String dst = "./test/input/this_to_that/popcorn_fp_new.js";

		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();

		this.runTest(new String[] { src, dst }, expectedAlerts, true);
	}

	/*
	 * hapi: same case as popcorn. Changes from this to self are expected
	 * because they are moving scope
	 */
	@Test
	public void testHapi() throws Exception {
		String src = "./test/input/this_to_that/hapi_self_old.js";
		String dst = "./test/input/this_to_that/hapi_self_new.js";

		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();

		this.runTest(new String[] { src, dst }, expectedAlerts, true);
	}

	/*
	 * hapi: same case as popcorn. Changes from this to self are expected
	 * because they are moving scope
	 */
	@Test
	public void testBower() throws Exception {
		String src = "./test/input/this_to_that/bower_fp_old.js";
		String dst = "./test/input/this_to_that/bower_fp_new.js";

		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();

		this.runTest(new String[] { src, dst }, expectedAlerts, true);
	}

}