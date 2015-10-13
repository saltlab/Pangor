package ca.ubc.ece.salt.pangor.test.classifiers;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import ca.ubc.ece.salt.pangor.analysis.argumentorder.ArgumentOrderAnalysis;
import ca.ubc.ece.salt.pangor.analysis.classify.ClassifierDataSet;
import ca.ubc.ece.salt.pangor.batch.AnalysisMetaInformation;
import ca.ubc.ece.salt.pangor.classify.alert.ArgumentOrderAlert;
import ca.ubc.ece.salt.pangor.classify.alert.ClassifierAlert;

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

		this.runTest(new String[] { src, dst }, expectedAlerts, false);
	}

	/*
	 * No alerts expected because opt is not an argument, so the pattern
	 * criteria is not match
	 */
	@Test
	public void testNewwww() throws Exception {
		String src = "./test/input/argument_order/newww_old.js";
		String dst = "./test/input/argument_order/newww_new.js";

		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();
		// expectedAlerts.add(new ArgumentOrderAlert(AMI, "~anonymous~", "TODO"));

		this.runTest(new String[] { src, dst }, expectedAlerts, false);
	}

	/*
	 * No alerts expected because there is only one argument. Which order are we
	 * changing?
	 */
	@Test
	public void testNodeMysql() throws Exception {
		String src = "./test/input/argument_order/node-mysql_old.js";
		String dst = "./test/input/argument_order/node-mysql_new.js";

		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();
		// expectedAlerts.add(new ArgumentOrderAlert(AMI, "PoolConfig", "TODO"));

		this.runTest(new String[] { src, dst }, expectedAlerts, false);
	}
}