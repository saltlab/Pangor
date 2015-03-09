package ca.ubc.ece.salt.sdjsb.test;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import ca.ubc.ece.salt.sdjsb.checker.SpecialTypeMap.SpecialType;
import ca.ubc.ece.salt.sdjsb.checker.alert.Alert;
import ca.ubc.ece.salt.sdjsb.checker.alert.SpecialTypeAlert;

public class TestSpecialTypeHandling extends TestSDJSB {
	
	@Test
	public void testUndefined(){
		String src = "./test/input/sth_undefined_old.js";
		String dst = "./test/input/sth_undefined_new.js";
		List<Alert> expectedAlerts = new LinkedList<Alert>();
		expectedAlerts.add(new SpecialTypeAlert("STH", "a", SpecialType.UNDEFINED));
		this.runTest(new String[] {src, dst}, expectedAlerts, false);
	}

	@Test
	public void testAllSpecialTypes(){
		String src = "./test/input/sth_all_types_old.js";
		String dst = "./test/input/sth_all_types_new.js";
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
		String src = "./test/input/sth_undefined_field_old.js";
		String dst = "./test/input/sth_undefined_field_new.js";
		List<Alert> expectedAlerts = new LinkedList<Alert>();
		expectedAlerts.add(new SpecialTypeAlert("STH", "a.field.value", SpecialType.UNDEFINED));
		this.runTest(new String[] {src, dst}, expectedAlerts, true);
	}

	@Test
	public void testUndefinedRealWorld(){
		String src = "./test/input/tv-functions-old.js";
		String dst = "./test/input/tv-functions-new.js";
		List<Alert> expectedAlerts = new LinkedList<Alert>();
		expectedAlerts.add(new SpecialTypeAlert("STH", "tvShowTitle", SpecialType.UNDEFINED));
		expectedAlerts.add(new SpecialTypeAlert("STH", "progression", SpecialType.UNDEFINED));
		this.runTest(new String[] {src, dst}, expectedAlerts, false);
	}

	@Test
	public void testUsedDereferenced(){
		String src = "./test/input/sth_used_dereferenced_old.js";
		String dst = "./test/input/sth_used_dereferenced_new.js";
		List<Alert> expectedAlerts = new LinkedList<Alert>();
		this.runTest(new String[] {src, dst}, expectedAlerts, false);
	}

	@Test
	public void testUsedArgument(){
		String src = "./test/input/sth_used_argument_old.js";
		String dst = "./test/input/sth_used_argument_new.js";
		List<Alert> expectedAlerts = new LinkedList<Alert>();
		this.runTest(new String[] {src, dst}, expectedAlerts, false);
	}

	@Test
	public void testUsedFieldArgument(){
		String src = "./test/input/sth_used_field_argument_old.js";
		String dst = "./test/input/sth_used_field_argument_new.js";
		List<Alert> expectedAlerts = new LinkedList<Alert>();
		this.runTest(new String[] {src, dst}, expectedAlerts, false);
	}

	@Test
	public void testUsedFieldDereferenced(){
		String src = "./test/input/sth_used_field_dereferenced_old.js";
		String dst = "./test/input/sth_used_field_dereferenced_new.js";
		List<Alert> expectedAlerts = new LinkedList<Alert>();
		this.runTest(new String[] {src, dst}, expectedAlerts, false);
	}

	@Test
	public void testAssignedField(){
		String src = "./test/input/sth_assigned_field_old.js";
		String dst = "./test/input/sth_assigned_field_new.js";
		List<Alert> expectedAlerts = new LinkedList<Alert>();
		this.runTest(new String[] {src, dst}, expectedAlerts, false);
	}

	@Test
	public void testUndefinedWhile(){
		String src = "./test/input/sth_undefined_while_old.js";
		String dst = "./test/input/sth_undefined_while_new.js";
		List<Alert> expectedAlerts = new LinkedList<Alert>();
		expectedAlerts.add(new SpecialTypeAlert("STH", "a", SpecialType.UNDEFINED));
		this.runTest(new String[] {src, dst}, expectedAlerts, false);
	}

	@Test
	public void testUndefinedFor(){
		String src = "./test/input/sth_undefined_for_old.js";
		String dst = "./test/input/sth_undefined_for_new.js";
		List<Alert> expectedAlerts = new LinkedList<Alert>();
		expectedAlerts.add(new SpecialTypeAlert("STH", "a", SpecialType.UNDEFINED));
		this.runTest(new String[] {src, dst}, expectedAlerts, false);
	}

	@Test
	public void testUndefinedDo(){
		String src = "./test/input/sth_undefined_do_old.js";
		String dst = "./test/input/sth_undefined_do_new.js";
		List<Alert> expectedAlerts = new LinkedList<Alert>();
		expectedAlerts.add(new SpecialTypeAlert("STH", "a", SpecialType.UNDEFINED));
		this.runTest(new String[] {src, dst}, expectedAlerts, false);
	}

	@Test
	public void testUndefinedConditional(){
		String src = "./test/input/sth_undefined_conditional_old.js";
		String dst = "./test/input/sth_undefined_conditional_new.js";
		List<Alert> expectedAlerts = new LinkedList<Alert>();
		expectedAlerts.add(new SpecialTypeAlert("STH", "a", SpecialType.UNDEFINED));
		this.runTest(new String[] {src, dst}, expectedAlerts, false);
	}

	@Test
	public void testBooleanAssignment(){
		String src = "./test/input/sth_boolean_assignment_old.js";
		String dst = "./test/input/sth_boolean_assignment_new.js";
		List<Alert> expectedAlerts = new LinkedList<Alert>();
		this.runTest(new String[] {src, dst}, expectedAlerts, false);
	}

}
