package ca.ubc.ece.salt.pangor.test.analysis;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import ca.ubc.ece.salt.pangor.analysis.ast.STHScopeAnalysis;
import ca.ubc.ece.salt.pangor.analysis.classify.ClassifierDataSet;
import ca.ubc.ece.salt.pangor.batch.AnalysisMetaInformation;
import ca.ubc.ece.salt.pangor.classify.alert.ClassifierAlert;
import ca.ubc.ece.salt.pangor.classify.alert.SpecialTypeAlert;
import ca.ubc.ece.salt.pangor.classify.alert.SpecialTypeAlert.SpecialType;

public class TestASTSpecialTypeHandling extends TestAnalysis {

	private final AnalysisMetaInformation AMI = new AnalysisMetaInformation(0, 0, "test", "homepage", "src file", "dst file", "src commit", "dst commit", "src code", "dst code");

	private void runTest(String[] args, List<ClassifierAlert> expectedAlerts, boolean printAlerts) throws Exception {
		ClassifierDataSet dataSet = new ClassifierDataSet(null, null);
		STHScopeAnalysis analysis = new STHScopeAnalysis(dataSet, AMI);
		super.runTest(args, expectedAlerts, printAlerts, analysis, dataSet);
	}

	@Test
	public void testUndefined() throws Exception{
		String src = "./test/input/special_type_handling/sth_undefined_old.js";
		String dst = "./test/input/special_type_handling/sth_undefined_new.js";
		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();
		expectedAlerts.add(new SpecialTypeAlert(AMI, "~script~", "NF_STH", "a", SpecialType.UNDEFINED));
		this.runTest(new String[] {src, dst}, expectedAlerts, false);
	}

	@Test
	public void testTypeOf() throws Exception {
		String src = "./test/input/special_type_handling/sth_undefined_typeof_old.js";
		String dst = "./test/input/special_type_handling/sth_undefined_typeof_new.js";
		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();
		expectedAlerts.add(new SpecialTypeAlert(AMI, "~script~", "NF_STH", "a", SpecialType.UNDEFINED));
		this.runTest(new String[] {src, dst}, expectedAlerts, false);
	}

	@Test
	public void testUndefinedReturn() throws Exception{
		String src = "./test/input/special_type_handling/sth_undefined_return_old.js";
		String dst = "./test/input/special_type_handling/sth_undefined_return_new.js";
		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();
		expectedAlerts.add(new SpecialTypeAlert(AMI, "~script~", "NF_STH", "a", SpecialType.UNDEFINED));
		this.runTest(new String[] {src, dst}, expectedAlerts, false);
	}

	@Test
	public void testFalsey() throws Exception{
		String src = "./test/input/special_type_handling/sth_falsey_old.js";
		String dst = "./test/input/special_type_handling/sth_falsey_new.js";
		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();
		expectedAlerts.add(new SpecialTypeAlert(AMI, "~script~", "NF_STH", "a", SpecialType.FALSEY));
		expectedAlerts.add(new SpecialTypeAlert(AMI, "~script~", "NF_STH", "b", SpecialType.FALSEY));
		expectedAlerts.add(new SpecialTypeAlert(AMI, "~script~", "NF_STH", "c", SpecialType.FALSEY));
		this.runTest(new String[] {src, dst}, expectedAlerts, true);
	}

	@Test
	public void testAllSpecialTypes() throws Exception{
		String src = "./test/input/special_type_handling/sth_all_types_old.js";
		String dst = "./test/input/special_type_handling/sth_all_types_new.js";
		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();
		expectedAlerts.add(new SpecialTypeAlert(AMI, "~script~", "NF_STH", "a", SpecialType.UNDEFINED));
		expectedAlerts.add(new SpecialTypeAlert(AMI, "~script~", "NF_STH", "b", SpecialType.NULL));
		expectedAlerts.add(new SpecialTypeAlert(AMI, "~script~", "NF_STH", "c", SpecialType.NAN));
		expectedAlerts.add(new SpecialTypeAlert(AMI, "~script~", "NF_STH", "d", SpecialType.ZERO));
		expectedAlerts.add(new SpecialTypeAlert(AMI, "~script~", "NF_STH", "e", SpecialType.BLANK));
		this.runTest(new String[] {src, dst}, expectedAlerts, true);
	}

	@Test
	public void testUndefinedField() throws Exception{
		String src = "./test/input/special_type_handling/sth_undefined_field_old.js";
		String dst = "./test/input/special_type_handling/sth_undefined_field_new.js";
		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();
		expectedAlerts.add(new SpecialTypeAlert(AMI, "~script~", "NF_STH", "a.field.value", SpecialType.UNDEFINED));
		this.runTest(new String[] {src, dst}, expectedAlerts, true);
	}

