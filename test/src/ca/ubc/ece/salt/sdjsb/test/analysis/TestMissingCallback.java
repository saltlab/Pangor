package ca.ubc.ece.salt.sdjsb.test.analysis;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import ca.ubc.ece.salt.sdjsb.alert.Alert;
import ca.ubc.ece.salt.sdjsb.alert.GlobalToLocalAlert;
import ca.ubc.ece.salt.sdjsb.analysis.globaltolocal.GlobalToLocalAnalysis;

public class TestMissingCallback extends TestAnalysis {
	
	private void runTest(String[] args, List<Alert> expectedAlerts, boolean printAlerts) throws Exception {
		//GlobalToLocalAnalysis analysis = new GlobalToLocalAnalysis(); // TODO: Create a MissingCallback analysis.
		//super.runTest(args, expectedAlerts, printAlerts, analysis);
	}

	@Test
	public void testMissingCallback() throws Exception{
		String src = "./test/input/callback_missing/cb_missing_old.js";
		String dst = "./test/input/callback_missing/cb_missing_new.js";
		List<Alert> expectedAlerts = new LinkedList<Alert>();
//		expectedAlerts.add(new GlobalToLocalAlert("GTL", "a")); // TODO: Create a MissingCallback alert.
		this.runTest(new String[] {src, dst}, expectedAlerts, false);
	}

}