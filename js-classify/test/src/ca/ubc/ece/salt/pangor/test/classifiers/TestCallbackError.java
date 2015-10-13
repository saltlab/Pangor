package ca.ubc.ece.salt.pangor.test.classifiers;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import ca.ubc.ece.salt.pangor.analysis.callbackerror.CallbackErrorAnalysis;
import ca.ubc.ece.salt.pangor.analysis.classify.ClassifierDataSet;
import ca.ubc.ece.salt.pangor.batch.AnalysisMetaInformation;
import ca.ubc.ece.salt.pangor.classify.alert.CallbackErrorAlert;
import ca.ubc.ece.salt.pangor.classify.alert.ClassifierAlert;

public class TestCallbackError extends TestAnalysis {

	private final AnalysisMetaInformation AMI = new AnalysisMetaInformation(0, 0, "test", "homepage", "src file",
			"dst file", "src commit", "dst commit", "src code", "dst code");

	private void runTest(String[] args, List<ClassifierAlert> expectedAlerts, boolean printAlerts) throws Exception {
		ClassifierDataSet dataSet = new ClassifierDataSet(null, null);
		CallbackErrorAnalysis analysis = new CallbackErrorAnalysis(dataSet, AMI);
		super.runTest(args, expectedAlerts, printAlerts, analysis, dataSet);
	}

	/*
	 * Initial simple scenario
	 */
	@Test
	public void testSimplestScenario() throws Exception {
		String src = "./test/input/callback_error/simplest_old.js";
		String dst = "./test/input/callback_error/simplest_new.js";

		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();
		expectedAlerts.add(new CallbackErrorAlert(AMI, "doSomething", "cb"));

		this.runTest(new String[] { src, dst }, expectedAlerts, true);
	}

	/*
	 * WikiIRC: 296924
	 */
	@Test
	public void testReal() throws Exception {
		String src = "./test/input/callback_error/real_old.js";
		String dst = "./test/input/callback_error/real_new.js";

		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();
		expectedAlerts.add(new CallbackErrorAlert(AMI, "~anonymous~", "callback"));

		this.runTest(new String[] { src, dst }, expectedAlerts, true);
	}

	/*
	 * marked: 558769
	 *
	 * No alerts because we already had another callback call passing error
	 */
	@Test
	public void testReal2() throws Exception {
		String src = "./test/input/callback_error/real2_old.js";
		String dst = "./test/input/callback_error/real2_new.js";

		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();

		this.runTest(new String[] { src, dst }, expectedAlerts, true);
	}

	/*
	 * mediacenterjs: 108767
	 */
	@Test
	public void testReal3() throws Exception {
		String src = "./test/input/callback_error/real3_old.js";
		String dst = "./test/input/callback_error/real3_new.js";

		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();
		expectedAlerts.add(new CallbackErrorAlert(AMI, "~anonymous~", "callback"));

		this.runTest(new String[] { src, dst }, expectedAlerts, true);
	}

	/*
	 * node-browserify: 2156173
	 */
	@Test
	public void testReal4() throws Exception {
		String src = "./test/input/callback_error/real4_old.js";
		String dst = "./test/input/callback_error/real4_new.js";

		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();
		expectedAlerts.add(new CallbackErrorAlert(AMI, "~anonymous~", "cb"));

		this.runTest(new String[] { src, dst }, expectedAlerts, true);
	}
}