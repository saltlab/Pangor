package ca.ubc.ece.salt.pangor.test.classifiers;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import ca.ubc.ece.salt.pangor.analysis.classify.ClassifierDataSet;
import ca.ubc.ece.salt.pangor.analysis.errorhandling.ErrorHandlingAnalysis;
import ca.ubc.ece.salt.pangor.batch.AnalysisMetaInformation;
import ca.ubc.ece.salt.pangor.classify.alert.ClassifierAlert;
import ca.ubc.ece.salt.pangor.classify.alert.ErrorHandlingAlert;

public class TestErrorHandling extends TestAnalysis {

	private final AnalysisMetaInformation AMI = new AnalysisMetaInformation(0, 0, "test", "homepage", "src file", "dst file", "src commit", "dst commit", "src code", "dst code");

	private void runTest(String[] args, List<ClassifierAlert> expectedAlerts, boolean printAlerts) throws Exception {
		ClassifierDataSet dataSet = new ClassifierDataSet(null, null);
		ErrorHandlingAnalysis analysis = new ErrorHandlingAnalysis(dataSet, AMI);
		super.runTest(args, expectedAlerts, printAlerts, analysis, dataSet);
	}

	@Test
	public void testErrorHandling() throws Exception{
		String src = "./test/input/error_handling/eh_old.js";
		String dst = "./test/input/error_handling/eh_new.js";
		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();
		expectedAlerts.add(new ErrorHandlingAlert(AMI, "~script~", "EH"));
		this.runTest(new String[] {src, dst}, expectedAlerts, true);
	}

	@Test
	public void testKiwiIRC() throws Exception{
		String src = "./test/input/error_handling/kiwiirc_old.js";
		String dst = "./test/input/error_handling/kiwiirc_new.js";
		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();
		expectedAlerts.add(new ErrorHandlingAlert(AMI, "~anonymous~", "EH"));
		this.runTest(new String[] {src, dst}, expectedAlerts, true);
	}

	@Test
	public void testExpress() throws Exception{
		String src = "./test/input/error_handling/express_old.js";
		String dst = "./test/input/error_handling/express_new.js";
		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();
		expectedAlerts.add(new ErrorHandlingAlert(AMI, "trim_prefix", "EH"));
		this.runTest(new String[] {src, dst}, expectedAlerts, true);
	}

	@Test
	public void testFalsePositive() throws Exception{
		String src = "./test/input/error_handling/eh_fp_old.js";
		String dst = "./test/input/error_handling/eh_fp_new.js";
		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();
		this.runTest(new String[] {src, dst}, expectedAlerts, true);
	}

	@Test
	public void testMultiMethods() throws Exception{
		String src = "./test/input/error_handling/eh_methods_old.js";
		String dst = "./test/input/error_handling/eh_methods_new.js";
		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();
		expectedAlerts.add(new ErrorHandlingAlert(AMI, "trim_prefix", "EH"));
		this.runTest(new String[] {src, dst}, expectedAlerts, true);
	}

	@Test
	public void testAddedCall() throws Exception{
		String src = "./test/input/error_handling/eh_added_call_old.js";
		String dst = "./test/input/error_handling/eh_added_call_new.js";
		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();
		this.runTest(new String[] {src, dst}, expectedAlerts, true);
	}

	@Test
	public void testDeletedTry() throws Exception{
		String src = "./test/input/error_handling/express2_old.js";
		String dst = "./test/input/error_handling/express2_new.js";
		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();
		this.runTest(new String[] {src, dst}, expectedAlerts, true);
	}

}