package ca.ubc.ece.salt.sdjsb.test;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import ca.ubc.ece.salt.sdjsb.checker.Alert;
import ca.ubc.ece.salt.sdjsb.checker.callbackerror.CallbackErrorAlert;

public class TestCallbackError extends TestSDJSB {
	
	private void runTest(String[] args, List<Alert> expectedAlerts, boolean printAlerts) {
		List<String> checkers = new LinkedList<String>();
		checkers.add("ca.ubc.ece.salt.sdjsb.checker.callbackerror.CallbackErrorChecker");
		super.runTest(args, checkers, expectedAlerts, printAlerts);
	}
	
	@Test
	public void testCallbackErrorHandling(){
		String src = "./test/input/callback_error/cbe_old.js";
		String dst = "./test/input/callback_error/cbe_new.js";
		List<Alert> expectedAlerts = new LinkedList<Alert>();
		expectedAlerts.add(new CallbackErrorAlert("CB", "donePrinting", "(err)", "err"));
		this.runTest(new String[] {src, dst}, expectedAlerts, true);
	}

	@Test
	public void testRealWorld(){
		String src = "./test/input/callback_error/CLI_old.js";
		String dst = "./test/input/callback_error/CLI_new.js";
		List<Alert> expectedAlerts = new LinkedList<Alert>();
		expectedAlerts.add(new CallbackErrorAlert("CB", "[anonymous]", "(err,processes)", "err"));
		this.runTest(new String[] {src, dst}, expectedAlerts, true);
	}

	/**
	 * This test fails right now because it's a bit limited by AST
	 * differencing. When we implement CFG analysis we should be able to
	 * make this test pass.
	 */
	@Test
	public void testMultiCheck(){
		String src = "./test/input/callback_error/cbe_multi_check_old.js";
		String dst = "./test/input/callback_error/cbe_multi_check_new.js";
		List<Alert> expectedAlerts = new LinkedList<Alert>();
		this.runTest(new String[] {src, dst}, expectedAlerts, true);
	}

}
