package ca.ubc.ece.salt.sdjsb.test.analysis;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import ca.ubc.ece.salt.sdjsb.analysis.classify.ClassifierDataSet;
import ca.ubc.ece.salt.sdjsb.analysis.errorhandling.ErrorHandlingAnalysis;
import ca.ubc.ece.salt.sdjsb.batch.AnalysisMetaInformation;
import ca.ubc.ece.salt.sdjsb.classify.alert.ClassifierAlert;
import ca.ubc.ece.salt.sdjsb.classify.alert.ErrorHandlingAlert;

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

	/* TODO: Should we make the analysis more advanced by getting a list of
	 * function identifiers which are protected by try blocks? */

}