	@Test
	public void testUndefinedElementGet() throws Exception{
		String src = "./test/input/special_type_handling/sth_undefined_elementget_old.js";
		String dst = "./test/input/special_type_handling/sth_undefined_elementget_new.js";
		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();
		expectedAlerts.add(new SpecialTypeAlert(AMI, "~script~", "NF_STH", "a", SpecialType.UNDEFINED));
		this.runTest(new String[] {src, dst}, expectedAlerts, true);
	}

	@Test
	public void testUndefinedRealWorld() throws Exception{
		String src = "./test/input/special_type_handling/tv-functions-old.js";
		String dst = "./test/input/special_type_handling/tv-functions-new.js";
		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();
		expectedAlerts.add(new SpecialTypeAlert(AMI, "~script~", "NF_STH", "tvShowTitle", SpecialType.UNDEFINED));
		expectedAlerts.add(new SpecialTypeAlert(AMI, "~script~", "NF_STH", "progression", SpecialType.UNDEFINED));
		this.runTest(new String[] {src, dst}, expectedAlerts, true);
	}

	@Test
	public void testUsedDereferenced() throws Exception{
		String src = "./test/input/special_type_handling/sth_used_dereferenced_old.js";
		String dst = "./test/input/special_type_handling/sth_used_dereferenced_new.js";
		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();
		this.runTest(new String[] {src, dst}, expectedAlerts, true);
	}

	@Test
	public void testUsedArgument() throws Exception{
		String src = "./test/input/special_type_handling/sth_used_argument_old.js";
		String dst = "./test/input/special_type_handling/sth_used_argument_new.js";
		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();
		this.runTest(new String[] {src, dst}, expectedAlerts, false);
	}

	@Test
	public void testUsedFieldArgument() throws Exception{
		String src = "./test/input/special_type_handling/sth_used_field_argument_old.js";
		String dst = "./test/input/special_type_handling/sth_used_field_argument_new.js";
		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();
		this.runTest(new String[] {src, dst}, expectedAlerts, false);
	}

	@Test
	public void testUsedFieldDereferenced() throws Exception{
		String src = "./test/input/special_type_handling/sth_used_field_dereferenced_old.js";
		String dst = "./test/input/special_type_handling/sth_used_field_dereferenced_new.js";
		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();
		this.runTest(new String[] {src, dst}, expectedAlerts, false);
	}

	@Test
	public void testAssignedField() throws Exception{
		String src = "./test/input/special_type_handling/sth_assigned_field_old.js";
		String dst = "./test/input/special_type_handling/sth_assigned_field_new.js";
		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();
		this.runTest(new String[] {src, dst}, expectedAlerts, true);
	}

	@Test
	public void testUndefinedWhile() throws Exception {
		String src = "./test/input/special_type_handling/sth_undefined_while_old.js";
		String dst = "./test/input/special_type_handling/sth_undefined_while_new.js";
		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();
		expectedAlerts.add(new SpecialTypeAlert(AMI, "~script~", "NF_STH", "a", SpecialType.UNDEFINED));
		this.runTest(new String[] {src, dst}, expectedAlerts, false);
	}

	@Test
	public void testUndefinedFor() throws Exception {
		String src = "./test/input/special_type_handling/sth_undefined_for_old.js";
		String dst = "./test/input/special_type_handling/sth_undefined_for_new.js";
		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();
		expectedAlerts.add(new SpecialTypeAlert(AMI, "~script~", "NF_STH", "a", SpecialType.UNDEFINED));
		this.runTest(new String[] {src, dst}, expectedAlerts, false);
	}

	@Test
	public void testUndefinedDo() throws Exception {
		String src = "./test/input/special_type_handling/sth_undefined_do_old.js";
		String dst = "./test/input/special_type_handling/sth_undefined_do_new.js";
		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();
		expectedAlerts.add(new SpecialTypeAlert(AMI, "~script~", "NF_STH", "a", SpecialType.UNDEFINED));
		this.runTest(new String[] {src, dst}, expectedAlerts, false);
	}

	@Test
	public void testUndefinedConditional() throws Exception {
		String src = "./test/input/special_type_handling/sth_undefined_conditional_old.js";
		String dst = "./test/input/special_type_handling/sth_undefined_conditional_new.js";
		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();
		expectedAlerts.add(new SpecialTypeAlert(AMI, "~script~", "NF_STH", "a", SpecialType.UNDEFINED));
		this.runTest(new String[] {src, dst}, expectedAlerts, false);
	}

