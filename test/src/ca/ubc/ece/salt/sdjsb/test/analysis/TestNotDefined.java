package ca.ubc.ece.salt.sdjsb.test.analysis;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import ca.ubc.ece.salt.sdjsb.alert.Alert;
import ca.ubc.ece.salt.sdjsb.alert.NotDefinedAlert;
import ca.ubc.ece.salt.sdjsb.analysis.FlowAnalysis;
import ca.ubc.ece.salt.sdjsb.analysis.notdefined.NotDefinedAnalysis;
import ca.ubc.ece.salt.sdjsb.analysis.notdefined.NotDefinedLatticeElement;

public class TestNotDefined extends TestAnalysis {
	
	private void runTest(String[] args, List<Alert> expectedAlerts, boolean printAlerts) throws Exception {
		FlowAnalysis<NotDefinedLatticeElement> analysis = new NotDefinedAnalysis();
		super.runTest(args, expectedAlerts, printAlerts, analysis);
	}

	@Test
	public void testNotDefined() throws Exception{
		String src = "./test/input/not_defined/nd_old.js";
		String dst = "./test/input/not_defined/nd_new.js";
		List<Alert> expectedAlerts = new LinkedList<Alert>();
		expectedAlerts.add(new NotDefinedAlert("ND", "a"));
		this.runTest(new String[] {src, dst}, expectedAlerts, false);
	}

	@Test
	public void testFieldAccess() throws Exception {
		String src = "./test/input/not_defined/nd_field_old.js";
		String dst = "./test/input/not_defined/nd_field_new.js";
		List<Alert> expectedAlerts = new LinkedList<Alert>();
		expectedAlerts.add(new NotDefinedAlert("ND", "a"));
		this.runTest(new String[] {src, dst}, expectedAlerts, false);
	}

	@Test
	public void testUsedVariable() throws Exception {
		String src = "./test/input/not_defined/nd_used_variable_old.js";
		String dst = "./test/input/not_defined/nd_used_variable_new.js";
		List<Alert> expectedAlerts = new LinkedList<Alert>();
		this.runTest(new String[] {src, dst}, expectedAlerts, false);
	}

	@Test
	public void testUsedField() throws Exception {
		String src = "./test/input/not_defined/nd_used_field_old.js";
		String dst = "./test/input/not_defined/nd_used_field_new.js";
		List<Alert> expectedAlerts = new LinkedList<Alert>();
		this.runTest(new String[] {src, dst}, expectedAlerts, false);
	}

	@Test
	public void testUsedCall() throws Exception {
		String src = "./test/input/not_defined/nd_used_call_old.js";
		String dst = "./test/input/not_defined/nd_used_call_new.js";
		List<Alert> expectedAlerts = new LinkedList<Alert>();
		this.runTest(new String[] {src, dst}, expectedAlerts, false);
	}

	@Test
	public void testCallField() throws Exception {
		String src = "./test/input/not_defined/CliUx_old.js";
		String dst = "./test/input/not_defined/CliUx_new.js";
		List<Alert> expectedAlerts = new LinkedList<Alert>();
		this.runTest(new String[] {src, dst}, expectedAlerts, false);
	}


}