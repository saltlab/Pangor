package ca.ubc.ece.salt.sdjsb.test.analysis;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import ca.ubc.ece.salt.sdjsb.alert.Alert;
import ca.ubc.ece.salt.sdjsb.alert.SpecialTypeAlert;
import ca.ubc.ece.salt.sdjsb.alert.SpecialTypeAlert.SpecialType;
import ca.ubc.ece.salt.sdjsb.analysis.FlowAnalysis;
import ca.ubc.ece.salt.sdjsb.analysis.specialtype.SpecialTypeAnalysis;
import ca.ubc.ece.salt.sdjsb.analysis.specialtype.SpecialTypeLatticeElement;

public class TestSpecialTypeHandling extends TestAnalysis {
	
	private void runTest(String[] args, List<Alert> expectedAlerts, boolean printAlerts) throws Exception {
		FlowAnalysis<SpecialTypeLatticeElement> analysis = new SpecialTypeAnalysis();
		super.runTest(args, expectedAlerts, printAlerts, analysis);
	}

	@Test
	public void testUndefined() throws Exception{
		String src = "./test/input/special_type_handling/sth_undefined_old.js";
		String dst = "./test/input/special_type_handling/sth_undefined_new.js";
		List<Alert> expectedAlerts = new LinkedList<Alert>();
		expectedAlerts.add(new SpecialTypeAlert("STH", "a", SpecialType.UNDEFINED));
		this.runTest(new String[] {src, dst}, expectedAlerts, false);
	}

	@Test
	public void testUndefinedReturn() throws Exception{
		String src = "./test/input/special_type_handling/sth_undefined_return_old.js";
		String dst = "./test/input/special_type_handling/sth_undefined_return_new.js";
		List<Alert> expectedAlerts = new LinkedList<Alert>();
		expectedAlerts.add(new SpecialTypeAlert("STH", "a", SpecialType.UNDEFINED));
		this.runTest(new String[] {src, dst}, expectedAlerts, false);
	}

	@Test
	public void testFalsey() throws Exception{
		String src = "./test/input/special_type_handling/sth_falsey_old.js";
		String dst = "./test/input/special_type_handling/sth_falsey_new.js";
		List<Alert> expectedAlerts = new LinkedList<Alert>();
		expectedAlerts.add(new SpecialTypeAlert("STH", "a", SpecialType.FALSEY));
		expectedAlerts.add(new SpecialTypeAlert("STH", "b", SpecialType.FALSEY));
		expectedAlerts.add(new SpecialTypeAlert("STH", "c", SpecialType.FALSEY));
		this.runTest(new String[] {src, dst}, expectedAlerts, false);
	}

	@Test
	public void testAllSpecialTypes() throws Exception{
		String src = "./test/input/special_type_handling/sth_all_types_old.js";
		String dst = "./test/input/special_type_handling/sth_all_types_new.js";
		List<Alert> expectedAlerts = new LinkedList<Alert>();
		expectedAlerts.add(new SpecialTypeAlert("STH", "a", SpecialType.UNDEFINED));
		expectedAlerts.add(new SpecialTypeAlert("STH", "b", SpecialType.NULL));
		expectedAlerts.add(new SpecialTypeAlert("STH", "c", SpecialType.NAN));
		expectedAlerts.add(new SpecialTypeAlert("STH", "d", SpecialType.ZERO));
		expectedAlerts.add(new SpecialTypeAlert("STH", "e", SpecialType.BLANK));
		this.runTest(new String[] {src, dst}, expectedAlerts, true);
	}

	@Test
	public void testUndefinedField() throws Exception{
		String src = "./test/input/special_type_handling/sth_undefined_field_old.js";
		String dst = "./test/input/special_type_handling/sth_undefined_field_new.js";
		List<Alert> expectedAlerts = new LinkedList<Alert>();
		expectedAlerts.add(new SpecialTypeAlert("STH", "a.field.value", SpecialType.UNDEFINED));
		this.runTest(new String[] {src, dst}, expectedAlerts, true);
	}

	@Test
	public void testUndefinedElementGet() throws Exception{
		String src = "./test/input/special_type_handling/sth_undefined_elementget_old.js";
		String dst = "./test/input/special_type_handling/sth_undefined_elementget_new.js";
		List<Alert> expectedAlerts = new LinkedList<Alert>();
		expectedAlerts.add(new SpecialTypeAlert("STH", "a", SpecialType.UNDEFINED));
		this.runTest(new String[] {src, dst}, expectedAlerts, true);
	}

	@Test
	public void testUndefinedRealWorld() throws Exception{
		String src = "./test/input/special_type_handling/tv-functions-old.js";
		String dst = "./test/input/special_type_handling/tv-functions-new.js";
		List<Alert> expectedAlerts = new LinkedList<Alert>();
		expectedAlerts.add(new SpecialTypeAlert("STH", "tvShowTitle", SpecialType.UNDEFINED));
		expectedAlerts.add(new SpecialTypeAlert("STH", "progression", SpecialType.UNDEFINED));
		this.runTest(new String[] {src, dst}, expectedAlerts, true);
	}

	@Test
	public void testUsedDereferenced() throws Exception{
		String src = "./test/input/special_type_handling/sth_used_dereferenced_old.js";
		String dst = "./test/input/special_type_handling/sth_used_dereferenced_new.js";
		List<Alert> expectedAlerts = new LinkedList<Alert>();
		this.runTest(new String[] {src, dst}, expectedAlerts, true);
	}

