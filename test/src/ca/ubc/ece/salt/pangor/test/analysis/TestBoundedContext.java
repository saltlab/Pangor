package ca.ubc.ece.salt.pangor.test.analysis;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import ca.ubc.ece.salt.pangor.analysis.boundedcontext.BoundedContextAnalysis;
import ca.ubc.ece.salt.pangor.analysis.classify.ClassifierDataSet;
import ca.ubc.ece.salt.pangor.batch.AnalysisMetaInformation;
import ca.ubc.ece.salt.pangor.classify.alert.BoundedContextAlert;
import ca.ubc.ece.salt.pangor.classify.alert.ClassifierAlert;

public class TestBoundedContext extends TestAnalysis {

	private final AnalysisMetaInformation AMI = new AnalysisMetaInformation(0, 0, "test", "homepage", "src file",
			"dst file", "src commit", "dst commit", "src code", "dst code");

	private void runTest(String[] args, List<ClassifierAlert> expectedAlerts, boolean printAlerts) throws Exception {
		ClassifierDataSet dataSet = new ClassifierDataSet(null, null);
		BoundedContextAnalysis analysis = new BoundedContextAnalysis(dataSet, AMI);
		super.runTest(args, expectedAlerts, printAlerts, analysis, dataSet);
	}

	@Test
	public void testSimplestScenario() throws Exception {
		String src = "./test/input/wrong_bounded_context/simplest_old.js";
		String dst = "./test/input/wrong_bounded_context/simplest_new.js";

		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();
		expectedAlerts.add(new BoundedContextAlert(AMI, "foo1", "call"));
		expectedAlerts.add(new BoundedContextAlert(AMI, "foo2", "call"));
		expectedAlerts.add(new BoundedContextAlert(AMI, "object.foo3", "call"));

		this.runTest(new String[] { src, dst }, expectedAlerts, true);
	}

	/*
	 * Fake insertion in existing code from 'async' project
	 * Should output no alerts
	 */
	@Test
	public void testFakeInsertion() throws Exception {
		String src = "./test/input/wrong_bounded_context/fake_insertion_old.js";
		String dst = "./test/input/wrong_bounded_context/fake_insertion_new.js";

		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();

		this.runTest(new String[] { src, dst }, expectedAlerts, true);
	}

	/*
	 * Fake removal in existing code from 'async' project Should output no
	 * alerts
	 */
	@Test
	public void testFakeRemoval() throws Exception {
		String src = "./test/input/wrong_bounded_context/fake_removal_old.js";
		String dst = "./test/input/wrong_bounded_context/fake_removal_new.js";

		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();

		this.runTest(new String[] { src, dst }, expectedAlerts, true);
	}

	@Test
	public void testSocketIO() throws Exception {
		String src = "./test/input/wrong_bounded_context/socketio_old.js";
		String dst = "./test/input/wrong_bounded_context/socketio_new.js";

		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();
		expectedAlerts.add(new BoundedContextAlert(AMI, "env", "call"));
		expectedAlerts.add(new BoundedContextAlert(AMI, "fn", "call"));

		this.runTest(new String[] { src, dst }, expectedAlerts, true);
	}

	@Test
	public void testBluebird() throws Exception {
		String src = "./test/input/wrong_bounded_context/bluebird_old.js";
		String dst = "./test/input/wrong_bounded_context/bluebird_new.js";

		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();
		expectedAlerts.add(new BoundedContextAlert(AMI, "AsyncSettlePromises", "call"));

		this.runTest(new String[] { src, dst }, expectedAlerts, true);
	}

	@Test
	public void testBluebirdChange() throws Exception {
		String src = "./test/input/wrong_bounded_context/bluebird_change_old.js";
		String dst = "./test/input/wrong_bounded_context/bluebird_change_new.js";

		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();

		this.runTest(new String[] { src, dst }, expectedAlerts, true);
	}

	@Test
	public void testChalk() throws Exception {
		String src = "./test/input/wrong_bounded_context/chalk_old.js";
		String dst = "./test/input/wrong_bounded_context/chalk_new.js";

		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();
		expectedAlerts.add(new BoundedContextAlert(AMI, "build", "call"));

		this.runTest(new String[] { src, dst }, expectedAlerts, true);
	}

	/*
	 * .bind being inserted after .then()
	 *
	 * Tricky edge-case, because FunctionNode is transformed into FunctionCall,
	 * but we don't keep track of FunctionNodes
	 */
	@Test
	public void testCordovaCli() throws Exception {
		String src = "./test/input/wrong_bounded_context/cordova-cli_old.js";
		String dst = "./test/input/wrong_bounded_context/cordova-cli_new.js";

		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();
		expectedAlerts.add(new BoundedContextAlert(AMI, "~anonymous~", "bind"));

		this.runTest(new String[] { src, dst }, expectedAlerts, true);
	}

	/*
	 * .bind being inserted.
	 *
	 * Tricky edge-case, because Name is transformed into FunctionCall, but we
	 * don't keep track of Name
	 */
	@Test
	public void testFacebookMessenger() throws Exception {
		String src = "./test/input/wrong_bounded_context/facebook_messenger_old.js";
		String dst = "./test/input/wrong_bounded_context/facebook_messenger_new.js";

		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();
		expectedAlerts.add(new BoundedContextAlert(AMI, "this.prompt", "bind"));

		this.runTest(new String[] { src, dst }, expectedAlerts, true);
	}

	/*
	 * Converts redirecToRoot to redirectToRoot.bind(this).
	 *
	 * Tricky edge-case, because Name is transformed into FunctionCall, but we
	 * don't keep track of Name
	 */
	@Test
	public void testNodeInspector() throws Exception {
		String src = "./test/input/wrong_bounded_context/node-inspector_old.js";
		String dst = "./test/input/wrong_bounded_context/node-inspector_new.js";

		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();
		expectedAlerts.add(new BoundedContextAlert(AMI, "redirectToRoot", "bind"));

		this.runTest(new String[] { src, dst }, expectedAlerts, true);
	}

	/*
	 * Test false positive.
	 *
	 * TODO: Failing test because of other occurrences of callback(). Maybe we
	 * can fix it if we inspect scopes
	 */
	@Test
	public void testNodeInspectorInsertion() throws Exception {
		String src = "./test/input/wrong_bounded_context/node-inspector_insertion_old.js";
		String dst = "./test/input/wrong_bounded_context/node-inspector_insertion_new.js";

		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();

		this.runTest(new String[] { src, dst }, expectedAlerts, true);
	}
}