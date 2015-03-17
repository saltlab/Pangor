package ca.ubc.ece.salt.sdjsb.test;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import ca.ubc.ece.salt.sdjsb.checker.Alert;
import ca.ubc.ece.salt.sdjsb.checker.specialtype.SpecialTypeAlert;
import ca.ubc.ece.salt.sdjsb.checker.specialtype.SpecialTypeMap.SpecialType;

public class TestSpecialTypeHandling extends TestSDJSB {
	
	@Test
	public void testUndefined(){
		String src = "./test/input/special_type_handling/sth_undefined_old.js";
		String dst = "./test/input/special_type_handling/sth_undefined_new.js";
		List<Alert> expectedAlerts = new LinkedList<Alert>();
		expectedAlerts.add(new SpecialTypeAlert("STH", "a", SpecialType.UNDEFINED));
		this.runTest(new String[] {src, dst}, expectedAlerts, false);
	}

	@Test
	public void testFalsey(){
		String src = "./test/input/special_type_handling/sth_falsey_old.js";
		String dst = "./test/input/special_type_handling/sth_falsey_new.js";
		List<Alert> expectedAlerts = new LinkedList<Alert>();
		expectedAlerts.add(new SpecialTypeAlert("STH", "a", SpecialType.FALSEY));
		expectedAlerts.add(new SpecialTypeAlert("STH", "b", SpecialType.FALSEY));
		expectedAlerts.add(new SpecialTypeAlert("STH", "c", SpecialType.FALSEY));
		this.runTest(new String[] {src, dst}, expectedAlerts, false);
	}

	@Test
	public void testAllSpecialTypes(){
		String src = "./test/input/special_type_handling/sth_all_types_old.js";
		String dst = "./test/input/special_type_handling/sth_all_types_new.js";
		List<Alert> expectedAlerts = new LinkedList<Alert>();
		expectedAlerts.add(new SpecialTypeAlert("STH", "a", SpecialType.UNDEFINED));
		expectedAlerts.add(new SpecialTypeAlert("STH", "b", SpecialType.NULL));
		expectedAlerts.add(new SpecialTypeAlert("STH", "c", SpecialType.NAN));
		expectedAlerts.add(new SpecialTypeAlert("STH", "d", SpecialType.ZERO));
		expectedAlerts.add(new SpecialTypeAlert("STH", "e", SpecialType.BLANK));
		this.runTest(new String[] {src, dst}, expectedAlerts, false);
	}

	@Test
	public void testUndefinedField(){
		String src = "./test/input/special_type_handling/sth_undefined_field_old.js";
		String dst = "./test/input/special_type_handling/sth_undefined_field_new.js";
		List<Alert> expectedAlerts = new LinkedList<Alert>();
		expectedAlerts.add(new SpecialTypeAlert("STH", "a.field.value", SpecialType.UNDEFINED));
		this.runTest(new String[] {src, dst}, expectedAlerts, true);
	}

	@Test
	public void testUndefinedElementGet(){
		String src = "./test/input/special_type_handling/sth_undefined_elementget_old.js";
		String dst = "./test/input/special_type_handling/sth_undefined_elementget_new.js";
		List<Alert> expectedAlerts = new LinkedList<Alert>();
		expectedAlerts.add(new SpecialTypeAlert("STH", "a", SpecialType.UNDEFINED));
		this.runTest(new String[] {src, dst}, expectedAlerts, false);
	}

	@Test
	public void testUndefinedRealWorld(){
		String src = "./test/input/special_type_handling/tv-functions-old.js";
		String dst = "./test/input/special_type_handling/tv-functions-new.js";
		List<Alert> expectedAlerts = new LinkedList<Alert>();
		expectedAlerts.add(new SpecialTypeAlert("STH", "tvShowTitle", SpecialType.UNDEFINED));
		expectedAlerts.add(new SpecialTypeAlert("STH", "progression", SpecialType.UNDEFINED));
		this.runTest(new String[] {src, dst}, expectedAlerts, false);
	}

	@Test
	public void testUsedDereferenced(){
		String src = "./test/input/special_type_handling/sth_used_dereferenced_old.js";
		String dst = "./test/input/special_type_handling/sth_used_dereferenced_new.js";
		List<Alert> expectedAlerts = new LinkedList<Alert>();
		this.runTest(new String[] {src, dst}, expectedAlerts, false);
	}

	@Test
	public void testUsedArgument(){
		String src = "./test/input/special_type_handling/sth_used_argument_old.js";
		String dst = "./test/input/special_type_handling/sth_used_argument_new.js";
		List<Alert> expectedAlerts = new LinkedList<Alert>();
		this.runTest(new String[] {src, dst}, expectedAlerts, false);
	}

