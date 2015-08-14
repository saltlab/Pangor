package ca.ubc.ece.salt.sdjsb.test.analysis;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import ca.ubc.ece.salt.sdjsb.analysis.callbackerror.CallbackErrorAnalysis;
import ca.ubc.ece.salt.sdjsb.analysis.classify.ClassifierDataSet;
import ca.ubc.ece.salt.sdjsb.batch.AnalysisMetaInformation;
import ca.ubc.ece.salt.sdjsb.classify.alert.CallbackErrorAlert;
import ca.ubc.ece.salt.sdjsb.classify.alert.ClassifierAlert;

public class TestCallbackError extends TestAnalysis {

	private final AnalysisMetaInformation AMI = new AnalysisMetaInformation(0, 0, "test", "homepage", "src file", "dst file", "src commit", "dst commit", "src code", "dst code");

	private void runTest(String[] args, List<ClassifierAlert> expectedAlerts, boolean printAlerts) throws Exception {
		ClassifierDataSet dataSet = new ClassifierDataSet(null, null);
		CallbackErrorAnalysis analysis = new CallbackErrorAnalysis(dataSet, AMI);
		super.runTest(args, expectedAlerts, printAlerts, analysis, dataSet);
	}

	@Test
	public void testCallbackErrorHandling() throws Exception {
		String src = "./test/input/callback_error/cbe_old.js";
		String dst = "./test/input/callback_error/cbe_new.js";
		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();
		expectedAlerts.add(new CallbackErrorAlert(AMI, "printMessage", "CB", "donePrinting", "(err)", "err"));
		this.runTest(new String[] {src, dst}, expectedAlerts, true);
	}

	@Test
	public void testRealWorld() throws Exception {
		String src = "./test/input/callback_error/CLI_old.js";
		String dst = "./test/input/callback_error/CLI_new.js";
		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();
		expectedAlerts.add(new CallbackErrorAlert(AMI, "~anonymous~", "CB", "[anonymous]", "(err,processes)", "err"));
		this.runTest(new String[] {src, dst}, expectedAlerts, true);
	}

	/**
	 * This test fails right now because it's a bit limited by AST
	 * differencing. When we implement CFG analysis we should be able to
	 * make this test pass.
	 */
	@Test
	public void testMultiCheck() throws Exception {
		String src = "./test/input/callback_error/cbe_multi_check_old.js";
		String dst = "./test/input/callback_error/cbe_multi_check_new.js";
		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();
		this.runTest(new String[] {src, dst}, expectedAlerts, true);
	}

	@Test
	public void testInteractorDaemonizer() throws Exception {
		String src = "./test/input/callback_error/InteractorDaemonizer_old.js";
		String dst = "./test/input/callback_error/InteractorDaemonizer_new.js";
		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();
		this.runTest(new String[] {src, dst}, expectedAlerts, true);
	}

	@Test
	public void testCLI2() throws Exception {
		String src = "./test/input/callback_error/CLI2_old.js";
		String dst = "./test/input/callback_error/CLI2_new.js";
		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();
		this.runTest(new String[] {src, dst}, expectedAlerts, true);
	}

}