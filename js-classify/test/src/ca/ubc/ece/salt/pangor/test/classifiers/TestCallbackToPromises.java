package ca.ubc.ece.salt.pangor.test.classifiers;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import ca.ubc.ece.salt.pangor.analysis.classify.ClassifierDataSet;
import ca.ubc.ece.salt.pangor.analysis.promises.PromisesAnalysis;
import ca.ubc.ece.salt.pangor.batch.AnalysisMetaInformation;
import ca.ubc.ece.salt.pangor.classify.alert.ClassifierAlert;
import ca.ubc.ece.salt.pangor.classify.alert.PromisesAlert;

public class TestCallbackToPromises extends TestAnalysis {

	private final AnalysisMetaInformation AMI = new AnalysisMetaInformation(0, 0, "test", "homepage", "src file", "dst file", "src commit", "dst commit", "src code", "dst code");

	private void runTest(String[] args, List<ClassifierAlert> expectedAlerts, boolean printAlerts) throws Exception {
		ClassifierDataSet dataSet = new ClassifierDataSet(null, null);
		PromisesAnalysis analysis = new PromisesAnalysis(dataSet, AMI);
		super.runTest(args, expectedAlerts, printAlerts, analysis, dataSet);
	}

	@Test
	public void testSimple() throws Exception{
		String src = "./test/input/promises/simple.js";
		String dst = "./test/input/promises/simple-human.js";
		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();
		expectedAlerts.add(new PromisesAlert(AMI, "UNKNOWN", "REF", "PROM"));
		this.runTest(new String[] {src, dst}, expectedAlerts, false);
	}

	@Test
	public void testNewFunction() throws Exception{
		String src = "./test/input/promises/new.js";
		String dst = "./test/input/promises/new-human.js";
		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();
		this.runTest(new String[] {src, dst}, expectedAlerts, false);
	}

	/**
	 * Test a case where a promise exists in another function. Should not
	 * generate an alert. Doing the analysis at a function level instead of
	 * a script level will make this test generate an alert.
	 * @throws Exception
	 */
	@Test
	public void testPromisesPresent() throws Exception{
		String src = "./test/input/promises/added.js";
		String dst = "./test/input/promises/added-human.js";
		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();
		this.runTest(new String[] {src, dst}, expectedAlerts, false);
	}

}