	@Test
	public void testUsedFieldArgument(){
		String src = "./test/input/special_type_handling/sth_used_field_argument_old.js";
		String dst = "./test/input/special_type_handling/sth_used_field_argument_new.js";
		List<Alert> expectedAlerts = new LinkedList<Alert>();
		this.runTest(new String[] {src, dst}, expectedAlerts, false);
	}

	@Test
	public void testUsedFieldDereferenced(){
		String src = "./test/input/special_type_handling/sth_used_field_dereferenced_old.js";
		String dst = "./test/input/special_type_handling/sth_used_field_dereferenced_new.js";
		List<Alert> expectedAlerts = new LinkedList<Alert>();
		this.runTest(new String[] {src, dst}, expectedAlerts, false);
	}

	@Test
	public void testAssignedField(){
		String src = "./test/input/special_type_handling/sth_assigned_field_old.js";
		String dst = "./test/input/special_type_handling/sth_assigned_field_new.js";
		List<Alert> expectedAlerts = new LinkedList<Alert>();
		this.runTest(new String[] {src, dst}, expectedAlerts, false);
	}

	@Test
	public void testUndefinedWhile(){
		String src = "./test/input/special_type_handling/sth_undefined_while_old.js";
		String dst = "./test/input/special_type_handling/sth_undefined_while_new.js";
		List<Alert> expectedAlerts = new LinkedList<Alert>();
		expectedAlerts.add(new SpecialTypeAlert("STH", "a", SpecialType.UNDEFINED));
		this.runTest(new String[] {src, dst}, expectedAlerts, false);
	}

	@Test
	public void testUndefinedFor(){
		String src = "./test/input/special_type_handling/sth_undefined_for_old.js";
		String dst = "./test/input/special_type_handling/sth_undefined_for_new.js";
		List<Alert> expectedAlerts = new LinkedList<Alert>();
		expectedAlerts.add(new SpecialTypeAlert("STH", "a", SpecialType.UNDEFINED));
		this.runTest(new String[] {src, dst}, expectedAlerts, false);
	}

	@Test
	public void testUndefinedDo(){
		String src = "./test/input/special_type_handling/sth_undefined_do_old.js";
		String dst = "./test/input/special_type_handling/sth_undefined_do_new.js";
		List<Alert> expectedAlerts = new LinkedList<Alert>();
		expectedAlerts.add(new SpecialTypeAlert("STH", "a", SpecialType.UNDEFINED));
		this.runTest(new String[] {src, dst}, expectedAlerts, false);
	}

	@Test
	public void testUndefinedConditional(){
		String src = "./test/input/special_type_handling/sth_undefined_conditional_old.js";
		String dst = "./test/input/special_type_handling/sth_undefined_conditional_new.js";
		List<Alert> expectedAlerts = new LinkedList<Alert>();
		expectedAlerts.add(new SpecialTypeAlert("STH", "a", SpecialType.UNDEFINED));
		this.runTest(new String[] {src, dst}, expectedAlerts, false);
	}

	@Test
	public void testRealWorldConditional(){
		String src = "./test/input/special_type_handling/sample-conf_old.js";
		String dst = "./test/input/special_type_handling/sample-conf_new.js";
		List<Alert> expectedAlerts = new LinkedList<Alert>();
		expectedAlerts.add(new SpecialTypeAlert("STH", "process.env.PM2_LOG_DATE_FORMAT", SpecialType.UNDEFINED));
		this.runTest(new String[] {src, dst}, expectedAlerts, false);
	}

	@Test
	public void testBooleanAssignment(){
		String src = "./test/input/special_type_handling/sth_boolean_assignment_old.js";
		String dst = "./test/input/special_type_handling/sth_boolean_assignment_new.js";
		List<Alert> expectedAlerts = new LinkedList<Alert>();
		this.runTest(new String[] {src, dst}, expectedAlerts, false);
	}

	@Test
	public void testDeleteUpdate(){
		String src = "./test/input/special_type_handling/sth_deleted_old.js";
		String dst = "./test/input/special_type_handling/sth_deleted_new.js";
		List<Alert> expectedAlerts = new LinkedList<Alert>();
		this.runTest(new String[] {src, dst}, expectedAlerts, false);
	}

	@Test
	public void testUnused(){
		String src = "./test/input/special_type_handling/sth_not_used_old.js";
		String dst = "./test/input/special_type_handling/sth_not_used_new.js";
		List<Alert> expectedAlerts = new LinkedList<Alert>();
		this.runTest(new String[] {src, dst}, expectedAlerts, false);
	}

	/**
	 * This fails, but it's actually GumTree's fault and there's not much we can do.
	 */
//	@Test
//	public void testUsedInCondition(){
//		String src = "./test/input/special_type_handling/CLI_old.js";
//		String dst = "./test/input/special_type_handling/CLI_new.js";
//		List<Alert> expectedAlerts = new LinkedList<Alert>();
//		this.runTest(new String[] {src, dst}, expectedAlerts, false);
//	}

}
