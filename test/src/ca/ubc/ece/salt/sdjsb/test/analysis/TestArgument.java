package ca.ubc.ece.salt.sdjsb.test.analysis;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode.ChangeType;
import ca.ubc.ece.salt.sdjsb.analysis.argument.ArgumentAnalysis;
import ca.ubc.ece.salt.sdjsb.analysis.classify.ClassifierDataSet;
import ca.ubc.ece.salt.sdjsb.batch.AnalysisMetaInformation;
import ca.ubc.ece.salt.sdjsb.classify.alert.ArgumentAlert;
import ca.ubc.ece.salt.sdjsb.classify.alert.ClassifierAlert;

public class TestArgument extends TestAnalysis {

	private final AnalysisMetaInformation AMI = new AnalysisMetaInformation(0, 0, "test", "homepage", "src file",
			"dst file", "src commit", "dst commit", "src code", "dst code");

	private void runTest(String[] args, List<ClassifierAlert> expectedAlerts, boolean printAlerts) throws Exception {
		ClassifierDataSet dataSet = new ClassifierDataSet(null, null);
		ArgumentAnalysis analysis = new ArgumentAnalysis(dataSet, AMI);
		super.runTest(args, expectedAlerts, printAlerts, analysis, dataSet);
	}

	@Test
	public void testSimplestScenario() throws Exception {
		String src = "./test/input/argument/simplest_old.js";
		String dst = "./test/input/argument/simplest_new.js";

		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();
		expectedAlerts.add(new ArgumentAlert(AMI, "lmao", "string", ChangeType.INSERTED));
		expectedAlerts.add(new ArgumentAlert(AMI, "ca", "3", ChangeType.REMOVED));

		this.runTest(new String[] { src, dst }, expectedAlerts, true);
	}

	/*
	 * pm2 false positive 1: function removed
	 */
	@Test
	public void testPM2FP1() throws Exception {
		String src = "./test/input/argument/pm2_fp1_old.js";
		String dst = "./test/input/argument/pm2_fp1_new.js";

		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();

		this.runTest(new String[] { src, dst }, expectedAlerts, true);
	}


	/*
	 * pm2 false positive 2: function call substituted by another function call
	 */
	@Test
	public void testPM2FP2() throws Exception {
		String src = "./test/input/argument/pm2_fp2_old.js";
		String dst = "./test/input/argument/pm2_fp2_new.js";

		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();

		this.runTest(new String[] { src, dst }, expectedAlerts, true);
	}

	/*
	 * Changing a field of an object literal that is used as parameter
	 */
	@Test
	public void testObjectLiteral() throws Exception {
		String src = "./test/input/argument/object_literal_change_old.js";
		String dst = "./test/input/argument/object_literal_change_new.js";

		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();
		expectedAlerts.add(new ArgumentAlert(AMI, "watch", "~objectLiteral~", ChangeType.UPDATED));

		this.runTest(new String[] { src, dst }, expectedAlerts, true);
	}

	/*
	 * Changing order of arguments
	 */
	@Test
	public void testChangingOrder() throws Exception {
		String src = "./test/input/argument/change_order_old.js";
		String dst = "./test/input/argument/change_order_new.js";

		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();
		expectedAlerts.add(new ArgumentAlert(AMI, "watch", "~objectLiteral~", ChangeType.UPDATED));

		this.runTest(new String[] { src, dst }, expectedAlerts, true);
	}

	/*
	 * False positive: function refactoring
	 */
	@Test
	public void testFunctionRefactoring() throws Exception {
		String src = "./test/input/argument/function_refactor_old.js";
		String dst = "./test/input/argument/function_refactor_new.js";

		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();

		this.runTest(new String[] { src, dst }, expectedAlerts, true);
	}

	/*
	 * False positive: event
	 */
	@Test
	public void testEvent() throws Exception {
		String src = "./test/input/argument/event_old.js";
		String dst = "./test/input/argument/event_new.js";

		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();

		this.runTest(new String[] { src, dst }, expectedAlerts, true);
	}

	/*
	 * Test how we print the function name. On chain function calls, we were
	 * printing everything, which breaks the .csv file
	 */
	@Test
	public void testChain() throws Exception {
		String src = "./test/input/argument/chain_old.js";
		String dst = "./test/input/argument/chain_new.js";

		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();
		expectedAlerts.add(new ArgumentAlert(AMI, "insertAfter", "~objectLiteral~", ChangeType.UPDATED));

		this.runTest(new String[] { src, dst }, expectedAlerts, true);
	}

	/*
	 * Test how we print the function name. On functions assingments as a param
	 */
	@Test
	public void testFunctionAssignment() throws Exception {
		String src = "./test/input/argument/function_assign_old.js";
		String dst = "./test/input/argument/function_assign_new.js";

		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();
		expectedAlerts.add(new ArgumentAlert(AMI, "attachEvent", "~expression~", ChangeType.INSERTED));

		this.runTest(new String[] { src, dst }, expectedAlerts, true);
	}

	/*
	 * Test how we print the function name. On functions expressions as a param
	 */
	@Test
	public void testExpression() throws Exception {
		String src = "./test/input/argument/expression_old.js";
		String dst = "./test/input/argument/expression_new.js";

		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();
		expectedAlerts.add(new ArgumentAlert(AMI, "set", "~expression~", ChangeType.INSERTED));

		this.runTest(new String[] { src, dst }, expectedAlerts, true);
	}
}