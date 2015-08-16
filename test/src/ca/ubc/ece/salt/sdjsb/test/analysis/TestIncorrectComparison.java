package ca.ubc.ece.salt.sdjsb.test.analysis;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import ca.ubc.ece.salt.sdjsb.analysis.classify.ClassifierDataSet;
import ca.ubc.ece.salt.sdjsb.analysis.specialtype.SpecialTypeAnalysis;
import ca.ubc.ece.salt.sdjsb.batch.AnalysisMetaInformation;
import ca.ubc.ece.salt.sdjsb.classify.alert.ClassifierAlert;
import ca.ubc.ece.salt.sdjsb.classify.alert.IncorrectConditionAlert;
import ca.ubc.ece.salt.sdjsb.classify.alert.SpecialTypeAlert.SpecialType;

public class TestIncorrectComparison extends TestAnalysis {

	private final AnalysisMetaInformation AMI = new AnalysisMetaInformation(0, 0, "test", "homepage", "src file", "dst file", "src commit", "dst commit", "src code", "dst code");

	private void runTest(String[] args, List<ClassifierAlert> expectedAlerts, boolean printAlerts) throws Exception {
		ClassifierDataSet dataSet = new ClassifierDataSet(null, null);
		SpecialTypeAnalysis analysis = new SpecialTypeAnalysis(dataSet, AMI);
		super.runTest(args, expectedAlerts, printAlerts, analysis, dataSet);
	}

	@Test
	public void testFalseyToValue() throws Exception{
		String src = "./test/input/incorrect_comparison/ic_falsey_to_value_old.js";
		String dst = "./test/input/incorrect_comparison/ic_falsey_to_value_new.js";
		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();
		expectedAlerts.add(new IncorrectConditionAlert(AMI, "~script~", "IC", "a", SpecialType.FALSEY, SpecialType.NO_VALUE));
		this.runTest(new String[] {src, dst}, expectedAlerts, true);
	}

	@Test
	public void testValueToFalsey() throws Exception{
		String src = "./test/input/incorrect_comparison/ic_value_to_falsey_old.js";
		String dst = "./test/input/incorrect_comparison/ic_value_to_falsey_new.js";
		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();
		expectedAlerts.add(new IncorrectConditionAlert(AMI, "~script~", "IC", "a", SpecialType.NO_VALUE, SpecialType.FALSEY));
		this.runTest(new String[] {src, dst}, expectedAlerts, true);
	}

	@Test
	public void testFalseyToType() throws Exception{
		String src = "./test/input/incorrect_comparison/ic_falsey_to_type_old.js";
		String dst = "./test/input/incorrect_comparison/ic_falsey_to_type_new.js";
		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();
		expectedAlerts.add(new IncorrectConditionAlert(AMI, "~script~", "IC", "a", SpecialType.FALSEY, SpecialType.UNDEFINED));
		this.runTest(new String[] {src, dst}, expectedAlerts, true);
	}

	@Test
	public void testTypeToFalsey() throws Exception{
		String src = "./test/input/incorrect_comparison/ic_type_to_falsey_old.js";
		String dst = "./test/input/incorrect_comparison/ic_type_to_falsey_new.js";
		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();
		expectedAlerts.add(new IncorrectConditionAlert(AMI, "~script~", "IC", "a", SpecialType.UNDEFINED, SpecialType.FALSEY));
		this.runTest(new String[] {src, dst}, expectedAlerts, true);
	}

	@Test
	public void testValueToType() throws Exception{
		String src = "./test/input/incorrect_comparison/ic_value_to_type_old.js";
		String dst = "./test/input/incorrect_comparison/ic_value_to_type_new.js";
		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();
		expectedAlerts.add(new IncorrectConditionAlert(AMI, "~script~", "IC", "a", SpecialType.NO_VALUE, SpecialType.UNDEFINED));
		this.runTest(new String[] {src, dst}, expectedAlerts, true);
	}

	@Test
	public void testTypeToValue() throws Exception{
		String src = "./test/input/incorrect_comparison/ic_type_to_value_old.js";
		String dst = "./test/input/incorrect_comparison/ic_type_to_value_new.js";
		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();
		expectedAlerts.add(new IncorrectConditionAlert(AMI, "~script~", "IC", "a", SpecialType.UNDEFINED, SpecialType.NO_VALUE));
		this.runTest(new String[] {src, dst}, expectedAlerts, true);
	}

}