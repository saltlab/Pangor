package ca.ubc.ece.salt.sdjsb.test.analysis;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import ca.ubc.ece.salt.sdjsb.analysis.argumentorder.ArgumentOrderAnalysis;
import ca.ubc.ece.salt.sdjsb.analysis.classify.ClassifierDataSet;
import ca.ubc.ece.salt.sdjsb.batch.AnalysisMetaInformation;
import ca.ubc.ece.salt.sdjsb.classify.alert.ArgumentOrderAlert;
import ca.ubc.ece.salt.sdjsb.classify.alert.ClassifierAlert;

public class TestArgumentOrder extends TestAnalysis {

	private final AnalysisMetaInformation AMI = new AnalysisMetaInformation(0, 0, "test", "homepage", "src file",
			"dst file", "src commit", "dst commit", "src code", "dst code");

	private void runTest(String[] args, List<ClassifierAlert> expectedAlerts, boolean printAlerts) throws Exception {
		ClassifierDataSet dataSet = new ClassifierDataSet(null, null);
		ArgumentOrderAnalysis analysis = new ArgumentOrderAnalysis(dataSet, AMI);
		super.runTest(args, expectedAlerts, printAlerts, analysis, dataSet);
	}

	/*
	 * Simplest scenario. Inserting if block.
	 */
	@Test
	public void testPM2() throws Exception {
		String src = "./test/input/argument_order/pm2_old.js";
		String dst = "./test/input/argument_order/pm2_new.js";

		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();
		expectedAlerts.add(new ArgumentOrderAlert(AMI, "~anonymous~", "TODO"));

		this.runTest(new String[] { src, dst }, expectedAlerts, true);
	}

	/*
	 * Also a simple scenario
	 */
	@Test
	public void testNewwww() throws Exception {
		String src = "./test/input/argument_order/newww_old.js";
		String dst = "./test/input/argument_order/newww_new.js";

		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();
		expectedAlerts.add(new ArgumentOrderAlert(AMI, "~anonymous~", "TODO"));

		this.runTest(new String[] { src, dst }, expectedAlerts, true);
	}

	/*
	 * TODO: Should this pass? To remove false positives, we could check if
	 * there are at least two parameters on the function
	 */
	@Test
	public void testNodeMysql() throws Exception {
		String src = "./test/input/argument_order/node-mysql_old.js";
		String dst = "./test/input/argument_order/node-mysql_new.js";

		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();
		expectedAlerts.add(new ArgumentOrderAlert(AMI, "PoolConfig", "TODO"));

		this.runTest(new String[] { src, dst }, expectedAlerts, true);
	}
}