	@Test
	public void testUsedArgument() throws Exception{
		String src = "./test/input/special_type_handling/sth_used_argument_old.js";
		String dst = "./test/input/special_type_handling/sth_used_argument_new.js";
		List<Alert> expectedAlerts = new LinkedList<Alert>();
		this.runTest(new String[] {src, dst}, expectedAlerts, false);
	}

	@Test
	public void testUsedFieldArgument() throws Exception{
		String src = "./test/input/special_type_handling/sth_used_field_argument_old.js";
		String dst = "./test/input/special_type_handling/sth_used_field_argument_new.js";
		List<Alert> expectedAlerts = new LinkedList<Alert>();
		this.runTest(new String[] {src, dst}, expectedAlerts, false);
	}

	@Test
	public void testUsedFieldDereferenced() throws Exception{
		String src = "./test/input/special_type_handling/sth_used_field_dereferenced_old.js";
		String dst = "./test/input/special_type_handling/sth_used_field_dereferenced_new.js";
		List<Alert> expectedAlerts = new LinkedList<Alert>();
		this.runTest(new String[] {src, dst}, expectedAlerts, false);
	}

	@Test
	public void testAssignedField() throws Exception{
		String src = "./test/input/special_type_handling/sth_assigned_field_old.js";
		String dst = "./test/input/special_type_handling/sth_assigned_field_new.js";
		List<Alert> expectedAlerts = new LinkedList<Alert>();
		this.runTest(new String[] {src, dst}, expectedAlerts, true);
	}

	@Test
	public void testUndefinedWhile() throws Exception {
		String src = "./test/input/special_type_handling/sth_undefined_while_old.js";
		String dst = "./test/input/special_type_handling/sth_undefined_while_new.js";
		List<Alert> expectedAlerts = new LinkedList<Alert>();
		expectedAlerts.add(new SpecialTypeAlert("STH", "a", SpecialType.UNDEFINED));
		this.runTest(new String[] {src, dst}, expectedAlerts, false);
	}

	@Test
	public void testUndefinedFor() throws Exception {
		String src = "./test/input/special_type_handling/sth_undefined_for_old.js";
		String dst = "./test/input/special_type_handling/sth_undefined_for_new.js";
		List<Alert> expectedAlerts = new LinkedList<Alert>();
		expectedAlerts.add(new SpecialTypeAlert("STH", "a", SpecialType.UNDEFINED));
		this.runTest(new String[] {src, dst}, expectedAlerts, false);
	}

	@Test
	public void testUndefinedDo() throws Exception {
		String src = "./test/input/special_type_handling/sth_undefined_do_old.js";
		String dst = "./test/input/special_type_handling/sth_undefined_do_new.js";
		List<Alert> expectedAlerts = new LinkedList<Alert>();
		expectedAlerts.add(new SpecialTypeAlert("STH", "a", SpecialType.UNDEFINED));
		this.runTest(new String[] {src, dst}, expectedAlerts, false);
	}

	@Test
	public void testUndefinedConditional() throws Exception {
		String src = "./test/input/special_type_handling/sth_undefined_conditional_old.js";
		String dst = "./test/input/special_type_handling/sth_undefined_conditional_new.js";
		List<Alert> expectedAlerts = new LinkedList<Alert>();
		expectedAlerts.add(new SpecialTypeAlert("STH", "a", SpecialType.UNDEFINED));
		this.runTest(new String[] {src, dst}, expectedAlerts, false);
	}

	@Test
	public void testRealWorldConditional() throws Exception {
		String src = "./test/input/special_type_handling/sample-conf_old.js";
		String dst = "./test/input/special_type_handling/sample-conf_new.js";
		List<Alert> expectedAlerts = new LinkedList<Alert>();
		expectedAlerts.add(new SpecialTypeAlert("STH", "process.env.PM2_LOG_DATE_FORMAT", SpecialType.UNDEFINED));
		this.runTest(new String[] {src, dst}, expectedAlerts, false);
	}

	@Test
	public void testBooleanAssignment() throws Exception {
		String src = "./test/input/special_type_handling/sth_boolean_assignment_old.js";
		String dst = "./test/input/special_type_handling/sth_boolean_assignment_new.js";
		List<Alert> expectedAlerts = new LinkedList<Alert>();
		this.runTest(new String[] {src, dst}, expectedAlerts, false);
	}

	/**
	 * TODO
	 * This produces a false positive. We can catch it by performing this
	 * analysis on the source file, except replacing "INSERT" with "DELETE". We
	 * then filter out any alerts that match for the source and destination.
	 * 
	 * This means we need to have some sort of meta analysis that can run an
	 * analysis on both source and destination files and then compare the 
	 * results...
	 */
	@Test
	public void testDeleteUpdate() throws Exception {
		String src = "./test/input/special_type_handling/sth_deleted_old.js";
		String dst = "./test/input/special_type_handling/sth_deleted_new.js";
		List<Alert> expectedAlerts = new LinkedList<Alert>();
		this.runTest(new String[] {src, dst}, expectedAlerts, true);
	}

	@Test
	public void testUnused() throws Exception {
		String src = "./test/input/special_type_handling/sth_not_used_old.js";
		String dst = "./test/input/special_type_handling/sth_not_used_new.js";
		List<Alert> expectedAlerts = new LinkedList<Alert>();
		this.runTest(new String[] {src, dst}, expectedAlerts, false);
	}

}