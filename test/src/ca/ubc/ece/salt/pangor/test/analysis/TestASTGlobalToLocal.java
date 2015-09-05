package ca.ubc.ece.salt.pangor.test.analysis;

import java.util.LinkedList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import ca.ubc.ece.salt.pangor.analysis.ast.GTLScopeAnalysis;
import ca.ubc.ece.salt.pangor.analysis.classify.ClassifierDataSet;
import ca.ubc.ece.salt.pangor.batch.AnalysisMetaInformation;
import ca.ubc.ece.salt.pangor.classify.alert.ClassifierAlert;
import ca.ubc.ece.salt.pangor.classify.alert.GlobalToLocalAlert;

@Ignore
public class TestASTGlobalToLocal extends TestAnalysis {

	private final AnalysisMetaInformation AMI = new AnalysisMetaInformation(0, 0, "test", "homepage", "src file", "dst file", "src commit", "dst commit", "src code", "dst code");

	private void runTest(String[] args, List<ClassifierAlert> expectedAlerts, boolean printAlerts) throws Exception {
		ClassifierDataSet dataSet = new ClassifierDataSet(null, null);
		GTLScopeAnalysis analysis = new GTLScopeAnalysis(dataSet, AMI);
		super.runTest(args, expectedAlerts, printAlerts, analysis, dataSet);
	}

	@Test
	public void testNotDefined() throws Exception{
		String src = "./test/input/not_defined/nd_old.js";
		String dst = "./test/input/not_defined/nd_new.js";
		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();
		expectedAlerts.add(new GlobalToLocalAlert(AMI, "helloWorld", "AST_GTL", "a"));
		this.runTest(new String[] {src, dst}, expectedAlerts, true);
	}

	@Test
	public void testFieldAccess() throws Exception {
		String src = "./test/input/not_defined/nd_field_old.js";
		String dst = "./test/input/not_defined/nd_field_new.js";
		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();
		expectedAlerts.add(new GlobalToLocalAlert(AMI, "~script~", "AST_GTL", "a"));
		this.runTest(new String[] {src, dst}, expectedAlerts, false);
	}

	@Test
	public void testUsedVariable() throws Exception {
		String src = "./test/input/not_defined/nd_used_variable_old.js";
		String dst = "./test/input/not_defined/nd_used_variable_new.js";
		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();
		this.runTest(new String[] {src, dst}, expectedAlerts, false);
	}

	@Test
	public void testUsedField() throws Exception {
		String src = "./test/input/not_defined/nd_used_field_old.js";
		String dst = "./test/input/not_defined/nd_used_field_new.js";
		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();
		this.runTest(new String[] {src, dst}, expectedAlerts, false);
	}

	@Test
	public void testUsedCall() throws Exception {
		String src = "./test/input/not_defined/nd_used_call_old.js";
		String dst = "./test/input/not_defined/nd_used_call_new.js";
		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();
		this.runTest(new String[] {src, dst}, expectedAlerts, false);
	}

	@Test
	public void testCallField() throws Exception {
		String src = "./test/input/not_defined/CliUx_old.js";
		String dst = "./test/input/not_defined/CliUx_new.js";
		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();
		this.runTest(new String[] {src, dst}, expectedAlerts, false);
	}

	@Test
	public void testDeletedHigher() throws Exception {
		String src = "./test/input/not_defined/nd_deleted_higher_old.js";
		String dst = "./test/input/not_defined/nd_deleted_higher_new.js";
		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();
		this.runTest(new String[] {src, dst}, expectedAlerts, false);
	}

	@Test
	public void testNested() throws Exception {
		String src = "./test/input/not_defined/nd_nested_old.js";
		String dst = "./test/input/not_defined/nd_nested_new.js";
		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();
		expectedAlerts.add(new GlobalToLocalAlert(AMI, "helloWorld", "AST_GTL", "i"));
		this.runTest(new String[] {src, dst}, expectedAlerts, false);
	}

	/**
	 * This gives a false positive. The developer initializes 'fs' to the same
	 * thing twice... which is kind of a bad practice bug on their part.
	 */
	@Test
	public void testProcessContainer() throws Exception {
		String src = "./test/input/not_defined/ProcessContainer_old.js";
		String dst = "./test/input/not_defined/ProcessContainer_new.js";
		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();
		this.runTest(new String[] {src, dst}, expectedAlerts, false);
	}

	@Test
	public void testCLI1() throws Exception {
		String src = "./test/input/not_defined/CLI1_old.js";
		String dst = "./test/input/not_defined/CLI1_new.js";
		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();
		this.runTest(new String[] {src, dst}, expectedAlerts, false);
	}

}