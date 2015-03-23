package ca.ubc.ece.salt.sdjsb.test;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import ca.ubc.ece.salt.sdjsb.checker.Alert;
import ca.ubc.ece.salt.sdjsb.checker.notdefined.NotDefinedAlert;

public class TestNotDefined extends TestSDJSB {
	
	private void runTest(String[] args, List<Alert> expectedAlerts, boolean printAlerts) {
		List<String> checkers = new LinkedList<String>();
		checkers.add("ca.ubc.ece.salt.sdjsb.checker.notdefined.NotDefinedChecker");
		super.runTest(args, checkers, expectedAlerts, printAlerts);
	}
	
	@Test
	public void testNotDefined(){
		String src = "./test/input/not_defined/nd_old.js";
		String dst = "./test/input/not_defined/nd_new.js";
		List<Alert> expectedAlerts = new LinkedList<Alert>();
		expectedAlerts.add(new NotDefinedAlert("ND", "a"));
		this.runTest(new String[] {src, dst}, expectedAlerts, false);
	}

	@Test
	public void testFieldAccess(){
		String src = "./test/input/not_defined/nd_field_old.js";
		String dst = "./test/input/not_defined/nd_field_new.js";
		List<Alert> expectedAlerts = new LinkedList<Alert>();
		expectedAlerts.add(new NotDefinedAlert("ND", "a"));
		this.runTest(new String[] {src, dst}, expectedAlerts, false);
	}

	@Test
	public void testUsedVariable(){
		String src = "./test/input/not_defined/nd_used_variable_old.js";
		String dst = "./test/input/not_defined/nd_used_variable_new.js";
		List<Alert> expectedAlerts = new LinkedList<Alert>();
		this.runTest(new String[] {src, dst}, expectedAlerts, false);
	}

	@Test
	public void testUsedField(){
		String src = "./test/input/not_defined/nd_used_field_old.js";
		String dst = "./test/input/not_defined/nd_used_field_new.js";
		List<Alert> expectedAlerts = new LinkedList<Alert>();
		this.runTest(new String[] {src, dst}, expectedAlerts, false);
	}

	@Test
	public void testUsedCall(){
		String src = "./test/input/not_defined/nd_used_call_old.js";
		String dst = "./test/input/not_defined/nd_used_call_new.js";
		List<Alert> expectedAlerts = new LinkedList<Alert>();
		this.runTest(new String[] {src, dst}, expectedAlerts, false);
	}

	@Test
	public void testCallField(){
		String src = "./test/input/not_defined/CliUx_old.js";
		String dst = "./test/input/not_defined/CliUx_new.js";
		List<Alert> expectedAlerts = new LinkedList<Alert>();
		this.runTest(new String[] {src, dst}, expectedAlerts, false);
	}

}