	/**
	 * TODO:
	 * This fails to produce the alert because GumTree miss-classifies the
	 * identifier. It might be possible to capture it using looser change
	 * checking (i.e., get the infix operator change type instead of the
	 * identifier change type) or post-processing the change classifications.
	 * @throws Exception
	 */
	@Test
	public void testRealWorldConditional() throws Exception {
		String src = "./test/input/special_type_handling/sample-conf_old.js";
		String dst = "./test/input/special_type_handling/sample-conf_new.js";
		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();
		expectedAlerts.add(new SpecialTypeAlert(AMI, "~script~", "NF_STH", "process.env.PM2_LOG_DATE_FORMAT", SpecialType.UNDEFINED));
		this.runTest(new String[] {src, dst}, expectedAlerts, false);
	}

	@Test
	public void testBooleanAssignment() throws Exception {
		String src = "./test/input/special_type_handling/sth_boolean_assignment_old.js";
		String dst = "./test/input/special_type_handling/sth_boolean_assignment_new.js";
		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();
		this.runTest(new String[] {src, dst}, expectedAlerts, false);
	}

	@Test
	public void testDeleteUpdate() throws Exception {
		String src = "./test/input/special_type_handling/sth_deleted_old.js";
		String dst = "./test/input/special_type_handling/sth_deleted_new.js";
		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();
		this.runTest(new String[] {src, dst}, expectedAlerts, true);
	}

	@Test
	public void testUnused() throws Exception {
		String src = "./test/input/special_type_handling/sth_not_used_old.js";
		String dst = "./test/input/special_type_handling/sth_not_used_new.js";
		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();
		this.runTest(new String[] {src, dst}, expectedAlerts, false);
	}

	@Test
	public void testInfiniteLoop() throws Exception {
		String src = "./test/input/cfg_diff/CLI_1_old.js";
		String dst = "./test/input/cfg_diff/CLI_1_new.js";
		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();
		this.runTest(new String[] {src, dst}, expectedAlerts, false);
	}

	@Test
	public void testCommon() throws Exception {
		String src = "./test/input/special_type_handling/Common_old.js";
		String dst = "./test/input/special_type_handling/Common_new.js";
		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();
		this.runTest(new String[] {src, dst}, expectedAlerts, false);
	}

	@Test
	public void testForkMode() throws Exception {
		String src = "./test/input/special_type_handling/ForkMode_old.js";
		String dst = "./test/input/special_type_handling/ForkMode_new.js";
		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();
		this.runTest(new String[] {src, dst}, expectedAlerts, true);
	}

	@Test
	public void testForkMode2() throws Exception {
		String src = "./test/input/special_type_handling/ForkMode_e_old.js";
		String dst = "./test/input/special_type_handling/ForkMode_e_new.js";
		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();
		this.runTest(new String[] {src, dst}, expectedAlerts, true);
	}

	@Test
	public void testForkMode3() throws Exception {
		String src = "./test/input/special_type_handling/ForkMode_e2_old.js";
		String dst = "./test/input/special_type_handling/ForkMode_e2_new.js";
		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();
		this.runTest(new String[] {src, dst}, expectedAlerts, true);
	}

	/**
	 * This produces a false positive. This is caused by incorrect GumTree
	 * differencing. TODO: handle it by inspecting changes at the name
	 * level instead of the infix expression level.
	 * @throws Exception
	 */
	@Test
	public void testMovieFunctions() throws Exception {
		String src = "./test/input/special_type_handling/movie-functions_old.js";
		String dst = "./test/input/special_type_handling/movie-functions_new.js";
		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();
		this.runTest(new String[] {src, dst}, expectedAlerts, true);
	}

	@Test
	public void testResolveCache() throws Exception {
		String src = "./test/input/special_type_handling/ResolveCache_old.js";
		String dst = "./test/input/special_type_handling/ResolveCache_new.js";
		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();
		this.runTest(new String[] {src, dst}, expectedAlerts, true);
	}

	@Test
	public void testProtectOther() throws Exception {
		String src = "./test/input/special_type_handling/sth_undefined_protect_other_old.js";
		String dst = "./test/input/special_type_handling/sth_undefined_protect_other_new.js";
		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();
		this.runTest(new String[] {src, dst}, expectedAlerts, true);
	}

	@Test
	public void testMusicMetadata() throws Exception {
		String src = "./test/input/special_type_handling/music-metadata_old.js";
		String dst = "./test/input/special_type_handling/music-metadata_new.js";
		List<ClassifierAlert> expectedAlerts = new LinkedList<ClassifierAlert>();
		this.runTest(new String[] {src, dst}, expectedAlerts, true);
	}

}