package ca.ubc.ece.salt.sdjsb.test;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import ca.ubc.ece.salt.sdjsb.alert.Alert;
import ca.ubc.ece.salt.sdjsb.alert.CallbackParameterAlert;

public class TestCallbackParameter extends TestSDJSB {
	
	private void runTest(String[] args, List<Alert> expectedAlerts, boolean printAlerts) {
		List<String> checkers = new LinkedList<String>();
		checkers.add("ca.ubc.ece.salt.sdjsb.checker.callbackparam.CallbackParameterChecker");
		super.runTest(args, checkers, expectedAlerts, printAlerts);
	}
	
	@Test
	public void testMissingErrorParameter(){
		String src = "./test/input/callback_parameter/cbp_old.js";
		String dst = "./test/input/callback_parameter/cbp_new.js";
		List<Alert> expectedAlerts = new LinkedList<Alert>();
		expectedAlerts.add(new CallbackParameterAlert("CB", "donePrinting", "(err)", "err"));
		this.runTest(new String[] {src, dst}, expectedAlerts, true);
	}

}
